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
          prop="firstBuy.price"
          label="买入价">
      </el-table-column>
      <el-table-column
          prop="nextBuy.price"
          label="下次加仓">
      </el-table-column>
      <el-table-column
          prop="earn"
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
        firstBuy: {
          price: '20.12',
          amount: 1000,
        },
        nextBuy: {
          price: '15.12',
          amount: 2000
        },
        earn: '5.5%'
      }]
    }
  },
  methods: {}
}
</script>
<style lang="scss">
</style>
