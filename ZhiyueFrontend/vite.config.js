import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

export default defineConfig({
  plugins: [vue()],
  server: {
    port: 3000,
    host: '0.0.0.0',
    allowedHosts: true,
    proxy: {
      // 统一代理配置：所有 /api 开头的请求都转发到后端
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        secure: false,
        rewrite: (path) => path.replace(/^\/api/, '')
      },
      // 图片路径代理（不需要 /api 前缀）
      '/image': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        secure: false
      },
      // 验证码相关接口代理（本地后端）
      '/capture': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        secure: false
      },
      // 用户相关接口代理（扫码登录等）
      '/user': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        secure: false
      },
      // 评论相关接口代理
      '/comment': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        secure: false
      },
      // WebSocket 聊天接口代理
      '/chat': {
        target: 'ws://localhost:8080',
        changeOrigin: true,
        secure: false,
        ws: true
      }
    }
  }
})
