<template>
  <div>
    <el-table :data="tasks" style="width: 100%">
      <el-table-column prop="id" label="ID" width="80"></el-table-column>
      <el-table-column prop="title" label="标题"></el-table-column>
      <el-table-column prop="description" label="描述"></el-table-column>
      <el-table-column prop="relatedEntity" label="关联实体"></el-table-column>
      <el-table-column prop="status" label="状态">
        <template slot-scope="{ row }">
          <el-tag :type="getStatusTagType(row.status)">{{ getStatusLabel(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作">
        <template slot-scope="{ row }">
          <el-button type="primary" v-if="row.status === 'PENDING'" @click="completeTask(row.id)">完成</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script>

export default {
  data() {
    return {
      tasks: []
    }
  },
  methods: {
    getTasks() {
      const that = this
      this.$http.get(`/api/task/list`).then(res=>{
        that.tasks = res
      })
    },
    completeTask(taskId) {
      const that = this
      this.$http.get(`/api/task/complete`).then(res=>{
        that.getTasks()
      })
    },
    getStatusLabel(status) {
      switch (status) {
        case 'PENDING':
          return '待完成'
        case 'COMPLETED':
          return '已完成'
        default:
          return ''
      }
    },
    getStatusTagType(status) {
      switch (status) {
        case 'PENDING':
          return 'warning'
        case 'COMPLETED':
          return 'success'
        default:
          return 'info'
      }
    }
  },
  created() {
    this.getTasks()
  }
}
</script>