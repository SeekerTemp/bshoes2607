package com.vn.test.bshoes.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Nationalized;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "san_pham_chi_tiet", schema = "dbo")
public class SanPhamChiTiet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_san_pham_chi_tiet", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_san_pham")
    private SanPham idSanPham;

    // Màu sắc và kích cỡ là thuộc tính của BIẾN THỂ, không phải của sản phẩm cha.
    // Bắt buộc, nếu không hai biến thể của cùng một sản phẩm sẽ không phân biệt được.
    @NotNull(message = "Biến thể phải có kích cỡ")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_kich_co")
    private KichCo idKichCo;

    @NotNull(message = "Biến thể phải có màu sắc")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_mau_sac")
    private MauSac idMauSac;

    @Column(name = "ma_san_pham_chi_tiet", length = 50)
    private String maSanPhamChiTiet;

    @NotNull(message = "Biến thể phải có số lượng tồn")
    @Min(value = 0, message = "Số lượng tồn không được âm")
    @Column(name = "so_luong_ton")
    private Integer soLuongTon;

    @NotNull(message = "Biến thể phải có đơn giá")
    @DecimalMin(value = "1", message = "Đơn giá phải lớn hơn 0")
    @Column(name = "don_gia")
    private BigDecimal donGia;

    @Column(name = "gia_nhap")
    private BigDecimal giaNhap;

    @ColumnDefault("getdate()")
    @Column(name = "ngay_tao")
    private Instant ngayTao;

    @ColumnDefault("getdate()")
    @Column(name = "ngay_cap_nhat")
    private Instant ngayCapNhat;

    @Nationalized
    @Column(name = "nguoi_tao", length = 50)
    private String nguoiTao;

    @Nationalized
    @Column(name = "nguoi_cap_nhat", length = 50)
    private String nguoiCapNhat;

    @Column(name = "trang_thai")
    private Boolean trangThai;

    @ColumnDefault("0")
    @Column(name = "trang_thai_xoa")
    private Boolean trangThaiXoa;

    @Column(name = "image_url", length = 255)
    private String imageUrl;

    @OneToMany(mappedBy = "idSanPhamChiTiet")
    private Set<HoaDonChiTiet> hoaDonChiTiets = new LinkedHashSet<>();

    /**
     * Biến thể MỚI chỉ được mở bán khi vừa có giá > 0 vừa có hàng. Đây chính là chỗ
     * chặn lỗi "sản phẩm bày bán mà không có giá": không thể tạo một biến thể rỗng rồi
     * để nó nằm trong danh sách đang bán.
     */
    @PrePersist
    void moBanKhiDuDieuKien() {
        boolean duDieuKien = coGiaBan() && soLuongTon != null && soLuongTon > 0;
        trangThai = duDieuKien && !Boolean.FALSE.equals(trangThai);
    }

    /**
     * Khi cập nhật thì chỉ giá mới là điều kiện cứng. Hết hàng KHÔNG tự chuyển sang
     * ngừng bán: {@code SanPhamChiTietRepository.findForStore()} cố ý giữ lại biến thể
     * tồn 0 để khách đặt trước, nên tự tắt sẽ làm hỏng luồng đó.
     *
     * Lưu ý: decrementStock/incrementStock là bulk JPQL nên không kích hoạt callback
     * này — đúng như mong muốn, bán hết hàng không được đổi trạng thái.
     */
    @PreUpdate
    void dongBanKhiMatGia() {
        if (!coGiaBan()) trangThai = false;
    }

    private boolean coGiaBan() {
        return donGia != null && donGia.signum() > 0;
    }
}