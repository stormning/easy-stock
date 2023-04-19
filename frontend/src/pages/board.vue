<template>
  <div id="app">
    <el-table :data="tableData">
      <el-table-column prop="name" label="名称"></el-table-column>
      <el-table-column prop="description" label="描述"></el-table-column>
      <el-table-column prop="status" label="状态">
        <template slot-scope="scope">
          <el-tag :type="statusTag(scope.row.status)">{{ scope.row.status }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="assignee" label="负责人"></el-table-column>
      <el-table-column prop="deadline" label="截止日期"></el-table-column>
    </el-table>
  </div>
</template>
<script>
export default {
  name: 'board',
  data() {
    return {
      tableData: [
        {
          name: '股票卖出提醒',
          description: '股票卖出策略和仓位管理之间的关系',
          status: '已完成',
          assignee: 'stormning',
          deadline: '2023-03-30'
        },
        {
          name: '板块机会',
          description: '板块估值体系和机会发掘',
          status: '待开发',
          assignee: 'stormning',
          deadline: '2023-05-10'
        }
      ]
    }
  },
  methods: {
    statusTag(status) {
      if (status === '待开发') {
        return 'info';
      } else if (status === '开发中') {
        return 'warning';
      } else if (status === '已完成') {
        return 'success';
      }
    }
  }
}
</script>
