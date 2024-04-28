package com.springboot.sample.service.impl;

import com.springboot.sample.bean.User;
import com.springboot.sample.service.UserService;
import lombok.Data;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.*;

/***
 * 包装成批量执行的地方，使用queue解决超时问题
 * */
@Service
public class UserWrapBatchQueueService {
    @Resource
    private UserService userService;

    /**
     * 最大任务数
     **/
    public static int MAX_TASK_NUM = 100;

    @Data
    public class Request {
        // 请求id
        String requestId;

        // 参数
        Long userId;
        // 队列，这个有超时机制
        LinkedBlockingQueue<User> usersQueue;

    }

    private final Queue<Request> queue = new LinkedBlockingQueue();

    @PostConstruct
    public void init() {
        //定时任务线程池,创建一个支持定时、周期性或延时任务的限定线程数目(这里传入的是1)的线程池
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);

        scheduledExecutorService.scheduleAtFixedRate(() -> {
            int size = queue.size();
            //如果队列没数据,表示这段时间没有请求,直接返回
            if (size == 0) {
                return;
            }
            List<Request> list = new ArrayList<>();
            System.out.println("合并了 [" + size + "] 个请求");
            //将队列的请求消费到一个集合保存
            for (int i = 0; i < size; i++) {
                // 后面的SQL语句是有长度限制的，所以还要做限制每次批量的数量,超过最大任务数，等下次执行
                if (i < MAX_TASK_NUM) {
                    list.add(queue.poll());
                }
            }
            //拿到我们需要去数据库查询的特征,保存为集合
            List<Request> userReqs = new ArrayList<>();
            for (Request request : list) {
                userReqs.add(request);
            }
            //将参数传入service处理, 这里是本地服务，也可以把userService 看成RPC之类的远程调用
            Map<String, User> response = userService.queryUserByIdBatchQueue(userReqs);
            for (Request userReq : userReqs) {
                // 这里再把结果放到队列里
                User users = response.get(userReq.getRequestId());
                userReq.usersQueue.offer(users);
            }

        }, 100, 10, TimeUnit.MILLISECONDS);
        //scheduleAtFixedRate是周期性执行 schedule是延迟执行 initialDelay是初始延迟 period是周期间隔 后面是单位
        //这里我写的是 初始化后100毫秒后执行，周期性执行10毫秒执行一次
    }

    public User queryUser(Long userId) {
        Request request = new Request();
        // 这里用UUID做请求id
        request.requestId = UUID.randomUUID().toString().replace("-", "");
        request.userId = userId;
        LinkedBlockingQueue<User> usersQueue = new LinkedBlockingQueue<>();
        request.usersQueue = usersQueue;
        //将对象传入队列
        queue.offer(request);
        //取出元素时，如果队列为空，给定阻塞多少毫秒再队列取值，这里是3秒
        try {
            return usersQueue.poll(3000,TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }
}