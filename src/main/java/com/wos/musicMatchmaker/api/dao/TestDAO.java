package com.wos.musicMatchmaker.api.dao;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class TestDAO {

    @Autowired
    private SqlSession sqlSession;

    private final String nameSpace = "com.wos.musicMatchmaker.api.dao.TestDAO.";

    public List<Map<String, Object>> selectSampleData() {
        return this.sqlSession.selectList(nameSpace + "selectSampleData");
    }
}
