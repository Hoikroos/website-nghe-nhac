package com.musical.musican.Controller.MUSICAN;

import com.musical.musican.Model.Entity.Account;
import com.musical.musican.Model.Entity.Album;
import com.musical.musican.Model.Entity.Artist;
import com.musical.musican.Service.AlbumService;
import com.musical.musican.Service.ArtistService;
import com.musical.musican.Service.FileUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/musican/albums")
public class AlbumController {

    @Autowired
    private AlbumService albumService;

    @Autowired
    private ArtistService artistService;

    @Autowired
    private FileUploadService fileUploadService;

    @GetMapping
    public String index(
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "artistId", required = false) Integer artistId,
            Model model,
            RedirectAttributes redirectAttributes) {
        try {
            Account currentAccount = albumService.getCurrentAccount();
            List<Album> albums = albumService.searchAlbums(title, artistId).stream()
                    .filter(a -> a.getAccount() != null && a.getAccount().getId().equals(currentAccount.getId()))
                    .toList();

            List<Artist> artists = artistService.findAll();

            model.addAttribute("albums", albums);
            model.addAttribute("artists", artists);
            model.addAttribute("searchTitle", title);
            model.addAttribute("searchArtistId", artistId);

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
            return "redirect:/musican/dashboard";
        }

        return "Musican/albums";
    }

    @PostMapping("/add")
    public String addAlbum(
            @RequestParam("title") String title,
            @RequestParam("artistId") Integer artistId,
            @RequestParam(value = "releaseDate", required = false) String releaseDate,
            @RequestParam("coverFile") MultipartFile coverFile,
            RedirectAttributes redirectAttributes) {
        try {
            Artist artist = artistService.findById(artistId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy nghệ sĩ!"));

            String coverUrl = fileUploadService.uploadFile(coverFile, "albums");
            Album album = Album.builder()
                    .title(title)
                    .artist(artist)
                    .account(albumService.getCurrentAccount())
                    .coverUrl(coverUrl)
                    .releaseDate((releaseDate != null && !releaseDate.isEmpty()) ? LocalDate.parse(releaseDate) : null)
                    .build();

            albumService.save(album);
            redirectAttributes.addFlashAttribute("message", "Thêm album thành công!");
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi upload ảnh: " + e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
        }
        return "redirect:/musican/albums";
    }

    @GetMapping("/edit/{id}")
    public String editAlbumForm(@PathVariable Integer id, Model model) {
        Album album = albumService.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy album!"));
        if (album.getArtist() == null) {
            album.setArtist(new Artist());
        }

        List<Artist> artists = artistService.findAll();
        model.addAttribute("album", album);
        model.addAttribute("artists", artists);
        model.addAttribute("albums", albumService.findAll());
        model.addAttribute("isEdit", true);
        model.addAttribute("showModal", true);

        return "Musican/albums";
    }

    @PostMapping("/update/{id}")
    public String updateAlbum(
            @PathVariable("id") Integer id,
            @RequestParam("title") String title,
            @RequestParam("artistId") Integer artistId,
            @RequestParam(value = "releaseDate", required = false) String releaseDate,
            @RequestParam(value = "coverFile", required = false) MultipartFile coverFile,
            RedirectAttributes redirectAttributes) {
        try {
            Album album = albumService.findById(id)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy album!"));
            Artist artist = artistService.findById(artistId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy nghệ sĩ!"));

            album.setTitle(title);
            album.setArtist(artist);
            album.setReleaseDate((releaseDate != null && !releaseDate.isEmpty()) ? LocalDate.parse(releaseDate) : null);

            if (coverFile != null && !coverFile.isEmpty()) {
                if (album.getCoverUrl() != null) {
                    fileUploadService.deleteFile(album.getCoverUrl());
                }
                String newCoverUrl = fileUploadService.uploadFile(coverFile, "albums");
                album.setCoverUrl(newCoverUrl);
            }

            albumService.save(album);
            redirectAttributes.addFlashAttribute("message", "Cập nhật album thành công!");
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi upload ảnh: " + e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
        }
        return "redirect:/musican/albums";
    }

    @PostMapping("/delete/{id}")
    public String deleteAlbum(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        try {
            Album album = albumService.findById(id)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy album!"));

            if (album.getCoverUrl() != null) {
                fileUploadService.deleteFile(album.getCoverUrl());
            }
            albumService.delete(id);
            redirectAttributes.addFlashAttribute("message", "Xóa album thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
        }
        return "redirect:/musican/albums";
    }
}
