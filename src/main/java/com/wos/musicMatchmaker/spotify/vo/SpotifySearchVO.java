package com.wos.musicMatchmaker.spotify.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class SpotifySearchVO {

    private TrackSearchResult tracks;

   public class TrackSearchResult {
       private String href;
       private List<Track> items;
       private int limit;
       private String next;
       private int offset;
       private String previous;
       private int total;
   }

   public class Track {
       private Album album;
       private List<Artist> artists;
       private int disc_number;
       private int duration_ms;
       private boolean explicit;
       private ExternalIds external_ids;
       private ExternalUrls external_urls;
       private String href;
       private String id;
       private boolean is_local;
       private boolean is_playable;
       private String name;
       private int popularity;
       private String preview_url;
       private int track_number;
       private String type;
       private String uri;

       // Getters and setters
   }

   public class Album {
       private String album_type;
       private List<Artist> artists;
       private ExternalUrls external_urls;
       private String href;
       private String id;
       private List<Image> images;
       private boolean is_playable;
       private String name;
       private String release_date;
       private String release_date_precision;
       private int total_tracks;
       private String type;
       private String uri;

       // Getters and setters
   }

   public class Artist {
       private ExternalUrls external_urls;
       private String href;
       private String id;
       private String name;
       private String type;
       private String uri;

       // Getters and setters
   }

   public class ExternalIds {
       private String isrc;

       // Getters and setters
   }

   public class ExternalUrls {
       private String spotify;

       // Getters and setters
   }

   public class Image {
       private int height;
       private String url;
       private int width;

       // Getters and setters
   }

}
