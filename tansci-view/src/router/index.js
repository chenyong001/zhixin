import { createRouter, createWebHistory} from 'vue-router'
import NProgress from 'nprogress'
import 'nprogress/nprogress.css'

import Layout from '@/layout/Layout.vue'
import {menuList} from '@/api/systemApi'
import {useMenuStore} from '@/store/settings'
import {isMobile} from '../utils/utils'
import Cookies from 'js-cookie'

// 路由按模块分类
import common from './common'

// createRouter 创建路由实例
const router = createRouter({
    /**
     * hash模式：createWebHashHistory，
     * history模式：createWebHistory
     */
    scrollBehavior (to, from, savedPosition) {
        if (savedPosition) {
          return savedPosition
        } else {
          return {
            x: 0,
            y: 0
          }
        }
    },
    history: createWebHistory(),
    routes:[
        ...common
    ]
})
if(!isMobile()){
    NProgress.inc(0.2)
}

// NProgress.configure({ easing: 'ease', speed: 200, showSpinner: true })

let flag = true // 刷新标识
router.beforeEach(async (to, from, next) => {
    // 启动进度条
    if(!isMobile()){
        NProgress.start()
    }
    // debugger
    if(to.query&&to.query.token){
        localStorage.setItem('token',to.query.token)
    }
    // 是否登陆
    if (!localStorage.getItem('token') && to.path !== "/login") {
        return next({ path: "/login" });
    };

    // 设置头部
    if (to.meta.title) {
        document.title = to.meta.title
    } else {
        document.title = "智心库"
    }

    // 动态添加路由
    if(localStorage.getItem('token') && flag){
        const menuStore = useMenuStore();
        await  menuList({types:'1,2,3', status: 1}).then((res)=>{
            let result = routerFilter(res.result)
            result.push({path:'/:pathMatch(.*)*', redirect:'/404'})
            result.forEach((item) => {
                router.addRoute(item)
            })
            menuStore.setMenu([...result])
            flag = false
            next({ ...to, replace: true })
        }
        ,error=>{
            // token 过期
            localStorage.removeItem("token")
            Cookies.remove("token")
            // localStorage.setItem("token","")
            // localStorage.clear();
            router.push({path: 'login'})
            console.log("token 过期,error="+error)
            // next({ ...to, replace: true })
            // next()
        }
        )
    } else{
        next()
    }
})

router.afterEach(() => {
    if(!isMobile()){
        // 关闭进度条
        NProgress.done()
    }
})


// 格式化路由
let modules = import.meta.glob('../views/**/**/*.vue')
export function routerFilter(data) {
    data.forEach((item)=>{
        let flag = false;
        if(item.parentId == '0'){
            item.path = '/' + item.name;
            item.name = item.name;
            item.chineseName = item.chineseName;
            item.englishName = item.englishName;
            item.icon = item.icon;
            item.meta = { title: item.chineseName };
            item.redirect = item.name;
            item.component = Layout;
            if(!item.children || item.children.length == 0){
                item.children = [{
                    path: '/' + item.name,
                    name: item.name,
                    icon: item.icon,
                    chineseName: item.chineseName,
                    englishName: item.englishName,
                    meta: { title: item.chineseName,  keepAlive: true },
                    // component: () => import('../views' + item.url + '.vue')
                    component: modules['../views' + item.url + '.vue']
                }];
                flag = true;
            }
        } else {
            item.path = '/' + item.name;
            item.name = item.name;
            item.chineseName = item.chineseName;
            item.englishName = item.englishName;
            item.icon = item.icon;
            item.meta = { title: item.chineseName ,  keepAlive: true};
            // item.component = () => import('../views' + item.url + '.vue');
            item.component = modules['../views' + item.url + '.vue'];
        }
        if(item.children && item.children.length && !flag){
            routerFilter(item.children)
        }
    })

    return data;
}

export default router