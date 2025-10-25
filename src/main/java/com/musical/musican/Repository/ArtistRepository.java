package com.musical.musican.Repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.musical.musican.Model.Entity.Account;
import com.musical.musican.Model.Entity.Artist;

@Repository
public interface ArtistRepository extends JpaRepository<Artist, Integer> {
        List<Artist> findByNameContainingIgnoreCase(String name);

        @Query("SELECT a FROM Artist a WHERE " +
                        "(:keyword IS NULL OR LOWER(a.name) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
                        "(:categoryId IS NULL OR a.category.id = :categoryId)")
        List<Artist> search(@Param("keyword") String keyword,
                        @Param("categoryId") Long categoryId);

        List<Artist> findTop5ByOrderByCreatedAtDesc();

        long countByCreatedAtAfter(LocalDateTime date);

        List<Artist> findTop3ByOrderByCreatedAtDesc();

        long countByAccount(Account account);

        long countByAccountAndCreatedAtAfter(Account account, LocalDateTime createdAt);

        List<Artist> findTop5ByAccountOrderByCreatedAtDesc(Account account);
}
