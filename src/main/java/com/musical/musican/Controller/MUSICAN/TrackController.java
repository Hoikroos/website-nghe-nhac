package com.musical.musican.Controller.MUSICAN;

import com.musical.musican.Model.Entity.Track;
import com.musical.musican.Service.AlbumService;
import com.musical.musican.Service.TrackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/musican/tracks")
public class TrackController {

    @Autowired
    private TrackService trackService;

    @Autowired
    private AlbumService albumService;

    @GetMapping
    public String index(Model model,
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "albumId", required = false) Integer albumId,
            @RequestParam(value = "sourceType", required = false) String sourceType) {

        model.addAttribute("tracks", trackService.searchTracks(title, albumId, sourceType));
        model.addAttribute("albums", albumService.findAll());
        model.addAttribute("title", title);
        model.addAttribute("albumId", albumId);
        model.addAttribute("sourceType", sourceType);
        return "Musican/tracks";
    }

    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("track", new Track());
        model.addAttribute("albums", albumService.findAll());
        return "Musican/tracks";
    }

    @PostMapping("/create")
    public String createTrack(
            @ModelAttribute Track track,
            @RequestParam(value = "fileUpload", required = false) MultipartFile fileUpload,
            @RequestParam(value = "externalLink", required = false) String externalLink,
            RedirectAttributes redirectAttributes) {
        try {
            if (fileUpload != null && !fileUpload.isEmpty()) {
                String filePath = trackService.saveFile(fileUpload);
                track.setAudioUrl(filePath);
                track.setSourceType(Track.SourceType.UPLOAD);
            } else if (externalLink != null && !externalLink.trim().isEmpty()) {
                track.setAudioUrl(externalLink.trim());
                track.setSourceType(Track.SourceType.EXTERNAL);
            }

            trackService.save(track);
            redirectAttributes.addFlashAttribute("message", "Thêm bài hát thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi thêm bài hát: " + e.getMessage());
        }
        return "redirect:/musican/tracks";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable("id") Integer id, Model model, RedirectAttributes redirectAttributes) {
        Track track = trackService.getTrackById(id);
        if (track == null) {
            redirectAttributes.addFlashAttribute("errorMessage", " Không tìm thấy bài hát.");
            return "redirect:/musican/tracks";
        }
        model.addAttribute("track", track);
        model.addAttribute("albums", albumService.findAll());
        return "Musican/track-form";
    }

    @PostMapping("/edit/{id}")
    public String updateTrack(
            @PathVariable("id") Integer id,
            @ModelAttribute Track trackData,
            @RequestParam(value = "fileUpload", required = false) MultipartFile fileUpload,
            @RequestParam(value = "externalLink", required = false) String externalLink,
            RedirectAttributes redirectAttributes) {
        try {
            trackService.update(id, trackData, fileUpload, externalLink);
            redirectAttributes.addFlashAttribute("message", "Cập nhật bài hát thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi cập nhật: " + e.getMessage());
        }
        return "redirect:/musican/tracks";
    }

    @PostMapping("/delete/{id}")
    public String deleteTrack(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        try {
            trackService.deleteTrackById(id);
            redirectAttributes.addFlashAttribute("message", "Xóa bài hát thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi xóa bài hát: " + e.getMessage());
        }
        return "redirect:/musican/tracks";
    }
}
