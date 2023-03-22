<template>
  <div>
    <el-tabs v-model="activeTab" @tab-click="handleTabClick">
      <el-tab-pane label="待完成" name="PENDING"></el-tab-pane>
      <el-tab-pane label="进行中" name="IN_PROGRESS"></el-tab-pane>
      <el-tab-pane label="已完成" name="COMPLETED"></el-tab-pane>
    </el-tabs>
    <el-table :data="tasks" style="width: 100%">
      <el-table-column prop="id" label="ID" width="60"></el-table-column>
      <el-table-column prop="title" label="标题"></el-table-column>
<!--      <el-table-column prop="description" label="描述"></el-table-column>-->
<!--      <el-table-column prop="relatedEntity" label="关联实体"></el-table-column>-->
      <el-table-column prop="status" label="状态" width="100">
        <template slot-scope="{ row }">
          <el-tag :type="getStatusTagType(row.status)">{{ getStatusLabel(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作">
        <template slot-scope="{ row }">
          <el-button type="primary" v-if="row.status === 'PENDING'" @click="startTask(row.id)">开始进行</el-button>
          <el-button type="primary" v-if="row.status === 'IN_PROGRESS'" @click="completeTask(row.id)">完成</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script>

export default {
  data() {
    return {
      tasks: [],
      activeTab: 'PENDING'
    }
  },
  methods: {
    getTasks(status) {
      const that = this
      this.$http.get(`/api/task/list`, {params: {status: status}}).then(res=>{
        that.tasks = res
      })
    },
    startTask(taskId) {
      const that = this
      this.$http.post(`/api/task/start`, {id: taskId}).then(res=>{
        that.getTasks(this.activeTab)
      })
    },
    completeTask(taskId) {
      const that = this
      this.$http.post(`/api/task/complete`, {id: taskId}).then(res=>{
        that.getTasks(this.activeTab)
      })
    },
    getStatusLabel(status) {
      switch (status) {
        case 'PENDING':
          return '待完成'
        case 'IN_PROGRESS':
          return '进行中'
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
        case 'IN_PROGRESS':
          return 'primary'
        case 'COMPLETED':
          return 'success'
        default:
          return 'info'
      }
    },
    handleTabClick(tab) {
      this.activeTab = tab.name
      this.getTasks(this.activeTab)
    }
  },
  created() {
    this.getTasks(this.activeTab)
  }
}
</script>