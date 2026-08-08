// Money formatter used by every screen that shows a price.
//
// Number(n) is not optional: a numeric STRING (money values arriving as strings
// from JSON) has its own toLocaleString that ignores the locale, so vnd('250000')
// used to render "250000 ₫" with no thousands separators while vnd(250000)
// rendered "250.000 ₫". Coercing first makes both paths agree.
export const vnd = (n) => {
  const v = Number(n)
  return (Number.isFinite(v) ? v : 0).toLocaleString('vi-VN') + ' ₫'
}
