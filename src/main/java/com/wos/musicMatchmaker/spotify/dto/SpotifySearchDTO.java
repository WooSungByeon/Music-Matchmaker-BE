package com.wos.musicMatchmaker.spotify.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SpotifySearchDTO {

    private String searchWord;
    private String type;
    private String market;
    private Integer limit;
    private Integer offset;

}
