package com.musical.musican.Service.Impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.musical.musican.Model.Entity.Account;
import com.musical.musican.Model.Entity.Album;
import com.musical.musican.Model.Entity.Artist;
import com.musical.musican.Repository.AccountRepository;
import com.musical.musican.Repository.AlbumRepository;
import com.musical.musican.Security.CustomUserDetails;
import com.musical.musican.Service.AlbumService;

@Service
public class AlbumServiceImpl implements AlbumService {
    @Autowired
    private AlbumRepository albumRepository;

    @Autowired
    private AccountRepository accountRepository;

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

    @Override
    public Account getCurrentAccount() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()
                || authentication.getPrincipal().equals("anonymousUser")) {
            throw new IllegalStateException("Bạn cần đăng nhập để thực hiện hành động này!");
        }

        String username = null;
        if (authentication.getPrincipal() instanceof CustomUserDetails customUserDetails) {
            username = customUserDetails.getUsername();
        } else if (authentication.getPrincipal() instanceof org.springframework.security.core.userdetails.User user) {
            username = user.getUsername();
        }

        if (username == null) {
            throw new IllegalStateException("Không thể xác định người dùng hiện tại!");
        }

        Account account = accountRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("Tài khoản không tồn tại"));

        if (!"MUSICIAN".equals(account.getRole().name())) {
            throw new IllegalStateException("Chỉ tài khoản MUSICIAN mới có quyền thực hiện hành động này!");
        }

        return account;
    }

    @Override
    public List<Album> findByAccount(Account account) {
        return albumRepository.findByAccount(account);
    }

}
