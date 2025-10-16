package com.musical.musican.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.musical.musican.Model.Entity.Album;

@Repository
public interface AlbumRepository extends JpaRepository<Album, Integer> {
    List<Album> findByArtistId(Integer artistId);

    List<Album> findByTitleContainingIgnoreCase(String title);

    List<Album> findByTitleContainingIgnoreCaseAndArtistId(String title, Integer artistId);
}
