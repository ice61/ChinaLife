<template>
  <div style="width: 90%;margin-left: 50px;margin-top: 30px;">

    <!--保单详情弹窗-->
    <el-dialog title="保单详情" :visible.sync="detailShow">
      <el-tabs tab-position="left" active-name="first">
        <el-tab-pane label="投保人信息" name="first">
          <table style="width: 500px;margin-left: 100px">
            <tr>
              <td>姓名：</td>
              <td>{{clientInformation.name}}</td>
            </tr>
            <tr>
              <td>身份证号:</td>
              <td>{{clientInformation.id}}</td>
            </tr>
            <tr>
              <td>性别：</td>
              <td>{{clientInformation.sex}}</td>
            </tr>
            <tr>
              <td>生日：</td>
              <td>{{clientInformation.birthday}}</td>
            </tr>
            <tr>
              <td>电话：</td>
              <td>{{clientInformation.phone}}</td>
            </tr>
            <tr>
              <td>最后交易时间：</td>
              <td>{{clientInformation.update}}</td>
            </tr>
          </table>
        </el-tab-pane>
        <el-tab-pane label="被保人信息" name="second">
          <table style="width: 500px;margin-left: 100px;margin-top: 20px">
            <tr>
              <td>被保人1身份证号：</td>
              <td>{{insuranceInformation.clientfId}}</td>
            </tr>
            <tr>
              <td>被保人1姓名：</td>
              <td>{{insuranceInformation.clientfName}}</td>
            </tr>
            <tr>
              <td>被保人2身份证号：</td>
              <td>{{insuranceInformation.clientsId}}</td>
            </tr>
            <tr>
              <td>被保人2姓名：</td>
              <td>{{insuranceInformation.clientsName}}</td>
            </tr>
          </table>
        </el-tab-pane>
        <el-tab-pane label="保单详情" name="third">
          <table style="width: 500px;margin-left: 100px;margin-top: 15px">
            <tr>
              <td>保单号：</td>
              <td>{{insuranceInformation.orderId}}</td>
            </tr>
            <tr>
              <td>金额：</td>
              <td>{{insuranceInformation.money}}</td>
            </tr>
            <tr>
              <td>险种：</td>
              <td>{{insuranceInformation.sort}}</td>
            </tr>
            <tr>
              <td>出单时间：</td>
              <td>{{insuranceInformation.created}}</td>
            </tr>
            <tr>
              <td>修改时间：</td>
              <td>{{insuranceInformation.updated}}</td>
            </tr>
          </table>
        </el-tab-pane>
        <el-tab-pane label="销售员信息" name="fourth">
          <div style="padding-left: 12px;margin-left: 100px">
            <!--<el-image fit="fill" :src="clerkInformation.image"
                      style="width: 100px;height: 100px; margin-left: 100px;border-radius: 50%">
            </el-image>-->
            <img :src="clerkInformation.image" style="width: 80px;height: 80px;margin-left: 80px">
            <table style="width: 450px;margin-top: 5px">
              <tr>
                <td>工号：</td>
                <td v-text="clerkInformation.id"></td>
              </tr>
              <tr>
                <td>机构号：</td>
                <td v-text="clerkInformation.instiution"></td>
              </tr>
              <tr>
                <td>姓名：</td>
                <td v-text="clerkInformation.name"></td>
              </tr>
              <tr>
                <td>电话：</td>
                <td v-text="clerkInformation.phone"></td>
              </tr>
              <tr>
                <td>生日：</td>
                <td v-text="clerkInformation.birthday"></td>
              </tr>
              <tr>
                <td>创建日期：</td>
                <td v-text="clerkInformation.created"></td>
              </tr>
              <tr>
                <td>改密日期：</td>
                <td v-text="clerkInformation.updated"></td>
              </tr>
            </table>
          </div>
        </el-tab-pane>
      </el-tabs>
    </el-dialog>

    <!--保单编辑弹窗-->
    <el-dialog title="修改保单" :visible.sync="editShow">
      <el-form :model="newIns" label-width="150px" labelPosition="left" style="margin-left: 150px">
        <el-form-item label="保单号：">
          <el-input v-model="newIns.orderId" :disabled="true" style="width: 400px"></el-input>
        </el-form-item>
        <el-form-item label="投保人身份证号码：">
          <el-input v-model="newIns.clientId" :disabled="true" style="width: 400px"></el-input>
        </el-form-item>
        <el-form-item label="投保人姓名：">
          <el-input v-model="newIns.clientName" :disabled="true" style="width: 400px"></el-input>
        </el-form-item>
        <el-form-item label="投保人性别：">
          <el-input v-model="newIns.sex" :disabled="true" style="width: 400px"></el-input>
        </el-form-item>
        <el-form-item label="投保人电话：">
          <el-input v-model.number="newIns.phone" style="width: 400px"></el-input>
        </el-form-item>
        <el-form-item label="投保人生日：">
          <el-date-picker
            v-model="newIns.birthday"
            type="date"
            placeholder="选择日期"
            format="yyyy-MM-dd"
            :disabled="true"
            value-format="yyyy-MM-dd"
            style="width: 400px">
          </el-date-picker>
        </el-form-item>
      </el-form>
      <el-button
        type="primary"
        @click="saveEdit"
        style="width: 200px;margin-left: 350px"
        class="green-button">
        保存
      </el-button>
    </el-dialog>

    <!--表格-->
    <el-card>
      <el-button
        type="danger"
        @click="handleClick"
        class="yellow-button">批量删除
      </el-button>
      <el-table
        ref="insTable"
        :data="insuranceTable"
        @selection-change="checkMore"
        @sort-change="sortChange"
      >
        <el-table-column
          type="selection"
          width="55">
        </el-table-column>

        <el-table-column
          prop="orderId"
          label="保单号"
          sortable="custom">
        </el-table-column>

        <el-table-column
          prop="clientId"
          label="投保人身份证"
          sortable="custom">
        </el-table-column>

        <el-table-column
          prop="clientName"
          label="投保人">
        </el-table-column>

        <el-table-column
          prop="created"
          label="出单时间"
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
              @click="handleSee(scope.row)">详情
            </el-button>
            <el-button
              size="mini"
              @click="handleEdit(scope.row)">编辑
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

    <!--个人视图-->
  </div>
</template>

<script>
  export default {
    name: "Insurance",
    data() {
      return {
        /*表格信息*/
        insuranceTable: [],
        checkedInsIds: '',

        /*表格搜索分页查询相关数据*/
        key: '',
        page: '',
        sizes: '',
        sortBy: '',
        desc: false,
        total: 0,

        newIns: {},
        editShow: false,

        /*详情信息*/
        clerkInformation: {},
        clientInformation: {},
        insuranceInformation: {},
        detailShow: false,
      }
    },

    methods: {
      verifyInfo() {
        this.$http.get('/api/manauth/verify')
          .then((res) => {
            this.$store.commit('updateInfo', res.data);
            this.haveAuth(2);
          }).catch((error) => {
          this.$message.warning(error.response.data.message);
          this.$store.commit('clear');
          this.$router.push('/login');
        });
      },

      haveAuth(id) {
        let temp = this.$store.getters.getAuth;
        if (!temp.includes(id)) {
          this.$emit('logout');
          this.$store.commit('clear');
          this.$router.push('/login');
        }
      },

      /*批量选中功能*/
      checkMore(val) {
        if (val.length !== 0) {
          this.checkedInsIds = '';
          this.checkedInsIds = val.map(c => c.orderId).join(",");
        }else {
          this.checkedInsIds='';
        }
        this.verifyInfo();
      },
      /*批量删除功能*/
      handleClick() {
        if (!this.checkedInsIds) {
          this.$message.warning('请勾选复选框');
        } else {
          this.$confirm('确认删除？', '提示', {
            type: 'warning',
          }).then(() => {
            let form = {};
            form.orderIds = this.checkedInsIds;
            this.$http.post('/api/insurance/delete', this.$qs.stringify(form)
            ).then(() => {
              this.checkedInsIds = '';
              this.searchIns();
              this.$message.success('删除成功');
            });
          }).catch(() => {
            this.$message({
              type: 'info',
              message: '已取消删除'
            });
          });
          this.verifyInfo();
        }
      },
      searchIns() {
        this.$http.get('/api/search/insurance/page', {
          params: {
            page: this.page,
            rows: this.sizes,
            sortBy: this.sortBy,
            desc: this.desc,
            key: this.key,
          },
        })
          .then((res) => {
            this.insuranceTable = res.data.data;
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
        this.$http.get('/api/search/client/' + val.clientId)
          .then((res) => {
            this.clientInformation = res.data;
          });
        this.$http.get('/api/search/clerk/one/' + val.clerkId)
          .then((res) => {
            this.clerkInformation = res.data;
          });
        this.insuranceInformation = val;
        this.detailShow = true;
        this.verifyInfo();
      },
      handleDelete(val) {
        this.$confirm('确认删除？', '提示', {
          type: 'warning',
        }).then(() => {
          let form = {};
          form.orderIds = val.orderId;
          this.$http.post('/api/insurance/delete', this.$qs.stringify(form)
          ).then(() => {
            this.searchIns();
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

      handleEdit(val) {
        this.newIns = val;
        this.editShow = true;
        this.verifyInfo();
      },

      saveEdit() {
        var param = this.newIns;
        param.sex = param.sex === '男' ? true : false;
        delete param.created;
        delete param.updated;
        this.$http.put('/api/insurance/update', param)
          .then((res) => {
            this.$message.success('修改成功');
            this.editShow = false;
          }).catch((error) => {
          this.$message.error(error.response.data.message);
        });
        this.verifyInfo();
      }

    },


    beforeMount() {
      this.verifyInfo();
      this.searchIns();
    },

    watch: {
      sortBy() {
        this.searchIns();
      },
      desc() {
        this.searchIns();
      },
      sizes() {
        this.searchIns();
      },
      page() {
        this.searchIns();
      },
      key() {
        this.searchIns();
      },
    }
  }
</script>

<style scoped>
  @import url('../assets/myStyle.css');
</style>
