# Role-based login landing (branch `test-auth`)

## Problem

After a staff member signs in, `LoginView.vue` always does
`router.push(route.query.redirect || '/')` — hardcoded to `/` (Doanh Thu
dashboard). For a role that cannot access the dashboard (e.g. `NV` — Nhân viên
bán hàng), the app lands on `/`, the router guard then bounces to the first
allowed screen. This causes:

- a redirect flash through a screen the user is not authorized for, and
- a `?redirect=` value being trusted and pushed even when the role cannot
  access it, again relying on the guard to clean up after the fact.

The login page is also framed as a generic "Đăng nhập" with no distinction that
it is a **staff** sign-in, even though the storefront is fully public and
customers never authenticate.

## Constraints (data model, confirmed)

- Only `nhan_vien` has credentials (`tai_khoan` / `mat_khau`). `khach_hang` has
  **no** login columns (only email / phone). Customers browse the storefront
  without authenticating.
- Roles (`vai_tro` seed): `ADMIN` (Quản trị, `quyen = '*'`), `QL` (Quản lý, all
  screens), `NV` (Nhân viên bán hàng, `dashboard,hoa-don,don-hang,dat-truoc,bao-hanh`).
- Effective per-user permissions come from `nhan_vien_quyen`; the login response
  exposes them as `quyen` (CSV of screen keys, or `*`).

## Decision

UI split, **no** customer authentication. Fix the staff landing to be direct
and correct; give customers a clear link to the public storefront. No backend
or DB changes.

## Changes

### 1. `frontend/src/composables/useAuth.js` — add `landingRoute`

A computed that answers "where does the current user belong?":

- `quyen === '*'` **or** `allowed` includes `dashboard` → `/`
- otherwise → the first entry in `SCREENS` order whose `key` is in `allowed`,
  using its `.to` (e.g. `NV` → `/hoa-don`)
- no allowed screens → `/login` (defensive; should not occur for a valid staff
  account)

Landing logic lives next to the permission data (`allowed`, `SCREENS`) that
already exists here, so the login page and the router guard agree by
construction. Export `landingRoute` from `useAuth`.

### 2. `frontend/src/views/LoginView.vue` — use it + reframe as staff sign-in

- After a successful login, resolve the target:
  - if `route.query.redirect` is present, `router.resolve()` it to a route name
    and honor it **only if** `can(name)` is true; otherwise fall back to
    `landingRoute`.
  - if no redirect → `landingRoute`.
- `demo()` (admin `*`) → `landingRoute` → `/`.
- UI:
  - heading "Đăng nhập nhân viên"
  - a divider + link **"Tiếp tục mua sắm như khách →"** → `/home`
  - keep the demo button and the sample-account hint.

### 3. Router guard — unchanged

`router/index.js` `beforeEach` already redirects to the first allowed screen
when a role hits a screen it cannot access. It stays as defense-in-depth; the
new landing logic simply means the guard no longer has to correct the login
push in the common case.

## Data flow

```
submit → authApi.login() → persist(user{quyen})
       → target = allowedRedirect ?? landingRoute
       → router.push(target)
       → guard re-validates (defense in depth) → screen renders
```

## Error handling

Unchanged: 401 → "Sai tài khoản hoặc mật khẩu"; network/other error → keep the
"dùng Vào demo" hint.

## Testing

- Vitest unit test on the landing derivation:
  - `quyen = '*'` → `/`
  - `quyen = 'dashboard,hoa-don'` (includes dashboard) → `/`
  - `quyen = 'hoa-don,don-hang'` (no dashboard) → `/hoa-don` (first allowed in
    `SCREENS` order)
  - `quyen = ''` → `/login`

  Note: the seeded `NV` role includes `dashboard`, so it lands on `/`. The
  first-allowed-screen branch is what matters for any future/custom role that
  omits `dashboard`; the test covers it directly with `'hoa-don,don-hang'`.
- Confirm the frontend test setup during planning. If no Vitest harness exists,
  note it in the plan rather than scaffolding a full test framework unasked;
  fall back to a documented manual verification checklist (login as `NV`-type
  account lands on `/hoa-don`, as admin lands on `/`, customer link → `/home`).

## Out of scope

- Customer accounts / customer authentication (no credentials in the data
  model; would be its own spec).
- Token/session hardening — the app trusts `localStorage`; unchanged here.
- Any change to the public storefront routes.
