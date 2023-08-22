package com.bsh.bshauction.global.partitioner;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;

import java.util.Set;

@RequiredArgsConstructor
public class CustomItemReader implements ItemReader<Set<String>> {
    private final Set<String> setList;

    @Override
    public Set<String> read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
        if(!setList.isEmpty()) {
            return setList;
        }
        return null;
    }
}
