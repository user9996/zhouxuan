package com.springboot.sample.service.impl;

import com.springboot.sample.bean.User;
import com.springboot.sample.service.UserService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.*;


@Service
@Slf4j
public class UserWrapBatchService {
    @Resource
    private UserService userService;

    @Autowired
    private ExecutorService executorService;

    /**
     * 最大任务数
     **/
    public static int MAX_TASK_NUM = 100;

    /**
     * 这次从线程池配置入手
     * 1. 优化线程池配置
     * //        return new ThreadPoolExecutor(
     * //                2 * coreSize + 1,
     * //                coreSize * 5,
     * //                5L,
     * //                TimeUnit.MINUTES,
     * //                new LinkedBlockingQueue<Runnable>(1024)
     * 替换为
     *         int coreSize = Runtime.getRuntime().availableProcessors();
     *         return new ThreadPoolExecutor(
     *                 2 * coreSize + 1,
     *                 coreSize * 5,
     *                 5L,
     *                 TimeUnit.MINUTES,
     *                 new SynchronousQueue<>(),
     *                 new ThreadPoolExecutor.CallerRunsPolicy()
     *
     * 2. 主线程阻塞等待也是一种资源的浪费 让主线程也运行一个任务
     *
     * @param userId
     * @return
     */
    public User queryUserAsync(Long userId) {
        User user = new User();
        CompletableFuture<Void> nameFuture = CompletableFuture.runAsync(() -> {
            // 根据userId 查询用户姓名
            String name = userService.queryUserByUserId(userId);
            user.setName(name);
        }, executorService);

        CompletableFuture<Void> integralFuture = CompletableFuture.runAsync(() -> {
            // 根据userId 查积分系统
            Integer integral = userService.queryIntegralByUserId(userId);
            user.setIntegral(integral);
        }, executorService);

        // 根据userId 查coupon 优惠券系统
        Integer coupon = userService.queryCouponByUserId(userId);
        user.setCoupon(coupon);
        try {
            CompletableFuture.allOf(nameFuture, integralFuture).get(5, TimeUnit.SECONDS);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return user;
    }


}