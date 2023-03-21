import AuthService from '@/util/auth'
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
            redirect: '/plans',
            meta: { requiresAuth: true }
        },
        {
            path: '/plans',
            component: () => import('../pages/plans.vue'),
            meta: { requiresAuth: true }
        },
        {
            path: '/plan/:id',
            component: () => import('../pages/plan.vue'),
            meta: { requiresAuth: true }
        },
        {
            path: '/plates',
            component: () => import('../pages/plates.vue'),
            meta: { requiresAuth: true }
        },
        {
            path: '/login',
            component: () => import('../pages/login.vue'),
            meta: { requiresAuth: false }
        },
        {
            path: '/register',
            component: () => import('../pages/register.vue'),
            meta: { requiresAuth: false }
        },
        {
            path: '/tasks',
            component: () => import('../pages/tasks.vue'),
            meta: { requiresAuth: true }
        },
        {
            path: '/scheduledTasks',
            component: () => import('../pages/scheduledTasks.vue'),
            meta: { requiresAuth: true }
        }
    ]
});

router.beforeEach((to, from, next) => {
    const authRequired = to.matched.some(record => record.meta.requiresAuth)
    const loggedIn = AuthService.getToken()

    if (authRequired && !loggedIn) {
        return next('/login')
    }
    next()
});
export default router
