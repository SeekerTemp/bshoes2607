package com.vn.test.bshoes.service;

import com.vn.test.bshoes.dto.NhanVienDto;
import com.vn.test.bshoes.entity.NhanVien;
import com.vn.test.bshoes.entity.VaiTro;
import com.vn.test.bshoes.repository.NhanVienRepository;
import com.vn.test.bshoes.repository.VaiTroRepository;
import com.vn.test.bshoes.service.impl.NhanVienServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Test đơn vị cho NhanVienServiceImpl — chạy được khi chưa có SQL Server (mock repository).
 * Bao phủ 2 root cause vừa fix: vai trò phải resolve theo id (không phải theo tên), và
 * mật khẩu phải được set khi tạo mới / chỉ ghi đè khi có giá trị mới lúc sửa.
 */
class NhanVienServiceImplTest {

    private NhanVienRepository nhanVienRepo;
    private VaiTroRepository vaiTroRepo;
    private NhanVienQuyenService quyenService;
    private NhanVienServiceImpl service;

    @BeforeEach
    void setUp() {
        nhanVienRepo = mock(NhanVienRepository.class);
        vaiTroRepo = mock(VaiTroRepository.class);
        quyenService = mock(NhanVienQuyenService.class);
        service = new NhanVienServiceImpl(nhanVienRepo, vaiTroRepo, quyenService);

        // repo.save trả lại đúng entity truyền vào, gán id giả lập nếu chưa có (giống IDENTITY)
        when(nhanVienRepo.save(any(NhanVien.class))).thenAnswer(inv -> {
            NhanVien n = inv.getArgument(0);
            if (n.getId() == null) n.setId(1);
            return n;
        });
    }

    private VaiTro vaiTro(int id, String ten) {
        VaiTro vt = new VaiTro();
        vt.setId(id);
        vt.setTenVaiTro(ten);
        vt.setMaVaiTro(ten);
        vt.setQuyen("dashboard");
        return vt;
    }

    private NhanVienDto dto() {
        NhanVienDto d = new NhanVienDto();
        d.setTen("Nguyễn Văn A");
        d.setTaiKhoan("nva");
        d.setEmail("a@bshoes.vn");
        d.setSdt("0900000000");
        d.setCccd("012345678900");
        d.setChucVu("Bán hàng");
        d.setGioiTinh("Nam");
        d.setTrangThai(true);
        return d;
    }

    // ---------- create ----------

    @Test
    @DisplayName("create: resolve vai trò theo id (không phải theo tên) và set mật khẩu")
    void createResolveVaiTroByIdVaSetMatKhau() {
        VaiTro vt2 = vaiTro(2, "NHÂN VIÊN");
        when(vaiTroRepo.findById(2)).thenReturn(Optional.of(vt2));

        NhanVienDto d = dto();
        d.setIdVaiTro(2);
        d.setMatKhau("matkhau123");

        service.create(d);

        ArgumentCaptor<NhanVien> cap = ArgumentCaptor.forClass(NhanVien.class);
        verify(nhanVienRepo, org.mockito.Mockito.atLeastOnce()).save(cap.capture());
        NhanVien saved = cap.getAllValues().get(0);

        verify(vaiTroRepo).findById(2);
        assertThat(saved.getIdVaiTro()).isEqualTo(vt2);
        assertThat(saved.getMatKhau()).isEqualTo("matkhau123");
    }

    @Test
    @DisplayName("create: idVaiTro không tồn tại -> vaiTro null, không nổ")
    void createVaiTroKhongTonTai() {
        when(vaiTroRepo.findById(99)).thenReturn(Optional.empty());

        NhanVienDto d = dto();
        d.setIdVaiTro(99);
        d.setMatKhau("x");

        service.create(d);

        ArgumentCaptor<NhanVien> cap = ArgumentCaptor.forClass(NhanVien.class);
        verify(nhanVienRepo, org.mockito.Mockito.atLeastOnce()).save(cap.capture());
        assertThat(cap.getAllValues().get(0).getIdVaiTro()).isNull();
    }

    // ---------- update ----------

    @Test
    @DisplayName("update: mật khẩu để trống -> giữ nguyên mật khẩu cũ")
    void updateMatKhauRongGiuNguyen() {
        NhanVien existing = new NhanVien();
        existing.setId(5);
        existing.setMatKhau("mat-khau-cu");
        existing.setIdVaiTro(vaiTro(1, "ADMIN"));
        when(nhanVienRepo.findById(5)).thenReturn(Optional.of(existing));

        NhanVienDto d = dto();
        d.setId(5);
        d.setIdVaiTro(1);
        d.setMatKhau("");

        service.update(d);

        assertThat(existing.getMatKhau()).isEqualTo("mat-khau-cu");
    }

    @Test
    @DisplayName("update: mật khẩu không để trống -> ghi đè mật khẩu mới")
    void updateMatKhauMoiGhiDe() {
        NhanVien existing = new NhanVien();
        existing.setId(5);
        existing.setMatKhau("mat-khau-cu");
        existing.setIdVaiTro(vaiTro(1, "ADMIN"));
        when(nhanVienRepo.findById(5)).thenReturn(Optional.of(existing));
        when(vaiTroRepo.findById(1)).thenReturn(Optional.of(vaiTro(1, "ADMIN")));

        NhanVienDto d = dto();
        d.setId(5);
        d.setIdVaiTro(1);
        d.setMatKhau("mat-khau-moi");

        service.update(d);

        assertThat(existing.getMatKhau()).isEqualTo("mat-khau-moi");
    }

    @Test
    @DisplayName("toDto: không bao giờ trả mật khẩu về client")
    void toDtoKhongTraMatKhau() {
        VaiTro vt2 = vaiTro(2, "NHÂN VIÊN");
        when(vaiTroRepo.findById(2)).thenReturn(Optional.of(vt2));

        NhanVienDto d = dto();
        d.setIdVaiTro(2);
        d.setMatKhau("matkhau123");

        NhanVienDto result = service.create(d);

        assertThat(result.getMatKhau()).isNull();
        assertThat(result.getIdVaiTro()).isEqualTo(2);
        assertThat(result.getVaiTro()).isEqualTo("NHÂN VIÊN");
    }
}
