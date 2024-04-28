package com.springboot.sample.service;

import com.springboot.sample.bean.User;
import com.springboot.sample.service.impl.UserWrapBatchQueueService;
import com.springboot.sample.service.impl.UserWrapBatchService;

import java.util.List;
import java.util.Map;

public interface UserService {

    Map<String, User> queryUserByIdBatch(List<UserWrapBatchService.Request> userReqs);

    Map<String, User> queryUserByIdBatchQueue(List<UserWrapBatchQueueService.Request> userReqs);

    String queryUserByUserId(Long userId);

    Integer queryIntegralByUserId(Long userId);

    Integer queryCouponByUserId(Long userId);
}
