<template>
  <div style="position: relative">
    <!--添加权限组卡片-->
    <el-card class="authGroupAdd-card">
      <el-form :model="authGroupAdd">
        <el-form-item label="权限组ID">
          <el-input v-model.number="authGroupAdd.id"></el-input>
        </el-form-item>
        <el-form-item label="权限组名称">
          <el-input v-model="authGroupAdd.alias"></el-input>
        </el-form-item>
      </el-form>
      <el-button
        type="primary"
        @click="addAuthGroup"
        class="green-button"
        style="margin-left: 70px">
        添加权限组
      </el-button>
    </el-card>

    <!--删除编辑权限卡片-->
    <el-card class="authOption-card">
      <el-tree
        :data="auths"
        :props="props"
      >
        <span class="custom-tree-node" slot-scope="{ node, data }">
        <span>{{ node.label }}</span>
        <span>
          <el-button
            type="text"
            size="mini"
            class="green-text"
            v-show="isNotGroup(data)"
            @click="() => edit(data)"
          >
            编辑
          </el-button>
          <el-button
            type="text"
            size="mini"
            class="yellow-text"
            v-show="isNotGroup(data)"
            @click="()=>del(data)"
          >
            删除
          </el-button>
        </span>
      </span>
      </el-tree>
    </el-card>

    <!--权限添加卡片-->
    <el-card class="authAdd-card">
      <el-form :model="authAdd">
        <el-form-item label="权限名称">
          <el-input v-model.number="authAdd.alias"></el-input>
        </el-form-item>
        <el-form-item label="权限url">
          <el-input v-model="authAdd.url"></el-input>
        </el-form-item>
        <el-form-item label="权限所属权限组">
          <el-select v-model="authAdd.groupId" placeholder="请选择" style="width: 260px">
            <el-option
              v-for="authGroup in authGroupHad"
              :key="authGroup.id"
              :label="authGroup.alias"
              :value="authGroup.id">
            </el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <el-button
        type="primary"
        @click="addAuth"
        class="green-button"
        style="margin-left: 70px">
        添加权限
      </el-button>
    </el-card>

    <!--编辑权限弹框-->
    <el-dialog title="编辑权限" :visible.sync="editShow">
      <el-form :model="newAuth" label-width="150px" labelPosition="left" style="margin-left: 150px">
        <el-form-item label="权限url">
          <el-input v-model="newAuth.url" class="input-width"></el-input>
        </el-form-item>
        <el-form-item label="权限别名">
          <el-input v-model="newAuth.alias" class="input-width"></el-input>
        </el-form-item>
        <el-form-item label="权限所属权限组">
          <el-select v-model="newAuth.groupId" placeholder="请选择" class="input-width">
            <el-option
              v-for="authGroup in authGroupHad"
              :key="authGroup.id"
              :label="authGroup.alias"
              :value="authGroup.id">
            </el-option>
          </el-select>
        </el-form-item>
        <el-button
          @click="update"
          class="green-button"
          type="primary"
          style="width: 200px;margin-left: 180px">提 交
        </el-button>
      </el-form>
    </el-dialog>

  </div>
</template>

<script>
  export default {
    name: "Auth",
    data() {
      return {
        authGroupAdd: {},
        authGroupHad: [],

        authAdd: {},

        auths: [],

        newAuth: {},

        editShow: false,

        props: {
          children: 'children',
          label: 'alias',
        }

      }
    },
    methods: {

      haveAuth(id) {
        let temp = this.$store.getters.getAuth;
        if (!temp.includes(id)) {
          this.$emit('logout');
          this.$store.commit('clear');
          this.$router.push('/login');
        }
      },

      addAuthGroup() {
        let params = this.authGroupAdd;
        this.authGroupAdd = {};
        this.$http.post('/api/auth/add/group', this.$qs.stringify(params))
          .then((res) => {
            this.$message.success('添加权限组成功');
            this.authGroupAdd = {};
            this.findAllAuthGroup();
            this.findAllAuth();
          })
          .catch((error) => {
            this.$message.error(error.response.data.message);
          });
        this.verifyInfo();
      },

      findAllAuthGroup() {
        this.$http.get('/api/auth/find/group')
          .then((res) => {
            this.authGroupHad = res.data;
          }).catch((error) => {
          this.$message.error(error.response.data.message);
        });
        this.verifyInfo();
      },

      addAuth() {
        let params = this.authAdd;
        this.$http.post('/api/auth/add', this.$qs.stringify(params))
          .then((res) => {
            this.$message.success('添加权限成功');
            this.authAdd = {};
            this.findAllAuth();
          })
          .catch((error) => {
            this.$message.error(error.response.data.message);
          });
        this.verifyInfo();
      },

      findAllAuth() {
        this.$http.get('/api/auth/find/groupAll')
          .then((res) => {
            this.auths = res.data;
          }).catch((error) => {
          this.$message.error(error.response.data.message);
        });
        this.verifyInfo();
      },
      isNotGroup(data) {
        if (data.children === undefined) {
          return true;
        } else
          return false;
      },
      del(data) {
        this.$confirm('此操作将永久删除该文件, 是否继续?', '提示', {
          type: "warning"
        }).then(() => {
          this.$http.delete('/api/auth/del/' + data.id)
            .then(() => {
              this.$message.success('删除权限成功');
              this.findAllAuth();
            }).catch((error) => {
            this.$message.error(error.response.data.message);
          })
        }).catch(() => {
          this.$message.info('已取消删除');
        });
        this.verifyInfo();
      },

      edit(data) {
        this.editShow = true;
        this.newAuth = JSON.parse(JSON.stringify(data));
      },

      update() {
        this.$http.put('/api/auth/update', this.newAuth)
          .then(() => {
            this.$message.success('权限修改成功');
            this.editShow = false;
            this.findAllAuth();
          }).catch((error) => {
          this.$message.error(error.response.data.message);
        });
        this.verifyInfo();
      },
      verifyInfo() {
        this.$http.get('/api/manauth/verify')
          .then((res) => {
            this.$store.commit('updateInfo', res.data);
            this.haveAuth(4);
          }).catch((error) => {
          this.$message.warning(error.response.data.message);
          this.$store.commit('clear');
          this.$router.push('/login');
        });
      },

    },
    beforeMount() {
      this.verifyInfo();
      this.findAllAuthGroup();
      this.findAllAuth();
    }
  }
</script>

<style scoped>

  @import url('../assets/myStyle.css');

  .authGroupAdd-card {
    width: 300px;
    margin-left: 180px;
    /*float: left;*/
    position: absolute;
  }

  .authAdd-card {
    width: 300px;
    margin-top: 400px;
    margin-left: 180px;
    /*float: left;*/
    position: absolute;
  }

  .authOption-card {
    width: 600px;
    padding: 50px;
    /*position: absolute;
    right: 200px;
    top: 0;*/
    float: right;
    margin-right: 200px;

  }

  .custom-tree-node {
    flex: 1;
    display: flex;
    align-items: center;
    justify-content: space-between;
    font-size: 14px;
    padding-right: 8px;
  }

  .input-width {
    width: 400px;
  }

</style>
