<template>
  <div>
    <el-form :inline="true" class="demo-form-inline">
      <el-form-item label="股票">
        <el-select
            v-model="stockCode"
            filterable
            clearable
            remote
            reserve-keyword
            placeholder="请输入关键词"
            :remote-method="queryStocks"
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
        <el-button type="primary" @click="queryPlans">查询</el-button>
        <el-button type="primary" @click="initPlan">添加</el-button>
      </el-form-item>
    </el-form>
    <el-table
        :data="plans"
        style="width: 100%">
      <el-table-column
          prop="stock.name"
          label="名称">
      </el-table-column>
      <el-table-column
          prop="stock.info.price"
          label="当前价格">
      </el-table-column>
      <el-table-column
          prop="avgPrice"
          label="持仓均价">
      </el-table-column>
      <el-table-column
          prop="amount"
          label="股数">
      </el-table-column>
      <el-table-column
          prop="cost"
          label="持仓总成本">
      </el-table-column>
      <el-table-column
          prop="win"
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
      stockCode: '',
      options: [],
      formLabelWidth: '120px',
      plans: []
    }
  },
  methods: {
    handleClick(row) {
      this.$router.push({path: `/plan/${row.id}`})
    },
    initPlan() {
      const that = this
      if (!this.stockCode) {
        this.$message({
          message: '请选择一个股票',
          type: 'warning'
        });
        return
      }
      this.$http.post("/api/plan/init", {stockCode: this.stockCode}).then(res => {
        that.stockCode = ''
        that.queryPlans()
      })
    },
    queryStocks(query) {
      const that = this
      if (query !== '' && !that.loading) {
        setTimeout(function (){
          that.loading = true;
          that.$http.get(`/api/stock/list?keyword=${query}`).then(res => {
            that.loading = false;
            if (res.success) {
              that.options = res.data
            } else {
              that.options = []
            }
          })
        },500)
      } else {
        this.options = [];
      }
    },
    queryPlans(){
      const that = this
      this.$http.get(`/api/plan/list?stockCode=${this.stockCode}`).then(res=>{
        that.plans = res.data
      })
    }
  },
  mounted() {
    this.queryPlans()
  }
}
</script>
<style lang="scss">
</style>