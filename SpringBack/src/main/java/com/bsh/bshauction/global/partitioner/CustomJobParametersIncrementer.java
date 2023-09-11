package com.bsh.bshauction.global.partitioner;

import io.micrometer.core.lang.NonNullApi;
import io.micrometer.core.lang.Nullable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersIncrementer;
import org.springframework.beans.factory.annotation.Value;

@Slf4j
@NonNullApi
public class CustomJobParametersIncrementer implements JobParametersIncrementer {
    @Value("${RUN_ID_KEY}")
    private String runId;

    @Override
    public JobParameters getNext(@Nullable JobParameters parameters) {
        JobParameters params = parameters == null ? new JobParameters() : parameters;
        JobParameter runIdParameter = params.getParameters().get(runId);

        long startId = 1L;
        log.info("startId : {}", startId);

        if(runIdParameter != null) {
            try {
                startId = Long.parseLong(runIdParameter.getValue().toString()) + 1L;
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid value for parameter " + runId, e);
            }
        }

        return new JobParametersBuilder(params).addLong(runId, startId).toJobParameters();
    }
}
