-- =============================================
-- JEWELRY SHOP DATABASE - SQL SERVER SCRIPT
-- Chay file nay trong SQL Server Management Studio
-- =============================================

-- Tao database
IF NOT EXISTS (SELECT * FROM sys.databases WHERE name = 'jewelry_shop')
BEGIN
    CREATE DATABASE jewelry_shop;
END
GO

USE jewelry_shop;
GO

-- =============================================
-- 1. BANG USERS
-- =============================================
IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='users' AND xtype='U')
CREATE TABLE users (
    id          BIGINT IDENTITY(1,1) PRIMARY KEY,
    username    NVARCHAR(50)  NOT NULL UNIQUE,
    email       NVARCHAR(100) NOT NULL UNIQUE,
    password    NVARCHAR(255) NOT NULL,
    full_name   NVARCHAR(100),
    phone       NVARCHAR(20),
    address     NVARCHAR(500),
    role        NVARCHAR(20)  NOT NULL DEFAULT 'USER',
    is_active   BIT           NOT NULL DEFAULT 1,
    created_at  DATETIME2     NOT NULL DEFAULT GETDATE()
);
GO

-- =============================================
-- 2. BANG CATEGORIES
-- =============================================
IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='categories' AND xtype='U')
CREATE TABLE categories (
    id          BIGINT IDENTITY(1,1) PRIMARY KEY,
    name        NVARCHAR(100) NOT NULL,
    description NVARCHAR(500),
    image       NVARCHAR(255),
    is_active   BIT NOT NULL DEFAULT 1,
    created_at  DATETIME2 NOT NULL DEFAULT GETDATE()
);
GO

-- =============================================
-- 3. BANG PRODUCTS
-- =============================================
IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='products' AND xtype='U')
CREATE TABLE products (
    id             BIGINT IDENTITY(1,1) PRIMARY KEY,
    name           NVARCHAR(255) NOT NULL,
    description    NVARCHAR(MAX),
    price          DECIMAL(18,2) NOT NULL CHECK (price >= 0),
    sale_price     DECIMAL(18,2) CHECK (sale_price >= 0),
    category_id    BIGINT REFERENCES categories(id) ON DELETE SET NULL,
    brand          NVARCHAR(100),
    material       NVARCHAR(100),
    stock_quantity INT           NOT NULL DEFAULT 0 CHECK (stock_quantity >= 0),
    main_image     NVARCHAR(255),
    is_active      BIT           NOT NULL DEFAULT 1,
    view_count     INT           NOT NULL DEFAULT 0,
    created_at     DATETIME2     NOT NULL DEFAULT GETDATE(),
    updated_at     DATETIME2
);
GO

-- =============================================
-- 4. BANG PRODUCT_IMAGES
-- =============================================
IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='product_images' AND xtype='U')
CREATE TABLE product_images (
    id         BIGINT IDENTITY(1,1) PRIMARY KEY,
    product_id BIGINT REFERENCES products(id) ON DELETE CASCADE,
    image_path NVARCHAR(255) NOT NULL,
    sort_order INT DEFAULT 0
);
GO

-- =============================================
-- 5. BANG COUPONS
-- =============================================
IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='coupons' AND xtype='U')
CREATE TABLE coupons (
    id                BIGINT IDENTITY(1,1) PRIMARY KEY,
    code              NVARCHAR(50)   NOT NULL UNIQUE,
    description       NVARCHAR(255),
    discount_type     NVARCHAR(20)   NOT NULL DEFAULT 'PERCENTAGE', -- PERCENTAGE | FIXED_AMOUNT
    discount_value    DECIMAL(18,2)  NOT NULL CHECK (discount_value > 0),
    min_order_amount  DECIMAL(18,2)  DEFAULT 0,
    max_discount      DECIMAL(18,2),
    max_uses          INT            DEFAULT 100,
    used_count        INT            NOT NULL DEFAULT 0,
    is_active         BIT            NOT NULL DEFAULT 1,
    expired_at        DATETIME2,
    created_at        DATETIME2      NOT NULL DEFAULT GETDATE()
);
GO

-- =============================================
-- 6. BANG ORDERS
-- =============================================
IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='orders' AND xtype='U')
CREATE TABLE orders (
    id               BIGINT IDENTITY(1,1) PRIMARY KEY,
    user_id          BIGINT REFERENCES users(id) ON DELETE SET NULL,
    total_amount     DECIMAL(18,2) NOT NULL,
    discount_amount  DECIMAL(18,2) DEFAULT 0,
    final_amount     DECIMAL(18,2) NOT NULL,
    status           NVARCHAR(30)  NOT NULL DEFAULT 'PENDING',
    -- PENDING | CONFIRMED | SHIPPING | COMPLETED | CANCELLED
    shipping_name    NVARCHAR(100) NOT NULL,
    shipping_phone   NVARCHAR(20)  NOT NULL,
    shipping_address NVARCHAR(500) NOT NULL,
    payment_method   NVARCHAR(50)  NOT NULL DEFAULT 'COD',
    coupon_code      NVARCHAR(50),
    note             NVARCHAR(500),
    created_at       DATETIME2     NOT NULL DEFAULT GETDATE(),
    updated_at       DATETIME2
);
GO

-- =============================================
-- 7. BANG ORDER_ITEMS
-- =============================================
IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='order_items' AND xtype='U')
CREATE TABLE order_items (
    id         BIGINT IDENTITY(1,1) PRIMARY KEY,
    order_id   BIGINT REFERENCES orders(id) ON DELETE CASCADE,
    product_id BIGINT REFERENCES products(id) ON DELETE SET NULL,
    quantity   INT           NOT NULL CHECK (quantity > 0),
    price      DECIMAL(18,2) NOT NULL,
    product_name NVARCHAR(255),
    product_image NVARCHAR(255)
);
GO

-- =============================================
-- 8. BANG CART_ITEMS
-- =============================================
IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='cart_items' AND xtype='U')
CREATE TABLE cart_items (
    id         BIGINT IDENTITY(1,1) PRIMARY KEY,
    user_id    BIGINT REFERENCES users(id) ON DELETE CASCADE,
    product_id BIGINT REFERENCES products(id) ON DELETE CASCADE,
    quantity   INT NOT NULL DEFAULT 1 CHECK (quantity > 0),
    created_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    CONSTRAINT UQ_cart_user_product UNIQUE (user_id, product_id)
);
GO

-- =============================================
-- 9. BANG REVIEWS
-- =============================================
IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='reviews' AND xtype='U')
CREATE TABLE reviews (
    id          BIGINT IDENTITY(1,1) PRIMARY KEY,
    product_id  BIGINT REFERENCES products(id) ON DELETE CASCADE,
    user_id     BIGINT REFERENCES users(id) ON DELETE SET NULL,
    rating      INT NOT NULL CHECK (rating BETWEEN 1 AND 5),
    comment     NVARCHAR(1000),
    is_approved BIT NOT NULL DEFAULT 0,
    created_at  DATETIME2 NOT NULL DEFAULT GETDATE()
);
GO

-- =============================================
-- DU LIEU MAU (SAMPLE DATA)
-- =============================================

-- =============================================
-- TAI KHOAN NGUOI DUNG
-- =============================================
-- LUU Y: Tai khoan Admin va User se duoc TU DONG tao boi DataInitializer.java
-- khi ung dung Spring Boot chay lan dau tien.
-- Khong can INSERT users thu cong vao day.
-- Tai khoan mac dinh:
--   Admin: admin / admin123
--   User:  user1 / user123
--   User:  user2 / user123

-- =============================================
-- DANH MUC SAN PHAM
-- =============================================
INSERT INTO categories (name, description, is_active) VALUES
(N'Nhẫn', N'Nhẫn vàng, bạch kim, bạc các loại - từ nhẫn đính hôn đến nhẫn thời trang', 1),
(N'Dây Chuyền', N'Dây chuyền vàng, bạc cao cấp - tinh tế và sang trọng', 1),
(N'Bông Tai', N'Bông tai thời trang, phong cách - phù hợp mọi dịp', 1),
(N'Vòng Tay', N'Vòng tay lắc tay cao cấp - quà tặng hoàn hảo', 1),
(N'Lắc Chân', N'Lắc chân vàng, bạc tinh tế - tôn vẻ đẹp nữ tính', 1);

-- =============================================
-- SAN PHAM (12 san pham mau)
-- =============================================
INSERT INTO products (name, description, price, sale_price, category_id, brand, material, stock_quantity, is_active, view_count) VALUES
-- Nhan (category 1)
(N'Nhẫn Kim Cương Vàng 18K', N'Nhẫn đính kim cương thiên nhiên 0.3ct, vàng 18K, thiết kế tinh tế sang trọng. Phù hợp làm quà tặng cho người thân hoặc làm nhẫn đính hôn. Chứng chỉ GIA đi kèm.', 15000000, 12000000, 1, N'PNJ', N'Vàng 18K + Kim cương', 10, 1, 156),
(N'Nhẫn Vàng Hoa Hồng', N'Nhẫn vàng hồng 14K thiết kế hoa hồng lãng mạn, phù hợp với mọi dịp. Mặt nhẫn được chạm khắc tinh xảo bằng công nghệ CNC hiện đại.', 5500000, NULL, 1, N'DOJI', N'Vàng hồng 14K', 15, 1, 89),
(N'Nhẫn Đôi Bạc 925', N'Nhẫn đôi bạc 925 khắc tên theo yêu cầu, quà tặng valentine lý tưởng. Có thể khắc tên hoặc ngày kỷ niệm miễn phí.', 850000, NULL, 1, N'Bạc Việt', N'Bạc 925', 50, 1, 234),
-- Day Chuyen (category 2)
(N'Dây Chuyền Bạch Kim Ngọc Trai', N'Dây chuyền bạch kim 950 đính ngọc trai thiên nhiên 8mm, mệnh danh hoàng hậu của biển cả. Ngọc trai Akoya Nhật Bản chính hãng.', 22000000, 18000000, 2, N'PNJ', N'Bạch kim 950 + Ngọc trai', 8, 1, 312),
(N'Dây Chuyền Vàng Bông Sen', N'Dây chuyền vàng 18K mặt dây hình hoa sen - biểu tượng của sự thanh khiết và may mắn. Thiết kế độc quyền, giới hạn 100 chiếc.', 8500000, NULL, 2, N'SJC', N'Vàng 18K', 20, 1, 67),
(N'Dây Chuyền Đá Ruby', N'Dây chuyền vàng 18K mặt đá ruby đỏ rực, mang lại may mắn và tình yêu. Ruby tự nhiên Myanmar, màu đỏ huyết bồ câu.', 13500000, 11000000, 2, N'PNJ', N'Vàng 18K + Ruby', 7, 1, 198),
-- Bong Tai (category 3)
(N'Bông Tai Kim Cương Giọt Nước', N'Bông tai kim cương hình giọt nước, vàng trắng 18K. Tinh tế và sang trọng, phù hợp cho các buổi tiệc và sự kiện quan trọng.', 18000000, 15000000, 3, N'PNJ', N'Vàng trắng 18K + Kim cương', 5, 1, 445),
(N'Bông Tai Ngọc Lục Bảo', N'Bông tai ngọc lục bảo tự nhiên, viền vàng 14K. Màu xanh lá đặc trưng, cực kỳ quý hiếm. Kèm giấy chứng nhận đá quý.', 12000000, NULL, 3, N'DOJI', N'Vàng 14K + Ngọc lục bảo', 6, 1, 178),
(N'Bông Tai Ngọc Trai Nhỏ', N'Bông tai ngọc trai nhỏ thanh lịch, phù hợp đi làm và đi tiệc. Ngọc trai nước ngọt 6mm, ánh xà cừ tự nhiên.', 2500000, NULL, 3, N'Ngọc Trai VN', N'Bạc 925 + Ngọc trai', 25, 1, 567),
-- Vong Tay (category 4)
(N'Vòng Tay Vàng Charm', N'Vòng tay vàng 18K với các charm trang trí dễ thương, có thể tùy chỉnh theo ý thích. Bộ sưu tập gồm 12 charm chủ đề khác nhau.', 7500000, 6500000, 4, N'PNJ', N'Vàng 18K', 12, 1, 345),
(N'Vòng Tay Bạc Đính Đá CZ', N'Vòng tay bạc 925 đính đá CZ lấp lánh, thiết kế trẻ trung năng động. Khóa cài an toàn, điều chỉnh được kích thước.', 1200000, NULL, 4, N'Trang Sức Bạc', N'Bạc 925 + Đá CZ', 30, 1, 123),
-- Lac Chan (category 5)
(N'Lắc Chân Vàng Bướm', N'Lắc chân vàng 14K hình bướm dịu dàng nữ tính, tôn lên vẻ đẹp đôi chân. Thiết kế mỏng nhẹ, thoải mái khi đeo cả ngày.', 4500000, 3800000, 5, N'DOJI', N'Vàng 14K', 18, 1, 256);

-- =============================================
-- MA GIAM GIA (COUPONS)
-- =============================================
INSERT INTO coupons (code, description, discount_type, discount_value, min_order_amount, max_discount, max_uses, is_active, expired_at) VALUES
('WELCOME10', N'Giảm 10% cho đơn hàng đầu tiên', 'PERCENTAGE', 10, 500000, 2000000, 1000, 1, '2027-12-31'),
('SAVE500K', N'Giảm 500.000đ cho đơn từ 5 triệu', 'FIXED_AMOUNT', 500000, 5000000, NULL, 200, 1, '2027-12-31'),
('VIP20', N'Giảm 20% dành cho khách VIP (tối đa 3 triệu)', 'PERCENTAGE', 20, 1000000, 3000000, 50, 1, '2027-06-30'),
('SUMMER15', N'Giảm 15% mùa hè - Tối đa 1.5 triệu', 'PERCENTAGE', 15, 2000000, 1500000, 300, 1, '2027-08-31'),
('FREESHIP', N'Giảm 30.000đ phí ship cho mọi đơn', 'FIXED_AMOUNT', 30000, 0, NULL, 500, 1, '2027-12-31');

-- =============================================
-- DANH GIA MAU (REVIEWS)
-- =============================================
-- LUU Y: Danh gia se duoc them tu giao dien website
-- vi user_id phu thuoc vao thu tu tao boi DataInitializer
-- INSERT INTO reviews (product_id, user_id, rating, comment, is_approved, created_at) VALUES
-- (1, 2, 5, N'Nhẫn rất đẹp, kim cương lấp lánh!', 1, '2026-01-15');
-- =============================================

PRINT N'✅ Database jewelry_shop đã tạo thành công!';
PRINT N'';
PRINT N'📧 Tài khoản Admin: admin / admin123';
PRINT N'📧 Tài khoản User 1: user1 / user123';
PRINT N'📧 Tài khoản User 2: user2 / user123';
PRINT N'';
PRINT N'💡 Lưu ý: Nếu mật khẩu không đúng, hãy đăng ký tài khoản mới qua website';
PRINT N'   hoặc cập nhật hash BCrypt trong bảng users.';
GO
