import { describe, it, expect, vi } from 'vitest'
import { useCrud, crudErrorMessage } from './useCrud'

// useCrud() calls onMounted(load) internally; onMounted is a no-op outside a
// component setup context (Vue just logs a warning), so `load()` is never
// triggered automatically here — each test drives load/add/update/remove
// directly against a fake api.

function fakeApi(overrides = {}) {
  return {
    findAll: vi.fn().mockResolvedValue([]),
    create: vi.fn().mockResolvedValue({}),
    update: vi.fn().mockResolvedValue({}),
    remove: vi.fn().mockResolvedValue({}),
    ...overrides,
  }
}

describe('useCrud write contract', () => {
  it('add() resolves and reloads rows when api.create succeeds', async () => {
    const rowsAfterCreate = [{ id: 1, ten: 'A' }]
    const api = fakeApi({
      create: vi.fn().mockResolvedValue({}),
      findAll: vi.fn().mockResolvedValue(rowsAfterCreate),
    })
    const { rows, add } = useCrud(api, [])

    await expect(add({ ten: 'A' })).resolves.toBeUndefined()

    expect(api.create).toHaveBeenCalledTimes(1)
    expect(api.findAll).toHaveBeenCalledTimes(1)
    expect(rows.value).toEqual(rowsAfterCreate)
  })

  it('add() rejects when api.create fails and does not mutate rows locally', async () => {
    const api = fakeApi({
      create: vi.fn().mockRejectedValue(new Error('network down')),
    })
    const { rows, add } = useCrud(api, [])

    await expect(add({ ten: 'A' })).rejects.toThrow('network down')

    expect(rows.value).toEqual([])
    expect(api.findAll).not.toHaveBeenCalled()
  })

  it('update() resolves and reloads rows when api.update succeeds', async () => {
    const rowsAfterUpdate = [{ id: 1, ten: 'B updated' }]
    const api = fakeApi({
      update: vi.fn().mockResolvedValue({}),
      findAll: vi.fn().mockResolvedValue(rowsAfterUpdate),
    })
    const { rows, update } = useCrud(api, [])

    await expect(update({ id: 1, ten: 'B updated' })).resolves.toBeUndefined()

    expect(api.update).toHaveBeenCalledTimes(1)
    expect(rows.value).toEqual(rowsAfterUpdate)
  })

  it('update() rejects when api.update fails and does not mutate rows locally', async () => {
    const api = fakeApi({
      update: vi.fn().mockRejectedValue(new Error('server error')),
    })
    const seedRow = { id: 1, ten: 'Original' }
    const { rows, update } = useCrud(api, [])
    rows.value = [seedRow]

    await expect(update({ id: 1, ten: 'Changed' })).rejects.toThrow('server error')

    expect(rows.value).toEqual([seedRow])
    expect(api.findAll).not.toHaveBeenCalled()
  })

  it('remove() resolves and reloads rows when api.remove succeeds', async () => {
    const api = fakeApi({
      remove: vi.fn().mockResolvedValue({}),
      findAll: vi.fn().mockResolvedValue([]),
    })
    const { rows, remove } = useCrud(api, [])
    rows.value = [{ id: 1, ten: 'A' }]

    await expect(remove(1)).resolves.toBeUndefined()

    expect(api.remove).toHaveBeenCalledWith(1)
    expect(rows.value).toEqual([])
  })

  it('remove() rejects when api.remove fails and does not mutate rows locally', async () => {
    const api = fakeApi({
      remove: vi.fn().mockRejectedValue(new Error('cannot delete')),
    })
    const seedRow = { id: 1, ten: 'A' }
    const { rows, remove } = useCrud(api, [])
    rows.value = [seedRow]

    await expect(remove(1)).rejects.toThrow('cannot delete')

    expect(rows.value).toEqual([seedRow])
    expect(api.findAll).not.toHaveBeenCalled()
  })

  it('load() falls back to the mock seed when api.findAll fails (read-only offline display)', async () => {
    const seed = [{ id: 1, ten: 'Mock' }]
    const api = fakeApi({ findAll: vi.fn().mockRejectedValue(new Error('offline')) })
    const { rows, load } = useCrud(api, seed)

    await load()

    expect(rows.value).toEqual(seed)
  })
})

describe('crudErrorMessage', () => {
  it('returns the backend-provided response message when present', () => {
    const err = { response: { data: { message: 'Mã đã tồn tại' } }, message: 'Request failed with status code 400' }
    expect(crudErrorMessage(err)).toBe('Mã đã tồn tại')
  })

  it('falls back to err.message when no response message is present', () => {
    const err = new Error('Network Error')
    expect(crudErrorMessage(err)).toBe('Network Error')
  })

  it('falls back to a default message when neither is present', () => {
    expect(crudErrorMessage({})).toBe('Lỗi không xác định')
    expect(crudErrorMessage(undefined)).toBe('Lỗi không xác định')
  })
})
