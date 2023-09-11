package com.bsh.bshauction.global.partitioner;

import io.micrometer.core.lang.NonNullApi;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@NonNullApi
public class AlarmPartitioner implements Partitioner {
    private final Long size;

    @Override
    public Map<String, ExecutionContext> partition(int gridSize) {

        Map<String, ExecutionContext> result = new HashMap<>();
        int partitionIndex = 0;
        int calcSize = 0;

        if(size != null) {
            calcSize = Math.toIntExact(size);
        }

        int calcGridSize = gridSize;

        int start = 0;

        for(int i=0; i<gridSize; i++) {
            ExecutionContext value = new ExecutionContext();
            result.put("partition" + partitionIndex, value);

            int itemsPerPartition = calcSize / calcGridSize;

            value.putLong("start_index", start);

            if(calcSize % calcGridSize != 0) {
                itemsPerPartition += 1;
                value.putLong("end_index", start + itemsPerPartition - 1);
            } else {
                value.putLong("end_index", start + itemsPerPartition - 1);
            }

            calcSize -= itemsPerPartition;
            calcGridSize -= 1;

            if(calcSize <= 0) {
                break;
            }

            start += itemsPerPartition;
            partitionIndex++;
        }

        return result;
    }
}
