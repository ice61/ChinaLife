<template>
  <div class="bgc">
    <div class="lg-box">
      <el-card class="login-card" style="text-align: center">
        <p style="font-size: 18px ;font-weight: bold">密码登陆</p>
        <el-form ref="lgform" :rules="rules" :model="lgform">
          <el-form-item prop="id" style="margin-top: 50px">
            <el-input v-model.number="lgform.id" placeholder="请输入工号" @blur="showimg"></el-input>
          </el-form-item>
          <el-form-item prop="password" style="margin-top: 30px">
            <el-input placeholder="请输入密码" v-model="lgform.password" show-password></el-input>
          </el-form-item>
          <el-form-item prop="code" v-show="imgshow" inline-message="true" style="margin-top: 10px">
            <el-input v-model="lgform.code" placeholder="请输入验证码"></el-input>
          </el-form-item>
          <img :src="imgsrc" v-show="imgshow" class="img-code" @click="showimg"><br><br>
          <el-button type="primary" class="login green-button" @click="login('lgform')">登陆</el-button>
          <!--<el-button @click="reset('lgform')">重置</el-button>-->
          <el-button type="primary" class="find yellow-button" @click="find">找回密码</el-button>
        </el-form>
      </el-card>
    </div>
  </div>

</template>

<script>
  export default {
    name: "Login",
    data() {
      return {
        lgform: {
          id: '',
          password: '',
          code: ''
        },
        rules: {
          id: [
            {required: true, message: '请输入工号', trigger: 'blur'},
          ],
          password: [
            {required: true, message: '请输入密码', trigger: 'blur'},
          ],
          code: [
            {required: true, message: '请输入验证码', trigger: 'blur'},
          ]
        },
        imgsrc: '',
        imgshow: false,
      }
    },
    methods: {
      showimg() {
        if (this.lgform.id) {
          this.$http.get('/api/clerk/image/code/' + this.lgform.id, {
            responseType: "arraybuffer",
          })
            .then((res) => {
              var src = 'data:image/jpg;base64,' + btoa(new Uint8Array(res.data).reduce((data, byte) => data + String.fromCharCode(byte), ''));
              this.imgsrc = src;
              this.imgshow = true;
            })
        }
      },
      find() {
        this.$router.push('/find');
      },
      login(formName)  {
        this.$refs[formName].validate((valid) => {
          if (valid) {
            var params = this.lgform;
            this.$http.post('/api/manauth/login', this.$qs.stringify(params))
              .then((res) => {
                this.$http.get('/api/manauth/verify')
                  .then((res) => {
                    this.$store.commit('updateInfo',res.data);
                    this.$message.success('登陆成功');
                    this.$router.push('/clerk');
                  });
              })
              .catch((error) => {
                this.$message.error(error.response.data.message);
                this.showimg();
              })
          } else {
            return false;
          }
        });
      },
      reset(formName) {
        this.$refs[formName].resetFields();
      },
      verifyInfo() {
        this.$http.get('/api/manauth/verify')
          .then((res) => {
            this.$store.commit('updateInfo',res.data);
          });
      }
    }

  }
</script>


<style scoped>

  @import "../assets/myStyle.css";

  .lg-box {
    left: 50%;
    top: 50%;
    position: absolute;
    transform: translate(-50%, -50%);
  }

  .login-card {
    /*height: 400px;*/
    width: 300px;
    padding: 20px;
    /*height: 400px;*/
    /*transition: height 1s;*/
  }

  .login {
    width: 260px;
  }

  .img-code {
    display: block;
    height: 50px;
    /*position: absolute;
    left: 50%;
    transform: translateX(-50%);*/
    margin-left: 85px;
  }

  .img-code:hover {
    cursor: pointer;
  }

  .find{
    width: 260px;
    margin-left: 0px;
    margin-top: 10px;
  }

  .bgc{
    width: 100%;
    height: 100%;
    background-image: url(http://image.chinalife.com/group1/M00/00/00/wKgrslzkAXmABtXTAAIBkEpF0sU563.jpg);
    background-size: cover;

  }
</style>
