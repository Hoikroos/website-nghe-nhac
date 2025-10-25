package com.musical.musican.Model.DTO;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActivityDto {
    private String title;
    private String icon;
    private String timeAgo;
    private String type;
    private LocalDateTime createdAt = LocalDateTime.now();
}