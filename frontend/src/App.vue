<template>
  <div id="app" class="app">
    <el-menu default-active="plans" :router="true" mode="horizontal" v-if="isLoggedIn()">
      <el-menu-item index="2-1">我的看板</el-menu-item>
      <el-menu-item index="/plans">个股计划</el-menu-item>
      <el-menu-item index="/todos">今日代办</el-menu-item>
      <el-menu-item index="/plates">策略管理</el-menu-item>
      <el-menu-item index="/plates">板块机会</el-menu-item>
      <el-menu-item class="logout">
        <el-button type="text" @click="handleLogout">登出</el-button>
      </el-menu-item>
    </el-menu>
    <el-container>
      <el-main>
        <router-view v-if="isRouterAlive"></router-view>
      </el-main>
    </el-container>
  </div>
</template>
<style scoped>
.logout {
  float: right ;
}
</style>
<script>
import AuthService from "@/util/auth";

export default {
  name: 'App',
  components: {},
  data() {
    return {
      isRouterAlive: true,
      activeIndex: '1',
    }
  },
  provide() {
    return {
      reload: this.reload
    }
  },
  methods: {
    reload() {
      this.isRouterAlive = false;
      this.$nextTick(function () {
        this.isRouterAlive = true;
      })
    },
    handleSelect(key, keyPath) {
      console.log(key, keyPath);
    },
    jumpTo(url) {
      console.log(this)
      this.router.push({path: url})
    },
    handleLogout() {
      // 调用登出接口
      this.$http.post('/api/auth/logout').then(() => {
        AuthService.logout()
        // 跳转到登录页
        this.$router.push('/login');
      });
    },
    isLoggedIn() {
      return AuthService.getToken()
    }
  },
  mounted() {
    const that = this;
  },
}
</script>
