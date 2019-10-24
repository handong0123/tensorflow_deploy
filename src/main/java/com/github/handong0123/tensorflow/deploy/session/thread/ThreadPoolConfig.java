package com.github.handong0123.tensorflow.deploy.session.thread;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.*;

/**
 * 线程池配置
 *
 * @author handong
 */
@Configuration
public class ThreadPoolConfig {

    @Bean
    public ExecutorService threadExecutor() {
        ThreadFactory namedThreadFactory = new ThreadFactoryBuilder().setNameFormat("tensorflow-provider-pool-%d").build();
        return new ThreadPoolExecutor(10, 20, 0, TimeUnit.SECONDS, new LinkedBlockingQueue<>(1), namedThreadFactory);
    }
}
