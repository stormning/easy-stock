<template>
  <div>
    <el-table :data="tasks" style="width: 100%">
      <el-table-column prop="taskName" label="名称"></el-table-column>
      <el-table-column label="操作">
        <template slot-scope="{ row }">
          <el-button type="primary" @click="runTask(row.taskName)">立即执行</el-button>
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
      this.$http.get(`/api/scheduler/tasks`).then(res=>{
        that.tasks = res.data
      })
    },
    runTask(name) {
      const that = this
      this.$http.post(`/api/scheduler/runTask`, {name: name}).then(res=>{
        that.getTasks()
      })
    }
  },
  created() {
    this.getTasks()
  }
}
</script>