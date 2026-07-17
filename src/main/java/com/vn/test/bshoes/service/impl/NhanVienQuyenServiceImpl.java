package com.vn.test.bshoes.service.impl;

import com.vn.test.bshoes.dto.NhanVienQuyenDto;
import com.vn.test.bshoes.entity.NhanVien;
import com.vn.test.bshoes.entity.NhanVienQuyen;
import com.vn.test.bshoes.entity.VaiTro;
import com.vn.test.bshoes.repository.NhanVienQuyenRepository;
import com.vn.test.bshoes.repository.NhanVienRepository;
import com.vn.test.bshoes.service.NhanVienQuyenService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class NhanVienQuyenServiceImpl implements NhanVienQuyenService {

    /** vai_tro.quyen của vai trò toàn quyền. */
    private static final String TAT_CA = "*";

    private final NhanVienQuyenRepository repo;
    private final NhanVienRepository nhanVienRepository;

    public NhanVienQuyenServiceImpl(NhanVienQuyenRepository repo, NhanVienRepository nhanVienRepository) {
        this.repo = repo;
        this.nhanVienRepository = nhanVienRepository;
    }

    private boolean laToanQuyen(NhanVien n) {
        VaiTro vt = n == null ? null : n.getIdVaiTro();
        return vt != null && TAT_CA.equals(vt.getQuyen() == null ? null : vt.getQuyen().trim());
    }

    /** Tách CSV template của vai trò thành danh sách key, bỏ trùng và giữ thứ tự. */
    private List<String> tachTemplate(NhanVien n) {
        VaiTro vt = n.getIdVaiTro();
        String q = vt == null ? null : vt.getQuyen();
        if (!StringUtils.hasText(q) || TAT_CA.equals(q.trim())) return List.of();
        return new ArrayList<>(new LinkedHashSet<>(
                java.util.Arrays.stream(q.split(",")).map(String::trim).filter(s -> !s.isEmpty()).toList()));
    }

    private NhanVien require(int id) {
        return nhanVienRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Nhân viên không tồn tại."));
    }

    private NhanVienQuyenDto toDto(NhanVien n, List<String> quyen) {
        NhanVienQuyenDto d = new NhanVienQuyenDto();
        d.setIdNhanVien(n.getId());
        d.setMa(n.getMaNhanVien());
        d.setTen(n.getTenNhanVien());
        VaiTro vt = n.getIdVaiTro();
        d.setVaiTro(vt != null ? vt.getTenVaiTro() : null);
        d.setIdVaiTro(vt != null ? vt.getId() : null);
        boolean full = laToanQuyen(n);
        d.setToanQuyen(full);
        // ADMIN: không trả danh sách — frontend tự tích hết theo SCREENS và khóa ô lại.
        d.setQuyen(full ? List.of() : quyen);
        return d;
    }

    @Override
    @Transactional(readOnly = true)
    public String quyenCsv(int idNhanVien) {
        NhanVien n = require(idNhanVien);
        if (laToanQuyen(n)) return TAT_CA;
        return String.join(",", repo.findManHinhByNhanVien(idNhanVien));
    }

    @Override
    @Transactional(readOnly = true)
    public List<NhanVienQuyenDto> bangQuyen() {
        List<NhanVien> nhanViens = nhanVienRepository.findAll().stream()
                .filter(n -> n.getTrangThaiXoa() == null || !n.getTrangThaiXoa())
                .toList();
        // Gom 1 lượt rồi map trong bộ nhớ — tránh N+1 query khi lưới có nhiều dòng.
        Map<Integer, List<String>> theoNhanVien = repo.findAll().stream()
                .collect(Collectors.groupingBy(NhanVienQuyen::getIdNhanVien,
                        Collectors.mapping(NhanVienQuyen::getManHinh, Collectors.toList())));
        return nhanViens.stream()
                .map(n -> toDto(n, theoNhanVien.getOrDefault(n.getId(), List.of())))
                .toList();
    }

    @Override
    @Transactional
    public NhanVienQuyenDto luuQuyen(int idNhanVien, List<String> manHinhs) {
        NhanVien n = require(idNhanVien);
        if (laToanQuyen(n)) {
            throw new IllegalStateException("Vai trò quản trị luôn có toàn quyền — không cần phân quyền riêng.");
        }
        repo.deleteByNhanVien(idNhanVien);
        List<String> sach = manHinhs == null ? List.<String>of()
                : new ArrayList<>(new LinkedHashSet<>(manHinhs.stream()
                        .filter(StringUtils::hasText).map(String::trim).toList()));
        repo.saveAll(sach.stream().map(m -> new NhanVienQuyen(idNhanVien, m)).toList());
        return toDto(n, sach);
    }

    @Override
    @Transactional
    public NhanVienQuyenDto apTemplate(int idNhanVien) {
        NhanVien n = require(idNhanVien);
        if (laToanQuyen(n)) return toDto(n, List.of());
        List<String> template = tachTemplate(n);
        repo.deleteByNhanVien(idNhanVien);
        repo.saveAll(template.stream().map(m -> new NhanVienQuyen(idNhanVien, m)).toList());
        return toDto(n, template);
    }
}
