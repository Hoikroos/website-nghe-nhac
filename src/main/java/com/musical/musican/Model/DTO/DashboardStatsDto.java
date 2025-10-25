package com.musical.musican.Model.DTO;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardStatsDto {
    private long totalArtists;
    private double artistsChange;
    
    private long totalAlbums;
    private double albumsChange;
    
    private long totalTracks;
    private double tracksChange;
    
    private long totalPlaylists;
    private double playlistsChange;
}