package com.wos.musicMatchmaker.api.service;

import com.wos.musicMatchmaker.api.dao.TestDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class TestService {

    @Autowired
    private TestDAO testDAO;

    public List<Map<String, Object>> selectSampleData() {
        return testDAO.selectSampleData();
    }
}
