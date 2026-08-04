// Pure helpers for storefront category filtering (HomeView.vue).

/**
 * Filter products by category id.
 * @param {Array<{idLoaiSanPham?: number|null}>} products
 * @param {number|null|undefined|0} catId falsy (null/undefined/0) => no filter, return all
 * @returns {Array} filtered products (new array)
 */
export function filterByCategory(products, catId) {
  if (!catId) return products
  return products.filter(p => p.idLoaiSanPham === catId)
}

/**
 * Filter products by a free-text keyword typed in the storefront search box.
 * Matches the product name, the brand, and the màu/size label — the three things
 * the placeholder promises ("Tìm giày, thương hiệu, mã sản phẩm...").
 * Accent- and case-insensitive, so "giay" finds "Giày".
 * @param {Array<{ten?: string, brand?: string, mauSize?: string}>} products
 * @param {string} keyword blank/whitespace => no filter, return all
 * @returns {Array} filtered products
 */
export function filterByKeyword(products, keyword) {
  const k = normalize(keyword)
  if (!k) return products
  return products.filter(p =>
    normalize(p.ten).includes(k) ||
    normalize(p.brand).includes(k) ||
    normalize(p.mauSize).includes(k)
  )
}

// Lowercases and strips Vietnamese diacritics so "giay the thao" matches
// "Giày thể thao". đ/Đ has no combining form, so it is replaced explicitly.
function normalize(value) {
  return String(value ?? '')
    .toLowerCase()
    .normalize('NFD')
    .replace(/\p{Diacritic}/gu, '')
    .replace(/đ/g, 'd')
    .trim()
}
