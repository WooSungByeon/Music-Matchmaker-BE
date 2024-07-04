package com.wos.musicMatchmaker.spotify.dao;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class SpotifyAPIDAO {

    @Autowired
    private SqlSession sqlSession;

    private final String nameSpace = "com.wos.musicMatchmaker.spotify.dao.SpotifyAPIDAO.";

    public List<Map<String, Object>> selectTrackSearchHistory() {
        return this.sqlSession.selectList(nameSpace + "selectTrackSearchHistory");
    }

    public Integer insertTrackSearchHistory(Map<String, Object> params) {
        return this.sqlSession.insert(nameSpace + "insertTrackSearchHistory", params);
    }
}
