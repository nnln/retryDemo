package com.example.demo.controller;

import com.example.demo.service.RecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


/**
 * @Author: Zyq
 * @Date: 2022/4/7 15:29
 */
@RestController
public class RecordController {

    @Autowired
    private RecordService recordService;

    /**
     * Test
     *
     * @return
     */
    @RequestMapping(value = "/test", method = RequestMethod.GET)
    public String MD5test() {
        return recordService.errorRetry();
    }

}
