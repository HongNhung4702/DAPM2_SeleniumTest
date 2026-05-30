# Tổng quan dự án: Football Booking System (`foot`)

Tài liệu này nhằm **tóm tắt toàn bộ dự án** để một AI/đồng đội khác chỉ cần đọc file này là hiểu được: công nghệ, kiến trúc, module, luồng nghiệp vụ, API/URL, view JSP, database schema, cách build/run/deploy và các điểm cần lưu ý.

---

## 1) TL;DR

- **Loại ứng dụng**: Web app **Spring MVC** (không phải Spring Boot), đóng gói **WAR**.
- **UI**: JSP + JSTL + Bootstrap/FontAwesome CDN, layout JSP include theo biến `contentPage`.
- **DB**: MySQL, truy cập bằng **Spring JDBC (`JdbcTemplate`)**.
- **Auth**: Đăng nhập thủ công, lưu `username` + `role` trong `HttpSession` (không Spring Security).
- **Chức năng chính**:
  - Người dùng: đăng ký/đăng nhập, xem danh sách sân, đặt sân, xem lịch sử đặt sân, hủy đơn (trước giờ bắt đầu > 30 phút).
  - Admin: dashboard, duyệt/từ chối booking, CRUD sân (xóa mềm), upload ảnh sân.

---

## 2) Công nghệ & dependency chính

Nguồn: `pom.xml`

- **Java**: cấu hình compiler `source/target = 11`
- **Packaging**: `war`
- **Spring Framework**: `6.1.15` (core/context/web/webmvc/jdbc)
- **Servlet/JSP**: Jakarta Servlet `6.1.0`, JSP `3.1.1`, JSTL `2.0.0`
- **MySQL driver**: `mysql-connector-j 8.3.0`
- **Plugin chạy dev**:
  - `jetty-maven-plugin 11.0.15` (Jakarta)
  - `tomcat7-maven-plugin 2.2` (rất cũ; thường không phù hợp Jakarta)

> Gợi ý môi trường runtime: do dùng Jakarta Servlet 6, nếu deploy Tomcat thì thường là **Tomcat 10.1+**.

---

## 3) Cấu trúc thư mục (chỉ phần quan trọng)

```
src/main/java/
  config/
    DatabaseConfig.java
    WebAppInitializer.java
    WebConfig.java
  controller/
    AdminController.java
    UserController.java
  dao/
    BookingDao.java
    StadiumDao.java
    UserDao.java
  model/
    Booking.java
    Stadium.java
    User.java
  service/
    BookingService.java
    UserService.java

src/main/resources/
  database.properties

src/main/webapp/
  css/admin-custom.css
  images/...
  WEB-INF/
    web.xml
    views/
      layouts/
        user_layout.jsp
        admin_layout.jsp
      login.jsp
      register.jsp
      stadiums.jsp
      booking-form.jsp
      my-bookings.jsp
      admin/
        dashboard.jsp
        bookings.jsp
        stadiums.jsp
        stadium-form.jsp
      admin.jsp
```

---

## 4) Kiến trúc & luồng request

### 4.1 Pattern tổng quát

- **Controller**: nhận HTTP request, kiểm tra session, gọi Service/Dao, set `Model` và trả về JSP view name.
- **Service**: chứa logic nghiệp vụ (validate, check trùng giờ, transaction…).
- **DAO**: thao tác SQL bằng `JdbcTemplate` (RowMapper map ResultSet → model POJO).
- **View (JSP)**: hiển thị; các trang “user/admin” dùng layout và include nội dung theo `contentPage`.

### 4.2 Cấu hình Spring MVC & resource tĩnh

Nguồn: `src/main/java/config/WebConfig.java`

- View resolver:
  - Prefix: `/WEB-INF/views/`
  - Suffix: `.jsp`
- Static resources:
  - `/images/**` → `/images/`
  - `/css/**` → `/css/`
  - `/js/**` → `/js/` (dự án hiện chưa thấy thư mục `js/`)
- Multipart upload:
  - `StandardServletMultipartResolver`

### 4.3 Khởi tạo web app (DispatcherServlet)

Nguồn: `src/main/java/config/WebAppInitializer.java`

- `getServletMappings()` → mapping DispatcherServlet: `"/"` (mọi route đi qua Spring MVC)
- `customizeRegistration` cấu hình multipart:
  - max file size 5MB, max request 10MB, threshold 1MB

> Lưu ý kỹ thuật: `WebAppInitializer` đang đưa cả `WebConfig` và `DatabaseConfig` vào `getRootConfigClasses()` và trả `getServletConfigClasses()` rỗng. Thông thường (best practice), `WebConfig` nên nằm trong Servlet application context (`getServletConfigClasses`) để đảm bảo MVC infrastructure (handler mapping, view resolver…) được tạo đúng nơi. Nếu app chạy ổn trong máy bạn thì vẫn OK; nhưng đây là điểm đáng nhớ khi debug lỗi 404/không map controller.

### 4.4 Cấu hình DB

Nguồn: `src/main/resources/database.properties` + `DatabaseConfig.java`

- `db.url`: `jdbc:mysql://localhost:3306/bookingfootball?...UTF-8...`
- `db.username`: `root`
- `db.password`: `12345`
- `DatabaseConfig` tạo:
  - `DriverManagerDataSource`
  - `JdbcTemplate`
  - `DataSourceTransactionManager`

> Các “pool settings” trong `database.properties` hiện **chưa được dùng** (vì đang dùng `DriverManagerDataSource`, không phải pool datasource).

### 4.5 `web.xml`

Nguồn: `src/main/webapp/WEB-INF/web.xml`

- Encoding filter `CharacterEncodingFilter` ép UTF-8.
- Session timeout 30 phút.
- Welcome file cấu hình `index.jsp` (**nhưng dự án hiện không có `src/main/webapp/index.jsp`**).

---

## 5) Module nghiệp vụ & màn hình (JSP)

### 5.1 Layout strategy

- User pages:
  - Controller trả view: `layouts/user_layout`
  - Bên trong layout include: `<jsp:include page="/WEB-INF/views/${contentPage}.jsp"/>`
  - Vì vậy Controller cần set `contentPage` đúng (ví dụ: `"stadiums"`, `"booking-form"`, `"my-bookings"`)

- Admin pages:
  - Controller trả view: `layouts/admin_layout`
  - Include: `/WEB-INF/views/${contentPage}.jsp` với `contentPage` kiểu `"admin/dashboard"`, `"admin/bookings"`, `"admin/stadiums"`, `"admin/stadium-form"`

### 5.2 Danh sách JSP view

- Auth:
  - `login.jsp`
  - `register.jsp`
- User:
  - `stadiums.jsp` (list & filter)
  - `booking-form.jsp` (đặt sân + check slot bằng API)
  - `my-bookings.jsp` (lịch sử + modal hủy)
- Admin:
  - `admin/dashboard.jsp`
  - `admin/bookings.jsp`
  - `admin/stadiums.jsp`
  - `admin/stadium-form.jsp`
  - `admin.jsp` (có vẻ là dashboard cũ/khác, nhưng Controller hiện dùng `admin/dashboard`)

---

## 6) API/URL map (routes)

Giả sử context path là `/foot` (do WAR name và cấu hình plugin), thì URL thực tế sẽ là `/foot/...`.

### 6.1 Auth & session

Controller: `controller.UserController`

- `GET /` → redirect `/login`
- `GET /login` → view `login.jsp`
- `POST /login`
  - `UserService.login(username, password)` (so password plain-text)
  - nếu OK: set session:
    - `username`
    - `role` (string, lấy từ DB)
  - nếu `ADMIN` → redirect `/admin`
  - else → redirect `/stadiums`
- `GET /register` → view `register.jsp`
- `POST /register`
  - `UserService.register(user)`:
    - check trùng username/email
    - insert user
  - thành công: redirect `/login` với flash message `success`
- `GET /logout` → session.invalidate() → redirect `/login`

### 6.2 User flow (đặt sân)

Controller: `controller.UserController` (yêu cầu session `username`)

- `GET /home` → redirect `/stadiums`
- `GET /stadiums`
  - lấy danh sách sân: `StadiumDao.findAll()` (chỉ sân `is_active = TRUE`)
  - tạo danh sách filter:
    - `uniqueAreas`: từ `Stadium.getArea()` (tách từ **address**), không phải cột `stadium.area` trong DB
    - `uniqueFieldType`: từ `stadium.fieldType.toString()`
  - render layout user với `contentPage = "stadiums"`

- `GET /stadiums/{id}/book`
  - load sân theo id: `StadiumDao.findById(id)` (is_active = TRUE)
  - render layout user với `contentPage = "booking-form"`

- `POST /stadiums/{id}/book`
  - parse `bookingDate`, `startTime`, `endTime`
  - set `userId`, `stadiumId`, `status=PENDING`
  - `BookingService.createBooking(booking)`:
    - validate endTime > startTime
    - check trùng giờ: `BookingDao.findOverlappingBookings(...)` (bỏ qua booking CANCELLED)
    - set `createdAt` nếu null
    - insert booking
  - redirect `/stadiums` với flash `success` hoặc `error`

- `GET /my-bookings`
  - lấy user hiện tại, rồi `BookingDao.findByUserId(userId)`
  - load tất cả stadium để map `stadiumNames` và `stadiumPrices`
  - render layout user với `contentPage = "my-bookings"`

- `POST /bookings/{id}/cancel`
  - chỉ cho hủy nếu booking thuộc user hiện tại
  - rule: **không cho hủy nếu còn <= 30 phút** trước giờ bắt đầu
  - nếu OK: `BookingDao.updateStatus(id, CANCELLED)`
  - redirect `/my-bookings`

### 6.3 API hỗ trợ UI (AJAX)

Controller: `controller.UserController`

- `GET /api/bookings/check-availability?stadiumId=...&date=YYYY-MM-DD`
  - trả JSON list các slot đã đặt (startTime/endTime) của sân trong ngày
  - filter bỏ `CANCELLED`
  - `booking-form.jsp` dùng API này để disable time slots

### 6.4 Admin flow

Controller: `controller.AdminController` (prefix `/admin`)

Cơ chế auth admin: `isAdmin(session)` kiểm tra `session.role == "ADMIN"`.

- `GET /admin`
  - dashboard: đếm booking pending (`BookingService.getPendingBookingsWithDetails()`)
  - render layout admin với `contentPage = "admin/dashboard"`

- `GET /admin/bookings?status=PENDING|APPROVED|REJECTED` (optional)
  - list booking kèm chi tiết (join user + stadium)
  - render `contentPage = "admin/bookings"`

- `POST /admin/bookings/approve/{id}` → update status `APPROVED`
- `POST /admin/bookings/reject/{id}` → update status `REJECTED`

- `GET /admin/stadiums` → list stadium (DAO hiện chỉ `findAll()` “active”; admin cũng chỉ thấy active)
- `GET /admin/stadiums/add` → form thêm
- `GET /admin/stadiums/edit/{id}` → form sửa
- `POST /admin/stadiums/save`
  - nếu có file ảnh: lưu xuống thư mục triển khai `.../images/stadiums/` (trong webapp đã deploy) và set `imageUrl = "/images/stadiums/<file>"`
  - `StadiumDao.save(stadium)` insert/update
- `POST /admin/stadiums/delete/{id}`
  - chặn xóa nếu `BookingDao.hasActiveBookingForStadium(id)` còn `PENDING`/`APPROVED`
  - nếu OK: xóa mềm `is_active = FALSE`

---

## 7) Data model (Java) & quy ước hiển thị

### 7.1 `model.User`

- Fields: `id, username, password, fullName, email, phone, address, role, createdAt`
- Enum `Role`: `USER`, `ADMIN`
- Helper:
  - `getFormattedCreatedAt()` → `dd/MM/yyyy`

### 7.2 `model.Stadium`

- Fields: `id, name, address, pricePerHour, description, imageUrl, createdAt, fieldType, isActive`
- Enum `FieldType`:
  - `SÂN_5` → `"Sân 5"`
  - `SÂN_7` → `"Sân 7"`
  - `SÂN_11` → `"Sân 11"`
- Helper:
  - `getFormattedCreatedAt()`
  - `getArea()`:
    - **tách khu vực từ `address`** (lấy phần sau dấu phẩy cuối)
    - có “normalize” prefix kiểu `TP.`, `Huyện`, `Quận`...

> Điểm quan trọng: DB có cột `stadium.area` (enum các huyện/thị xã), nhưng hiện Java model không có field `area`, và DAO không map cột này. UI filter khu vực dùng `getArea()` từ `address`.

### 7.3 `model.Booking`

- Fields: `id, userId, stadiumId, bookingDate, startTime, endTime, status, createdAt`
- Enum `Status`: `PENDING, APPROVED, REJECTED, CANCELLED`
- Helpers cho JSP/UI:
  - format date/time
  - `getStatusInVietnamese()`
  - `canBeCancelled()` (logic UI; Controller vẫn có rule 30 phút khi cancel)
  - `getStatusColorClass()`
  - `getDurationInHours()`

---

## 8) Database schema (MySQL): `bookingfootball.sql`

Database: `bookingfootball`

### 8.1 Bảng `user`

```sql
CREATE TABLE `user` (
  `id` bigint AUTO_INCREMENT PRIMARY KEY,
  `username` varchar(50) NOT NULL UNIQUE,
  `password` varchar(100) NOT NULL,
  `full_name` varchar(100),
  `email` varchar(100) NOT NULL UNIQUE,
  `phone` varchar(20),
  `address` varchar(255),
  `role` enum('USER','ADMIN') DEFAULT 'USER',
  `created_at` timestamp DEFAULT CURRENT_TIMESTAMP
);
```

Seed mẫu:
- admin / admin123 (ADMIN)
- User / User123 (USER)

### 8.2 Bảng `stadium`

```sql
CREATE TABLE `stadium` (
  `id` bigint AUTO_INCREMENT PRIMARY KEY,
  `name` varchar(100) NOT NULL,
  `address` varchar(255) NOT NULL,
  `area` enum('Quy Nhơn','An Lão','Hoài Ân','Hoài Nhơn','Phù Cát','Phù Mỹ','Tây Sơn','Tuy Phước','Vân Canh','Vĩnh Thạnh','An Nhơn') NOT NULL,
  `price_per_hour` decimal(10,2) NOT NULL,
  `description` varchar(255),
  `image_url` varchar(255),
  `contact_phone` varchar(15),
  `field_type` enum('Sân 5','Sân 7','Sân 11') NOT NULL,
  `created_at` timestamp DEFAULT CURRENT_TIMESTAMP,
  `is_active` tinyint(1) DEFAULT '1'
);
```

Ghi chú:
- App **đang dùng** `is_active` để “xóa mềm”.
- Cột `area` và `contact_phone` hiện **chưa được map/khai thác đầy đủ** trong Java.

### 8.3 Bảng `booking`

```sql
CREATE TABLE `booking` (
  `id` bigint AUTO_INCREMENT PRIMARY KEY,
  `user_id` bigint NOT NULL,
  `stadium_id` bigint NOT NULL,
  `booking_date` date NOT NULL,
  `start_time` time NOT NULL,
  `end_time` time NOT NULL,
  `status` enum('PENDING','APPROVED','REJECTED','CANCELLED') DEFAULT 'PENDING',
  `created_at` timestamp DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
  FOREIGN KEY (`stadium_id`) REFERENCES `stadium` (`id`)
);
```

---

## 9) Quy ước đặt tên table trong SQL vs code (cần lưu ý)

Trong DAO, query dùng tên table dạng `User`, `Stadium`, `Booking` (chữ hoa đầu), ví dụ:

- `SELECT * FROM User WHERE ...`

Trong file SQL dump, table được tạo là `user`, `stadium`, `booking` (chữ thường).

- Trên Windows MySQL mặc định có thể **không phân biệt hoa/thường**, nên chạy bình thường.
- Trên Linux (tùy cấu hình `lower_case_table_names`) có thể **lỗi “table not found”** nếu case-sensitive.

Nếu deploy môi trường Linux/production: nên thống nhất 1 kiểu (thường chọn lowercase) và sửa SQL trong DAO cho an toàn.

---

## 10) Cách chạy dự án (local/dev)

### 10.1 Chuẩn bị DB

- Tạo database `bookingfootball`
- Import `bookingfootball.sql`
- Sửa `src/main/resources/database.properties` nếu khác user/password

### 10.2 Build WAR

```bash
mvn clean package
```

WAR thường nằm ở `target/foot.war`.

### 10.3 Run nhanh bằng Jetty Maven plugin

Vì dự án dùng Jakarta Servlet, hướng “khả thi” nhất là Jetty 11:

```bash
mvn jetty:run
```

- Port: `8080`
- Context path: `/foot`
- URL: `http://localhost:8080/foot/login`

> `tomcat7-maven-plugin` rất cũ; nếu gặp lỗi, ưu tiên Jetty hoặc deploy vào Tomcat 10.1+.

### 10.4 Deploy lên Tomcat

- Copy `target/foot.war` vào thư mục `webapps/` của Tomcat (10.1+)
- Truy cập: `/foot/login`

---

## 11) Các rule nghiệp vụ quan trọng

- **Đặt sân**:
  - `endTime` phải sau `startTime`
  - Không cho phép trùng khung giờ với booking khác (trừ booking `CANCELLED`)
  - Booking mới mặc định `PENDING`
- **Admin duyệt**:
  - `PENDING` → `APPROVED` hoặc `REJECTED`
- **Hủy booking (user)**:
  - chỉ chủ booking được hủy
  - không cho hủy nếu <= 30 phút trước giờ bắt đầu
  - hủy bằng cách update status → `CANCELLED` (không xóa record)
- **Xóa sân (admin)**:
  - nếu sân có booking `PENDING` hoặc `APPROVED` thì không cho xóa
  - xóa mềm bằng `is_active = FALSE`

---

## 12) Upload ảnh sân (admin)

Nguồn: `AdminController.saveStadium(...)`

- Upload file từ form `admin/stadium-form.jsp` field `imageFile`
- File được lưu vào **thư mục runtime của webapp đã deploy**:
  - `ServletContext.getRealPath("/") + "images/stadiums/"`
- URL lưu trong DB: `"/images/stadiums/<filename>"`

Lưu ý:
- Khi redeploy/clean server, ảnh có thể bị mất nếu server xóa thư mục deploy.
- Nếu muốn bền vững: nên chuyển sang lưu ở thư mục ngoài webapp hoặc object storage (và chỉ lưu URL).

---

## 13) Những điểm “tech debt / rủi ro” nên biết

- **Password lưu plain-text** trong DB và so sánh trực tiếp → rủi ro bảo mật lớn (nên hash bằng BCrypt/Argon2).
- **Không có CSRF protection** (form POST admin/user).
- **Auth/role check thủ công** qua session string; không có interceptor/filter để bảo vệ route một cách hệ thống.
- **Table name case mismatch** (mục 9).
- **Thiếu `index.jsp`** trong `src/main/webapp/` trong khi `web.xml` khai báo welcome file.
- **`database.properties` chứa mật khẩu thật** (commit vào git) → nên dùng env/secret.
- **Cột DB chưa dùng**: `stadium.area`, `stadium.contact_phone` (và admin list chỉ hiện active stadium).
- **Version Java vs Spring 6**: Spring Framework 6 thường yêu cầu Java mới hơn (baseline Java 17). Pom đang set Java 11. Nếu build bị lỗi ở máy khác, đây là chỗ cần kiểm tra đầu tiên.

---

## 14) “Bản đồ class” (ai chịu trách nhiệm cái gì)

- `config.WebConfig`
  - scan bean, view resolver, resource handler, multipart resolver
- `config.DatabaseConfig`
  - tạo DataSource + JdbcTemplate + transaction manager
- `config.WebAppInitializer`
  - bootstrap Spring MVC (DispatcherServlet mapping `/`) + multipart limits

- `controller.UserController`
  - login/register/logout
  - user pages: stadium list, booking form, my bookings
  - API check availability

- `controller.AdminController`
  - admin dashboard
  - booking approval/reject
  - stadium CRUD + upload image + soft delete

- `service.UserService`
  - login/register logic đơn giản (check trùng)

- `service.BookingService`
  - validate booking time
  - check overlapping
  - CRUD status
  - query booking join details cho admin

- `dao.*`
  - SQL trực tiếp bằng JdbcTemplate

---

## 15) Nếu muốn AI khác “tiếp quản” để phát triển tiếp

Các hướng mở rộng phổ biến:

- **Thêm Spring Security**: login, role-based authorization, CSRF, remember-me…
- **Chuẩn hóa DB mapping**:
  - sửa query table name lowercase
  - map `stadium.area`, `contact_phone`
- **Tách layer rõ hơn**:
  - Controller chỉ gọi Service, hạn chế gọi DAO trực tiếp
  - DTO cho API `/api/bookings/check-availability`
- **Nâng cấp datasource**: HikariCP (pool) thay DriverManagerDataSource.
- **Fix upload persistence**: lưu file ngoài webapp / cloud storage.

---

## 16) Quick “chạy thử” bằng tài khoản seed

- User:
  - username: `User`
  - password: `User123`
- Admin:
  - username: `admin`
  - password: `admin123`

---

## 17) Checklist debug nhanh (khi gặp lỗi)

- 404 toàn bộ route:
  - kiểm tra deploy đúng context `/foot`
  - kiểm tra `WebAppInitializer` (mục 4.3) và log startup Spring
- Không connect DB:
  - kiểm tra `database.properties` + MySQL đang chạy + DB `bookingfootball` tồn tại
- Login không được:
  - check record trong bảng `user` + password plain-text đúng
- Đặt sân báo trùng giờ sai:
  - xem query `BookingDao.findOverlappingBookings(...)`
- Admin không vào được:
  - session `role` phải là `"ADMIN"` (string) sau login

