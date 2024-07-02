package com.wos.musicMatchmaker.api.controller;


import com.wos.musicMatchmaker.api.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class TestController {

    @Autowired
    private TestService testService;


    /**
     * Call API Test Method
     * @return
     */
    @RequestMapping(value = "/hello", method = {RequestMethod.GET})
    public String HelloMethod() {
        return "Hello World";
    }

    /**
     * Select DB data Test Method
     * @return
     */
    @RequestMapping(value = "/selectSampleData", method = {RequestMethod.GET})
    public List<Map<String, Object>> selectSampleData() {
        return testService.selectSampleData();
    }

}
