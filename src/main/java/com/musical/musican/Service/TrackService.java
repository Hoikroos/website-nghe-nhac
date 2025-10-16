package com.musical.musican.Service;

import com.musical.musican.Model.Entity.Track;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

public interface TrackService {
    List<Track> getAllTracks();

    List<Track> searchTracks(String title, Integer albumId, String sourceType);

    Track getTrackById(Integer id);

    List<Track> getTracksByAlbumId(Integer albumId);

    List<Track> getTracksByTitle(String keyword);

    void deleteTrackById(Integer id);

    Track save(Track track);

    Track update(Integer id, Track trackData, MultipartFile fileUpload, String externalLink);

    String saveFile(MultipartFile file);
}
