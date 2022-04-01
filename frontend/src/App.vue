<template>
  <div id="app" class="app">
    <el-menu :default-active="activeIndex" class="el-menu-demo" mode="horizontal" @select="handleSelect">
      <el-menu-item index="1">仓位计划</el-menu-item>
    </el-menu>
    <div style="margin-top: 10px">
      <router-view v-if="isRouterAlive"></router-view>
    </div>
  </div>
</template>

<script>

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
    }
  },
  mounted() {
    const that = this;
  },
}
</script>
