package com.wos.musicMatchmaker.spotify.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wos.musicMatchmaker.config.SingletonVOconfig;
import com.wos.musicMatchmaker.spotify.dto.SpotifyAuthDTO;
import com.wos.musicMatchmaker.spotify.dto.SpotifyRcmDTO;
import com.wos.musicMatchmaker.spotify.dto.SpotifySearchDTO;
import com.wos.musicMatchmaker.spotify.vo.SpotifyAuthVO;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Service
public class SpotifyAPIService {

    private final String SPOTIFY_TOKEN_API_URL = "https://accounts.spotify.com";
    private final String SPOTIFY_API_URL = "https://api.spotify.com";

    @Autowired
    private RestTemplate restTemplate;

    @Value("${SPOTIFY_CLIENT_ID}")
    private String SPOTIFY_CLIENT_ID;

    @Value("${SPOTIFY_CLIENT_SECRET}")
    private String SPOTIFY_CLIENT_SECRET;

    /**
     * Get Spofity API Token
     * 
     * @param spotifyAuthDTO
     * @return
     */
    public Object getSpotifyAccessToken(SpotifyAuthDTO spotifyAuthDTO) {
        JSONArray resultObject = new JSONArray();
        SpotifyAuthVO spotifyAuthVO = null;

        String spotifyTokenUrl = SPOTIFY_TOKEN_API_URL + "/api/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", spotifyAuthDTO.getGrantType());
        body.add("client_id", SPOTIFY_CLIENT_ID);
        body.add("client_secret", SPOTIFY_CLIENT_SECRET);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);
        try{
            ResponseEntity<String> response = restTemplate.exchange(spotifyTokenUrl, HttpMethod.POST, request, String.class);

            ObjectMapper mapper = new ObjectMapper();
            spotifyAuthVO = mapper.readValue(response.getBody(), SpotifyAuthVO.class);
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

        SingletonVOconfig.INSTANCE.getInstance().setSpotifyAuthConfig(SPOTIFY_CLIENT_ID, spotifyAuthVO);
        resultObject.put("Get Token Success");
        return resultObject.toString();
    }

    /**
     * Verification Spofity API Token
     * Look up token value by client ID
     *
     * @param spotifyAuthDTO
     * @return
     */
    public SpotifyAuthVO getSaveSpotifyAccessToken(SpotifyAuthDTO spotifyAuthDTO) {
        return SingletonVOconfig.INSTANCE.getInstance().getSpotifyAuthConfig(spotifyAuthDTO.getClientId());
    }

    /**
     * Spofity API Search
     * Get track information by track title
     *
     * @param spotifySearchDTO
     * @return
     */
    public Object searchTrackList(SpotifySearchDTO spotifySearchDTO) {
        JSONArray resultObject = new JSONArray();

        SpotifyAuthVO spotifyAuthVO = SingletonVOconfig.INSTANCE.getInstance().getSpotifyAuthConfig(SPOTIFY_CLIENT_ID);
        if(spotifyAuthVO == null) {
            SpotifyAuthDTO spotifyAuthDTO = new SpotifyAuthDTO();
            spotifyAuthDTO.setGrantType("client_credentials");
            spotifyAuthDTO.setClientId(SPOTIFY_CLIENT_ID);
            spotifyAuthDTO.setClientSecret(SPOTIFY_CLIENT_SECRET);
            this.getSpotifyAccessToken(spotifyAuthDTO);
            spotifyAuthVO = SingletonVOconfig.INSTANCE.getInstance().getSpotifyAuthConfig(SPOTIFY_CLIENT_ID);
        }

        String spotifyApiUrl = SPOTIFY_API_URL + "/v1/search"
                + "?q=" + spotifySearchDTO.getSearchWord()
                + "&type=" + spotifySearchDTO.getType()
                + "&market=" + spotifySearchDTO.getMarket()
                + "&limit=" + spotifySearchDTO.getLimit()
                + "&offset=" + spotifySearchDTO.getOffset()
        ;

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(spotifyAuthVO.getAccessToken());

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(headers);

        try{
            ResponseEntity<String> response = restTemplate.exchange(spotifyApiUrl, HttpMethod.GET, request, String.class);

            // ObjectMapper 객체 생성
            ObjectMapper mapper = new ObjectMapper();

            // 문자열을 JsonNode로 변환
            JsonNode jsonNode = mapper.readTree(response.getBody());
            JsonNode jsonList = jsonNode.get("tracks").get("items");

            for (int i = 0; i < jsonList.size(); i++) {
                JSONObject tempObject = new JSONObject();
                tempObject.put("track", jsonList.get(i).get("name").asText());
                tempObject.put("track_id", jsonList.get(i).get("id").asText());
                tempObject.put("artists", jsonList.get(i).get("artists").get(0).get("name").asText());
                tempObject.put("artists_id", jsonList.get(i).get("artists").get(0).get("id").asText());
                tempObject.put("track_img", jsonList.get(i).get("album").get("images").get(2).get("url").asText());
                tempObject.put("track_url", jsonList.get(i).get("external_urls").get("spotify").asText());
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

        return resultObject.toString();
    }

    /**
     * Spofity API recommend
     * Get recommend track list information by track info
     *
     * @param spotifyRcmDTO
     * @return
     */
    public Object recommendTrackList(SpotifyRcmDTO spotifyRcmDTO) {
        JSONArray resultObject = new JSONArray();

        SpotifyAuthVO spotifyAuthVO = SingletonVOconfig.INSTANCE.getInstance().getSpotifyAuthConfig(SPOTIFY_CLIENT_ID);
        if(spotifyAuthVO == null) {
            SpotifyAuthDTO spotifyAuthDTO = new SpotifyAuthDTO();
            spotifyAuthDTO.setGrantType("client_credentials");
            spotifyAuthDTO.setClientId(SPOTIFY_CLIENT_ID);
            spotifyAuthDTO.setClientSecret(SPOTIFY_CLIENT_SECRET);
            this.getSpotifyAccessToken(spotifyAuthDTO);
            spotifyAuthVO = SingletonVOconfig.INSTANCE.getInstance().getSpotifyAuthConfig(SPOTIFY_CLIENT_ID);
        }

        String spotifyApiUrl = SPOTIFY_API_URL + "/v1/recommendations"
                + "?seed_artists=" + spotifyRcmDTO.getArtistsId()
                + "&seed_tracks=" + spotifyRcmDTO.getTrackId()
                + "&market=" + spotifyRcmDTO.getMarket()
                + "&limit=" + spotifyRcmDTO.getLimit()
        ;

        if(spotifyRcmDTO.getSeedGenres() != null) spotifyApiUrl += "&seedGenres=" + spotifyRcmDTO.getSeedGenres();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(spotifyAuthVO.getAccessToken());

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(spotifyApiUrl, HttpMethod.GET, request, String.class);
            if (response.getStatusCode() == HttpStatus.OK) {

               // ObjectMapper 객체 생성
               ObjectMapper mapper = new ObjectMapper();

               // 문자열을 JsonNode로 변환
               JsonNode jsonNode = mapper.readTree(response.getBody());
               JsonNode jsonList = jsonNode.get("tracks");

               for (int i = 0; i < jsonList.size(); i++) {
                   JSONObject tempObject = new JSONObject();
                   tempObject.put("track", jsonList.get(i).get("name").asText());
                   tempObject.put("artists", jsonList.get(i).get("artists").get(0).get("name").asText());
                   tempObject.put("track_img", jsonList.get(i).get("album").get("images").get(0).get("url").asText());
                   tempObject.put("track_url", jsonList.get(i).get("external_urls").get("spotify").asText());
                   resultObject.put(tempObject);
               }
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

        return resultObject.toString();
    }

}
