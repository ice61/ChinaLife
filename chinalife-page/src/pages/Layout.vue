<template>
  <el-container style="width: 100%;height: 100%">
    <el-aside width="auto" ><!--style="background-color:#363640;"-->
      <el-menu
        :collapse="collShow"
        class="el-menu-vertical-demo"
        :router=true
        background-color="#363640"
        text-color="#fff"
        active-text-color="#0b8257">
        <el-menu-item index="/clerk" :disabled="show(1)">
          <i class="el-icon-s-home"></i>
          <span slot="title">个人中心</span>
        </el-menu-item>
        <el-menu-item index="/insurance" :disabled="show(2)">
          <i class="el-icon-document"></i>
          <span slot="title">保单中心</span>
        </el-menu-item>
        <el-menu-item index="/manauth" :disabled="show(3)">
          <i class="el-icon-circle-plus"></i>
          <span slot="title">授权中心</span>
        </el-menu-item>
        <el-menu-item index="/auth" :disabled="show(4)">
          <i class="el-icon-s-tools"></i>
          <span slot="title">权限中心</span>
        </el-menu-item>
        <el-menu-item index="/staff" :disabled="show(5)">
          <i class="el-icon-user"></i>
          <span slot="title">员工中心</span>
        </el-menu-item>
        <el-menu-item index="/client" :disabled="show(6)">
          <i class="el-icon-s-custom"></i>
          <span slot="title">客户中心</span>
        </el-menu-item>
        <el-menu-item index="/chart" :disabled="show(7)">
          <i class="el-icon-s-data"></i>
          <span slot="title">视图中心</span>
        </el-menu-item>
        <el-menu-item index="/pan" :disabled="show(8)">
          <i class="el-icon-upload"></i>
          <span slot="title">文件中心</span>
        </el-menu-item>
        <!--<el-menu-item index="/add" :disabled="show(7)">
          <i class="el-icon-s-data"></i>
          <span slot="title">增添数据</span>
        </el-menu-item>-->
      </el-menu>
    </el-aside>

    <el-container style="height: 100%;width: 100%">
      <el-header class="header">
        <el-button
          icon="el-icon-s-operation"
          circle size="small"
          style="margin-top: 12px"
          @click="()=>this.collShow = !this.collShow"></el-button>
        <el-button @click="logout" class="logout" size="medium" round>
          登出
        </el-button>
      </el-header>
      <el-main class="main">
        <transition name="el-zoom-in-top" mode="out-in">
          <router-view @logout="logout"></router-view>
        </transition>
      </el-main>
    </el-container>
  </el-container>
</template>

<script>
  export default {
    name: "Layout",
    data() {
      return {
        auth: [],
        collShow: false,
      }
    },
    methods: {
      show(id) {
        return !this.auth.includes(id);
      },
      logout() {
        this.$http.get('/api/manauth/logout')
          .then(() => {
            this.$store.commit('clear');
            this.$router.push('/login');
            this.$message.success('成功退出');
          });
      }
    },
    mounted() {
      this.auth = this.$store.getters.getAuth;
      setInterval(() => {
        this.auth = this.$store.getters.getAuth;
      }, 3000)
    }
  }
</script>

<style scoped>
  .main {
    background-color: #f7f7f9;
    width: 100%;
    height: 100%;
  }

  .logout {
    float: right;
    margin-right: 30px;
    margin-top: 10px;
    color: #009b62;
    border: 1px solid #009b62;
    transition: all 0.5s;
  }

  .logout:hover {
    background-color: #009b62;
    color: #fff;
  }

  .header {
    border: 2px solid #eeeff0;
    height: 85px;
  }

  .el-menu-vertical-demo:not(.el-menu--collapse) {
    width: 200px;
    min-height: 100%;
  }

  .el-menu-vertical-demo {
    min-height: 100%;
  }

</style>
