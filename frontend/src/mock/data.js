// Mock seed data copied verbatim (Vietnamese intact) from preview/*.html demo screens.
// This is the data seam: composables read these arrays via useCrud() until the
// Phase 2 backend is wired up through src/api/*.js.

// ---- preview/index.html (Dashboard) ----
export const dashboardCards = [
  { label: 'Doanh thu tháng', value: '128.500.000 ₫' },
  { label: 'Số đơn', value: 342 },
  { label: 'Đơn thành công', value: 310 },
  { label: 'Đơn chờ', value: 18 },
  { label: 'Đơn huỷ', value: 14 },
]

export const dashboardSanPham = [
  { maSP: 'SP1', tenSP: 'Nike Air Zoom', loaiSP: 'Thể thao', chatLieu: 'Vải', mauSac: 'Đen', kichThuoc: '42', soLuongTon: 35, soLuongBan: 120, doanhThu: 240000000 },
  { maSP: 'SP2', tenSP: 'Adidas Ultraboost', loaiSP: 'Chạy bộ', chatLieu: 'Primeknit', mauSac: 'Trắng', kichThuoc: '41', soLuongTon: 22, soLuongBan: 98, doanhThu: 196000000 },
  { maSP: 'SP3', tenSP: 'Converse Classic', loaiSP: 'Lifestyle', chatLieu: 'Canvas', mauSac: 'Đỏ', kichThuoc: '40', soLuongTon: 60, soLuongBan: 75, doanhThu: 90000000 },
]

// ---- preview/phieu-giam-gia.html ----
export const phieuGiamGia = [
  { id: 1, ma: 'PGG1', ten: 'Giảm 10% toàn shop', loai: 0, giaTri: 10, donToiThieu: 500000, giamToiDa: 100000, soLuong: 50, batDau: '2026-07-01', ketThuc: '2026-07-31', trangThai: true },
  { id: 2, ma: 'PGG2', ten: 'Giảm 50k', loai: 1, giaTri: 50000, donToiThieu: 300000, giamToiDa: 50000, soLuong: 100, batDau: '2026-07-01', ketThuc: '2026-08-15', trangThai: true },
  { id: 3, ma: 'PGG3', ten: 'Flash sale', loai: 0, giaTri: 25, donToiThieu: 1000000, giamToiDa: 300000, soLuong: 20, batDau: '2026-06-01', ketThuc: '2026-06-30', trangThai: false },
]

// ---- preview/khach-hang.html ----
export const khachHang = [
  { id: 1, ma: 'KH1', ten: 'Nguyễn Văn A', gioiTinh: 'Nam', sdt: '0901111111', email: 'a@gmail.com', diaChi: 'Hà Nội', trangThai: true },
  { id: 2, ma: 'KH2', ten: 'Trần Thị B', gioiTinh: 'Nữ', sdt: '0902222222', email: 'b@gmail.com', diaChi: 'Đà Nẵng', trangThai: true },
  { id: 3, ma: 'KH3', ten: 'Lê Văn C', gioiTinh: 'Nam', sdt: '0903333333', email: 'c@gmail.com', diaChi: 'TP.HCM', trangThai: false },
]

// ---- preview/nhan-vien.html ----
export const nhanVien = [
  { id: 1, ma: 'NV1', ten: 'Phạm Quản Trị', taiKhoan: 'admin', email: 'admin@bshoes.vn', sdt: '0900000001', cccd: '012345678901', chucVu: 'Quản lý', gioiTinh: 'Nam', vaiTro: 'ADMIN', trangThai: true },
  { id: 2, ma: 'NV2', ten: 'Hoàng Bán Hàng', taiKhoan: 'hoangbh', email: 'hoang@bshoes.vn', sdt: '0900000002', cccd: '012345678902', chucVu: 'Bán hàng', gioiTinh: 'Nam', vaiTro: 'NHÂN VIÊN', trangThai: true },
  { id: 3, ma: 'NV3', ten: 'Vũ Thu Ngân', taiKhoan: 'vungan', email: 'ngan@bshoes.vn', sdt: '0900000003', cccd: '012345678903', chucVu: 'Thu ngân', gioiTinh: 'Nữ', vaiTro: 'NHÂN VIÊN', trangThai: false },
]

// ---- preview/san-pham.html ----
export const thuongHieuList = ['Nike', 'Adidas', 'Converse']
export const chatLieuList = ['Vải', 'Da', 'Canvas', 'Primeknit']

export const sanPham = [
  {
    id: 1, ma: 'SP1', ten: 'Nike Air Zoom', thuongHieu: 'Nike', chatLieu: 'Vải', gia: 2000000, moTa: 'Giày chạy bộ',
    bienThe: [{ ma: 'SP1-D42', mau: 'Đen', size: '42', ton: 12, gia: 2000000 }, { ma: 'SP1-T41', mau: 'Trắng', size: '41', ton: 8, gia: 2000000 }],
  },
  {
    id: 2, ma: 'SP2', ten: 'Adidas Ultraboost', thuongHieu: 'Adidas', chatLieu: 'Primeknit', gia: 2500000, moTa: 'Đệm Boost',
    bienThe: [{ ma: 'SP2-T40', mau: 'Trắng', size: '40', ton: 5, gia: 2500000 }],
  },
  {
    id: 3, ma: 'SP3', ten: 'Converse Classic', thuongHieu: 'Converse', chatLieu: 'Canvas', gia: 1200000, moTa: 'Cổ điển',
    bienThe: [{ ma: 'SP3-D39', mau: 'Đỏ', size: '39', ton: 20, gia: 1200000 }],
  },
]

// ---- preview/hoa-don.html (POS) ----
export const posSanPham = [
  { id: 1, ma: 'SP1', ten: 'Nike Air Zoom', mau: 'Đen', size: '42', ton: 12, gia: 2000000 },
  { id: 2, ma: 'SP2', ten: 'Adidas Ultraboost', mau: 'Trắng', size: '40', ton: 5, gia: 2500000 },
  { id: 3, ma: 'SP3', ten: 'Converse Classic', mau: 'Đỏ', size: '39', ton: 20, gia: 1200000 },
]

export const posKhachHang = ['Khách lẻ', 'Nguyễn Văn A', 'Trần Thị B']

export const posVouchers = [
  { value: 0, label: 'Không' },
  { value: 0.1, label: 'Giảm 10%' },
  { value: 50000, label: 'Giảm 50k' },
]

export const posHinhThuc = ['Tiền mặt', 'Chuyển khoản', 'Thẻ']

// Invoices sitting on the queue (NetBeans "Danh sách hóa đơn" / tbl_hoaDon).
// The first one is the one being edited ("Đang tạo"); the rest are waiting.
export const posHoaDonQueue = [
  {
    ma: 'HD001', nhanVien: 'admin', khachHang: 'Khách lẻ', sdt: '', diaChi: '',
    trangThai: 'Đang tạo', trangThaiHang: '-', ngayTao: '03-07-2026',
    voucher: 0, hinhThuc: 'Tiền mặt', khachDua: 0, memberCode: '', phiShip: 0, ghiChu: '',
    gio: [
      { spId: 1, ma: 'SP1', ten: 'Nike Air Zoom', mau: 'Đen', size: '42', gia: 2000000, ton: 12, soLuong: 1, trangThai: '-' },
    ],
  },
  {
    ma: 'HD002', nhanVien: 'hoangbh', khachHang: 'Nguyễn Văn A', sdt: '0901234567', diaChi: '',
    trangThai: 'Chờ', trangThaiHang: 'Chưa thanh toán', ngayTao: '03-07-2026',
    voucher: 0, hinhThuc: 'Tiền mặt', khachDua: 0, memberCode: '', phiShip: 0, ghiChu: '',
    gio: [
      { spId: 2, ma: 'SP2', ten: 'Adidas Ultraboost', mau: 'Trắng', size: '40', gia: 2500000, ton: 5, soLuong: 2, trangThai: '-' },
    ],
  },
  {
    ma: 'HD003', nhanVien: 'admin', khachHang: 'Trần Thị B', sdt: '0912345678', diaChi: '',
    trangThai: 'Chờ', trangThaiHang: 'Đã thanh toán', ngayTao: '02-07-2026',
    voucher: 0, hinhThuc: 'Chuyển khoản', khachDua: 1200000, memberCode: '', phiShip: 0, ghiChu: '',
    gio: [
      { spId: 3, ma: 'SP3', ten: 'Converse Classic', mau: 'Đỏ', size: '39', gia: 1200000, ton: 20, soLuong: 1, trangThai: '-' },
    ],
  },
]

// ---- preview/lich-su.html ----
export const lichSu = [
  {
    id: 1, ma: 'HD1', khach: 'Nguyễn Văn A', nhanVien: 'admin', ngayTao: '2026-07-01', tongTien: 2000000, trangThai: 'Thành công',
    chiTiet: [{ ten: 'Nike Air Zoom (Đen/42)', soLuong: 1, donGia: 2000000 }],
  },
  {
    id: 2, ma: 'HD2', khach: 'Khách lẻ', nhanVien: 'hoangbh', ngayTao: '2026-07-01', tongTien: 3700000, trangThai: 'Chờ',
    chiTiet: [{ ten: 'Adidas Ultraboost (Trắng/40)', soLuong: 1, donGia: 2500000 }, { ten: 'Converse Classic (Đỏ/39)', soLuong: 1, donGia: 1200000 }],
  },
  {
    id: 3, ma: 'HD3', khach: 'Trần Thị B', nhanVien: 'vungan', ngayTao: '2026-06-30', tongTien: 1200000, trangThai: 'Huỷ',
    chiTiet: [{ ten: 'Converse Classic (Đỏ/39)', soLuong: 1, donGia: 1200000 }],
  },
]
