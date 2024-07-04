package com.wos.musicMatchmaker.youtube.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wos.musicMatchmaker.youtube.dto.YoutubeSearchDTO;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Service
public class YoutubeAPIService {

    @Autowired
    private RestTemplate restTemplate;

    private final String YOUTUBE_API_URL = "https://youtube.googleapis.com/youtube";
    private final String YOUTUBE_VIDEO_URL = "https://www.youtube.com/watch?v=";
    private final String YOUTUBE_VIDEO_MAX_CNT = "6";
    private final String YOUTUBE_VIDEO_ORDER = "relevance";

    @Value("${YOUTUBE_API_KEY}")
    private String YOUTUBE_API_KEY;

    /**
     * Youtube API Search
     *
     * @param youtubeSearchDTO
     * @return
     */
    public Object searchYoutubeList(YoutubeSearchDTO youtubeSearchDTO) {

        JSONArray resultObject = new JSONArray();

        String youtubeApiUrl = YOUTUBE_API_URL + "/v3/search"
                + "?key=" + YOUTUBE_API_KEY
                + "&part=" + youtubeSearchDTO.getPart()
                + "&q=" + youtubeSearchDTO.getSearchWord()
                + "&maxResults=" + YOUTUBE_VIDEO_MAX_CNT
                + "&order=" + YOUTUBE_VIDEO_ORDER
                + "&type=" + "video"
        ;

        HttpHeaders headers = new HttpHeaders();

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(headers);

        try{
            ResponseEntity<String> response = restTemplate.exchange(youtubeApiUrl, HttpMethod.GET, request, String.class);

            // ObjectMapper 객체 생성
            ObjectMapper mapper = new ObjectMapper();

            // 문자열을 JsonNode로 변환
            JsonNode jsonNode = mapper.readTree(response.getBody());
            JsonNode jsonList = jsonNode.get("items");

            for (int i = 0; i < jsonList.size(); i++) {
                JSONObject tempObject = new JSONObject();
                tempObject.put("title", jsonList.get(i).get("snippet").get("title").asText());
                tempObject.put("videoLink", YOUTUBE_VIDEO_URL + jsonList.get(i).get("id").get("videoId").asText());
                tempObject.put("thumbnailsLink", jsonList.get(i).get("snippet").get("thumbnails").get("high").get("url").asText());
                resultObject.put(tempObject);
            }
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                resultObject.put("Invalid access token");
            } else {
                resultObject.put("API Connect Error : " + e.getMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
            resultObject.put("An unexpected error occurred.");
        }
        System.out.println(resultObject);
        return resultObject.toString();
    }

}
