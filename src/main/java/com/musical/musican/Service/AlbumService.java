package com.musical.musican.Service;

import java.util.List;
import java.util.Optional;

import com.musical.musican.Model.Entity.Album;

public interface AlbumService {
    List<Album> findAll();

     List<Album> searchAlbums(String title, Integer artistId);

    Optional<Album> findById(Integer id);

    Album save(Album album);

    Album update(Integer id, Album albumData);

    void delete(Integer id);

}
