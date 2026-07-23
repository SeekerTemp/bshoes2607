import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import './styles/app.scss'
import { logEvent } from './utils/logger'

const app = createApp(App)

app.config.errorHandler = (err, instance, info) => {
  console.error(err)
  logEvent({
    level: 'error',
    category: 'ui',
    message: String(err?.message || err),
    detail: { info, stack: err?.stack },
  })
}

window.addEventListener('error', (event) => {
  logEvent({
    level: 'error',
    category: 'app',
    message: String(event?.message || 'window error'),
    detail: {
      filename: event?.filename,
      lineno: event?.lineno,
      colno: event?.colno,
      stack: event?.error?.stack,
    },
  })
})

window.addEventListener('unhandledrejection', (event) => {
  const reason = event?.reason
  logEvent({
    level: 'error',
    category: 'app',
    message: String(reason?.message || reason || 'unhandled rejection'),
    detail: { stack: reason?.stack },
  })
})

app.use(router).mount('#app')
