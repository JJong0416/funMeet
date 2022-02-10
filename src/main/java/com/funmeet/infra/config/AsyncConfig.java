package com.funmeet.infra.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration(proxyBeanMethods = false)
@EnableAsync
public class AsyncConfig implements AsyncConfigurer {
    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor(); // 자바의 ThreadPoolTaskExecuter
        int processors = Runtime.getRuntime().availableProcessors(); // 현재 시스템 사용가능한 프로세스 개수만큼 코어 사이즈 설정
        executor.setCorePoolSize(processors); // 수영장 위에 있는 튜브 갯수
        executor.setMaxPoolSize(processors * 2); // 51명째 사람이 들어오면, 50번쨰의 첫번쨰 사람한테 튜브를 만들어서 준다. process*2만큼
        executor.setQueueCapacity(50); // 수영장 위 사람이 꽉 차면 50번째까지 세운다.
        executor.setKeepAliveSeconds(60); // 덤으로 만들어진 튜브들을 살리는 최대 시간
        executor.setThreadNamePrefix("AsyncExecutor:");
        executor.initialize();
        return executor;
    }
}

