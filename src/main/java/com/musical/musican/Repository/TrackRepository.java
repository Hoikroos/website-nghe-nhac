package com.musical.musican.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.musical.musican.Model.Entity.Track;

@Repository
public interface TrackRepository extends JpaRepository<Track, Integer> {
    List<Track> findByAlbumId(Integer albumId);

    List<Track> findByTitleContainingIgnoreCase(String keyword);

     @Query("SELECT t FROM Track t " +
           "WHERE (:title IS NULL OR t.title LIKE %:title%) " +
           "AND (:albumId IS NULL OR t.album.id = :albumId) " +
           "AND (:sourceType IS NULL OR t.sourceType = :sourceType)")
    List<Track> searchTracks(@Param("title") String title,
                             @Param("albumId") Integer albumId,
                             @Param("sourceType") Track.SourceType sourceType);
}
