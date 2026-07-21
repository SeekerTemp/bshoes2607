import { describe, it, expect } from 'vitest'
import { filterByCategory } from './catalog'

const products = [
  { id: 1, ten: 'Giay A', idLoaiSanPham: 1 },
  { id: 2, ten: 'Giay B', idLoaiSanPham: 2 },
  { id: 3, ten: 'Giay C', idLoaiSanPham: 1 },
  { id: 4, ten: 'Giay D', idLoaiSanPham: null },
]

describe('filterByCategory', () => {
  it('returns all products when catId is falsy (null/undefined/0)', () => {
    expect(filterByCategory(products, null)).toEqual(products)
    expect(filterByCategory(products, undefined)).toEqual(products)
    expect(filterByCategory(products, 0)).toEqual(products)
  })

  it('returns only products matching the given category id', () => {
    const result = filterByCategory(products, 1)
    expect(result).toEqual([
      { id: 1, ten: 'Giay A', idLoaiSanPham: 1 },
      { id: 3, ten: 'Giay C', idLoaiSanPham: 1 },
    ])
  })

  it('returns an empty array when no product matches the category id', () => {
    expect(filterByCategory(products, 999)).toEqual([])
  })
})
