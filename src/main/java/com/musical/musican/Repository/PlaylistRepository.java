package com.musical.musican.Repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.musical.musican.Model.Entity.Account;
import com.musical.musican.Model.Entity.Playlist;

@Repository
public interface PlaylistRepository extends JpaRepository<Playlist, Integer> {
    List<Playlist> findByAccountId(Integer accountId);

    List<Playlist> findByIsPublicTrue();

    List<Playlist> findByTitleContainingIgnoreCase(String title);

    List<Playlist> findByTitleContainingIgnoreCaseAndIsPublic(String title, Boolean isPublic);

    List<Playlist> findByIsPublic(Boolean isPublic);

    boolean existsByTitleAndAccount(String title, Account account);

    List<Playlist> findByTitleContainingIgnoreCaseAndAccount(String title, Account account);

    List<Playlist> findByTitleContainingIgnoreCaseAndIsPublicAndAccount(String title, Boolean isPublic,
            Account account);

    List<Playlist> findByIsPublicAndAccount(Boolean isPublic, Account account);

    List<Playlist> findByAccount(Account account);

    List<Playlist> findTop10ByOrderByCreatedAtDesc();

    List<Playlist> findTop5ByOrderByCreatedAtDesc();

    long countByCreatedAtAfter(LocalDateTime date);

       long countByAccount(Account account);

    long countByAccountAndCreatedAtAfter(Account account, LocalDateTime createdAt);

    List<Playlist> findTop5ByAccountOrderByCreatedAtDesc(Account account);
}
