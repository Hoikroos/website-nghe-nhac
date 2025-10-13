package com.musical.musican.Service.Impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.musical.musican.Model.Entity.Track;
import com.musical.musican.Repository.TrackRepository;
import com.musical.musican.Service.TrackService;
@Service
public class TrackServiceImpl implements TrackService {
    @Autowired
    private TrackRepository trackRepository;

    @Override
    public List<Track> getAllTracks() {
        return trackRepository.findAll();
    }
}
