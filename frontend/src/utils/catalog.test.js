import { describe, it, expect } from 'vitest'
import { filterByCategory, filterByKeyword } from './catalog'

describe('filterByKeyword', () => {
  const products = [
    { ten: 'Giày thể thao Nike', brand: 'Nike', mauSize: 'Đen / 42' },
    { ten: 'Giày da Adidas', brand: 'Adidas', mauSize: 'Nâu / 40' },
    { ten: 'Sandal Puma', brand: 'Puma', mauSize: 'Trắng / 41' },
  ]

  it('returns everything for a blank or whitespace keyword', () => {
    expect(filterByKeyword(products, '')).toHaveLength(3)
    expect(filterByKeyword(products, '   ')).toHaveLength(3)
    expect(filterByKeyword(products, null)).toHaveLength(3)
  })

  it('matches the product name, case-insensitively', () => {
    expect(filterByKeyword(products, 'sandal')).toEqual([products[2]])
  })

  it('matches the brand', () => {
    expect(filterByKeyword(products, 'adidas')).toEqual([products[1]])
  })

  it('matches the màu/size label', () => {
    expect(filterByKeyword(products, '42')).toEqual([products[0]])
  })

  it('ignores Vietnamese diacritics both ways', () => {
    expect(filterByKeyword(products, 'giay')).toHaveLength(2)
    expect(filterByKeyword(products, 'Giày')).toHaveLength(2)
    expect(filterByKeyword(products, 'den')).toEqual([products[0]])
  })

  it('returns [] when nothing matches', () => {
    expect(filterByKeyword(products, 'zzz')).toEqual([])
  })
})

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
