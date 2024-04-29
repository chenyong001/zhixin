import path from "path"
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

// const url = 'https://ykai.itjcloud.com';
const localUrl='http://127.0.0.1:8015'
// const localUrl='http://192.168.90.124:8015'

export default defineConfig({
    plugins: [vue()],
    resolve: {
        alias: {
            '@': path.resolve(__dirname, 'src'),
        },
    },
    esbuild: {
        jsxFactory: 'h',
        jsxFragment: 'Fragment',
        jsxInject: "import { h } from 'vue';"
    },
    // 全局样式
    css: {
        preprocessorOptions: {
            scss: {
                additionalData: `@use "@/styles/element/index.scss" as *;`,
            },
        },
        // postcss:{}
    },
    // 反向代理
    server: {
        headers: {
            'Access-Control-Allow-Origin': '*',
        },
        disableHostCheck: true,
        port: 8006,
        host:'0.0.0.0',
        proxy: {
            '/zx': {
                target: localUrl,//url,testUrl,localUrl
                changeOrigin: true,
                pathRewrite: {
                    '^/zx': '/zx'
                }
            }
        },
        hmr:{
            overlay:true
        }
    }

})
