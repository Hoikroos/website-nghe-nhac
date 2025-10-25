package com.musical.musican.Repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.musical.musican.Model.Entity.Account;
import com.musical.musican.Model.Entity.Album;

@Repository
public interface AlbumRepository extends JpaRepository<Album, Integer> {
    List<Album> findByArtistId(Integer artistId);

    List<Album> findByTitleContainingIgnoreCase(String title);

    List<Album> findByTitleContainingIgnoreCaseAndArtistId(String title, Integer artistId);

    List<Album> findTop5ByOrderByCreatedAtDesc();

    long countByCreatedAtAfter(LocalDateTime date);

    List<Album> findTop10ByOrderByCreatedAtDesc();

    List<Album> findByAccount(Account account);

    long countByAccount(Account account);

    long countByAccountAndCreatedAtAfter(Account account, LocalDateTime createdAt);

    List<Album> findTop5ByAccountOrderByCreatedAtDesc(Account account);
}
