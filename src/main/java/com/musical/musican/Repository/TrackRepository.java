package com.musical.musican.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.musical.musican.Model.Entity.Track;

@Repository
public interface TrackRepository extends JpaRepository<Track, Integer> {
    List<Track> findByAlbumId(Integer albumId);
    List<Track> findByTitleContainingIgnoreCase(String keyword);
}
