<script setup>
// Shown whenever a screen has fallen back to local mock data because the API
// call failed. Without it a reader cannot tell a working screen from a broken
// one: the fallback paints a full, plausible-looking table either way.
//
// Deliberately loud (red, sticky at the top of the content area) — a subtle
// hint here would defeat the whole point.
defineProps({
  // What the screen is showing instead of real data, e.g. 'danh sách nhân viên'.
  what: { type: String, default: 'dữ liệu' },
})
defineEmits(['retry'])
</script>

<template>
  <div class="demo-banner" role="alert">
    <i class="bi bi-exclamation-octagon-fill"></i>
    <div class="demo-banner-text">
      <b>ĐANG HIỂN THỊ DỮ LIỆU DEMO</b> — không gọi được máy chủ, {{ what }} bên dưới
      <b>không phải dữ liệu thật</b>. Mọi thao tác lưu / xoá sẽ thất bại.
    </div>
    <button class="btn btn-sm btn-outline-light text-nowrap" @click="$emit('retry')">
      <i class="bi bi-arrow-clockwise"></i> Thử lại
    </button>
  </div>
</template>

<style scoped>
.demo-banner {
  display: flex;
  align-items: center;
  gap: .75rem;
  background: #b02a37;
  color: #fff;
  padding: .6rem .9rem;
  border-radius: .5rem;
  margin-bottom: .75rem;
  font-size: .9rem;
}
.demo-banner > .bi {
  font-size: 1.25rem;
  flex: 0 0 auto;
}
.demo-banner-text { flex: 1 1 auto; }
</style>
