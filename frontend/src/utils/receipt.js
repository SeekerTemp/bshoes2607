// Shared receipt builder + printer, used by the POS ("Phiếu tạm tính" / checkout
// auto-print in HoaDonView.vue) and by the reprint action on the history/order
// screens (LichSuView.vue, DonHangView.vue).
//
// Normalized `receipt` shape (all callers must map into this before calling):
//   {
//     ma: string,                 // invoice code
//     ngay: string,                // display date/time (already formatted)
//     khach: string,                // customer display name
//     hinhThuc: string,             // payment method label
//     items: [{ ten, mota?, soLuong, donGia, thanhTien }],
//     tamTinh: number,
//     giamGia: number,
//     phiShip: number,
//     phaiTra: number,
//     paid?: boolean,               // true = "HÓA ĐƠN THANH TOÁN", false = "PHIẾU TẠM TÍNH" (default true)
//     khachDua?: number,            // cash given (shown only when paid + provided)
//     tienThua?: number,            // change (shown alongside khachDua)
//   }
import { vnd } from './format'

/** Pure: build the printable receipt HTML from a normalized receipt object. */
export function buildReceiptHtml(receipt) {
  const r = receipt || {}
  const items = Array.isArray(r.items) ? r.items : []
  const paid = r.paid !== false

  const rows = items.map((it, i) => {
    const thanhTien = it.thanhTien != null ? it.thanhTien : (it.donGia || 0) * (it.soLuong || 0)
    return `
    <tr>
      <td>${i + 1}</td>
      <td>${it.ten || ''}${it.mota ? `<br><small>${it.mota}</small>` : ''}</td>
      <td class="c">${it.soLuong ?? ''}</td>
      <td class="r">${vnd(it.donGia || 0)}</td>
      <td class="r">${vnd(thanhTien)}</td>
    </tr>`
  }).join('')

  return `<!doctype html><html><head><meta charset="utf-8"><title>${r.ma || ''}</title>
    <style>
      *{font-family:'Segoe UI',Arial,sans-serif;box-sizing:border-box}
      body{width:300px;margin:0 auto;padding:12px;color:#111}
      h1{font-size:18px;text-align:center;margin:0 0 2px}
      .sub{text-align:center;font-size:11px;color:#555;margin-bottom:8px}
      .meta{font-size:12px;margin-bottom:8px;border-bottom:1px dashed #999;padding-bottom:6px}
      table{width:100%;border-collapse:collapse;font-size:12px}
      th{border-bottom:1px solid #333;text-align:left;padding:3px 2px}
      td{padding:3px 2px;vertical-align:top}
      td.c,th.c{text-align:center}td.r,th.r{text-align:right}
      small{color:#666}
      .tot{margin-top:8px;border-top:1px dashed #999;padding-top:6px;font-size:12px}
      .tot div{display:flex;justify-content:space-between;padding:1px 0}
      .tot .big{font-weight:700;font-size:14px}
      .tag{text-align:center;margin:6px 0;font-weight:700;color:${paid ? '#0B895A' : '#b26a00'}}
      .thanks{text-align:center;font-size:11px;margin-top:10px;color:#555}
    </style></head><body>
    <h1>BShoes</h1>
    <div class="sub">Cửa hàng giày dép BShoes</div>
    <div class="tag">${paid ? 'HÓA ĐƠN THANH TOÁN' : 'PHIẾU TẠM TÍNH'}</div>
    <div class="meta">
      Mã HĐ: <b>${r.ma || ''}</b><br>
      Ngày: ${r.ngay || ''}<br>
      Khách: ${r.khach || 'Khách lẻ'}<br>
      Thanh toán: ${r.hinhThuc || ''}
    </div>
    <table>
      <thead><tr><th>#</th><th>Sản phẩm</th><th class="c">SL</th><th class="r">Đơn giá</th><th class="r">T.Tiền</th></tr></thead>
      <tbody>${rows}</tbody>
    </table>
    <div class="tot">
      <div><span>Tạm tính</span><span>${vnd(r.tamTinh)}</span></div>
      <div><span>Giảm giá</span><span>-${vnd(r.giamGia)}</span></div>
      ${r.phiShip ? `<div><span>Phí ship</span><span>${vnd(r.phiShip)}</span></div>` : ''}
      <div class="big"><span>Phải trả</span><span>${vnd(r.phaiTra)}</span></div>
      ${paid && r.khachDua ? `<div><span>Khách đưa</span><span>${vnd(r.khachDua)}</span></div><div><span>Tiền thừa</span><span>${vnd(r.tienThua)}</span></div>` : ''}
    </div>
    <div class="thanks">Cảm ơn quý khách &amp; hẹn gặp lại!</div>
    </body></html>`
}

/**
 * Build the receipt HTML, inject it into a hidden iframe and print it.
 * Hardened vs. the original inline version: no silent empty catch (failures are
 * logged and reported to the caller via the return value), and printing waits for
 * the iframe document to actually be ready (onload + readyState + rAF) instead of
 * firing after a bare 300ms timer.
 *
 * Returns true if printing was triggered, false on any failure (nothing to print,
 * or the iframe/document could not be prepared). Callers should notify the user
 * on a falsy return.
 */
export function printReceipt(receipt) {
  const items = receipt && Array.isArray(receipt.items) ? receipt.items : []
  if (!receipt || items.length === 0) {
    console.error('printReceipt: nothing to print (no items)', receipt)
    return false
  }

  let html
  try {
    html = buildReceiptHtml(receipt)
  } catch (e) {
    console.error('printReceipt: failed to build receipt HTML', e)
    return false
  }

  const iframe = document.createElement('iframe')
  iframe.setAttribute('aria-hidden', 'true')
  iframe.style.cssText = 'position:fixed;right:0;bottom:0;width:0;height:0;border:0'
  document.body.appendChild(iframe)

  const cleanup = () => { if (iframe.parentNode) iframe.remove() }

  try {
    const doc = iframe.contentWindow.document
    doc.open()
    doc.write(html)
    doc.close()
  } catch (e) {
    console.error('printReceipt: failed to write receipt document into iframe', e)
    cleanup()
    return false
  }

  let fired = false
  const fire = () => {
    if (fired) return
    fired = true
    try {
      iframe.contentWindow.focus()
      iframe.contentWindow.print()
    } catch (e) {
      console.error('printReceipt: window.print() failed', e)
    } finally {
      setTimeout(cleanup, 1000)
    }
  }

  // Wait for the iframe document to actually be ready rather than guessing a
  // fixed delay: poll readyState (bounded) and confirm with requestAnimationFrame,
  // with iframe.onload as a second, independent trigger.
  let attempts = 0
  const MAX_ATTEMPTS = 50 // ~1.5s at 30ms/attempt, generous upper bound
  const waitReady = () => {
    attempts++
    let ready = false
    try {
      ready = iframe.contentWindow.document.readyState === 'complete'
    } catch (e) {
      console.error('printReceipt: readiness check failed', e)
      fire()
      return
    }
    if (ready) {
      requestAnimationFrame(fire)
    } else if (attempts >= MAX_ATTEMPTS) {
      console.error('printReceipt: gave up waiting for iframe readiness, printing anyway')
      fire()
    } else {
      setTimeout(waitReady, 30)
    }
  }
  iframe.onload = () => requestAnimationFrame(fire)
  waitReady()

  return true
}
