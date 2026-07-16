package com.vn.test.bshoes.service.impl;

import com.vn.test.bshoes.dto.ThongKeDoanhThuDto;
import com.vn.test.bshoes.dto.ThongKeDongTienDto;
import com.vn.test.bshoes.dto.ThongKeRetentionDto;
import com.vn.test.bshoes.dto.ThongKeRoiDto;
import com.vn.test.bshoes.dto.ThongKeSanPhamDto;
import com.vn.test.bshoes.dto.ThongKeTongQuanDto;
import com.vn.test.bshoes.dto.ThongKeTrendingDto;
import com.vn.test.bshoes.repository.ThongKeRepository;
import com.vn.test.bshoes.service.ThongKeService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;

@Service
public class ThongKeServiceImpl implements ThongKeService {

    private final ThongKeRepository repo;

    public ThongKeServiceImpl(ThongKeRepository repo) {
        this.repo = repo;
    }

    private static Integer i(Object o){ return o==null?null:((Number)o).intValue(); }
    private static java.math.BigDecimal bd(Object o){ if(o==null) return null; if(o instanceof java.math.BigDecimal b) return b; return java.math.BigDecimal.valueOf(((Number)o).doubleValue()); }

    @Override
    public List<ThongKeDoanhThuDto> homNay() {
        return repo.homNay().stream()
                .map(r -> new ThongKeDoanhThuDto(i(r[0]), null, null, null, bd(r[4]), i(r[1]), i(r[2]), i(r[3])))
                .toList();
    }

    @Override
    public List<ThongKeDoanhThuDto> theoNgay(Date from, Date to) {
        return repo.theoNgay(from, to).stream()
                .map(r -> new ThongKeDoanhThuDto(null, null, null, null, bd(r[3]), i(r[0]), i(r[1]), i(r[2])))
                .toList();
    }

    @Override
    public List<ThongKeDoanhThuDto> theoThang(int thang, int nam) {
        return repo.theoThang(thang, nam).stream()
                .map(r -> new ThongKeDoanhThuDto(i(r[0]), i(r[1]), bd(r[2]), bd(r[3]), bd(r[4]), i(r[5]), i(r[6]), i(r[7])))
                .toList();
    }

    @Override
    public List<ThongKeDoanhThuDto> theoNam(int nam) {
        return repo.theoNam(nam).stream()
                .map(r -> new ThongKeDoanhThuDto(i(r[0]), i(r[1]), bd(r[2]), bd(r[3]), bd(r[4]), null, null, null))
                .toList();
    }

    @Override
    public List<ThongKeSanPhamDto> tatCaSanPham() {
        return repo.tatCaSanPham().stream()
                .map(r -> new ThongKeSanPhamDto((String)r[0], (String)r[2], (String)r[1], (String)r[3], (String)r[4], (String)r[5], i(r[6]), i(r[7]), bd(r[8])))
                .toList();
    }

    @Override
    public List<Integer> loatNam() {
        return repo.loatNam();
    }

    @Override
    public ThongKeTongQuanDto tongQuan() {
        List<Object[]> rows = repo.tongQuanRaw();
        Object[] r = rows.isEmpty() ? new Object[6] : rows.get(0);
        BigDecimal doanhThu = bd(r[0]) == null ? BigDecimal.ZERO : bd(r[0]);
        BigDecimal doanhThuThang = bd(r[1]) == null ? BigDecimal.ZERO : bd(r[1]);
        Integer soDon = i(r[2]) == null ? 0 : i(r[2]);
        Integer thanhCong = i(r[3]) == null ? 0 : i(r[3]);
        Integer cho = i(r[4]) == null ? 0 : i(r[4]);
        Integer huy = i(r[5]) == null ? 0 : i(r[5]);
        Long ban = repo.soSanPhamBan();
        BigDecimal giaVon = bd(repo.giaVonTong());
        BigDecimal loiNhuan = doanhThu.subtract(giaVon == null ? BigDecimal.ZERO : giaVon);
        return new ThongKeTongQuanDto(doanhThu, loiNhuan, doanhThuThang, soDon, thanhCong, cho, huy,
                ban == null ? 0 : ban.intValue());
    }

    @Override
    public List<ThongKeDongTienDto> dongTien(int nam) {
        return repo.dongTien(nam).stream()
                .map(r -> new ThongKeDongTienDto(i(r[0]), bd(r[1]), bd(r[2]), bd(r[3])))
                .toList();
    }

    @Override
    public List<ThongKeRoiDto> roiSanPham() {
        return repo.roiSanPham().stream().map(r -> {
            BigDecimal doanhThu = bd(r[1]) == null ? BigDecimal.ZERO : bd(r[1]);
            BigDecimal giaVon = bd(r[2]) == null ? BigDecimal.ZERO : bd(r[2]);
            BigDecimal roi = giaVon.signum() == 0 ? BigDecimal.ZERO
                    : doanhThu.subtract(giaVon).multiply(BigDecimal.valueOf(100))
                        .divide(giaVon, 0, java.math.RoundingMode.HALF_UP);
            return new ThongKeRoiDto((String) r[0], doanhThu, giaVon, roi);
        }).toList();
    }

    @Override
    public ThongKeRetentionDto retention() {
        List<Object[]> rows = repo.retention();
        Object[] r = rows.isEmpty() ? new Object[2] : rows.get(0);
        int quayLai = i(r[0]) == null ? 0 : i(r[0]);
        int moi = i(r[1]) == null ? 0 : i(r[1]);
        int tong = quayLai + moi;
        BigDecimal tyLe = tong == 0 ? BigDecimal.ZERO
                : BigDecimal.valueOf(quayLai * 100L).divide(BigDecimal.valueOf(tong), 0, java.math.RoundingMode.HALF_UP);
        return new ThongKeRetentionDto(quayLai, moi, tyLe);
    }

    @Override
    public List<ThongKeTrendingDto> trending(int nam) {
        return repo.trending(nam).stream()
                .map(r -> new ThongKeTrendingDto((String) r[0], i(r[1]), i(r[2])))
                .toList();
    }
}
