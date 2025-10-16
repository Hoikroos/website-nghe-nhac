package com.musical.musican.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.musical.musican.Model.Entity.Artist;

@Repository
public interface ArtistRepository extends JpaRepository<Artist, Integer> {
    List<Artist> findByNameContainingIgnoreCase(String name);

    @Query("SELECT a FROM Artist a WHERE " +
            "(:keyword IS NULL OR LOWER(a.name) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
            "(:categoryId IS NULL OR a.category.id = :categoryId)")
    List<Artist> search(@Param("keyword") String keyword,
            @Param("categoryId") Long categoryId);
}
