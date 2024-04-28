package com.springboot.sample.controller;

import com.springboot.sample.bean.User;
import com.springboot.sample.service.impl.UserWrapBatchQueueService;
import com.springboot.sample.service.impl.UserWrapBatchService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/*
 * 蜗牛哥
 * */
@RestController
@RequestMapping("/async")
public class AsyncController {

    @Resource
    private UserWrapBatchService userBatchService;

    @Resource
    private UserWrapBatchQueueService userWrapBatchQueueService;


    /**
     * 多线程并发执行 可能会出现的问题？
     * <p>
     *
     * @param userId
     * @return
     */
    @RequestMapping("/async1")
    public User async1(Long userId) {
        return userBatchService.queryUserAsync(userId);
    }


    /***
     * 接口请求批量调用结合多线程 优化系统性能
     * */
    @RequestMapping("/merge")
    public User merge(Long userId) {
//        return userBatchService.queryUser(userId);
       return userWrapBatchQueueService.queryUser(userId);
    }

}
