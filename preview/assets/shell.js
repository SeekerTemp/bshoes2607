function renderShell(active) {
  const links = [
    ['index.html', 'dashboard', 'Doanh Thu'],
    ['san-pham.html', 'sanpham', 'Sản Phẩm'],
    ['hoa-don.html', 'hoadon', 'Hóa Đơn'],
    ['nhan-vien.html', 'nhanvien', 'Nhân Viên'],
    ['khach-hang.html', 'khachhang', 'Khách Hàng'],
    ['lich-su.html', 'lichsu', 'Lịch Sử'],
    ['phieu-giam-gia.html', 'phieu', 'Khuyến Mãi'],
    ['he-thong.html', 'hethong', 'Hệ Thống'],
    ['login.html', 'login', 'Đăng Nhập'],
  ];
  var header = document.getElementById('app-header');
  if (header) {
    header.innerHTML = '<span class="fs-4 fw-bold text-white">BShoes</span>' +
      '<span class="ms-2 text-white-50">Quản lý cửa hàng giày</span>';
  }
  var side = document.getElementById('app-sidebar');
  if (side) {
    side.innerHTML = links.map(function (l) {
      var cls = 'nav-item' + (l[1] === active ? ' active' : '') + (l[1] === 'login' ? ' mt-auto' : '');
      return '<a href="' + l[0] + '" class="' + cls + '">' + l[2] + '</a>';
    }).join('');
  }
}
