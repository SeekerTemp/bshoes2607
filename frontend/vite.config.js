import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

// Where the Spring Boot API lives. Defaults to localhost so a fresh clone just
// works; override on another machine (API on a different host/port) with:
//   VITE_API_TARGET=http://192.168.1.50:8085 npm run dev
const apiTarget = process.env.VITE_API_TARGET || 'http://localhost:8085'
const proxy = { '/api': { target: apiTarget, changeOrigin: true } }

export default defineConfig({
  plugins: [vue()],
  server: { port: 5173, proxy },
  // `npm run preview` serves the production build — it needs the SAME proxy or
  // every /api call (including the log shipping to /api/logs/client) 404s.
  preview: { port: 4173, proxy },
})
