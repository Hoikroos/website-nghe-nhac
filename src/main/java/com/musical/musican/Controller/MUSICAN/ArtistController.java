package com.musical.musican.Controller.MUSICAN;

import com.musical.musican.Model.Entity.Artist;
import com.musical.musican.Model.Entity.Account;
import com.musical.musican.Repository.AccountRepository;
import com.musical.musican.Security.CustomUserDetails;
import com.musical.musican.Service.ArtistService;
import com.musical.musican.Service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/musican/artists")
public class ArtistController {

    @Autowired
    private ArtistService artistService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private AccountRepository accountRepository;

    private Account getCurrentAccount() {
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

    @GetMapping
    public String listArtists(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "categoryId", required = false) Long categoryId,
            Model model,
            RedirectAttributes redirectAttributes) {
        try {
            Account currentAccount = getCurrentAccount();

            List<Artist> allArtists = artistService.search(keyword, categoryId);

            List<Artist> artists = allArtists.stream()
                    .filter(a -> a.getAccount() != null && a.getAccount().getId().equals(currentAccount.getId()))
                    .collect(Collectors.toList());

            model.addAttribute("artists", artists);
            model.addAttribute("keyword", keyword);
            model.addAttribute("categoryId", categoryId);
            model.addAttribute("categories", categoryService.findAll());
            model.addAttribute("artist", new Artist());
            model.addAttribute("showModal", false);

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
            return "redirect:/musican/dashboard";
        }

        return "Musican/artists";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("artist", new Artist());
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("isEdit", false);
        model.addAttribute("showModal", true);
        return "Musican/artists";
    }

    @PostMapping("/create")
    public String createArtist(@ModelAttribute("artist") Artist artist,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            RedirectAttributes redirectAttributes) {
        try {
            Account account = getCurrentAccount();
            artist.setAccount(account);
            artistService.save(artist);
            redirectAttributes.addFlashAttribute("message", "Thêm nghệ sĩ thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi thêm nghệ sĩ: " + e.getMessage());
        }
        return "redirect:/musican/artists";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Integer id, Model model) {
        Artist artist = artistService.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nghệ sĩ"));
        model.addAttribute("artist", artist);
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("isEdit", true);
        model.addAttribute("showModal", true);
        return "Musican/artists";
    }

    @PostMapping("/update/{id}")
    public String updateArtist(@PathVariable("id") Integer id,
            @ModelAttribute("artist") Artist artist,
            RedirectAttributes redirectAttributes) {
        try {
            artistService.update(id, artist);
            redirectAttributes.addFlashAttribute("message", "Cập nhật nghệ sĩ thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi cập nhật nghệ sĩ: " + e.getMessage());
        }
        return "redirect:/musican/artists";
    }

    @PostMapping("/delete/{id}")
    public String deleteArtist(@PathVariable("id") Integer id,
            RedirectAttributes redirectAttributes) {
        try {
            artistService.delete(id);
            redirectAttributes.addFlashAttribute("message", "Xóa nghệ sĩ thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi xóa nghệ sĩ: " + e.getMessage());
        }
        return "redirect:/musican/artists";
    }
}
