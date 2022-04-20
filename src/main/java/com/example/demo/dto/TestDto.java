package com.example.demo.dto;

import lombok.Data;

/**
 * @Classname TestDto
 * @Description TODO
 * @Date 2021/11/5 13:54
 * @Created by yuyangkang
 */
@Data
public class TestDto {

    public TestDto(String param1, String param2) {
        this.param1 = param1;
        this.param2 = param2;
    }

    private String param1;

    private String param2;
}
