package controller;

import model.User;
import model.Stadium;
import model.Booking;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;

import dao.UserDao;
import dao.StadiumDao;
import dao.BookingDao;
import service.UserService;
import service.BookingService;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Map;
import java.util.HashMap;

@Controller
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private UserDao userDao;
    @Autowired
    private StadiumDao stadiumDao;
    @Autowired
    private BookingDao bookingDao;
    @Autowired
    private BookingService bookingService;

    @GetMapping("/")
    public String root() {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String showLoginForm(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("pageTitle", "Đăng nhập");
        return "login";
    }

    @PostMapping("/login")
    public String login(@ModelAttribute("user") User user,
                        HttpSession session,
                        Model model) {
        if (userService.login(user.getUsername(), user.getPassword())) {
            session.setAttribute("username", user.getUsername());
            User loggedInUser = userDao.findByUsername(user.getUsername());
            if (loggedInUser == null) {
                model.addAttribute("error", "Không tìm thấy người dùng trong cơ sở dữ liệu");
                model.addAttribute("pageTitle", "Đăng nhập");
                return "login";
            }
            session.setAttribute("role", loggedInUser.getRole().toString());
            if (loggedInUser.getRole() == User.Role.ADMIN) {
                return "redirect:/admin";
            }
            return "redirect:/stadiums";
        } else {
            model.addAttribute("error", "Tên đăng nhập hoặc mật khẩu không đúng");
            model.addAttribute("pageTitle", "Đăng nhập");
            return "login";
        }
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("pageTitle", "Đăng ký");
        return "register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute("user") User user,
                           Model model,
                           RedirectAttributes ra) {
        UserService.RegisterResult result = userService.register(user);
        if (result == UserService.RegisterResult.SUCCESS) {
            ra.addFlashAttribute("success", "Đăng ký tài khoản thành công! Vui lòng đăng nhập.");
            return "redirect:/login";
        } else if (result == UserService.RegisterResult.USER_EXISTS) {
            model.addAttribute("error", "Tên đăng nhập đã tồn tại");
        } else if (result == UserService.RegisterResult.EMAIL_EXISTS) {
            model.addAttribute("error", "Email đã được sử dụng");
        } else {
            model.addAttribute("error", "Lỗi không xác định");
        }
        model.addAttribute("pageTitle", "Đăng ký");
        return "register";
    }

    @GetMapping("/home")
    public String home(HttpSession session) {
        String username = (String) session.getAttribute("username");
        if (username == null) return "redirect:/login";
        return "redirect:/stadiums";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }

    @GetMapping("/stadiums")
    public String viewStadiums(HttpSession session, Model model) {
        String username = (String) session.getAttribute("username");
        if (username == null) return "redirect:/login";

        List<Stadium> stadiums = stadiumDao.findAll();
        List<String> uniqueAreas = stadiums.stream()
                .map(Stadium::getArea)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
        List<String> uniqueFieldType = stadiums.stream()
                .map(s -> s.getFieldType().toString())
                .distinct()
                .sorted()
                .collect(Collectors.toList());

        model.addAttribute("stadiums", stadiums);
        model.addAttribute("uniqueAreas", uniqueAreas);
        model.addAttribute("uniqueFieldType", uniqueFieldType);
        model.addAttribute("username", username);
        model.addAttribute("pageTitle", "Đặt Sân Bóng");
        model.addAttribute("contentPage", "stadiums");
        return "layouts/user_layout";
    }

    @GetMapping("/stadiums/{id}/book")
    public String showBookingForm(@PathVariable Long id,
                                  HttpSession session,
                                  Model model,
                                  RedirectAttributes ra) {
        String username = (String) session.getAttribute("username");
        if (username == null) return "redirect:/login";

        Stadium stadium = stadiumDao.findById(id);
        if (stadium == null) {
            ra.addFlashAttribute("error", "Không tìm thấy sân!");
            return "redirect:/stadiums";
        }

        model.addAttribute("stadium", stadium);
        model.addAttribute("booking", new Booking()); // giữ lại để không làm vỡ JSP cũ nếu đang dùng form tags
        model.addAttribute("username", username);
        model.addAttribute("pageTitle", "Đặt Sân - " + stadium.getName());
        model.addAttribute("contentPage", "booking-form");
        return "layouts/user_layout";
    }

    @PostMapping("/stadiums/{id}/book")
    public String bookStadium(@PathVariable Long id,
                              @RequestParam(value = "bookingDate", required = false) String bookingDateRaw,
                              @RequestParam(value = "startTime", required = false) String startTimeRaw,
                              @RequestParam(value = "endTime", required = false) String endTimeRaw,
                              HttpSession session,
                              RedirectAttributes ra) {
        String username = (String) session.getAttribute("username");
        if (username == null) return "redirect:/login";

        User user = userDao.findByUsername(username);
        Stadium stadium = stadiumDao.findById(id);

        if (user == null || stadium == null) {
            ra.addFlashAttribute("error", "Không tìm thấy người dùng hoặc sân!");
            return "redirect:/stadiums";
        }

        if (isBlank(bookingDateRaw) || isBlank(startTimeRaw) || isBlank(endTimeRaw)) {
            ra.addFlashAttribute("error", "Vui lòng chọn đầy đủ ngày đặt, giờ bắt đầu và giờ kết thúc.");
            return "redirect:/stadiums/" + id + "/book";
        }

        try {
            LocalDate bookingDate = LocalDate.parse(bookingDateRaw.trim());
            LocalTime startTime = LocalTime.parse(startTimeRaw.trim());
            LocalTime endTime = LocalTime.parse(endTimeRaw.trim());

            if (!endTime.isAfter(startTime)) {
                ra.addFlashAttribute("error", "Giờ kết thúc phải lớn hơn giờ bắt đầu.");
                return "redirect:/stadiums/" + id + "/book";
            }

            Booking booking = new Booking();
            booking.setUserId(user.getId());
            booking.setStadiumId(id);
            booking.setBookingDate(bookingDate);
            booking.setStartTime(startTime);
            booking.setEndTime(endTime);
            booking.setStatus(Booking.Status.PENDING);

            bookingService.createBooking(booking);
            ra.addFlashAttribute("success", "Đặt sân thành công!");
            return "redirect:/stadiums";

        } catch (DateTimeParseException e) {
            ra.addFlashAttribute("error", "Dữ liệu ngày hoặc giờ không đúng định dạng.");
            return "redirect:/stadiums/" + id + "/book";

        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("error", e.getMessage());
            return "redirect:/stadiums/" + id + "/book";

        } catch (Exception e) {
            e.printStackTrace();
            ra.addFlashAttribute("error", "Lỗi khi đặt sân: " + e.getMessage());
            return "redirect:/stadiums/" + id + "/book";
        }
    }

    @GetMapping("/my-bookings")
    public String viewMyBookings(HttpSession session, Model model) {
        String username = (String) session.getAttribute("username");
        if (username == null) return "redirect:/login";

        User user = userDao.findByUsername(username);
        List<Booking> bookings = bookingDao.findByUserId(user.getId());
        List<Stadium> stadiums = stadiumDao.findAll();
        Map<Long, String> stadiumNames = new HashMap<>();
        Map<Long, Double> stadiumPrices = new HashMap<>();
        for (Stadium s : stadiums) {
            stadiumNames.put(s.getId(), s.getName());
            stadiumPrices.put(s.getId(), s.getPricePerHour());
        }
        model.addAttribute("bookings", bookings);
        model.addAttribute("stadiumNames", stadiumNames);
        model.addAttribute("stadiumPrices", stadiumPrices);
        model.addAttribute("username", username);
        model.addAttribute("pageTitle", "Lịch Sử Đặt Sân");
        model.addAttribute("contentPage", "my-bookings");
        return "layouts/user_layout";
    }

    @PostMapping("/bookings/{id}/cancel")
    public String cancelBooking(@PathVariable Long id,
                                HttpSession session,
                                RedirectAttributes ra) {
        String username = (String) session.getAttribute("username");
        if (username == null) return "redirect:/login";

        User user = userDao.findByUsername(username);
        try {
            Booking booking = bookingDao.findById(id);
            if (booking != null && booking.getUserId().equals(user.getId())) {
                java.time.LocalDateTime now = java.time.LocalDateTime.now();
                java.time.LocalDateTime start = java.time.LocalDateTime.of(booking.getBookingDate(), booking.getStartTime());
                long minutes = java.time.Duration.between(now, start).toMinutes();
                if (minutes <= 30) {
                    ra.addFlashAttribute("error", "Không thể hủy đơn, vui lòng hủy trước ít nhất 30 phút trước giờ bắt đầu!");
                } else {
                    bookingDao.updateStatus(id, Booking.Status.CANCELLED);
                    ra.addFlashAttribute("success", "Hủy đặt sân thành công!");
                }
            } else {
                ra.addFlashAttribute("error", "Bạn không có quyền hủy đặt sân này!");
            }
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Lỗi khi hủy đặt sân: " + e.getMessage());
        }
        return "redirect:/my-bookings";
    }

    @GetMapping("/api/bookings/check-availability")
    @ResponseBody
    public ResponseEntity<?> checkAvailability(@RequestParam Long stadiumId,
                                               @RequestParam(value = "date", required = false) String dateRaw,
                                               @RequestParam(value = "bookingDate", required = false) String bookingDateRaw) {
        try {
            String effectiveDate = !isBlank(dateRaw) ? dateRaw : bookingDateRaw;
            if (isBlank(effectiveDate)) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Thiếu tham số ngày.");
                return ResponseEntity.badRequest().body(error);
            }

            LocalDate bookingDate = LocalDate.parse(effectiveDate.trim());
            List<Booking> bookings = bookingService.getBookingsByStadiumAndDate(stadiumId, bookingDate);

            return ResponseEntity.ok(
                    bookings.stream()
                            .filter(b -> b.getStatus() != Booking.Status.CANCELLED)
                            .map(b -> {
                                Map<String, String> slot = new HashMap<>();
                                slot.put("startTime", b.getStartTime().toString());
                                slot.put("endTime", b.getEndTime().toString());
                                return slot;
                            })
                            .collect(Collectors.toList())
            );
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}