package com.wos.musicMatchmaker.spotify.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wos.musicMatchmaker.common.util.RandomKeyGenerator;
import com.wos.musicMatchmaker.config.SingletonVOconfig;
import com.wos.musicMatchmaker.spotify.dao.SpotifyAPIDAO;
import com.wos.musicMatchmaker.spotify.dto.SpotifyAuthDTO;
import com.wos.musicMatchmaker.spotify.dto.SpotifyRcmDTO;
import com.wos.musicMatchmaker.spotify.dto.SpotifySearchDTO;
import com.wos.musicMatchmaker.spotify.vo.SpotifyAuthVO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
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

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class SpotifyAPIService {

    private final String SPOTIFY_TOKEN_API_URL = "https://accounts.spotify.com";
    private final String SPOTIFY_API_URL = "https://api.spotify.com";

    @Autowired
    private SpotifyAPIDAO spotifyAPIDAO;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${SPOTIFY_CLIENT_ID}")
    private String SPOTIFY_CLIENT_ID;

    @Value("${SPOTIFY_CLIENT_SECRET}")
    private String SPOTIFY_CLIENT_SECRET;


    /**
     *  Token Refresh
     */
    public void getSpotifyRefreshAccessTokenSchedule() {
        String spotifyTokenUrl = SPOTIFY_TOKEN_API_URL + "/api/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "client_credentials");
        body.add("client_id", SPOTIFY_CLIENT_ID);
        body.add("client_secret", SPOTIFY_CLIENT_SECRET);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);
        try{
            ResponseEntity<String> response = restTemplate.exchange(spotifyTokenUrl, HttpMethod.POST, request, String.class);
            ObjectMapper mapper = new ObjectMapper();
            SpotifyAuthVO spotifyAuthVO = mapper.readValue(response.getBody(), SpotifyAuthVO.class);
            SingletonVOconfig.INSTANCE.getInstance().setSpotifyAuthConfig(SPOTIFY_CLIENT_ID, spotifyAuthVO);
            log.info("getSpotifyRefreshAccessTokenSchedule SUCCESS");
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    /**
     * Get the created Spotify API token
     * @return
     */
    private SpotifyAuthVO useSpotifyAccessToken() {
        SpotifyAuthVO spotifyAuthVO = SingletonVOconfig.INSTANCE.getInstance().getSpotifyAuthConfig(SPOTIFY_CLIENT_ID);
        if(spotifyAuthVO == null) {
            SpotifyAuthDTO spotifyAuthDTO = new SpotifyAuthDTO();
            spotifyAuthDTO.setGrantType("client_credentials");
            spotifyAuthDTO.setClientId(SPOTIFY_CLIENT_ID);
            spotifyAuthDTO.setClientSecret(SPOTIFY_CLIENT_SECRET);
            this.getSpotifyAccessToken(spotifyAuthDTO);
            spotifyAuthVO = SingletonVOconfig.INSTANCE.getInstance().getSpotifyAuthConfig(SPOTIFY_CLIENT_ID);
        }
        return spotifyAuthVO;
    }

    /**
     * Get Spofity API Token
     * 
     * @param spotifyAuthDTO
     * @return
     */
    public ResponseEntity<?> getSpotifyAccessToken(SpotifyAuthDTO spotifyAuthDTO) {
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
            SingletonVOconfig.INSTANCE.getInstance().setSpotifyAuthConfig(SPOTIFY_CLIENT_ID, spotifyAuthVO);
            resultObject.put("Get Token Success");

            return new ResponseEntity<>(resultObject.toString(), HttpStatus.OK);
        } catch (HttpClientErrorException e) {
            return new ResponseEntity<>(e.getMessage(), e.getStatusCode());
        } catch (Exception e) {
            return new ResponseEntity<>("An unexpected error occurred.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Verification Spofity API Token
     * Look up token value by client ID
     *
     * @param spotifyAuthDTO
     * @return
     */
    public ResponseEntity<?>  getSaveSpotifyAccessToken(SpotifyAuthDTO spotifyAuthDTO) {
        return new ResponseEntity<>(SingletonVOconfig.INSTANCE.getInstance().getSpotifyAuthConfig(spotifyAuthDTO.getClientId()), HttpStatus.OK);
    }

    /**
     * Spofity API Search
     * Get track information by track title
     *
     * @param spotifySearchDTO
     * @return
     */
    public ResponseEntity<?> searchTrackList(SpotifySearchDTO spotifySearchDTO) {
        JSONArray resultObject = new JSONArray();

        SpotifyAuthVO spotifyAuthVO = this.useSpotifyAccessToken();

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
                tempObject.put("artist", jsonList.get(i).get("artists").get(0).get("name").asText());
                tempObject.put("artist_id", jsonList.get(i).get("artists").get(0).get("id").asText());
                tempObject.put("track_img", jsonList.get(i).get("album").get("images").get(0).get("url").asText());
                tempObject.put("track_url", jsonList.get(i).get("external_urls").get("spotify").asText());
                resultObject.put(tempObject);
            }
            return new ResponseEntity<>(resultObject.toString(), HttpStatus.OK);
        } catch (HttpClientErrorException e) {
            return new ResponseEntity<>(e.getMessage(), e.getStatusCode());
        } catch (Exception e) {
            return new ResponseEntity<>("An unexpected error occurred.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Spofity API recommend
     * Get recommend track list information by track info
     *
     * @param spotifyRcmDTO
     * @return
     */
    public ResponseEntity<?> recommendTrackList(HttpServletRequest httpServletRequest, SpotifyRcmDTO spotifyRcmDTO) {
        JSONArray resultObject = new JSONArray();

        SpotifyAuthVO spotifyAuthVO = this.useSpotifyAccessToken();

        try {
            Map<String, Object> params = new HashMap<>();
            String ipAddress = httpServletRequest.getRemoteAddr();
            String hisSeq = RandomKeyGenerator.generateRandomKey("HIS_");
            params.put("hisSeq", hisSeq);
            params.put("trackTitle", spotifyRcmDTO.getTrackTitle());
            params.put("trackId", spotifyRcmDTO.getTrackId());
            params.put("artistsId", spotifyRcmDTO.getArtistsId());
            params.put("artistsName", spotifyRcmDTO.getArtistsName());
            params.put("nationalCode", spotifyRcmDTO.getMarket());
            params.put("regIp", ipAddress);
            Integer result = spotifyAPIDAO.insertTrackSearchHistory(params);
        } catch (Exception e) {
            log.error(e.toString());
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
                   tempObject.put("artist", jsonList.get(i).get("artists").get(0).get("name").asText());
                   tempObject.put("track_img", jsonList.get(i).get("album").get("images").get(0).get("url").asText());
                   tempObject.put("track_url", jsonList.get(i).get("external_urls").get("spotify").asText());
                   resultObject.put(tempObject);
               }
            }
            return new ResponseEntity<>(resultObject.toString(), HttpStatus.OK);
        } catch (HttpClientErrorException e) {
            return new ResponseEntity<>(e.getMessage(), e.getStatusCode());
        } catch (Exception e) {
            return new ResponseEntity<>("An unexpected error occurred.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
