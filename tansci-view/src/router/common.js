export default[
    {
        path: '/',
        redirect: '/login'
    },
    {
        path: '/login',
        name: 'login',
        meta: {title: "登录"},
        component: () => import('@/views/common/Login.vue')
    },
    {
        path: '/404',
        name: '404',
        meta: {title: "404"},
        component: () => import('@/views/common/404.vue')
    },
    {
        path: '/500',
        name: '500',
        meta: {title: "500"},
        component: () => import('@/views/common/500.vue')
    },
    {
        path: '/main',
        name: 'main',
        meta: { title: "首页" },
        component: () => import('@/layout/Layout.vue'),
        children: [{
            path: "/main",
            name: "main",
            meta: { title: "首页" },
            component: () => import('@/views/common/Main.vue')
        }]
    },
    // {
    //     path: '/list',
    //     name: 'List',
    //     meta: { title: '审核列表', menu: true, breadcrumb: false, icon: 'menu_home' },
    //     component: () => import('@/layout/Layout.vue'),
    //     children: [
    //       {
    //         path: '/list',
    //         name: 'List',
    //         component: () => import( '@/views/List/index.vue'),
    //         meta: { title: '列表', menu: false, breadcrumb: false, activeMenu: '/home' },
    //       },
    //       {
    //         path: 'details',
    //         name: 'Details',
    //         component: () =>
    //           import( '@/views/List/Details/index.vue'),
    //         meta: { title: '审核详情', menu: false, breadcrumb: true, activeMenu: '/home' },
    //       },
    //     ],
    // },
    // {
    //     path: '/search',
    //     name: 'Search',
    //     component: () => import('@/layout/Layout.vue'),
    //     meta: { title: '检索', menu: true, breadcrumb: false, icon: 'menu_home' },
    //     children: [{
    //         path: '/search',
    //         name: 'Search',
    //         meta: { title: '检索', menu: true, breadcrumb: false, icon: 'menu_home' },
    //         component: () => import( '@/views/Search/index.vue'),
    //     }]
    // },
    // {
    //     path: '/record/recordData',
    //     name: 'recordData',
    //     meta: {title: "详情"},
    //     component: () => import('@/views/collect/RecordData.vue')
    // },
    // {
    //     path: '/note/noteDetail',
    //     name: 'noteDetail',
    //     meta: {title: "创建"},
    //     component: () => import('@/views/collect/NoteDetail.vue')
    // },

 
]