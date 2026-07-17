// Single registry of admin screens: drives the sidebar, the router guard AND the
// phân-quyền grid (mỗi key = 1 cột). `key` matches rows in nhan_vien_quyen.
// Danh mục / Thuộc tính giờ là tab trong Sản Phẩm, Phân quyền là tab trong Nhân Viên
// → tab kế thừa quyền của màn cha, không có key riêng.
export const SCREENS = [
  { key: 'dashboard', label: 'Doanh Thu', icon: 'bi-graph-up', to: '/' },
  { key: 'san-pham', label: 'Sản Phẩm', icon: 'bi-box-seam', to: '/san-pham' },
  { key: 'hoa-don', label: 'Bán Hàng', icon: 'bi-cart', to: '/hoa-don' },
  { key: 'don-hang', label: 'Giao Hàng', icon: 'bi-truck', to: '/don-hang' },
  { key: 'dat-truoc', label: 'Đặt Trước', icon: 'bi-bookmark-star', to: '/dat-truoc' },
  { key: 'nhan-vien', label: 'Nhân Viên', icon: 'bi-people', to: '/nhan-vien' },
  { key: 'khach-hang', label: 'Khách Hàng', icon: 'bi-person-badge', to: '/khach-hang' },
  { key: 'lich-su', label: 'Lịch Sử', icon: 'bi-clock-history', to: '/lich-su' },
  { key: 'bao-hanh', label: 'Bảo Hành', icon: 'bi-shield-check', to: '/bao-hanh' },
  { key: 'phieu-giam-gia', label: 'Khuyến Mãi', icon: 'bi-ticket-perforated', to: '/phieu-giam-gia' },
  { key: 'he-thong', label: 'Hệ Thống', icon: 'bi-gear', to: '/he-thong' },
]
