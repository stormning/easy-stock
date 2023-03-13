import axios from 'axios'
import router from "@/util/router";
import qs from 'qs'
import { Loading } from 'element-ui';


let loadingInstance
let loadingCount = 0

function startLoading() {
    if (loadingCount === 0) {
        loadingInstance = Loading.service({
            lock: true,
            text: 'Loading...',
            background: 'rgba(0, 0, 0, 0.7)'
        })
    }
    loadingCount++
}

function endLoading() {
    if (loadingCount <= 0) {
        return
    }
    loadingCount--
    if (loadingCount === 0) {
        loadingInstance.close()
    }
}

// const origin = location.origin
// const apiUrl = origin.indexOf('localhost') > 0 /*|| location.port === '8080'*/? `http://${location.hostname}:8080` : origin

axios.defaults.withCredentials = true

axios.interceptors.request.use(
    config => {
        /*if (config.url.indexOf("http") < 0) {
            config.url = apiUrl + config.url
        }*/
        const token = localStorage.getItem('token')
        if (token) {
            config.headers.Authorization = `Bearer ${token}`
        }
        config.crossDomain = true
        if (!config.hasOwnProperty("autoToast")) {
            config.autoToast = true
        }
        const singleParam = config.data && Object.keys(config.data).length === 1;
        if (singleParam) {
            config.headers['Content-Type'] = 'application/x-www-form-urlencoded'
        }
        const contentType = config.headers['Content-Type']
        if (contentType === 'application/x-www-form-urlencoded') {
            config.data = qs.stringify(config.data)
        }
        if (!config.hasOwnProperty("indicator") || config.indicator) {
            startLoading()
        }
        return config
    },
    error => {
        endLoading()
        return Promise.reject(error)
    }
)

const init = function (vm) {
    axios.interceptors.response.use(
        response => {
            if (!response.config.hasOwnProperty("indicator") || response.config.indicator) {
                endLoading()
            }
            const code = response.status
            const hasResultWrapper = response.data.hasOwnProperty("success");
            if ((code >= 200 && code < 300) || code === 304) {
                if (hasResultWrapper && !response.data.success && response.config.autoToast) {
                    vm.$message({
                        message: response.data.msg,
                        type: 'error'
                    });
                }
                return Promise.resolve(response.data)
            } else {
                return Promise.reject(response)
            }
        },
        error => {
            endLoading()
            if (error.response) {
                switch (error.response.status) {
                    case 401:
                        // 返回401 清除token信息并跳转到登陆页面
                        router.push({
                            path: '/login'
                        })
                        break
                    case 404:
                        // Toast("网络请求不存在")
                        break
                    default:
                    // Toast(error.response.data.message)
                }
            } else {
                // 请求超时或者网络有问题
                if (error.message.includes('timeout')) {
                    // Toast('请求超时')
                } else {
                    // Toast('网络异常')
                }
            }
            return Promise.reject(error)
        }
    )

    return axios
}

export default init