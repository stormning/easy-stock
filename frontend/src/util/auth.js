import axios from 'axios'
import jwtDecode from 'jwt-decode'
import vm from "@/main.js"

class AuthService {
    login(user) {
        return axios
            .post('/api/auth/signin', {
                username: user.username,
                password: user.password
            })
            .then(response => {
                console.log(response)
                const token = response.accessToken;
                // const user = response.data.user;
                // 在本地存储中保存 Token 和用户信息
                localStorage.setItem('token', token);
                // localStorage.setItem('user', JSON.stringify(user));
                vm.$router.push('/')
            })
    }

    logout() {
        localStorage.removeItem('token');
    }

    getToken() {
        let token = localStorage.getItem('token')
        if (token) {
            try {
                const decodedToken = jwtDecode(token)
                if (decodedToken.exp * 1000 < new Date().getTime()) {
                    localStorage.removeItem('token')
                    return null
                }
                return token
            } catch (e){
                localStorage.removeItem('token')
                return null
            }
        }
        return null
    }
}

export default new AuthService()
