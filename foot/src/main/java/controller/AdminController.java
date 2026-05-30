package controller;

import model.Booking;
import service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.multipart.MultipartFile;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpServletRequest;

import java.io.File;
import java.io.IOException;
import java.util.List;

import model.Stadium;
import dao.StadiumDao;
import dao.BookingDao;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired private BookingService bookingService;
    @Autowired private StadiumDao stadiumDao;
    @Autowired private dao.BookingDao bookingDao;

    // Kiểm tra có phải ADMIN không
    private boolean isAdmin(HttpSession session) {
        String role = (String) session.getAttribute("role");
        return "ADMIN".equals(role);
    }

    // ===== DASHBOARD =====
    @GetMapping
    public String adminDashboard(HttpSession session, Model model) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }

        // Lấy số booking đang chờ
        List<Object[]> pendingBookings = bookingService.getPendingBookingsWithDetails();

        model.addAttribute("username", session.getAttribute("username"));
        model.addAttribute("pageTitle", "Admin Dashboard");
        model.addAttribute("contentPage", "admin/dashboard");
        model.addAttribute("pendingBookingsCount", pendingBookings.size());

        return "layouts/admin_layout";
    }

    // ===== BOOKING MANAGEMENT =====
    @GetMapping("/bookings")
    public String bookingList(@RequestParam(required = false) String status,
                              HttpSession session,
                              Model model) {
        if (!isAdmin(session)) return "redirect:/login";

        List<Object[]> bookings;
        if (status != null && !status.isEmpty()) {
            try {
                Booking.Status bs = Booking.Status.valueOf(status.toUpperCase());
                bookings = bookingService.getBookingsWithDetailsByStatus(bs);
            } catch (IllegalArgumentException e) {
                bookings = bookingService.getBookingsWithDetails();
            }
        } else {
            bookings = bookingService.getBookingsWithDetails();
        }

        model.addAttribute("username", session.getAttribute("username"));
        model.addAttribute("pageTitle", "Booking Management");
        model.addAttribute("contentPage", "admin/bookings");
        model.addAttribute("bookings", bookings);
        model.addAttribute("selectedStatus", status);
        return "layouts/admin_layout";
    }

    @PostMapping("/bookings/approve/{id}")
    public String approveBooking(@PathVariable Long id,
                                 HttpSession session,
                                 RedirectAttributes ra) {
        if (!isAdmin(session)) return "redirect:/login";
        try {
            bookingService.updateBookingStatus(id, Booking.Status.APPROVED);
            ra.addFlashAttribute("successMessage", "Phê duyệt đặt sân thành công!");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", "Lỗi khi phê duyệt đặt sân: " + e.getMessage());
        }
        return "redirect:/admin/bookings";
    }

    @PostMapping("/bookings/reject/{id}")
    public String rejectBooking(@PathVariable Long id,
                                HttpSession session,
                                RedirectAttributes ra) {
        if (!isAdmin(session)) return "redirect:/login";
        try {
            bookingService.updateBookingStatus(id, Booking.Status.REJECTED);
            ra.addFlashAttribute("successMessage", "Từ chối đặt sân thành công!");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", "Lỗi khi từ chối đặt sân: " + e.getMessage());
        }
        return "redirect:/admin/bookings";
    }

    // ===== STADIUM MANAGEMENT =====
    @GetMapping("/stadiums")
    public String stadiumList(HttpSession session, Model model) {
        if (!isAdmin(session)) return "redirect:/login";

        List<Stadium> stadiums = stadiumDao.findAll();
        model.addAttribute("stadiums", stadiums);
        model.addAttribute("pageTitle", "Stadium Management");
        model.addAttribute("contentPage", "admin/stadiums");
        return "layouts/admin_layout";
    }

    @GetMapping("/stadiums/add")
    public String addStadiumForm(HttpSession session, Model model) {
        if (!isAdmin(session)) return "redirect:/login";

        model.addAttribute("stadium", new Stadium());
        model.addAttribute("pageTitle", "Add New Stadium");
        model.addAttribute("contentPage", "admin/stadium-form");
        model.addAttribute("fieldTypes", Stadium.FieldType.values());
        model.addAttribute("isEdit", false);
        return "layouts/admin_layout";
    }

    @GetMapping("/stadiums/edit/{id}")
    public String editStadiumForm(@PathVariable Long id, HttpSession session, Model model) {
        if (!isAdmin(session)) return "redirect:/login";

        Stadium stadium = stadiumDao.findById(id);
        if (stadium == null) {
            return "redirect:/admin/stadiums";
        }
        model.addAttribute("stadium", stadium);
        model.addAttribute("pageTitle", "Edit Stadium");
        model.addAttribute("contentPage", "admin/stadium-form");
        model.addAttribute("fieldTypes", Stadium.FieldType.values());
        model.addAttribute("isEdit", true);
        return "layouts/admin_layout";
    }

    @PostMapping("/stadiums/save")
    public String saveStadium(@ModelAttribute Stadium stadium,
                              @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
                              @RequestParam(value = "existingImageUrl", required = false) String existingImageUrl,
                              HttpSession session,
                              RedirectAttributes ra,
                              HttpServletRequest request) {
        if (!isAdmin(session)) return "redirect:/login";
        try {
            if (imageFile != null && !imageFile.isEmpty()) {
                String url = saveUploadedStadiumImage(imageFile, request);
                stadium.setImageUrl(url);
            } else if ((stadium.getImageUrl() == null || stadium.getImageUrl().isBlank())
                    && existingImageUrl != null && !existingImageUrl.isBlank()) {
                stadium.setImageUrl(existingImageUrl);
            }
            stadiumDao.save(stadium);
            ra.addFlashAttribute("successMessage", "Thêm sân bóng thành công!");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", "Lỗi khi lưu sân bóng: " + e.getMessage());
        }
        return "redirect:/admin/stadiums";
    }

    private String saveUploadedStadiumImage(MultipartFile imageFile, HttpServletRequest req) throws IOException {
        String webappPath = req.getServletContext().getRealPath("/");
        String uploadDir  = webappPath + "images" + File.separator + "stadiums" + File.separator;
        File dir = new File(uploadDir);
        if (!dir.exists()) dir.mkdirs();
        String orig = imageFile.getOriginalFilename();
        String ext  = orig != null && orig.contains(".") ? orig.substring(orig.lastIndexOf(".")) : "";
        String filename = "stadium_" + System.currentTimeMillis() + ext;
        File dest = new File(dir, filename);
        imageFile.transferTo(dest);
        return "/images/stadiums/" + filename;
    }

    @PostMapping("/stadiums/delete/{id}")
    public String deleteStadium(@PathVariable Long id,
                                HttpSession session,
                                RedirectAttributes ra) {
        if (!isAdmin(session)) return "redirect:/login";
        try {
            if (bookingDao.hasActiveBookingForStadium(id)) {
                ra.addFlashAttribute("errorMessage", "Không thể xóa sân bóng vì còn đơn đặt chưa hoàn thành!");
            } else {
                stadiumDao.deleteById(id);
                ra.addFlashAttribute("successMessage", "Xóa sân bóng thành công!");
            }
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", "Lỗi khi xóa sân bóng: " + e.getMessage());
        }
        return "redirect:/admin/stadiums";
    }
}
