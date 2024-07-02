package com.wos.musicMatchmaker.spotify.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.json.JSONPropertyName;

@Getter
@Setter
public class SpotifyRcmDTO {

    private String trackId;
    private String artistsId;
    private String market;
    private String limit;
    private String seedGenres;

}
