package com.example.demo.service.impl;

import com.example.demo.dto.TestDto;
import com.example.demo.service.RecordService;
import com.example.demo.utils.AsynchRetryUtil;
import com.google.common.util.concurrent.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * @Author: Zyq
 * @Date: 2022/4/7 15:29
 */

@Service
public class RecordServiceImpl implements RecordService {

    private final int MaxErrorRetryCount = 5;


    ThreadPoolExecutor poll = new ThreadPoolExecutor(2, 2, 60,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(1000),
            new ThreadPoolExecutor.CallerRunsPolicy()
    );

    @Override
    public String errorRetry() {
        async();
        System.out.println("返回结果");
        return "111111111111111";
    }

    public void async(){
        List<TestDto> orderList = new ArrayList<TestDto>();
        orderList.add(new TestDto("test1","test1"));
        orderList.add(new TestDto("test2","test2"));
        Function<List<TestDto>, AsynchRetryUtil.Param> getCode = code -> {
            try {
                return new AsynchRetryUtil.Param(getData(orderList) != 200, null);
            } catch (Exception e) {
                System.out.println("函数内异常 ");
                e.printStackTrace();
                return new AsynchRetryUtil.Param(true, e);
            }
        };

        AsynchRetryUtil.getAsynchRetryUtil().asyncRetry(MaxErrorRetryCount, getCode, orderList);
    }

    public int getData(List<TestDto> orderList) throws Exception{
        long timeout = 3;
        FutureTask<Integer> future = new FutureTask<>(() -> {
            Random random = new Random();
            // 异常测试
            if(random.nextInt(100) > 50){
                Thread.sleep(4000);
            }
            // 失败测试
            if(random.nextInt(100) > 50){
                return 500;
            }
            return 200;
        });
        poll.execute(future);
        return future.get(timeout, TimeUnit.SECONDS);
    }


}
