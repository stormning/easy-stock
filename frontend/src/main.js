import Vue from 'vue'
import ElementUI from 'element-ui';
import 'element-ui/lib/theme-chalk/index.css';
import VueRouter from "vue-router"
import '@/assets/css/core.scss'
import App from './App.vue'
import 'babel-polyfill'
import Es6Promise from 'es6-promise'
import moment from 'moment'
import router from "@/util/router";
import axios from "@/util/axios"
Es6Promise.polyfill()

Vue.use(ElementUI)
Vue.use(VueRouter)
Vue.config.productionTip = false
Vue.prototype.$moment = moment;
Vue.prototype.$http = axios

const props = {}

const vm = new Vue({
    router,
    render: h => h(App, {props: props}),
    data: {
        user: {}
    }
});


Vue.mixin({
    computed: {
        user: {
            get: function () {
                return vm.$data.user
            },
            set: function (user) {
                vm.$data.user = user;
            }
        }
    }
})

vm.$mount('#app');

export default vm;
