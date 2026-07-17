package com.vn.test.bshoes.service;

import com.vn.test.bshoes.dto.NhanVienQuyenDto;
import com.vn.test.bshoes.entity.NhanVien;
import com.vn.test.bshoes.entity.NhanVienQuyen;
import com.vn.test.bshoes.entity.VaiTro;
import com.vn.test.bshoes.repository.NhanVienQuyenRepository;
import com.vn.test.bshoes.repository.NhanVienRepository;
import com.vn.test.bshoes.service.impl.NhanVienQuyenServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Test đơn vị cho quy tắc phân quyền — chạy được khi chưa có SQL Server (mock repository).
 * Đây là phần logic dễ sai nhất của tính năng: ngoại lệ ADMIN, parse template CSV,
 * khử trùng lặp, và việc "đổi vai trò thì mất phần mở rộng".
 */
class NhanVienQuyenServiceImplTest {

    private NhanVienQuyenRepository quyenRepo;
    private NhanVienRepository nhanVienRepo;
    private NhanVienQuyenServiceImpl service;

    @BeforeEach
    void setUp() {
        quyenRepo = mock(NhanVienQuyenRepository.class);
        nhanVienRepo = mock(NhanVienRepository.class);
        service = new NhanVienQuyenServiceImpl(quyenRepo, nhanVienRepo);
    }

    private NhanVien nv(int id, String maVaiTro, String quyenTemplate) {
        VaiTro vt = new VaiTro();
        vt.setId(maVaiTro == null ? null : 1);
        vt.setMaVaiTro(maVaiTro);
        vt.setTenVaiTro(maVaiTro);
        vt.setQuyen(quyenTemplate);
        NhanVien n = new NhanVien();
        n.setId(id);
        n.setMaNhanVien("NV00" + id);
        n.setTenNhanVien("Nhân viên " + id);
        n.setTrangThaiXoa(false);
        n.setIdVaiTro(maVaiTro == null ? null : vt);
        return n;
    }

    // ---------- ngoại lệ ADMIN ----------

    @Test
    @DisplayName("ADMIN trả '*' và không hề đọc bảng phân quyền")
    void adminLuonToanQuyen() {
        when(nhanVienRepo.findById(1)).thenReturn(Optional.of(nv(1, "ADMIN", "*")));

        assertThat(service.quyenCsv(1)).isEqualTo("*");
        // Điểm mấu chốt: thêm màn hình mới thì admin vẫn vào được vì không phụ thuộc rows.
        verify(quyenRepo, never()).findManHinhByNhanVien(anyInt());
    }

    @Test
    @DisplayName("Không cho phân quyền riêng cho ADMIN")
    void khongLuuQuyenChoAdmin() {
        when(nhanVienRepo.findById(1)).thenReturn(Optional.of(nv(1, "ADMIN", "*")));

        assertThatThrownBy(() -> service.luuQuyen(1, List.of("dashboard")))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("toàn quyền");
        verify(quyenRepo, never()).deleteByNhanVien(anyInt());
    }

    @Test
    @DisplayName("Áp template cho ADMIN không ghi row nào")
    void apTemplateChoAdminKhongGhiGi() {
        when(nhanVienRepo.findById(1)).thenReturn(Optional.of(nv(1, "ADMIN", "*")));

        NhanVienQuyenDto d = service.apTemplate(1);

        assertThat(d.isToanQuyen()).isTrue();
        assertThat(d.getQuyen()).isEmpty();
        verify(quyenRepo, never()).deleteByNhanVien(anyInt());
    }

    // ---------- quyền thường ----------

    @Test
    @DisplayName("Nhân viên thường: CSV dựng từ rows, không phải từ template vai trò")
    void quyenThuongLayTuRows() {
        // template chỉ có 'dashboard' nhưng rows đã được mở rộng -> phải theo rows
        when(nhanVienRepo.findById(2)).thenReturn(Optional.of(nv(2, "NV", "dashboard")));
        when(quyenRepo.findManHinhByNhanVien(2)).thenReturn(List.of("dashboard", "hoa-don", "khach-hang"));

        assertThat(service.quyenCsv(2)).isEqualTo("dashboard,hoa-don,khach-hang");
    }

    @Test
    @DisplayName("Nhân viên chưa có row nào -> CSV rỗng (không âm thầm kế thừa vai trò)")
    void khongCoRowThiRong() {
        when(nhanVienRepo.findById(2)).thenReturn(Optional.of(nv(2, "NV", "dashboard,hoa-don")));
        when(quyenRepo.findManHinhByNhanVien(2)).thenReturn(List.of());

        assertThat(service.quyenCsv(2)).isEmpty();
    }

    @Test
    @DisplayName("Lưu quyền: xóa rows cũ rồi ghi rows mới, có khử trùng lặp và trim")
    void luuQuyenKhuTrungLap() {
        when(nhanVienRepo.findById(2)).thenReturn(Optional.of(nv(2, "NV", "dashboard")));

        NhanVienQuyenDto d = service.luuQuyen(2, List.of("dashboard", " hoa-don ", "dashboard", "", "  "));

        verify(quyenRepo).deleteByNhanVien(2);
        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<NhanVienQuyen>> cap = ArgumentCaptor.forClass(List.class);
        verify(quyenRepo).saveAll(cap.capture());
        assertThat(cap.getValue()).extracting(NhanVienQuyen::getManHinh)
                .containsExactly("dashboard", "hoa-don");
        assertThat(d.getQuyen()).containsExactly("dashboard", "hoa-don");
    }

    // ---------- template ----------

    @Test
    @DisplayName("Áp template: tách CSV vai trò, bỏ khoảng trắng và mục trùng")
    void apTemplateTachCsv() {
        when(nhanVienRepo.findById(2)).thenReturn(Optional.of(nv(2, "NV", "dashboard, hoa-don ,dashboard,,bao-hanh")));

        NhanVienQuyenDto d = service.apTemplate(2);

        verify(quyenRepo).deleteByNhanVien(2);
        assertThat(d.getQuyen()).containsExactly("dashboard", "hoa-don", "bao-hanh");
        assertThat(d.isToanQuyen()).isFalse();
    }

    @Test
    @DisplayName("Nhân viên không có vai trò: áp template ra rỗng, không nổ")
    void khongCoVaiTro() {
        when(nhanVienRepo.findById(9)).thenReturn(Optional.of(nv(9, null, null)));

        NhanVienQuyenDto d = service.apTemplate(9);

        assertThat(d.getQuyen()).isEmpty();
        assertThat(d.isToanQuyen()).isFalse();
    }

    // ---------- lưới ----------

    @Test
    @DisplayName("Lưới: gom rows 1 lượt, dòng ADMIN đánh dấu toàn quyền")
    void bangQuyenGomMotLuot() {
        when(nhanVienRepo.findAll()).thenReturn(List.of(nv(1, "ADMIN", "*"), nv(2, "NV", "dashboard")));
        when(quyenRepo.findAll()).thenReturn(List.of(
                new NhanVienQuyen(2, "dashboard"),
                new NhanVienQuyen(2, "hoa-don")));

        List<NhanVienQuyenDto> bang = service.bangQuyen();

        assertThat(bang).hasSize(2);
        assertThat(bang.get(0).isToanQuyen()).isTrue();
        assertThat(bang.get(0).getQuyen()).isEmpty();   // frontend tự tích hết theo SCREENS
        assertThat(bang.get(1).isToanQuyen()).isFalse();
        assertThat(bang.get(1).getQuyen()).containsExactlyInAnyOrder("dashboard", "hoa-don");
        // gom bằng 1 truy vấn, không hỏi từng nhân viên -> tránh N+1
        verify(quyenRepo, never()).findManHinhByNhanVien(anyInt());
    }

    @Test
    @DisplayName("Lưới bỏ nhân viên đã xóa mềm")
    void bangQuyenBoNhanVienDaXoa() {
        NhanVien daXoa = nv(3, "NV", "dashboard");
        daXoa.setTrangThaiXoa(true);
        when(nhanVienRepo.findAll()).thenReturn(List.of(nv(2, "NV", "dashboard"), daXoa));
        when(quyenRepo.findAll()).thenReturn(List.of());

        assertThat(service.bangQuyen()).extracting(NhanVienQuyenDto::getIdNhanVien).containsExactly(2);
    }

    @Test
    @DisplayName("Nhân viên không tồn tại -> lỗi rõ ràng")
    void nhanVienKhongTonTai() {
        when(nhanVienRepo.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.quyenCsv(99))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("không tồn tại");
    }
}
