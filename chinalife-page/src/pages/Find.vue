<template>
  <div class="bgc">
    <el-card class="find-card" style="text-align: center">
      <p style="font-size: 18px ;font-weight: bold">短信改密</p>
      <el-form ref="lgform">
        <el-form-item  style="margin-top: 50px">
          <el-input v-model.number="info.id" placeholder="请输入工号" @blur="showimg"></el-input>
        </el-form-item>
        <el-form-item  style="margin-top: 30px">
          <el-input placeholder="请输入新密码" v-model="info.newPassword" show-password></el-input>
        </el-form-item>
        <el-form-item  style="margin-top: 30px">
          <el-input placeholder="请输入手机号码" v-model="info.phone"></el-input>
        </el-form-item>
        <el-form-item  style="margin-top: 30px">
          <el-input placeholder="请输入验证码" v-model="info.code"  style="width: 130px;: 0px"></el-input>
          <el-button type="primary" class=" yellow-button" style="margin-left: 28px" @click="sendMessage">发送短信</el-button>
        </el-form-item>
        <el-button type="primary" class=" yellow-button" style="width: 260px" @click="update" >提交</el-button>
        <el-button type="primary" class=" green-button" @click="login" style="width: 260px;margin-left: 0;margin-top: 10px">登陆</el-button>
      </el-form>
    </el-card>
  </div>
</template>

<script>
  export default {
    name: "Find",
    data() {
      return {
        info: {
          id: '',
          newPassword:'',
          phone:'',
          code:'',
        },
      }
    },
    methods: {
      sendMessage() {
        let phone = this.info.phone;
        if (!(/^1[34578]\d{9}$/.test(phone))) {
          this.$message.warning('请正确填写手机号码');
          return;
        }
        let temp = {};
        temp.phone = this.info.phone
        this.$http.post('/api/clerk/password/code',this.$qs.stringify(temp))
          .then(() => {
            this.$message.success('发送成功');
        }).catch(()=> {
          this.$message.error('发送失败');
        })
      },

      login() {
        this.$router.push('/login');
      },

      update() {
        let temp = {};
        temp.clerkId = this.info.id;
        temp.newPassword = this.info.newPassword;
        temp.code = this.info.code;
        temp.phone = this.info.phone;
        this.$http.post('/api/clerk/change/password/code',this.$qs.stringify(temp))
          .then(() => {
            this.$message.success('修改成功');
          }).catch((error)=> {
          this.$message.error(error.data.message);
        })
      }
    }
  }
</script>

<style scoped>

  @import "../assets/myStyle.css";

  .bgc {
    width: 100%;
    height: 100%;
    background-image: url(http://image.chinalife.com/group1/M00/00/00/wKgrslzkAXmABtXTAAIBkEpF0sU563.jpg);
    background-size: cover;
  }

  .find-card {
    width: 300px;
    padding: 20px;

    left: 50%;
    top: 50%;
    position: absolute;
    transform: translate(-50%, -50%);
  }
</style>
