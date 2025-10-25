package com.musical.musican.Service.Impl;

import com.musical.musican.Model.Entity.Account;
import com.musical.musican.Model.Entity.Track;
import com.musical.musican.Repository.AccountRepository;
import com.musical.musican.Repository.TrackRepository;
import com.musical.musican.Security.CustomUserDetails;
import com.musical.musican.Service.TrackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;

@Service
public class TrackServiceImpl implements TrackService {

    private static final String UPLOAD_DIR = "src/main/resources/static/uploads/tracks/";
    @Autowired
    private TrackRepository trackRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Override
    public List<Track> searchTracksByAccount(Account account, String title, Integer albumId, String sourceType) {
        Track.SourceType type = null;
        if (sourceType != null && !sourceType.isEmpty()) {
            type = Track.SourceType.valueOf(sourceType.toUpperCase());
        }
        return trackRepository.searchTracksByAccount(account, title, albumId, type);
    }

    @Override
    public List<Track> getAllTracks() {
        return trackRepository.findAll();
    }

    @Override
    public Track getTrackById(Integer id) {
        return trackRepository.findById(id).orElse(null);
    }

    @Override
    public List<Track> getTracksByAlbumId(Integer albumId) {
        return trackRepository.findByAlbumId(albumId);
    }

    @Override
    public List<Track> getTracksByTitle(String keyword) {
        return trackRepository.findByTitleContainingIgnoreCase(keyword);
    }

    @Override
    public void deleteTrackById(Integer id) {
        trackRepository.deleteById(id);
    }

    @Override
    public Track save(Track track) {
        return trackRepository.save(track);
    }

    @Override
    public Track update(Integer id, Track trackData, MultipartFile fileUpload, String externalLink) {
        Track track = trackRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy track có ID: " + id));

        track.setTitle(trackData.getTitle());
        track.setDuration(trackData.getDuration());
        track.setAlbum(trackData.getAlbum());

        if (fileUpload != null && !fileUpload.isEmpty()) {
            String filePath = saveFile(fileUpload);
            track.setAudioUrl(filePath);
            track.setSourceType(Track.SourceType.UPLOAD);
        } else if (externalLink != null && !externalLink.trim().isEmpty()) {
            track.setAudioUrl(externalLink.trim());
            track.setSourceType(Track.SourceType.EXTERNAL);
        }

        return trackRepository.save(track);
    }

    @Override
    public String saveFile(MultipartFile file) {
        try {
            if (file.isEmpty())
                return null;
            Files.createDirectories(Paths.get(UPLOAD_DIR));
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path filePath = Paths.get(UPLOAD_DIR + fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            return "/uploads/tracks/" + fileName;
        } catch (IOException e) {
            throw new RuntimeException("Lỗi khi lưu file: " + e.getMessage(), e);
        }
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
}
