package com.wos.musicMatchmaker.youtube.controller;

import com.wos.musicMatchmaker.youtube.dto.YoutubeSearchDTO;
import com.wos.musicMatchmaker.youtube.service.YoutubeAPIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/youtube")
public class YoutubeAPIController {

    @Autowired
    private YoutubeAPIService youtubeAPIService;

    /**
     * Youtube API Search
     *
     * @param youtubeSearchDTO
     * @return
     */
    @RequestMapping(value = "/searchYoutubeList", method = {RequestMethod.POST})
    public ResponseEntity<?> searchYoutubeList(@RequestBody YoutubeSearchDTO youtubeSearchDTO) {
        return youtubeAPIService.searchYoutubeList(youtubeSearchDTO);
    }

}
