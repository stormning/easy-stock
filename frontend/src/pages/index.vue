<template>
  <div>
    <el-table
        :data="plans"
        height="250"
        border
        style="width: 100%">
      <el-table-column
          prop="stock.name"
          label="名称">
      </el-table-column>
      <el-table-column
          prop="buyPrice"
          label="买入价">
      </el-table-column>
      <el-table-column
          prop="name"
          label="下次加仓">
      </el-table-column>
      <el-table-column
          prop="address"
          label="盈亏">
      </el-table-column>
    </el-table>
  </div>
</template>
<script>

export default {
  components: {},
  data() {
    return {
      plans: [{
        stock: {
          name: '京泉华'
        },
        buyPrice: '20.12'
      }]
    }
  },
  methods: {}
}
</script>
<style lang="scss">
</style>
