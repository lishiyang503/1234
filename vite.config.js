import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import path from 'path'

export default defineConfig({
  plugins: [vue()],

  resolve: {
    alias: {
      '@': path.resolve(__dirname, 'src')
    }
  },

  server: {
    port: 5173,
    open: true,

    proxy: {

      // ✅ 1️⃣ AI 接口（不 rewrite）
      '/api/ai': {
        target: 'http://localhost:8990',
        changeOrigin: true
        // ❗ 不要 rewrite
      },

      // ✅ 2️⃣ 其他接口（保持原方案）
      '/api': {
        target: 'http://localhost:8990',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api/, '')
      }
    }
  }
})
