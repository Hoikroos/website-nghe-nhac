package com.musical.musican.Service;

import java.util.List;
import java.util.Optional;

import com.musical.musican.Model.Entity.Artist;

public interface ArtistService {

    List<Artist> findAll();

    List<Artist> search(String keyword, Long categoryId);

    List<Artist> searchByName(String keyword);

    Optional<Artist> findById(Integer id);

    Artist save(Artist artist);

    Artist update(Integer id, Artist artist);

    void delete(Integer id);
}
