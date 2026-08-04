import { describe, it, expect, beforeEach, vi } from 'vitest'

// useCart holds a module-level singleton cart seeded from localStorage at import
// time, so each test resets storage and re-imports the module.
async function freshCart(seed) {
  localStorage.clear()
  if (seed !== undefined) localStorage.setItem('bshoes_cart', seed)
  vi.resetModules()
  const mod = await import('./useCart')
  return mod
}

const SP = { id: 1, ten: 'Giày Nike', gia: 500000, mauSize: 'Đen / 42', ton: 3 }

describe('readCart — corrupted storage must not kill the app', () => {
  it('returns [] for invalid JSON instead of throwing at import time', async () => {
    const { useCart } = await freshCart('{not json')
    expect(useCart().items.value).toEqual([])
  })

  it('returns [] when the stored value is not an array', async () => {
    const { useCart } = await freshCart('{"a":1}')
    expect(useCart().items.value).toEqual([])
  })

  it('drops rows with no id or a non-numeric quantity', async () => {
    const { useCart } = await freshCart(JSON.stringify([
      { id: 1, soLuong: 2, gia: 100 },
      { soLuong: 5, gia: 100 },
      { id: 3, soLuong: 'nhiều', gia: 100 },
      { id: 4, soLuong: 0, gia: 100 },
    ]))
    expect(useCart().items.value.map(i => i.id)).toEqual([1])
  })

  it('keeps a valid stored cart across a reload', async () => {
    const { useCart } = await freshCart(JSON.stringify([{ id: 1, ten: 'X', gia: 100, soLuong: 2 }]))
    expect(useCart().soLuong.value).toBe(2)
  })
})

describe('add', () => {
  it('adds a new line and persists it', async () => {
    const { useCart } = await freshCart()
    const { add, items } = useCart()

    expect(add(SP)).toEqual({ ok: true })
    expect(items.value).toHaveLength(1)
    expect(JSON.parse(localStorage.getItem('bshoes_cart'))).toHaveLength(1)
  })

  it('merges into the existing line instead of duplicating it', async () => {
    const { useCart } = await freshCart()
    const { add, items } = useCart()

    add(SP)
    add(SP)

    expect(items.value).toHaveLength(1)
    expect(items.value[0].soLuong).toBe(2)
  })

  it('refuses to exceed stock, counting what is already in the cart', async () => {
    const { useCart } = await freshCart()
    const { add, items } = useCart()

    add(SP, 2)
    const r = add(SP, 2)

    expect(r.ok).toBe(false)
    expect(r.message).toContain('3')
    expect(items.value[0].soLuong).toBe(2)
  })

  it('refuses a sold-out product', async () => {
    const { useCart } = await freshCart()
    const { add, items } = useCart()

    expect(add({ ...SP, ton: 0 }).ok).toBe(false)
    expect(items.value).toHaveLength(0)
  })

  it('refuses a zero or negative quantity', async () => {
    const { useCart } = await freshCart()
    const { add, items } = useCart()

    expect(add(SP, 0).ok).toBe(false)
    expect(add(SP, -3).ok).toBe(false)
    expect(items.value).toHaveLength(0)
  })

  it('refuses a product with no id', async () => {
    const { useCart } = await freshCart()
    const { add, items } = useCart()

    expect(add({ ten: 'Không id', gia: 1 }).ok).toBe(false)
    expect(items.value).toHaveLength(0)
  })

  it('allows unlimited quantity when the product carries no stock figure', async () => {
    const { useCart } = await freshCart()
    const { add, items } = useCart()

    expect(add({ id: 9, ten: 'X', gia: 100 }, 99).ok).toBe(true)
    expect(items.value[0].soLuong).toBe(99)
  })

  it('refreshes the stock snapshot on an existing line after a restock', async () => {
    const { useCart } = await freshCart()
    const { add, setQty } = useCart()

    add(SP)                          // ton = 3
    add({ ...SP, ton: 10 })          // restocked
    expect(setQty(1, 8).ok).toBe(true)
  })
})

describe('setQty', () => {
  it('sets an exact quantity', async () => {
    const { useCart } = await freshCart()
    const { add, setQty, items } = useCart()

    add(SP)
    expect(setQty(1, 3)).toEqual({ ok: true })
    expect(items.value[0].soLuong).toBe(3)
  })

  it('removes the line when the quantity drops to zero or below', async () => {
    const { useCart } = await freshCart()
    const { add, setQty, items } = useCart()

    add(SP)
    setQty(1, 0)
    expect(items.value).toHaveLength(0)
  })

  it('refuses to exceed stock and leaves the quantity untouched', async () => {
    const { useCart } = await freshCart()
    const { add, setQty, items } = useCart()

    add(SP)
    const r = setQty(1, 99)

    expect(r.ok).toBe(false)
    expect(items.value[0].soLuong).toBe(1)
  })

  it('reports a miss for an unknown line', async () => {
    const { useCart } = await freshCart()
    expect(useCart().setQty(404, 1).ok).toBe(false)
  })
})

describe('remove / clear', () => {
  it('removes one line and keeps the rest', async () => {
    const { useCart } = await freshCart()
    const { add, remove, items } = useCart()

    add(SP)
    add({ id: 2, ten: 'Adidas', gia: 700000, ton: 5 })
    remove(1)

    expect(items.value.map(i => i.id)).toEqual([2])
  })

  it('empties the cart and the persisted copy', async () => {
    const { useCart } = await freshCart()
    const { add, clear, items } = useCart()

    add(SP)
    clear()

    expect(items.value).toEqual([])
    expect(JSON.parse(localStorage.getItem('bshoes_cart'))).toEqual([])
  })
})

describe('totals', () => {
  it('sums quantity and money across lines', async () => {
    const { useCart } = await freshCart()
    const { add, soLuong, tongTien } = useCart()

    add(SP, 2)                                              // 2 x 500000
    add({ id: 2, ten: 'Adidas', gia: 700000, ton: 5 })      // 1 x 700000

    expect(soLuong.value).toBe(3)
    expect(tongTien.value).toBe(1700000)
  })

  it('never produces NaN from a row with a missing price', async () => {
    const { useCart } = await freshCart(JSON.stringify([{ id: 1, ten: 'X', soLuong: 2 }]))
    const { tongTien, soLuong } = useCart()

    expect(tongTien.value).toBe(0)
    expect(soLuong.value).toBe(2)
  })
})
