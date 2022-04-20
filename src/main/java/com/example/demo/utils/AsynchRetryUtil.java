package com.example.demo.utils;

import com.google.common.util.concurrent.*;

import java.util.concurrent.Callable;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * @author ：zyq
 * @description :AsynchRetryUtil
 * @date ：2022/4/18 11:40
 */
public class AsynchRetryUtil {
    private static AsynchRetryUtil asynchRetryUtil = new AsynchRetryUtil();
    public static AsynchRetryUtil getAsynchRetryUtil(){
        return  asynchRetryUtil;
    }

    public static class Param{
        boolean result;
        Exception erro;

        public Param(boolean result, Exception erro){
            setResult(result);
            setErro(erro);
        }

        public boolean isResult() {
            return result;
        }

        public void setResult(boolean result) {
            this.result = result;
        }

        public Exception getErro() {
            return erro;
        }

        public void setErro(Exception erro) {
            this.erro = erro;
        }
    }

    /**
     * 线程池
     */
    private ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(2, 2, 60,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(1000),
            new ThreadPoolExecutor.CallerRunsPolicy()
    );

    /**
     * 装饰线程池
     */
    private ListeningExecutorService executorService = MoreExecutors.listeningDecorator(threadPoolExecutor);

    public void asyncRetry(int errorRetryCount, Function getResult, Object input) {
        long waitTime = 2000;
        ListenableFuture<Boolean> future = executorService.submit(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                int retryCount = 2;
                Param param = (Param) getResult.apply(input);
                while(param.isResult() && retryCount > 0){
                    System.out.println("请求错误进入重试！");
                    if(param.getErro() != null){
                        System.out.println("请求异常进入重试！");
                        throw param.getErro();
                    }
                    Thread.sleep(waitTime);
                    retryCount--;
                    param = (Param) getResult.apply(input);
                }
                return true;
            }
        });
        Futures.addCallback(future, new FutureCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                if(result){
                    System.out.println("成功 ");
                }else{
                    System.out.println("失败 : 已达到最大重试次数 ");
                }
            }

            @Override
            public void onFailure(Throwable throwable) {
                if(errorRetryCount>0){
                    try {
                        Thread.sleep(waitTime);
                        int newErrorRetryCount = errorRetryCount - 1;
                        asyncRetry(newErrorRetryCount, getResult, input);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }else{
                    System.out.println("失败 : 已达到最大异常重试次数： " + throwable);
                }
            }
        }, executorService);
    }

}
