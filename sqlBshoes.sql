/* ============================================================================
   BShoes — SQL Server schema + seed data (normalized for the Spring Boot / JPA port)

   What changed vs the legacy script (backup kept as sqlBshoes.sql.orig.bak):
   - Removed all CRUD stored procedures (insert_* / sp_insert_*). Under Spring Data
     JPA, inserts/updates/deletes are done through repositories (repository.save()),
     so these procedures are dead weight.
   - Removed the composite/test procedures (sp_insert_san_pham_with_placeholder,
     sp_tao_hoa_don_va_chi_tiet, sp_insert_hoa_don_placeholder) and their EXEC test rows.
   - Removed the lich_su_hoa_don triggers. Invoice-history logging now lives in the
     Java service layer (write LichSuHoaDon on create/pay/cancel). Demo history is
     seeded below with a single set-based INSERT.
   - Removed sp_cap_nhat_trang_thai_phieu_giam_gia (voucher active-status is handled
     by the date predicate in view_phieu_giam_gia_hoat_dong).
   - Converted every seed `EXEC ..._insert ...` call into a plain INSERT statement.
   - Folded id_nhan_vien into the hoa_don table (was a later ALTER) and image_url
     into the san_pham_chi_tiet seed (was a 140-row round-robin UPDATE block).

   Kept (real DB logic the backend calls via native queries):
   - function dbo.tinh_tien_giam_gia  (discount amount)
   - view view_phieu_giam_gia_hoat_dong  (active vouchers)

   Database name: BShoes. Run on a fresh DB so IDENTITY ids line up with the FKs below.
============================================================================ */

--USE master;
--GO
--ALTER DATABASE BShoes SET SINGLE_USER WITH ROLLBACK IMMEDIATE;
--GO
--DROP DATABASE BShoes;
--GO
--CREATE DATABASE BShoes;
--GO
USE BShoes;
GO

/* ============================================================================
   1. TABLES
============================================================================ */

-- 1.1 loai_san_pham
create table loai_san_pham (
    id_loai_san_pham int identity(1,1) primary key,
    ma_loai_san_pham varchar(20) unique,
    ten_loai_san_pham nvarchar(100),
    mo_ta nvarchar(255),
    trang_thai bit,
    trang_thai_xoa bit default 0
);
go
-- 1.2 chat_lieu
create table chat_lieu (
    id_chat_lieu int identity(1,1) primary key,
    ma_chat_lieu varchar(20) unique,
    ten_chat_lieu nvarchar(100),
    trang_thai bit,
    trang_thai_xoa bit default 0
);
go
-- 1.3 kieu_dang
create table kieu_dang (
    id_kieu_dang int identity(1,1) primary key,
    ma_kieu_dang varchar(20) unique,
    ten_kieu_dang nvarchar(100),
    trang_thai bit,
    trang_thai_xoa bit default 0
);
go
-- 1.4 kieu_co_giay
create table kieu_co_giay (
    id_kieu_co_giay int identity(1,1) primary key,
    ma_co_giay varchar(20) unique,
    ten_co_giay nvarchar(100),
    trang_thai bit,
    trang_thai_xoa bit default 0
);
go
-- 1.5 kieu_day_giay
create table kieu_day_giay (
    id_kieu_day_giay int identity(1,1) primary key,
    ma_day_giay varchar(20) unique,
    ten_day_giay nvarchar(100),
    trang_thai bit,
    trang_thai_xoa bit default 0
);
go
-- 1.6 thuong_hieu
create table thuong_hieu (
    id_thuong_hieu int identity(1,1) primary key,
    ma_thuong_hieu varchar(20) unique,
    ten_thuong_hieu nvarchar(100),
    mo_ta nvarchar(255),
    trang_thai bit,
    trang_thai_xoa bit default 0
);
go
-- 1.7 xuat_su
create table xuat_su (
    id_xuat_su int identity(1,1) primary key,
    ma_xuat_su varchar(20) unique,
    ten_xuat_su nvarchar(100),
    mo_ta nvarchar(255),
    trang_thai bit,
    trang_thai_xoa bit default 0
);
go
-- 1.8 san_pham
create table san_pham (
    id_san_pham int identity(1,1) primary key,
    id_loai_san_pham int,
    id_chat_lieu int,
    id_kieu_dang int,
    id_kieu_co_giay int,
    id_kieu_day_giay int,
    id_thuong_hieu int,
    id_xuat_su int,
    ma_san_pham varchar(20) unique,
    ten_san_pham nvarchar(100),
    mo_ta nvarchar(255),
    ngay_tao DATETIME default GetDate(),
    ngay_cap_nhat DATETIME default GetDate(),
    trang_thai bit,
    trang_thai_xoa bit default 0,
    foreign key (id_loai_san_pham) references loai_san_pham(id_loai_san_pham),
    foreign key (id_chat_lieu) references chat_lieu(id_chat_lieu),
    foreign key (id_kieu_dang) references kieu_dang(id_kieu_dang),
    foreign key (id_kieu_co_giay) references kieu_co_giay(id_kieu_co_giay),
    foreign key (id_kieu_day_giay) references kieu_day_giay(id_kieu_day_giay),
    foreign key (id_thuong_hieu) references thuong_hieu(id_thuong_hieu),
    foreign key (id_xuat_su) references xuat_su(id_xuat_su)
);
go
-- 1.9 kich_co
create table kich_co (
    id_kich_co int identity(1,1) primary key,
    ma_kich_co varchar(20),
    ten_kich_co nvarchar(50),
    trang_thai bit,
    trang_thai_xoa bit default 0
);
go
-- 1.10 mau_sac
create table mau_sac (
    id_mau_sac int identity(1,1) primary key,
    ma_mau_sac varchar(20),
    ten_mau_sac nvarchar(50),
    trang_thai bit,
    trang_thai_xoa bit default 0
);
go
-- 1.11 san_pham_chi_tiet
create table san_pham_chi_tiet (
    id_san_pham_chi_tiet int identity(1,1) primary key,
    id_san_pham int,
    id_kich_co int,
    id_mau_sac int,
    ma_san_pham_chi_tiet varchar(50),
    so_luong_ton int,
    don_gia money,
    gia_nhap money,
    ngay_tao DATETIME default GetDate(),
    ngay_cap_nhat DATETIME default GetDate(),
    nguoi_tao nvarchar(50),
    nguoi_cap_nhat nvarchar(50),
    trang_thai bit,
    trang_thai_xoa bit default 0,
    image_url varchar(255),
    foreign key (id_san_pham) references san_pham(id_san_pham),
    foreign key (id_kich_co) references kich_co(id_kich_co),
    foreign key (id_mau_sac) references mau_sac(id_mau_sac)
);
go
-- 1.12 vai_tro
create table vai_tro (
    id_vai_tro int identity(1,1) primary key,
    ma_vai_tro varchar(20) unique,
    ten_vai_tro nvarchar(100),
    quyen varchar(500),                 -- CSV of allowed UI screen keys; '*' = tất cả
    trang_thai bit,
    trang_thai_xoa bit default 0
);
go
-- 1.13 nhan_vien
create table nhan_vien (
    id_nhan_vien int identity(1,1) primary key,
    id_vai_tro int,
    ma_nhan_vien varchar(20) unique,
    ten_nhan_vien nvarchar(100),
    tai_khoan varchar(50),
    email varchar(100),
    mat_khau varchar(50),
    cccd varchar(20),
    so_dien_thoai varchar(20),
    dia_chi nvarchar(255),
    chuc_vu nvarchar(50),
    ngay_sinh DATETIME default GetDate(),
    gioi_tinh nvarchar(10),
    nguoi_tao_ma nvarchar(50),
    nguoi_cap_nhat nvarchar(50),
    ngay_tao_ma DATETIME default GetDate(),
    ngay_cap_nhat DATETIME default GetDate(),
    trang_thai bit,
    trang_thai_xoa bit default 0,
    foreign key (id_vai_tro) references vai_tro(id_vai_tro)
);
go
-- 1.14 khach_hang
create table khach_hang (
    id_khach_hang int identity(1,1) primary key,
    ma_khach_hang varchar(20) unique,
    ten_khach_hang nvarchar(100),
    gioi_tinh nvarchar(10),
    so_dien_thoai varchar(20),
    dia_chi nvarchar(255),
    email varchar(100),
    nguoi_tao_ma nvarchar(50),
    nguoi_cap_nhat nvarchar(50),
    ngay_tao_ma DATETIME default GetDate(),
    ngay_cap_nhat DATETIME default GetDate(),
    trang_thai bit,
    trang_thai_xoa bit default 0
);
go
-- 1.15 dia_chi
create table dia_chi (
    id_dia_chi int identity(1,1) primary key,
    id_khach_hang int,
    dia_chi_mac_dinh nvarchar(255),
    thanh_pho nvarchar(50),
    phuong nvarchar(50),
    dia_chi_them nvarchar(255),
    nguoi_tao nvarchar(50),
    nguoi_cap_nhat nvarchar(50),
    ngay_tao DATETIME default GetDate(),
    ngay_cap_nhat DATETIME default GetDate(),
    trang_thai bit,
    trang_thai_xoa bit default 0,
    foreign key (id_khach_hang) references khach_hang(id_khach_hang)
);
go
-- 1.16 phieu_giam_gia
create table phieu_giam_gia (
    id_phieu_giam_gia int identity(1,1) primary key,
    ma_phieu_giam varchar(20) unique,
    ten_phieu_giam nvarchar(100),
    loai_giam_gia int,
    gia_tri_giam money,
    don_toi_thieu money,
    giam_toi_da money,
    so_luong int,
    thoi_gian_bat_dau DATETIME default GetDate(),
    thoi_gian_ket_thuc DATETIME default GetDate(),
    nguoi_tao_ma nvarchar(50),
    nguoi_cap_nhat nvarchar(50),
    ngay_tao_ma DATETIME default GetDate(),
    ngay_cap_nhat DATETIME default GetDate(),
    trang_thai bit,
    trang_thai_xoa bit default 0
);
go
-- 1.17 hoa_don  (id_nhan_vien folded in; was a later ALTER in the legacy script)
create table hoa_don (
    id_hoa_don int identity(1,1) primary key,
    id_khach_hang int,
    id_phieu_giam_gia int,
    id_nhan_vien int,
    ma_hoa_don varchar(20) unique,
    tong_tien_ban_dau money,
    tien_giam_gia money,
    tong_tien_phai_tra money,
    phi_ship money,
    ten_nguoi_nhan nvarchar(100),
    so_dien_thoai varchar(20),
    dia_chi nvarchar(255),
    phuong_thuc_thanh_toan nvarchar(50),
    ghi_chu nvarchar(255),
    nguoi_tao_ma nvarchar(50),
    nguoi_cap_nhat nvarchar(50),
    ngay_tao_ma DATETIME default GetDate(),
    ngay_cap_nhat DATETIME default GetDate(),
    trang_thai int,
    loai_hoa_don bit,
    foreign key (id_khach_hang) references khach_hang(id_khach_hang),
    foreign key (id_phieu_giam_gia) references phieu_giam_gia(id_phieu_giam_gia),
    foreign key (id_nhan_vien) references nhan_vien(id_nhan_vien)
);
go
-- 1.18 hoa_don_chi_tiet
create table hoa_don_chi_tiet (
    id_hoa_don_chi_tiet int identity(1,1) primary key,
    id_san_pham_chi_tiet int,
    id_hoa_don int,
    gia_giam money,
    so_luong int,
    ngay_tao_ma DATETIME default GetDate(),
    ngay_cap_nhat DATETIME default GetDate(),
    nguoi_tao nvarchar(50),
    nguoi_cap_nhat nvarchar(50),
    trang_thai bit,
    trang_thai_xoa bit default 0,
    thanh_tien money,
    foreign key (id_san_pham_chi_tiet) references san_pham_chi_tiet(id_san_pham_chi_tiet),
    foreign key (id_hoa_don) references hoa_don(id_hoa_don)
);
go
-- 1.19 lich_su_hoa_don
create table lich_su_hoa_don (
    id int identity(1,1) primary key,
    id_nhan_vien int,
    id_hoa_don int,
    ghi_chu nvarchar(255),
    thoi_gian_thay_doi DATETIME default GetDate(),
    nguoi_tao_ma nvarchar(50),
    nguoi_cap_nhat nvarchar(50),
    ngay_tao_ma DATETIME default GetDate(),
    ngay_cap_nhat DATETIME default GetDate(),
    trang_thai bit,
    trang_thai_xoa bit default 0,
    foreign key (id_hoa_don) references hoa_don(id_hoa_don),
    foreign key (id_nhan_vien) references nhan_vien(id_nhan_vien)
);
go

-- 1.20 bao_hanh  (net-new feature for the Spring port. A warranty claim ties a
-- sold variant (san_pham_chi_tiet) to a customer, its invoice, and the handling
-- staff. Status/history logic lives in Java.)
create table bao_hanh (
    id_bao_hanh int identity(1,1) primary key,
    ma_bao_hanh varchar(20) unique,
    id_san_pham_chi_tiet int,
    id_khach_hang int,
    id_hoa_don int,
    id_nhan_vien int,
    serial varchar(50),
    mo_ta_loi nvarchar(255),
    loai_yeu_cau nvarchar(100),
    don_vi_bao_hanh nvarchar(100),
    chi_phi money,
    thay_linh_kien bit,
    ngay_bat_dau datetime,
    ngay_ket_thuc datetime,
    trang_thai nvarchar(50),   -- Chưa xử lý / Đã chẩn đoán / Đang xử lý / Đã xử lý / Đã thu phí / Đã trả
    ngay_tao datetime default getdate(),
    ngay_cap_nhat datetime default getdate(),
    trang_thai_xoa bit default 0,
    foreign key (id_san_pham_chi_tiet) references san_pham_chi_tiet(id_san_pham_chi_tiet),
    foreign key (id_khach_hang) references khach_hang(id_khach_hang),
    foreign key (id_hoa_don) references hoa_don(id_hoa_don),
    foreign key (id_nhan_vien) references nhan_vien(id_nhan_vien)
);
go

-- 1.21 dat_truoc  (net-new feature for the Spring port. Khách đăng ký đặt
-- trước biến thể đang hết hàng. Quy tắc: cho đặt trước khi
-- san_pham_chi_tiet.so_luong_ton = 0 (không đổi kiểu cột trang_thai). Trạng
-- thái xử lý nằm ở Java service như bao_hanh.)
create table dat_truoc (
    id_dat_truoc int identity(1,1) primary key,
    ma_dat_truoc varchar(20) unique,
    id_san_pham_chi_tiet int,
    id_khach_hang int,
    id_hoa_don int,                    -- điền khi đã chuyển thành đơn hàng
    ten_khach_hang nvarchar(100),
    so_dien_thoai varchar(20),
    email varchar(100),
    so_luong int,
    ngay_dang_ky datetime default getdate(),
    ngay_du_kien datetime,
    trang_thai nvarchar(50),           -- Chờ hàng / Đã có hàng / Đã chuyển đơn / Đã hủy
    ghi_chu nvarchar(255),
    ngay_tao datetime default getdate(),
    ngay_cap_nhat datetime default getdate(),
    trang_thai_xoa bit default 0,
    foreign key (id_san_pham_chi_tiet) references san_pham_chi_tiet(id_san_pham_chi_tiet),
    foreign key (id_khach_hang) references khach_hang(id_khach_hang),
    foreign key (id_hoa_don) references hoa_don(id_hoa_don)
);
go

-- 1.22 nhan_vien_quyen  (1 dòng = 1 nhân viên vào được 1 màn hình. Rows là SỰ
-- THẬT; vai_tro.quyen chỉ là template để chép sang đây khi tạo NV / đổi vai
-- trò, sau đó tick thêm/bớt tự do cho từng người.
-- Ngoại lệ duy nhất: vai trò ADMIN (quyen = '*') tính động = tất cả màn,
-- KHÔNG đọc bảng này, để thêm màn mới về sau admin không tự khóa mình ngoài
-- hệ thống. Vì vậy NV001/003/005 (ADMIN) không cần seed dòng nào.)
create table nhan_vien_quyen (
    id_nhan_vien int not null,
    man_hinh varchar(30) not null,
    primary key (id_nhan_vien, man_hinh),
    foreign key (id_nhan_vien) references nhan_vien(id_nhan_vien)
);
go

/* ============================================================================
   2. SEED DATA  (plain INSERTs, in FK-safe order)
============================================================================ */

-- 2.1 loai_san_pham
insert into loai_san_pham (ma_loai_san_pham, ten_loai_san_pham, mo_ta, trang_thai, trang_thai_xoa) values
('LSP01', N'Giày thể thao', N'Giày dành cho hoạt động thể thao', 1, 0),
('LSP02', N'Giày da', N'Giày công sở làm bằng da thật', 1, 0),
('LSP03', N'Sandal', N'Giày dép quai hậu thoáng mát', 1, 0),
('LSP04', N'Dép thời trang', N'Dép mang trong nhà hoặc đi dạo', 1, 0),
('LSP05', N'Giày tây', N'Giày sang trọng phù hợp môi trường công sở', 1, 0),
('LSP06', N'Giày chạy bộ', N'Giày nhẹ, êm, dành cho chạy bộ', 1, 0);
go

-- 2.2 chat_lieu
insert into chat_lieu (ma_chat_lieu, ten_chat_lieu, trang_thai, trang_thai_xoa) values
('CL01', N'Da thật', 1, 0),
('CL02', N'Vải canvas', 1, 0),
('CL03', N'Da tổng hợp', 1, 0),
('CL04', N'Vải lưới', 1, 0),
('CL05', N'Nỉ', 1, 0),
('CL06', N'Cao su', 1, 0);
go

-- 2.3 kieu_dang
insert into kieu_dang (ma_kieu_dang, ten_kieu_dang, trang_thai, trang_thai_xoa) values
('KD01', N'Cổ thấp', 1, 0),
('KD02', N'Cổ cao', 1, 0),
('KD03', N'Slip-on', 1, 0),
('KD04', N'Sneaker', 1, 0),
('KD05', N'Boots', 1, 0),
('KD06', N'Moccasin', 1, 0);
go

-- 2.4 kieu_co_giay
insert into kieu_co_giay (ma_co_giay, ten_co_giay, trang_thai, trang_thai_xoa) values
('KC01', N'Cổ trơn', 1, 0),
('KC02', N'Cổ chun', 1, 0),
('KC03', N'Cổ bo', 1, 0),
('KC04', N'Cổ gập', 1, 0),
('KC05', N'Cổ cao su', 1, 0),
('KC06', N'Cổ bọc nỉ', 1, 0);
go

-- 2.5 kieu_day_giay
insert into kieu_day_giay (ma_day_giay, ten_day_giay, trang_thai, trang_thai_xoa) values
('DG01', N'Dây bản nhỏ', 1, 0),
('DG02', N'Dây bản to', 1, 0),
('DG03', N'Dây tròn', 1, 0),
('DG04', N'Dây dẹt', 1, 0),
('DG05', N'Dây co giãn', 1, 0),
('DG06', N'Dây thun', 1, 0);
go

-- 2.6 thuong_hieu
insert into thuong_hieu (ma_thuong_hieu, ten_thuong_hieu, mo_ta, trang_thai, trang_thai_xoa) values
('TH01', N'Nike', N'Thương hiệu giày thể thao nổi tiếng', 1, 0),
('TH02', N'Adidas', N'Giày thể thao và lifestyle', 1, 0),
('TH03', N'Puma', N'Thương hiệu thể thao toàn cầu', 1, 0),
('TH04', N'Gucci', N'Thương hiệu giày cao cấp', 1, 0),
('TH05', N'Vans', N'Giày Skate & Casual', 1, 0),
('TH06', N'Reebok', N'Giày thể thao cổ điển', 1, 0);
go

-- 2.7 xuat_su
insert into xuat_su (ma_xuat_su, ten_xuat_su, mo_ta, trang_thai, trang_thai_xoa) values
('XX01', N'Việt Nam', N'Sản xuất trong nước', 1, 0),
('XX02', N'Mỹ', N'Hàng nhập khẩu từ Mỹ', 1, 0),
('XX03', N'Nhật Bản', N'Chất lượng cao từ Nhật', 1, 0),
('XX04', N'Hàn Quốc', N'Xuất xứ Hàn Quốc', 1, 0),
('XX05', N'Đức', N'Hàng sản xuất tại Đức', 1, 0),
('XX06', N'Italia', N'Hàng nhập khẩu Ý', 1, 0);
go

-- 2.8 san_pham
insert into san_pham (id_loai_san_pham, id_chat_lieu, id_kieu_dang, id_kieu_co_giay, id_kieu_day_giay, id_thuong_hieu, id_xuat_su, ma_san_pham, ten_san_pham, mo_ta, trang_thai, trang_thai_xoa) values
(1, 1, 1, 1, 1, 1, 1, 'SP01', N'Giày thể thao Nike', N'Giày chạy bộ nhẹ, êm chân', 1, 0),
(2, 2, 2, 2, 2, 2, 2, 'SP02', N'Giày da Adidas', N'Giày công sở da thật', 1, 0),
(3, 3, 3, 3, 3, 3, 3, 'SP03', N'Sandal Puma', N'Sandal thoáng mát', 1, 0),
(4, 4, 4, 4, 4, 4, 4, 'SP04', N'Dép Gucci', N'Dép thời trang cao cấp', 1, 0),
(5, 5, 5, 5, 5, 5, 5, 'SP05', N'Giày tây Vans', N'Giày sang trọng đi làm', 1, 0),
(6, 6, 6, 6, 6, 6, 6, 'SP06', N'Giày chạy bộ Reebok', N'Giày thể thao cổ điển', 1, 0);
go

-- 2.9 kich_co
insert into kich_co (ma_kich_co, ten_kich_co, trang_thai, trang_thai_xoa) values
('S', N'Size S', 1, 0),
('M', N'Size M', 1, 0),
('L', N'Size L', 1, 0),
('XL', N'Size XL', 1, 0),
('XXL', N'Size XXL', 1, 0),
('XS', N'Size XS', 1, 0);
go

-- 2.10 mau_sac
insert into mau_sac (ma_mau_sac, ten_mau_sac, trang_thai, trang_thai_xoa) values
('RD', N'Đỏ', 1, 0),
('BL', N'Xanh biển', 1, 0),
('BK', N'Đen', 1, 0),
('WH', N'Trắng', 1, 0),
('YL', N'Vàng', 1, 0),
('GN', N'Xanh lá', 1, 0);
go

-- 2.11 san_pham_chi_tiet  (image_url + gia_nhap folded in; gia_nhap ~ 60% of don_gia)
insert into san_pham_chi_tiet (id_san_pham, id_kich_co, id_mau_sac, ma_san_pham_chi_tiet, so_luong_ton, don_gia, gia_nhap, nguoi_tao, nguoi_cap_nhat, trang_thai, trang_thai_xoa, image_url) values
(1, 1, 1, 'SPCT001', 20, 200000, 120000, N'admin', N'admin', 1, 0, '/images/shoes/img_shoe_10001.png'),
(2, 2, 2, 'SPCT002', 30, 250000, 150000, N'admin', N'admin', 1, 0, '/images/shoes/img_shoe_10002.png'),
(3, 3, 3, 'SPCT003', 25, 320000, 190000, N'admin', N'admin', 1, 0, '/images/shoes/img_shoe_10003.png'),
(4, 4, 4, 'SPCT004',  0, 350000, 210000, N'admin', N'admin', 1, 0, '/images/shoes/img_shoe_10004.png'),  -- hết hàng → demo đặt trước
(5, 5, 5, 'SPCT005', 40, 400000, 240000, N'admin', N'admin', 1, 0, '/images/shoes/img_shoe_10005.png'),
(6, 6, 6, 'SPCT006',  0, 450000, 270000, N'admin', N'admin', 1, 0, '/images/shoes/img_shoe_10006.png');  -- hết hàng → demo đặt trước
go

-- 2.12 vai_tro  (quyen = CSV of allowed screen keys; '*' = tất cả)
-- Đây chỉ là TEMPLATE: bộ quyền mặc định chép sang nhan_vien_quyen khi tạo NV / đổi
-- vai trò. Quyền hiệu lực của từng người nằm ở bảng nhan_vien_quyen.
-- Danh mục + Thuộc tính giờ là tab trong Sản Phẩm, Phân quyền là tab trong Nhân Viên
-- => không còn key danh-muc / thuoc-tinh / phan-quyen.
insert into vai_tro (ma_vai_tro, ten_vai_tro, quyen, trang_thai, trang_thai_xoa) values
('ADMIN', N'Quản trị', '*', 1, 0),
('QL', N'Quản lý', 'dashboard,san-pham,hoa-don,don-hang,dat-truoc,nhan-vien,khach-hang,lich-su,bao-hanh,phieu-giam-gia,he-thong', 1, 0),
('NV', N'Nhân viên bán hàng', 'dashboard,hoa-don,don-hang,dat-truoc,bao-hanh', 1, 0),
('KT', N'Kế toán', 'dashboard,lich-su,phieu-giam-gia', 1, 0),
('BH', N'Bảo hành', 'bao-hanh,lich-su', 1, 0),
('NK', N'Nhập kho', 'san-pham,dat-truoc', 1, 0);
go

-- 2.13 nhan_vien
insert into nhan_vien (id_vai_tro, ma_nhan_vien, ten_nhan_vien, tai_khoan, email, mat_khau, cccd, so_dien_thoai, dia_chi, chuc_vu, ngay_sinh, gioi_tinh, nguoi_tao_ma, nguoi_cap_nhat, trang_thai, trang_thai_xoa) values
(1, N'NV001', N'Nguyễn Văn A', N'vanan', N'vana@p_example.com', N'123456', N'123456789', N'0912345678', N'Hà Nội', N'Nhân viên', '1990-01-01', N'Nam', N'admin', N'admin', 1, 0),
(3, N'NV002', N'Lê Thị B', N'thib', N'thib@p_example.com', N'123456', N'987654321', N'0987654321', N'Hải Phòng', N'Nhân viên', '1992-02-02', N'Nữ', N'admin', N'admin', 1, 0),
(1, N'NV003', N'Trần Văn C', N'vanc', N'vanc@p_example.com', N'123456', N'111222333', N'0911222333', N'Đà Nẵng', N'Nhân viên', '1991-03-03', N'Nam', N'admin', N'admin', 1, 0),
(3, N'NV004', N'Phạm Thị D', N'thid', N'thid@p_example.com', N'123456', N'444555666', N'0944555666', N'Quảng Ninh', N'Nhân viên', '1993-04-04', N'Nữ', N'admin', N'admin', 1, 0),
(1, N'NV005', N'Ngô Văn E', N'vane', N'vane@p_example.com', N'123456', N'777888999', N'0977888999', N'Hà Nam', N'Nhân viên', '1990-05-05', N'Nam', N'admin', N'admin', 1, 0),
(3, N'NV006', N'Hồ Thị F', N'thif', N'thif@p_example.com', N'123456', N'000111222', N'0900111222', N'Hải Dương', N'Nhân viên', '1994-06-06', N'Nữ', N'admin', N'admin', 1, 0),
-- Tài khoản test luồng đăng nhập theo vai trò (branch test-auth): NV007 banhang
-- (NV, KHÔNG có 'dashboard' -> /hoa-don), NV008 quanly (QL, KHÔNG 'dashboard' -> /san-pham).
-- Quyền hiệu lực seed ở mục 6 (nhan_vien_quyen), dùng đúng id 7/8 theo thứ tự insert này.
(3, N'NV007', N'Test Bán Hàng', N'banhang', N'banhang@p_example.com', N'123456', N'700700700', N'0900700700', N'Hà Nội', N'Nhân viên', '1995-07-07', N'Nam', N'admin', N'admin', 1, 0),
(2, N'NV008', N'Test Quản Lý', N'quanly', N'quanly@p_example.com', N'123456', N'800800800', N'0900800800', N'Hà Nội', N'Quản lý', '1988-08-08', N'Nữ', N'admin', N'admin', 1, 0);
go

-- 2.14 khach_hang
insert into khach_hang (ma_khach_hang, ten_khach_hang, gioi_tinh, so_dien_thoai, dia_chi, email, nguoi_tao_ma, nguoi_cap_nhat, ngay_tao_ma, ngay_cap_nhat, trang_thai, trang_thai_xoa) values
(N'KH01', N'Nguyễn Trung Nghĩa', N'Nam', '0968291160', N'Hải Dương', N'nghia@gmail.com', N'admin', N'admin', '2025-11-09', '2025-11-09', 1, 0),
(N'KH07', N'Ngô Văn Hùng', N'Nam', '0941234567', N'Quảng Ninh', N'hungnv@gmail.com', N'admin', N'admin', '2025-11-09', '2025-11-09', 1, 0),
(N'KH08', N'Đặng Thị Hồng', N'Nữ', '0923456789', N'Bắc Ninh', N'hongdt@gmail.com', N'admin', N'admin', '2025-11-09', '2025-11-09', 1, 0),
(N'KH09', N'Phan Minh Tuấn', N'Nam', '0956789123', N'Hải Dương', N'tuanpm@gmail.com', N'admin', N'admin', '2025-11-09', '2025-11-09', 1, 0),
(N'KH10', N'Trương Thị Lan', N'Nữ', '0919876543', N'Hà Nội', N'lantr@gmail.com', N'admin', N'admin', '2025-11-09', '2025-11-09', 1, 0),
(N'KH11', N'Lê Văn Sơn', N'Nam', '0965432198', N'Hồ Chí Minh', N'sonlv@gmail.com', N'admin', N'admin', '2025-11-09', '2025-11-09', 1, 0);
go

-- 2.15 dia_chi
insert into dia_chi (id_khach_hang, dia_chi_mac_dinh, thanh_pho, phuong, dia_chi_them, nguoi_tao, nguoi_cap_nhat, ngay_tao, ngay_cap_nhat, trang_thai, trang_thai_xoa) values
(1, N'Số 12, Đường Nguyễn Lương Bằng, TP Hải Dương', N'Hải Dương', N'Ngọc Châu', N'Căn hộ 202 - Chung cư Lê Thanh Nghị', N'admin', N'admin', '2025-11-09', '2025-11-09', 1, 0),
(2, N'Số 5, Trần Phú, TP Hạ Long', N'Quảng Ninh', N'Bạch Đằng', N'Gần quảng trường 30/10', N'admin', N'admin', '2025-11-09', '2025-11-09', 1, 0),
(3, N'25 Nguyễn Cao, TP Bắc Ninh', N'Bắc Ninh', N'Ninh Xá', N'Cạnh chợ Đọ Xá', N'admin', N'admin', '2025-11-09', '2025-11-09', 1, 0),
(4, N'123 Nguyễn Văn Linh, Hải Dương', N'Hải Dương', N'Tân Bình', N'Nhà riêng, gần siêu thị Big C', N'admin', N'admin', '2025-11-09', '2025-11-09', 1, 0),
(5, N'89 Láng Hạ, Đống Đa, Hà Nội', N'Hà Nội', N'Láng Hạ', N'Tầng 5, Tòa nhà Vinaconex 9', N'admin', N'admin', '2025-11-09', '2025-11-09', 1, 0),
(6, N'15 Nguyễn Thị Minh Khai, Quận 1', N'Hồ Chí Minh', N'Bến Nghé', N'Gần Nhà thờ Đức Bà', N'admin', N'admin', '2025-11-09', '2025-11-09', 1, 0);
go

-- 2.16 phieu_giam_gia
insert into phieu_giam_gia (ma_phieu_giam, ten_phieu_giam, loai_giam_gia, gia_tri_giam, don_toi_thieu, giam_toi_da, so_luong, thoi_gian_bat_dau, thoi_gian_ket_thuc, nguoi_tao_ma, nguoi_cap_nhat, trang_thai, trang_thai_xoa) values
(N'PGG001', N'Giảm giá dịp lễ', 1, 10, 500000, 100000, 50, '2025-11-08', '2025-11-30', N'admin', N'admin', 1, 0),
(N'PGG002', N'Giảm giá cuối tuần', 0, 20000, 100000, 50000, 100, '2025-11-09', '2025-11-10', N'admin', N'admin', 1, 0),
(N'PGG003', N'Khuyến mãi đầu tháng', 1, 15, 300000, 80000, 70, '2025-11-01', '2025-11-07', N'admin', N'admin', 1, 0),
(N'PGG004', N'Flash sale', 0, 50000, 200000, 50000, 30, '2025-11-08', '2025-11-08', N'admin', N'admin', 1, 0),
(N'PGG005', N'Giảm giá sinh nhật', 1, 20, 400000, 120000, 60, '2025-11-15', '2025-11-15', N'admin', N'admin', 1, 0),
(N'PGG006', N'Mua 1 tặng 1', 0, 0, 0, 0, 20, '2025-11-20', '2025-11-25', N'admin', N'admin', 1, 0);
go

-- 2.17 hoa_don  (trang_thai 1 = Đã thanh toán, loai_hoa_don 1 = hợp lệ; id_nhan_vien rotated 1-6 for demo)
insert into hoa_don (id_khach_hang, id_phieu_giam_gia, id_nhan_vien, ma_hoa_don, tong_tien_ban_dau, tien_giam_gia, tong_tien_phai_tra, ten_nguoi_nhan, so_dien_thoai, dia_chi, phuong_thuc_thanh_toan, ghi_chu, nguoi_tao_ma, nguoi_cap_nhat, ngay_tao_ma, ngay_cap_nhat, trang_thai, loai_hoa_don) values
(1, 1, 1, 'HD001', 500000, 50000, 450000, N'Nguyễn Văn A', N'0987654321', N'Hà Nội', N'Tiền mặt', N'Mua giày Nike Air', N'Nghia', N'Nghia', '2025-11-12 14:30:00', '2025-11-12 14:30:00', 1, 1),
(2, 2, 2, 'HD002', 1200000, 200000, 1000000, N'Trần Thị B', N'0912345678', N'Hải Dương', N'Chuyển khoản', N'Mua áo khoác Adidas', N'Nghia', N'Nghia', '2025-11-12 15:00:00', '2025-11-12 15:00:00', 1, 1),
(3, 1, 3, 'HD003', 800000, 100000, 700000, N'Lê Văn C', N'0978123456', N'Bắc Ninh', N'Tiền mặt', N'Mua balo thời trang', N'Nghia', N'Nghia', '2025-11-12 15:30:00', '2025-11-12 15:30:00', 1, 1),
(1, 1, 4, 'HD202401001', 600000, 60000, 540000, N'Nguyễn Văn A', N'0987654321', N'Hà Nội', N'Tiền mặt', N'Mua giày Nike Air', N'Nghia', N'Nghia', '2024-01-15 10:30:00', '2024-01-15 10:30:00', 1, 1),
(2, 2, 5, 'HD202402001', 1300000, 220000, 1080000, N'Trần Thị B', N'0912345678', N'Hải Dương', N'Chuyển khoản', N'Mua áo khoác Adidas', N'Nghia', N'Nghia', '2024-02-18 14:00:00', '2024-02-18 14:00:00', 1, 1),
(3, 1, 6, 'HD202403001', 900000, 110000, 790000, N'Lê Văn C', N'0978123456', N'Bắc Ninh', N'Tiền mặt', N'Mua balo thời trang', N'Nghia', N'Nghia', '2024-03-22 11:15:00', '2024-03-22 11:15:00', 1, 1),
(1, 2, 1, 'HD202404001', 1100000, 180000, 920000, N'Phạm Minh Tuấn', N'0956789123', N'Hải Dương', N'Chuyển khoản', N'Mua giày chạy bộ', N'Nghia', N'Nghia', '2024-04-10 09:45:00', '2024-04-10 09:45:00', 1, 1),
(2, 1, 2, 'HD202405001', 1400000, 240000, 1160000, N'Đặng Thị Hồng', N'0923456789', N'Bắc Ninh', N'Tiền mặt', N'Mua sandal thời trang', N'Nghia', N'Nghia', '2024-05-20 15:30:00', '2024-05-20 15:30:00', 1, 1),
(3, 2, 3, 'HD202406001', 1550000, 310000, 1240000, N'Trương Thị Lan', N'0919876543', N'Hà Nội', N'Chuyển khoản', N'Mua giày tây cao cấp', N'Nghia', N'Nghia', '2024-06-12 13:20:00', '2024-06-12 13:20:00', 1, 1),
(1, 1, 4, 'HD202407001', 1700000, 350000, 1350000, N'Lê Văn Sơn', N'0965432198', N'Hồ Chí Minh', N'Tiền mặt', N'Mua giày Reebok', N'Nghia', N'Nghia', '2024-07-25 16:45:00', '2024-07-25 16:45:00', 1, 1),
(2, 2, 5, 'HD202408001', 1850000, 420000, 1430000, N'Ngô Văn Hùng', N'0941234567', N'Quảng Ninh', N'Chuyển khoản', N'Mua sandal da', N'Nghia', N'Nghia', '2024-08-08 10:15:00', '2024-08-08 10:15:00', 1, 1),
(3, 1, 6, 'HD202409001', 2000000, 480000, 1520000, N'Hồ Thị Hương', N'0934567890', N'Hải Phòng', N'Tiền mặt', N'Mua giày Nike Pro', N'Nghia', N'Nghia', '2024-09-18 14:30:00', '2024-09-18 14:30:00', 1, 1),
(1, 2, 1, 'HD202410001', 2150000, 550000, 1600000, N'Võ Thị Ngọc', N'0923789456', N'Đà Nẵng', N'Chuyển khoản', N'Mua giày Adidas Ultra', N'Nghia', N'Nghia', '2024-10-05 11:00:00', '2024-10-05 11:00:00', 1, 1),
(2, 1, 2, 'HD202411001', 2300000, 620000, 1680000, N'Bùi Văn Long', N'0945678901', N'Nam Định', N'Tiền mặt', N'Mua giày chạy bộ', N'Nghia', N'Nghia', '2024-11-14 09:30:00', '2024-11-14 09:30:00', 1, 1),
(3, 2, 3, 'HD202412001', 2450000, 700000, 1750000, N'Đinh Thị Mai', N'0912456789', N'Thái Bình', N'Chuyển khoản', N'Mua giày tây Gucci', N'Nghia', N'Nghia', '2024-12-20 15:45:00', '2024-12-20 15:45:00', 1, 1),
(1, 1, 4, 'HD202501001', 2600000, 780000, 1820000, N'Trần Minh Châu', N'0934789012', N'Tuyên Quang', N'Tiền mặt', N'Mua giày Nike Air Max', N'Nghia', N'Nghia', '2025-01-12 10:20:00', '2025-01-12 10:20:00', 1, 1),
(2, 2, 5, 'HD202502001', 2750000, 850000, 1900000, N'Hoàng Văn Nam', N'0923901234', N'Yên Bái', N'Chuyển khoản', N'Mua Adidas Boost', N'Nghia', N'Nghia', '2025-02-08 13:40:00', '2025-02-08 13:40:00', 1, 1),
(3, 1, 6, 'HD202503001', 2900000, 920000, 1980000, N'Vũ Thị Huyền', N'0945012345', N'Lạng Sơn', N'Tiền mặt', N'Mua giày Puma Ultra', N'Nghia', N'Nghia', '2025-03-16 11:55:00', '2025-03-16 11:55:00', 1, 1),
(1, 2, 1, 'HD202504001', 3050000, 1000000, 2050000, N'Ngô Văn Kiên', N'0912567890', N'Cao Bằng', N'Chuyển khoản', N'Mua giày thể thao', N'Nghia', N'Nghia', '2025-04-22 14:25:00', '2025-04-22 14:25:00', 1, 1),
(2, 1, 2, 'HD202505001', 3200000, 1080000, 2120000, N'Tô Thị Hà', N'0934678901', N'Bắc Giang', N'Tiền mặt', N'Mua giày Nike Flex', N'Nghia', N'Nghia', '2025-05-10 09:35:00', '2025-05-10 09:35:00', 1, 1),
(3, 2, 3, 'HD202506001', 3350000, 1160000, 2190000, N'Nông Văn Tiến', N'0956789012', N'Phú Thọ', N'Chuyển khoản', N'Mua giày cao cấp', N'Nghia', N'Nghia', '2025-06-18 12:10:00', '2025-06-18 12:10:00', 1, 1),
(1, 1, 4, 'HD202507001', 3500000, 1240000, 2260000, N'Chu Thị Linh', N'0923890123', N'Vĩnh Phúc', N'Tiền mặt', N'Mua giày thời trang', N'Nghia', N'Nghia', '2025-07-25 16:50:00', '2025-07-25 16:50:00', 1, 1),
(2, 2, 5, 'HD202508001', 3650000, 1320000, 2330000, N'Lý Văn Hồng', N'0945901234', N'Hà Tây', N'Chuyển khoản', N'Mua giày chạy marathon', N'Nghia', N'Nghia', '2025-08-05 10:15:00', '2025-08-05 10:15:00', 1, 1),
(3, 1, 6, 'HD202509001', 3800000, 1400000, 2400000, N'Phan Văn Dũng', N'0967012345', N'Hà Nội', N'Tiền mặt', N'Mua giày Vans Pro', N'Nghia', N'Nghia', '2025-09-12 13:45:00', '2025-09-12 13:45:00', 1, 1),
(1, 2, 1, 'HD202510001', 3950000, 1500000, 2450000, N'Mạch Thị Thanh', N'0912678901', N'Hải Dương', N'Chuyển khoản', N'Mua giày Reebok Classic', N'Nghia', N'Nghia', '2025-10-20 15:20:00', '2025-10-20 15:20:00', 1, 1),
(2, 1, 2, 'HD202511001', 4100000, 1600000, 2500000, N'Giang Văn Phú', N'0934789012', N'Bắc Ninh', N'Tiền mặt', N'Mua giày Nike Court', N'Nghia', N'Nghia', '2025-11-08 11:30:00', '2025-11-08 11:30:00', 1, 1),
(3, 2, 3, 'HD202512001', 4250000, 1700000, 2550000, N'Khương Thị Hương', N'0956890123', N'Quảng Ninh', N'Chuyển khoản', N'Mua giày Gucci Limited', N'Nghia', N'Nghia', '2025-12-15 14:55:00', '2025-12-15 14:55:00', 1, 1);
go

/* 27 hóa đơn ở trên đều trạng thái 1 (Thành công), nên màn Giao Hàng, luồng trả hàng và
   mục "Đơn của tôi" ngoài cửa hàng online sẽ trống trơn trên DB mới. Ba hóa đơn dưới đây
   phủ nốt các trạng thái còn lại để demo được ngay.

   Quy ước cờ loai_hoa_don (mọi truy vấn doanh thu ở ThongKe lọc loai_hoa_don = 1):
     Chờ giao COD  -> 0, chưa thu tiền
     Đã giao       -> 1, thu tiền khi giao
     Trả hàng      -> 0, đã hoàn tiền cho khách
   Số điện thoại trùng khách hàng có sẵn để tra cứu "Đơn của tôi" ra kết quả. */
insert into hoa_don (id_khach_hang, id_phieu_giam_gia, id_nhan_vien, ma_hoa_don, tong_tien_ban_dau, tien_giam_gia, phi_ship, tong_tien_phai_tra, ten_nguoi_nhan, so_dien_thoai, dia_chi, phuong_thuc_thanh_toan, ghi_chu, nguoi_tao_ma, nguoi_cap_nhat, ngay_tao_ma, ngay_cap_nhat, trang_thai, loai_hoa_don) values
-- id 28: khách đặt online, đang chờ giao. Kho đã giữ hàng, chưa thu tiền.
(1, NULL, NULL, 'HD028', 200000, 0, 30000, 230000, N'Nguyễn Trung Nghĩa', '0968291160', N'Hải Dương', N'COD', N'Khách đặt online, giao giờ hành chính', N'Nguyễn Trung Nghĩa', N'Nguyễn Trung Nghĩa', '2026-07-14 09:20:00', '2026-07-14 09:20:00', 3, 0),
-- id 29: đã giao xong, thu tiền khi giao nên loai_hoa_don = 1.
(3, NULL, 2, 'HD029', 250000, 0, 30000, 280000, N'Đặng Thị Hồng', '0923456789', N'Bắc Ninh', N'COD', N'Khách đặt online, đã nhận hàng', N'Lê Thị B', N'Lê Thị B', '2026-07-08 16:05:00', '2026-07-11 10:00:00', 4, 1),
-- id 30: khách trả hàng, kho đã hoàn, tiền đã trả lại nên rút khỏi doanh thu.
(4, NULL, 2, 'HD030', 320000, 0, 0, 320000, N'Phan Minh Tuấn', '0956789123', N'Hải Dương', N'Tiền mặt', N'Khách trả hàng do sai size', N'Lê Thị B', N'Lê Thị B', '2026-07-05 13:40:00', '2026-07-09 09:15:00', 5, 0);
go

-- 2.18 hoa_don_chi_tiet
-- NOTE: the legacy seed linked every monthly invoice's lines to id_hoa_don 1..24, but the
-- three HD001-003 rows shifted the IDENTITY ids by 3 (so lines landed on the wrong invoices
-- and invoices 25-27 had none). Fixed here: each invoice's lines reference its real id.
insert into hoa_don_chi_tiet (id_san_pham_chi_tiet, id_hoa_don, gia_giam, so_luong, ngay_tao_ma, ngay_cap_nhat, nguoi_tao, nguoi_cap_nhat, trang_thai, trang_thai_xoa, thanh_tien) values
-- HD001 (id 1)
(1, 1, 5000, 2, '2025-11-09', '2025-11-09', N'admin', N'admin', 1, 0, 50000),
(2, 1, 5000, 2, '2025-11-09', '2025-11-09', N'admin', N'admin', 1, 0, 50000),
(3, 1, 5000, 2, '2025-11-09', '2025-11-09', N'admin', N'admin', 1, 0, 50000),
-- HD002 (id 2)
(3, 2, 5000, 2, '2025-11-09', '2025-11-09', N'admin', N'admin', 1, 0, 50000),
(2, 2, 10000, 1, '2025-11-09', '2025-11-09', N'admin', N'admin', 1, 0, 50000),
(2, 2, 0, 3, '2025-11-09', '2025-11-09', N'admin', N'admin', 1, 0, 50000),
-- HD003 (id 3)
(2, 3, 7500, 1, '2025-11-09', '2025-11-09', N'admin', N'admin', 1, 0, 50000),
(3, 3, 4000, 4, '2025-11-09', '2025-11-09', N'admin', N'admin', 1, 0, 50000),
-- HD202401001 (id 4)
(1, 4, 5000, 3, '2024-01-15', '2024-01-15', N'admin', N'admin', 1, 0, 60000),
(2, 4, 5000, 3, '2024-01-15', '2024-01-15', N'admin', N'admin', 1, 0, 75000),
(3, 4, 5000, 3, '2024-01-15', '2024-01-15', N'admin', N'admin', 1, 0, 96000),
-- HD202402001 (id 5)
(1, 5, 5000, 6, '2024-02-18', '2024-02-18', N'admin', N'admin', 1, 0, 120000),
(2, 5, 5000, 6, '2024-02-18', '2024-02-18', N'admin', N'admin', 1, 0, 150000),
(4, 5, 5000, 6, '2024-02-18', '2024-02-18', N'admin', N'admin', 1, 0, 210000),
-- HD202403001 (id 6)
(2, 6, 5000, 9, '2024-03-22', '2024-03-22', N'admin', N'admin', 1, 0, 225000),
(3, 6, 5000, 9, '2024-03-22', '2024-03-22', N'admin', N'admin', 1, 0, 288000),
(5, 6, 5000, 9, '2024-03-22', '2024-03-22', N'admin', N'admin', 1, 0, 360000),
-- HD202404001 (id 7)
(3, 7, 5000, 12, '2024-04-10', '2024-04-10', N'admin', N'admin', 1, 0, 384000),
(4, 7, 5000, 12, '2024-04-10', '2024-04-10', N'admin', N'admin', 1, 0, 420000),
(1, 7, 5000, 12, '2024-04-10', '2024-04-10', N'admin', N'admin', 1, 0, 240000),
-- HD202405001 (id 8)
(4, 8, 5000, 15, '2024-05-20', '2024-05-20', N'admin', N'admin', 1, 0, 525000),
(5, 8, 5000, 15, '2024-05-20', '2024-05-20', N'admin', N'admin', 1, 0, 600000),
(2, 8, 5000, 15, '2024-05-20', '2024-05-20', N'admin', N'admin', 1, 0, 375000),
-- HD202406001 (id 9)
(5, 9, 5000, 18, '2024-06-12', '2024-06-12', N'admin', N'admin', 1, 0, 720000),
(6, 9, 5000, 18, '2024-06-12', '2024-06-12', N'admin', N'admin', 1, 0, 810000),
(3, 9, 5000, 18, '2024-06-12', '2024-06-12', N'admin', N'admin', 1, 0, 576000),
-- HD202407001 (id 10)
(1, 10, 5000, 21, '2024-07-25', '2024-07-25', N'admin', N'admin', 1, 0, 420000),
(2, 10, 5000, 21, '2024-07-25', '2024-07-25', N'admin', N'admin', 1, 0, 525000),
(4, 10, 5000, 21, '2024-07-25', '2024-07-25', N'admin', N'admin', 1, 0, 735000),
-- HD202408001 (id 11)
(3, 11, 5000, 24, '2024-08-08', '2024-08-08', N'admin', N'admin', 1, 0, 768000),
(5, 11, 5000, 24, '2024-08-08', '2024-08-08', N'admin', N'admin', 1, 0, 960000),
(6, 11, 5000, 24, '2024-08-08', '2024-08-08', N'admin', N'admin', 1, 0, 1080000),
-- HD202409001 (id 12)
(1, 12, 5000, 27, '2024-09-18', '2024-09-18', N'admin', N'admin', 1, 0, 540000),
(4, 12, 5000, 27, '2024-09-18', '2024-09-18', N'admin', N'admin', 1, 0, 945000),
(2, 12, 5000, 27, '2024-09-18', '2024-09-18', N'admin', N'admin', 1, 0, 675000),
-- HD202410001 (id 13)
(2, 13, 5000, 30, '2024-10-05', '2024-10-05', N'admin', N'admin', 1, 0, 750000),
(3, 13, 5000, 30, '2024-10-05', '2024-10-05', N'admin', N'admin', 1, 0, 960000),
(5, 13, 5000, 30, '2024-10-05', '2024-10-05', N'admin', N'admin', 1, 0, 1200000),
-- HD202411001 (id 14)
(4, 14, 5000, 33, '2024-11-14', '2024-11-14', N'admin', N'admin', 1, 0, 1155000),
(6, 14, 5000, 33, '2024-11-14', '2024-11-14', N'admin', N'admin', 1, 0, 1485000),
(1, 14, 5000, 33, '2024-11-14', '2024-11-14', N'admin', N'admin', 1, 0, 660000),
-- HD202412001 (id 15)
(5, 15, 5000, 36, '2024-12-20', '2024-12-20', N'admin', N'admin', 1, 0, 1440000),
(3, 15, 5000, 36, '2024-12-20', '2024-12-20', N'admin', N'admin', 1, 0, 1152000),
(2, 15, 5000, 36, '2024-12-20', '2024-12-20', N'admin', N'admin', 1, 0, 900000),
-- HD202501001 (id 16)
(1, 16, 5000, 39, '2025-01-12', '2025-01-12', N'admin', N'admin', 1, 0, 780000),
(4, 16, 5000, 39, '2025-01-12', '2025-01-12', N'admin', N'admin', 1, 0, 1365000),
(6, 16, 5000, 39, '2025-01-12', '2025-01-12', N'admin', N'admin', 1, 0, 1755000),
-- HD202502001 (id 17)
(2, 17, 5000, 42, '2025-02-08', '2025-02-08', N'admin', N'admin', 1, 0, 1050000),
(3, 17, 5000, 42, '2025-02-08', '2025-02-08', N'admin', N'admin', 1, 0, 1344000),
(5, 17, 5000, 42, '2025-02-08', '2025-02-08', N'admin', N'admin', 1, 0, 1680000),
-- HD202503001 (id 18)
(4, 18, 5000, 45, '2025-03-16', '2025-03-16', N'admin', N'admin', 1, 0, 1575000),
(1, 18, 5000, 45, '2025-03-16', '2025-03-16', N'admin', N'admin', 1, 0, 900000),
(3, 18, 5000, 45, '2025-03-16', '2025-03-16', N'admin', N'admin', 1, 0, 1440000),
-- HD202504001 (id 19)
(5, 19, 5000, 48, '2025-04-22', '2025-04-22', N'admin', N'admin', 1, 0, 1920000),
(2, 19, 5000, 48, '2025-04-22', '2025-04-22', N'admin', N'admin', 1, 0, 1200000),
(6, 19, 5000, 48, '2025-04-22', '2025-04-22', N'admin', N'admin', 1, 0, 2160000),
-- HD202505001 (id 20)
(1, 20, 5000, 51, '2025-05-10', '2025-05-10', N'admin', N'admin', 1, 0, 1020000),
(4, 20, 5000, 51, '2025-05-10', '2025-05-10', N'admin', N'admin', 1, 0, 1785000),
(2, 20, 5000, 51, '2025-05-10', '2025-05-10', N'admin', N'admin', 1, 0, 1275000),
-- HD202506001 (id 21)
(3, 21, 5000, 54, '2025-06-18', '2025-06-18', N'admin', N'admin', 1, 0, 1728000),
(5, 21, 5000, 54, '2025-06-18', '2025-06-18', N'admin', N'admin', 1, 0, 2160000),
(4, 21, 5000, 54, '2025-06-18', '2025-06-18', N'admin', N'admin', 1, 0, 1890000),
-- HD202507001 (id 22)
(6, 22, 5000, 57, '2025-07-25', '2025-07-25', N'admin', N'admin', 1, 0, 2565000),
(1, 22, 5000, 57, '2025-07-25', '2025-07-25', N'admin', N'admin', 1, 0, 1140000),
(3, 22, 5000, 57, '2025-07-25', '2025-07-25', N'admin', N'admin', 1, 0, 1824000),
-- HD202508001 (id 23)
(2, 23, 5000, 60, '2025-08-05', '2025-08-05', N'admin', N'admin', 1, 0, 1500000),
(4, 23, 5000, 60, '2025-08-05', '2025-08-05', N'admin', N'admin', 1, 0, 2100000),
(5, 23, 5000, 60, '2025-08-05', '2025-08-05', N'admin', N'admin', 1, 0, 2400000),
-- HD202509001 (id 24)
(3, 24, 5000, 63, '2025-09-12', '2025-09-12', N'admin', N'admin', 1, 0, 2016000),
(6, 24, 5000, 63, '2025-09-12', '2025-09-12', N'admin', N'admin', 1, 0, 2835000),
(2, 24, 5000, 63, '2025-09-12', '2025-09-12', N'admin', N'admin', 1, 0, 1575000),
-- HD202510001 (id 25)
(1, 25, 5000, 66, '2025-10-20', '2025-10-20', N'admin', N'admin', 1, 0, 1320000),
(5, 25, 5000, 66, '2025-10-20', '2025-10-20', N'admin', N'admin', 1, 0, 2640000),
(4, 25, 5000, 66, '2025-10-20', '2025-10-20', N'admin', N'admin', 1, 0, 2310000),
-- HD202511001 (id 26)
(4, 26, 5000, 69, '2025-11-08', '2025-11-08', N'admin', N'admin', 1, 0, 2415000),
(2, 26, 5000, 69, '2025-11-08', '2025-11-08', N'admin', N'admin', 1, 0, 1725000),
(6, 26, 5000, 69, '2025-11-08', '2025-11-08', N'admin', N'admin', 1, 0, 3105000),
-- HD202512001 (id 27)
(5, 27, 5000, 72, '2025-12-15', '2025-12-15', N'admin', N'admin', 1, 0, 2880000),
(3, 27, 5000, 72, '2025-12-15', '2025-12-15', N'admin', N'admin', 1, 0, 2304000),
(1, 27, 5000, 72, '2025-12-15', '2025-12-15', N'admin', N'admin', 1, 0, 1440000),
-- Dòng hàng cho 3 hóa đơn 28, 29, 30 ở trên. Chỉ dùng biến thể còn hàng (SPCT001, 002, 003).
(1, 28, 0, 1, '2026-07-14', '2026-07-14', N'admin', N'admin', 1, 0, 200000),
(2, 29, 0, 1, '2026-07-08', '2026-07-08', N'admin', N'admin', 1, 0, 250000),
(3, 30, 0, 1, '2026-07-05', '2026-07-05', N'admin', N'admin', 1, 0, 320000);
go

-- 2.19 lich_su_hoa_don — demo history: one "created" row per invoice.
-- Going forward, the Java service layer writes history on create/pay/cancel.
insert into lich_su_hoa_don (id_nhan_vien, id_hoa_don, ghi_chu, thoi_gian_thay_doi, nguoi_tao_ma, nguoi_cap_nhat, ngay_tao_ma, ngay_cap_nhat, trang_thai, trang_thai_xoa)
select id_nhan_vien, id_hoa_don,
       N'Tạo mới hóa đơn: ' + ma_hoa_don,
       ngay_tao_ma, nguoi_tao_ma, nguoi_cap_nhat, ngay_tao_ma, ngay_cap_nhat, 1, 0
from hoa_don;
go

-- Lịch sử đổi trạng thái cho 3 hóa đơn demo ở trên, để màn Lịch Sử có dữ liệu thật.
-- Từ đây trở đi service Java tự ghi lịch sử mỗi lần đổi trạng thái.
insert into lich_su_hoa_don (id_nhan_vien, id_hoa_don, ghi_chu, thoi_gian_thay_doi, nguoi_tao_ma, nguoi_cap_nhat, ngay_tao_ma, ngay_cap_nhat, trang_thai, trang_thai_xoa) values
(NULL, 28, N'Khách đặt hàng online, chờ giao: HD028', '2026-07-14 09:20:00', N'Nguyễn Trung Nghĩa', N'Nguyễn Trung Nghĩa', '2026-07-14 09:20:00', '2026-07-14 09:20:00', 1, 0),
(2, 29, N'Đã giao hàng, đã thu tiền: HD029', '2026-07-11 10:00:00', N'Lê Thị B', N'Lê Thị B', '2026-07-11 10:00:00', '2026-07-11 10:00:00', 1, 0),
(2, 30, N'Trả hàng, hoàn tiền: HD030', '2026-07-09 09:15:00', N'Lê Thị B', N'Lê Thị B', '2026-07-09 09:15:00', '2026-07-09 09:15:00', 0, 0);
go

-- 2.20 bao_hanh
insert into bao_hanh (ma_bao_hanh, id_san_pham_chi_tiet, id_khach_hang, id_hoa_don, id_nhan_vien, serial, mo_ta_loi, loai_yeu_cau, don_vi_bao_hanh, chi_phi, thay_linh_kien, ngay_bat_dau, ngay_ket_thuc, trang_thai) values
('BH0001', 1, 1, 1,  2, 'NK-42-000123', N'Đế bị bong ở mũi giày sau 2 tháng sử dụng.', N'Bong đế',  N'BShoes Center', 120000, 1, '2026-05-08', '2026-11-08', N'Đang xử lý'),
('BH0002', 2, 3, NULL, 3, 'AD-40-000456', N'Đường chỉ gót bị bung.',                    N'Đứt chỉ',  N'Adidas Care',  80000,  0, '2026-10-20', '2027-04-20', N'Chưa xử lý'),
('BH0003', 3, 4, NULL, 5, 'PM-41-000789', N'Quai hậu bị gãy khớp nối.',                 N'Gãy quai', N'BShoes Center', 60000,  1, '2026-09-12', '2027-03-12', N'Đã xử lý'),
('BH0004', 5, 5, NULL, 6, 'VN-39-000234', N'Ngoài thời hạn bảo hành.',                  N'Mòn đế',   N'BShoes Center', 0,      0, '2025-08-05', '2026-02-05', N'Đã trả'),
('BH0005', 6, 6, NULL, 2, 'RB-43-000567', N'Keo dán đế lỗi, tách lớp.',                 N'Lỗi keo',  N'BShoes Center', 150000, 1, '2026-07-25', '2027-01-25', N'Đã thu phí');
go

-- 2.21 dat_truoc
insert into dat_truoc (ma_dat_truoc, id_san_pham_chi_tiet, id_khach_hang, ten_khach_hang, so_dien_thoai, email, so_luong, ngay_dang_ky, ngay_du_kien, trang_thai, ghi_chu) values
-- SPCT004 + SPCT006 đang hết hàng → 'Chờ hàng'. SPCT005 hàng đã về → 'Đã có hàng' (chuyển đơn được ngay).
('DT0001', 6, 1, N'Nguyễn Trung Nghĩa', '0968291160', N'nghia@gmail.com', 1, '2026-07-02', '2026-08-15', N'Chờ hàng',     N'Khách hỏi size 42, báo khi có hàng'),
('DT0002', 4, 3, N'Đặng Thị Hồng',      '0923456789', N'hongdt@gmail.com', 2, '2026-07-05', '2026-08-20', N'Chờ hàng',     N'Đặt 2 đôi cho cả nhà'),
('DT0003', 5, 4, N'Phan Minh Tuấn',     '0956789123', N'tuanpm@gmail.com', 1, '2026-06-20', '2026-07-30', N'Đã có hàng',   N'Đã gọi báo khách, chờ tới lấy'),
('DT0004', 5, 5, N'Trương Thị Lan',     '0919876543', N'lantr@gmail.com', 1, '2026-06-10', '2026-07-10', N'Đã hủy',       N'Khách đổi ý');
go

-- 2.22 nhan_vien_quyen
-- NV002, NV004, NV006 mang vai trò NV -> chép template 'Nhân viên bán hàng'.
-- NV002 được mở rộng thêm 'khach-hang': minh họa "template + mở rộng riêng".
insert into nhan_vien_quyen (id_nhan_vien, man_hinh) values
(2, 'dashboard'), (2, 'hoa-don'), (2, 'don-hang'), (2, 'dat-truoc'), (2, 'bao-hanh'), (2, 'khach-hang'),
(4, 'dashboard'), (4, 'hoa-don'), (4, 'don-hang'), (4, 'dat-truoc'), (4, 'bao-hanh'),
(6, 'dashboard'), (6, 'hoa-don'), (6, 'don-hang'), (6, 'dat-truoc'), (6, 'bao-hanh'),
-- Tài khoản test (mục 2.13): id 7 = banhang (NV), id 8 = quanly (QL). Đều KHÔNG có
-- 'dashboard' -> đăng nhập rơi vào màn được phép đầu tiên (/hoa-don, /san-pham).
(7, 'hoa-don'), (7, 'don-hang'), (7, 'bao-hanh'),
(8, 'san-pham'), (8, 'khach-hang'), (8, 'phieu-giam-gia');
go

/* ============================================================================
   3. DB LOGIC KEPT AS NATIVE OBJECTS (called from the backend via native queries)
============================================================================ */

-- 3.1 Active-vouchers view — HoaDonRepository.getVouchersActive()
CREATE OR ALTER VIEW view_phieu_giam_gia_hoat_dong AS
SELECT
    id_phieu_giam_gia,
    ma_phieu_giam,
    ten_phieu_giam,
    loai_giam_gia,
    gia_tri_giam,
    don_toi_thieu,
    giam_toi_da,
    so_luong,
    thoi_gian_bat_dau,
    thoi_gian_ket_thuc,
    trang_thai
FROM phieu_giam_gia
WHERE trang_thai = 1
  AND trang_thai_xoa = 0
  AND GETDATE() BETWEEN thoi_gian_bat_dau AND thoi_gian_ket_thuc
  AND so_luong > 0;
GO

-- 3.2 Discount-amount function — HoaDonRepository.tinhGiamGia(idPhieu, tongTien)
CREATE OR ALTER FUNCTION tinh_tien_giam_gia
(
    @id_phieu INT,
    @tong_tien DECIMAL(18,2)
)
RETURNS DECIMAL(18,2)
AS
BEGIN
    DECLARE @loai INT,
            @gia_tri DECIMAL(18,2),
            @don_toi_thieu DECIMAL(18,2),
            @giam_toi_da DECIMAL(18,2),
            @giam DECIMAL(18,2);

    SELECT @loai = loai_giam_gia,
           @gia_tri = gia_tri_giam,
           @don_toi_thieu = don_toi_thieu,
           @giam_toi_da = giam_toi_da
    FROM phieu_giam_gia
    WHERE id_phieu_giam_gia = @id_phieu;

    -- Không đủ điều kiện đơn tối thiểu
    IF (@tong_tien < @don_toi_thieu)
        RETURN 0;

    -- Loại 0 = giảm theo %
    IF (@loai = 0)
        SET @giam = @tong_tien * (@gia_tri / 100.0);

    -- Loại 1 = giảm số tiền cố định
    IF (@loai = 1)
        SET @giam = @gia_tri;

    -- Giới hạn giảm tối đa
    IF (@giam > @giam_toi_da)
        SET @giam = @giam_toi_da;

    RETURN @giam;
END;
GO

