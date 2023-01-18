package com.itemreader.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.itemreader.model.StudentResponse;

@Service
public class RestApiItemReader {

    List<StudentResponse> li;

    public List<StudentResponse> studentDetails() {
        RestTemplate restTemplate = new RestTemplate();
        StudentResponse[] studentResponsesArray = restTemplate
                .getForObject("http://localhost:8081/api/v1/studentDetails", StudentResponse[].class);

        li = new ArrayList<>();
        for (StudentResponse sr : studentResponsesArray) {
            li.add(sr);
        }

        return li;
    }

    // inorder to pass each value seperately for an individual shot the above method
    // should be again processed to the bellow.

    public StudentResponse indiviualData(Long id, String name) {
        System.out.println("Id: " + id + " and name: " + name);
        if (li == null) {
            studentDetails();
        }

        if (li != null && !li.isEmpty()) {
            return li.remove(0);
        }
        return null;
    }
}
