import axios from 'axios'
import router from "@/util/router";
import qs from 'qs'

// const origin = location.origin
// const apiUrl = origin.indexOf('localhost') > 0 /*|| location.port === '8080'*/? `http://${location.hostname}:8080` : origin

axios.defaults.withCredentials = true

axios.interceptors.request.use(
    config => {
        /*if (config.url.indexOf("http") < 0) {
            config.url = apiUrl + config.url
        }*/
        config.crossDomain = true
        if (!config.hasOwnProperty("autoToast")) {
            config.autoToast = true
        }
        const contentType = config.headers['Content-Type']
        if (contentType === 'application/x-www-form-urlencoded') {
            config.data = qs.stringify(config.data)
        }
        if (!config.hasOwnProperty("indicator") || config.indicator) {
            // Indicator.open()
        }
        return config
    },
    error => {
        // Indicator.close()
        return Promise.reject(error)
    }
)

axios.interceptors.response.use(
    response => {
        console.log(response)
        if (!response.config.hasOwnProperty("indicator") || response.config.indicator) {
            // Indicator.close()
        }
        const code = response.status
        const hasResultWrapper = response.data.hasOwnProperty("success");
        if ((code >= 200 && code < 300) || code === 304) {
            if (hasResultWrapper && !response.data.success && response.config.autoToast) {
                // Toast(response.data.msg)
            }
            return Promise.resolve(response.data)
        } else {
            return Promise.reject(response)
        }
    },
    error => {
        // Indicator.close()
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

export default axios
