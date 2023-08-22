package com.bsh.bshauction.global.config;

import com.bsh.bshauction.global.partitioner.AlarmPartitioner;
import com.bsh.bshauction.global.partitioner.CustomItemReader;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.partition.support.TaskExecutorPartitionHandler;
import org.springframework.batch.item.*;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.Collections;
import java.util.Iterator;
import java.util.Set;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class PartitionConfig {
    private final RedisTemplate<String, String> redisTemplate;
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final RabbitTemplate rabbitTemplate;
    public static final String JOB_NAME = "partitionLocalBatch";


    @Value("${chunkSize:100}")
    private int chunkSize;

    @Value("${poolSize:5}")
    private int poolSize;

    @Bean(name = JOB_NAME + "_taskPool")
    public TaskExecutor executor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(poolSize);
        executor.setMaxPoolSize(poolSize);
        executor.setThreadNamePrefix("partition-thread");
        executor.setWaitForTasksToCompleteOnShutdown(Boolean.TRUE);
        executor.initialize();
        return executor;
    }

    @Bean(name = JOB_NAME + "_partitionHandler")
    public TaskExecutorPartitionHandler partitionHandler() {
        TaskExecutorPartitionHandler partitionHandler = new TaskExecutorPartitionHandler();
        partitionHandler.setStep(step1());
        partitionHandler.setTaskExecutor(executor());
        partitionHandler.setGridSize(poolSize);
        return partitionHandler;
    }

    @Bean(name = JOB_NAME + "_partitioner")
    @StepScope
    public AlarmPartitioner partitioner(@Value("#{jobParameters['alarmListSize']}") Long size) {
        return new AlarmPartitioner(size);
    }
    
    @Bean(name = JOB_NAME)
    public Job partitionLocalBatch() {
        return jobBuilderFactory.get(JOB_NAME)
                .start(step1Manager())
//                .preventRestart()
                .build();
    }

    @Bean(name = JOB_NAME + "_step1Manager")
    public Step step1Manager() {
        return stepBuilderFactory.get("step1.manager")
                .partitioner("step1", partitioner(null))
                .step(step1())
                .partitionHandler(partitionHandler())
                .build();
    }

    @Bean(name = JOB_NAME + "_step")
    public Step step1() {
        return stepBuilderFactory.get(JOB_NAME + "_step")
                .chunk(1)
                .reader(reader(null, null, null))
                .writer(writer())
                .build();
    }

    @Bean(name = JOB_NAME + "_reader")
    @StepScope
    public ItemReader<Set<String>> reader(@Value("#{jobParameters['productId']}") Long productId, @Value("#{stepExecutionContext['start_index']}") Long startIndex
    , @Value("#{stepExecutionContext['end_index']}") Long endIndex) {

        if(startIndex != null && endIndex != null) {
            Set<String> alarmList = redisTemplate.opsForZSet().range("product" + productId, startIndex, endIndex);
            log.info("min : {}, max : {}", startIndex, endIndex);

            if(alarmList != null) {
                log.info("reader size : {}", alarmList.size());
                Iterator<String> alarmIterator = alarmList.iterator();
                return () -> alarmIterator.hasNext() ? Collections.singleton(alarmIterator.next()) : null;
            }
        }
        return null;
    }

    @Bean(name = JOB_NAME + "_writer")
    @StepScope
    public ItemWriter<Object> writer() {
        return items -> {
            for(Object item : items) {
                log.info(items.toString());
                if(item != null) {
                    log.info("message user Id : {}", item);
                    rabbitTemplate.convertAndSend("amq.topic", "userId." + item, "상품 경매 시간이 증가 했습니다.");
                }else {
                    log.info("item is null");
                }
            }
        };
    }


}
