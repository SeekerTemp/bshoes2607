import { reactive } from 'vue'

// Module-level reactive state so any component can push/read toasts.
const toasts = reactive([])
let seq = 0

function notify(message, type = 'success') {
  const id = ++seq
  toasts.push({ id, message, type })
  setTimeout(() => {
    const i = toasts.findIndex(t => t.id === id)
    if (i !== -1) toasts.splice(i, 1)
  }, 3000)
}

function remove(id) {
  const i = toasts.findIndex(t => t.id === id)
  if (i !== -1) toasts.splice(i, 1)
}

export function useToast() {
  return { toasts, notify, remove }
}
