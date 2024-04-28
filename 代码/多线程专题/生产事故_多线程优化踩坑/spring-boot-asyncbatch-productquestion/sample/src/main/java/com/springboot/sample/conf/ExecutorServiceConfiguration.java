package com.springboot.sample.conf;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.*;

@Configuration
public class ExecutorServiceConfiguration {

    @Bean
    public ExecutorService executorService() {
//        return new ThreadPoolExecutor(
//                2 * coreSize + 1,
//                coreSize * 5,
//                5L,
//                TimeUnit.MINUTES,
//                new LinkedBlockingQueue<Runnable>(1024)
        int coreSize = Runtime.getRuntime().availableProcessors();
        return new ThreadPoolExecutor(
                2 * coreSize + 1,
                coreSize * 5,
                5L,
                TimeUnit.MINUTES,
                new SynchronousQueue<>(),
                new ThreadPoolExecutor.CallerRunsPolicy()
        );
    }


}
