import path from "path"
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

// const url = 'https://ykai.itjcloud.com';
const localUrl='https://www.opencast.site'
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
            '/m2c': {
                target: localUrl,//url,testUrl,localUrl
                changeOrigin: true,
                pathRewrite: {
                    '^/m2c': '/m2c'
                }
            }
        },
        hmr:{
            overlay:true
        }
    }

})
