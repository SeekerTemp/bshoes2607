# Role-based Login Landing Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** After a staff member signs in, route them directly to the URL their role is authorized for (no flash through `/`), and give customers a clear link to the public storefront.

**Architecture:** Extract the "which screen keys does this `quyen` allow, and where does it land?" logic into two pure functions in `useAuth.js` (`expandQuyen`, `landingFor`). The `LoginView` uses `landingRoute` (a computed over `landingFor`) instead of hardcoding `/`, and honors `?redirect=` only when the role can access it. The existing router guard is unchanged (defense-in-depth).

**Tech Stack:** Vue 3 + vue-router 4, Vite. Tests via Vitest (added in Task 1 — first FE test harness in this repo; backend already uses JUnit).

**Spec:** `docs/superpowers/specs/2026-07-20-role-based-login-landing-design.md`

**Test accounts (already seeded in `sqlBshoes.sql`, documented in `credential.txt`):**
- `vanan` / `123456` — ADMIN (`*`) → `/`
- `thib` / `123456` — NV with `dashboard` → `/`
- `banhang` / `123456` — NV, no `dashboard` → `/hoa-don`
- `quanly` / `123456` — QL, no `dashboard` → `/san-pham`

---

## File Structure

- `frontend/src/composables/useAuth.js` — **modify**. Add exported pure helpers `expandQuyen(quyen)` and `landingFor(quyen)`; add `landingRoute` computed; reuse `expandQuyen` in the existing `allowed` computed. One responsibility: the auth store + permission/landing derivation.
- `frontend/src/composables/useAuth.landing.test.js` — **create**. Unit tests for the pure helpers (no Vue mount needed — pure functions).
- `frontend/src/views/LoginView.vue` — **modify**. Use `landingRoute` + safe `redirect`; reframe as staff sign-in; add customer storefront link.
- `frontend/package.json` — **modify**. Add `vitest` devDependency + `test` scripts.
- `frontend/vitest.config.js` — **create**. Minimal node-env Vitest config.

---

## Task 1: Add a minimal Vitest harness

**Files:**
- Modify: `frontend/package.json`
- Create: `frontend/vitest.config.js`

- [ ] **Step 1: Add test scripts + devDependency to `frontend/package.json`**

Change the `scripts` and `devDependencies` blocks to:

```json
  "scripts": {
    "dev": "vite",
    "build": "vite build",
    "preview": "vite preview",
    "test": "vitest run",
    "test:watch": "vitest"
  },
  "dependencies": {
    "@zxing/browser": "^0.2.0",
    "axios": "^1.7.9",
    "bootstrap": "^5.3.3",
    "bootstrap-icons": "^1.11.3",
    "chart.js": "^4.5.1",
    "vue": "^3.5.13",
    "vue-router": "^4.5.0"
  },
  "devDependencies": {
    "@vitejs/plugin-vue": "^5.2.1",
    "sass": "^1.83.0",
    "vite": "^6.0.7",
    "vitest": "^2.1.8"
  }
```

- [ ] **Step 2: Create `frontend/vitest.config.js`**

The helpers under test are pure JS (no DOM), so the default `node` environment is enough.

```js
import { defineConfig } from 'vitest/config'

export default defineConfig({
  test: {
    environment: 'node',
    include: ['src/**/*.test.js'],
  },
})
```

- [ ] **Step 3: Install and verify the runner works**

Run (from `frontend/`): `npm install`
Then: `npm test`
Expected: Vitest runs and reports "No test files found" (no tests yet) — exit code 0 or the "no tests" notice. The point is the runner is installed and executes.

> If `npm install` cannot reach the registry (offline), STOP and skip to the "Offline fallback" note at the end of this plan — implement Tasks 2–4 without the Vitest steps and verify via the manual checklist in Task 5.

- [ ] **Step 4: Commit**

```bash
git add frontend/package.json frontend/vitest.config.js frontend/package-lock.json
git commit -m "test(fe): add minimal Vitest harness"
```

---

## Task 2: Pure landing helpers (`expandQuyen`, `landingFor`) — TDD

**Files:**
- Test: `frontend/src/composables/useAuth.landing.test.js`
- Modify: `frontend/src/composables/useAuth.js`

- [ ] **Step 1: Write the failing test**

Create `frontend/src/composables/useAuth.landing.test.js`:

```js
import { describe, it, expect } from 'vitest'
import { expandQuyen, landingFor } from './useAuth'

describe('expandQuyen', () => {
  it('expands "*" to every screen key', () => {
    const keys = expandQuyen('*')
    expect(keys).toContain('dashboard')
    expect(keys).toContain('he-thong')
    expect(keys.length).toBeGreaterThan(5)
  })
  it('splits a CSV, trimming blanks', () => {
    expect(expandQuyen('hoa-don, don-hang ,')).toEqual(['hoa-don', 'don-hang'])
  })
  it('returns [] for empty / null', () => {
    expect(expandQuyen('')).toEqual([])
    expect(expandQuyen(null)).toEqual([])
    expect(expandQuyen(undefined)).toEqual([])
  })
})

describe('landingFor', () => {
  it('sends admin ("*") to /', () => {
    expect(landingFor('*')).toBe('/')
  })
  it('sends a role that includes dashboard to /', () => {
    expect(landingFor('dashboard,hoa-don')).toBe('/')
  })
  it('sends a role WITHOUT dashboard to its first authorized screen', () => {
    // SCREENS order: dashboard, san-pham, hoa-don, don-hang, ...
    expect(landingFor('hoa-don,don-hang')).toBe('/hoa-don')
    expect(landingFor('san-pham,khach-hang,phieu-giam-gia')).toBe('/san-pham')
  })
  it('falls back to /login when nothing is allowed', () => {
    expect(landingFor('')).toBe('/login')
  })
})
```

- [ ] **Step 2: Run the test to verify it fails**

Run (from `frontend/`): `npm test`
Expected: FAIL — `expandQuyen`/`landingFor` are not exported from `./useAuth` (import error / undefined).

- [ ] **Step 3: Add the pure helpers to `frontend/src/composables/useAuth.js`**

Insert these two exported functions immediately after the `persist` function (before `export function useAuth()`):

```js
// Pure: expand a quyen CSV ('*' or 'a,b,c') into an ordered list of allowed screen keys.
export function expandQuyen(quyen) {
  const q = (quyen || '').trim()
  if (q === '*') return SCREENS.map(s => s.key)
  return q.split(',').map(s => s.trim()).filter(Boolean)
}

// Pure: the path this quyen should land on after login.
// includes 'dashboard' (or '*', which expands to include it) -> '/';
// else the first SCREENS entry that is allowed; else '/login'.
export function landingFor(quyen) {
  const keys = expandQuyen(quyen)
  if (!keys.length) return '/login'
  if (keys.includes('dashboard')) return '/'
  const first = SCREENS.find(s => keys.includes(s.key))
  return first ? first.to : '/login'
}
```

- [ ] **Step 4: Run the test to verify it passes**

Run (from `frontend/`): `npm test`
Expected: PASS — all `expandQuyen` and `landingFor` cases green.

- [ ] **Step 5: Commit**

```bash
git add frontend/src/composables/useAuth.js frontend/src/composables/useAuth.landing.test.js
git commit -m "feat(auth): pure expandQuyen/landingFor helpers with tests"
```

---

## Task 3: Wire `landingRoute` + reuse `expandQuyen` in `useAuth`

**Files:**
- Modify: `frontend/src/composables/useAuth.js`

- [ ] **Step 1: Replace the `allowed` computed and add `landingRoute`**

In `export function useAuth()`, replace the existing `allowed` computed:

```js
  const allowed = computed(() => {
    if (!user.value) return []
    const q = (user.value.quyen || '').trim()
    if (q === '*') return SCREENS.map(s => s.key)
    return q.split(',').map(s => s.trim()).filter(Boolean)
  })
  function can(key) { return allowed.value.includes(key) }
```

with (DRY — reuse `expandQuyen`, add `landingRoute`):

```js
  const allowed = computed(() => expandQuyen(user.value?.quyen))
  function can(key) { return allowed.value.includes(key) }
  const landingRoute = computed(() => landingFor(user.value?.quyen))
```

- [ ] **Step 2: Export `landingRoute` from the composable**

Change the return statement:

```js
  return { user, isAuthed, allowed, can, login, loginDemo, logout }
```

to:

```js
  return { user, isAuthed, allowed, can, landingRoute, login, loginDemo, logout }
```

- [ ] **Step 3: Verify existing tests still pass**

Run (from `frontend/`): `npm test`
Expected: PASS — Task 2 tests unaffected (pure helpers unchanged; `allowed` now delegates to `expandQuyen`).

- [ ] **Step 4: Commit**

```bash
git add frontend/src/composables/useAuth.js
git commit -m "feat(auth): expose landingRoute; allowed reuses expandQuyen"
```

---

## Task 4: LoginView — role-based landing + staff reframe + customer link

**Files:**
- Modify: `frontend/src/views/LoginView.vue`

- [ ] **Step 1: Update the `<script setup>` to use `landingRoute` + safe redirect**

Replace the current destructure line:

```js
const { login: doLogin, loginDemo } = useAuth()
```

with:

```js
const { login: doLogin, loginDemo, can, landingRoute } = useAuth()
```

Replace the current `go()`:

```js
function go() { router.push(route.query.redirect || '/') }
```

with (honor `?redirect=` only if the role can access that route; else land by role):

```js
function go() {
  const redirect = route.query.redirect
  if (redirect) {
    // The guard only ever sets redirect to a protected screen route, whose name
    // matches a SCREENS key — so can(name) is the right authorization check.
    const resolved = router.resolve(redirect)
    if (resolved?.name && can(resolved.name)) { router.push(redirect); return }
  }
  router.push(landingRoute.value)
}
```

- [ ] **Step 2: Reframe the template as staff sign-in + add the customer link**

In the `<template>`, change the heading:

```html
      <h4 class="text-center fw-bold mb-4" style="color: var(--c-primary)">Đăng nhập</h4>
```

to:

```html
      <h4 class="text-center fw-bold mb-1" style="color: var(--c-primary)">Đăng nhập nhân viên</h4>
      <p class="text-center text-muted small mb-4">Khu vực quản trị — dành cho nhân viên BShoes</p>
```

Then, immediately after the demo button line:

```html
      <button class="btn btn-outline-secondary w-100 mt-2" @click="demo">Vào demo (Admin)</button>
```

insert a divider + customer link:

```html
      <div class="text-center text-muted small my-3">— hoặc —</div>
      <router-link to="/home" class="btn btn-link w-100 p-0 text-decoration-none">
        <i class="bi bi-bag-heart"></i> Tiếp tục mua sắm như khách →
      </router-link>
```

- [ ] **Step 3: Verify the app builds**

Run (from `frontend/`): `npm run build`
Expected: build succeeds with no errors referencing `LoginView.vue` or `useAuth.js`.

- [ ] **Step 4: Commit**

```bash
git add frontend/src/views/LoginView.vue
git commit -m "feat(login): role-based landing, staff reframe, customer storefront link"
```

---

## Task 5: Manual verification against seeded accounts

**Files:** none (verification only).

- [ ] **Step 1: Start backend + frontend**

Backend (from repo root): `./mvnw spring-boot:run`
Frontend (from `frontend/`): `npm run dev`
Ensure the DB has been seeded with the updated `sqlBshoes.sql` (so `banhang` / `quanly` exist).

- [ ] **Step 2: Verify each landing**

Log in at `/login` with each account and confirm the landing URL:

| Account | Password | Expected landing |
|---------|----------|------------------|
| `vanan` | `123456` | `/` (Doanh Thu) |
| `thib` | `123456` | `/` (has dashboard) |
| `banhang` | `123456` | `/hoa-don` |
| `quanly` | `123456` | `/san-pham` |

Expected: no flash through `/` for `banhang` / `quanly` — they land directly on their first authorized screen.

- [ ] **Step 3: Verify the redirect guard interaction**

While logged out, visit `/hoa-don` directly. Expected: bounced to `/login?redirect=/hoa-don`.
- Log in as `banhang` (can access `hoa-don`) → lands on `/hoa-don` (redirect honored).
- Log out, visit `/nhan-vien` directly (→ `/login?redirect=/nhan-vien`), log in as `banhang` (cannot access `nhan-vien`) → lands on `/hoa-don` (redirect rejected, role landing used).

- [ ] **Step 4: Verify the customer link**

On `/login`, click "Tiếp tục mua sắm như khách" → lands on `/home` (public storefront), no auth required.

- [ ] **Step 5: Final full test run**

Run (from `frontend/`): `npm test`
Expected: PASS — all landing tests green.

---

## Offline fallback (only if Task 1 Step 3 failed to install)

If `npm install` cannot reach the registry, skip every Vitest step (Task 1 entirely, Task 2 Steps 1–2 and 4, Task 3 Step 3, Task 5 Step 5). Still implement the pure helpers (Task 2 Step 3), the wiring (Task 3), and the LoginView changes (Task 4) exactly as written, and rely on the manual checklist in Task 5 (Steps 1–4) for verification. Note the skipped harness in the final report so it can be added when the registry is reachable.
