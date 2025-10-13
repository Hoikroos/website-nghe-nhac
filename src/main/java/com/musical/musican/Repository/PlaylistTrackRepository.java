package com.musical.musican.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.musical.musican.Model.Entity.PlaylistTrack;
import com.musical.musican.Model.Entity.PlaylistTrackId;

@Repository
public interface PlaylistTrackRepository extends JpaRepository<PlaylistTrack, PlaylistTrackId> {
    List<PlaylistTrack> findByPlaylistId(Integer playlistId);
    void deleteByPlaylistId(Integer playlistId);
}
