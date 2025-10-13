package com.musical.musican.Service;

import java.util.List;
import java.util.Optional;

import com.musical.musican.Model.Entity.Account;
import com.musical.musican.Model.Entity.Playlist;

public interface PlayListService {
    List<Playlist> getAllPlaylists();

    List<Playlist> searchPlaylists(String title, Boolean isPublic, Account account);

    Optional<Playlist> getPlaylistById(Integer id);

    Playlist addPlaylist(Playlist playlist) throws IllegalArgumentException;

    Playlist updatePlaylist(Integer id, Playlist updatedPlaylist) throws IllegalArgumentException;

    void updatePlaylistTracks(Integer playlistId, List<Integer> trackIds) throws IllegalArgumentException;

    void deletePlaylist(Integer id) throws IllegalArgumentException;
}
