<template>
  <div>
    <el-breadcrumb separator="/" class="mb20">
      <el-breadcrumb-item :to="{ path: '/stocks' }">个股计划</el-breadcrumb-item>
      <el-breadcrumb-item>中国电影</el-breadcrumb-item>
    </el-breadcrumb>
    <StockDetail/>
    <div class="line"></div>
    <div class="mb10">
      <el-button type="primary" @click="dialogGenFormVisible = true">策略生成项</el-button>
      <el-button type="primary" @click="dialogFormVisible = true">自定义项</el-button>
    </div>
    <el-table
        :data="tableData"
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

    <el-dialog title="计划项" :visible.sync="dialogGenFormVisible">
      <el-form :model="form">
        <el-form-item label="买入价格" :label-width="formLabelWidth">
          <el-input v-model="form.name" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="最低价格" :label-width="formLabelWidth">
          <el-input v-model="form.name" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="下跌幅度" :label-width="formLabelWidth">
          <el-input v-model="form.name" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="加仓至亏损幅度" :label-width="formLabelWidth">
          <el-input v-model="form.name" autocomplete="off"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取 消</el-button>
        <el-button type="primary" @click="dialogFormVisible = false">确 定</el-button>
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
import StockDetail from "@/components/StockDetail";

export default {
  components: {StockDetail},
  data() {
    return {
      tableData: [{
        price: '14.12',
        amount: 100,
        turnover: '1417.03',
        remark: '',
        status: {
          code: 'FINISHED',
          text: '已完成'
        }
      }, {
        price: '13.20',
        amount: 100,
        turnover: '1325.03',
        remark: '',
        status: {
          code: 'FINISHED',
          text: '已完成'
        }
      }, {
        price: '12.28',
        amount: 400,
        turnover: '4917.10',
        remark: '',
        status: {
          code: 'WAIT',
          text: '待完成'
        }
      }, {
        price: '11.36',
        amount: 1200,
        turnover: '13637.27',
        remark: '',
        status: {
          code: 'WAIT',
          text: '待完成'
        }
      }, {
        price: '11.36',
        amount: 1200,
        turnover: '13637.27',
        remark: '',
        status: {
          code: 'WAIT',
          text: '待完成'
        }
      }],
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
      curItem: {},
      dialogGenFormVisible: false,
      dialogFormVisible: false,
      formLabelWidth: '120px'
    }
  },
  methods: {
    onSubmit() {
      console.log('submit!');
    },
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
    }
  }
}
</script>