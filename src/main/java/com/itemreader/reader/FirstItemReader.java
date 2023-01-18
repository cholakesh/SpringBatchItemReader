package com.itemreader.reader;

import java.util.Arrays;
import java.util.List;

import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

@Component
public class FirstItemReader implements ItemReader<Integer> {

    List<Integer> li = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
    int i = 0;

    @Override
    @Nullable
    public Integer read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
        System.out.println("Inside Item Reader");
        Integer item;
        if (i < li.size()) {
            item = li.get(i);
            i++;
            return item;
        }
        i = 0;
        return null;
    }

}
