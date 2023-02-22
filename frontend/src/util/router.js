import sec from '@/util/sec'
import Vue from 'vue'
import VueRouter from "vue-router"

Vue.use(VueRouter)
const originalPush = VueRouter.prototype.push

VueRouter.prototype.push = function push(location) {
    return originalPush.call(this, location).catch(err => err)
}

const router = new VueRouter({
    routes: [
        {
            path: '/',
            redirect: '/stocks'
        },
        {
            path: '/stocks',
            component: () => import('../pages/stocks.vue')
        },
        {
            path: '/stock',
            component: () => import('../pages/stock.vue'),
        },
        {
            path: '/plates',
            component: () => import('../pages/plates.vue'),
        }
    ]
});

router.beforeEach((to, from, next) => {
    if (sec.isLogin()) { // 判断是否登录
        if (to.path === '/login') {  //
            //登录状态下 访问login.vue页面 会跳到index.vue
            next({path: '/index'})
        } else {
            next()
        }
    } else {
        if (to.path === '/login' || to.path === '/logout') {
            next()
        } else {
            /*sec.initUserInfo(function (){
                next({path: '/login'})
            })*/
            next({path: '/login'})
        }
    }
});
export default router
