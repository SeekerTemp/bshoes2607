// In-memory localStorage for tests (environment is 'node', which has none).
//
// This used to be a no-op stub whose getItem() always returned null. That made
// anything persisted through localStorage — the cart, the auth session, the log
// ring buffer — effectively untestable: a test could write a value and never
// read it back, so round-trip bugs stayed invisible. A real implementation costs
// nothing and lets those paths be covered.
class MemoryStorage {
  constructor() {
    this.store = new Map()
  }

  getItem(key) {
    const k = String(key)
    return this.store.has(k) ? this.store.get(k) : null
  }

  setItem(key, value) {
    this.store.set(String(key), String(value))
  }

  removeItem(key) {
    this.store.delete(String(key))
  }

  clear() {
    this.store.clear()
  }

  key(index) {
    return [...this.store.keys()][index] ?? null
  }

  get length() {
    return this.store.size
  }
}

global.localStorage = new MemoryStorage()
