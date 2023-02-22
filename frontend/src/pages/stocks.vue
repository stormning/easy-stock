<template>
  <div>
    <el-form :inline="true" class="demo-form-inline">
      <el-form-item label="关键字">
        <el-select
            v-model="stockCode"
            filterable
            clearable
            remote
            reserve-keyword
            placeholder="请输入关键词"
            :remote-method="remoteMethod"
            :loading="loading">
          <el-option
              v-for="item in options"
              :key="item.code"
              :label="`${item.name}(${item.code})`"
              :value="item.code">
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="search">查询</el-button>
        <el-button type="primary" @click="addStock">添加</el-button>
      </el-form-item>
    </el-form>
    <el-table
        :data="plans"
        height="250"
        style="width: 100%">
      <el-table-column
          prop="stock.name"
          label="名称">
      </el-table-column>
      <el-table-column
          prop="stock.price"
          label="当前价格">
      </el-table-column>
      <el-table-column
          prop="nextBuy.price"
          label="下次加仓">
      </el-table-column>
      <el-table-column
          prop="earn"
          label="盈亏">
      </el-table-column>
      <el-table-column
          fixed="right"
          label="操作"
          width="100">
        <template slot-scope="scope">
          <el-button @click="handleClick(scope.row)" type="text" size="small">编辑</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>
<script>

export default {
  components: {},
  data() {
    return {
      loading: false,
      stockCode: null,
      options: [],
      formLabelWidth: '120px',
      stockForm: {
        codeOrName: ''
      },
      plans: [{
        stock: {
          name: '京泉华',
          price: '20.2'
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
  methods: {
    handleClick(row) {
      console.log(this.$router)
      this.$router.push({path: '/stock'})
    },
    addStock() {
      if (!this.stockCode) {
        this.$message({
          message: '请选择一个股票',
          type: 'warning'
        });
        return
      }
      this.$http.post("/api/stock/add", {stock: this.stockForm.codeOrName}).then(res => {
        this.dialogVisible = false
      })
    },
    search() {

    },
    remoteMethod(query) {
      if (query !== '') {
        this.loading = true;
        this.$http.get(`/api/stock/list?keyword=${query}`).then(res => {
          this.loading = false;
          if (res.success) {
            this.options = res.data
          } else {
            this.options = []
          }
        })
      } else {
        this.options = [];
      }
    }
  }
}
</script>
<style lang="scss">
</style>