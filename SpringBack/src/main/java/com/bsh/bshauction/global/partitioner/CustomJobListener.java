package com.bsh.bshauction.global.partitioner;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.JobParameters;
import org.springframework.data.redis.core.RedisTemplate;

@Slf4j
@RequiredArgsConstructor
public class CustomJobListener implements JobExecutionListener {
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public void beforeJob(JobExecution jobExecution) {
        // 작업 실행 전에 수행할 작업
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        // 작업 실행 후에 수행할 작업
        // 배치 잡을 나눠서 처리할수 있나?
        if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
            JobParameters jobParameters = jobExecution.getJobParameters();
            Long productId = jobParameters.getLong("productId");

            if(productId != null) {
                redisTemplate.opsForZSet().incrementScore("product_id_update", productId.toString(), 1);
            }
        }
    }
}