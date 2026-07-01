/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package ui;

import com.formdev.flatlaf.FlatLightLaf;
import daoipml.DaoImpl_ChatLieu;
import daoipml.DaoImpl_KieuDayGiay;
import daoipml.DaoImpl_KichThuoc_ql;
import daoipml.DaoImpl_KieuCoGiay;
import daoipml.DaoImpl_KieuDang;
import daoipml.DaoImpl_MauSac_ql;
import daoipml.DaoImpl_ThuongHieu;
import daoipml.DaoImpl_XuatXu;
import entity.ChatLieu;
import entity.KieuDayGiay;
import entity.KichCo;
import entity.KieuCoGiay;
import entity.KieuDang;
import entity.MauSac;
import entity.SanPham_ql;
import entity.SanPhamChiTiet_ql;
import entity.ThuongHieu;
import entity.XuatXu;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import poly.cafe.util.XDialog;
import dao.Dao_KieuCoGiay;
import entity.LoaiSanPham;
import javax.swing.table.TableColumn;
import controller.Controller_SanPham_ql;

/**
 *
 * @author DELL
 */
public class Pnl_3_qlSanPham extends javax.swing.JPanel implements Controller_SanPham_ql {

    private DefaultTableModel mol = new DefaultTableModel();
    private DefaultTableModel mol2 = new DefaultTableModel();
    private DefaultTableModel mol3 = new DefaultTableModel();

    private daoipml.DaoImpl_SanPham_ql repoSanPham = new daoipml.DaoImpl_SanPham_ql();
    private daoipml.DaoImpl_SanPhamChiTiet_ql repoSPCT = new daoipml.DaoImpl_SanPhamChiTiet_ql();
    private DaoImpl_ChatLieu repoCL = new DaoImpl_ChatLieu();
    private DaoImpl_KieuDayGiay repoDG = new DaoImpl_KieuDayGiay();
    private DaoImpl_KichThuoc_ql repoKT = new DaoImpl_KichThuoc_ql();
    private daoipml.DaoImpl_KieuCoGiay repoKCG = new DaoImpl_KieuCoGiay();
    private daoipml.DaoImpl_KieuDang repoKD = new DaoImpl_KieuDang();
    private daoipml.DaoImpl_MauSac_ql repoMS = new DaoImpl_MauSac_ql();
    private daoipml.DaoImpl_ThuongHieu repoTH = new DaoImpl_ThuongHieu();
    private daoipml.DaoImpl_XuatXu repoXX = new DaoImpl_XuatXu();
    private daoipml.DaoImpl_LoaiSanPham repoLSP = new daoipml.DaoImpl_LoaiSanPham();
    private Integer selectedIdSanPham = null;

    private int index = -1;
    private int taqwe = -1;

    /**
     * Creates new form panel_1_sanPhamA
     */
    public Pnl_3_qlSanPham() {
        try {
            FlatLightLaf.setup(); // Consistent flat look
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        initComponents();
        
        taqwe = tbl_obj.getSelectedRow();
        this.fillToTable();
        this.fillToTableSPCT();
        this.fillToTableLoaiSanPham();
        section_1.removeTabAt(3);
        this.checkSoLuongTon();
    }

    public void actionUpdateThuocTinh() {

        if (rdo_loaisanpham.isSelected()) {
            LoaiSanPham x = this.getFormLoaiSP();
            repoLSP.update(x);
            this.fillToTableLoaiSanPham();
             JOptionPane.showMessageDialog(this, "Sửa thành công!");
            return;
        }

        if (rdo_chatlieu.isSelected()) {
            ChatLieu x = this.getFormCL();
            repoCL.update(x);
            this.fillToTableChatLieu();
             JOptionPane.showMessageDialog(this, "Sửa thành công!");
            return;
        }

        if (rdo_mausac.isSelected()) {
            MauSac x = this.getFormMS();
            repoMS.update(x);
            this.fillToTableMauSac();
             JOptionPane.showMessageDialog(this, "Sửa thành công!");
            return;
        }

        if (rdo_kichthuoc.isSelected()) {
            KichCo x = this.getFormKT();
            repoKT.update(x);
            this.fillToTableKichThuoc();
             JOptionPane.showMessageDialog(this, "Sửa thành công!");
            return;
        }

        if (rdo_kieudang.isSelected()) {
            KieuDang x = this.getFormKD();
            repoKD.update(x);
            this.fillToTableKieuDang();
             JOptionPane.showMessageDialog(this, "Sửa thành công!");
            return;
        }

        if (rdo_keiucogiay.isSelected()) {
            KieuCoGiay x = this.getFormKCG();
            repoKCG.update(x);
            fillToTableKieuCoGiay();
             JOptionPane.showMessageDialog(this, "Sửa thành công!");
            return;
        }

        if (rdo_kieudaygiay.isSelected()) {
            KieuDayGiay x = this.getFormDG();
            repoDG.update(x);
            fillToTableKieuDayGiay();
             JOptionPane.showMessageDialog(this, "Sửa thành công!");
            return;
        }

        if (rdo_thuonghieu.isSelected()) {
            ThuongHieu x = this.getFormTH();
            repoTH.update(x);
            fillToTableKieuThuongHieu();
             JOptionPane.showMessageDialog(this, "Sửa thành công!");
            return;
        }

        if (rdo_xuatsu.isSelected()) {
            XuatXu x = this.getFormXS();
            repoXX.update(x);
            fillToTableKieuXuatSu();
             JOptionPane.showMessageDialog(this, "Sửa thành công!");
            return;
        }
    }

    public void actionAddThuocTinh() {

        if (rdo_loaisanpham.isSelected()) {
            LoaiSanPham x = this.getFormLoaiSP();
            repoLSP.create(x);
            this.fillToTableLoaiSanPham();
             JOptionPane.showMessageDialog(this, "Thêm thành công!");
            return;
        }

        if (rdo_chatlieu.isSelected()) {
            ChatLieu x = this.getFormCL();
            repoCL.create(x);
            this.fillToTableChatLieu();
            JOptionPane.showMessageDialog(this, "Thêm thành công!");
            return;
        }

        if (rdo_mausac.isSelected()) {
            MauSac x = this.getFormMS();
            repoMS.create(x);
            this.fillToTableMauSac();
            JOptionPane.showMessageDialog(this, "Thêm thành công!");
            return;
        }

        if (rdo_kichthuoc.isSelected()) {
            KichCo x = this.getFormKT();
            repoKT.create(x);
            this.fillToTableKichThuoc();
            JOptionPane.showMessageDialog(this, "Thêm thành công!");
            return;
        }

        if (rdo_kieudang.isSelected()) {
            KieuDang x = this.getFormKD();
            repoKD.create(x);
            this.fillToTableKieuDang();
            JOptionPane.showMessageDialog(this, "Thêm thành công!");
            return;
        }

        if (rdo_keiucogiay.isSelected()) {
            KieuCoGiay x = this.getFormKCG();
            repoKCG.create(x);
            fillToTableKieuCoGiay();
            JOptionPane.showMessageDialog(this, "Thêm thành công!");
            return;
        }

        if (rdo_kieudaygiay.isSelected()) {
            KieuDayGiay x = this.getFormDG();
            repoDG.create(x);
            fillToTableKieuDayGiay();
            JOptionPane.showMessageDialog(this, "Thêm thành công!");
            return;
        }

        if (rdo_thuonghieu.isSelected()) {
            ThuongHieu x = this.getFormTH();
            repoTH.create(x);
            fillToTableKieuThuongHieu();
            JOptionPane.showMessageDialog(this, "Thêm thành công!");
            return;
        }

        if (rdo_xuatsu.isSelected()) {
            XuatXu x = this.getFormXS();
            repoXX.create(x);
            fillToTableKieuXuatSu();
            JOptionPane.showMessageDialog(this, "Thêm thành công!");
            return;
        }
    }
public void ClearThuocTinh() {

    // Xóa trắng form chung, không ảnh hưởng tới logic
    clearFormThuocTinh();

    // Kiểm tra đúng radio
    if (rdo_loaisanpham.isSelected()) {
        fillToTableLoaiSanPham();  // load lại bảng loại sản phẩm
        
        return;
    }

    if (rdo_chatlieu.isSelected()) {
        fillToTableChatLieu();     // load lại bảng chất liệu
        
        return;
    }

    if (rdo_mausac.isSelected()) {
        fillToTableMauSac();       // load lại bảng màu sắc
        return;
    }

    if (rdo_kichthuoc.isSelected()) {
        fillToTableKichThuoc();
        return;
    }

    if (rdo_kieudang.isSelected()) {
        fillToTableKieuDang();
        return;
    }

    if (rdo_keiucogiay.isSelected()) {
        fillToTableKieuCoGiay();
        return;
    }

    if (rdo_kieudaygiay.isSelected()) {
        fillToTableKieuDayGiay();
        return;
    }

    if (rdo_thuonghieu.isSelected()) {
        fillToTableKieuThuongHieu();
        return;
    }

    if (rdo_xuatsu.isSelected()) {
        fillToTableKieuXuatSu();
        return;
    }
}
public void actionDeleteThuocTinh() {

    int row = tbl_thuoctinh.getSelectedRow();
    if (row == -1) {
        JOptionPane.showMessageDialog(this, "Vui lòng chọn một dòng để xóa!");
        return;
    }

    int id = Integer.parseInt(tbl_thuoctinh.getValueAt(row, 0).toString()); // lấy ID

    if (rdo_chatlieu.isSelected()) {
        repoCL.deleteById(id);
        fillToTableChatLieu();
        clearFormThuocTinh();
        JOptionPane.showMessageDialog(this, "Xóa thành công!");
        return;
    }

    if (rdo_loaisanpham.isSelected()) {
        repoLSP.deleteById(id);
        fillToTableLoaiSanPham();
        clearFormThuocTinh();
        JOptionPane.showMessageDialog(this, "Xóa thành công!");
        return;
    }
 // MÀU SẮC
    if (rdo_mausac.isSelected()) {
        repoMS.deleteById(id);
        fillToTableMauSac();
        clearFormThuocTinh();
        JOptionPane.showMessageDialog(this, "Xóa thành công!");
        return;
    }

    // KÍCH THƯỚC
    if (rdo_kichthuoc.isSelected()) {
        repoKT.deleteById(id);
        fillToTableKichThuoc();
        clearFormThuocTinh();
        JOptionPane.showMessageDialog(this, "Xóa thành công!");
        return;
    }

    // KIỂU DÁNG
    if (rdo_kieudang.isSelected()) {
        repoKD.deleteById(id);
        fillToTableKieuDang();
        clearFormThuocTinh();
        JOptionPane.showMessageDialog(this, "Xóa thành công!");
        return;
    }

    // KIỂU CỔ GIÀY
    if (rdo_keiucogiay.isSelected()) {
        repoKCG.deleteById(id);
        fillToTableKieuCoGiay();
        clearFormThuocTinh();
        JOptionPane.showMessageDialog(this, "Xóa thành công!");
        return;
    }

    // KIỂU DÂY GIÀY
    if (rdo_kieudaygiay.isSelected()) {
        repoDG.deleteById(id);
        fillToTableKieuDayGiay();
        clearFormThuocTinh();
        JOptionPane.showMessageDialog(this, "Xóa thành công!");
        return;
    }

    // THƯƠNG HIỆU
    if (rdo_thuonghieu.isSelected()) {
        repoTH.deleteById(id);
        fillToTableKieuThuongHieu();
        clearFormThuocTinh();
        JOptionPane.showMessageDialog(this, "Xóa thành công!");
        return;
    }

    // XUẤT XỨ
    if (rdo_xuatsu.isSelected()) {
        repoXX.deleteById(id);
        fillToTableKieuXuatSu();
        clearFormThuocTinh();
        JOptionPane.showMessageDialog(this, "Xóa thành công!");
        return;
    }
    
}


    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        rdogrp_trangThai = new javax.swing.ButtonGroup();
        rdo_thuoctinh = new javax.swing.ButtonGroup();
        section_1 = new javax.swing.JTabbedPane();
        section_1_SanPham = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tbl_obj = new javax.swing.JTable();
        jLabel3 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        section_2_tab_2 = new javax.swing.JPanel();
        field_1 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        txt_objMaSanPham = new javax.swing.JTextField();
        field_6 = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        cbo_chatlieu = new javax.swing.JComboBox<>();
        field_20 = new javax.swing.JPanel();
        jLabel31 = new javax.swing.JLabel();
        cbo_kieudang = new javax.swing.JComboBox<>();
        field_21 = new javax.swing.JPanel();
        jLabel32 = new javax.swing.JLabel();
        cbo_kieucogiay = new javax.swing.JComboBox<>();
        field_22 = new javax.swing.JPanel();
        jLabel33 = new javax.swing.JLabel();
        cbo_kieudaygiay = new javax.swing.JComboBox<>();
        field_23 = new javax.swing.JPanel();
        jLabel34 = new javax.swing.JLabel();
        cbo_thuonghieu = new javax.swing.JComboBox<>();
        field_24 = new javax.swing.JPanel();
        jLabel35 = new javax.swing.JLabel();
        cbo_xuatxu = new javax.swing.JComboBox<>();
        field_4 = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        txt_objMoTa = new javax.swing.JTextField();
        field_29 = new javax.swing.JPanel();
        jLabel40 = new javax.swing.JLabel();
        cbo_LoaiSP = new javax.swing.JComboBox<>();
        field_5 = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        txt_objTenSanPham = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        section_2_tab_3 = new javax.swing.JPanel();
        btn_sua = new javax.swing.JButton();
        btn_them = new javax.swing.JButton();
        btn_lammoi = new javax.swing.JButton();
        btn_xoa = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        btn_timkiemsp = new javax.swing.JButton();
        section_2_sanPhamChiTiet = new javax.swing.JPanel();
        scollpanel = new javax.swing.JScrollPane();
        tbl_spct = new javax.swing.JTable();
        jLabel13 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        section_2_tab_4 = new javax.swing.JPanel();
        field_3 = new javax.swing.JPanel();
        jLabel14 = new javax.swing.JLabel();
        txt_mspct = new javax.swing.JTextField();
        field_8 = new javax.swing.JPanel();
        jLabel15 = new javax.swing.JLabel();
        txt_slt = new javax.swing.JTextField();
        field_9 = new javax.swing.JPanel();
        jLabel16 = new javax.swing.JLabel();
        txt_dongiaspct = new javax.swing.JTextField();
        field_10 = new javax.swing.JPanel();
        jLabel17 = new javax.swing.JLabel();
        txt_tenspct = new javax.swing.JTextField();
        field_11 = new javax.swing.JPanel();
        jLabel18 = new javax.swing.JLabel();
        rdo_dangban = new javax.swing.JRadioButton();
        rdo_ngungban = new javax.swing.JRadioButton();
        field_27 = new javax.swing.JPanel();
        jLabel38 = new javax.swing.JLabel();
        cbo_mausac = new javax.swing.JComboBox<>();
        field_28 = new javax.swing.JPanel();
        jLabel39 = new javax.swing.JLabel();
        cbo_kichco = new javax.swing.JComboBox<>();
        field_7 = new javax.swing.JPanel();
        jLabel19 = new javax.swing.JLabel();
        cbo_sanpham = new javax.swing.JComboBox<>();
        jLabel20 = new javax.swing.JLabel();
        section_2_tab_5 = new javax.swing.JPanel();
        btn_suaspct = new javax.swing.JButton();
        btn_lammoispct = new javax.swing.JButton();
        btn_xoaspct = new javax.swing.JButton();
        btn_themspct = new javax.swing.JButton();
        btn_timkiemsp1 = new javax.swing.JButton();
        section_3_thuocTinh = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tbl_thuoctinh = new javax.swing.JTable();
        jLabel22 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        section_2_tab_6 = new javax.swing.JPanel();
        field_16 = new javax.swing.JPanel();
        jLabel26 = new javax.swing.JLabel();
        txt_motathuoctinh = new javax.swing.JTextField();
        field_17 = new javax.swing.JPanel();
        jLabel27 = new javax.swing.JLabel();
        rdo_chatlieu = new javax.swing.JRadioButton();
        rdo_mausac = new javax.swing.JRadioButton();
        rdo_kichthuoc = new javax.swing.JRadioButton();
        rdo_kieudang = new javax.swing.JRadioButton();
        rdo_keiucogiay = new javax.swing.JRadioButton();
        rdo_kieudaygiay = new javax.swing.JRadioButton();
        rdo_thuonghieu = new javax.swing.JRadioButton();
        rdo_loaisanpham = new javax.swing.JRadioButton();
        rdo_xuatsu = new javax.swing.JRadioButton();
        field_18 = new javax.swing.JPanel();
        jLabel28 = new javax.swing.JLabel();
        txt_mathuoctinh = new javax.swing.JTextField();
        field_19 = new javax.swing.JPanel();
        jLabel30 = new javax.swing.JLabel();
        txt_tenthuoctinh = new javax.swing.JTextField();
        jLabel29 = new javax.swing.JLabel();
        section_2_tab_7 = new javax.swing.JPanel();
        btn_editInsert2 = new javax.swing.JButton();
        btn_themthuoctinh = new javax.swing.JButton();
        btn_formFillToTable2 = new javax.swing.JButton();
        btn_editDelete2 = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tbl_recycle = new javax.swing.JTable();
        jButton2 = new javax.swing.JButton();

        setBackground(new java.awt.Color(235, 249, 245));
        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        section_1.setToolTipText("tab");
        section_1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                section_1MouseClicked(evt);
            }
        });

        section_1_SanPham.setBackground(new java.awt.Color(250, 250, 250));
        section_1_SanPham.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        section_1_SanPham.setPreferredSize(new java.awt.Dimension(613, 637));
        section_1_SanPham.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jScrollPane1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jScrollPane1MouseClicked(evt);
            }
        });

        tbl_obj.setFont(new java.awt.Font("Segoe UI", 0, 10)); // NOI18N
        tbl_obj.setForeground(new java.awt.Color(6, 71, 46));
        tbl_obj.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "STT", "Mã SP", "Tên SP", "Chất liệu", "Kiểu dáng", "Cổ giày", "Dây giày", "Thương hiệu", "Xuất xứ", "Title 10"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tbl_obj.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbl_objMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tbl_obj);
        if (tbl_obj.getColumnModel().getColumnCount() > 0) {
            tbl_obj.getColumnModel().getColumn(0).setPreferredWidth(28);
        }

        section_1_SanPham.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(13, 40, 587, 560));

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        jLabel3.setText("Sản phẩm");
        section_1_SanPham.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(13, 7, -1, -1));

        jPanel2.setBackground(new java.awt.Color(38, 159, 114));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        section_2_tab_2.setBackground(new java.awt.Color(250, 250, 250));
        section_2_tab_2.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        section_2_tab_2.setForeground(new java.awt.Color(250, 250, 250));
        section_2_tab_2.setPreferredSize(new java.awt.Dimension(386, 637));
        section_2_tab_2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        field_1.setBackground(new java.awt.Color(250, 250, 250));
        field_1.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        field_1.setForeground(new java.awt.Color(250, 250, 250));
        field_1.setPreferredSize(new java.awt.Dimension(386, 637));
        field_1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 9)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(14, 159, 103));
        jLabel6.setText("Mã sản phẩm:");
        field_1.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(7, 4, 100, 11));

        txt_objMaSanPham.setFont(new java.awt.Font("Segoe UI", 0, 9)); // NOI18N
        field_1.add(txt_objMaSanPham, new org.netbeans.lib.awtextra.AbsoluteConstraints(113, 1, 240, 17));

        section_2_tab_2.add(field_1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 360, 20));

        field_6.setBackground(new java.awt.Color(250, 250, 250));
        field_6.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        field_6.setForeground(new java.awt.Color(250, 250, 250));
        field_6.setPreferredSize(new java.awt.Dimension(386, 637));
        field_6.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel11.setFont(new java.awt.Font("Segoe UI", 1, 9)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(14, 159, 103));
        jLabel11.setText("Chất liệu:");
        field_6.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(7, 4, 100, 11));

        cbo_chatlieu.setFont(new java.awt.Font("Segoe UI", 0, 9)); // NOI18N
        cbo_chatlieu.setForeground(new java.awt.Color(6, 71, 46));
        cbo_chatlieu.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        field_6.add(cbo_chatlieu, new org.netbeans.lib.awtextra.AbsoluteConstraints(113, 1, 240, 17));

        section_2_tab_2.add(field_6, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 60, 360, 20));

        field_20.setBackground(new java.awt.Color(255, 255, 255));
        field_20.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        field_20.setForeground(new java.awt.Color(250, 250, 250));
        field_20.setPreferredSize(new java.awt.Dimension(386, 637));
        field_20.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel31.setFont(new java.awt.Font("Segoe UI", 1, 9)); // NOI18N
        jLabel31.setForeground(new java.awt.Color(14, 159, 103));
        jLabel31.setText("Kiểu dáng:");
        field_20.add(jLabel31, new org.netbeans.lib.awtextra.AbsoluteConstraints(7, 4, 100, 11));

        cbo_kieudang.setFont(new java.awt.Font("Segoe UI", 0, 9)); // NOI18N
        cbo_kieudang.setForeground(new java.awt.Color(6, 71, 46));
        cbo_kieudang.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        field_20.add(cbo_kieudang, new org.netbeans.lib.awtextra.AbsoluteConstraints(113, 1, 240, 17));

        section_2_tab_2.add(field_20, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 80, 360, 20));

        field_21.setBackground(new java.awt.Color(250, 250, 250));
        field_21.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        field_21.setForeground(new java.awt.Color(250, 250, 250));
        field_21.setPreferredSize(new java.awt.Dimension(386, 637));
        field_21.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel32.setFont(new java.awt.Font("Segoe UI", 1, 9)); // NOI18N
        jLabel32.setForeground(new java.awt.Color(14, 159, 103));
        jLabel32.setText("Kiểu cổ giày");
        field_21.add(jLabel32, new org.netbeans.lib.awtextra.AbsoluteConstraints(7, 4, 100, 11));

        cbo_kieucogiay.setFont(new java.awt.Font("Segoe UI", 0, 9)); // NOI18N
        cbo_kieucogiay.setForeground(new java.awt.Color(6, 71, 46));
        cbo_kieucogiay.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        field_21.add(cbo_kieucogiay, new org.netbeans.lib.awtextra.AbsoluteConstraints(113, 1, 240, 17));

        section_2_tab_2.add(field_21, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 100, 360, 20));

        field_22.setBackground(new java.awt.Color(255, 255, 255));
        field_22.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        field_22.setForeground(new java.awt.Color(250, 250, 250));
        field_22.setPreferredSize(new java.awt.Dimension(386, 637));
        field_22.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel33.setFont(new java.awt.Font("Segoe UI", 1, 9)); // NOI18N
        jLabel33.setForeground(new java.awt.Color(14, 159, 103));
        jLabel33.setText("Kiểu dây giày");
        field_22.add(jLabel33, new org.netbeans.lib.awtextra.AbsoluteConstraints(7, 4, 100, 11));

        cbo_kieudaygiay.setFont(new java.awt.Font("Segoe UI", 0, 9)); // NOI18N
        cbo_kieudaygiay.setForeground(new java.awt.Color(6, 71, 46));
        cbo_kieudaygiay.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        field_22.add(cbo_kieudaygiay, new org.netbeans.lib.awtextra.AbsoluteConstraints(113, 1, 240, 17));

        section_2_tab_2.add(field_22, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 120, 360, 20));

        field_23.setBackground(new java.awt.Color(250, 250, 250));
        field_23.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        field_23.setForeground(new java.awt.Color(250, 250, 250));
        field_23.setPreferredSize(new java.awt.Dimension(386, 637));
        field_23.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel34.setFont(new java.awt.Font("Segoe UI", 1, 9)); // NOI18N
        jLabel34.setForeground(new java.awt.Color(14, 159, 103));
        jLabel34.setText("Thương hiệu");
        field_23.add(jLabel34, new org.netbeans.lib.awtextra.AbsoluteConstraints(7, 4, 100, 11));

        cbo_thuonghieu.setFont(new java.awt.Font("Segoe UI", 0, 9)); // NOI18N
        cbo_thuonghieu.setForeground(new java.awt.Color(6, 71, 46));
        cbo_thuonghieu.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        field_23.add(cbo_thuonghieu, new org.netbeans.lib.awtextra.AbsoluteConstraints(113, 1, 240, 17));

        section_2_tab_2.add(field_23, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 140, 360, 20));

        field_24.setBackground(new java.awt.Color(255, 255, 255));
        field_24.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        field_24.setForeground(new java.awt.Color(250, 250, 250));
        field_24.setPreferredSize(new java.awt.Dimension(386, 637));
        field_24.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel35.setFont(new java.awt.Font("Segoe UI", 1, 9)); // NOI18N
        jLabel35.setForeground(new java.awt.Color(14, 159, 103));
        jLabel35.setText("Xuất xứ");
        field_24.add(jLabel35, new org.netbeans.lib.awtextra.AbsoluteConstraints(7, 4, 100, 11));

        cbo_xuatxu.setFont(new java.awt.Font("Segoe UI", 0, 9)); // NOI18N
        cbo_xuatxu.setForeground(new java.awt.Color(6, 71, 46));
        cbo_xuatxu.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        field_24.add(cbo_xuatxu, new org.netbeans.lib.awtextra.AbsoluteConstraints(113, 1, 240, 17));

        section_2_tab_2.add(field_24, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 160, 360, 20));

        field_4.setBackground(new java.awt.Color(255, 255, 255));
        field_4.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        field_4.setForeground(new java.awt.Color(250, 250, 250));
        field_4.setPreferredSize(new java.awt.Dimension(386, 637));
        field_4.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel9.setFont(new java.awt.Font("Segoe UI", 1, 9)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(14, 159, 103));
        jLabel9.setText("Mô tả:");
        field_4.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(7, 4, 100, 11));

        txt_objMoTa.setFont(new java.awt.Font("Segoe UI", 0, 9)); // NOI18N
        field_4.add(txt_objMoTa, new org.netbeans.lib.awtextra.AbsoluteConstraints(113, 1, 240, 80));

        section_2_tab_2.add(field_4, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 180, 360, 82));

        field_29.setBackground(new java.awt.Color(255, 255, 255));
        field_29.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        field_29.setForeground(new java.awt.Color(250, 250, 250));
        field_29.setPreferredSize(new java.awt.Dimension(386, 637));
        field_29.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel40.setFont(new java.awt.Font("Segoe UI", 1, 9)); // NOI18N
        jLabel40.setForeground(new java.awt.Color(14, 159, 103));
        jLabel40.setText("Loại Sản phẩm:");
        field_29.add(jLabel40, new org.netbeans.lib.awtextra.AbsoluteConstraints(7, 4, 100, 11));

        cbo_LoaiSP.setFont(new java.awt.Font("Segoe UI", 0, 9)); // NOI18N
        cbo_LoaiSP.setForeground(new java.awt.Color(6, 71, 46));
        cbo_LoaiSP.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        field_29.add(cbo_LoaiSP, new org.netbeans.lib.awtextra.AbsoluteConstraints(113, 1, 240, 17));

        section_2_tab_2.add(field_29, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 20, 360, 20));

        field_5.setBackground(new java.awt.Color(255, 255, 255));
        field_5.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        field_5.setForeground(new java.awt.Color(250, 250, 250));
        field_5.setPreferredSize(new java.awt.Dimension(386, 637));
        field_5.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel10.setFont(new java.awt.Font("Segoe UI", 1, 9)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(14, 159, 103));
        jLabel10.setText("Tên sản phẩm:");
        field_5.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(7, 4, 100, 11));

        txt_objTenSanPham.setFont(new java.awt.Font("Segoe UI", 0, 9)); // NOI18N
        field_5.add(txt_objTenSanPham, new org.netbeans.lib.awtextra.AbsoluteConstraints(113, 1, 240, 17));

        section_2_tab_2.add(field_5, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 40, 360, 20));

        jPanel2.add(section_2_tab_2, new org.netbeans.lib.awtextra.AbsoluteConstraints(13, 35, 360, 394));

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setText("Thông tin sản phẩm");
        jPanel2.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(13, 13, -1, 15));

        section_2_tab_3.setBackground(new java.awt.Color(250, 250, 250));
        section_2_tab_3.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        section_2_tab_3.setForeground(new java.awt.Color(250, 250, 250));
        section_2_tab_3.setPreferredSize(new java.awt.Dimension(386, 637));
        section_2_tab_3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        btn_sua.setBackground(new java.awt.Color(14, 159, 103));
        btn_sua.setFont(new java.awt.Font("Segoe UI", 1, 9)); // NOI18N
        btn_sua.setForeground(new java.awt.Color(255, 255, 255));
        btn_sua.setText("Sửa");
        btn_sua.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_suaActionPerformed(evt);
            }
        });
        section_2_tab_3.add(btn_sua, new org.netbeans.lib.awtextra.AbsoluteConstraints(13, 36, 333, 16));

        btn_them.setBackground(new java.awt.Color(14, 159, 103));
        btn_them.setFont(new java.awt.Font("Segoe UI", 1, 9)); // NOI18N
        btn_them.setForeground(new java.awt.Color(255, 255, 255));
        btn_them.setText("Thêm");
        btn_them.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_themActionPerformed(evt);
            }
        });
        section_2_tab_3.add(btn_them, new org.netbeans.lib.awtextra.AbsoluteConstraints(13, 13, 333, 16));

        btn_lammoi.setText("Làm mới");
        btn_lammoi.setBackground(new java.awt.Color(14, 159, 103));
        btn_lammoi.setFont(new java.awt.Font("Segoe UI", 1, 9)); // NOI18N
        btn_lammoi.setForeground(new java.awt.Color(255, 255, 255));
        btn_lammoi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_lammoiActionPerformed(evt);
            }
        });
        section_2_tab_3.add(btn_lammoi, new org.netbeans.lib.awtextra.AbsoluteConstraints(13, 59, 333, 16));

        btn_xoa.setText("Xóa");
        btn_xoa.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(146, 23, 21), 2));
        btn_xoa.setFont(new java.awt.Font("Segoe UI", 1, 9)); // NOI18N
        btn_xoa.setForeground(new java.awt.Color(146, 23, 21));
        btn_xoa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_xoaActionPerformed(evt);
            }
        });
        section_2_tab_3.add(btn_xoa, new org.netbeans.lib.awtextra.AbsoluteConstraints(13, 82, 333, 16));

        jButton1.setText("Ẩn");
        jButton1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(146, 23, 21), 2));
        jButton1.setFont(new java.awt.Font("Segoe UI", 1, 9)); // NOI18N
        jButton1.setForeground(new java.awt.Color(146, 23, 21));
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        section_2_tab_3.add(jButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(13, 106, 333, 16));

        jPanel2.add(section_2_tab_3, new org.netbeans.lib.awtextra.AbsoluteConstraints(13, 442, 360, 180));

        section_1_SanPham.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(620, 7, 386, 672));

        btn_timkiemsp.setBackground(new java.awt.Color(38, 159, 114));
        btn_timkiemsp.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btn_timkiemsp.setForeground(new java.awt.Color(255, 255, 255));
        btn_timkiemsp.setText("Tìm sản phẩm");
        btn_timkiemsp.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btn_timkiemspMouseClicked(evt);
            }
        });
        btn_timkiemsp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_timkiemspActionPerformed(evt);
            }
        });
        section_1_SanPham.add(btn_timkiemsp, new org.netbeans.lib.awtextra.AbsoluteConstraints(470, 10, 128, 18));

        section_1.addTab("Sản phẩm", section_1_SanPham);

        section_2_sanPhamChiTiet.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        tbl_spct.setForeground(new java.awt.Color(6, 71, 46));
        tbl_spct.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "STT", "Mã SP", "Tên SP", "Màu Sắc", "Kích Cỡ", "Đơn giá", "Số lượng tồn", "Trạng thái", "Ngày tạo", "Ngày cập nhật"
            }
        ));
        tbl_spct.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbl_spctMouseClicked(evt);
            }
        });
        scollpanel.setViewportView(tbl_spct);
        if (tbl_spct.getColumnModel().getColumnCount() > 0) {
            tbl_spct.getColumnModel().getColumn(0).setPreferredWidth(28);
        }

        section_2_sanPhamChiTiet.add(scollpanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(13, 40, 587, 560));

        jLabel13.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        jLabel13.setText("Chi tiết sản phẩm");
        section_2_sanPhamChiTiet.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(13, 7, -1, -1));

        jPanel3.setBackground(new java.awt.Color(38, 159, 114));
        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        section_2_tab_4.setBackground(new java.awt.Color(250, 250, 250));
        section_2_tab_4.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        section_2_tab_4.setForeground(new java.awt.Color(250, 250, 250));
        section_2_tab_4.setPreferredSize(new java.awt.Dimension(386, 637));
        section_2_tab_4.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        field_3.setBackground(new java.awt.Color(250, 250, 250));
        field_3.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        field_3.setForeground(new java.awt.Color(250, 250, 250));
        field_3.setPreferredSize(new java.awt.Dimension(386, 637));
        field_3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel14.setFont(new java.awt.Font("Segoe UI", 1, 9)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(14, 159, 103));
        jLabel14.setText("Mã sản phẩm:");
        field_3.add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(7, 4, 100, 11));

        txt_mspct.setFont(new java.awt.Font("Segoe UI", 0, 9)); // NOI18N
        txt_mspct.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_mspctActionPerformed(evt);
            }
        });
        field_3.add(txt_mspct, new org.netbeans.lib.awtextra.AbsoluteConstraints(113, 1, 240, 17));

        section_2_tab_4.add(field_3, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 140, 360, 20));

        field_8.setBackground(new java.awt.Color(255, 255, 255));
        field_8.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        field_8.setForeground(new java.awt.Color(250, 250, 250));
        field_8.setPreferredSize(new java.awt.Dimension(386, 637));
        field_8.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel15.setFont(new java.awt.Font("Segoe UI", 1, 9)); // NOI18N
        jLabel15.setForeground(new java.awt.Color(14, 159, 103));
        jLabel15.setText("Số lượng tồn");
        field_8.add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(7, 4, 100, 11));

        txt_slt.setFont(new java.awt.Font("Segoe UI", 0, 9)); // NOI18N
        field_8.add(txt_slt, new org.netbeans.lib.awtextra.AbsoluteConstraints(113, 1, 240, 17));

        section_2_tab_4.add(field_8, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 100, 360, 20));

        field_9.setBackground(new java.awt.Color(250, 250, 250));
        field_9.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        field_9.setForeground(new java.awt.Color(250, 250, 250));
        field_9.setPreferredSize(new java.awt.Dimension(386, 637));
        field_9.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel16.setFont(new java.awt.Font("Segoe UI", 1, 9)); // NOI18N
        jLabel16.setForeground(new java.awt.Color(14, 159, 103));
        jLabel16.setText("Đơn giá");
        field_9.add(jLabel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(7, 4, 100, 11));

        txt_dongiaspct.setFont(new java.awt.Font("Segoe UI", 0, 9)); // NOI18N
        field_9.add(txt_dongiaspct, new org.netbeans.lib.awtextra.AbsoluteConstraints(113, 1, 240, 17));

        section_2_tab_4.add(field_9, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 80, 360, 20));

        field_10.setBackground(new java.awt.Color(255, 255, 255));
        field_10.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        field_10.setForeground(new java.awt.Color(250, 250, 250));
        field_10.setPreferredSize(new java.awt.Dimension(386, 637));
        field_10.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel17.setFont(new java.awt.Font("Segoe UI", 1, 9)); // NOI18N
        jLabel17.setForeground(new java.awt.Color(14, 159, 103));
        jLabel17.setText("Tên sản phẩm");
        field_10.add(jLabel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(7, 4, 100, 11));

        txt_tenspct.setFont(new java.awt.Font("Segoe UI", 0, 9)); // NOI18N
        field_10.add(txt_tenspct, new org.netbeans.lib.awtextra.AbsoluteConstraints(113, 1, 240, 17));

        section_2_tab_4.add(field_10, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 20, 360, 20));

        field_11.setBackground(new java.awt.Color(255, 255, 255));
        field_11.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        field_11.setForeground(new java.awt.Color(250, 250, 250));
        field_11.setPreferredSize(new java.awt.Dimension(386, 637));
        field_11.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel18.setFont(new java.awt.Font("Segoe UI", 1, 9)); // NOI18N
        jLabel18.setForeground(new java.awt.Color(14, 159, 103));
        jLabel18.setText("Trạng thái");
        field_11.add(jLabel18, new org.netbeans.lib.awtextra.AbsoluteConstraints(7, 4, 100, 11));

        rdogrp_trangThai.add(rdo_dangban);
        rdo_dangban.setSelected(true);
        rdo_dangban.setText("Đang bán");
        rdo_dangban.setFont(new java.awt.Font("Segoe UI", 0, 9)); // NOI18N
        rdo_dangban.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdo_dangbanActionPerformed(evt);
            }
        });
        field_11.add(rdo_dangban, new org.netbeans.lib.awtextra.AbsoluteConstraints(113, 1, 100, 17));

        rdogrp_trangThai.add(rdo_ngungban);
        rdo_ngungban.setText("Ngừng bán");
        rdo_ngungban.setFont(new java.awt.Font("Segoe UI", 0, 9)); // NOI18N
        rdo_ngungban.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdo_ngungbanActionPerformed(evt);
            }
        });
        field_11.add(rdo_ngungban, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 0, 100, 17));

        section_2_tab_4.add(field_11, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 120, 360, 20));

        field_27.setBackground(new java.awt.Color(250, 250, 250));
        field_27.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        field_27.setForeground(new java.awt.Color(250, 250, 250));
        field_27.setPreferredSize(new java.awt.Dimension(386, 637));
        field_27.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel38.setFont(new java.awt.Font("Segoe UI", 1, 9)); // NOI18N
        jLabel38.setForeground(new java.awt.Color(14, 159, 103));
        jLabel38.setText("Màu sắc:");
        field_27.add(jLabel38, new org.netbeans.lib.awtextra.AbsoluteConstraints(7, 4, 100, 11));

        cbo_mausac.setFont(new java.awt.Font("Segoe UI", 0, 9)); // NOI18N
        cbo_mausac.setForeground(new java.awt.Color(6, 71, 46));
        cbo_mausac.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        field_27.add(cbo_mausac, new org.netbeans.lib.awtextra.AbsoluteConstraints(113, 1, 240, 17));

        section_2_tab_4.add(field_27, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 40, 360, 20));

        field_28.setBackground(new java.awt.Color(255, 255, 255));
        field_28.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        field_28.setForeground(new java.awt.Color(250, 250, 250));
        field_28.setPreferredSize(new java.awt.Dimension(386, 637));
        field_28.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel39.setFont(new java.awt.Font("Segoe UI", 1, 9)); // NOI18N
        jLabel39.setForeground(new java.awt.Color(14, 159, 103));
        jLabel39.setText("Kích cỡ:");
        field_28.add(jLabel39, new org.netbeans.lib.awtextra.AbsoluteConstraints(7, 4, 100, 11));

        cbo_kichco.setFont(new java.awt.Font("Segoe UI", 0, 9)); // NOI18N
        cbo_kichco.setForeground(new java.awt.Color(6, 71, 46));
        cbo_kichco.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        field_28.add(cbo_kichco, new org.netbeans.lib.awtextra.AbsoluteConstraints(113, 1, 240, 17));

        section_2_tab_4.add(field_28, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 60, 360, 20));

        field_7.setBackground(new java.awt.Color(250, 250, 250));
        field_7.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        field_7.setForeground(new java.awt.Color(250, 250, 250));
        field_7.setPreferredSize(new java.awt.Dimension(386, 637));
        field_7.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel19.setFont(new java.awt.Font("Segoe UI", 1, 9)); // NOI18N
        jLabel19.setForeground(new java.awt.Color(14, 159, 103));
        jLabel19.setText("Mã sản phẩm:");
        field_7.add(jLabel19, new org.netbeans.lib.awtextra.AbsoluteConstraints(7, 4, 100, 11));

        cbo_sanpham.setFont(new java.awt.Font("Segoe UI", 0, 9)); // NOI18N
        cbo_sanpham.setForeground(new java.awt.Color(6, 71, 46));
        cbo_sanpham.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        field_7.add(cbo_sanpham, new org.netbeans.lib.awtextra.AbsoluteConstraints(113, 1, 240, 17));

        section_2_tab_4.add(field_7, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 360, 20));

        jPanel3.add(section_2_tab_4, new org.netbeans.lib.awtextra.AbsoluteConstraints(13, 35, 360, 394));

        jLabel20.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel20.setForeground(new java.awt.Color(255, 255, 255));
        jLabel20.setText("Thông tin sản phẩm");
        jPanel3.add(jLabel20, new org.netbeans.lib.awtextra.AbsoluteConstraints(13, 13, -1, 15));

        section_2_tab_5.setBackground(new java.awt.Color(250, 250, 250));
        section_2_tab_5.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        section_2_tab_5.setForeground(new java.awt.Color(250, 250, 250));
        section_2_tab_5.setPreferredSize(new java.awt.Dimension(386, 637));
        section_2_tab_5.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        btn_suaspct.setBackground(new java.awt.Color(14, 159, 103));
        btn_suaspct.setFont(new java.awt.Font("Segoe UI", 1, 9)); // NOI18N
        btn_suaspct.setForeground(new java.awt.Color(255, 255, 255));
        btn_suaspct.setText("Sửa");
        btn_suaspct.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_suaspctActionPerformed(evt);
            }
        });
        section_2_tab_5.add(btn_suaspct, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 36, 333, 16));

        btn_lammoispct.setBackground(new java.awt.Color(14, 159, 103));
        btn_lammoispct.setFont(new java.awt.Font("Segoe UI", 1, 9)); // NOI18N
        btn_lammoispct.setForeground(new java.awt.Color(255, 255, 255));
        btn_lammoispct.setText("Làm mới");
        btn_lammoispct.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_lammoispctActionPerformed(evt);
            }
        });
        section_2_tab_5.add(btn_lammoispct, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 59, 333, 16));

        btn_xoaspct.setFont(new java.awt.Font("Segoe UI", 1, 9)); // NOI18N
        btn_xoaspct.setForeground(new java.awt.Color(146, 23, 21));
        btn_xoaspct.setText("Xóa");
        btn_xoaspct.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(146, 23, 21), 2));
        btn_xoaspct.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_xoaspctActionPerformed(evt);
            }
        });
        section_2_tab_5.add(btn_xoaspct, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 82, 333, 16));

        btn_themspct.setBackground(new java.awt.Color(14, 159, 103));
        btn_themspct.setFont(new java.awt.Font("Segoe UI", 1, 9)); // NOI18N
        btn_themspct.setForeground(new java.awt.Color(255, 255, 255));
        btn_themspct.setText("Thêm");
        btn_themspct.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_themspctActionPerformed(evt);
            }
        });
        section_2_tab_5.add(btn_themspct, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 333, 16));

        jPanel3.add(section_2_tab_5, new org.netbeans.lib.awtextra.AbsoluteConstraints(13, 442, 360, 180));

        section_2_sanPhamChiTiet.add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(620, 7, 386, 672));

        btn_timkiemsp1.setBackground(new java.awt.Color(38, 159, 114));
        btn_timkiemsp1.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btn_timkiemsp1.setForeground(new java.awt.Color(255, 255, 255));
        btn_timkiemsp1.setText("Tìm sản phẩm chi tiết");
        btn_timkiemsp1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btn_timkiemsp1MouseClicked(evt);
            }
        });
        btn_timkiemsp1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_timkiemsp1ActionPerformed(evt);
            }
        });
        section_2_sanPhamChiTiet.add(btn_timkiemsp1, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 10, 180, 18));

        section_1.addTab("Sản phẩm chi tiết", section_2_sanPhamChiTiet);

        section_3_thuocTinh.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        tbl_thuoctinh.setForeground(new java.awt.Color(6, 71, 46));
        tbl_thuoctinh.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "STT", "Mã thuộc tính", "Tên thuộc tính", "Mô tả"
            }
        ));
        tbl_thuoctinh.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbl_thuoctinhMouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(tbl_thuoctinh);
        if (tbl_thuoctinh.getColumnModel().getColumnCount() > 0) {
            tbl_thuoctinh.getColumnModel().getColumn(0).setPreferredWidth(28);
        }

        section_3_thuocTinh.add(jScrollPane3, new org.netbeans.lib.awtextra.AbsoluteConstraints(13, 40, 587, 560));

        jLabel22.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        jLabel22.setText("Thuộc tính sản phẩm");
        section_3_thuocTinh.add(jLabel22, new org.netbeans.lib.awtextra.AbsoluteConstraints(13, 7, -1, -1));

        jPanel4.setBackground(new java.awt.Color(38, 159, 114));
        jPanel4.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        section_2_tab_6.setBackground(new java.awt.Color(250, 250, 250));
        section_2_tab_6.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        section_2_tab_6.setForeground(new java.awt.Color(250, 250, 250));
        section_2_tab_6.setPreferredSize(new java.awt.Dimension(386, 637));
        section_2_tab_6.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        field_16.setBackground(new java.awt.Color(255, 255, 255));
        field_16.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        field_16.setForeground(new java.awt.Color(250, 250, 250));
        field_16.setPreferredSize(new java.awt.Dimension(386, 637));
        field_16.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel26.setFont(new java.awt.Font("Segoe UI", 1, 9)); // NOI18N
        jLabel26.setForeground(new java.awt.Color(14, 159, 103));
        jLabel26.setText("Mô tả;");
        field_16.add(jLabel26, new org.netbeans.lib.awtextra.AbsoluteConstraints(7, 4, 100, 11));

        txt_motathuoctinh.setFont(new java.awt.Font("Segoe UI", 0, 9)); // NOI18N
        field_16.add(txt_motathuoctinh, new org.netbeans.lib.awtextra.AbsoluteConstraints(113, 1, 240, 90));

        section_2_tab_6.add(field_16, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 204, 360, 100));

        field_17.setBackground(new java.awt.Color(255, 255, 255));
        field_17.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        field_17.setForeground(new java.awt.Color(250, 250, 250));
        field_17.setPreferredSize(new java.awt.Dimension(386, 637));
        field_17.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel27.setFont(new java.awt.Font("Segoe UI", 1, 9)); // NOI18N
        jLabel27.setForeground(new java.awt.Color(14, 159, 103));
        jLabel27.setText("Loại thuộc tinh:");
        field_17.add(jLabel27, new org.netbeans.lib.awtextra.AbsoluteConstraints(7, 4, 100, 11));

        rdo_thuoctinh.add(rdo_chatlieu);
        rdo_chatlieu.setText("Chất liệu");
        rdo_chatlieu.setFont(new java.awt.Font("Segoe UI", 0, 9)); // NOI18N
        rdo_chatlieu.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                rdo_chatlieuItemStateChanged(evt);
            }
        });
        rdo_chatlieu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdo_chatlieuActionPerformed(evt);
            }
        });
        field_17.add(rdo_chatlieu, new org.netbeans.lib.awtextra.AbsoluteConstraints(113, 19, 240, 17));

        rdo_thuoctinh.add(rdo_mausac);
        rdo_mausac.setText("Màu sắc");
        rdo_mausac.setFont(new java.awt.Font("Segoe UI", 0, 9)); // NOI18N
        rdo_mausac.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                rdo_mausacItemStateChanged(evt);
            }
        });
        rdo_mausac.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdo_mausacActionPerformed(evt);
            }
        });
        field_17.add(rdo_mausac, new org.netbeans.lib.awtextra.AbsoluteConstraints(113, 38, 240, 17));

        rdo_thuoctinh.add(rdo_kichthuoc);
        rdo_kichthuoc.setText("Kích thước");
        rdo_kichthuoc.setFont(new java.awt.Font("Segoe UI", 0, 9)); // NOI18N
        rdo_kichthuoc.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                rdo_kichthuocItemStateChanged(evt);
            }
        });
        rdo_kichthuoc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdo_kichthuocActionPerformed(evt);
            }
        });
        field_17.add(rdo_kichthuoc, new org.netbeans.lib.awtextra.AbsoluteConstraints(113, 56, 240, 17));

        rdo_thuoctinh.add(rdo_kieudang);
        rdo_kieudang.setText("Kiểu dáng");
        rdo_kieudang.setFont(new java.awt.Font("Segoe UI", 0, 9)); // NOI18N
        rdo_kieudang.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                rdo_kieudangItemStateChanged(evt);
            }
        });
        rdo_kieudang.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdo_kieudangActionPerformed(evt);
            }
        });
        field_17.add(rdo_kieudang, new org.netbeans.lib.awtextra.AbsoluteConstraints(113, 74, 240, 17));

        rdo_thuoctinh.add(rdo_keiucogiay);
        rdo_keiucogiay.setText("Kiểu cổ giày");
        rdo_keiucogiay.setFont(new java.awt.Font("Segoe UI", 0, 9)); // NOI18N
        rdo_keiucogiay.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                rdo_keiucogiayItemStateChanged(evt);
            }
        });
        rdo_keiucogiay.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdo_keiucogiayActionPerformed(evt);
            }
        });
        field_17.add(rdo_keiucogiay, new org.netbeans.lib.awtextra.AbsoluteConstraints(113, 92, 240, 17));

        rdo_thuoctinh.add(rdo_kieudaygiay);
        rdo_kieudaygiay.setText("Kiểu dây giày");
        rdo_kieudaygiay.setFont(new java.awt.Font("Segoe UI", 0, 9)); // NOI18N
        rdo_kieudaygiay.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                rdo_kieudaygiayItemStateChanged(evt);
            }
        });
        rdo_kieudaygiay.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdo_kieudaygiayActionPerformed(evt);
            }
        });
        field_17.add(rdo_kieudaygiay, new org.netbeans.lib.awtextra.AbsoluteConstraints(113, 110, 240, 17));

        rdo_thuoctinh.add(rdo_thuonghieu);
        rdo_thuonghieu.setText("Thương hiệu");
        rdo_thuonghieu.setFont(new java.awt.Font("Segoe UI", 0, 9)); // NOI18N
        rdo_thuonghieu.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                rdo_thuonghieuItemStateChanged(evt);
            }
        });
        rdo_thuonghieu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdo_thuonghieuActionPerformed(evt);
            }
        });
        field_17.add(rdo_thuonghieu, new org.netbeans.lib.awtextra.AbsoluteConstraints(113, 128, 240, 17));

        rdo_thuoctinh.add(rdo_loaisanpham);
        rdo_loaisanpham.setSelected(true);
        rdo_loaisanpham.setText("Loại sản phẩm");
        rdo_loaisanpham.setFont(new java.awt.Font("Segoe UI", 0, 9)); // NOI18N
        rdo_loaisanpham.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                rdo_loaisanphamItemStateChanged(evt);
            }
        });
        rdo_loaisanpham.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdo_loaisanphamActionPerformed(evt);
            }
        });
        field_17.add(rdo_loaisanpham, new org.netbeans.lib.awtextra.AbsoluteConstraints(113, 1, 240, 17));

        rdo_thuoctinh.add(rdo_xuatsu);
        rdo_xuatsu.setText("Xuất xứ");
        rdo_xuatsu.setFont(new java.awt.Font("Segoe UI", 0, 9)); // NOI18N
        rdo_xuatsu.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                rdo_xuatsuItemStateChanged(evt);
            }
        });
        rdo_xuatsu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdo_xuatsuActionPerformed(evt);
            }
        });
        field_17.add(rdo_xuatsu, new org.netbeans.lib.awtextra.AbsoluteConstraints(113, 146, 240, 17));

        section_2_tab_6.add(field_17, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 360, 164));

        field_18.setBackground(new java.awt.Color(255, 255, 255));
        field_18.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        field_18.setForeground(new java.awt.Color(250, 250, 250));
        field_18.setPreferredSize(new java.awt.Dimension(386, 637));
        field_18.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel28.setFont(new java.awt.Font("Segoe UI", 1, 9)); // NOI18N
        jLabel28.setForeground(new java.awt.Color(14, 159, 103));
        jLabel28.setText("Mã thuộc tính:");
        field_18.add(jLabel28, new org.netbeans.lib.awtextra.AbsoluteConstraints(7, 4, 100, 11));

        txt_mathuoctinh.setFont(new java.awt.Font("Segoe UI", 0, 9)); // NOI18N
        field_18.add(txt_mathuoctinh, new org.netbeans.lib.awtextra.AbsoluteConstraints(113, 1, 240, 17));

        section_2_tab_6.add(field_18, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 164, 360, 20));

        field_19.setBackground(new java.awt.Color(255, 255, 255));
        field_19.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        field_19.setForeground(new java.awt.Color(250, 250, 250));
        field_19.setPreferredSize(new java.awt.Dimension(386, 637));
        field_19.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel30.setFont(new java.awt.Font("Segoe UI", 1, 9)); // NOI18N
        jLabel30.setForeground(new java.awt.Color(14, 159, 103));
        jLabel30.setText("Tên thuộc tính:");
        field_19.add(jLabel30, new org.netbeans.lib.awtextra.AbsoluteConstraints(7, 4, 100, 11));

        txt_tenthuoctinh.setFont(new java.awt.Font("Segoe UI", 0, 9)); // NOI18N
        field_19.add(txt_tenthuoctinh, new org.netbeans.lib.awtextra.AbsoluteConstraints(113, 1, 240, 17));

        section_2_tab_6.add(field_19, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 184, 360, 20));

        jPanel4.add(section_2_tab_6, new org.netbeans.lib.awtextra.AbsoluteConstraints(13, 35, 360, 394));

        jLabel29.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel29.setForeground(new java.awt.Color(255, 255, 255));
        jLabel29.setText("Thông tin sản phẩm");
        jPanel4.add(jLabel29, new org.netbeans.lib.awtextra.AbsoluteConstraints(13, 13, -1, 15));

        section_2_tab_7.setBackground(new java.awt.Color(250, 250, 250));
        section_2_tab_7.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        section_2_tab_7.setForeground(new java.awt.Color(250, 250, 250));
        section_2_tab_7.setPreferredSize(new java.awt.Dimension(386, 637));
        section_2_tab_7.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        btn_editInsert2.setText("Sửa");
        btn_editInsert2.setBackground(new java.awt.Color(14, 159, 103));
        btn_editInsert2.setFont(new java.awt.Font("Segoe UI", 1, 9)); // NOI18N
        btn_editInsert2.setForeground(new java.awt.Color(255, 255, 255));
        btn_editInsert2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_editInsert2ActionPerformed(evt);
            }
        });
        section_2_tab_7.add(btn_editInsert2, new org.netbeans.lib.awtextra.AbsoluteConstraints(13, 36, 333, 16));

        btn_themthuoctinh.setText("Thêm");
        btn_themthuoctinh.setBackground(new java.awt.Color(14, 159, 103));
        btn_themthuoctinh.setFont(new java.awt.Font("Segoe UI", 1, 9)); // NOI18N
        btn_themthuoctinh.setForeground(new java.awt.Color(255, 255, 255));
        btn_themthuoctinh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_themthuoctinhActionPerformed(evt);
            }
        });
        section_2_tab_7.add(btn_themthuoctinh, new org.netbeans.lib.awtextra.AbsoluteConstraints(13, 13, 333, 16));

        btn_formFillToTable2.setText("Làm mới");
        btn_formFillToTable2.setBackground(new java.awt.Color(14, 159, 103));
        btn_formFillToTable2.setFont(new java.awt.Font("Segoe UI", 1, 9)); // NOI18N
        btn_formFillToTable2.setForeground(new java.awt.Color(255, 255, 255));
        btn_formFillToTable2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_formFillToTable2ActionPerformed(evt);
            }
        });
        section_2_tab_7.add(btn_formFillToTable2, new org.netbeans.lib.awtextra.AbsoluteConstraints(13, 59, 333, 16));

        btn_editDelete2.setText("Xóa");
        btn_editDelete2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(146, 23, 21), 2));
        btn_editDelete2.setFont(new java.awt.Font("Segoe UI", 1, 9)); // NOI18N
        btn_editDelete2.setForeground(new java.awt.Color(146, 23, 21));
        btn_editDelete2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_editDelete2ActionPerformed(evt);
            }
        });
        section_2_tab_7.add(btn_editDelete2, new org.netbeans.lib.awtextra.AbsoluteConstraints(13, 82, 333, 16));

        jPanel4.add(section_2_tab_7, new org.netbeans.lib.awtextra.AbsoluteConstraints(13, 442, 360, 180));

        section_3_thuocTinh.add(jPanel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(620, 7, 386, 672));

        section_1.addTab("Thuộc tính", section_3_thuocTinh);

        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        tbl_recycle.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Mã sp", "Tên sp", "Thương Hiệu", "Xuất Sứ"
            }
        ));
        jScrollPane2.setViewportView(tbl_recycle);

        jPanel1.add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, -1, -1));

        jButton2.setText("restore");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton2, new org.netbeans.lib.awtextra.AbsoluteConstraints(550, 70, -1, -1));

        section_1.addTab("Danh sách ẩn", jPanel1);

        add(section_1, new org.netbeans.lib.awtextra.AbsoluteConstraints(26, -5, 1028, 672));
    }// </editor-fold>//GEN-END:initComponents


    private void tbl_objMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbl_objMouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() == 1) {
            this.edit();

        } else if (evt.getClickCount() == 2) {
            int index = tbl_obj.getSelectedRow();
            int idsp = Integer.parseInt(tbl_obj.getValueAt(index, 0).toString());

            repoSPCT.findallbyid(idsp);
            fillTableSPCT(idsp);
            section_1.setSelectedIndex(1);
        }


    }//GEN-LAST:event_tbl_objMouseClicked

    private void tbl_spctMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbl_spctMouseClicked
        // TODO add your handling code here:
        int row = tbl_obj.getSelectedRow();
        if (row >= 0) {
            selectedIdSanPham = (Integer) tbl_obj.getValueAt(row, 0); // cột 0 là id_san_pham
        }

        this.editspct();
    }//GEN-LAST:event_tbl_spctMouseClicked

    private void tbl_thuoctinhMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbl_thuoctinhMouseClicked
        // TODO add your handling code here:
        setFormThuocTinh();

    }//GEN-LAST:event_tbl_thuoctinhMouseClicked

    private void jScrollPane1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jScrollPane1MouseClicked
        // TODO add your handling code here:
//        if(evt.getClickCount()==1){
//            this.edit();
//        }
//        else if(evt.getClickCount() == 2) { // double click
//            int row = tbl_obj.getSelectedRow();
//            if (row != -1) {
//                // lấy id sản phẩm
//                int idSP = Integer.parseInt(tbl_obj.getValueAt(row, 0).toString());
//                
//                // gọi hàm load SPCT
//              repoSPCT.findallbyid(idSP);//
//                section_1.setSelectedIndex(1);   // chuyển qua tab SPCT
//            }

    }//GEN-LAST:event_jScrollPane1MouseClicked

    private void btn_themActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_themActionPerformed
        // TODO add your handling code here:

        // 1) Tạo object SanPham từ form
        SanPham_ql sp = this.getForm();
        List<SanPham_ql> list = repoSanPham.findByMa(sp.getMa_san_pham());
        if (!list.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Mã sản phẩm đã tồn tại! Vui lòng nhập mã khác.");
            return; // ❗ Dừng lại, không cho insert
        }

        // 2) Insert sản phẩm (DAO trả về entity có id)
        try {
            sp = repoSanPham.create(sp); // repoSanPham.create phải set id_san_pham

            // 3) Tạo SanPhamChiTiet placeholder và insert
            SanPhamChiTiet_ql ct = new SanPhamChiTiet_ql();
            ct.setId_san_pham(sp.getId_san_pham()); // ID đã có
            ct.setMa_san_pham_chi_tiet("SPCT" + sp.getId_san_pham());

            // Nếu id_mau_sac, id_kich_co của SanPhamChiTiet là Integer, bạn có thể để null
            // Nếu là int primitive, bỏ set để nó mặc định 0
            ct.setId_mau_sac(null); // chỉ nếu field là Integer
            ct.setId_kich_co(null); // chỉ nếu field là Integer
            ct.setDon_gia(BigDecimal.ZERO);
            ct.setSo_luong_ton(0);
            ct.setTrang_thai_xoa(true);

            // gọi DAO SPCT
            repoSPCT.create(ct);

            // 4) reload data và clear form
            fillToTable();
            fillToTableSPCT();
            clear();

            JOptionPane.showMessageDialog(this, "Thêm sản phẩm  thành công!");

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi thêm: " + ex.getMessage());
        }


    }//GEN-LAST:event_btn_themActionPerformed

    private void btn_xoaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_xoaActionPerformed
        // TODO add your handling code here:

        int row = tbl_obj.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Hãy chọn sản phẩm cần xóa!");
            return;
        }

        String ma = tbl_obj.getValueAt(row, 1).toString();

        repoSanPham.softDelete(ma);

        fillToTable();    // load danh sách đang hoạt động
        fillToRecycle();         // load danh sách trong recycle

        JOptionPane.showMessageDialog(this, "Đã đưa vào Recycle");
    }//GEN-LAST:event_btn_xoaActionPerformed

    private void btn_suaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_suaActionPerformed
        // TODO add your handling code here:
        this.update();
        this.fillToTable();
    }//GEN-LAST:event_btn_suaActionPerformed

    private void btn_lammoiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_lammoiActionPerformed
        // TODO add your handling code here:
        this.clear();
        this.fillToTable();
    }//GEN-LAST:event_btn_lammoiActionPerformed

    private void txt_mspctActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_mspctActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_mspctActionPerformed

    private void btn_timkiemspMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btn_timkiemspMouseClicked
        String name = JOptionPane.showInputDialog("nhap ten: ");
        mol.setRowCount(0);
        for (SanPham_ql x : repoSanPham.findByMa("%" + name + "%")) {
            mol.addRow(new Object[]{
                x.getId_san_pham(),
                x.getMa_san_pham(),
                x.getTen_san_pham(),
                repoCL.findById(x.getId_chat_lieu()).getTen_chat_lieu(),
                repoKD.findById(x.getId_kieu_dang()).getTen_kieu_dang(),
                repoKCG.findById(x.getId_kieu_co_giay()).getTen_co_giay(),
                repoDG.findById(x.getId_kieu_day_giay()).getTen_day_giay(),
                repoTH.findById(x.getId_thuong_hieu()).getTen_thuong_hieu(),
                repoXX.findById(x.getId_xuat_su()).getTen_xuat_su()});
        }
    }//GEN-LAST:event_btn_timkiemspMouseClicked

    private void rdo_ngungbanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdo_ngungbanActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_rdo_ngungbanActionPerformed

    private void btn_timkiemspActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_timkiemspActionPerformed
        // TODO add your handling code here:
//        this.timTheoMa(txt_timkiemsp.getText());
    }//GEN-LAST:event_btn_timkiemspActionPerformed

    private void rdo_chatlieuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdo_chatlieuActionPerformed
        // TODO add your handling code here:
        clearFormThuocTinh();           // reset form
        fillToTableChatLieu();       // load bảng mới
    }//GEN-LAST:event_rdo_chatlieuActionPerformed

    private void rdo_loaisanphamItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_rdo_loaisanphamItemStateChanged
        // TODO add your handling code here:
        if (rdo_loaisanpham.isSelected()) {
            this.fillToTableLoaiSanPham();
        }
    }//GEN-LAST:event_rdo_loaisanphamItemStateChanged

    private void rdo_chatlieuItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_rdo_chatlieuItemStateChanged
        // TODO add your handling code here:
        if (rdo_chatlieu.isSelected()) {
            this.fillToTableChatLieu();
        }
    }//GEN-LAST:event_rdo_chatlieuItemStateChanged

    private void rdo_mausacItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_rdo_mausacItemStateChanged
        // TODO add your handling code here:
        if (rdo_mausac.isSelected()) {
            this.fillToTableMauSac();
        }
    }//GEN-LAST:event_rdo_mausacItemStateChanged

    private void rdo_kichthuocItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_rdo_kichthuocItemStateChanged
        // TODO add your handling code here:
        if (rdo_kichthuoc.isSelected()) {
            this.fillToTableKichThuoc();
        }
    }//GEN-LAST:event_rdo_kichthuocItemStateChanged

    private void rdo_kieudangItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_rdo_kieudangItemStateChanged
        // TODO add your handling code here:
        if (rdo_kieudang.isSelected()) {
            this.fillToTableKieuDang();
        }
    }//GEN-LAST:event_rdo_kieudangItemStateChanged

    private void rdo_keiucogiayItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_rdo_keiucogiayItemStateChanged
        // TODO add your handling code here:
        if (rdo_keiucogiay.isSelected()) {
            this.fillToTableKieuCoGiay();
        }
    }//GEN-LAST:event_rdo_keiucogiayItemStateChanged

    private void rdo_kieudaygiayItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_rdo_kieudaygiayItemStateChanged
        // TODO add your handling code here:
        if (rdo_kieudaygiay.isSelected()) {
            this.fillToTableKieuDayGiay();
        }
    }//GEN-LAST:event_rdo_kieudaygiayItemStateChanged

    private void rdo_thuonghieuItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_rdo_thuonghieuItemStateChanged
        // TODO add your handling code here:
        if (rdo_thuonghieu.isSelected()) {
            this.fillToTableKieuThuongHieu();
        }
    }//GEN-LAST:event_rdo_thuonghieuItemStateChanged

    private void rdo_xuatsuItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_rdo_xuatsuItemStateChanged
        // TODO add your handling code here:
        if (rdo_xuatsu.isSelected()) {
            this.fillToTableKieuXuatSu();
        }

    }//GEN-LAST:event_rdo_xuatsuItemStateChanged

    private void btn_lammoispctActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_lammoispctActionPerformed
        // TODO add your handling code here:
        this.clearspct();
        this.fillToTableSPCT();
    }//GEN-LAST:event_btn_lammoispctActionPerformed

    private void btn_suaspctActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_suaspctActionPerformed
        // TODO add your handling code here:
        this.updatespct();
        this.fillToTableSPCT();
        JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
    }//GEN-LAST:event_btn_suaspctActionPerformed

    private void btn_themspctActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_themspctActionPerformed
        // TODO add your handling code here:
        SanPhamChiTiet_ql sp = new SanPhamChiTiet_ql();
        List<SanPhamChiTiet_ql> list = repoSPCT.findByMa(sp.getMa_san_pham_chi_tiet());
        if (!list.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Mã sản phẩm đã tồn tại! Vui lòng nhập mã khác.");
            return; // ❗ Dừng lại, không cho insert
        }
        JOptionPane.showMessageDialog(this, "Thêm thành công!");
        this.createspct();
        this.fillToTableSPCT();
    }//GEN-LAST:event_btn_themspctActionPerformed

    private void section_1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_section_1MouseClicked
        // TODO add your handling code here:
        this.fillToTable();
        this.fillToTableSPCT();
    }//GEN-LAST:event_section_1MouseClicked

    private void btn_themthuoctinhActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_themthuoctinhActionPerformed
        // TODO add your handling code here:
        this.actionAddThuocTinh();
//        this.fillToTableLoaiSanPham();
    }//GEN-LAST:event_btn_themthuoctinhActionPerformed

    private void btn_timkiemsp1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_timkiemsp1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btn_timkiemsp1ActionPerformed

    private void btn_timkiemsp1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btn_timkiemsp1MouseClicked
        // TODO add your handling code here:
        this.fillToTableSPCTbyName();
//         String name = JOptionPane.showInputDialog("nhap san pham: ");
//        mol2.setRowCount(0);
//        for (SanPhamChiTiet x : repoSPCT.findByMa("%" + name + "%")) {
//             cbo_mausac.removeAllItems();
//            for (MauSac ms : repoMS.findAll()) {
//                cbo_mausac.addItem(ms.getTen_mau_sac());
//            }
//            String tenMau = "";
//            if (x.getId_mau_sac() != null && x.getId_mau_sac() > 0) {
//                MauSac ms = repoMS.findById(x.getId_mau_sac());
//                if (ms != null) {
//                    tenMau = ms.getTen_mau_sac();
//                }
//            }
//
//            // Lấy tên kích cỡ (nếu có)
//            cbo_kichco.removeAllItems();
//            for (KichCo kc : repoKT.findAll()) {
//                cbo_kichco.addItem(kc.getTen_kich_co());
//            }
//            String tenKichCo = "";
//            if (x.getId_kich_co() != null && x.getId_kich_co() > 0) {
//                KichCo kc = repoKT.findById(x.getId_kich_co());
//                if (kc != null) {
//                    tenKichCo = kc.getTen_kich_co();
//                }
//            }
//            mol2.addRow(new Object[]{
//               x.getId_san_pham_chi_tiet(),
//                x.getMa_san_pham_chi_tiet(),
//                x.getTen_san_pham(),
//                tenMau, // tên màu
//                tenKichCo, // tên kích cỡ
//                x.getDon_gia(),
//                x.getSo_luong_ton(),
//                x.isTrang_thai(),
//                x.getNgay_tao(),
//                x.getNgay_cap_nhat()
//        };
//            
//        }

    }//GEN-LAST:event_btn_timkiemsp1MouseClicked

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        this.fillToRecycle();
        section_1.addTab("Sản phẩm ẩn", jPanel1);
        section_1.setSelectedIndex(3);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:

        int row = tbl_recycle.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Hãy chọn sản phẩm muốn khôi phục!");
            return;
        }

        String maSP = tbl_recycle.getValueAt(row, 0).toString(); // cột 0 là mã SP

        try {
            repoSanPham.restore(maSP);   // UPDATE san_pham SET trang_thai = 1 WHERE ma_san_pham = ?

            fillToTable();   // load lại danh sách SP đang hoạt động
            fillToRecycle();        // load lại thùng rác

            JOptionPane.showMessageDialog(this, "Khôi phục thành công!");

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi khôi phục!");
        }

    }//GEN-LAST:event_jButton2ActionPerformed

    private void btn_xoaspctActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_xoaspctActionPerformed
        // TODO add your handling code here:
        this.delete();
        this.fillToTableSPCT();
    }//GEN-LAST:event_btn_xoaspctActionPerformed

    private void btn_editInsert2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_editInsert2ActionPerformed
        // TODO add your handling code here:
        this.actionUpdateThuocTinh();
    }//GEN-LAST:event_btn_editInsert2ActionPerformed

    private void btn_formFillToTable2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_formFillToTable2ActionPerformed
        // TODO add your handling code here:
       ClearThuocTinh();
    }//GEN-LAST:event_btn_formFillToTable2ActionPerformed

    private void rdo_loaisanphamActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdo_loaisanphamActionPerformed
        // TODO add your handling code here:
        clearFormThuocTinh();           // reset form
        fillToTableLoaiSanPham();       // load bảng mới

    }//GEN-LAST:event_rdo_loaisanphamActionPerformed

    private void rdo_mausacActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdo_mausacActionPerformed
        // TODO add your handling code here:
        clearFormThuocTinh();           // reset form
        fillToTableMauSac();       // load bảng mới
    }//GEN-LAST:event_rdo_mausacActionPerformed

    private void rdo_kichthuocActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdo_kichthuocActionPerformed
        // TODO add your handling code here:
        clearFormThuocTinh();           // reset form
        fillToTableKichThuoc();       // load bảng mới
    }//GEN-LAST:event_rdo_kichthuocActionPerformed

    private void rdo_kieudangActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdo_kieudangActionPerformed
        // TODO add your handling code here:
        clearFormThuocTinh();           // reset form
        fillToTableKieuDang();       // load bảng mới
    }//GEN-LAST:event_rdo_kieudangActionPerformed

    private void rdo_keiucogiayActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdo_keiucogiayActionPerformed
        // TODO add your handling code here:
        clearFormThuocTinh();           // reset form
        fillToTableKieuCoGiay();       // load bảng mới
    }//GEN-LAST:event_rdo_keiucogiayActionPerformed

    private void rdo_kieudaygiayActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdo_kieudaygiayActionPerformed
        // TODO add your handling code here:
        clearFormThuocTinh();           // reset form
        fillToTableKieuDayGiay();       // load bảng mới
    }//GEN-LAST:event_rdo_kieudaygiayActionPerformed

    private void rdo_thuonghieuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdo_thuonghieuActionPerformed
        // TODO add your handling code here:
        clearFormThuocTinh();           // reset form
        fillToTableKieuThuongHieu();       // load bảng mới
    }//GEN-LAST:event_rdo_thuonghieuActionPerformed

    private void rdo_xuatsuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdo_xuatsuActionPerformed
        // TODO add your handling code here:
        clearFormThuocTinh();           // reset form
        fillToTableKieuXuatSu();       // load bảng mới
    }//GEN-LAST:event_rdo_xuatsuActionPerformed

    private void btn_editDelete2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_editDelete2ActionPerformed
        // TODO add your handling code here:
        actionDeleteThuocTinh();
    }//GEN-LAST:event_btn_editDelete2ActionPerformed

    private void rdo_dangbanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdo_dangbanActionPerformed
        // TODO add your handling code here:
            try {
        double donGia = Double.parseDouble(txt_dongiaspct.getText().trim());
        int soLuongTon = Integer.parseInt(txt_slt.getText().trim());

        if (donGia <= 0) {
            XDialog.alert("Đơn giá phải lớn hơn 0 khi chuyển sang ĐANG BÁN!");
            rdo_ngungban.setSelected(true);
            return;
        }

        if (soLuongTon <= 0) {
            XDialog.alert( "Số lượng tồn phải lớn hơn 0 khi chuyển sang ĐANG BÁN!");
            rdo_ngungban.setSelected(true);
            return;
        }

    } catch (Exception e) {
        XDialog.alert("Vui lòng nhập đơn giá và số lượng tồn hợp lệ!");
        rdo_ngungban.setSelected(true);
    }
    }//GEN-LAST:event_rdo_dangbanActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_editDelete2;
    private javax.swing.JButton btn_editInsert2;
    private javax.swing.JButton btn_formFillToTable2;
    private javax.swing.JButton btn_lammoi;
    private javax.swing.JButton btn_lammoispct;
    private javax.swing.JButton btn_sua;
    private javax.swing.JButton btn_suaspct;
    private javax.swing.JButton btn_them;
    private javax.swing.JButton btn_themspct;
    private javax.swing.JButton btn_themthuoctinh;
    private javax.swing.JButton btn_timkiemsp;
    private javax.swing.JButton btn_timkiemsp1;
    private javax.swing.JButton btn_xoa;
    private javax.swing.JButton btn_xoaspct;
    private javax.swing.JComboBox<String> cbo_LoaiSP;
    private javax.swing.JComboBox<String> cbo_chatlieu;
    private javax.swing.JComboBox<String> cbo_kichco;
    private javax.swing.JComboBox<String> cbo_kieucogiay;
    private javax.swing.JComboBox<String> cbo_kieudang;
    private javax.swing.JComboBox<String> cbo_kieudaygiay;
    private javax.swing.JComboBox<String> cbo_mausac;
    private javax.swing.JComboBox<String> cbo_sanpham;
    private javax.swing.JComboBox<String> cbo_thuonghieu;
    private javax.swing.JComboBox<String> cbo_xuatxu;
    private javax.swing.JPanel field_1;
    private javax.swing.JPanel field_10;
    private javax.swing.JPanel field_11;
    private javax.swing.JPanel field_16;
    private javax.swing.JPanel field_17;
    private javax.swing.JPanel field_18;
    private javax.swing.JPanel field_19;
    private javax.swing.JPanel field_20;
    private javax.swing.JPanel field_21;
    private javax.swing.JPanel field_22;
    private javax.swing.JPanel field_23;
    private javax.swing.JPanel field_24;
    private javax.swing.JPanel field_27;
    private javax.swing.JPanel field_28;
    private javax.swing.JPanel field_29;
    private javax.swing.JPanel field_3;
    private javax.swing.JPanel field_4;
    private javax.swing.JPanel field_5;
    private javax.swing.JPanel field_6;
    private javax.swing.JPanel field_7;
    private javax.swing.JPanel field_8;
    private javax.swing.JPanel field_9;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JRadioButton rdo_chatlieu;
    private javax.swing.JRadioButton rdo_dangban;
    private javax.swing.JRadioButton rdo_keiucogiay;
    private javax.swing.JRadioButton rdo_kichthuoc;
    private javax.swing.JRadioButton rdo_kieudang;
    private javax.swing.JRadioButton rdo_kieudaygiay;
    private javax.swing.JRadioButton rdo_loaisanpham;
    private javax.swing.JRadioButton rdo_mausac;
    private javax.swing.JRadioButton rdo_ngungban;
    private javax.swing.ButtonGroup rdo_thuoctinh;
    private javax.swing.JRadioButton rdo_thuonghieu;
    private javax.swing.JRadioButton rdo_xuatsu;
    private javax.swing.ButtonGroup rdogrp_trangThai;
    private javax.swing.JScrollPane scollpanel;
    private javax.swing.JTabbedPane section_1;
    private javax.swing.JPanel section_1_SanPham;
    private javax.swing.JPanel section_2_sanPhamChiTiet;
    private javax.swing.JPanel section_2_tab_2;
    private javax.swing.JPanel section_2_tab_3;
    private javax.swing.JPanel section_2_tab_4;
    private javax.swing.JPanel section_2_tab_5;
    private javax.swing.JPanel section_2_tab_6;
    private javax.swing.JPanel section_2_tab_7;
    private javax.swing.JPanel section_3_thuocTinh;
    private javax.swing.JTable tbl_obj;
    private javax.swing.JTable tbl_recycle;
    private javax.swing.JTable tbl_spct;
    private javax.swing.JTable tbl_thuoctinh;
    private javax.swing.JTextField txt_dongiaspct;
    private javax.swing.JTextField txt_mathuoctinh;
    private javax.swing.JTextField txt_motathuoctinh;
    private javax.swing.JTextField txt_mspct;
    private javax.swing.JTextField txt_objMaSanPham;
    private javax.swing.JTextField txt_objMoTa;
    private javax.swing.JTextField txt_objTenSanPham;
    private javax.swing.JTextField txt_slt;
    private javax.swing.JTextField txt_tenspct;
    private javax.swing.JTextField txt_tenthuoctinh;
    // End of variables declaration//GEN-END:variables

    @Override
    public void open() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override

    public void fillToTable() {
        mol = (DefaultTableModel) tbl_obj.getModel();
        mol.setRowCount(0);

        //getitem
        for (SanPham_ql x : repoSanPham.findAll()) {
            Object[] data = new Object[]{
                x.getId_san_pham(),
                x.getMa_san_pham(),
                x.getTen_san_pham(),
                repoCL.findById(x.getId_chat_lieu()).getTen_chat_lieu(),
                repoKD.findById(x.getId_kieu_dang()).getTen_kieu_dang(),
                repoKCG.findById(x.getId_kieu_co_giay()).getTen_co_giay(),
                repoDG.findById(x.getId_kieu_day_giay()).getTen_day_giay(),
                repoTH.findById(x.getId_thuong_hieu()).getTen_thuong_hieu(),
                repoXX.findById(x.getId_xuat_su()).getTen_xuat_su(),
                repoLSP.findById(x.getId_loai_san_pham()).getTen_loai_san_pham()};
            mol.addRow(data);
        }
        cbo_LoaiSP.removeAllItems();
        for (LoaiSanPham x : repoLSP.findAll()) {
            cbo_LoaiSP.addItem(x.getTen_loai_san_pham());
        }
        cbo_chatlieu.removeAllItems();
        for (ChatLieu x : repoCL.findAll()) {
            cbo_chatlieu.addItem(x.getTen_chat_lieu());
        }
        cbo_kieudaygiay.removeAllItems();
        for (KieuDayGiay x : repoDG.findAll()) {
            cbo_kieudaygiay.addItem(x.getTen_day_giay());
        }

        cbo_kieucogiay.removeAllItems();
        for (KieuCoGiay x : repoKCG.findAll()) {
            cbo_kieucogiay.addItem(x.getTen_co_giay());
        }
        cbo_kieudang.removeAllItems();
        for (KieuDang x : repoKD.findAll()) {
            cbo_kieudang.addItem(x.getTen_kieu_dang());
        }

        cbo_thuonghieu.removeAllItems();
        for (ThuongHieu x : repoTH.findAll()) {
            cbo_thuonghieu.addItem(x.getTen_thuong_hieu());

        }
        cbo_xuatxu.removeAllItems();
        for (XuatXu x : repoXX.findAll()) {
            cbo_xuatxu.addItem(x.getTen_xuat_su());
        }

        TableColumn col = tbl_obj.getColumnModel().getColumn(9);
        col.setMinWidth(0);
        col.setMaxWidth(0);
        col.setPreferredWidth(0);
        col.setWidth(0);

//        throw new UnsupportedOperationException("Npported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public void fillToRecycle() {
        DefaultTableModel model = (DefaultTableModel) tbl_recycle.getModel();
        model.setRowCount(0);

        List<SanPham_ql> list = repoSanPham.findRecycle();
        for (SanPham_ql x : list) {
            model.addRow(new Object[]{
                x.getMa_san_pham(),
                x.getTen_san_pham(),
                x.getId_thuong_hieu(),
                x.getId_xuat_su()
            });

        }
    }

    void fillTableSPCT(int idSP) {
        DefaultTableModel model = (DefaultTableModel) tbl_spct.getModel();
        model.setRowCount(0);

        List<SanPhamChiTiet_ql> list = repoSPCT.findallbyid(idSP);
        for (SanPhamChiTiet_ql x : list) {
            cbo_mausac.removeAllItems();
            for (MauSac ms : repoMS.findAll()) {
                cbo_mausac.addItem(ms.getTen_mau_sac());
            }
            String tenMau = "";
            if (x.getId_mau_sac() != null && x.getId_mau_sac() > 0) {
                MauSac ms = repoMS.findById(x.getId_mau_sac());
                if (ms != null) {
                    tenMau = ms.getTen_mau_sac();
                }
            }

            // Lấy tên kích cỡ (nếu có)
            cbo_kichco.removeAllItems();
            for (KichCo kc : repoKT.findAll()) {
                cbo_kichco.addItem(kc.getTen_kich_co());
            }
            String tenKichCo = "";
            if (x.getId_kich_co() != null && x.getId_kich_co() > 0) {
                KichCo kc = repoKT.findById(x.getId_kich_co());
                if (kc != null) {
                    tenKichCo = kc.getTen_kich_co();
                }
            }

            Object[] data = new Object[]{
                x.getId_san_pham_chi_tiet(),
                x.getMa_san_pham_chi_tiet(),
                x.getTen_san_pham(),
                tenMau, // tên màu
                tenKichCo, // tên kích cỡ
                x.getDon_gia(),
                x.getSo_luong_ton(),
                x.isTrang_thai(),
                x.getNgay_tao(),
                x.getNgay_cap_nhat()
            };
            mol2.addRow(data);
        }
    }

    public void fillToTableSPCT() {
        mol2 = (DefaultTableModel) tbl_spct.getModel();
        mol2.setRowCount(0);
        for (SanPhamChiTiet_ql x : repoSPCT.findAll()) {
            // Lấy tên màu (nếu có)
            cbo_mausac.removeAllItems();
            for (MauSac ms : repoMS.findAll()) {
                cbo_mausac.addItem(ms.getTen_mau_sac());
            }
            String tenMau = "";
            if (x.getId_mau_sac() != null && x.getId_mau_sac() > 0) {
                MauSac ms = repoMS.findById(x.getId_mau_sac());
                if (ms != null) {
                    tenMau = ms.getTen_mau_sac();
                }
            }

            // Lấy tên kích cỡ (nếu có)
            cbo_kichco.removeAllItems();
            for (KichCo kc : repoKT.findAll()) {
                cbo_kichco.addItem(kc.getTen_kich_co());
            }
            String tenKichCo = "";
            if (x.getId_kich_co() != null && x.getId_kich_co() > 0) {
                KichCo kc = repoKT.findById(x.getId_kich_co());
                if (kc != null) {
                    tenKichCo = kc.getTen_kich_co();
                }
            }

            Object[] data = new Object[]{
                x.getId_san_pham_chi_tiet(),
                x.getMa_san_pham_chi_tiet(),
                x.getTen_san_pham(),
                tenMau, // tên màu
                tenKichCo, // tên kích cỡ
                x.getDon_gia(),
                x.getSo_luong_ton(),
                x.isTrang_thai(),
                x.getNgay_tao(),
                x.getNgay_cap_nhat()
            };
            mol2.addRow(data);
        }
        cbo_sanpham.removeAllItems();
        for (SanPham_ql sp : repoSanPham.findAll()) {
            cbo_sanpham.addItem(sp.getMa_san_pham());

//        throw new UnsupportedOperationException("Npported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }
    }

    public void fillToTableSPCTbyName() {
        String name = JOptionPane.showInputDialog("nhap san pham: ");
        mol2.setRowCount(0);
        for (SanPhamChiTiet_ql x : repoSPCT.findByMa("%" + name + "%")) {
            // Lấy tên màu (nếu có)
            cbo_mausac.removeAllItems();
            for (MauSac ms : repoMS.findAll()) {
                cbo_mausac.addItem(ms.getTen_mau_sac());
            }
            String tenMau = "";
            if (x.getId_mau_sac() != null && x.getId_mau_sac() > 0) {
                MauSac ms = repoMS.findById(x.getId_mau_sac());
                if (ms != null) {
                    tenMau = ms.getTen_mau_sac();
                }
            }

            // Lấy tên kích cỡ (nếu có)
            cbo_kichco.removeAllItems();
            for (KichCo kc : repoKT.findAll()) {
                cbo_kichco.addItem(kc.getTen_kich_co());
            }
            String tenKichCo = "";
            if (x.getId_kich_co() != null && x.getId_kich_co() > 0) {
                KichCo kc = repoKT.findById(x.getId_kich_co());
                if (kc != null) {
                    tenKichCo = kc.getTen_kich_co();
                }
            }

            Object[] data = new Object[]{
                x.getId_san_pham_chi_tiet(),
                x.getMa_san_pham_chi_tiet(),
                x.getTen_san_pham(),
                tenMau, // tên màu
                tenKichCo, // tên kích cỡ
                x.getDon_gia(),
                x.getSo_luong_ton(),
                x.isTrang_thai(),
                x.getNgay_tao(),
                x.getNgay_cap_nhat()
            };
            mol2.addRow(data);
        }
        cbo_sanpham.removeAllItems();
        for (SanPham_ql sp : repoSanPham.findAll()) {
            cbo_sanpham.addItem(sp.getMa_san_pham());

//        throw new UnsupportedOperationException("Npported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }
    }

    public void fillToTableLoaiSanPham() {
        mol3 = (DefaultTableModel) tbl_thuoctinh.getModel();
        mol3.setRowCount(0);

        //getitem
        for (LoaiSanPham x : repoLSP.findAll()) {
            Object[] data = new Object[]{
                x.getId_loai_san_pham(),
                x.getMa_loai_san_pham(),
                x.getTen_loai_san_pham(),
                x.getMo_ta()
            };
            mol3.addRow(data);
        }

//        throw new UnsupportedOperationException("Npported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public void fillToTableChatLieu() {
        mol3 = (DefaultTableModel) tbl_thuoctinh.getModel();
        mol3.setRowCount(0);

        //getitem
        for (ChatLieu x : repoCL.findAll()) {
            Object[] data = new Object[]{
                x.getId_chat_lieu(),
                x.getMa_chat_lieu(),
                x.getTen_chat_lieu()
            };
            mol3.addRow(data);
        }

//        throw new UnsupportedOperationException("Npported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public void fillToTableMauSac() {
        mol3 = (DefaultTableModel) tbl_thuoctinh.getModel();
        mol3.setRowCount(0);

        //getitem
        for (MauSac x : repoMS.findAll()) {
            Object[] data = new Object[]{
                x.getId_mau_sac(),
                x.getMa_mau_sac(),
                x.getTen_mau_sac(),};
            mol3.addRow(data);
        }

//        throw new UnsupportedOperationException("Npported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public void fillToTableKichThuoc() {
        mol3 = (DefaultTableModel) tbl_thuoctinh.getModel();
        mol3.setRowCount(0);

        //getitem
        for (KichCo x : repoKT.findAll()) {
            Object[] data = new Object[]{
                x.getId_kich_co(),
                x.getMa_kich_co(),
                x.getTen_kich_co(),};
            mol3.addRow(data);
        }

//        throw new UnsupportedOperationException("Npported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public void fillToTableKieuDang() {
        mol3 = (DefaultTableModel) tbl_thuoctinh.getModel();
        mol3.setRowCount(0);

        //getitem
        for (KieuDang x : repoKD.findAll()) {
            Object[] data = new Object[]{
                x.getId_kieu_dang(),
                x.getMa_kieu_dang(),
                x.getTen_kieu_dang(),};
            mol3.addRow(data);
        }

//        throw new UnsupportedOperationException("Npported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public void fillToTableKieuCoGiay() {
        mol3 = (DefaultTableModel) tbl_thuoctinh.getModel();
        mol3.setRowCount(0);

        //getitem
        for (KieuCoGiay x : repoKCG.findAll()) {
            Object[] data = new Object[]{
                x.getId_kieu_co_giay(),
                x.getMa_co_giay(),
                x.getTen_co_giay(),};
            mol3.addRow(data);
        }

//        throw new UnsupportedOperationException("Npported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public void fillToTableKieuDayGiay() {
        mol3 = (DefaultTableModel) tbl_thuoctinh.getModel();
        mol3.setRowCount(0);

        //getitem
        for (KieuDayGiay x : repoDG.findAll()) {
            Object[] data = new Object[]{
                x.getId_kieu_day_giay(),
                x.getMa_day_giay(),
                x.getTen_day_giay(),};
            mol3.addRow(data);
        }

//        throw new UnsupportedOperationException("Npported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public void fillToTableKieuThuongHieu() {
        mol3 = (DefaultTableModel) tbl_thuoctinh.getModel();
        mol3.setRowCount(0);

        //getitem
        for (ThuongHieu x : repoTH.findAll()) {
            Object[] data = new Object[]{
                x.getId_thuong_hieu(),
                x.getMa_thuong_hieu(),
                x.getTen_thuong_hieu(),
                x.getMo_ta(),};
            mol3.addRow(data);
        }

//        throw new UnsupportedOperationException("Npported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public void fillToTableKieuXuatSu() {
        mol3 = (DefaultTableModel) tbl_thuoctinh.getModel();
        mol3.setRowCount(0);

        //getitem
        for (XuatXu x : repoXX.findAll()) {
            Object[] data = new Object[]{
                x.getId_xuat_su(),
                x.getMa_xuat_su(),
                x.getTen_xuat_su(),
                x.getMo_ta(),};
            mol3.addRow(data);
        }

//        throw new UnsupportedOperationException("Npported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void fillToCbo() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void edit() {
        int id = Integer.parseInt(tbl_obj.getValueAt(tbl_obj.getSelectedRow(), 0).toString());
        SanPham_ql x = repoSanPham.findById(id);

        this.setForm(x);
//        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public void editspct() {
        int id = Integer.parseInt(tbl_spct.getValueAt(tbl_spct.getSelectedRow(), 0).toString());
        SanPhamChiTiet_ql x = repoSPCT.findById(id);
        this.setFormspct(x);
//        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void create() {
        SanPham_ql x = this.getForm();
        repoSanPham.create(x);
        //throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public void createspct() {
        SanPhamChiTiet_ql x = this.getFormspct();
        if (x == null) {
            return;
        }
        repoSPCT.create(x);
        //throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public void createloaisp() {
        LoaiSanPham x = this.getFormLoaiSP();
        repoLSP.create(x);
        //throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    private void clearFormThuocTinh() {
        txt_mathuoctinh.setText("");
        txt_tenthuoctinh.setText("");
        txt_motathuoctinh.setText("");
    }

    @Override
    public void update() {
        SanPham_ql sp = this.getForm();
        int index = tbl_obj.getSelectedRow();
        int idDangChon = Integer.parseInt(tbl_obj.getValueAt(index, 0).toString());

        // ❗ Lấy danh sách có mã giống mã đang sửa
        List<SanPham_ql> list = repoSanPham.findByMa(sp.getMa_san_pham());

        // ❗ Nếu list không rỗng và mã này thuộc về sản phẩm KHÁC → báo trùng
        if (!list.isEmpty()) {
            SanPham_ql spTonTai = list.get(0);

            // Nếu ID khác nhau → trùng mã
            if (spTonTai.getId_san_pham() != idDangChon) {
                JOptionPane.showMessageDialog(this,
                        "Mã sản phẩm đã tồn tại! Không thể cập nhật.");
                return;
            }
        }

        // Nếu không trùng → cho update
        sp.setId_san_pham(idDangChon);  // cập nhật ID vào object
        repoSanPham.update(sp);
        JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
    }

    public void updatespct() {
//         int row = tbl_spct.getSelectedRow();
//    if (row < 0) {
//        JOptionPane.showMessageDialog(this, "Hãy chọn dòng cần sửa");
//        return;
//    }
//
//    int id = Integer.parseInt(tbl_spct.getValueAt(row, 0).toString()); // ID SPCT
//    SanPhamChiTiet spct = this.getFormspct();
//    spct.setId_san_pham_chi_tiet(id);  // BẮT BUỘC
//
//    repoSPCT.update(spct);
        SanPhamChiTiet_ql x = this.getFormupdatespct();
        repoSPCT.update(x);
        //throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void delete() {

        int row = tbl_spct.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Bạn chưa chọn sản phẩm chi tiết!");
            return;
        }

        int maSPCT = Integer.parseInt(tbl_spct.getValueAt(row, 0).toString());

        // gọi DAO xóa mềm
        repoSPCT.deleteById(maSPCT);

        fillToTableSPCT();   // load lại bảng
        JOptionPane.showMessageDialog(this, "Đã xóa SPCT!");
        //throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void clear() {
        txt_objMaSanPham.setText("");
        txt_objTenSanPham.setText("");
        txt_objMoTa.setText("");
        cbo_chatlieu.setSelectedIndex(-1);
        cbo_LoaiSP.setSelectedIndex(-1);
        cbo_kieucogiay.setSelectedIndex(-1);
        cbo_kieudang.setSelectedIndex(-1);
        cbo_kieudaygiay.setSelectedIndex(-1);
        cbo_thuonghieu.setSelectedIndex(-1);
        cbo_xuatxu.setSelectedIndex(-1);
        //throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public void clearspct() {
        txt_mspct.setText("");
        txt_tenspct.setText("");
        txt_dongiaspct.setText("");
        txt_slt.setText("");
        cbo_mausac.setSelectedIndex(-1);
        cbo_kichco.setSelectedIndex(-1);
        rdogrp_trangThai.clearSelection();

        //throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    

   


    @Override
    public void setEditable(boolean editable) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void checkAll() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void uncheckAll() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void deleteCheckedItems() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void moveFirst() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void movePrevious() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void moveNext() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void moveLast() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void moveTo(int rowIndex) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void setForm(SanPham_ql entity) {

        txt_objMaSanPham.setText(entity.getMa_san_pham());
        txt_objTenSanPham.setText(entity.getTen_san_pham());
        txt_objMoTa.setText(entity.getMo_ta());

        LoaiSanPham lsp = repoLSP.findById(entity.getId_loai_san_pham());
        cbo_LoaiSP.setSelectedItem(lsp.getTen_loai_san_pham());

        // Chat liệu
        ChatLieu cl = repoCL.findById(entity.getId_chat_lieu());
        cbo_chatlieu.setSelectedItem(cl.getTen_chat_lieu());

        // Kiểu dáng
        KieuDang kd = repoKD.findById(entity.getId_kieu_dang());
        cbo_kieudang.setSelectedItem(kd.getTen_kieu_dang());

        // Kiểu cổ giày
        KieuCoGiay kcg = repoKCG.findById(entity.getId_kieu_co_giay());
        cbo_kieucogiay.setSelectedItem(kcg.getTen_co_giay());

        // Kiểu dây giày
        KieuDayGiay dg = repoDG.findById(entity.getId_kieu_day_giay());
        cbo_kieudaygiay.setSelectedItem(dg.getTen_day_giay());

        // Thương hiệu
        ThuongHieu th = repoTH.findById(entity.getId_thuong_hieu());
        cbo_thuonghieu.setSelectedItem(th.getTen_thuong_hieu());

        // Xuất xứ
        XuatXu xx = repoXX.findById(entity.getId_xuat_su());
        cbo_xuatxu.setSelectedItem(xx.getTen_xuat_su());

        //throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public void setFormspct(SanPhamChiTiet_ql entity) {
        txt_mspct.setText(entity.getMa_san_pham_chi_tiet());
        txt_tenspct.setText(entity.getTen_san_pham());
        txt_dongiaspct.setText(entity.getDon_gia().toString());
        txt_slt.setText(String.valueOf(entity.getSo_luong_ton()));
        cbo_sanpham.setSelectedItem(entity.getMa_san_pham());
        if (entity.getId_mau_sac() == null) {
            // Không có màu → bỏ chọn
            cbo_mausac.setSelectedIndex(-1);
        } else {
            MauSac ms = repoMS.findById(entity.getId_mau_sac());
            if (ms != null) {
                cbo_mausac.setSelectedItem(ms.getTen_mau_sac());
            } else {
                // Màu không tồn tại → cũng bỏ chọn
                cbo_mausac.setSelectedIndex(-1);
            }
        }

        if (entity.getId_kich_co() == null) {
            // Không có màu → bỏ chọn
            cbo_kichco.setSelectedIndex(-1);
        } else {
            KichCo kc = repoKT.findById(entity.getId_kich_co());
            if (kc != null) {
                cbo_kichco.setSelectedItem(kc.getTen_kich_co());
            } else {
                // Màu không tồn tại → cũng bỏ chọn
                cbo_kichco.setSelectedIndex(-1);
            }
        }
        checkSoLuongTon();
    }

    public void setFormLoaiSP(LoaiSanPham entity) {
        txt_mathuoctinh.setText(entity.getMa_loai_san_pham());
        txt_tenthuoctinh.setText(entity.getTen_loai_san_pham());
        txt_motathuoctinh.setText(entity.getMo_ta());
    }

    private void setFormChatLieu(ChatLieu entity) {
   txt_mathuoctinh.setText(entity.getMa_chat_lieu());
        txt_tenthuoctinh.setText(entity.getTen_chat_lieu());
    
}


    public void setFormMauSac(MauSac entity) {
        txt_mathuoctinh.setText(entity.getMa_mau_sac());
        txt_tenthuoctinh.setText(entity.getTen_mau_sac());
    }

    public void setFormKichThuoc(KichCo entity) {
        txt_mathuoctinh.setText(entity.getMa_kich_co());
        txt_tenthuoctinh.setText(entity.getTen_kich_co());
    }

    public void setFormKieuDang(KieuDang entity) {
        txt_mathuoctinh.setText(entity.getMa_kieu_dang());
        txt_tenthuoctinh.setText(entity.getTen_kieu_dang());
    }

    public void setFormKieuCoGiay(KieuCoGiay entity) {
        txt_mathuoctinh.setText(entity.getMa_co_giay());
        txt_tenthuoctinh.setText(entity.getTen_co_giay());
    }

    public void setFormKieuDayGiay(KieuDayGiay entity) {
        txt_mathuoctinh.setText(entity.getMa_day_giay());
        txt_tenthuoctinh.setText(entity.getTen_day_giay());
    }

    public void setFormThuongHieu(ThuongHieu entity) {
        txt_mathuoctinh.setText(entity.getMa_thuong_hieu());
        txt_tenthuoctinh.setText(entity.getTen_thuong_hieu());
        txt_motathuoctinh.setText(entity.getMo_ta());
    }

    public void setFormXuatSu(XuatXu entity) {
        txt_mathuoctinh.setText(entity.getMa_xuat_su());
        txt_tenthuoctinh.setText(entity.getTen_xuat_su());
        txt_motathuoctinh.setText(entity.getMo_ta());
    }

   private String safe(Object o) {
    return o == null ? "" : o.toString();
}

private void setFormThuocTinh() {
    int row = tbl_thuoctinh.getSelectedRow();
    if (row == -1) return;

    txt_mathuoctinh.setText(safe(tbl_thuoctinh.getValueAt(row, 1))); // mã
    txt_tenthuoctinh.setText(safe(tbl_thuoctinh.getValueAt(row, 2))); // tên
    txt_motathuoctinh.setText(safe(tbl_thuoctinh.getValueAt(row, 3))); // mô tả
}


    @Override
    public SanPham_ql getForm() {
        SanPham_ql sp = new SanPham_ql();

        // Lấy text
        sp.setMa_san_pham(txt_objMaSanPham.getText());
        sp.setTen_san_pham(txt_objTenSanPham.getText());
        sp.setMo_ta(txt_objMoTa.getText());

        // Lấy ID từ tên được chọn trên ComboBox
        LoaiSanPham lsp = repoLSP.findByName(cbo_LoaiSP.getSelectedItem().toString());
        sp.setId_loai_san_pham(lsp.getId_loai_san_pham());

        ChatLieu cl = repoCL.findByName(cbo_chatlieu.getSelectedItem().toString());
        sp.setId_chat_lieu(cl.getId_chat_lieu());

        KieuDang kd = repoKD.findByName(cbo_kieudang.getSelectedItem().toString());
        sp.setId_kieu_dang(kd.getId_kieu_dang());

        KieuCoGiay kcg = repoKCG.findByName(cbo_kieucogiay.getSelectedItem().toString());
        sp.setId_kieu_co_giay(kcg.getId_kieu_co_giay());

        KieuDayGiay dg = repoDG.findByName(cbo_kieudaygiay.getSelectedItem().toString());
        sp.setId_kieu_day_giay(dg.getId_kieu_day_giay());

        ThuongHieu th = repoTH.findByName(cbo_thuonghieu.getSelectedItem().toString());
        sp.setId_thuong_hieu(th.getId_thuong_hieu());

        XuatXu xx = repoXX.findByName(cbo_xuatxu.getSelectedItem().toString());
        sp.setId_xuat_su(xx.getId_xuat_su());

        return sp;
        //throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public SanPhamChiTiet_ql getFormspct() {
        SanPhamChiTiet_ql spct = new SanPhamChiTiet_ql();

        // ---- BẮT LỖI CHƯA CHỌN SẢN PHẨM ----
        if (selectedIdSanPham == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn sản phẩm trước khi thêm SPCT!");
            return null;
        }

//    spct.setId_san_pham(repoSanPham.findByName(temp).getId_san_pham());  // <<< FIX LỖI CHÍNH
        spct.setId_san_pham(selectedIdSanPham);  // <<< FIX LỖI CHÍNH

        // Lấy text
        spct.setMa_san_pham_chi_tiet(txt_mspct.getText());
        spct.setTen_san_pham(txt_tenspct.getText());
        spct.setDon_gia(new BigDecimal(txt_dongiaspct.getText()));

        MauSac ms = repoMS.findByName(cbo_mausac.getSelectedItem().toString());
        spct.setId_mau_sac(ms.getId_mau_sac());

        KichCo kc = repoKT.findByName(cbo_kichco.getSelectedItem().toString());
        spct.setId_kich_co(kc.getId_kich_co());
        int soLuong = Integer.parseInt(txt_slt.getText().trim());
        spct.setSo_luong_ton(soLuong);
        if (soLuong <= 0) {
            spct.setTrang_thai(false);
        } else {
            spct.setTrang_thai(true);
        }

        return spct;
    }

    public SanPhamChiTiet_ql getFormupdatespct() {
        int row = tbl_spct.getSelectedRow();
        int idspct = Integer.parseInt(tbl_spct.getValueAt(row, 0).toString());
        SanPhamChiTiet_ql spct = repoSPCT.findById(idspct);

//        // ---- BẮT LỖI CHƯA CHỌN SẢN PHẨM ----
//        if (selectedIdSanPham == null) {
//            JOptionPane.showMessageDialog(this, "Vui lòng chọn sản phẩm trước khi thêm SPCT!");
//            return null;
//        }
//
        ////    spct.setId_san_pham(repoSanPham.findByName(temp).getId_san_pham());  // <<< FIX LỖI CHÍNH
//        spct.setId_san_pham(selectedIdSanPham);  // <<< FIX LỖI CHÍNH
//
//        // Lấy text
        spct.setMa_san_pham_chi_tiet(txt_mspct.getText());
        spct.setTen_san_pham(txt_tenspct.getText());
        spct.setDon_gia(new BigDecimal(txt_dongiaspct.getText()));
        spct.setSo_luong_ton(Integer.parseInt(txt_slt.getText()));

        MauSac ms = repoMS.findByName(cbo_mausac.getSelectedItem().toString());
        spct.setId_mau_sac(ms.getId_mau_sac());

        KichCo kc = repoKT.findByName(cbo_kichco.getSelectedItem().toString());
        spct.setId_kich_co(kc.getId_kich_co());

        spct.setTrang_thai(rdo_dangban.isSelected());

        return spct;
    }

    public LoaiSanPham getFormLoaiSP() {
        LoaiSanPham lsp = new LoaiSanPham();
        lsp.setMa_loai_san_pham(txt_mathuoctinh.getText());
        lsp.setTen_loai_san_pham(txt_tenthuoctinh.getText());
        lsp.setMo_ta(txt_motathuoctinh.getText());

        return lsp;
        //throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public ChatLieu getFormCL() {
        ChatLieu cl = new ChatLieu();
        cl.setMa_chat_lieu(txt_mathuoctinh.getText());
        cl.setTen_chat_lieu(txt_tenthuoctinh.getText());
        return cl;
        //throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public MauSac getFormMS() {
        MauSac ms = new MauSac();
        ms.setMa_mau_sac(txt_mathuoctinh.getText());
        ms.setTen_mau_sac(txt_tenthuoctinh.getText());
        return ms;
        //throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public KichCo getFormKT() {
        KichCo kc = new KichCo();
        kc.setMa_kich_co(txt_mathuoctinh.getText());
        kc.setTen_kich_co(txt_tenthuoctinh.getText());
        return kc;
        //throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public KieuDang getFormKD() {
        KieuDang kd = new KieuDang();
        kd.setMa_kieu_dang(txt_mathuoctinh.getText());
        kd.setTen_kieu_dang(txt_tenthuoctinh.getText());
        return kd;
        //throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public KieuCoGiay getFormKCG() {
        KieuCoGiay kcg = new KieuCoGiay();
        kcg.setMa_co_giay(txt_mathuoctinh.getText());
        kcg.setTen_co_giay(txt_tenthuoctinh.getText());
        return kcg;
        //throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public KieuDayGiay getFormDG() {
        KieuDayGiay dg = new KieuDayGiay();
         dg.setMa_day_giay(txt_mathuoctinh.getText());
        dg.setTen_day_giay(txt_tenthuoctinh.getText());
        return dg;
        //throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public ThuongHieu getFormTH() {
        ThuongHieu th = new ThuongHieu();
        th.setMa_thuong_hieu(txt_mathuoctinh.getText());
        th.setTen_thuong_hieu(txt_tenthuoctinh.getText());
        th.setMo_ta(txt_motathuoctinh.getText());

        return th;
//throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public XuatXu getFormXS() {
        XuatXu xs = new XuatXu();
        xs.setMa_xuat_su(txt_mathuoctinh.getText());
        xs.setTen_xuat_su(txt_tenthuoctinh.getText());
        xs.setMo_ta(txt_motathuoctinh.getText());

        return xs;
        //throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public List<SanPham_ql> findString(String key) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public List<SanPham_ql> filterCategory(String key) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    private void checkSoLuongTon() {
    try {
        int soLuong = Integer.parseInt(txt_slt.getText().trim());

        if (soLuong <= 0) {
            rdo_ngungban.setSelected(true);
        } else {
            rdo_dangban.setSelected(true);
        }

    } catch (Exception e) {
        rdo_ngungban.setSelected(true);
    }
}
}
