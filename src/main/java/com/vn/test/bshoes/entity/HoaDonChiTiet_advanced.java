package com.vn.test.bshoes.entity;
import java.math.BigDecimal;
import java.sql.Timestamp;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor     // Tự sinh constructor đầy đủ tham số
@NoArgsConstructor      // Tự sinh constructor rỗng
@Data                   // Tự sinh getter/setter, toString(), equals(), hashCode()
@Builder                // Tạo pattern builder để khởi tạo object đẹp hơn

/**
 *
 * @author DELL
 */
public class HoaDonChiTiet_advanced {
    private int id_hoa_don_chi_tiet;
    private int id_san_pham_chi_tiet;
    private int id_hoa_don;
    private BigDecimal gia_giam ;
    private int so_luong;
    private Timestamp ngay_tao_ma;
    private Timestamp ngay_cap_nhat;
    private String nguoi_tao;
    private String nguoi_cap_nhat;
    private boolean trang_thai;

    //fk
    private String ma_san_pham_chi_tiet;
    private String ten_san_pham;
    private BigDecimal don_gia;
    private int so_luong_ton;
    private String ten_kich_co;
    private String ten_mau_sac;
    private BigDecimal thanh_tien;
}
