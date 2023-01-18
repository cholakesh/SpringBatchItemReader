package com.itemreader.writer;

import java.util.List;

import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import com.itemreader.model.StudentCsv;
import com.itemreader.model.StudentJdbc;
import com.itemreader.model.StudentJson;
import com.itemreader.model.StudentResponse;
import com.itemreader.model.StudentXml;

@Component
public class FirstItemWriter implements ItemWriter<StudentResponse> {

    @Override
    public void write(List<? extends StudentResponse> items) throws Exception {
        System.out.println("inside Item Writer");
        items.stream().forEach(System.out::println);
    }

}
