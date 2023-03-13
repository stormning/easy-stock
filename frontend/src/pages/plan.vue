<template>
  <div>
    <el-breadcrumb separator="/" class="mb20">
      <el-breadcrumb-item :to="{ path: '/plans' }">个股计划</el-breadcrumb-item>
      <el-breadcrumb-item>中国电影</el-breadcrumb-item>
    </el-breadcrumb>
    <StockDetail :plan="plan"/>
    <div class="line"></div>
    <div class="mb10">
      <el-button type="primary" @click="dialogGenFormVisible = true">按策略生成</el-button>
      <el-button type="primary" @click="dialogFormVisible = true">自定义</el-button>
    </div>
    <el-table
        :data="items"
        style="width: 100%" :row-class-name="tableRowClassName" :highlight-current-row="false">
      <el-table-column
          prop="price"
          label="价格"
          width="100">
      </el-table-column>
      <el-table-column
          prop="amount"
          label="计划数量"
          width="100">
      </el-table-column>
      <el-table-column
          prop="turnover"
          label="计划金额">
      </el-table-column>
      <el-table-column
          prop="remark"
          label="备注">
      </el-table-column>
      <el-table-column
          prop="status.text"
          label="状态">
      </el-table-column>
      <el-table-column
          fixed="right"
          label="操作">
        <template slot-scope="scope">
          <template v-if="scope.row.status.code==='WAIT'">
            <el-button @click="onItemOpen(scope.row)" type="text" size="small">完成</el-button>
            <el-button @click="onItemOpen(scope.row)" type="text" size="small">编辑</el-button>
            <el-button @click="onItemOpen(scope.row)" type="text" size="small">删除</el-button>
          </template>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog title="计划项" :visible.sync="dialogFormVisible" :destroy-on-close="true" :before-close="onItemClose">
      <el-form :model="curItem">
        <el-form-item label="价格" :label-width="formLabelWidth">
          <el-input v-model="curItem.price" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="数量" :label-width="formLabelWidth">
          <el-input v-model="curItem.amount" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="备注" :label-width="formLabelWidth">
          <el-input v-model="curItem.remark" autocomplete="off"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="onItemClose">取 消</el-button>
        <el-button type="primary" @click="dialogFormVisible = false">确 定</el-button>
      </div>
    </el-dialog>

    <el-dialog title="计划项" :visible.sync="dialogGenFormVisible" width="500px">
      <el-steps :active="active" simple>
        <el-step title="选择策略" icon="el-icon-cpu"></el-step>
        <el-step title="填写参数" icon="el-icon-edit"></el-step>
      </el-steps>
      <el-divider>策略参数</el-divider>
      <el-form :model="strategy" v-if="active===1">
        <el-form-item label="策略类型" :label-width="formLabelWidth">
          <el-radio :label="s.className" v-for="s in strategies" v-model="strategy.className" border>
            {{ s.remark }}
          </el-radio>
        </el-form-item>
      </el-form>
      <el-form :model="strategy" v-if="active===2">
        <el-form-item label="买入价格" :label-width="formLabelWidth">
          <el-input v-model="strategy.args.startPrice" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="最低价格" :label-width="formLabelWidth">
          <el-input v-model="strategy.args.endPrice" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="等额梯度" :label-width="formLabelWidth">
          <el-input v-model="strategy.args.gradient" autocomplete="off"
                    placeholder="如：取最近一次下跌最大幅度和最幅度之和的两倍"></el-input>
        </el-form-item>
        <el-divider>公共参数</el-divider>
        <el-form-item label="起手最大金额" :label-width="formLabelWidth">
          <el-input v-model="strategy.startCost" autocomplete="off"
                    placeholder="调整起手金额查看计划能否承受"></el-input>
        </el-form-item>
        <el-form-item label="补仓至亏损幅度" :label-width="formLabelWidth">
          <el-input v-model="strategy.supplement" autocomplete="off" placeholder="建议等额梯度的一半"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <template v-if="active===1">
          <el-button @click="active=2">下一步</el-button>
        </template>
        <template v-else>
          <el-button @click="active=1">上一步</el-button>
          <el-button type="primary" @click="genItemsByStrategy">生成</el-button>
        </template>
      </div>
    </el-dialog>
  </div>
</template>

<style lang="scss">
@import '../assets/css/core.scss';

.el-table .wait {
  background: #DFF6FC;
}

.el-table .finished {
  background: #F2F6FC;
}
</style>
<script>
import StockDetail from "@/components/PlanDetail";
import {Decimal} from 'decimal.js'

export default {
  components: {StockDetail},
  data() {
    return {
      plan: null,
      active: 1,
      items: [],
      strategies: [
        {
          className: 'com.slyak.es.service.impl.SimplePriceStepStrategy',
          remark: '起始、终止价等额梯度',
          args: {
            startPrice: 0,
            gradient: 20
          }
        }
      ],
      form: {
        name: '',
        region: '',
        date1: '',
        date2: '',
        delivery: false,
        type: [],
        resource: '',
        desc: ''
      },
      strategy: {
        className: '',
        args: {},
        startCost: 0,
        supplement: 7.6
      },
      curItem: {},
      dialogGenFormVisible: false,
      dialogFormVisible: false,
      formLabelWidth: '120px'
    }
  },
  methods: {
    tableRowClassName({row, rowIndex}) {
      return row.status.code.toLowerCase();
    },
    onItemOpen(row) {
      this.curItem = row
      this.dialogFormVisible = true
    },
    onItemClose() {
      this.dialogFormVisible = false
      this.curItem = {}
    },
    genItemsByStrategy() {
      const that = this
      this.$http.post(`/api/plan/genItemsByStrategy?id=${this.$route.params.id}`, this.strategy).then(res => {
        that.items = res.data
        that.dialogGenFormVisible = false
      })
    }
  },
  beforeMount() {
    const that = this
    let planId = this.$route.params.id
    this.$http.get(`/api/plan/get?id=${planId}`).then(res => {
      that.plan = res.data
      that.strategy.className = that.strategies[0].className
    })
  },
  mounted() {
    console.log(this.plan)
    const that = this
    let planId = this.$route.params.id
    this.$http.get(`/api/plan/items?id=${planId}`).then(res => {

    })
  },
  watch: {
    'strategy.className': {
      handler(newVal, oldVal) {
        if (oldVal !== newVal) {
          this.$set(this.strategy, 'args', this.strategies[this.active - 1].args)
          this.strategy.args.startPrice = this.plan.stock.info.price
        }
      }
    },
    'strategy.args.startPrice': {
      handler(newVal, oldVal) {
        if (oldVal !== newVal) {
          const places = new Decimal(newVal).decimalPlaces()
          this.$set(this.strategy.args, 'endPrice', new Decimal(newVal).div(2).toFixed(places))
          this.strategy.startCost = new Decimal(newVal).mul(100).toFixed(0)
        }
      }
    },
    'strategy.args.gradient': {
      handler(newVal, oldVal) {
        if (oldVal !== newVal) {
          this.strategy.supplement = new Decimal(newVal).mul(new Decimal('0.382')).toFixed(1)
        }
      }
    },
  }
}
</script>