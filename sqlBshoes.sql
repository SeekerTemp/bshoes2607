--update 08/12
--Insert dữ liệu hóa đơn mẫu

--USE master;
--GO
--ALTER DATABASE BShoes
--SET SINGLE_USER
--WITH ROLLBACK IMMEDIATE;
--GO
--DROP DATABASE BShoes
--GO
--create database BShoes;
--go
use BShoes;
go
select db_name() as CurrentDatabase;
--1. khai bao bang

-- Bảng 1.1 LOẠI SẢN PHẨM
create table loai_san_pham (
    id_loai_san_pham int identity(1,1) primary key,
    ma_loai_san_pham varchar(20) unique,
    ten_loai_san_pham nvarchar(100),
    mo_ta nvarchar(255),
    trang_thai bit,
    trang_thai_xoa bit default 0
);
go
-- Bảng 1.2 CHẤT LIỆU
create table chat_lieu (
    id_chat_lieu int identity(1,1) primary key,
    ma_chat_lieu varchar(20) unique,
    ten_chat_lieu nvarchar(100),
    trang_thai bit,
    trang_thai_xoa bit default 0
);
go
--Bảng 1.3 KIỂU DÁNG
create table kieu_dang (
    id_kieu_dang int identity(1,1) primary key,
    ma_kieu_dang varchar(20) unique,
    ten_kieu_dang nvarchar(100),
    trang_thai bit,
    trang_thai_xoa bit default 0
);
go
--Bảng 1.4 KIỂU CỔ GIÀY
create table kieu_co_giay (
    id_kieu_co_giay int identity(1,1) primary key,
    ma_co_giay varchar(20) unique,
    ten_co_giay nvarchar(100),
    trang_thai bit,
    trang_thai_xoa bit default 0
);
go

--Bảng 1.5 KIỂU DÂY GIÀY
create table kieu_day_giay (
    id_kieu_day_giay int identity(1,1) primary key,
    ma_day_giay varchar(20) unique,
    ten_day_giay nvarchar(100),
    trang_thai bit,
    trang_thai_xoa bit default 0
);
go

--Bảng 1.6 THƯƠNG HIỆU
create table thuong_hieu (
    id_thuong_hieu int identity(1,1) primary key,
    ma_thuong_hieu varchar(20) unique,
    ten_thuong_hieu nvarchar(100),
    mo_ta nvarchar(255),
    trang_thai bit,
    trang_thai_xoa bit default 0
);
go
--Bảng 1.7 XUẤT XỨ
create table xuat_su (
    id_xuat_su int identity(1,1) primary key,
    ma_xuat_su varchar(20) unique,
    ten_xuat_su nvarchar(100),
    mo_ta nvarchar(255),
    trang_thai bit,
    trang_thai_xoa bit default 0
);
go
--Bảng 1.8 SẢN PHẨM 
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
--Bảng 1.9 KÍCH CỠ
create table kich_co (
    id_kich_co int identity(1,1) primary key,
    ma_kich_co varchar(20),
    ten_kich_co nvarchar(50),
    trang_thai bit,
    trang_thai_xoa bit default 0
);
go
select * from kich_co;

--Bảng 1.10 MÀU SẮC
create table mau_sac (
    id_mau_sac int identity(1,1) primary key,
    ma_mau_sac varchar(20),
    ten_mau_sac nvarchar(50),
    trang_thai bit,
    trang_thai_xoa bit default 0
);
go
--Bảng 1.11 san_pham_chi_tiet --hoàn tất toàn bộ thông tin về sản phẩm
create table san_pham_chi_tiet (
    id_san_pham_chi_tiet int identity(1,1) primary key,
    id_san_pham int,
    id_kich_co int,
    id_mau_sac int,
    ma_san_pham_chi_tiet varchar(50),
    so_luong_ton int,
    don_gia money,
    ngay_tao DATETIME default GetDate(),
    ngay_cap_nhat DATETIME default GetDate(),
    nguoi_tao nvarchar(50),
    nguoi_cap_nhat nvarchar(50),
    trang_thai bit,
    trang_thai_xoa bit default 0,
    image_url nvarchar(255),

    foreign key (id_san_pham) references san_pham(id_san_pham),
    foreign key (id_kich_co) references kich_co(id_kich_co),
    foreign key (id_mau_sac) references mau_sac(id_mau_sac)
);
go
--Bảng 1.12 vai_tro
create table vai_tro (
    id_vai_tro int identity(1,1) primary key,
    ma_vai_tro varchar(20) unique,
    ten_vai_tro nvarchar(100),
    trang_thai bit,
    trang_thai_xoa bit default 0
);
go
--Bảng 1.13 nhan_vien
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
--Bảng 1.14 khach_hang
create table khach_hang (
    id_khach_hang int identity(1,1) primary key,
    ma_khach_hang varchar(20) unique,
    ten_khach_hang nvarchar(100),
    gioi_tinh nvarchar(10),
    so_dien_thoai nvarchar(20),
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
--Bảng 1.15 dia_chi -- Hoàn thành khai báo thông tin đơn hàng
create table dia_chi (
    id_dia_chi int identity(1,1) primary key,
    id_khach_hang int ,
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
--Bảng 1.16 phieu_giam_gia
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
--Bảng 1.17 HÓA ĐƠN --Hoàn thành khai báo thông tin hóa đơn
create table hoa_don (
    id_hoa_don int identity(1,1) primary key,
    id_khach_hang int ,
    id_phieu_giam_gia int ,
    ma_hoa_don varchar(20) unique,
    tong_tien_ban_dau money,
    tien_giam_gia money,
    tong_tien_phai_tra money,
    ten_nguoi_nhan nvarchar(100),
    so_dien_thoai nvarchar(20),
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
    foreign key (id_phieu_giam_gia)references phieu_giam_gia(id_phieu_giam_gia),
);
go
--Bảng 1.18 HÓA ĐƠN CHI TIẾT
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
    foreign key (id_hoa_don) references hoa_don(id_hoa_don),
);
go
select * from hoa_don_chi_tiet
--Bảng 1.19 LỊCH SỬ HÓA ĐƠN
create table lich_su_hoa_don (
    id int identity(1,1) primary key,
    id_nhan_vien int ,
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

--2.Tao thu tuc insert - goi tu java
-- 2.1 insert bang loai_san_pham 
create or alter procedure insert_loai_san_pham 
    @p_ma_san_pham varchar(20) ,
    @p_ten_san_pham nvarchar(100),
    @p_mo_ta nvarchar(255),
    @p_trang_thai bit,
    @p_trang_thai_xoa bit
as
begin
        if exists (select 1 from loai_san_pham where ma_loai_san_pham = @p_ma_san_pham)
    begin
        raiserror('Ma loai san pham da ton tai tren he thong!', 16, 1);
        return;
    end;
    insert into loai_san_pham (ma_loai_san_pham,ten_loai_san_pham,mo_ta,trang_thai,trang_thai_xoa)
    values (@p_ma_san_pham,@p_ten_san_pham,@p_mo_ta,@p_trang_thai,@p_trang_thai_xoa);
    select * from loai_san_pham;
end;
go

-- 2.2 insert bang chat_lieu
create or alter procedure insert_chat_lieu 
    @p_ma_chat_lieu varchar(20),
    @p_ten_chat_lieu nvarchar(100),
    @p_trang_thai bit,
    @p_trang_thai_xoa bit

as
begin
        if exists (select 1 from chat_lieu where ma_chat_lieu = @p_ma_chat_lieu)
    begin
        raiserror('Ma chat lieu da ton tai tren he thong!', 16, 1);
        return;
    end;
    insert into chat_lieu (ma_chat_lieu ,ten_chat_lieu,trang_thai,trang_thai_xoa)
    values (@p_ma_chat_lieu ,@p_ten_chat_lieu,@p_trang_thai,@p_trang_thai_xoa);
    select * from chat_lieu;
end;
go

--2.3 insert bang kieu_dang
create or alter procedure insert_kieu_dang
    @p_ma_kieu_dang varchar(20) ,
    @p_ten_kieu_dang nvarchar(100),
    @p_trang_thai bit,
    @p_trang_thai_xoa bit
as
begin
    if exists (select 1 from kieu_dang where ma_kieu_dang = @p_ma_kieu_dang)
    begin
        raiserror('Ma kieu dang da ton tai tren he thong!', 16, 1);
        return;
    end;
    insert into kieu_dang(ma_kieu_dang,ten_kieu_dang,trang_thai,trang_thai_xoa)
    values (@p_ma_kieu_dang,@p_ten_kieu_dang,@p_trang_thai,@p_trang_thai_xoa);
    select * from kieu_dang;
end;
go
--2.4 insert Bảng KIỂU CỔ GIÀY
create or alter procedure insert_kieu_co_giay 
    @p_ma_co_giay varchar(20) ,
    @p_ten_co_giay nvarchar(100),
    @p_trang_thai bit,
    @p_trang_thai_xoa bit
as
begin
    if exists (select 1 from kieu_co_giay where ma_co_giay = @p_ma_co_giay)
    begin
        raiserror('Ma kieu co giay da ton tai tren he thong!', 16, 1);
        return;
    end;
    insert into kieu_co_giay(ma_co_giay ,ten_co_giay,trang_thai,trang_thai_xoa)
    values (@p_ma_co_giay ,@p_ten_co_giay,@p_trang_thai,@p_trang_thai_xoa);
    select * from kieu_co_giay ;
end;
go
--2.5 insert Bảng KIỂU DÂY GIÀY
create or alter procedure insert_kieu_day_giay 
    @p_ma_day_giay varchar(20) ,
    @p_ten_day_giay nvarchar(100),
    @p_trang_thai bit,
    @p_trang_thai_xoa bit
as
begin
    if exists (select 1 from kieu_day_giay where ma_day_giay = @p_ma_day_giay)
    begin
        raiserror('Ma kieu day giay ton tai tren he thong!', 16, 1);
        return;
    end;
    insert into kieu_day_giay(ma_day_giay,ten_day_giay,trang_thai,trang_thai_xoa)
    values (@p_ma_day_giay,@p_ten_day_giay,@p_trang_thai,@p_trang_thai_xoa);
    select * from kieu_day_giay ;
end;
go
--2.6 insert Bảng THƯƠNG HIỆU
create or alter procedure insert_thuong_hieu 
    @p_ma_thuong_hieu varchar(20),
    @p_ten_thuong_hieu nvarchar(100),
    @p_mo_ta nvarchar(255),
    @p_trang_thai bit,
    @p_trang_thai_xoa bit
as
begin
    if exists (select 1 from thuong_hieu where ma_thuong_hieu = @p_ma_thuong_hieu)
    begin
        raiserror('Ma thuong hieu da ton tai tren he thong!', 16, 1);
        return;
    end;
    insert into thuong_hieu(ma_thuong_hieu,ten_thuong_hieu,mo_ta,trang_thai,trang_thai_xoa)
    values (@p_ma_thuong_hieu,@p_ten_thuong_hieu,@p_mo_ta,@p_trang_thai,@p_trang_thai_xoa);
    select * from thuong_hieu;
end;
go
--2.7 insert Bảng XUẤT XỨ
create or alter procedure insert_xuat_su 
    @p_ma_xuat_su varchar(20),
    @p_ten_xuat_su nvarchar(100),
    @p_mo_ta nvarchar(255),
    @p_trang_thai bit,
    @p_trang_thai_xoa bit
as
begin
    if exists (select 1 from xuat_su where ma_xuat_su = @p_ma_xuat_su)
    begin
        raiserror('Ma xuat su da ton tai tren he thong!', 16, 1);
        return;
    end;
    insert into xuat_su (ma_xuat_su,ten_xuat_su,mo_ta,trang_thai,trang_thai_xoa)
    values (@p_ma_xuat_su,@p_ten_xuat_su,@p_mo_ta,@p_trang_thai,@p_trang_thai_xoa);
    select * from xuat_su;
end;
go
--2.8 insert Bảng SẢN PHẨM 
create or alter procedure insert_san_pham 
    @p_id_loai_san_pham int,
    @p_id_chat_lieu int,
    @p_id_kieu_dang int,
    @p_id_kieu_co_giay int,
    @p_id_kieu_day_giay int,
    @p_id_thuong_hieu int,
    @p_id_xuat_su int,
    @p_ma_san_pham varchar(20),
    @p_ten_san_pham NVARCHAR(100),
    @p_mo_ta NVARCHAR(255),
    @p_trang_thai BIT,
    @p_trang_thai_xoa bit
as
begin
    if not exists (select 1 from loai_san_pham where id_loai_san_pham = @p_id_loai_san_pham)
    begin
        raiserror('loai san pham khong ton tai tren he thong!', 16, 1);
        return;
    end;
    if not exists (select 1 from chat_lieu where id_chat_lieu = @p_id_chat_lieu)
    begin
        raiserror('chat lieu khong ton tai tren he thong!', 16, 1);
        return;
    end;
    if not exists (select 1 from kieu_dang where id_kieu_dang = @p_id_kieu_dang)
    begin
        raiserror('kieu dang khong ton tai tren he thong!', 16, 1);
        return;
    end;
    if not exists (select 1 from kieu_co_giay where id_kieu_co_giay = @p_id_kieu_co_giay)
    begin
        raiserror('kieu co giay khong ton tai tren he thong!', 16, 1);
        return;
    end;
    if not exists (select 1 from kieu_day_giay where id_kieu_day_giay = @p_id_kieu_day_giay)
    begin
        raiserror('kieu day giay khong ton tai tren he thong!', 16, 1);
        return;
    end;
    if not exists (select 1 from thuong_hieu where id_thuong_hieu = @p_id_thuong_hieu)
    begin
        raiserror('thuong hieu khong ton tai tren he thong!', 16, 1);
        return;
    end;
    if not exists (select 1 from xuat_su where id_xuat_su = @p_id_xuat_su)
    begin
        raiserror('xuat su khong ton tai tren he thong!', 16, 1);
        return;
    end;
    if exists (select 1 from san_pham where ma_san_pham = @p_ma_san_pham)
    begin
        raiserror('Ma san pham da ton tai tren he thong!', 16, 1);
        return;
    end;
    insert into san_pham (id_loai_san_pham,id_chat_lieu,id_kieu_dang,id_kieu_co_giay,id_kieu_day_giay,id_thuong_hieu,id_xuat_su,ma_san_pham,ten_san_pham,mo_ta,ngay_tao,ngay_cap_nhat,trang_thai,trang_thai_xoa)
    values (@p_id_loai_san_pham,@p_id_chat_lieu,@p_id_kieu_dang,@p_id_kieu_co_giay,@p_id_kieu_day_giay,@p_id_thuong_hieu,@p_id_xuat_su,@p_ma_san_pham,@p_ten_san_pham,@p_mo_ta,GETDATE(),GETDATE(),@p_trang_thai,@p_trang_thai_xoa);
    select * from san_pham;
end;
go

--2.9 insert bang kich_co
create or alter procedure sp_insert_kich_co
    @p_ma_kich_co varchar(20),
    @p_ten_kich_co nvarchar(50),
    @p_trang_thai bit,
    @p_trang_thai_xoa bit
as
begin
    if exists (select 1 from kich_co where ma_kich_co = @p_ma_kich_co)
    begin
        raiserror('Ma kich co da ton tai tren he thong!', 16, 1);
        return;
    end;
    insert into kich_co(ma_kich_co, ten_kich_co, trang_thai,trang_thai_xoa)
    values (@p_ma_kich_co, @p_ten_kich_co, @p_trang_thai,@p_trang_thai_xoa);
    select * from kich_co;
end;
go
--2.10 insert bảng mau_sac
create or alter procedure sp_insert_mau_sac
    @p_ma_mau_sac varchar(20),
    @p_ten_mau_sac nvarchar(50),
    @p_trang_thai bit,
    @p_trang_thai_xoa bit
as
begin
    if exists (select 1 from mau_sac where ma_mau_sac = @p_ma_mau_sac)
    begin
        raiserror('Ma mau sac da ton tai tren he thong!', 16, 1);
        return;
    end;
    insert into mau_sac(ma_mau_sac, ten_mau_sac, trang_thai,trang_thai_xoa)
    values (@p_ma_mau_sac, @p_ten_mau_sac, @p_trang_thai,@p_trang_thai_xoa);
    select * from mau_sac;
end;
go
--2.11 insert bảng san_pham_chi_tiet
create or alter procedure sp_insert_san_pham_chi_tiet
    @p_id_san_pham int,
    @p_id_kich_co int,
    @p_id_mau int,
    @p_ma_spct varchar(50),
    @p_so_luong int,
    @p_don_gia money,
    @p_nguoi_tao nvarchar(50),
    @p_nguoi_cap_nhat nvarchar(50),
    @p_trang_thai bit,
    @p_trang_thai_xoa bit
as
begin
        if not exists (select 1 from san_pham where id_san_pham = @p_id_san_pham)
    begin
        raiserror('San pham khong ton tai tren he thong!', 16, 1);
        return;
    end;
    if not exists (select 1 from kich_co where id_kich_co = @p_id_kich_co)
    begin
        raiserror('kich co khong ton tai tren he thong!', 16, 1);
        return;
    end;
    if not exists (select 1 from mau_sac where id_mau_sac = @p_id_mau)
    begin
        raiserror('mau sac khong ton tai tren he thong!', 16, 1);
        return;
    end;
    if exists (select 1 from san_pham_chi_tiet where ma_san_pham_chi_tiet = @p_ma_spct)
    begin
        raiserror('Ma san pham chi tiet da ton tai tren he thong!', 16, 1);
        return;
    end;
    insert into san_pham_chi_tiet(id_san_pham, id_kich_co, id_mau_sac, ma_san_pham_chi_tiet,so_luong_ton, don_gia, nguoi_tao, nguoi_cap_nhat, trang_thai,trang_thai_xoa)
    values(@p_id_san_pham, @p_id_kich_co, @p_id_mau, @p_ma_spct, @p_so_luong, @p_don_gia, @p_nguoi_tao, @p_nguoi_cap_nhat, @p_trang_thai,@p_trang_thai_xoa);
    select * from san_pham_chi_tiet;
end;
go

--2.12 insert Bảng vai_tro
create or alter procedure sp_insert_vai_tro
    @p_ma_vai_tro varchar(20),
    @p_ten_vai_tro nvarchar(50),
    @p_trang_thai bit,
    @p_trang_thai_xoa bit
as
begin
     if exists (select 1 from vai_tro where ma_vai_tro = @p_ma_vai_tro)
    begin
        raiserror('Ma vai tro da ton tai tren he thong!', 16, 1);
        return;
    end;
    insert into vai_tro(ma_vai_tro, ten_vai_tro, trang_thai,trang_thai_xoa)
    values (@p_ma_vai_tro, @p_ten_vai_tro, @p_trang_thai,@p_trang_thai_xoa);
    select * from vai_tro;
end;
go
--2.13 insert Bảng nhan_vien
create or alter procedure sp_insert_nhan_vien
    @p_id_vai_tro int,
    @p_ma_nhan_vien varchar(20),
    @p_ten_nhan_vien nvarchar(100),
    @p_tai_khoan nvarchar(50),
    @p_email nvarchar(100),
    @p_mat_khau nvarchar(50),
    @p_cccd nvarchar(20),
    @p_so_dien_thoai nvarchar(20),
    @p_dia_chi nvarchar(255),
    @p_chuc_vu nvarchar(50),
    @p_ngay_sinh DATETIME,
    @p_gioi_tinh nvarchar(10),
    @p_nguoi_tao_ma nvarchar(50),
    @p_nguoi_cap_nhat nvarchar(50),
    @p_trang_thai bit,
    @p_trang_thai_xoa bit
as
begin
    if not exists (select 1 from vai_tro where id_vai_tro = @p_id_vai_tro)
    begin
        raiserror('vai tro nhan vien khong ton tai hoac da bi sai!', 16, 1);
        return;
    end;
     if exists (select 1 from nhan_vien where ma_nhan_vien = @p_ma_nhan_vien)
    begin
        raiserror('Ma nhan vien da ton tai tren he thong!', 16, 1);
        return;
    end;
    insert into nhan_vien
    (id_vai_tro, ma_nhan_vien, ten_nhan_vien, tai_khoan, email, mat_khau, cccd, so_dien_thoai, dia_chi, chuc_vu, ngay_sinh, gioi_tinh, nguoi_tao_ma, nguoi_cap_nhat, ngay_tao_ma, ngay_cap_nhat, trang_thai,trang_thai_xoa)
    values
    (@p_id_vai_tro, @p_ma_nhan_vien, @p_ten_nhan_vien, @p_tai_khoan, @p_email, @p_mat_khau, @p_cccd, @p_so_dien_thoai, @p_dia_chi, @p_chuc_vu, @p_ngay_sinh, @p_gioi_tinh, @p_nguoi_tao_ma, @p_nguoi_cap_nhat, GETDATE(), GETDATE(), @p_trang_thai,@p_trang_thai_xoa);

    select * from nhan_vien;
end;
go

--2.14 insert Bảng khach_hang
create or alter procedure sp_insert_khach_hang
    @ma_khach_hang varchar(20),
    @ten_khach_hang nvarchar(100),
    @gioi_tinh nvarchar(10),
    @so_dien_thoai nvarchar(20),
    @dia_chi nvarchar(255),
    @email nvarchar(100),
    @nguoi_tao_ma nvarchar(50),
    @nguoi_cap_nhat nvarchar(50),
    @ngay_tao_ma DATETIME,
    @ngay_cap_nhat DATETIME,
    @trang_thai bit,
    @p_trang_thai_xoa bit
as
begin
     if exists (select 1 from khach_hang where ma_khach_hang = @ma_khach_hang)
    begin
        raiserror('Ma khach hang da ton tai tren he thong!', 16, 1);
        return;
    end;
    insert into khach_hang(ma_khach_hang, ten_khach_hang, gioi_tinh, so_dien_thoai ,dia_chi, email, nguoi_tao_ma, nguoi_cap_nhat, ngay_tao_ma, ngay_cap_nhat, trang_thai,trang_thai_xoa)
    values (@ma_khach_hang,@ten_khach_hang,@gioi_tinh,@so_dien_thoai,@dia_chi,@email,@nguoi_tao_ma,@nguoi_cap_nhat,@ngay_tao_ma,@ngay_cap_nhat, @trang_thai,@p_trang_thai_xoa);
    select * from khach_hang;
end;
go

--2.15 insert Bảng dia_chi
create or alter procedure sp_insert_dia_chi
    @id_khach_hang int ,
    @dia_chi_mac_dinh nvarchar(255),
    @thanh_pho nvarchar(50),
    @phuong nvarchar(50),
    @dia_chi_them nvarchar(255),
    @nguoi_tao nvarchar(50),
    @nguoi_cap_nhat nvarchar(50),
    @ngay_tao DATETIME,
    @ngay_cap_nhat DATETIME,
    @trang_thai bit,
    @p_trang_thai_xoa bit
as
begin
    if not exists (select 1 from khach_hang where id_khach_hang = @id_khach_hang)
    begin
        raiserror('khach hang khong ton tai tren he thong!', 16, 1);
        return;
    end;
    insert into dia_chi(id_khach_hang,dia_chi_mac_dinh,thanh_pho,phuong,dia_chi_them,nguoi_tao,nguoi_cap_nhat,ngay_tao,ngay_cap_nhat,trang_thai,trang_thai_xoa)
    values (@id_khach_hang,@dia_chi_mac_dinh,@thanh_pho,@phuong,@dia_chi_them,@nguoi_tao,@nguoi_cap_nhat,@ngay_tao,@ngay_cap_nhat,@trang_thai,@p_trang_thai_xoa);
    select * from dia_chi;
end;
go

--2.16 insert Bảng phieu_giam_gia
create or alter procedure sp_insert_phieu_giam_gia
    @p_ma_phieu_giam varchar(20),
    @p_ten_phieu_giam nvarchar(100),
    @p_loai_giam_gia int,
    @p_gia_tri_giam money,
    @p_don_toi_thieu money,
    @p_giam_toi_da money,
    @p_so_luong int,
    @p_thoi_gian_bat_dau DATETIME,
    @p_thoi_gian_ket_thuc DATETIME,
    @p_nguoi_tao_ma nvarchar(50),
    @p_nguoi_cap_nhat nvarchar(50),
    @p_trang_thai bit,
    @p_trang_thai_xoa bit
as
begin
         if exists (select 1 from phieu_giam_gia where ma_phieu_giam = @p_ma_phieu_giam)
    begin
        raiserror('Ma phieu giam da ton tai tren he thong!', 16, 1);
        return;
    end;
    insert into phieu_giam_gia (ma_phieu_giam, ten_phieu_giam, loai_giam_gia, gia_tri_giam, don_toi_thieu, giam_toi_da, so_luong, thoi_gian_bat_dau, thoi_gian_ket_thuc, nguoi_tao_ma, nguoi_cap_nhat, ngay_tao_ma, ngay_cap_nhat, trang_thai,trang_thai_xoa)
    values (@p_ma_phieu_giam, @p_ten_phieu_giam, @p_loai_giam_gia, @p_gia_tri_giam, @p_don_toi_thieu, @p_giam_toi_da, @p_so_luong, @p_thoi_gian_bat_dau, @p_thoi_gian_ket_thuc, @p_nguoi_tao_ma, @p_nguoi_cap_nhat, GETDATE(), GETDATE(), @p_trang_thai,@p_trang_thai_xoa);

    select * from phieu_giam_gia;
end;
go

--2.17 insert bang hoa don
create or alter procedure sp_insert_hoa_don
    @p_id_khach_hang int ,
    @p_id_phieu_giam_gia int ,
    @p_ma_hoa_don varchar(20) ,
    @p_tong_tien_ban_dau money,
    @p_tien_giam_gia money,
    @p_tong_tien_phai_tra money,
    @p_ten_nguoi_nhan nvarchar(100),
    @p_so_dien_thoai nvarchar(20),
    @p_dia_chi nvarchar(255),
    @p_phuong_thuc_thanh_toan nvarchar(50),
    @p_ghi_chu nvarchar(255),
    @p_nguoi_tao_ma nvarchar(50),
    @p_nguoi_cap_nhat nvarchar(50),
    @p_ngay_tao_ma DATETIME,
    @p_ngay_cap_nhat DATETIME,
    @p_trang_thai int,
	@p_loai_hoa_don bit
as
begin
    insert into hoa_don(id_khach_hang,id_phieu_giam_gia,ma_hoa_don,tong_tien_ban_dau,tien_giam_gia,tong_tien_phai_tra,ten_nguoi_nhan,so_dien_thoai,dia_chi,phuong_thuc_thanh_toan,ghi_chu,nguoi_tao_ma,nguoi_cap_nhat,ngay_tao_ma,ngay_cap_nhat,trang_thai,loai_hoa_don)
    values (@p_id_khach_hang,@p_id_phieu_giam_gia,@p_ma_hoa_don,@p_tong_tien_ban_dau,@p_tien_giam_gia,@p_tong_tien_phai_tra,@p_ten_nguoi_nhan,@p_so_dien_thoai,@p_dia_chi,@p_phuong_thuc_thanh_toan,@p_ghi_chu,@p_nguoi_tao_ma,@p_nguoi_cap_nhat,@p_ngay_tao_ma,@p_ngay_cap_nhat,@p_trang_thai,@p_loai_hoa_don);
    select * from hoa_don;
end;
go

--2.18 insert hoa_don_chi_tiet
create or alter procedure sp_insert_hoa_don_chi_tiet
    @id_san_pham_chi_tiet int,
    @id_hoa_don int,
    @gia_giam money,
    @so_luong int,
    @ngay_tao_ma DATETIME,
    @ngay_cap_nhat DATETIME,
    @nguoi_tao nvarchar(50),
    @nguoi_cap_nhat nvarchar(50),
    @trang_thai bit,
    @p_trang_thai_xoa bit,
    @p_thanh_tien money
as
begin
        if not exists (select 1 from san_pham_chi_tiet where id_san_pham_chi_tiet = @id_san_pham_chi_tiet)
    begin
        raiserror('san pham chi tiet khong ton tai tren he thong!', 16, 1);
        return;
    end;
    if not exists (select 1 from hoa_don where id_hoa_don = @id_hoa_don)
    begin
        raiserror('hoa don  khong ton tai tren he thong!', 16, 1);
        return;
    end;
    insert into hoa_don_chi_tiet(id_san_pham_chi_tiet,id_hoa_don,gia_giam,so_luong,ngay_tao_ma,ngay_cap_nhat,nguoi_tao,nguoi_cap_nhat,trang_thai,trang_thai_xoa,thanh_tien)
    values (@id_san_pham_chi_tiet,@id_hoa_don,@gia_giam,@so_luong,@ngay_tao_ma,@ngay_cap_nhat,@nguoi_tao,@nguoi_cap_nhat,@trang_thai,@p_trang_thai_xoa,@p_thanh_tien);
    select * from hoa_don_chi_tiet;
end;
go

--2.19 insert Bảng lich_su_hoa_don -- Dùng trigger để sinh tự động mỗi khi thêm hoặc hủy hóa đơn.
--CREATE OR ALTER PROCEDURE sp_insert_lich_su_hoa_don
--    @p_id_nhan_vien INT,
--    @p_id_hoa_don INT,
--    @p_ghi_chu NVARCHAR(255),
--    @p_nguoi_tao_ma NVARCHAR(50),
--    @p_nguoi_cap_nhat NVARCHAR(50),
--    @p_trang_thai BIT
--AS
--BEGIN
--    INSERT INTO lich_su_hoa_don (id_nhan_vien,id_hoa_don,ghi_chu,thoi_gian_thay_doi,nguoi_tao_ma,nguoi_cap_nhat,ngay_tao_ma,ngay_cap_nhat,trang_thai)
--    VALUES (@p_id_nhan_vien,@p_id_hoa_don,@p_ghi_chu,GETDATE(),@p_nguoi_tao_ma,@p_nguoi_cap_nhat,GETDATE(),GETDATE(),@p_trang_thai);

--    -- Trả về kết quả vừa thêm
--    SELECT * FROM lich_su_hoa_don WHERE id = SCOPE_IDENTITY();
--END;
--GO

--3.insert du lieu
--3.1 insert bang loai_san_pham @p_ma_san_pham,@p_ten_san_pham,@p_mo_ta,@p_trang_thai
exec insert_loai_san_pham 'LSP01', N'Giày thể thao', N'Giày dành cho hoạt động thể thao', 1,0;
exec insert_loai_san_pham 'LSP02', N'Giày da', N'Giày công sở làm bằng da thật', 1,0;
exec insert_loai_san_pham 'LSP03', N'Sandal', N'Giày dép quai hậu thoáng mát', 1,0;
exec insert_loai_san_pham 'LSP04', N'Dép thời trang', N'Dép mang trong nhà hoặc đi dạo', 1,0;
exec insert_loai_san_pham 'LSP05', N'Giày tây', N'Giày sang trọng phù hợp môi trường công sở', 1,0;
exec insert_loai_san_pham 'LSP06', N'Giày chạy bộ', N'Giày nhẹ, êm, dành cho chạy bộ', 1,0;
go
--3.2 insert bang chat_lieu @p_ma_chat_lieu ,@p_ten_chat_lieu,@p_trang_thai
exec insert_chat_lieu 'CL01', N'Da thật', 1,0;
exec insert_chat_lieu 'CL02', N'Vải canvas', 1,0;
exec insert_chat_lieu 'CL03', N'Da tổng hợp', 1,0;
EXEC insert_chat_lieu 'CL04', N'Vải lưới', 1,0;
EXEC insert_chat_lieu 'CL05', N'Nỉ', 1,0;
EXEC insert_chat_lieu 'CL06', N'Cao su', 1,0;
go
--3.3 insert bang kieu_dang @p_ma_kieu_dang,@p_ten_kieu_dang,@p_trang_thai
exec insert_kieu_dang 'KD01', N'Cổ thấp', 1,0;
exec insert_kieu_dang 'KD02', N'Cổ cao', 1,0;
exec insert_kieu_dang 'KD03', N'Slip-on', 1,0;
exec insert_kieu_dang 'KD04', N'Sneaker', 1,0;
exec insert_kieu_dang 'KD05', N'Boots', 1,0;
exec insert_kieu_dang 'KD06', N'Moccasin', 1,0;
go
--3.4 insert Bảng KIỂU CỔ GIÀY @p_ma_co_giay ,@p_ten_co_giay,@p_trang_thai
exec insert_kieu_co_giay 'KC01', N'Cổ trơn', 1,0;
exec insert_kieu_co_giay 'KC02', N'Cổ chun', 1,0;
exec insert_kieu_co_giay 'KC03', N'Cổ bo', 1,0;
exec insert_kieu_co_giay 'KC04', N'Cổ gập', 1,0;
exec insert_kieu_co_giay 'KC05', N'Cổ cao su', 1,0;
exec insert_kieu_co_giay 'KC06', N'Cổ bọc nỉ', 1,0;
go
--3.5 insert Bảng KIỂU DÂY GIÀY @p_ma_day_giay,@p_ten_day_giay,@p_trang_thai
exec insert_kieu_day_giay 'DG01', N'Dây bản nhỏ', 1,0;
exec insert_kieu_day_giay 'DG02', N'Dây bản to', 1,0;
exec insert_kieu_day_giay 'DG03', N'Dây tròn', 1,0;
exec insert_kieu_day_giay 'DG04', N'Dây dẹt', 1,0;
exec insert_kieu_day_giay 'DG05', N'Dây co giãn', 1,0;
exec insert_kieu_day_giay 'DG06', N'Dây thun', 1,0;
go
--3.6 insert Bảng THƯƠNG HIỆU @p_ma_thuong_hieu,@p_ten_thuong_hieu,@p_mo_ta,@p_trang_thai
exec insert_thuong_hieu 'TH01', N'Nike', N'Thương hiệu giày thể thao nổi tiếng', 1,0;
exec insert_thuong_hieu 'TH02', N'Adidas', N'Giày thể thao và lifestyle', 1,0;
exec insert_thuong_hieu 'TH03', N'Puma', N'Thương hiệu thể thao toàn cầu', 1,0;
exec insert_thuong_hieu 'TH04', N'Gucci', N'Thương hiệu giày cao cấp', 1,0;
exec insert_thuong_hieu 'TH05', N'Vans', N'Giày Skate & Casual', 1,0;
exec insert_thuong_hieu 'TH06', N'Reebok', N'Giày thể thao cổ điển', 1,0;
go
--3.7 insert Bảng XUẤT XỨ @p_ma_xuat_su,@p_ten_xuat_su,@p_mo_ta,@p_trang_thai
exec insert_xuat_su 'XX01', N'Việt Nam', N'Sản xuất trong nước', 1,0;
exec insert_xuat_su 'XX02', N'Mỹ', N'Hàng nhập khẩu từ Mỹ', 1,0;
exec insert_xuat_su 'XX03', N'Nhật Bản', N'Chất lượng cao từ Nhật', 1,0;
exec insert_xuat_su 'XX04', N'Hàn Quốc', N'Xuất xứ Hàn Quốc', 1,0;
exec insert_xuat_su 'XX05', N'Đức', N'Hàng sản xuất tại Đức', 1,0;
exec insert_xuat_su 'XX06', N'Italia', N'Hàng nhập khẩu Ý', 1,0;
go
--3.8 insert Bảng SẢN PHẨM @p_id_loai_san_pham,@p_id_chat_lieu,@p_id_kieu_dang,@p_id_kieu_co_giay,@p_id_kieu_day_giay,@p_id_thuong_hieu,@p_id_xuat_su,@p_ma_san_pham,@p_ten_san_pham,@p_mo_ta,GETDATE(),GETDATE(),@p_trang_thai
exec insert_san_pham 1, 1, 1, 1, 1, 1, 1,'SP01', N'Giày thể thao Nike', N'Giày chạy bộ nhẹ, êm chân', 1,0;
exec insert_san_pham 2, 2, 2, 2, 2, 2, 2,'SP02', N'Giày da Adidas', N'Giày công sở da thật', 1,0;
exec insert_san_pham 3, 3, 3, 3, 3, 3, 3,'SP03', N'Sandal Puma', N'Sandal thoáng mát', 1,0;
exec insert_san_pham 4, 4, 4, 4, 4, 4, 4,'SP04', N'Dép Gucci', N'Dép thời trang cao cấp', 1,0;
exec insert_san_pham 5, 5, 5, 5, 5, 5, 5,'SP05', N'Giày tây Vans', N'Giày sang trọng đi làm', 1,0;
exec insert_san_pham 6, 6, 6, 6, 6, 6, 6,'SP06', N'Giày chạy bộ Reebok', N'Giày thể thao cổ điển', 1,0;
go
--3.9 insert bang kich_co @p_ma_kich_co, @p_ten_kich_co, @p_trang_thai
exec sp_insert_kich_co 'S', 'Size S', 1,0;
exec sp_insert_kich_co 'M', 'Size M', 1,0;
exec sp_insert_kich_co 'L', 'Size L', 1,0;
exec sp_insert_kich_co 'XL', 'Size XL', 1,0;
exec sp_insert_kich_co 'XXL', 'Size XXL', 1,0;
exec sp_insert_kich_co 'XS', 'Size XS', 1,0;
go
--3.10 insert bảng mau_sac @p_ma_mau_sac, @p_ten_mau_sac, @p_trang_thai
exec sp_insert_mau_sac 'RD', N'Đỏ', 1,0;
exec sp_insert_mau_sac 'BL', N'Xanh biển', 1,0;
exec sp_insert_mau_sac 'BK', N'Đen', 1,0;
exec sp_insert_mau_sac 'WH', N'Trắng', 1,0;
exec sp_insert_mau_sac 'YL', N'Vàng', 1,0;
exec sp_insert_mau_sac 'GN', N'Xanh lá', 1,0;
go

--3.11 insert bảng san_pham_chi_tiet @p_id_san_pham, @p_id_kich_co, @p_id_mau, @p_ma_spct, @p_so_luong, @p_don_gia, @p_nguoi_tao, @p_nguoi_cap_nhat, @p_trang_thai
exec sp_insert_san_pham_chi_tiet 1, 1, 1, 'SPCT001', 20, 200000, 'admin', 'admin', 1,0;
exec sp_insert_san_pham_chi_tiet 2, 2, 2, 'SPCT002', 30, 250000, 'admin', 'admin', 1,0;
exec sp_insert_san_pham_chi_tiet 3, 3, 3, 'SPCT003', 25, 320000, 'admin', 'admin', 1,0;
exec sp_insert_san_pham_chi_tiet 4, 4, 4, 'SPCT004', 15, 350000, 'admin', 'admin', 1,0;
exec sp_insert_san_pham_chi_tiet 5, 5, 5, 'SPCT005', 40, 400000, 'admin', 'admin', 1,0;
exec sp_insert_san_pham_chi_tiet 6, 6, 6, 'SPCT006', 10, 450000, 'admin', 'admin', 1,0;
go

--3.12 insert Bảng vai_tro @p_ma_vai_tro, @p_ten_vai_tro, @p_trang_thai
exec sp_insert_vai_tro 'ADMIN', N'Quản trị', 1,0;
exec sp_insert_vai_tro 'QL', N'Quản lý', 1,0;
exec sp_insert_vai_tro 'NV', N'Nhân viên bán hàng', 1,0;
exec sp_insert_vai_tro 'KT', N'Kế toán', 1,0;
exec sp_insert_vai_tro 'BH', N'Bảo hành', 1,0;
exec sp_insert_vai_tro 'NK', N'Nhập kho', 1,0;
go
--3.13 insert Bảng nhan_vien @p_id_vai_tro, @p_ma_nhan_vien, @p_ten_nhan_vien, @p_tai_khoan, @p_email, @p_mat_khau, @p_cccd, @p_so_dien_thoai, @p_dia_chi, @p_chuc_vu, @p_ngay_sinh, @p_gioi_tinh, @p_nguoi_tao_ma, @p_nguoi_cap_nhat, GETDATE(), GETDATE(), @p_trang_thai 
exec sp_insert_nhan_vien 1, N'NV001', N'Nguyễn Văn A', N'vanan', N'vana@p_example.com', N'123456', N'123456789', N'0912345678', N'Hà Nội', N'Nhân viên', '1990-01-01', N'Nam', N'admin', N'admin', 1,0;
exec sp_insert_nhan_vien 3, N'NV002', N'Lê Thị B', N'thib', N'thib@p_example.com', N'123456', N'987654321', N'0987654321', N'Hải Phòng', N'Nhân viên', '1992-02-02', N'Nữ', N'admin', N'admin', 1,0;
exec sp_insert_nhan_vien 1, N'NV003', N'Trần Văn C', N'vanc', N'vanc@p_example.com', N'123456', N'111222333', N'0911222333', N'Đà Nẵng', N'Nhân viên', '1991-03-03', N'Nam', N'admin', N'admin', 1,0;
exec sp_insert_nhan_vien 3, N'NV004', N'Phạm Thị D', N'thid', N'thid@p_example.com', N'123456', N'444555666', N'0944555666', N'Quảng Ninh', N'Nhân viên', '1993-04-04', N'Nữ', N'admin', N'admin', 1,0;
exec sp_insert_nhan_vien 1, N'NV005', N'Ngô Văn E', N'vane', N'vane@p_example.com', N'123456', N'777888999', N'0977888999', N'Hà Nam', N'Nhân viên', '1990-05-05', N'Nam', N'admin', N'admin', 1,0;
exec sp_insert_nhan_vien 3, N'NV006', N'Hồ Thị F', N'thif', N'thif@p_example.com', N'123456', N'000111222', N'0900111222', N'Hải Dương', N'Nhân viên', '1994-06-06', N'Nữ', N'admin', N'admin', 1,0;
go
--3.14 insert Khach_hang
exec sp_insert_khach_hang N'KH01', N'Nguyễn Trung Nghĩa', N'Nam', '0968291160', N'Hải Dương', N'nghia@gmail.com', N'admin', N'admin', '2025-11-09', '2025-11-09', 1,0;
exec sp_insert_khach_hang N'KH07', N'Ngô Văn Hùng', N'Nam', '0941234567', N'Quảng Ninh', N'hungnv@gmail.com', N'admin', N'admin', '2025-11-09', '2025-11-09', 1,0;
exec sp_insert_khach_hang N'KH08', N'Đặng Thị Hồng', N'Nữ', '0923456789', N'Bắc Ninh', N'hongdt@gmail.com', N'admin', N'admin', '2025-11-09', '2025-11-09', 1,0;
exec sp_insert_khach_hang N'KH09', N'Phan Minh Tuấn', N'Nam', '0956789123', N'Hải Dương', N'tuanpm@gmail.com', N'admin', N'admin', '2025-11-09', '2025-11-09', 1,0;
exec sp_insert_khach_hang N'KH10', N'Trương Thị Lan', N'Nữ', '0919876543', N'Hà Nội', N'lantr@gmail.com', N'admin', N'admin', '2025-11-09', '2025-11-09', 1,0;
exec sp_insert_khach_hang N'KH11', N'Lê Văn Sơn', N'Nam', '0965432198', N'Hồ Chí Minh', N'sonlv@gmail.com', N'admin', N'admin', '2025-11-09', '2025-11-09', 1,0;
go
--3.15 insert dia_chi @id_khach_hang,@dia_chi_mac_dinh,@thanh_pho,@phuong,@dia_chi_them,@nguoi_tao,@nguoi_cap_nhat,@ngay_tao,@ngay_cap_nhat,@trang_thai
EXEC sp_insert_dia_chi @id_khach_hang=1,@dia_chi_mac_dinh=N'Số 12, Đường Nguyễn Lương Bằng, TP Hải Dương',@thanh_pho=N'Hải Dương',@phuong=N'Ngọc Châu',@dia_chi_them=N'Căn hộ 202 - Chung cư Lê Thanh Nghị',@nguoi_tao=N'admin',@nguoi_cap_nhat=N'admin',@ngay_tao='2025-11-09',@ngay_cap_nhat='2025-11-09',@trang_thai=1,@p_trang_thai_xoa=0;
EXEC sp_insert_dia_chi @id_khach_hang=2,@dia_chi_mac_dinh=N'Số 5, Trần Phú, TP Hạ Long',@thanh_pho=N'Quảng Ninh',@phuong=N'Bạch Đằng',@dia_chi_them=N'Gần quảng trường 30/10',@nguoi_tao=N'admin',@nguoi_cap_nhat=N'admin',@ngay_tao='2025-11-09',@ngay_cap_nhat='2025-11-09',@trang_thai=1,@p_trang_thai_xoa=0;
EXEC sp_insert_dia_chi @id_khach_hang=3,@dia_chi_mac_dinh=N'25 Nguyễn Cao, TP Bắc Ninh',@thanh_pho=N'Bắc Ninh',@phuong=N'Ninh Xá',@dia_chi_them=N'Cạnh chợ Đọ Xá',@nguoi_tao=N'admin',@nguoi_cap_nhat=N'admin',@ngay_tao='2025-11-09',@ngay_cap_nhat='2025-11-09',@trang_thai=1,@p_trang_thai_xoa=0;
EXEC sp_insert_dia_chi @id_khach_hang=4,@dia_chi_mac_dinh=N'123 Nguyễn Văn Linh, Hải Dương',@thanh_pho=N'Hải Dương',@phuong=N'Tân Bình',@dia_chi_them=N'Nhà riêng, gần siêu thị Big C',@nguoi_tao=N'admin',@nguoi_cap_nhat=N'admin',@ngay_tao='2025-11-09',@ngay_cap_nhat='2025-11-09',@trang_thai=1,@p_trang_thai_xoa=0;
EXEC sp_insert_dia_chi @id_khach_hang=5,@dia_chi_mac_dinh=N'89 Láng Hạ, Đống Đa, Hà Nội',@thanh_pho=N'Hà Nội',@phuong=N'Láng Hạ',@dia_chi_them=N'Tầng 5, Tòa nhà Vinaconex 9',@nguoi_tao=N'admin',@nguoi_cap_nhat=N'admin',@ngay_tao='2025-11-09',@ngay_cap_nhat='2025-11-09',@trang_thai=1,@p_trang_thai_xoa=0;
EXEC sp_insert_dia_chi @id_khach_hang=6,@dia_chi_mac_dinh=N'15 Nguyễn Thị Minh Khai, Quận 1',@thanh_pho=N'Hồ Chí Minh',@phuong=N'Bến Nghé',@dia_chi_them=N'Gần Nhà thờ Đức Bà',@nguoi_tao=N'admin',@nguoi_cap_nhat=N'admin',@ngay_tao='2025-11-09',@ngay_cap_nhat='2025-11-09',@trang_thai=1,@p_trang_thai_xoa=0;


--3.16 insert Bảng phieu_giam_gia @p_ma_phieu_giam, @p_ten_phieu_giam, @p_loai_giam_gia, @p_gia_tri_giam, @p_don_toi_thieu, @p_giam_toi_da, @p_so_luong, @p_thoi_gian_bat_dau, @p_thoi_gian_ket_thuc, @p_nguoi_tao_ma, @p_nguoi_cap_nhat, GETDATE(), GETDATE(), @p_trang_thai
EXEC sp_insert_phieu_giam_gia N'PGG001', N'Giảm giá dịp lễ', 1, 10, 500000, 100000, 50, '2025-11-08', '2025-11-30', N'admin', N'admin', 1,0;
EXEC sp_insert_phieu_giam_gia N'PGG002', N'Giảm giá cuối tuần',0, 20000, 100000, 50000, 100, '2025-11-09', '2025-11-10', N'admin', N'admin', 1,0;
EXEC sp_insert_phieu_giam_gia N'PGG003', N'Khuyến mãi đầu tháng', 1, 15, 300000, 80000, 70, '2025-11-01', '2025-11-07', N'admin', N'admin', 1,0;
EXEC sp_insert_phieu_giam_gia N'PGG004', N'Flash sale',0, 50000, 200000, 50000, 30, '2025-11-08', '2025-11-08', N'admin', N'admin', 1,0;
EXEC sp_insert_phieu_giam_gia N'PGG005', N'Giảm giá sinh nhật', 1, 20, 400000, 120000, 60, '2025-11-15', '2025-11-15', N'admin', N'admin', 1,0;
EXEC sp_insert_phieu_giam_gia N'PGG006', N'Mua 1 tặng 1',0, 0, 0, 0, 20, '2025-11-20', '2025-11-25', N'admin', N'admin', 1,0;
go
--3.17 insert bang hoa don
exec sp_insert_hoa_don 1, 1, 'HD001', 500000, 50000, 450000, N'Nguyễn Văn A', N'0987654321', N'Hà Nội', N'Tiền mặt', N'Mua giày Nike Air', N'Nghia', N'Nghia', '2025-11-12 14:30:00', '2025-11-12 14:30:00', 1, 1;
exec sp_insert_hoa_don 2, 2, 'HD002', 1200000, 200000, 1000000, N'Trần Thị B', N'0912345678', N'Hải Dương', N'Chuyển khoản', N'Mua áo khoác Adidas', N'Nghia', N'Nghia', '2025-11-12 15:00:00', '2025-11-12 15:00:00', 1, 1;
exec sp_insert_hoa_don 3, 1, 'HD003', 800000, 100000, 700000, N'Lê Văn C', N'0978123456', N'Bắc Ninh', N'Tiền mặt', N'Mua balo thời trang', N'Nghia', N'Nghia', '2025-11-12 15:30:00', '2025-11-12 15:30:00', 1, 1;
go

--3.18 insert hoa_don_chi_tiet @p_id_san_pham_chi_tiet,@p_id_hoa_don,@p_gia_giam,@p_so_luong,@p_ngay_tao_ma,@p_ngay_cap_nhat,@p_nguoi_tao,@p_nguoi_cap_nhat,@p_trang_thai,@p_thanh_tien
exec sp_insert_hoa_don_chi_tiet 1, 1, 5000, 2, '2025-11-09', '2025-11-09', N'admin', N'admin', 1,0,50000;
exec sp_insert_hoa_don_chi_tiet 2, 1, 5000, 2, '2025-11-09', '2025-11-09', N'admin', N'admin', 1,0,50000;
exec sp_insert_hoa_don_chi_tiet 3, 1, 5000, 2, '2025-11-09', '2025-11-09', N'admin', N'admin', 1,0,50000;
exec sp_insert_hoa_don_chi_tiet 3, 2, 5000, 2, '2025-11-09', '2025-11-09', N'admin', N'admin', 1,0,50000;
exec sp_insert_hoa_don_chi_tiet 2, 2, 10000, 1, '2025-11-09', '2025-11-09', N'admin', N'admin', 1,0,50000;
exec sp_insert_hoa_don_chi_tiet 2, 2, 0, 3, '2025-11-09', '2025-11-09', N'admin', N'admin', 1,0,50000;
exec sp_insert_hoa_don_chi_tiet 2, 3, 7500, 1, '2025-11-09', '2025-11-09', N'admin', N'admin', 1,0,50000;
exec sp_insert_hoa_don_chi_tiet 3, 3, 4000, 4, '2025-11-09', '2025-11-09', N'admin', N'admin', 1,0,50000;
go
--3.19 insert Bảng lich_su_hoa_don
--exec sp_insert_dia_chi 1, N'123 Đường A', N'Hải Dương', N'Phường 1', N'', N'admin', N'admin', '2025-11-09', '2025-11-09', 1;
--exec sp_insert_dia_chi 2, N'456 Đường B', N'Hà Nội', N'Phường 2', N'', N'admin', N'admin', '2025-11-09', '2025-11-09', 1;
--exec sp_insert_dia_chi 3, N'789 Đường C', N'Hồ Chí Minh', N'Phường 3', N'', N'admin', N'admin', '2025-11-09', '2025-11-09', 1;
--exec sp_insert_dia_chi 4, N'101 Đường D', N'Đà Nẵng', N'Phường 4', N'', N'admin', N'admin', '2025-11-09', '2025-11-09', 1;
--exec sp_insert_dia_chi 5, N'202 Đường E', N'Hải Phòng', N'Phường 5', N'', N'admin', N'admin', '2025-11-09', '2025-11-09', 1;
--exec sp_insert_dia_chi 6, N'303 Đường F', N'Nam Định', N'Phường 6', N'', N'admin', N'admin', '2025-11-09', '2025-11-09', 1;
--go

--3.20 sample data bảng hóa đơn & hóa đơn chi tiết để thống kê:
-- 24 MONTHS DEMO DATA (2024-01 to 2025-12)
-- Each month: 1 hóa_đơn + 3 hóa_đơn_chi_tiết entries
-- Quantity increases 3-5 units per month

-- ===== JANUARY 2024 =====
exec sp_insert_hoa_don 1, 1, 'HD202401001', 600000, 60000, 540000, N'Nguyễn Văn A', N'0987654321', N'Hà Nội', N'Tiền mặt', N'Mua giày Nike Air', N'Nghia', N'Nghia', '2024-01-15 10:30:00', '2024-01-15 10:30:00', 1, 1;
exec sp_insert_hoa_don_chi_tiet 1, 1, 5000, 3, '2024-01-15', '2024-01-15', N'admin', N'admin', 1, 0, 60000;
exec sp_insert_hoa_don_chi_tiet 2, 1, 5000, 3, '2024-01-15', '2024-01-15', N'admin', N'admin', 1, 0, 75000;
exec sp_insert_hoa_don_chi_tiet 3, 1, 5000, 3, '2024-01-15', '2024-01-15', N'admin', N'admin', 1, 0, 96000;

-- ===== FEBRUARY 2024 =====
exec sp_insert_hoa_don 2, 2, 'HD202402001', 1300000, 220000, 1080000, N'Trần Thị B', N'0912345678', N'Hải Dương', N'Chuyển khoản', N'Mua áo khoác Adidas', N'Nghia', N'Nghia', '2024-02-18 14:00:00', '2024-02-18 14:00:00', 1, 1;
exec sp_insert_hoa_don_chi_tiet 1, 2, 5000, 6, '2024-02-18', '2024-02-18', N'admin', N'admin', 1, 0, 120000;
exec sp_insert_hoa_don_chi_tiet 2, 2, 5000, 6, '2024-02-18', '2024-02-18', N'admin', N'admin', 1, 0, 150000;
exec sp_insert_hoa_don_chi_tiet 4, 2, 5000, 6, '2024-02-18', '2024-02-18', N'admin', N'admin', 1, 0, 210000;

-- ===== MARCH 2024 =====
exec sp_insert_hoa_don 3, 1, 'HD202403001', 900000, 110000, 790000, N'Lê Văn C', N'0978123456', N'Bắc Ninh', N'Tiền mặt', N'Mua balo thời trang', N'Nghia', N'Nghia', '2024-03-22 11:15:00', '2024-03-22 11:15:00', 1, 1;
exec sp_insert_hoa_don_chi_tiet 2, 3, 5000, 9, '2024-03-22', '2024-03-22', N'admin', N'admin', 1, 0, 225000;
exec sp_insert_hoa_don_chi_tiet 3, 3, 5000, 9, '2024-03-22', '2024-03-22', N'admin', N'admin', 1, 0, 288000;
exec sp_insert_hoa_don_chi_tiet 5, 3, 5000, 9, '2024-03-22', '2024-03-22', N'admin', N'admin', 1, 0, 360000;

-- ===== APRIL 2024 =====
exec sp_insert_hoa_don 1, 2, 'HD202404001', 1100000, 180000, 920000, N'Phạm Minh Tuấn', N'0956789123', N'Hải Dương', N'Chuyển khoản', N'Mua giày chạy bộ', N'Nghia', N'Nghia', '2024-04-10 09:45:00', '2024-04-10 09:45:00', 1, 1;
exec sp_insert_hoa_don_chi_tiet 3, 4, 5000, 12, '2024-04-10', '2024-04-10', N'admin', N'admin', 1, 0, 384000;
exec sp_insert_hoa_don_chi_tiet 4, 4, 5000, 12, '2024-04-10', '2024-04-10', N'admin', N'admin', 1, 0, 420000;
exec sp_insert_hoa_don_chi_tiet 1, 4, 5000, 12, '2024-04-10', '2024-04-10', N'admin', N'admin', 1, 0, 240000;

-- ===== MAY 2024 =====
exec sp_insert_hoa_don 2, 1, 'HD202405001', 1400000, 240000, 1160000, N'Đặng Thị Hồng', N'0923456789', N'Bắc Ninh', N'Tiền mặt', N'Mua sandal thời trang', N'Nghia', N'Nghia', '2024-05-20 15:30:00', '2024-05-20 15:30:00', 1, 1;
exec sp_insert_hoa_don_chi_tiet 4, 5, 5000, 15, '2024-05-20', '2024-05-20', N'admin', N'admin', 1, 0, 525000;
exec sp_insert_hoa_don_chi_tiet 5, 5, 5000, 15, '2024-05-20', '2024-05-20', N'admin', N'admin', 1, 0, 600000;
exec sp_insert_hoa_don_chi_tiet 2, 5, 5000, 15, '2024-05-20', '2024-05-20', N'admin', N'admin', 1, 0, 375000;

-- ===== JUNE 2024 =====
exec sp_insert_hoa_don 3, 2, 'HD202406001', 1550000, 310000, 1240000, N'Trương Thị Lan', N'0919876543', N'Hà Nội', N'Chuyển khoản', N'Mua giày tây cao cấp', N'Nghia', N'Nghia', '2024-06-12 13:20:00', '2024-06-12 13:20:00', 1, 1;
exec sp_insert_hoa_don_chi_tiet 5, 6, 5000, 18, '2024-06-12', '2024-06-12', N'admin', N'admin', 1, 0, 720000;
exec sp_insert_hoa_don_chi_tiet 6, 6, 5000, 18, '2024-06-12', '2024-06-12', N'admin', N'admin', 1, 0, 810000;
exec sp_insert_hoa_don_chi_tiet 3, 6, 5000, 18, '2024-06-12', '2024-06-12', N'admin', N'admin', 1, 0, 576000;

-- ===== JULY 2024 =====
exec sp_insert_hoa_don 1, 1, 'HD202407001', 1700000, 350000, 1350000, N'Lê Văn Sơn', N'0965432198', N'Hồ Chí Minh', N'Tiền mặt', N'Mua giày Reebok', N'Nghia', N'Nghia', '2024-07-25 16:45:00', '2024-07-25 16:45:00', 1, 1;
exec sp_insert_hoa_don_chi_tiet 1, 7, 5000, 21, '2024-07-25', '2024-07-25', N'admin', N'admin', 1, 0, 420000;
exec sp_insert_hoa_don_chi_tiet 2, 7, 5000, 21, '2024-07-25', '2024-07-25', N'admin', N'admin', 1, 0, 525000;
exec sp_insert_hoa_don_chi_tiet 4, 7, 5000, 21, '2024-07-25', '2024-07-25', N'admin', N'admin', 1, 0, 735000;

-- ===== AUGUST 2024 =====
exec sp_insert_hoa_don 2, 2, 'HD202408001', 1850000, 420000, 1430000, N'Ngô Văn Hùng', N'0941234567', N'Quảng Ninh', N'Chuyển khoản', N'Mua sandal da', N'Nghia', N'Nghia', '2024-08-08 10:15:00', '2024-08-08 10:15:00', 1, 1;
exec sp_insert_hoa_don_chi_tiet 3, 8, 5000, 24, '2024-08-08', '2024-08-08', N'admin', N'admin', 1, 0, 768000;
exec sp_insert_hoa_don_chi_tiet 5, 8, 5000, 24, '2024-08-08', '2024-08-08', N'admin', N'admin', 1, 0, 960000;
exec sp_insert_hoa_don_chi_tiet 6, 8, 5000, 24, '2024-08-08', '2024-08-08', N'admin', N'admin', 1, 0, 1080000;

-- ===== SEPTEMBER 2024 =====
exec sp_insert_hoa_don 3, 1, 'HD202409001', 2000000, 480000, 1520000, N'Hồ Thị Hương', N'0934567890', N'Hải Phòng', N'Tiền mặt', N'Mua giày Nike Pro', N'Nghia', N'Nghia', '2024-09-18 14:30:00', '2024-09-18 14:30:00', 1, 1;
exec sp_insert_hoa_don_chi_tiet 1, 9, 5000, 27, '2024-09-18', '2024-09-18', N'admin', N'admin', 1, 0, 540000;
exec sp_insert_hoa_don_chi_tiet 4, 9, 5000, 27, '2024-09-18', '2024-09-18', N'admin', N'admin', 1, 0, 945000;
exec sp_insert_hoa_don_chi_tiet 2, 9, 5000, 27, '2024-09-18', '2024-09-18', N'admin', N'admin', 1, 0, 675000;

-- ===== OCTOBER 2024 =====
exec sp_insert_hoa_don 1, 2, 'HD202410001', 2150000, 550000, 1600000, N'Võ Thị Ngọc', N'0923789456', N'Đà Nẵng', N'Chuyển khoản', N'Mua giày Adidas Ultra', N'Nghia', N'Nghia', '2024-10-05 11:00:00', '2024-10-05 11:00:00', 1, 1;
exec sp_insert_hoa_don_chi_tiet 2, 10, 5000, 30, '2024-10-05', '2024-10-05', N'admin', N'admin', 1, 0, 750000;
exec sp_insert_hoa_don_chi_tiet 3, 10, 5000, 30, '2024-10-05', '2024-10-05', N'admin', N'admin', 1, 0, 960000;
exec sp_insert_hoa_don_chi_tiet 5, 10, 5000, 30, '2024-10-05', '2024-10-05', N'admin', N'admin', 1, 0, 1200000;

-- ===== NOVEMBER 2024 =====
exec sp_insert_hoa_don 2, 1, 'HD202411001', 2300000, 620000, 1680000, N'Bùi Văn Long', N'0945678901', N'Nam Định', N'Tiền mặt', N'Mua giày chạy bộ', N'Nghia', N'Nghia', '2024-11-14 09:30:00', '2024-11-14 09:30:00', 1, 1;
exec sp_insert_hoa_don_chi_tiet 4, 11, 5000, 33, '2024-11-14', '2024-11-14', N'admin', N'admin', 1, 0, 1155000;
exec sp_insert_hoa_don_chi_tiet 6, 11, 5000, 33, '2024-11-14', '2024-11-14', N'admin', N'admin', 1, 0, 1485000;
exec sp_insert_hoa_don_chi_tiet 1, 11, 5000, 33, '2024-11-14', '2024-11-14', N'admin', N'admin', 1, 0, 660000;

-- ===== DECEMBER 2024 =====
exec sp_insert_hoa_don 3, 2, 'HD202412001', 2450000, 700000, 1750000, N'Đinh Thị Mai', N'0912456789', N'Thái Bình', N'Chuyển khoản', N'Mua giày tây Gucci', N'Nghia', N'Nghia', '2024-12-20 15:45:00', '2024-12-20 15:45:00', 1, 1;
exec sp_insert_hoa_don_chi_tiet 5, 12, 5000, 36, '2024-12-20', '2024-12-20', N'admin', N'admin', 1, 0, 1440000;
exec sp_insert_hoa_don_chi_tiet 3, 12, 5000, 36, '2024-12-20', '2024-12-20', N'admin', N'admin', 1, 0, 1152000;
exec sp_insert_hoa_don_chi_tiet 2, 12, 5000, 36, '2024-12-20', '2024-12-20', N'admin', N'admin', 1, 0, 900000;

-- ===== JANUARY 2025 =====
exec sp_insert_hoa_don 1, 1, 'HD202501001', 2600000, 780000, 1820000, N'Trần Minh Châu', N'0934789012', N'Tuyên Quang', N'Tiền mặt', N'Mua giày Nike Air Max', N'Nghia', N'Nghia', '2025-01-12 10:20:00', '2025-01-12 10:20:00', 1, 1;
exec sp_insert_hoa_don_chi_tiet 1, 13, 5000, 39, '2025-01-12', '2025-01-12', N'admin', N'admin', 1, 0, 780000;
exec sp_insert_hoa_don_chi_tiet 4, 13, 5000, 39, '2025-01-12', '2025-01-12', N'admin', N'admin', 1, 0, 1365000;
exec sp_insert_hoa_don_chi_tiet 6, 13, 5000, 39, '2025-01-12', '2025-01-12', N'admin', N'admin', 1, 0, 1755000;

-- ===== FEBRUARY 2025 =====
exec sp_insert_hoa_don 2, 2, 'HD202502001', 2750000, 850000, 1900000, N'Hoàng Văn Nam', N'0923901234', N'Yên Bái', N'Chuyển khoản', N'Mua Adidas Boost', N'Nghia', N'Nghia', '2025-02-08 13:40:00', '2025-02-08 13:40:00', 1, 1;
exec sp_insert_hoa_don_chi_tiet 2, 14, 5000, 42, '2025-02-08', '2025-02-08', N'admin', N'admin', 1, 0, 1050000;
exec sp_insert_hoa_don_chi_tiet 3, 14, 5000, 42, '2025-02-08', '2025-02-08', N'admin', N'admin', 1, 0, 1344000;
exec sp_insert_hoa_don_chi_tiet 5, 14, 5000, 42, '2025-02-08', '2025-02-08', N'admin', N'admin', 1, 0, 1680000;

-- ===== MARCH 2025 =====
exec sp_insert_hoa_don 3, 1, 'HD202503001', 2900000, 920000, 1980000, N'Vũ Thị Huyền', N'0945012345', N'Lạng Sơn', N'Tiền mặt', N'Mua giày Puma Ultra', N'Nghia', N'Nghia', '2025-03-16 11:55:00', '2025-03-16 11:55:00', 1, 1;
exec sp_insert_hoa_don_chi_tiet 4, 15, 5000, 45, '2025-03-16', '2025-03-16', N'admin', N'admin', 1, 0, 1575000;
exec sp_insert_hoa_don_chi_tiet 1, 15, 5000, 45, '2025-03-16', '2025-03-16', N'admin', N'admin', 1, 0, 900000;
exec sp_insert_hoa_don_chi_tiet 3, 15, 5000, 45, '2025-03-16', '2025-03-16', N'admin', N'admin', 1, 0, 1440000;

-- ===== APRIL 2025 =====
exec sp_insert_hoa_don 1, 2, 'HD202504001', 3050000, 1000000, 2050000, N'Ngô Văn Kiên', N'0912567890', N'Cao Bằng', N'Chuyển khoản', N'Mua giày thể thao', N'Nghia', N'Nghia', '2025-04-22 14:25:00', '2025-04-22 14:25:00', 1, 1;
exec sp_insert_hoa_don_chi_tiet 5, 16, 5000, 48, '2025-04-22', '2025-04-22', N'admin', N'admin', 1, 0, 1920000;
exec sp_insert_hoa_don_chi_tiet 2, 16, 5000, 48, '2025-04-22', '2025-04-22', N'admin', N'admin', 1, 0, 1200000;
exec sp_insert_hoa_don_chi_tiet 6, 16, 5000, 48, '2025-04-22', '2025-04-22', N'admin', N'admin', 1, 0, 2160000;

-- ===== MAY 2025 =====
exec sp_insert_hoa_don 2, 1, 'HD202505001', 3200000, 1080000, 2120000, N'Tô Thị Hà', N'0934678901', N'Bắc Giang', N'Tiền mặt', N'Mua giày Nike Flex', N'Nghia', N'Nghia', '2025-05-10 09:35:00', '2025-05-10 09:35:00', 1, 1;
exec sp_insert_hoa_don_chi_tiet 1, 17, 5000, 51, '2025-05-10', '2025-05-10', N'admin', N'admin', 1, 0, 1020000;
exec sp_insert_hoa_don_chi_tiet 4, 17, 5000, 51, '2025-05-10', '2025-05-10', N'admin', N'admin', 1, 0, 1785000;
exec sp_insert_hoa_don_chi_tiet 2, 17, 5000, 51, '2025-05-10', '2025-05-10', N'admin', N'admin', 1, 0, 1275000;

-- ===== JUNE 2025 =====
exec sp_insert_hoa_don 3, 2, 'HD202506001', 3350000, 1160000, 2190000, N'Nông Văn Tiến', N'0956789012', N'Phú Thọ', N'Chuyển khoản', N'Mua giày cao cấp', N'Nghia', N'Nghia', '2025-06-18 12:10:00', '2025-06-18 12:10:00', 1, 1;
exec sp_insert_hoa_don_chi_tiet 3, 18, 5000, 54, '2025-06-18', '2025-06-18', N'admin', N'admin', 1, 0, 1728000;
exec sp_insert_hoa_don_chi_tiet 5, 18, 5000, 54, '2025-06-18', '2025-06-18', N'admin', N'admin', 1, 0, 2160000;
exec sp_insert_hoa_don_chi_tiet 4, 18, 5000, 54, '2025-06-18', '2025-06-18', N'admin', N'admin', 1, 0, 1890000;

-- ===== JULY 2025 =====
exec sp_insert_hoa_don 1, 1, 'HD202507001', 3500000, 1240000, 2260000, N'Chu Thị Linh', N'0923890123', N'Vĩnh Phúc', N'Tiền mặt', N'Mua giày thời trang', N'Nghia', N'Nghia', '2025-07-25 16:50:00', '2025-07-25 16:50:00', 1, 1;
exec sp_insert_hoa_don_chi_tiet 6, 19, 5000, 57, '2025-07-25', '2025-07-25', N'admin', N'admin', 1, 0, 2565000;
exec sp_insert_hoa_don_chi_tiet 1, 19, 5000, 57, '2025-07-25', '2025-07-25', N'admin', N'admin', 1, 0, 1140000;
exec sp_insert_hoa_don_chi_tiet 3, 19, 5000, 57, '2025-07-25', '2025-07-25', N'admin', N'admin', 1, 0, 1824000;

-- ===== AUGUST 2025 =====
exec sp_insert_hoa_don 2, 2, 'HD202508001', 3650000, 1320000, 2330000, N'Lý Văn Hồng', N'0945901234', N'Hà Tây', N'Chuyển khoản', N'Mua giày chạy marathon', N'Nghia', N'Nghia', '2025-08-05 10:15:00', '2025-08-05 10:15:00', 1, 1;
exec sp_insert_hoa_don_chi_tiet 2, 20, 5000, 60, '2025-08-05', '2025-08-05', N'admin', N'admin', 1, 0, 1500000;
exec sp_insert_hoa_don_chi_tiet 4, 20, 5000, 60, '2025-08-05', '2025-08-05', N'admin', N'admin', 1, 0, 2100000;
exec sp_insert_hoa_don_chi_tiet 5, 20, 5000, 60, '2025-08-05', '2025-08-05', N'admin', N'admin', 1, 0, 2400000;

-- ===== SEPTEMBER 2025 =====
exec sp_insert_hoa_don 3, 1, 'HD202509001', 3800000, 1400000, 2400000, N'Phan Văn Dũng', N'0967012345', N'Hà Nội', N'Tiền mặt', N'Mua giày Vans Pro', N'Nghia', N'Nghia', '2025-09-12 13:45:00', '2025-09-12 13:45:00', 1, 1;
exec sp_insert_hoa_don_chi_tiet 3, 21, 5000, 63, '2025-09-12', '2025-09-12', N'admin', N'admin', 1, 0, 2016000;
exec sp_insert_hoa_don_chi_tiet 6, 21, 5000, 63, '2025-09-12', '2025-09-12', N'admin', N'admin', 1, 0, 2835000;
exec sp_insert_hoa_don_chi_tiet 2, 21, 5000, 63, '2025-09-12', '2025-09-12', N'admin', N'admin', 1, 0, 1575000;

-- ===== OCTOBER 2025 =====
exec sp_insert_hoa_don 1, 2, 'HD202510001', 3950000, 1500000, 2450000, N'Mạch Thị Thanh', N'0912678901', N'Hải Dương', N'Chuyển khoản', N'Mua giày Reebok Classic', N'Nghia', N'Nghia', '2025-10-20 15:20:00', '2025-10-20 15:20:00', 1, 1;
exec sp_insert_hoa_don_chi_tiet 1, 22, 5000, 66, '2025-10-20', '2025-10-20', N'admin', N'admin', 1, 0, 1320000;
exec sp_insert_hoa_don_chi_tiet 5, 22, 5000, 66, '2025-10-20', '2025-10-20', N'admin', N'admin', 1, 0, 2640000;
exec sp_insert_hoa_don_chi_tiet 4, 22, 5000, 66, '2025-10-20', '2025-10-20', N'admin', N'admin', 1, 0, 2310000;

-- ===== NOVEMBER 2025 =====
exec sp_insert_hoa_don 2, 1, 'HD202511001', 4100000, 1600000, 2500000, N'Giang Văn Phú', N'0934789012', N'Bắc Ninh', N'Tiền mặt', N'Mua giày Nike Court', N'Nghia', N'Nghia', '2025-11-08 11:30:00', '2025-11-08 11:30:00', 1, 1;
exec sp_insert_hoa_don_chi_tiet 4, 23, 5000, 69, '2025-11-08', '2025-11-08', N'admin', N'admin', 1, 0, 2415000;
exec sp_insert_hoa_don_chi_tiet 2, 23, 5000, 69, '2025-11-08', '2025-11-08', N'admin', N'admin', 1, 0, 1725000;
exec sp_insert_hoa_don_chi_tiet 6, 23, 5000, 69, '2025-11-08', '2025-11-08', N'admin', N'admin', 1, 0, 3105000;

-- ===== DECEMBER 2025 =====
exec sp_insert_hoa_don 3, 2, 'HD202512001', 4250000, 1700000, 2550000, N'Khương Thị Hương', N'0956890123', N'Quảng Ninh', N'Chuyển khoản', N'Mua giày Gucci Limited', N'Nghia', N'Nghia', '2025-12-15 14:55:00', '2025-12-15 14:55:00', 1, 1;
exec sp_insert_hoa_don_chi_tiet 5, 24, 5000, 72, '2025-12-15', '2025-12-15', N'admin', N'admin', 1, 0, 2880000;
exec sp_insert_hoa_don_chi_tiet 3, 24, 5000, 72, '2025-12-15', '2025-12-15', N'admin', N'admin', 1, 0, 2304000;
exec sp_insert_hoa_don_chi_tiet 1, 24, 5000, 72, '2025-12-15', '2025-12-15', N'admin', N'admin', 1, 0, 1440000;
go


--5.
--5.1 view sản phấm bán chạy nhất tháng hiện tại còn muốn xem bao thay top 1 bằng top đó
create or alter view view_top_spct_ban_chay_nhat_thang
as
select top 1
    spct.id_san_pham_chi_tiet,
    sp.id_san_pham,
    sp.ten_san_pham,
    spct.id_mau_sac,
    spct.id_kich_co,
    sum(hdct.so_luong) as tong_ban,
    month(hd.ngay_tao_ma) as thang,
    year(hd.ngay_tao_ma) as nam
from hoa_don_chi_tiet hdct
join hoa_don hd on hd.id_hoa_don = hdct.id_hoa_don
join san_pham_chi_tiet spct on spct.id_san_pham_chi_tiet = hdct.id_san_pham_chi_tiet
join san_pham sp on sp.id_san_pham = spct.id_san_pham
where 
    month(hd.ngay_tao_ma) = month(getdate())
    and year(hd.ngay_tao_ma) = year(getdate())
group by 
    spct.id_san_pham_chi_tiet,
    sp.id_san_pham,
    sp.ten_san_pham,
    spct.id_mau_sac,
    spct.id_kich_co,
    month(hd.ngay_tao_ma),
    year(hd.ngay_tao_ma)
order by sum(hdct.so_luong) desc;
go
select * from view_top_spct_ban_chay_nhat_thang
go
--5.2 muốn hiên bao nhiêu sản phẩm tồn tồn ít nhất thay top 1 bằng số sp muốn hiện
create or alter view view_spct_ton_kho_it_nhat
as
select top 1
    spct.id_san_pham_chi_tiet,
    sp.id_san_pham,
    sp.ten_san_pham,
    spct.id_mau_sac,
    spct.id_kich_co,
    spct.so_luong_ton,
    month(hd.ngay_tao_ma) as thang,
    year(hd.ngay_tao_ma) as nam
from hoa_don_chi_tiet hdct
join hoa_don hd on hd.id_hoa_don = hdct.id_hoa_don
join san_pham_chi_tiet spct on spct.id_san_pham_chi_tiet = hdct.id_san_pham_chi_tiet
join san_pham sp on sp.id_san_pham = spct.id_san_pham
where 
    month(hd.ngay_tao_ma) = month(GETDATE())
    and year(hd.ngay_tao_ma) = year(GETDATE())
group by
    spct.id_san_pham_chi_tiet,
    sp.id_san_pham,
    sp.ten_san_pham,
    spct.id_mau_sac,
    spct.id_kich_co,
    spct.so_luong_ton,
    month(hd.ngay_tao_ma),
    year(hd.ngay_tao_ma)
order by spct.so_luong_ton asc;  
go
select * from view_spct_ton_kho_it_nhat

--4. trigger
--4.1 insert bang sanpham&sanpham chi tiet
use BShoes
go
CREATE OR ALTER PROCEDURE sp_insert_san_pham_with_placeholder
    @p_id_loai_san_pham INT,
    @p_id_chat_lieu INT,
    @p_id_kieu_dang INT,
    @p_id_kieu_co_giay INT,
    @p_id_kieu_day_giay INT,
    @p_id_thuong_hieu INT,
    @p_id_xuat_su INT,
    @p_ma_san_pham VARCHAR(20),
    @p_ten_san_pham NVARCHAR(100),
    @p_mo_ta NVARCHAR(255),
    @p_trang_thai BIT
AS
BEGIN
    SET NOCOUNT ON;

    BEGIN TRY
        BEGIN TRANSACTION;

        INSERT INTO san_pham (
            id_loai_san_pham,id_chat_lieu,id_kieu_dang,id_kieu_co_giay,
            id_kieu_day_giay,id_thuong_hieu,id_xuat_su,
            ma_san_pham,ten_san_pham,mo_ta,trang_thai
        )
        VALUES (
            @p_id_loai_san_pham,@p_id_chat_lieu,@p_id_kieu_dang,@p_id_kieu_co_giay,
            @p_id_kieu_day_giay,@p_id_thuong_hieu,@p_id_xuat_su,
            @p_ma_san_pham,@p_ten_san_pham,@p_mo_ta,@p_trang_thai
        );

        DECLARE @p_new_id_san_pham INT = SCOPE_IDENTITY();

        -------------------------------------------------
        -- INSERT PLACEHOLDER *KHÔNG CẦN id_kich_co / id_mau_sac*
        -------------------------------------------------
        INSERT INTO san_pham_chi_tiet (
            id_san_pham,
            ma_san_pham_chi_tiet,
            so_luong_ton,
            don_gia,
            nguoi_tao,
            nguoi_cap_nhat,
            trang_thai
        )
        VALUES (
            @p_new_id_san_pham,CONCAT( 'SPCT', @p_ma_san_pham),0,0,'system','system',0
        );

        COMMIT TRANSACTION;

        SELECT sp.*, spct.*
        FROM san_pham sp
        JOIN san_pham_chi_tiet spct ON sp.id_san_pham = spct.id_san_pham
        WHERE sp.id_san_pham = @p_new_id_san_pham;

    END TRY
    BEGIN CATCH
        ROLLBACK TRANSACTION;
        THROW;
    END CATCH
END;
GO

--4.1 insert 2 bảng EXEC sp_insert_san_pham_with_placeholder  @p_id_loai_san_pham ,@p_id_chat_lieu , @p_id_kieu_dang , @p_id_kieu_co_giay , @p_id_kieu_day_giay , @p_id_thuong_hieu ,@p_id_xuat_su , @p_ma_san_pham , @p_ten_san_pham , @p_mo_ta , @p_trang_thai , 
EXEC sp_insert_san_pham_with_placeholder 
    1,2,3,1,2, 1,1,'SP010',N'Giày Thể Thao Pro Max',N'Bản demo sản phẩm',1;

	select *from san_pham;
	select *from san_pham_chi_tiet;

--4.2 insert hoa_don mới kèm 1 hoa_don_chi_tiet
use BShoes
go
CREATE or alter PROCEDURE sp_tao_hoa_don_va_chi_tiet
    @id_san_pham_chi_tiet INT,
    @so_luong INT,
    @gia_giam MONEY,
    @nguoi_tao NVARCHAR(50) = NULL
AS
BEGIN
    SET NOCOUNT ON;
    DECLARE @id_hoa_don INT;

    -- 1. Tạo hóa đơn trước
    INSERT INTO hoa_don (id_khach_hang,id_phieu_giam_gia,ma_hoa_don,tong_tien_ban_dau,tien_giam_gia,tong_tien_phai_tra,ten_nguoi_nhan,so_dien_thoai,dia_chi,phuong_thuc_thanh_toan,ghi_chu,nguoi_tao_ma,ngay_tao_ma,trang_thai,loai_hoa_don)
    VALUES (
        NULL,
        NULL,
        CONCAT('HD', FORMAT(GETDATE(), 'yyyyMMddHHmmss')),
        0,
        0,
        0,
        0,
        0,
        0,
        N'',
        N'',
        @nguoi_tao,
        GETDATE(),
        0,
        0
    );

    -- Lấy ID hóa đơn vừa tạo
    SET @id_hoa_don = SCOPE_IDENTITY();

    -- 2. Tạo hóa đơn chi tiết tương ứng
    INSERT INTO hoa_don_chi_tiet (
        id_san_pham_chi_tiet,id_hoa_don,gia_giam,so_luong,nguoi_tao,nguoi_cap_nhat,trang_thai
    )
    VALUES (
        @id_san_pham_chi_tiet,@id_hoa_don,@gia_giam,@so_luong,@nguoi_tao,@nguoi_tao,1
    );

    -- Trả về kết quả
    SELECT @id_hoa_don AS id_hoa_don_vua_tao;
END;
GO

--exec insert hoa_don @id_san_pham_chi_tiet ,@so_luong ,@gia_giam ,@nguoi_tao ;
EXEC sp_tao_hoa_don_va_chi_tiet 5,3,15000,N'admin';
select*from hoa_don;
select*from hoa_don_chi_tiet;
-- thêm id_nhan_vien cho bảng hóa_don mới có thể hiện lịch sử chuẩn
ALTER TABLE hoa_don ADD id_nhan_vien INT NULL;

ALTER TABLE hoa_don
ADD FOREIGN KEY (id_nhan_vien) REFERENCES nhan_vien(id_nhan_vien);
--4.3 insert hoadon rong
use BShoes
go
create or alter procedure sp_insert_hoa_don_placeholder
    @p_trang_thai int,
	@p_loai_hoa_don bit,
    @p_id_nhan_vien INT   
as
begin
    insert into hoa_don(ma_hoa_don,trang_thai,loai_hoa_don,id_nhan_vien)
    values (CONCAT('HD', FORMAT(GETDATE(), 'yyyyMMddHHmmss')),@p_trang_thai,@p_loai_hoa_don,@p_id_nhan_vien);
    select * from hoa_don;
end;
go

exec sp_insert_hoa_don_placeholder 1,0,1;
SELECT * FROM hoa_don ORDER BY id_hoa_don DESC;

-- 4.4 TRIGGER - Tự động ghi lịch sử hóa đơn khi INSERT, UPDATE, DELETE
USE BShoes
GO
-- TRIGGER cho INSERT hóa đơn
CREATE OR ALTER TRIGGER trg_insert_lich_su_hoa_don
ON hoa_don
AFTER INSERT
AS
BEGIN
    SET NOCOUNT ON;

    INSERT INTO lich_su_hoa_don
    (
        id_nhan_vien,
        id_hoa_don,
        ghi_chu,
        thoi_gian_thay_doi,
        nguoi_tao_ma,
        nguoi_cap_nhat,
        ngay_tao_ma,
        ngay_cap_nhat,
        trang_thai,
        trang_thai_xoa
    )
    SELECT 
        i.id_nhan_vien,
        i.id_hoa_don,
        N'Tạo mới hóa đơn: ' + i.ma_hoa_don,
        GETDATE(),
        i.id_nhan_vien,
        i.id_nhan_vien,
        GETDATE(),
        GETDATE(),
        i.trang_thai,
        0
    FROM inserted i;
END;
GO

-- TRIGGER cho UPDATE hóa đơn
CREATE OR ALTER TRIGGER trg_update_lich_su_hoa_don
ON hoa_don
AFTER UPDATE
AS
BEGIN
    SET NOCOUNT ON;

    INSERT INTO lich_su_hoa_don
    (
        id_nhan_vien,
        id_hoa_don,
        ghi_chu,
        thoi_gian_thay_doi,
        nguoi_tao_ma,
        nguoi_cap_nhat,
        ngay_tao_ma,
        ngay_cap_nhat,
        trang_thai,
        trang_thai_xoa
    )
    SELECT
        i.id_nhan_vien,
        i.id_hoa_don,
        N'Cập nhật hóa đơn: ' + i.ma_hoa_don +
        N' | Trạng thái: ' + CAST(i.trang_thai AS NVARCHAR(10)) +
        N' | Tổng tiền: ' + CAST(i.tong_tien_phai_tra AS NVARCHAR(20)),
        GETDATE(),
        i.id_nhan_vien,
        i.id_nhan_vien,
        GETDATE(),
        GETDATE(),
        i.trang_thai,
        0
    FROM inserted i;
END;
GO

-- TRIGGER cho DELETE hóa đơn - Soft Delete (cập nhật trang_thai = 3)
CREATE OR ALTER TRIGGER trg_delete_lich_su_hoa_don
ON hoa_don
INSTEAD OF DELETE
AS
BEGIN
    SET NOCOUNT ON;

    UPDATE hd
    SET 
        trang_thai = 3,
        ngay_cap_nhat = GETDATE(),
        nguoi_cap_nhat = SYSTEM_USER
    FROM hoa_don hd
    INNER JOIN deleted d ON hd.id_hoa_don = d.id_hoa_don;

    INSERT INTO lich_su_hoa_don
    (
        id_nhan_vien,
        id_hoa_don,
        ghi_chu,
        thoi_gian_thay_doi,
        nguoi_tao_ma,
        nguoi_cap_nhat,
        ngay_tao_ma,
        ngay_cap_nhat,
        trang_thai,
        trang_thai_xoa
    )
    SELECT
        d.id_nhan_vien,
        d.id_hoa_don,
        N'Xóa hóa đơn: ' + d.ma_hoa_don + N' (Soft Delete)',
        GETDATE(),
        d.id_nhan_vien,
        d.id_nhan_vien,
        GETDATE(),
        GETDATE(),
        3,
        0
    FROM deleted d;
END;
GO

--10. tiện ích bảng
--check số dòng xem bảng có insert dữ liệu thành công hay không. count<1 -> fail
EXEC sp_MSforeachtable 'SELECT ''?'' AS TableName, COUNT(*) AS SoDong FROM ?';
EXEC sp_MSforeachtable 'SELECT ''?'' AS TableName,* FROM ?';
go
-- Kiểm tra trigger
SELECT * FROM sys.triggers WHERE parent_id = OBJECT_ID('hoa_don');
GO
--cập nhật trạng thái tự động
CREATE OR ALTER PROCEDURE sp_cap_nhat_trang_thai_phieu_giam_gia
AS
BEGIN
    DECLARE @today DATETIME = GETDATE();

    -- Đang hoạt động
    UPDATE phieu_giam_gia
    SET trang_thai = 1
    WHERE @today BETWEEN thoi_gian_bat_dau AND thoi_gian_ket_thuc;

    UPDATE phieu_giam_gia
    SET trang_thai = 0
    WHERE @today < thoi_gian_bat_dau
       OR @today > thoi_gian_ket_thuc;
END;
GO
--lấy danh sách phiếu giảm giá đang hoạt động
CREATE VIEW view_phieu_giam_gia_hoat_dong AS
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
WHERE 
    trang_thai = 1                   
    AND trang_thai_xoa = 0         
    AND GETDATE() BETWEEN thoi_gian_bat_dau AND thoi_gian_ket_thuc
    AND so_luong > 0;                 
GO
--tính số tiền đc giảm
CREATE FUNCTION tinh_tien_giam_gia
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

    -- Không đủ điều kiện
    IF (@tong_tien < @don_toi_thieu)
        RETURN 0;

    -- Loại %  
    IF (@loai = 0)
        SET @giam = @tong_tien * (@gia_tri / 100.0);

    -- Loại số tiền
    IF (@loai = 1)
        SET @giam = @gia_tri;

    -- Giới hạn giảm tối đa
    IF (@giam > @giam_toi_da)
        SET @giam = @giam_toi_da;

    RETURN @giam;
END;
GO
--CREATE TRIGGER trg_auto_update_trang_thai_spct
--ON san_pham_chi_tiet
--AFTER INSERT, UPDATE
--AS
--BEGIN
--    SET NOCOUNT ON;

--    UPDATE spct
--    SET trang_thai = 
--        CASE 
--            WHEN i.so_luong_ton <= 0 THEN 0
--            ELSE 1
--        END
--    FROM san_pham_chi_tiet spct
--    INNER JOIN inserted i
--        ON spct.id_san_pham_chi_tiet = i.id_san_pham_chi_tiet;
--END;
--GO
-- ============================================================
-- image_url for san_pham_chi_tiet (product variant images)
-- Added 2026-07-03. Safe to re-run: guarded ALTER + round-robin seed.
-- Image files live in frontend/public/images/shoes/ (served at /images/shoes/...).
-- ============================================================
USE BShoes;
GO
IF COL_LENGTH('dbo.san_pham_chi_tiet','image_url') IS NULL
    ALTER TABLE dbo.san_pham_chi_tiet ADD image_url nvarchar(255) NULL;
GO
-- Seed sample images across existing variant rows (round-robin).
DECLARE @img TABLE (rn INT IDENTITY(1,1), url NVARCHAR(255));
INSERT INTO @img(url) VALUES
(N'/images/shoes/img_shoe_10001.png'),
(N'/images/shoes/img_shoe_10002.png'),
(N'/images/shoes/img_shoe_10003.png'),
(N'/images/shoes/img_shoe_10004.png'),
(N'/images/shoes/img_shoe_10005.png'),
(N'/images/shoes/img_shoe_10006.png'),
(N'/images/shoes/img_shoe_10007.png'),
(N'/images/shoes/img_shoe_10008.png'),
(N'/images/shoes/img_shoe_10009.png'),
(N'/images/shoes/img_shoe_10010.png'),
(N'/images/shoes/img_shoe_10011.png'),
(N'/images/shoes/img_shoe_10012.png'),
(N'/images/shoes/img_shoe_10013.png'),
(N'/images/shoes/img_shoe_10014.png'),
(N'/images/shoes/img_shoe_10015.png'),
(N'/images/shoes/img_shoe_10016.png'),
(N'/images/shoes/img_shoe_10018.png'),
(N'/images/shoes/img_shoe_10019.png'),
(N'/images/shoes/img_shoe_10020.png'),
(N'/images/shoes/img_shoe_10021.png'),
(N'/images/shoes/img_shoe_10022.png'),
(N'/images/shoes/img_shoe_10023.png'),
(N'/images/shoes/img_shoe_10024.png'),
(N'/images/shoes/img_shoe_10025.png'),
(N'/images/shoes/img_shoe_10026.png'),
(N'/images/shoes/img_shoe_10027.png'),
(N'/images/shoes/img_shoe_10028.png'),
(N'/images/shoes/img_shoe_10029.png'),
(N'/images/shoes/img_shoe_10030.png'),
(N'/images/shoes/img_shoe_10031.png'),
(N'/images/shoes/img_shoe_10032.png'),
(N'/images/shoes/img_shoe_10033.png'),
(N'/images/shoes/img_shoe_10034.png'),
(N'/images/shoes/img_shoe_10035.png'),
(N'/images/shoes/img_shoe_10036.png'),
(N'/images/shoes/img_shoe_10037.png'),
(N'/images/shoes/img_shoe_10038.png'),
(N'/images/shoes/img_shoe_10039.png'),
(N'/images/shoes/img_shoe_10040.png'),
(N'/images/shoes/img_shoe_10041.png'),
(N'/images/shoes/img_shoe_10042.png'),
(N'/images/shoes/img_shoe_10043.png'),
(N'/images/shoes/img_shoe_10044.png'),
(N'/images/shoes/img_shoe_10045.png'),
(N'/images/shoes/img_shoe_10046.png'),
(N'/images/shoes/img_shoe_10047.png'),
(N'/images/shoes/img_shoe_10048.png'),
(N'/images/shoes/img_shoe_10049.png'),
(N'/images/shoes/img_shoe_10050.png'),
(N'/images/shoes/img_shoe_10051.png'),
(N'/images/shoes/img_shoe_10052.png'),
(N'/images/shoes/img_shoe_10053.png'),
(N'/images/shoes/img_shoe_10054.png'),
(N'/images/shoes/img_shoe_10055.png'),
(N'/images/shoes/img_shoe_10056.png'),
(N'/images/shoes/img_shoe_10057.png'),
(N'/images/shoes/img_shoe_10058.png'),
(N'/images/shoes/img_shoe_10059.png'),
(N'/images/shoes/img_shoe_10060.png'),
(N'/images/shoes/img_shoe_10061.png'),
(N'/images/shoes/img_shoe_10062.png'),
(N'/images/shoes/img_shoe_10063.png'),
(N'/images/shoes/img_shoe_10064.png'),
(N'/images/shoes/img_shoe_10065.png'),
(N'/images/shoes/img_shoe_10066.png'),
(N'/images/shoes/img_shoe_10067.png'),
(N'/images/shoes/img_shoe_10068.png'),
(N'/images/shoes/img_shoe_10069.png'),
(N'/images/shoes/img_shoe_10070.png'),
(N'/images/shoes/img_shoe_10071.png'),
(N'/images/shoes/img_shoe_10072.png'),
(N'/images/shoes/img_shoe_10073.png'),
(N'/images/shoes/img_shoe_10074.png'),
(N'/images/shoes/img_shoe_10075.png'),
(N'/images/shoes/img_shoe_10076.png'),
(N'/images/shoes/img_shoe_10077.png'),
(N'/images/shoes/img_shoe_10078.png'),
(N'/images/shoes/img_shoe_10079.png'),
(N'/images/shoes/img_shoe_10080.png'),
(N'/images/shoes/img_shoe_10081.png'),
(N'/images/shoes/img_shoe_10082.png'),
(N'/images/shoes/img_shoe_10083.png'),
(N'/images/shoes/img_shoe_10084.png'),
(N'/images/shoes/img_shoe_10085.png'),
(N'/images/shoes/img_shoe_10086.png'),
(N'/images/shoes/img_shoe_10087.png'),
(N'/images/shoes/img_shoe_10088.png'),
(N'/images/shoes/img_shoe_10089.png'),
(N'/images/shoes/img_shoe_10090.png'),
(N'/images/shoes/img_shoe_10091.png'),
(N'/images/shoes/img_shoe_10092.png'),
(N'/images/shoes/img_shoe_10093.png'),
(N'/images/shoes/img_shoe_10094.png'),
(N'/images/shoes/img_shoe_10095.png'),
(N'/images/shoes/img_shoe_10096.png'),
(N'/images/shoes/img_shoe_10097.png'),
(N'/images/shoes/img_shoe_10098.png'),
(N'/images/shoes/img_shoe_10099.png'),
(N'/images/shoes/img_shoe_10100.png'),
(N'/images/shoes/img_shoe_10101.png'),
(N'/images/shoes/img_shoe_10102.png'),
(N'/images/shoes/img_shoe_10103.png'),
(N'/images/shoes/img_shoe_10104.png'),
(N'/images/shoes/img_shoe_10105.png'),
(N'/images/shoes/img_shoe_10106.png'),
(N'/images/shoes/img_shoe_10107.png'),
(N'/images/shoes/img_shoe_10108.png'),
(N'/images/shoes/img_shoe_10109.png'),
(N'/images/shoes/img_shoe_10110.png'),
(N'/images/shoes/img_shoe_10111.png'),
(N'/images/shoes/img_shoe_10112.png'),
(N'/images/shoes/img_shoe_10113.png'),
(N'/images/shoes/img_shoe_10114.png'),
(N'/images/shoes/img_shoe_10115.png'),
(N'/images/shoes/img_shoe_10116.png'),
(N'/images/shoes/img_shoe_10117.png'),
(N'/images/shoes/img_shoe_10118.png'),
(N'/images/shoes/img_shoe_10119.png'),
(N'/images/shoes/img_shoe_10120.png'),
(N'/images/shoes/img_shoe_10121.png'),
(N'/images/shoes/img_shoe_10122.png'),
(N'/images/shoes/img_shoe_10123.png'),
(N'/images/shoes/img_shoe_10124.png'),
(N'/images/shoes/img_shoe_10125.png'),
(N'/images/shoes/img_shoe_10126.png'),
(N'/images/shoes/img_shoe_10127.png'),
(N'/images/shoes/img_shoe_10128.png'),
(N'/images/shoes/img_shoe_10129.png'),
(N'/images/shoes/img_shoe_10130.png'),
(N'/images/shoes/img_shoe_10131.png'),
(N'/images/shoes/img_shoe_10132.png'),
(N'/images/shoes/img_shoe_10133.png'),
(N'/images/shoes/img_shoe_10134.png'),
(N'/images/shoes/img_shoe_10135.png'),
(N'/images/shoes/img_shoe_10136.png'),
(N'/images/shoes/img_shoe_10137.png'),
(N'/images/shoes/img_shoe_10138.png'),
(N'/images/shoes/img_shoe_10139.png'),
(N'/images/shoes/img_shoe_10140.png');
DECLARE @cnt INT = (SELECT COUNT(*) FROM @img);
;WITH n AS (
    SELECT id_san_pham_chi_tiet AS id, ROW_NUMBER() OVER (ORDER BY id_san_pham_chi_tiet) AS rn
    FROM dbo.san_pham_chi_tiet
)
UPDATE spct SET image_url = i.url
FROM dbo.san_pham_chi_tiet spct
JOIN n ON n.id = spct.id_san_pham_chi_tiet
JOIN @img i ON i.rn = ((n.rn - 1) % @cnt) + 1;
GO
