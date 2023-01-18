package com.itemreader.processor;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

@Component
public class FirstItemProcessor implements ItemProcessor<Integer, Long> {

    @Override
    @Nullable
    public Long process(@NonNull Integer item) throws Exception {
        System.out.println("Inside Item Processor");
        return Long.valueOf(item + 20);
    }

}
