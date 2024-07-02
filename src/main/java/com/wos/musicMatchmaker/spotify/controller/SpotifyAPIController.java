package com.wos.musicMatchmaker.spotify.controller;


import com.wos.musicMatchmaker.spotify.dto.SpotifyAuthDTO;
import com.wos.musicMatchmaker.spotify.dto.SpotifyRcmDTO;
import com.wos.musicMatchmaker.spotify.dto.SpotifySearchDTO;
import com.wos.musicMatchmaker.spotify.service.SpotifyAPIService;
import com.wos.musicMatchmaker.spotify.vo.SpotifyAuthVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/spotify")
public class SpotifyAPIController {

    @Autowired
    private SpotifyAPIService spotifyAPIService;

    /**
     * Get Spofity API Token
     * @param spotifyAuthDTO
     * @return
     */
    @RequestMapping(value = "/getSpotifyAccessToken", method = {RequestMethod.POST})
    public Object getSpotifyAccessToken(@RequestBody SpotifyAuthDTO spotifyAuthDTO) {
        return spotifyAPIService.getSpotifyAccessToken(spotifyAuthDTO);
    }

    /**
     * Verification Spofity API Token
     * @param spotifyAuthDTO
     * @return
     */
    @RequestMapping(value = "/getSaveSpotifyAccessToken", method = {RequestMethod.POST})
    public SpotifyAuthVO getSaveSpotifyAccessToken(@RequestBody SpotifyAuthDTO spotifyAuthDTO) {
        return spotifyAPIService.getSaveSpotifyAccessToken(spotifyAuthDTO);
    }

    /**
     * Spofity API Search
     * Get track information by track title
     *
     * @param spotifySearchDTO
     * @return
     */
    @RequestMapping(value = "/searchTrackList", method = {RequestMethod.POST})
    public Object searchTrackList(@RequestBody SpotifySearchDTO spotifySearchDTO) {
        return spotifyAPIService.searchTrackList(spotifySearchDTO);
    }

    /**
     * Spofity API recommend
     * Get recommend track list information by track info
     *
     * @param spotifyRcmDTO
     * @return
     */
    @RequestMapping(value = "/recommendTrackList", method = {RequestMethod.POST})
    public Object recommendTrackList(@RequestBody SpotifyRcmDTO spotifyRcmDTO) {
      return spotifyAPIService.recommendTrackList(spotifyRcmDTO);
    }

}