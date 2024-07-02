package com.wos.musicMatchmaker.spotify.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SpotifyAuthDTO {

    private String grantType;
    private String clientId;
    private String clientSecret;

}
