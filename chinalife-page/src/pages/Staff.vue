<template>
  <div>
    <!--员工信息表格-->
    <el-card>
      <el-button
        type="primary"
        class="green-button"
        @click="registeShow">
        新增员工
      </el-button>
      <el-table
        ref="insTable"
        :data="staffTable"
        @sort-change="sortChange"
      >
        <el-table-column
          prop="id"
          label="工号"
          sortable="custom">
        </el-table-column>

        <el-table-column
          prop="name"
          label="姓名">
        </el-table-column>

        <el-table-column
          prop="instiution"
          label="机构号"
          sortable="custom">
        </el-table-column>

        <el-table-column
          prop="sex"
          label="性别">
        </el-table-column>

        <el-table-column
          prop="created"
          label="创建时间"
          sortable="custom">
        </el-table-column>

        <el-table-column
          prop="score"
          label="销量"
          sortable="custom">
        </el-table-column>

        <el-table-column
          align="right">
          <template slot="header" slot-scope="scope">
            <el-input
              v-model="key"
              size="mini"
              placeholder="输入关键字搜索"/>
          </template>
          <template slot-scope="scope">
            <el-button
              size="mini"
              type="primary"
              class="green-button"
              @click="handleSee( scope.row)">详情
            </el-button>
            <el-button
              size="mini"
              type="danger"
              class="yellow-button"
              @click="handleDelete(scope.row)">删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      <!--表格分页-->
      <div style="margin-left: 350px;margin-top: 20px;width: 50%">
        <el-pagination
          :page-sizes="[5,10,15]"
          :page-size="5"
          :total="total"
          layout="total, sizes, prev, pager, next,->,jumper"
          :pager-count="7"
          @size-change="pageSizeChange"
          @current-change="pageChange"
        >
        </el-pagination>
      </div>
    </el-card>

    <!--员工注册表单弹窗-->
    <el-dialog title="员工注册" :visible.sync="registerShow">
      <el-form :model="newStaff" label-width="150px" labelPosition="left" style="margin-left: 150px">
        <div style="width: 100px ;margin-left: 240px;margin-bottom: 15px">
          <el-upload
            class="avatar-uploader"
            action="http://api.chinalife.com/zuul/api/upload/image"
            :show-file-list="false"
            :on-success="uploadImageSuccess">
            <img v-if="imageUrl" :src="imageUrl" class="avatar">
            <i v-else class="el-icon-plus avatar-uploader-icon"></i>
          </el-upload>
        </div>
        <el-form-item label="工号：">
          <el-input v-model="newStaff.id" style="width: 400px"></el-input>
        </el-form-item>
        <el-form-item label="机构号：">
          <el-input v-model="newStaff.instiution" style="width: 400px"></el-input>
        </el-form-item>
        <el-form-item label="姓名：">
          <el-input v-model="newStaff.name" style="width: 400px"></el-input>
        </el-form-item>
        <el-form-item label="性别：">
          <el-radio v-model="newStaff.sex" label=true>男</el-radio>
          <el-radio v-model="newStaff.sex" label=false>女</el-radio>
        </el-form-item>
        <el-form-item label="生日：">
          <el-date-picker
            v-model="newStaff.birthday"
            type="date"
            placeholder="选择日期"
            format="yyyy-MM-dd"
            style="width: 400px"
            value-format="yyyy-MM-dd">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="密码：">
          <el-input v-model="newStaff.password" style="width: 400px" show-password></el-input>
        </el-form-item>
        <el-form-item label="确认密码：">
          <el-input v-model="newStaff.newPassword" style="width: 400px" show-password></el-input>
        </el-form-item>
        <el-form-item label="电话：">
          <el-input v-model.number="newStaff.phone" style="width: 400px"></el-input>
        </el-form-item>
        <el-form-item label="验证码：">
          <el-input v-model="newStaff.code" style="width: 175px"></el-input>
          <el-button type="primary" style="margin-left: 50px;width: 175px" @click="getCode" class="yellow-button">获取验证码</el-button>
        </el-form-item>
      </el-form>
      <el-button type="primary" class="green-button" style="margin-left: 300px;width: 300px;margin-top: 10px" @click="registe">注册</el-button>
    </el-dialog>

    <!--员工信息详情弹窗-->
    <el-dialog title="员工信息" :visible.sync="detailShow">
      <table width="400px" style="margin-left: 240px">
        <tr>
          <th>头像：</th>
          <th>
            <el-image :src="staffDetail.image" style="width: 60px; height: 60px" fit="fill"></el-image>
          </th>
        </tr>
        <tr>
          <th>姓名：</th>
          <th>{{staffDetail.name}}</th>
        </tr>
        <tr>
          <th>工号：</th>
          <th>{{staffDetail.id}}</th>
        </tr>
        <tr>
          <th>电话:</th>
          <th>{{staffDetail.phone}}</th>
        </tr>
        <tr>
          <th>生日:</th>
          <th>{{staffDetail.birthday}}</th>
        </tr>
        <tr>
          <th>业绩:</th>
          <th>{{staffDetail.score}}</th>
        </tr>
        <tr>
          <th>创建时间:</th>
          <th>{{staffDetail.created}}</th>
        </tr>
        <tr>
          <th>改密时间:</th>
          <th>{{staffDetail.updated}}</th>
        </tr>
      </table>
    </el-dialog>
  </div>
</template>

<script>
  export default {
    name: "Staff",
    data() {
      return {
        staffTable: [],
        key: '',
        page: '',
        sizes: '',
        sortBy: '',
        desc: false,
        total: 0,

        staffDetail: {},

        detailShow: false,
        registerShow: false,

        newStaff: {},

        imageUrl: '',

      }
    },
    methods: {

      uploadImageSuccess(res, file) {
        this.imageUrl = res;
        this.newStaff.image = res;
        console.log(this.newStaff);
        this.verifyInfo();
      },

      verifyInfo() {
        this.$http.get('/api/manauth/verify')
          .then((res) => {
            this.$store.commit('updateInfo', res.data);
            this.haveAuth(5);
          }).catch((error) => {
          this.$message.warning(error.response.data.message);
          this.$store.commit('clear');
          this.$router.push('/login');
        });
      },

      getUrl(res) {
        this.newStaff.image = res;
      },

      registeShow() {
        this.newStaff = {};
        this.registerShow = true;
        this.verifyInfo();
      },

      getCode() {
        let phone = this.newStaff.phone;
        if (!(/^1[34578]\d{9}$/.test(phone))) {
          this.$message.warning('请正确填写手机号码');
          return;
        }
        let params = {};
        params.phone = phone;
        this.$http.post('/api/clerk/code', this.$qs.stringify(params));
        this.verifyInfo();
      },

      registe() {
        delete this.newStaff.newPassword;
        let params = this.newStaff;
        this.$http.post('/api/clerk/register', this.$qs.stringify(params))
          .then(() => {
            this.$message.success('注册成功');
            this.newStaff = {};
            this.registerShow = false;
          }).catch((error) => {
          this.$message.error(error.response.data.message);
        });
        this.verifyInfo();
      },

      searchStaffs() {
        this.$http.get('/api/search/clerk/page', {
          params: {
            page: this.page,
            rows: this.sizes,
            sortBy: this.sortBy,
            desc: this.desc,
            key: this.key,
          },
        })
          .then((res) => {
            this.staffTable = res.data.data;
            this.total = res.data.total;
          });
        this.verifyInfo();
      },

      sortChange(date) {
        this.desc = date.order === 'descending' ? true : false;
        this.sortBy = date.prop;
      },
      pageSizeChange(size) {
        this.sizes = size;
      },
      pageChange(page) {
        this.page = page;
      },

      handleSee(val) {
        this.staffDetail = val;
        this.detailShow = true;
      },

      handleDelete(val) {
        this.$confirm('确认删除？', '提示', {
          type: 'warning',
        }).then(() => {
          this.$http.delete('/api/clerk/del/' + val.id)
            .then(() => {
              this.searchStaffs();
              this.$message.success('删除成功');
            }).catch((error) => {
            this.$message.error(error.response.data.message);
          });
        }).catch(() => {
          this.$message({
            type: 'info',
            message: '已取消删除'
          });
        });
        this.verifyInfo();
      },

      haveAuth(id) {
        let temp = this.$store.getters.getAuth;
        if (!temp.includes(id)) {
          this.$emit('logout');
          this.$store.commit('clear');
          this.$router.push('/login');
        }
      },
    },

    beforeMount() {
      this.verifyInfo();
      this.searchStaffs();
    },

    watch: {
      sortBy() {
        this.searchStaffs();
      },
      desc() {
        this.searchStaffs();
      },
      sizes() {
        this.searchStaffs();
      },
      page() {
        this.searchStaffs();
      },
      key() {
        this.searchStaffs();
      },
    }
  }
</script>

<style scoped>

  @import "../assets/myStyle.css";

  .avatar-uploader {
    border: 1px dashed #d9d9d9;
    border-radius: 6px;
    cursor: pointer;
    position: relative;
    overflow: hidden;
  }

  .avatar-uploader:hover {
    border-color: #409EFF;
  }

  .avatar-uploader-icon {
    font-size: 28px;
    color: #8c939d;
    width: 100px;
    height: 100px;
    line-height: 100px;
    text-align: center;
  }

  .avatar {
    width: 100px;
    height: 100px;
    display: block;
  }

</style>
