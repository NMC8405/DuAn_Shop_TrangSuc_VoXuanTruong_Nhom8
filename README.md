# 💎 DỰ ÁN WEBSITE BÁN TRANG SỨC (JEWELRY SHOP)

Chào mừng bạn đến với dự án **Website Bán Trang Sức Online** - Một sản phẩm được xây dựng trên nền tảng **Java Spring Boot MVC** kết hợp với cơ sở dữ liệu **Microsoft SQL Server** và tích hợp trợ lý AI **Google Gemini**.

Dự án này được thiết kế để cung cấp trải nghiệm mua sắm trang sức trực tuyến cao cấp, hiện đại và thân thiện cho khách hàng, đồng thời hỗ trợ trang quản trị chuyên nghiệp cho người quản lý cửa hàng.

---

## 🛠️ Công Nghệ Sử Dụng

Dự án sử dụng các công nghệ hiện đại và chuẩn mực trong phát triển web bằng Java:

*   **Backend:**
    *   Java 21 (LTS)
    *   Spring Boot 3.2.0 (Spring MVC, Spring Data JPA, Spring Security)
    *   Lombok (Tự động hóa getter/setter, builder, log)
    *   Thymeleaf template engine (Render giao diện động)
*   **Database:** Microsoft SQL Server (Kết nối thông qua SQL Server JDBC Driver)
*   **Frontend:** HTML5, CSS3, JavaScript (Vanilla), Bootstrap / Custom CSS
*   **AI Integration:** Google Gemini API (Chatbot hỗ trợ tư vấn trang sức trực tiếp)
*   **Quản lý thư viện & Build:** Apache Maven

---

## 🌟 Các Tính Năng Chính

### 1. Phân Hệ Khách Hàng (Client Interface)
*   **Trang Chủ (Home):** Banner giới thiệu sản phẩm nổi bật, sản phẩm mới, và các danh mục bán chạy.
*   **Cửa Hàng (Shop):** Tìm kiếm sản phẩm, lọc sản phẩm theo danh mục và sắp xếp theo giá cả.
*   **Chi Tiết Sản Phẩm (Product Details):** Xem thông tin chi tiết, hình ảnh, giá cả, mô tả của trang sức.
*   **Giỏ Hàng (Cart):** Thêm sản phẩm vào giỏ hàng, cập nhật số lượng sản phẩm trực quan, xóa sản phẩm khỏi giỏ hàng.
*   **Thanh Toán (Checkout):** Nhập thông tin giao hàng, áp dụng mã giảm giá và tạo đơn hàng.
*   **Trợ Lý Chatbot AI (Google Gemini Chat):** Khung chat tự động tư vấn trang sức, giải đáp thắc mắc của khách hàng dựa trên trí tuệ nhân tạo.

### 2. Phân Hệ Quản Trị (Admin Panel)
*   **Bảng Điều Khiển (Dashboard):** Thống kê tổng quan về doanh thu, số lượng đơn hàng, sản phẩm và khách hàng.
*   **Quản Lý Sản Phẩm (Products):** Thêm mới sản phẩm (upload ảnh sản phẩm trực tiếp vào thư mục `uploads`), sửa thông tin sản phẩm, xóa sản phẩm.
*   **Quản Lý Danh Mục (Categories):** Thêm, sửa, xóa danh mục trang sức (Ví dụ: Nhẫn, Dây chuyền, Vòng tay, Bông tai,...).
*   **Quản Lý Đơn Hàng (Orders):** Xem danh sách đơn hàng của khách hàng, chi tiết đơn hàng, cập nhật trạng thái đơn hàng (Đang xử lý, Đang giao, Đã giao, Đã hủy).
*   **Quản Lý Mã Giảm Giá (Coupons):** Tạo các chương trình khuyến mãi, thiết lập mã giảm giá để khách hàng sử dụng khi mua sắm.
*   **Quản Lý Người Dùng (Users):** Quản lý tài khoản khách hàng và nhân viên hệ thống.

---

## 📂 Cấu Trúc File Database

File cơ sở dữ liệu của dự án nằm ngay tại thư mục gốc:
*   **Tên file:** `jewelry_shop.sql`
*   **Mục đích:** Chứa các câu lệnh tạo bảng (Tables), ràng buộc (Constraints) và dữ liệu mẫu (Sample data) về các sản phẩm trang sức, danh mục, đơn hàng.

---

## 🚀 Hướng Dẫn Cài Đặt và Chạy Dự Án

Để chạy dự án này trên máy tính cá nhân của bạn hoặc phục vụ cho việc chấm bài của Giảng viên, vui lòng thực hiện theo các bước chi tiết dưới đây:

### Bước 1: Khởi Tạo Cơ Sở Dữ Liệu
1. Mở **SQL Server Management Studio (SSMS)** trên máy tính.
2. Tạo một database mới có tên là `jewelry_shop`:
   ```sql
   CREATE DATABASE jewelry_shop;
   GO
   ```
3. Mở file `jewelry_shop.sql` (ở thư mục gốc của dự án này) bằng SSMS.
4. Nhấn **Execute** (hoặc `F5`) để import toàn bộ cấu trúc bảng và dữ liệu mẫu vào database `jewelry_shop`.

### Bước 2: Cấu Hình Kết Nối Database & API AI
Mở file `src/main/resources/application.properties` và điều chỉnh các thông số kết nối cơ sở dữ liệu phù hợp với máy tính của bạn:

```properties
# Thay thế localhost và sa / password của SQL Server trên máy bạn
spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=jewelry_shop;encrypt=true;trustServerCertificate=true
spring.datasource.username=sa
spring.datasource.password=123   # <--- Thay bằng mật khẩu SQL Server của bạn

# Nhập API Key Google Gemini để kích hoạt khung chat AI (Tùy chọn)
gemini.api.key=YOUR_GEMINI_API_KEY
```

> [!NOTE]
> Nếu bạn không cấu hình `gemini.api.key`, hệ thống chat AI sẽ tự động hoạt động ở chế độ ngoại tuyến (Offline) với câu trả lời mặc định, không làm ảnh hưởng đến các chức năng khác của website.

### Bước 3: Chạy Dự Án
Bạn có thể chạy dự án bằng 2 cách:

#### Cách 1: Sử dụng Command Line (CMD / PowerShell / Terminal)
Di chuyển vào thư mục dự án và chạy câu lệnh Maven:
```bash
mvn spring-boot:run
```

#### Cách 2: Sử dụng các IDE chuyên dụng
1. Mở IDE của bạn (IntelliJ IDEA, Eclipse, NetBeans hoặc VS Code).
2. Import dự án dưới dạng dự án **Maven**.
3. Chạy file chạy chính: `src/main/java/com/jewelryshop/JewelryShopApplication.java` bằng cách nhấp chuột phải và chọn **Run**.

---

## 🔑 Tài Khoản Đăng Nhập Mẫu

Dự án tích hợp tính năng **tự động khởi tạo tài khoản mẫu** (`DataInitializer.java`) khi ứng dụng khởi chạy lần đầu tiên nếu cơ sở dữ liệu chưa có tài khoản. Bạn có thể sử dụng các thông tin đăng nhập sau:

| Loại tài khoản | Username | Password | Quyền truy cập |
| :--- | :--- | :--- | :--- |
| **Quản trị viên (Admin)** | `admin` | `admin123` | Toàn bộ chức năng + Trang Quản trị (`/admin`) |
| **Khách hàng mẫu 1** | `user1` | `user123` | Xem hàng, mua sắm, đặt hàng, chat AI |
| **Khách hàng mẫu 2** | `user2` | `user123` | Xem hàng, mua sắm, đặt hàng, chat AI |

*Ngoài ra, bạn hoàn toàn có thể tự Đăng ký tài khoản mới trực tiếp trên giao diện website để trải nghiệm.*

---

## 🌐 Địa Chỉ Truy Cập Mặc Định

Sau khi chạy dự án thành công, hãy mở trình duyệt web và truy cập các địa chỉ sau:

*   **Giao diện mua sắm khách hàng:** [http://localhost:8080](http://localhost:8080)
*   **Trang đăng nhập:** [http://localhost:8080/login](http://localhost:8080/login)
*   **Trang quản lý Admin:** [http://localhost:8080/admin](http://localhost:8080/admin) *(Cần đăng nhập tài khoản admin)*
