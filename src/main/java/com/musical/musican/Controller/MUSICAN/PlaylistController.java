package com.musical.musican.Controller.MUSICAN;

import com.musical.musican.Model.Entity.Account;
import com.musical.musican.Model.Entity.Playlist;
import com.musical.musican.Repository.AccountRepository;
import com.musical.musican.Service.PlayListService;
import com.musical.musican.Service.TrackService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/musican/playlists")
public class PlaylistController {

    @Autowired
    private PlayListService playlistService;

    @Autowired
    private TrackService trackService;

    @Autowired
    private AccountRepository accountRepository;

    private Account getCurrentAccount() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() &&
                authentication.getPrincipal() instanceof org.springframework.security.core.userdetails.User) {
            org.springframework.security.core.userdetails.User user = (org.springframework.security.core.userdetails.User) authentication
                    .getPrincipal();
            Account account = accountRepository.findByUsername(user.getUsername())
                    .orElseThrow(() -> new IllegalStateException("Tài khoản không tồn tại"));
            if (!account.getRole().name().equals("MUSICIAN")) {
                throw new IllegalStateException("Chỉ tài khoản MUSICIAN mới có quyền thực hiện hành động này!");
            }
            return account;
        }
        throw new IllegalStateException("Bạn cần đăng nhập để thực hiện hành động này!");
    }

    @GetMapping
    public String listPlaylists(@RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "privacy", required = false) Boolean isPublic,
            Model model) {
        try {
            Account currentAccount = getCurrentAccount();
            List<Playlist> playlists = playlistService.searchPlaylists(title, isPublic, currentAccount);
            model.addAttribute("playlists", playlists);
            model.addAttribute("searchTitle", title);
            model.addAttribute("searchPrivacy", isPublic);
            model.addAttribute("showModal", false);
            model.addAttribute("showTracksModal", false);
            model.addAttribute("playlist", new Playlist());
            model.addAttribute("tracks", trackService.getAllTracks());
            model.addAttribute("currentUser",
                    currentAccount.getFullname() != null ? currentAccount.getFullname() : currentAccount.getEmail());
        } catch (IllegalStateException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "redirect:/Dangnhap";
        }
        return "Musican/playlist";
    }

    @GetMapping("/add")
    public String showAddPlaylistForm(Model model) {
        Playlist playlist = Playlist.builder()
                .createdAt(java.time.LocalDateTime.now())
                .build();
        model.addAttribute("playlist", playlist);
        model.addAttribute("isEdit", false);
        model.addAttribute("showModal", true);
        model.addAttribute("showTracksModal", false);
        model.addAttribute("playlists", playlistService.getAllPlaylists());
        model.addAttribute("tracks", trackService.getAllTracks());
        return "Musican/playlist";
    }

    @PostMapping("/add")
    public String addPlaylist(@Valid @ModelAttribute("playlist") Playlist playlist,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("isEdit", false);
            model.addAttribute("showModal", true);
            model.addAttribute("showTracksModal", false);
            model.addAttribute("playlists", playlistService.getAllPlaylists());
            model.addAttribute("tracks", trackService.getAllTracks());
            return "Musican/playlist";
        }
        try {
            Account currentAccount = getCurrentAccount();
            playlist.setAccount(currentAccount);
            playlist.setCreatedAt(java.time.LocalDateTime.now());
            playlistService.addPlaylist(playlist);
            redirectAttributes.addFlashAttribute("message", "Thêm playlist thành công!");
        } catch (IllegalArgumentException | IllegalStateException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("isEdit", false);
            model.addAttribute("showModal", true);
            model.addAttribute("showTracksModal", false);
            model.addAttribute("playlists", playlistService.getAllPlaylists());
            model.addAttribute("tracks", trackService.getAllTracks());
            return "Musican/playlist";
        }
        return "redirect:/musican/playlists";
    }

    @GetMapping("/edit/{id}")
    public String showEditPlaylistForm(@PathVariable("id") Integer id, Model model) {
        try {
            Account currentAccount = getCurrentAccount();
            Optional<Playlist> playlistOpt = playlistService.getPlaylistById(id);
            if (!playlistOpt.isPresent() || !playlistOpt.get().getAccount().getId().equals(currentAccount.getId())) {
                model.addAttribute("errorMessage", "Playlist không tồn tại hoặc bạn không có quyền chỉnh sửa!");
                model.addAttribute("playlists", playlistService.getAllPlaylists());
                model.addAttribute("showModal", false);
                model.addAttribute("showTracksModal", false);
                model.addAttribute("playlist", new Playlist());
                model.addAttribute("tracks", trackService.getAllTracks());
                return "Musican/playlist";
            }
            Playlist playlist = playlistOpt.get();
            model.addAttribute("playlist", playlist);
            model.addAttribute("isEdit", true);
            model.addAttribute("showModal", true);
            model.addAttribute("showTracksModal", false);
            model.addAttribute("playlists", playlistService.getAllPlaylists());
            model.addAttribute("tracks", trackService.getAllTracks());
        } catch (IllegalStateException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "redirect:/Dangnhap";
        }
        return "Musican/playlist";
    }

    @PostMapping("/edit/{id}")
    public String updatePlaylist(@PathVariable("id") Integer id,
            @Valid @ModelAttribute("playlist") Playlist playlist,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("isEdit", true);
            model.addAttribute("showModal", true);
            model.addAttribute("showTracksModal", false);
            model.addAttribute("playlists", playlistService.getAllPlaylists());
            model.addAttribute("tracks", trackService.getAllTracks());
            return "Musican/playlist";
        }
        try {
            Account currentAccount = getCurrentAccount();
            Optional<Playlist> existingPlaylist = playlistService.getPlaylistById(id);
            if (!existingPlaylist.isPresent()
                    || !existingPlaylist.get().getAccount().getId().equals(currentAccount.getId())) {
                model.addAttribute("errorMessage", "Playlist không tồn tại hoặc bạn không có quyền chỉnh sửa!");
                model.addAttribute("isEdit", true);
                model.addAttribute("showModal", true);
                model.addAttribute("showTracksModal", false);
                model.addAttribute("playlists", playlistService.getAllPlaylists());
                model.addAttribute("tracks", trackService.getAllTracks());
                return "Musican/playlist";
            }
            playlist.setId(id);
            playlist.setAccount(existingPlaylist.get().getAccount());
            playlist.setCreatedAt(existingPlaylist.get().getCreatedAt());
            playlistService.updatePlaylist(id, playlist);
            redirectAttributes.addFlashAttribute("message", "Cập nhật playlist thành công!");
        } catch (IllegalArgumentException | IllegalStateException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("isEdit", true);
            model.addAttribute("showModal", true);
            model.addAttribute("showTracksModal", false);
            model.addAttribute("playlists", playlistService.getAllPlaylists());
            model.addAttribute("tracks", trackService.getAllTracks());
            return "Musican/playlist";
        }
        return "redirect:/musican/playlists";
    }

    @GetMapping("/tracks/{id}")
    public String showTracksForm(@PathVariable("id") Integer id, Model model) {
        try {
            Account currentAccount = getCurrentAccount();
            Optional<Playlist> playlistOpt = playlistService.getPlaylistById(id);
            if (!playlistOpt.isPresent() || !playlistOpt.get().getAccount().getId().equals(currentAccount.getId())) {
                model.addAttribute("errorMessage", "Playlist không tồn tại hoặc bạn không có quyền chỉnh sửa!");
                model.addAttribute("playlists", playlistService.getAllPlaylists());
                model.addAttribute("showModal", false);
                model.addAttribute("showTracksModal", false);
                model.addAttribute("playlist", new Playlist());
                model.addAttribute("tracks", trackService.getAllTracks());
                return "Musican/playlist";
            }
            Playlist playlist = playlistOpt.get();
            model.addAttribute("playlist", playlist);
            model.addAttribute("isEdit", false);
            model.addAttribute("showModal", false);
            model.addAttribute("showTracksModal", true);
            model.addAttribute("playlists", playlistService.getAllPlaylists());
            model.addAttribute("tracks", trackService.getAllTracks());
        } catch (IllegalStateException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "redirect:/Dangnhap";
        }
        return "Musican/playlist"; // Sửa lỗi: trả về đúng template
    }

    @PostMapping("/tracks/{id}")
    public String updatePlaylistTracks(@PathVariable("id") Integer id,
            @RequestParam(value = "trackIds", required = false) List<Integer> trackIds,
            Model model,
            RedirectAttributes redirectAttributes) {
        try {
            Account currentAccount = getCurrentAccount();
            Optional<Playlist> playlistOpt = playlistService.getPlaylistById(id);
            if (!playlistOpt.isPresent() || !playlistOpt.get().getAccount().getId().equals(currentAccount.getId())) {
                model.addAttribute("errorMessage", "Playlist không tồn tại hoặc bạn không có quyền chỉnh sửa!");
                model.addAttribute("isEdit", false);
                model.addAttribute("showModal", false);
                model.addAttribute("showTracksModal", true);
                model.addAttribute("playlist", playlistOpt.orElse(new Playlist()));
                model.addAttribute("playlists", playlistService.getAllPlaylists());
                model.addAttribute("tracks", trackService.getAllTracks());
                return "Musican/playlist";
            }
            playlistService.updatePlaylistTracks(id, trackIds);
            redirectAttributes.addFlashAttribute("message", "Cập nhật danh sách bài hát thành công!");
        } catch (IllegalArgumentException | IllegalStateException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("isEdit", false);
            model.addAttribute("showModal", false);
            model.addAttribute("showTracksModal", true);
            Optional<Playlist> playlistOpt = playlistService.getPlaylistById(id);
            model.addAttribute("playlist", playlistOpt.orElse(new Playlist()));
            model.addAttribute("playlists", playlistService.getAllPlaylists());
            model.addAttribute("tracks", trackService.getAllTracks());
            return "Musican/playlist";
        }
        return "redirect:/musican/playlists";
    }

    @PostMapping("/delete/{id}")
    public String deletePlaylist(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        try {
            Account currentAccount = getCurrentAccount();
            Optional<Playlist> playlistOpt = playlistService.getPlaylistById(id);
            if (!playlistOpt.isPresent() || !playlistOpt.get().getAccount().getId().equals(currentAccount.getId())) {
                redirectAttributes.addFlashAttribute("errorMessage",
                        "Playlist không tồn tại hoặc bạn không có quyền xóa!");
                return "redirect:/musican/playlists";
            }
            playlistService.deletePlaylist(id);
            redirectAttributes.addFlashAttribute("message", "Xóa playlist thành công!");
        } catch (IllegalArgumentException | IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/musican/playlists";
    }
}