package com.vn.test.bshoes.service.impl;

import com.vn.test.bshoes.dto.ThongKeDoanhThuDto;
import com.vn.test.bshoes.dto.ThongKeSanPhamDto;
import com.vn.test.bshoes.repository.ThongKeRepository;
import com.vn.test.bshoes.service.ThongKeService;
import org.springframework.stereotype.Service;

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
                .map(r -> new ThongKeSanPhamDto((String)r[0], (String)r[2], (String)r[1], (String)r[3], (String)r[4], (String)r[5], i(r[6])))
                .toList();
    }

    @Override
    public List<Integer> loatNam() {
        return repo.loatNam();
    }
}
