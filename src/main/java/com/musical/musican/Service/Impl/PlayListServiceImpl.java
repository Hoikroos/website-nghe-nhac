package com.musical.musican.Service.Impl;

import com.musical.musican.Model.Entity.Account;
import com.musical.musican.Model.Entity.Playlist;
import com.musical.musican.Model.Entity.PlaylistTrack;
import com.musical.musican.Model.Entity.Track;
import com.musical.musican.Repository.PlaylistRepository;
import com.musical.musican.Repository.PlaylistTrackRepository;
import com.musical.musican.Repository.TrackRepository;
import com.musical.musican.Service.PlayListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PlayListServiceImpl implements PlayListService {

    @Autowired
    private PlaylistRepository playlistRepository;

    @Autowired
    private TrackRepository trackRepository;

    @Autowired
    private PlaylistTrackRepository playlistTrackRepository;

    @Override
    public List<Playlist> getAllPlaylists() {
        return playlistRepository.findAll().stream()
                .filter(p -> p != null && p.getAccount() != null)
                .collect(Collectors.toList());
    }

    @Override
    public List<Playlist> searchPlaylists(String title, Boolean isPublic, Account account) {
        if (title != null && !title.trim().isEmpty() && isPublic != null) {
            return playlistRepository.findByTitleContainingIgnoreCaseAndIsPublicAndAccount(
                    title.trim(), isPublic, account);
        } else if (title != null && !title.trim().isEmpty()) {
            return playlistRepository.findByTitleContainingIgnoreCaseAndAccount(title.trim(), account);
        } else if (isPublic != null) {
            return playlistRepository.findByIsPublicAndAccount(isPublic, account);
        }
        return playlistRepository.findByAccount(account);
    }

    @Override
    public Optional<Playlist> getPlaylistById(Integer id) {
        if (id == null) {
            return Optional.empty();
        }
        return playlistRepository.findById(id);
    }

    @Override
    public Playlist addPlaylist(Playlist playlist) throws IllegalArgumentException {
        if (playlist == null || playlist.getTitle() == null || playlist.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Tên playlist không được để trống!");
        }
        if (playlist.getAccount() == null) {
            throw new IllegalArgumentException("Thông tin tài khoản không hợp lệ!");
        }
        if (!"MUSICIAN".equalsIgnoreCase(playlist.getAccount().getRole().toString())) {
            throw new IllegalArgumentException("Chỉ tài khoản MUSICIAN mới có thể thực hiện hành động này!");
        }

        if (playlistRepository.existsByTitleAndAccount(playlist.getTitle().trim(), playlist.getAccount())) {
            throw new IllegalArgumentException("Playlist với tên này đã tồn tại!");
        }
        return playlistRepository.save(playlist);
    }

    @Override
    public Playlist updatePlaylist(Integer id, Playlist updatedPlaylist) throws IllegalArgumentException {
        if (id == null || updatedPlaylist == null || updatedPlaylist.getTitle() == null
                || updatedPlaylist.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Thông tin playlist không hợp lệ!");
        }
        Optional<Playlist> existingPlaylist = getPlaylistById(id);
        if (!existingPlaylist.isPresent()) {
            throw new IllegalArgumentException("Playlist không tồn tại!");
        }
        Playlist original = existingPlaylist.get();
        if (!original.getAccount().getRole().name().equals("MUSICIAN")) {
            throw new IllegalArgumentException("Chỉ tài khoản MUSICIAN mới có thể sửa playlist!");
        }
        if (!original.getTitle().equals(updatedPlaylist.getTitle().trim()) &&
                playlistRepository.existsByTitleAndAccount(updatedPlaylist.getTitle().trim(), original.getAccount())) {
            throw new IllegalArgumentException("Tên playlist mới đã tồn tại!");
        }
        updatedPlaylist.setId(id);
        updatedPlaylist.setAccount(original.getAccount());
        updatedPlaylist.setCreatedAt(original.getCreatedAt());
        updatedPlaylist.setPlaylistTracks(original.getPlaylistTracks());
        return playlistRepository.save(updatedPlaylist);
    }

    @Override
    public void updatePlaylistTracks(Integer playlistId, List<Integer> trackIds) throws IllegalArgumentException {
        Optional<Playlist> playlistOpt = getPlaylistById(playlistId);
        if (!playlistOpt.isPresent()) {
            throw new IllegalArgumentException("Playlist không tồn tại!");
        }
        Playlist playlist = playlistOpt.get();
        if (!playlist.getAccount().getRole().name().equals("MUSICIAN")) {
            throw new IllegalArgumentException("Chỉ tài khoản MUSICIAN mới có thể chỉnh sửa bài hát!");
        }

        // Xóa tất cả các PlaylistTrack hiện tại
        playlistTrackRepository.deleteByPlaylistId(playlistId);

        // Thêm các track mới
        if (trackIds != null && !trackIds.isEmpty()) {
            List<PlaylistTrack> newTracks = new ArrayList<>();
            for (Integer trackId : trackIds) {
                Optional<Track> trackOpt = trackRepository.findById(trackId);
                if (trackOpt.isPresent()) {
                    PlaylistTrack playlistTrack = new PlaylistTrack();
                    playlistTrack.setPlaylist(playlist);
                    playlistTrack.setTrack(trackOpt.get());
                    newTracks.add(playlistTrack);
                } else {
                    throw new IllegalArgumentException("Bài hát với ID " + trackId + " không tồn tại!");
                }
            }
            playlistTrackRepository.saveAll(newTracks);
        }
    }

    @Override
    public void deletePlaylist(Integer id) throws IllegalArgumentException {
        Optional<Playlist> playlistOpt = getPlaylistById(id);
        if (!playlistOpt.isPresent()) {
            throw new IllegalArgumentException("Playlist không tồn tại!");
        }
        Playlist playlist = playlistOpt.get();
        if (!playlist.getAccount().getRole().name().equals("MUSICIAN")) {
            throw new IllegalArgumentException("Chỉ tài khoản MUSICIAN mới có thể xóa playlist!");
        }
        playlistRepository.deleteById(id);
    }
}