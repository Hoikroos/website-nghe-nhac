package com.musical.musican.Service.Impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.musical.musican.Model.Entity.Artist;
import com.musical.musican.Repository.ArtistRepository;
import com.musical.musican.Service.ArtistService;

@Service
public class ArtistServiceImpl implements ArtistService {
    @Autowired
    private ArtistRepository artistRepository;

    @Override
    public List<Artist> findAll() {
        return artistRepository.findAll();
    }

    @Override
    public List<Artist> search(String keyword, Long categoryId) {
        String trimmedKeyword = (keyword == null || keyword.trim().isEmpty()) ? null : keyword.trim();
        return artistRepository.search(trimmedKeyword, categoryId);
    }

    @Override
    public List<Artist> searchByName(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return artistRepository.findAll();
        }
        return artistRepository.findByNameContainingIgnoreCase(keyword);
    }

    @Override
    public Optional<Artist> findById(Integer id) {
        return artistRepository.findById(id);
    }

    @Override
    public Artist save(Artist artist) {
        artist.setCreatedAt(LocalDateTime.now());
        return artistRepository.save(artist);
    }

    @Override
    public Artist update(Integer id, Artist artistData) {
        Artist artist = artistRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nghệ sĩ có ID: " + id));
        artist.setName(artistData.getName());
        artist.setBio(artistData.getBio());
        artist.setCategory(artistData.getCategory());
        return artistRepository.save(artist);
    }

    @Override
    public void delete(Integer id) {
        artistRepository.deleteById(id);
    }
}
