package com.musical.musican.Model.Entity;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PlaylistTrackId implements Serializable {
    private Integer playlistId;
    private Integer trackId;
}
