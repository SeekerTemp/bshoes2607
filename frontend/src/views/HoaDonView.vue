<script setup>
import { computed, ref } from 'vue'
import AppShell from '../components/layout/AppShell.vue'
import PageHeader from '../components/ui/PageHeader.vue'
import AppSelect from '../components/ui/AppSelect.vue'
import AppButton from '../components/ui/AppButton.vue'
import QrScanner from '../components/ui/QrScanner.vue'
import { useHoaDon } from '../composables/useHoaDon'
import { useToast } from '../composables/useToast'
import { vnd } from '../utils/format'

const {
  sanPham, khachHangOptions, voucherOptions, hinhThucOptions,
  keyword, ketQua, orderTab, leftTab,
  hoaDons, activeId, active, createHoaDon, switchHoaDon, removeHoaDon,
  tongHoaDonHomNay, tongTienHomNay,
  gio, selectedLineId, selectedLine, selectLine,
  addLine, removeLine, inc, dec, clampLine, hoanTra, scanAdd,
  soLuong, tamTinh, giamGia, phiShip, phaiTra, tienThua,
  thanhToan,
} = useHoaDon()
const { notify } = useToast()

const showScanner = ref(false)
async function onScan(text) {
  const r = await scanAdd(text)
  if (r.ok) notify('Đã thêm: ' + r.ten, 'success')
  else notify('Không tìm thấy mã: ' + text, 'warning')
}

const voucherSelectOptions = computed(() =>
  voucherOptions.value.map(v => ({ value: v.value, label: v.label }))
)
const voucherModel = computed({
  get: () => active.value.voucher,
  set: v => { active.value.voucher = Number(v) },
})

function needLine(action) {
  if (!selectedLine.value) { notify('Chọn một dòng trong giỏ hàng trước', 'warning'); return false }
  return true
}
function tangSL() { if (needLine()) inc(selectedLine.value) }
function giamSL() { if (needLine()) dec(selectedLine.value) }
function boSanPham() { if (needLine()) removeLine(selectedLine.value) }
function hoanTraLine() { if (needLine()) hoanTra(selectedLine.value) }

function pay() {
  if (gio.value.length === 0) return
  notify(`Thanh toán ${active.value.ma}: ${vnd(phaiTra.value)}`, 'success')
  thanhToan()
}
function huy() {
  if (confirm(`Huỷ hoá đơn ${active.value.ma}?`)) removeHoaDon(active.value.id)
}
</script>

<template>
  <AppShell>
    <PageHeader title="Bán hàng tại quầy" />

    <div class="pos-grid">
      <!-- ================= LEFT COLUMN (section_1) ================= -->
      <div class="pos-left">
        <!-- left content tabs -->
        <div class="pos-toptabs">
          <button class="pos-toptab" :class="{ active: leftTab === 'chitiet' }" @click="leftTab = 'chitiet'">Thông tin chi tiết</button>
          <button class="pos-toptab" :class="{ active: leftTab === 'thuoctinh' }" @click="leftTab = 'thuoctinh'">Thuộc tính sản phẩm</button>
        </div>

        <!-- Danh sách sản phẩm -->
        <section class="card pos-panel">
          <header class="pos-panel-head">
            <h6 class="pos-title">DANH SÁCH SẢN PHẨM</h6>
            <div class="d-flex align-items-center gap-2">
              <div class="input-group input-group-sm pos-search">
                <span class="input-group-text bg-white"><i class="bi bi-search"></i></span>
                <input class="form-control" v-model="keyword" placeholder="Tìm kiếm sản phẩm" />
              </div>
              <AppButton size="sm" variant="primary" icon="qr-code-scan" @click="showScanner = true">Quét QR</AppButton>
            </div>
          </header>
          <div class="table-scroll" style="height: 210px">
            <table class="table table-sm table-hover align-middle pos-table mb-0">
              <thead>
                <tr>
                  <th class="text-center">STT</th><th>Mã SP</th><th>Tên sp</th>
                  <th>Màu sắc</th><th>Kích thước</th><th class="text-end">Số lượng bán</th><th></th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="(p, i) in ketQua" :key="p.id" @dblclick="addLine(p)">
                  <td class="text-center">{{ i + 1 }}</td>
                  <td>{{ p.ma }}</td>
                  <td class="fw-medium">{{ p.ten }}</td>
                  <td>{{ p.mau }}</td>
                  <td>{{ p.size }}</td>
                  <td class="text-end">{{ p.ton }}</td>
                  <td class="text-end">
                    <button class="btn btn-sm btn-success py-0 px-2" :disabled="p.ton === 0" title="Thêm vào giỏ" @click="addLine(p)">
                      <i class="bi bi-plus-lg"></i>
                    </button>
                  </td>
                </tr>
                <tr v-if="ketQua.length === 0"><td colspan="7" class="text-center text-muted py-3">Không tìm thấy sản phẩm</td></tr>
              </tbody>
            </table>
          </div>
        </section>

        <!-- Giỏ hàng -->
        <section class="card pos-panel">
          <header class="pos-panel-head">
            <h6 class="pos-title">GIỎ HÀNG</h6>
            <span class="text-muted small">Số lượng: <b>{{ soLuong }}</b> • Tạm tính: <b>{{ vnd(tamTinh) }}</b></span>
          </header>
          <div class="table-scroll" style="height: 190px">
            <table class="table table-sm align-middle pos-table mb-0">
              <thead>
                <tr>
                  <th class="text-center">STT</th><th>Mã SP</th><th>Tên sp</th>
                  <th class="text-center">Số lượng</th><th class="text-end">Đơn giá</th>
                  <th class="text-end">Giảm giá</th><th class="text-end">Thành tiền</th><th>Trạng thái</th><th></th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="(l, i) in gio" :key="l.id"
                    :class="{ 'row-selected': l.id === selectedLineId, 'row-return': l.trangThai === 'Hoàn trả' }"
                    @click="selectLine(l)">
                  <td class="text-center">{{ i + 1 }}</td>
                  <td>{{ l.ma }}</td>
                  <td class="fw-medium">{{ l.ten }}</td>
                  <td>
                    <div class="stepper">
                      <button class="btn btn-sm btn-outline-danger" @click.stop="dec(l)">−</button>
                      <input type="number" min="1" :max="l.ton" class="form-control form-control-sm text-center"
                             v-model.number="l.soLuong" @change="clampLine(l)" @click.stop>
                      <button class="btn btn-sm btn-outline-success" @click.stop="inc(l)">+</button>
                    </div>
                  </td>
                  <td class="text-end">{{ vnd(l.gia) }}</td>
                  <td class="text-end">{{ vnd(0) }}</td>
                  <td class="text-end fw-medium">{{ vnd(l.gia * l.soLuong) }}</td>
                  <td>{{ l.trangThai }}</td>
                  <td class="text-end"><button class="btn btn-sm btn-outline-danger py-0 px-1" title="Xoá" @click.stop="removeLine(l)"><i class="bi bi-x"></i></button></td>
                </tr>
                <tr v-if="gio.length === 0"><td colspan="9" class="text-center text-muted py-4">Giỏ hàng trống — chọn sản phẩm bên trên</td></tr>
              </tbody>
            </table>
          </div>
          <footer class="pos-cart-actions">
            <button class="btn btn-sm btn-outline-secondary" @click="notify('Nhập tay', 'info')">Nhập tay</button>
            <button class="btn btn-sm btn-outline-secondary" @click="notify('Nhập mã / quét QR', 'info')">Nhập mã</button>
            <button class="btn btn-sm btn-outline-secondary" @click="tangSL">Tăng số lượng</button>
            <button class="btn btn-sm btn-outline-danger" @click="giamSL">Giảm số lượng</button>
            <button class="btn btn-sm btn-outline-danger" @click="boSanPham">Bỏ sản phẩm</button>
            <button class="btn btn-sm btn-outline-danger" @click="hoanTraLine">Hoàn trả</button>
          </footer>
        </section>

        <!-- Danh sách hóa đơn (invoice queue) -->
        <section class="card pos-panel">
          <header class="pos-panel-head"><h6 class="pos-title">DANH SÁCH HÓA ĐƠN</h6></header>
          <div class="table-scroll" style="height: 150px">
            <table class="table table-sm table-hover align-middle pos-table mb-0">
              <thead>
                <tr>
                  <th class="text-center">STT</th><th>Mã hóa đơn</th><th>Tên nhân viên</th><th>Tên KH</th>
                  <th>Trạng Thái</th><th>Trạng Thái hàng</th><th>Ngày tạo</th><th></th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="(h, i) in hoaDons" :key="h.id"
                    :class="{ 'row-active': h.id === activeId }" @click="switchHoaDon(h.id)">
                  <td class="text-center">{{ i + 1 }}</td>
                  <td class="fw-medium">{{ h.ma }}</td>
                  <td>{{ h.nhanVien }}</td>
                  <td>{{ h.khachHang }}</td>
                  <td>{{ h.trangThai }}</td>
                  <td>{{ h.trangThaiHang }}</td>
                  <td>{{ h.ngayTao }}</td>
                  <td class="text-end"><button class="btn btn-sm btn-outline-danger py-0 px-1" title="Đóng" @click.stop="removeHoaDon(h.id)"><i class="bi bi-x"></i></button></td>
                </tr>
              </tbody>
            </table>
          </div>
          <footer class="pos-queue-foot">
            <span class="text-muted">Tổng hóa đơn hôm nay:</span>
            <span class="fw-semibold">{{ tongHoaDonHomNay }}</span>
            <span class="fw-bold" style="color: var(--c-primary)">{{ vnd(tongTienHomNay) }}</span>
          </footer>
        </section>
      </div>

      <!-- ================= RIGHT COLUMN (section_2 JTabbedPane) ================= -->
      <div class="pos-right">
        <div class="pos-tabs">
          <button class="pos-tab" :class="{ active: orderTab === 'hoadon' }" @click="orderTab = 'hoadon'">Hóa đơn</button>
          <button class="pos-tab" :class="{ active: orderTab === 'dathang' }" @click="orderTab = 'dathang'">Đặt hàng</button>
        </div>

        <div class="pos-panel-green">
          <!-- ---------- HÓA ĐƠN tab ---------- -->
          <template v-if="orderTab === 'hoadon'">
            <h5 class="green-title">Hóa đơn</h5>
            <div class="green-box">
              <div class="input-group input-group-sm mb-2">
                <span class="input-group-text">Mã hội viên</span>
                <input class="form-control" v-model="active.memberCode" placeholder="" />
                <button class="btn btn-success" @click="notify('Nhập SĐT hội viên', 'info')">Nhập sdt</button>
              </div>
              <button class="btn btn-success btn-sm w-100 mb-1" @click="notify('Thêm khách hàng mới', 'info')">Thêm khách hàng mới</button>
              <button class="btn btn-success btn-sm w-100" @click="active.khachHang = 'Khách lẻ'">Khách vãng lai</button>
            </div>

            <h6 class="green-sub">Thông tin đơn hàng</h6>
            <dl class="green-fields">
              <div class="gf"><dt>SDT</dt><dd>{{ active.sdt || '—' }}</dd></div>
              <div class="gf"><dt>Tên khách hàng</dt><dd>
                <AppSelect v-model="active.khachHang" :options="khachHangOptions" />
              </dd></div>
              <div class="gf"><dt>Tổng tiền hàng</dt><dd class="val">{{ vnd(tamTinh) }}</dd></div>
              <div class="gf"><dt>Mã giảm giá</dt><dd><AppSelect v-model="voucherModel" :options="voucherSelectOptions" /></dd></div>
              <div class="gf"><dt>Thành tiền</dt><dd class="val strong">{{ vnd(phaiTra) }}</dd></div>
              <div class="gf"><dt>Hình thức thanh toán</dt><dd><AppSelect v-model="active.hinhThuc" :options="hinhThucOptions" /></dd></div>
              <div class="gf"><dt>Tiền khách đưa</dt><dd>
                <input type="number" min="0" step="1000" class="form-control form-control-sm text-end" v-model.number="active.khachDua" placeholder="0">
              </dd></div>
              <div class="gf"><dt>Tiền thừa</dt><dd class="val">{{ vnd(tienThua) }}</dd></div>
            </dl>

            <div class="green-actions">
              <button class="btn btn-success w-100" :disabled="gio.length === 0" @click="pay">Thanh toán</button>
              <button class="btn btn-success w-100" @click="createHoaDon">Tạo hóa đơn</button>
              <button class="btn btn-success w-100" :disabled="gio.length === 0" @click="notify('In phiếu tạm tính: ' + vnd(phaiTra), 'info')">Phiếu tạm tính</button>
              <button class="btn btn-outline-danger w-100" @click="huy">Hủy</button>
              <button class="btn btn-light w-100" disabled>Xuất json thông tin sản phẩm</button>
              <button class="btn btn-light w-100" disabled>Import sản phẩm bằng list json/csv/excel</button>
            </div>
          </template>

          <!-- ---------- ĐẶT HÀNG tab ---------- -->
          <template v-else>
            <h5 class="green-title">Đặt hàng</h5>
            <div class="green-box">
              <button class="btn btn-success btn-sm w-100 mb-2" @click="notify('Thêm khách hàng mới', 'info')">Thêm khách hàng mới</button>
              <div class="input-group input-group-sm">
                <span class="input-group-text">Mã hội viên</span>
                <input class="form-control" v-model="active.memberCode" />
                <button class="btn btn-success" @click="notify('Nhập SĐT hội viên', 'info')">Nhập sdt</button>
              </div>
            </div>

            <h6 class="green-sub">Thông tin đơn hàng</h6>
            <dl class="green-fields">
              <div class="gf"><dt>Tên khách hàng</dt><dd><input class="form-control form-control-sm" v-model="active.khachHang"></dd></div>
              <div class="gf"><dt>SDT</dt><dd><input class="form-control form-control-sm" v-model="active.sdt"></dd></div>
              <div class="gf"><dt>Địa chỉ</dt><dd><input class="form-control form-control-sm" v-model="active.diaChi"></dd></div>
              <div class="gf"><dt>Tổng tiền hàng</dt><dd class="val">{{ vnd(tamTinh) }}</dd></div>
              <div class="gf"><dt>Mã giảm giá</dt><dd><AppSelect v-model="voucherModel" :options="voucherSelectOptions" /></dd></div>
              <div class="gf"><dt>Phí ship</dt><dd><input type="number" min="0" step="1000" class="form-control form-control-sm text-end" v-model.number="active.phiShip"></dd></div>
              <div class="gf"><dt>Thành tiền</dt><dd class="val strong">{{ vnd(phaiTra) }}</dd></div>
              <div class="gf"><dt>Hình thức thanh toán</dt><dd><AppSelect v-model="active.hinhThuc" :options="hinhThucOptions" /></dd></div>
              <div class="gf"><dt>Tiền khách đưa</dt><dd><input type="number" min="0" step="1000" class="form-control form-control-sm text-end" v-model.number="active.khachDua"></dd></div>
              <div class="gf"><dt>Tiền thừa</dt><dd class="val">{{ vnd(tienThua) }}</dd></div>
              <div class="gf gf-note"><dt>Ghi chú</dt><dd><textarea class="form-control form-control-sm" rows="3" v-model="active.ghiChu"></textarea></dd></div>
            </dl>

            <div class="green-actions">
              <button class="btn btn-success w-100" @click="createHoaDon">Tạo hóa đơn</button>
              <button class="btn btn-success w-100" @click="notify('Giao hàng', 'info')">Giao hàng</button>
              <button class="btn btn-success w-100" @click="notify('Đã giao', 'success')">Đã giao</button>
              <button class="btn btn-outline-danger w-100" @click="hoanTraLine">Hoàn trả</button>
              <button class="btn btn-light w-100" disabled>Xuất json thông tin sản phẩm</button>
              <button class="btn btn-light w-100" disabled>Import sản phẩm bằng list json/csv/excel</button>
            </div>
          </template>
        </div>
      </div>
    </div>

    <QrScanner v-model:open="showScanner" @detected="onScan" />
  </AppShell>
</template>

<style scoped>
.pos-grid {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 400px;
  gap: 16px;
  align-items: start;          /* the right panel no longer scales with the cart */
}
.pos-left { display: flex; flex-direction: column; gap: 14px; min-width: 0; }

/* ---- tab strips ---- */
.pos-toptabs, .pos-tabs { display: flex; gap: 4px; }
.pos-toptab, .pos-tab {
  padding: 6px 16px;
  border: 1px solid var(--c-border);
  border-bottom: none;
  border-radius: var(--radius-sm) var(--radius-sm) 0 0;
  background: #eef1f4;
  color: var(--c-text-muted);
  font-weight: 500;
  font-size: 14px;
  cursor: pointer;
}
.pos-toptab.active { background: var(--c-surface); color: var(--c-text); }
.pos-tab { background: #eef1f4; }
.pos-tab.active { background: var(--c-primary); color: #fff; border-color: var(--c-primary); }

/* ---- panels ---- */
.pos-panel { padding: 10px 12px; }
.pos-panel-head { display: flex; align-items: center; justify-content: space-between; gap: 12px; margin-bottom: 8px; }
.pos-title { margin: 0; font-weight: 700; letter-spacing: .3px; color: var(--c-text); }
.pos-search { max-width: 280px; }

/* fixed-height scroll areas: adding rows never grows the panel */
.table-scroll { overflow: auto; border: 1px solid var(--c-border); border-radius: var(--radius-sm); }
.pos-table { min-width: 560px; }
.pos-table thead th {
  position: sticky; top: 0; z-index: 1;
  background: var(--c-primary); color: #fff;
  font-weight: 600; font-size: 12px; white-space: nowrap;
}
.pos-table tbody td { font-size: 13px; }
.row-selected > td { background: var(--c-primary-subtle); }
.row-return > td { background: var(--c-danger-subtle); }
.row-active > td { background: var(--c-primary); color: #fff; cursor: pointer; }
.pos-table tbody tr { cursor: pointer; }

/* stepper inside cart */
.stepper { display: inline-flex; align-items: center; gap: 4px; }
.stepper input { width: 52px; }
.stepper .btn { line-height: 1; padding: 0 7px; }

.pos-cart-actions, .pos-queue-foot { display: flex; flex-wrap: wrap; gap: 6px; margin-top: 8px; }
.pos-queue-foot { justify-content: space-between; align-items: center; padding-top: 6px; }

/* ---- right green panel ---- */
.pos-right { position: sticky; top: 16px; }
.pos-panel-green {
  background: var(--c-primary);
  border-radius: 0 var(--radius) var(--radius) var(--radius);
  padding: 14px;
  color: #fff;
}
.green-title { font-weight: 700; margin: 2px 0 10px; }
.green-sub { font-weight: 600; margin: 12px 0 8px; opacity: .95; }
.green-box { background: rgba(255,255,255,.12); border-radius: var(--radius-sm); padding: 10px; }
.green-box .input-group-text { background: #fff; }

.green-fields { display: flex; flex-direction: column; gap: 6px; margin: 0; }
.gf { display: grid; grid-template-columns: 130px 1fr; align-items: center; gap: 8px; }
.gf dt { font-weight: 500; font-size: 13px; opacity: .95; }
.gf dd { margin: 0; }
.gf .val { background: #fff; color: var(--c-text); border-radius: var(--radius-sm); padding: 4px 8px; text-align: right; font-weight: 600; }
.gf .val.strong { color: var(--c-primary); }
.gf-note { grid-template-columns: 130px 1fr; align-items: start; }

.green-actions { display: flex; flex-direction: column; gap: 8px; margin-top: 14px; }
.green-actions .btn-success { background: #fff; color: var(--c-primary); border-color: #fff; font-weight: 600; }
.green-actions .btn-success:hover:not(:disabled) { background: #f0f0f0; }
.green-actions .btn-outline-danger { background: transparent; color: #ffdede; border-color: #ffb3b3; }
.green-actions .btn-light:disabled { opacity: .5; }

@media (max-width: 992px) {
  .pos-grid { grid-template-columns: 1fr; }
  .pos-right { position: static; }
}
</style>
