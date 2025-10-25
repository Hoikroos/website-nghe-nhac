package com.musical.musican.Service.Impl;

import com.musical.musican.Model.DTO.ActivityDto;
import com.musical.musican.Model.DTO.DashboardStatsDto;
import com.musical.musican.Model.Entity.*;
import com.musical.musican.Repository.*;
import com.musical.musican.Service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DashboardServiceImpl implements DashboardService {

    @Autowired
    private ArtistRepository artistRepository;

    @Autowired
    private AlbumRepository albumRepository;

    @Autowired
    private TrackRepository trackRepository;

    @Autowired
    private PlaylistRepository playlistRepository;

    @Autowired
    private FavouriteRepository favouriteRepository;

    @Override
    public DashboardStatsDto getDashboardStats(Account account) {
        DashboardStatsDto stats = new DashboardStatsDto();

        // Đếm dữ liệu của tài khoản hiện tại
        stats.setTotalArtists(artistRepository.countByAccount(account));
        stats.setTotalAlbums(albumRepository.countByAccount(account));
        stats.setTotalTracks(trackRepository.countByAccount(account));
        stats.setTotalPlaylists(playlistRepository.countByAccount(account));

        LocalDateTime lastMonth = LocalDateTime.now().minusMonths(1);

        stats.setArtistsChange(calculateChange(
                artistRepository.countByAccountAndCreatedAtAfter(account, lastMonth),
                stats.getTotalArtists()));

        stats.setAlbumsChange(calculateChange(
                albumRepository.countByAccountAndCreatedAtAfter(account, lastMonth),
                stats.getTotalAlbums()));

        stats.setTracksChange(calculateChange(
                trackRepository.countByAccountAndCreatedAtAfter(account, lastMonth),
                stats.getTotalTracks()));

        stats.setPlaylistsChange(calculateChange(
                playlistRepository.countByAccountAndCreatedAtAfter(account, lastMonth),
                stats.getTotalPlaylists()));

        return stats;
    }

    @Override
    public List<ActivityDto> getRecentActivities(Account account, int limit) {
        List<ActivityDto> activities = new ArrayList<>();

        try {
            // Lấy theo account hiện tại
            List<Artist> recentArtists = artistRepository.findTop5ByAccountOrderByCreatedAtDesc(account);
            for (Artist artist : recentArtists) {
                activities.add(ActivityDto.builder()
                        .title("Thêm nghệ sĩ mới \"" + artist.getName() + "\"")
                        .icon("fas fa-microphone")
                        .timeAgo(calculateTimeAgo(artist.getCreatedAt()))
                        .createdAt(artist.getCreatedAt())
                        .type("artist")
                        .build());
            }

            List<Album> recentAlbums = albumRepository.findTop5ByAccountOrderByCreatedAtDesc(account);
            for (Album album : recentAlbums) {
                activities.add(ActivityDto.builder()
                        .title("Thêm album mới \"" + album.getTitle() + "\"")
                        .icon("fas fa-compact-disc")
                        .timeAgo(calculateTimeAgo(album.getCreatedAt()))
                        .createdAt(album.getCreatedAt())
                        .type("album")
                        .build());
            }

            List<Track> recentTracks = trackRepository.findTop5ByAccountOrderByCreatedAtDesc(account);
            for (Track track : recentTracks) {
                activities.add(ActivityDto.builder()
                        .title("Upload bài hát \"" + track.getTitle() + "\"")
                        .icon("fas fa-music")
                        .timeAgo(calculateTimeAgo(track.getCreatedAt()))
                        .createdAt(track.getCreatedAt())
                        .type("track")
                        .build());
            }

            List<Playlist> recentPlaylists = playlistRepository.findTop5ByAccountOrderByCreatedAtDesc(account);
            for (Playlist playlist : recentPlaylists) {
                activities.add(ActivityDto.builder()
                        .title("Tạo playlist \"" + playlist.getTitle() + "\"")
                        .icon("fas fa-list")
                        .timeAgo(calculateTimeAgo(playlist.getCreatedAt()))
                        .createdAt(playlist.getCreatedAt())
                        .type("playlist")
                        .build());
            }

        } catch (Exception e) {
            // activities.addAll(getFallbackActivities());
        }

        return activities.stream()
                .sorted((a, b) -> {
                    if (a.getCreatedAt() == null && b.getCreatedAt() == null)
                        return 0;
                    if (a.getCreatedAt() == null)
                        return 1;
                    if (b.getCreatedAt() == null)
                        return -1;
                    return b.getCreatedAt().compareTo(a.getCreatedAt());
                })
                .limit(limit)
                .collect(Collectors.toList());
    }

    private double calculateChange(long currentMonth, long total) {
        if (total == 0)
            return 0;
        return ((double) currentMonth / total * 100);
    }

    private String calculateTimeAgo(LocalDateTime createdAt) {
        if (createdAt == null)
            return "Vừa xong";

        long hours = ChronoUnit.HOURS.between(createdAt, LocalDateTime.now());
        if (hours < 1)
            return "Vừa xong";
        else if (hours < 24)
            return hours + " giờ trước";
        else {
            long days = ChronoUnit.DAYS.between(createdAt, LocalDateTime.now());
            return days + " ngày trước";
        }
    }
}