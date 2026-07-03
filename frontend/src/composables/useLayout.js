// Shared app-shell UI state (module-level singleton so the header toggle and the
// sidebar stay in sync).
import { ref } from 'vue'

const sidebarCollapsed = ref(false)

export function useLayout() {
  const toggleSidebar = () => { sidebarCollapsed.value = !sidebarCollapsed.value }
  return { sidebarCollapsed, toggleSidebar }
}
