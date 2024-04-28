package com.springboot.sample.service.impl;


import com.springboot.sample.bean.User;
import com.springboot.sample.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {


    @Override
    public Map<String, User> queryUserByIdBatch(List<UserWrapBatchService.Request> userReqs) {
        HashMap<String, User> result = new HashMap<>();
        if (CollectionUtils.isEmpty(userReqs)) {
            return result;
        }
        List<User> userList = new ArrayList<>();
        for (UserWrapBatchService.Request userReq : userReqs) {
            User user = new User();
            user.setId(userReq.getUserId());
            user.setRequestId(userReq.requestId);
            userList.add(user);
        }

        List<Long> userIds = userReqs.stream().map(UserWrapBatchService.Request::getUserId).distinct().collect(Collectors.toList());
        // 用in语句合并成一条SQL，模拟查数据库 避免多次请求数据库的IO
        // 批量查询用户信息 只有姓名
        List<User> userInfos = getUserList(userIds);
        if (CollectionUtils.isEmpty(userInfos)) {
            return result;
        }
        Map<Long, String> nameMap = userInfos.stream().collect(Collectors.toMap(User::getId, User::getName, (k1, k2) -> k1));

        // 批量查询用户积分信息 只有积分
        List<User> integrals = getUserIntegral(userIds);
        if (CollectionUtils.isEmpty(integrals)) {
            return result;
        }
        Map<Long, Integer> integralMap = userInfos.stream().collect(Collectors.toMap(User::getId, User::getIntegral, (k1, k2) -> k1));

        // 批量查询用户积分信息 只有优惠卷
        List<User> coupons = getUserCoupons(userIds);
        if (CollectionUtils.isEmpty(coupons)) {
            return result;
        }

        Map<Long, Integer> couponMap = coupons.stream().collect(Collectors.toMap(User::getId, User::getCoupon, (k1, k2) -> k1));

        // 组装结果
        for (User user : userList) {
            user.setName(nameMap.get(user.getId()));
            user.setIntegral(integralMap.get(user.getId()));
            user.setCoupon(couponMap.get(user.getId()));
        }
        return userList.stream().collect(Collectors.toMap(User::getRequestId, Function.identity(), (k1, k2) -> k1));
    }

    /**
     * 批量查询用户积分信息 只有优惠卷
     *
     * @param userIds
     * @return
     */
    private List<User> getUserCoupons(List<Long> userIds) {
        List<User> users = new ArrayList<>();
        for (Long userId : userIds) {
            User user = new User();
            user.setId(userId);
            user.setCoupon(userId.intValue() + userId.intValue());
            users.add(user);
        }
        return users;
    }

    /**
     * 批量查询用户信息 只有积分
     *
     * @param userIds
     * @return
     */
    private List<User> getUserIntegral(List<Long> userIds) {
        List<User> users = new ArrayList<>();
        for (Long userId : userIds) {
            User user = new User();
            user.setId(userId);
            user.setIntegral(userId.intValue());
            users.add(user);
        }
        return users;
    }

    /**
     * 根据用户id 批量查询用户信息
     *
     * @param userIds
     * @return
     */
    private List<User> getUserList(List<Long> userIds) {
        List<User> users = new ArrayList<>();
        for (Long userId : userIds) {
            User user = new User();
            user.setId(userId);
            user.setName(userId + "蜗牛");
            users.add(user);
        }
        return users;
    }

    @Override
    public Map<String, User> queryUserByIdBatchQueue(List<UserWrapBatchQueueService.Request> userReqs) {
        HashMap<String, User> result = new HashMap<>();
        if (CollectionUtils.isEmpty(userReqs)) {
            return result;
        }
        List<User> userList = new ArrayList<>();
        for (UserWrapBatchQueueService.Request userReq : userReqs) {
            User user = new User();
            user.setId(userReq.getUserId());
            user.setRequestId(userReq.requestId);
            userList.add(user);
        }

        List<Long> userIds = userReqs.stream().map(UserWrapBatchQueueService.Request::getUserId).distinct().collect(Collectors.toList());
        // 用in语句合并成一条SQL，模拟查数据库 避免多次请求数据库的IO
        // 批量查询用户信息 只有姓名
        List<User> userInfos = getUserList(userIds);
        if (CollectionUtils.isEmpty(userInfos)) {
            return result;
        }
        Map<Long, String> nameMap = userInfos.stream().collect(Collectors.toMap(User::getId, User::getName, (k1, k2) -> k1));

        // 批量查询用户积分信息 只有积分
        List<User> integrals = getUserIntegral(userIds);
        if (CollectionUtils.isEmpty(integrals)) {
            return result;
        }
        Map<Long, Integer> integralMap = userInfos.stream().collect(Collectors.toMap(User::getId, User::getIntegral, (k1, k2) -> k1));

        // 批量查询用户积分信息 只有优惠卷
        List<User> coupons = getUserCoupons(userIds);
        if (CollectionUtils.isEmpty(coupons)) {
            return result;
        }

        Map<Long, Integer> couponMap = coupons.stream().collect(Collectors.toMap(User::getId, User::getCoupon, (k1, k2) -> k1));

        // 组装结果
        for (User user : userList) {
            user.setName(nameMap.get(user.getId()));
            user.setIntegral(integralMap.get(user.getId()));
            user.setCoupon(couponMap.get(user.getId()));
        }
        return userList.stream().collect(Collectors.toMap(User::getRequestId, Function.identity(), (k1, k2) -> k1));
    }


    @Override
    public String queryUserByUserId(Long userId) {
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return userId.toString() + "蜗牛";
    }

    @Override
    public Integer queryIntegralByUserId(Long userId) {
        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return Math.toIntExact(userId);
    }

    @Override
    public Integer queryCouponByUserId(Long userId) {
        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return Math.toIntExact(userId) * 2;
    }
}