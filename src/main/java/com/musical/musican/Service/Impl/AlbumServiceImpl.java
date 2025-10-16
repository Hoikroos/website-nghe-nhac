package com.musical.musican.Service.Impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.musical.musican.Model.Entity.Album;
import com.musical.musican.Model.Entity.Artist;
import com.musical.musican.Repository.AlbumRepository;
import com.musical.musican.Service.AlbumService;

@Service
public class AlbumServiceImpl implements AlbumService {
    @Autowired
    private AlbumRepository albumRepository;

    @Override
    public List<Album> findAll() {
        return albumRepository.findAll();
    }

    @Override
    public List<Album> searchAlbums(String title, Integer artistId) {
        boolean hasTitle = title != null && !title.isEmpty();
        boolean hasArtist = artistId != null;

        if (hasTitle && hasArtist) {
            return albumRepository.findByTitleContainingIgnoreCaseAndArtistId(title, artistId);
        } else if (hasTitle) {
            return albumRepository.findByTitleContainingIgnoreCase(title);
        } else if (hasArtist) {
            return albumRepository.findByArtistId(artistId);
        } else {
            return albumRepository.findAll();
        }
    }

    @Override
    public Optional<Album> findById(Integer id) {
        return albumRepository.findById(id);
    }

    @Override
    public Album save(Album album) {
        return albumRepository.save(album);
    }

    @Override
    public Album update(Integer id, Album albumData) {
        Album album = albumRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy album có ID: " + id));
        album.setTitle(albumData.getTitle());
        album.setReleaseDate(albumData.getReleaseDate());
        album.setCoverUrl(albumData.getCoverUrl());
        return albumRepository.save(album);
    }

    @Override
    public void delete(Integer id) {
        albumRepository.deleteById(id);
    }
}
