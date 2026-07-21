// Pure validator for locally-uploaded images (product variant image on SanPhamView).
// Kept dependency-free so it's trivial to unit test with fake File-like objects.

const ALLOWED_TYPES = ['image/jpeg', 'image/png']
const MAX_SIZE_BYTES = 1_048_576 // 1MB

/**
 * @param {{ type?: string, size?: number } | null | undefined} file
 * @returns {{ ok: boolean, error: string }}
 */
export function validateImageFile(file) {
  if (!file) return { ok: false, error: 'Chưa chọn tệp ảnh' }
  if (!ALLOWED_TYPES.includes(file.type)) return { ok: false, error: 'Chỉ chấp nhận ảnh JPG/PNG' }
  if (!(file.size < MAX_SIZE_BYTES)) return { ok: false, error: 'Ảnh phải nhỏ hơn 1MB' }
  return { ok: true, error: '' }
}
