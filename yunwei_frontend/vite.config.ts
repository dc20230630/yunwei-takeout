import { fileURLToPath, URL } from 'node:url'
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import tailwindcss from '@tailwindcss/vite'

// https://vite.dev/config/
export default defineConfig({
  plugins: [vue(), tailwindcss()],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    }
  },

  server:{
    proxy:{
      '/api':{
        target:'http://localhost:8080',//后端Spring Boot地址
        changeOrigin:true,//代理时修改请求来源
        rewrite:(path)=>path.replace(/^\/api/,''),//转发时去掉 /api
      }
    }
  }
})

