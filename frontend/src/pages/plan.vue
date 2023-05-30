<template>
  <div>
    <StockDetail :plan="plan" ref="stockDetail"/>
    <div class="line"></div>
    <div class="mb10">
      <el-button type="primary" @click="dialogGenFormVisible = true" :disabled="plan.amount>0">按策略生成</el-button>
      <el-button type="primary" @click="itemFormVisible = true">自定义</el-button>
      <el-button type="danger" @click="deleteItems">清空所有</el-button>
    </div>
    <el-table
        :data="items"
        style="width: 100%"
        :row-class-name="tableRowClassName"
        :highlight-current-row="false"
    >
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
          prop="realAmount"
          label="已购数量"
          width="100">
      </el-table-column>
      <el-table-column
          prop="cost"
          label="计划金额">
      </el-table-column>
      <el-table-column
          prop="remark"
          label="备注">
      </el-table-column>
      <el-table-column
          label="状态">
        <template slot-scope="scope">
          {{ scope.row.status === 'WAIT' ? '待完成' : (scope.row.status === 'PART_FINISH'?'部成(缺'+(scope.row.amount- scope.row.realAmount)+')':'已完成') }}
        </template>
      </el-table-column>
      <el-table-column
          fixed="right"
          label="操作">
        <template slot-scope="scope">
          <template v-if="scope.row.status==='WAIT'||scope.row.status==='PART_FINISH'">
          <el-button @click="finishItem(scope.row)" type="text" size="small">完成</el-button>
          </template>
          <template v-if="scope.row.status==='WAIT'">
            <el-button @click="editItem(scope.row)" type="text" size="small">编辑</el-button>
            <el-button @click="deleteItem(scope.row)" type="text" size="small">删除</el-button>
          </template>
        </template>
      </el-table-column>
    </el-table>
    <table class="summary">
      <tr>
        <td>计划金额：{{ summary.totalCost }}元</td>
        <td>计划数量：{{ summary.totalAmount }}股</td>
      </tr>
      <tr>
        <td>持仓金额：{{ summary.curCost }}元</td>
        <td>持仓数量: {{ summary.curAmount }}股</td>
      </tr>
      <tr>
        <td>剩余金额：{{ summary.leftCost }}元</td>
        <td>剩余数量: {{ summary.leftAmount }}股</td>
      </tr>
    </table>


    <el-dialog title="计划项" :visible.sync="itemFormVisible" :destroy-on-close="true" :before-close="onItemClose">
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
        <el-button type="primary" @click="saveItem">确 定</el-button>
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

.el-table .wait,.el-table .part_finish {
  background: #DFF6FC;
}

.el-table .finish {
  background: #F2F6FC;
}

.summary {
  float: right;
  margin-top: 10px;
  td {
    border: 1px solid #000000;
    padding: 5px 10px;
    font-size: 14px;
  }
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
      hasFinishedItem: false,
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
      itemFormVisible: false,
      formLabelWidth: '120px'
    }
  },
  methods: {
    tableRowClassName({row, rowIndex}) {
      return row.status.toLowerCase();
    },
    editItem(row) {
      this.curItem = row
      this.itemFormVisible = true
    },
    finishItem(row) {
      const that = this
      this.$confirm('确定已经完成?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$prompt('请输入数量', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          inputValue: row.amount - row.realAmount,
          inputPattern: /^[1-9]+\d*00$/,
          inputErrorMessage: '输入正确的股数'
        }).then(({ value }) => {
          this.$http.post(`/api/plan/finishItem`, {'id':row.id, 'amount': value}, {
            headers: {
              'Content-Type': 'application/x-www-form-urlencoded'
            }
          }).then(res=>{
            that.loadPlanItems()
          })
        }).catch(() => {});
      })
    },
    deleteItem(row) {
      const that = this
      this.$confirm('确定要删除此项?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$http.post(`/api/plan/deleteItem`, {'id':row.id}).then(res=>{
          that.loadPlanItems()
        })
      })
    },
    saveItem() {
      const that = this
      this.curItem.planId = this.plan.id
      this.$http.post(`/api/plan/saveItem`, this.curItem).then(res => {
        that.itemFormVisible = false
        that.loadPlanItems()
      })
    },
    deleteItems() {
      const that = this
      that.$confirm('确定要清空所有计划项?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
          that.$confirm('这项操作不可逆，确定继续?', '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
          }).then(() => {
            that.$http.post(`/api/plan/deleteItems`, {'id':that.plan.id}).then(res=>{
              that.loadPlanItems()
            })
          })
      })
    },
    onItemClose() {
      this.itemFormVisible = false
      this.curItem = {}
    },
    genItemsByStrategy() {
      const that = this
      this.$http.post(`/api/plan/genItemsByStrategy?id=${this.$route.params.id}`, this.strategy).then(res => {
        that.items = res.data
        that.dialogGenFormVisible = false
      })
    },
    loadPlanItems() {
      const that = this
      let planId = this.$route.params.id
      this.$http.get(`/api/plan/items?id=${planId}`).then(res => {
        that.items = res.data
        that.loadPlan()
      })
    },
    loadPlan() {
      const that = this
      let planId = this.$route.params.id
      this.$http.get(`/api/plan/get?id=${planId}`).then(res => {
        that.plan = res.data
        that.strategy.className = that.strategies[0].className
        that.$refs.stockDetail.$props.plan = res.data
      })
    }
  },
  beforeMount() {
    this.loadPlan()
  },
  mounted() {
    console.log(this.plan)
    this.loadPlanItems()
  },
  computed: {
    summary: function () {
      let totalCost = new Decimal('0.00')
      let curCost = new Decimal('0.00')
      let leftCost = new Decimal('0.00')
      let totalAmount = 0
      let curAmount = 0
      let leftAmount = 0
      if (this.items && this.items.length > 0) {
        for (let i = 0; i < this.items.length; i++) {
          let item = this.items[i];
          totalCost = totalCost.add(new Decimal(item.cost))
          totalAmount += item.amount
          if (item.status === 'WAIT') {
            leftCost = leftCost.add(new Decimal(item.cost))
            leftAmount += item.amount
          } else {
            curCost = curCost.add(new Decimal(item.cost))
            curAmount += item.amount
          }
        }
      }
      return {totalCost, totalAmount, curCost, curAmount, leftCost, leftAmount}
    }
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