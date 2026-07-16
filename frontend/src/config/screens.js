// Single registry of admin screens: drives the sidebar AND the phân-quyền panel.
// `key` matches the CSV tokens stored in vai_tro.quyen ('*' = all).
export const SCREENS = [
  { key: 'dashboard', label: 'Doanh Thu', icon: 'bi-graph-up', to: '/' },
  { key: 'san-pham', label: 'Sản Phẩm', icon: 'bi-box-seam', to: '/san-pham' },
  { key: 'danh-muc', label: 'Danh Mục', icon: 'bi-diagram-3', to: '/danh-muc' },
  { key: 'thuoc-tinh', label: 'Thuộc Tính', icon: 'bi-tags', to: '/thuoc-tinh' },
  { key: 'hoa-don', label: 'Bán Hàng', icon: 'bi-cart', to: '/hoa-don' },
  { key: 'don-hang', label: 'Giao Hàng', icon: 'bi-truck', to: '/don-hang' },
  { key: 'nhan-vien', label: 'Nhân Viên', icon: 'bi-people', to: '/nhan-vien' },
  { key: 'khach-hang', label: 'Khách Hàng', icon: 'bi-person-badge', to: '/khach-hang' },
  { key: 'lich-su', label: 'Lịch Sử', icon: 'bi-clock-history', to: '/lich-su' },
  { key: 'bao-hanh', label: 'Bảo Hành', icon: 'bi-shield-check', to: '/bao-hanh' },
  { key: 'phieu-giam-gia', label: 'Khuyến Mãi', icon: 'bi-ticket-perforated', to: '/phieu-giam-gia' },
  { key: 'phan-quyen', label: 'Phân Quyền', icon: 'bi-shield-lock', to: '/phan-quyen' },
  { key: 'he-thong', label: 'Hệ Thống', icon: 'bi-gear', to: '/he-thong' },
]
