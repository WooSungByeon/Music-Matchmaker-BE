package com.wos.musicMatchmaker.config;

import com.wos.musicMatchmaker.spotify.vo.SpotifyAuthVO;
import org.springframework.context.annotation.Bean;

import java.util.HashMap;
import java.util.Map;

public enum SingletonVOconfig {

    INSTANCE();

    private Map<String, SpotifyAuthVO> spotifyAuthConfig;

    private SingletonVOconfig() {
        spotifyAuthConfig = new HashMap<>();
        }

    public SingletonVOconfig getInstance() {
        return INSTANCE;
    }

    @Bean
    public SpotifyAuthVO getSpotifyAuthVO() {
        return new SpotifyAuthVO();
    }

    public SpotifyAuthVO getSpotifyAuthConfig(String clientId) {
            return spotifyAuthConfig.get(clientId);
        }

    public void setSpotifyAuthConfig(String clientId, SpotifyAuthVO vo) {
        spotifyAuthConfig.put(clientId, vo);
        }

}
