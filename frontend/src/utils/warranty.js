// Pure builder for the printable warranty slip HTML, used by the "In phiếu bảo hành"
// action on BaoHanhView.vue. Mirrors the visual style of receipt.js's slip.
import { vnd } from './format'

function fmtDate(s) { return s ? String(s).slice(0, 10) : '—' }

/** Pure: build the printable warranty slip HTML from a bao_hanh row. */
export function buildWarrantyHtml(bh) {
  const b = bh || {}

  return `<!doctype html><html><head><meta charset="utf-8"><title>${b.ma || ''}</title>
    <style>
      *{font-family:'Segoe UI',Arial,sans-serif;box-sizing:border-box}
      body{width:320px;margin:0 auto;padding:12px;color:#111}
      h1{font-size:18px;text-align:center;margin:0 0 2px}
      .sub{text-align:center;font-size:11px;color:#555;margin-bottom:8px}
      .tag{text-align:center;margin:6px 0;font-weight:700;color:#0B895A}
      .meta{font-size:12px;margin-bottom:8px;border-bottom:1px dashed #999;padding-bottom:6px}
      .meta div{display:flex;justify-content:space-between;padding:1px 0}
      .grp{font-weight:700;font-size:12px;margin:8px 0 4px;color:#0B895A;text-transform:uppercase}
      .mota{font-size:12px;border-top:1px dashed #999;margin-top:8px;padding-top:6px}
      .thanks{text-align:center;font-size:11px;margin-top:10px;color:#555}
    </style></head><body>
    <h1>BShoes</h1>
    <div class="sub">Cửa hàng giày dép BShoes</div>
    <div class="tag">PHIẾU BẢO HÀNH</div>
    <div class="meta">
      <div><span>Mã BH</span><span><b>${b.ma || ''}</b></span></div>
      <div><span>Khách hàng</span><span>${b.tenKH || ''}</span></div>
      <div><span>SĐT</span><span>${b.sdt || ''}</span></div>
    </div>
    <div class="grp">Sản phẩm</div>
    <div class="meta">
      <div><span>Model</span><span>${b.model || ''}</span></div>
      <div><span>Màu / Size</span><span>${b.mauSize || ''}</span></div>
      <div><span>Serial</span><span>${b.serial || ''}</span></div>
      <div><span>Loại lỗi</span><span>${b.loai || ''}</span></div>
    </div>
    <div class="grp">Bảo hành</div>
    <div class="meta">
      <div><span>Đơn vị BH</span><span>${b.donVi || ''}</span></div>
      <div><span>NV xử lý</span><span>${b.nv || ''}</span></div>
      <div><span>Bắt đầu</span><span>${fmtDate(b.batDau)}</span></div>
      <div><span>Kết thúc</span><span>${fmtDate(b.hetHan)}</span></div>
      <div><span>Chi phí</span><span>${vnd(b.chiPhi || 0)}</span></div>
      <div><span>Thay linh kiện</span><span>${b.thayLinhKien ? 'Có' : 'Không'}</span></div>
      <div><span>Trạng thái</span><span>${b.trangThai || ''}</span></div>
    </div>
    <div class="mota">Mô tả lỗi: ${b.moTa || '—'}</div>
    <div class="thanks">Cảm ơn quý khách &amp; hẹn gặp lại!</div>
    </body></html>`
}
