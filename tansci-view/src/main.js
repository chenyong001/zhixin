import { createApp } from 'vue'
import App from './App.vue'
import store from './store'
import router from './router'
import "tailwindcss/tailwind.css";

import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import zhCn from 'element-plus/es/locale/lang/zh-cn'
import * as ElIcons from '@element-plus/icons-vue'
import "./assets/css/base.css";
import './styles/index.scss'
import ECharts from 'vue-echarts';
import TjDesign from 'tj-design-vue';

// 引入组件库的少量全局样式变量
import 'tj-design-vue/lib/index.css';

const app = createApp(App)
app.use(store)
app.use(router)
app.use(ElementPlus, {
    locale: zhCn,
    size: "default"
})
app.use(TjDesign);

// 统一导入el-icon图标
for(let icon in ElIcons){
    app.component(icon,ElIcons[icon])
}
app.component('VChart', ECharts);

app.mount('#app')



  
  
