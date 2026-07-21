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
