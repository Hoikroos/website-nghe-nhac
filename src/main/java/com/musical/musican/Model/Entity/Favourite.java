package com.musical.musican.Model.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "favourite")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Favourite {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // User đã tạo yêu thích
    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    // Chỉ 1 trong 4 field này được set (artist, album, playlist hoặc track)
    @ManyToOne
    @JoinColumn(name = "artist_id")
    private Artist artist;

    @ManyToOne
    @JoinColumn(name = "album_id")
    private Album album;

    @ManyToOne
    @JoinColumn(name = "playlist_id")
    private Playlist playlist; // ✅ sửa lại: tham chiếu trực tiếp playlist

    @ManyToOne
    @JoinColumn(name = "track_id")
    private Track track;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}
