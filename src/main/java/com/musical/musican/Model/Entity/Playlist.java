package com.musical.musican.Model.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "playlist")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Playlist {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY) // ✅ nên dùng LAZY tránh load toàn bộ Account
    @JoinColumn(name = "account_id", nullable = false)
    @ToString.Exclude
    private Account account;

    @Column(nullable = false, length = 150)
    private String title;

    @Lob
    private String description;

    @Column(name = "is_public")
    private Boolean isPublic = true;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToMany(mappedBy = "playlist", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<PlaylistTrack> playlistTracks;

    @OneToMany(mappedBy = "playlist")
    @ToString.Exclude
    private List<Favourite> favourites;
}
