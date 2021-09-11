package com.funmeet.infra.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration(proxyBeanMethods = false)
@EnableAsync
@Slf4j
public class AsyncConfig implements AsyncConfigurer{

    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        int processors = Runtime.getRuntime().availableProcessors();
        executor.setCorePoolSize(processors);
        executor.setMaxPoolSize(processors * 2);
        executor.setQueueCapacity(50); // 꽉 찼을 경우, 크기 45의 queue 생성
        executor.setKeepAliveSeconds(60); // 새로 만든 쓰레드를 45초까지만 살리기
        executor.setThreadNamePrefix("AsyncExecutor:");
        executor.initialize();
        return executor;
    }

}

