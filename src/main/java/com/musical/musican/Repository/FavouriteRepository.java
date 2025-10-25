package com.musical.musican.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.musical.musican.Model.Entity.Favourite;

@Repository
public interface FavouriteRepository extends JpaRepository<Favourite, Integer> {
    List<Favourite> findByAccountId(Integer accountId);

    List<Favourite> findTop10ByOrderByCreatedAtDesc();
}
