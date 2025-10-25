package com.musical.musican.Repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.musical.musican.Model.Entity.Account;
import com.musical.musican.Model.Entity.Track;

@Repository
public interface TrackRepository extends JpaRepository<Track, Integer> {
        List<Track> findByAlbumId(Integer albumId);

        List<Track> findByTitleContainingIgnoreCase(String keyword);

        @Query("SELECT t FROM Track t WHERE t.account = :account "
                        + "AND (:title IS NULL OR LOWER(t.title) LIKE LOWER(CONCAT('%', :title, '%'))) "
                        + "AND (:albumId IS NULL OR t.album.id = :albumId) "
                        + "AND (:sourceType IS NULL OR t.sourceType = :sourceType)")
        List<Track> searchTracksByAccount(@Param("account") Account account,
                        @Param("title") String title,
                        @Param("albumId") Integer albumId,
                        @Param("sourceType") Track.SourceType sourceType);

        List<Track> findTop10ByOrderByCreatedAtDesc();

        List<Track> findTop5ByOrderByCreatedAtDesc();

        long countByCreatedAtAfter(LocalDateTime date);

        long countByAccount(Account account);

        long countByAccountAndCreatedAtAfter(Account account, LocalDateTime createdAt);

        List<Track> findTop5ByAccountOrderByCreatedAtDesc(Account account);
}
