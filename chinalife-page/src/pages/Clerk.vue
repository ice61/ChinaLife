<template>
  <div style="position: relative">
    <!--个人信息卡片-->
    <el-card class="clerkInfo-card">
      <div style="padding-left: 12px">
        <el-image fit="fill" :src="clerkInfo.image"
                  style="width: 100px;height: 100px; margin-left: 100px;border-radius: 50%">
        </el-image>
        <table style="width: 450px;margin-top: 15px">
          <tr>
            <td>工号：</td>
            <td v-text="clerkInfo.id"></td>
          </tr>
          <tr>
            <td>机构号：</td>
            <td v-text="clerkInfo.instiution"></td>
          </tr>
          <tr>
            <td>姓名：</td>
            <td v-text="clerkInfo.name"></td>
          </tr>
          <tr>
            <td>电话：</td>
            <td v-text="clerkInfo.phone"></td>
          </tr>
          <tr>
            <td>生日：</td>
            <td v-text="clerkInfo.birthday"></td>
          </tr>
          <tr>
            <td>创建日期：</td>
            <td v-text="clerkInfo.created"></td>
          </tr>
          <tr>
            <td>改密日期：</td>
            <td v-text="clerkInfo.updated"></td>
          </tr>
        </table>
        <el-button
          type="primary"
          @click="clerkInfoShow=true"
          class="green-button"
          style="width: 280px;margin-top: 20px;margin-left: 10px">
          编辑
        </el-button>
      </div>

    </el-card>

    <!--修改个人信息弹窗-->
    <el-dialog title="修改个人信息" :visible.sync="clerkInfoShow" :before-close="infoClose">
      <el-form ref="clerkInfo" :model="clerkInfo" label-width="150px" labelPosition="left" style="margin-left: 150px">
        <el-form-item label="工号：">
          <el-input v-model="clerkInfo.id" :disabled="true" style="width: 400px" ></el-input>
        </el-form-item>
        <el-form-item label="姓名：">
          <el-input v-model="clerkInfo.name" :disabled="true" style="width: 400px"></el-input>
        </el-form-item>
        <el-form-item label="机构号：">
          <el-input v-model="clerkInfo.instiution" style="width: 400px"></el-input>
        </el-form-item>
        <el-form-item label="生日：">
          <el-date-picker
            v-model="clerkInfo.birthday"
            type="date"
            placeholder="选择日期"
            format="yyyy-MM-dd"
            value-format="yyyy-MM-dd"
            style="width: 400px">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="电话：">
          <el-input v-model.number="clerkInfo.phone" style="width: 400px"></el-input>
        </el-form-item>
        <el-button type="primary" @click="update" style="width: 300px;margin-left: 140px" class="green-button">保存
        </el-button>
      </el-form>
    </el-dialog>

    <!--个人表单表格-->
    <el-card style="margin-top: 30px">
      <el-input
        v-model="key"
        style="width: 200px"
        placeholder="搜索"/>
      <el-button
        type="primary"
        @click="show"
        class="green-button"
        style="float: right;margin-right: 25px">
        填写保单
      </el-button>
      <el-table
        ref="insTable"
        :data="insuranceTable"
        @sort-change="sortChange"
      >
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

    <!--新增保单弹窗-->
    <el-dialog title="新增保单" :visible.sync="addShow">
      <el-form ref="clerkInfo" :model="insurance" label-width="150px" labelPosition="left" style="margin-left: 150px">

        <el-form-item label="身份证号码：">
          <el-input v-model="insurance.clientId" style="width: 400px"></el-input>
        </el-form-item>

        <el-form-item label="姓名：">
          <el-input v-model="insurance.clientName" style="width: 400px"></el-input>
        </el-form-item>

        <el-form-item label="性别：">
          <el-radio v-model="insurance.sex" label=true>男</el-radio>
          <el-radio v-model="insurance.sex" label=false>女</el-radio>
        </el-form-item>

        <el-form-item label="电话号码：">
          <el-input v-model.number="insurance.phone" style="width: 400px"></el-input>
        </el-form-item>

        <el-form-item label="生日：">
          <el-date-picker
            v-model="insurance.birthday"
            type="date"
            placeholder="选择日期"
            format="yyyy-MM-dd"
            value-format="yyyy-MM-dd"
            style="width: 400px">
          </el-date-picker>
        </el-form-item>

        <el-form-item label="保险种类：">
          <el-select v-model="insurance.sort" placeholder="请选择" style="width: 400px">
            <el-option
              label="分红险"
              value="分红险">
            </el-option>
            <el-option
              label="补充医疗险"
              value="补充医疗险">
            </el-option>
            <el-option
              label="旅游险"
              value="旅游险">
            </el-option>
            <el-option
              label="健康险"
              value="健康险">
            </el-option>
            <el-option
              label="财产险"
              value="财产险">
            </el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="保金：">
          <el-input v-model.number="insurance.money" style="width: 400px"></el-input>
        </el-form-item>

        <el-form-item label="被保人1身份证：">
          <el-input v-model="insurance.clientfId" style="width: 400px"></el-input>
        </el-form-item>
        <el-form-item label="被保人1姓名：">
          <el-input v-model="insurance.clientfName" style="width: 400px"></el-input>
        </el-form-item>
        <el-form-item label="被保人2身份证：">
          <el-input v-model="insurance.clientsId" style="width: 400px"></el-input>
        </el-form-item>
        <el-form-item label="被保人2姓名：">
          <el-input v-model="insurance.clientsName" style="width: 400px"></el-input>
        </el-form-item>
        <el-button type="primary" class="green-button" style="width: 200px;margin-left: 180px" @click="add">提交
        </el-button>
      </el-form>
    </el-dialog>

    <el-card class="pick" shadow="hover">
      <el-select v-model="interval" placeholder="请选择" style="margin-left: 35px">
        <el-option
          label="天"
          value="1">
        </el-option>
        <el-option
          label="周"
          value="7">
        </el-option>
        <el-option
          label="月"
          value="31">
        </el-option>
        <el-option
          label="年"
          value="365">
        </el-option>
      </el-select>
      <el-date-picker
        v-model="from"
        align="right"
        type="date"
        placeholder="选择起始日期"
        value-format="yyyy-MM-dd">
      </el-date-picker>
      <el-date-picker
        v-model="to"
        align="right"
        type="date"
        placeholder="选择终止日期"
        value-format="yyyy-MM-dd">
      </el-date-picker>
    </el-card>
    <div id="sum"></div>

  </div>


</template>

<script>
  import echarts from 'echarts'

  export default {
    name: "Clerk",
    data() {
      return {
        clerkInfoShow: false,
        clerkInfo: {},

        insuranceTable: [],
        key: '',
        page: '',
        sizes: '',
        sortBy: '',
        desc: false,
        total: 0,

        addShow: false,

        insurance: {},

        sumXData: [],
        sumData: [],
        from: '',
        to: '',
        interval: '',

      }
    },
    methods: {

      add() {
        let params = this.insurance;
        params.image = "???";
        this.insurance = {};
        this.$http.post('/api/insurance/add', this.$qs.stringify(params))
          .then((res) => {
            this.$message.success('添加成功');
            this.searchIns();
            this.addShow = false;
          }).catch((error) => {
          this.$message.error(error.response.data.message);
        });
        //this.verifyInfo();
      },

      show() {
        this.insurance = {},
          this.addShow = true;
        this.verifyInfo();
      },

      infoClose(done) {
        this.clerkInfo = JSON.parse(JSON.stringify(this.$store.getters.getInfo));
        done();
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

      verifyInfo() {
        this.$http.get('/api/manauth/verify')
          .then((res) => {
            this.$store.commit('updateInfo', res.data);
            this.clerkInfo = JSON.parse(JSON.stringify(this.$store.getters.getInfo));
            this.haveAuth(1);
          }).catch((error) => {
          this.$message.warning(error.response.data.message);
          this.$store.commit('clear');
          this.$router.push('/login');
        })
      },

      update() {
        let param = this.clerkInfo;
        this.$http.put('/api/clerk/update/person', param)
          .then(() => {
            this.$message.success('修改成功');
            this.addShow = false;
            this.clerkInfoShow = false;
            this.verifyInfo();
          })
      },

      searchIns() {
        this.$http.get('/api/search/insurance/page/person', {
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

      getSum() {
        this.sumXData = [];
        this.sumData = [];
        this.$http.get('/api/search/score/person', {
          params: {
            from: this.from,
            to: this.to,
            interval: this.interval
          }
        }).then((res) => {
          let temp = res.data;
          for (let i in temp) {
            this.sumXData.push(i);
            this.sumData.push(temp[i]);
          }
        }).catch((error) => {
          this.$message.error(error.response.data.message);
        });
        this.verifyInfo();
      },

      drawSum() {
        this.sum = echarts.init(document.getElementById('sum'));
        this.sum.setOption({
          title: {
            text: '销量统计',
            x: 'center'
          },
          tooltip: {
            trigger: 'axis',
          },
          xAxis: [
            {
              type: 'category',
              data: this.sumXData,
            }
          ],
          yAxis: [
            {
              type: 'value',
              name: '销量',
            }
          ],
          series: [
            {
              name: '销量',
              type: 'bar',
              data: this.sumData,
              itemStyle: {
                normal: {
                  color: function () {
                    return '#B5C334';
                  },
                }
              },
            },
            {
              name: '销量',
              type: 'line',
              data: this.sumData,
              itemStyle: {
                normal: {
                  color: '#60C0DD'
                }
              },
            },
          ]
        })
      }


    },
    beforeMount() {
      this.verifyInfo();
      this.searchIns();
      this.getSum();

    },

    /*mounted() {
      this.$nextTick(()=> {
        this.getSum();
      })
    },*/

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
      sumData: {
        deep: true,
        handler() {
          this.drawSum();
        }
      },
      interval() {
        this.getSum();
      },
      from() {
        this.getSum();
      },
      to() {
        this.getSum();
      },
    }
  }


</script>

<style scoped>

  @import url('../assets/myStyle.css');

  .clerkInfo-card {
    width: 380px;
    height: 430px;
    /*text-align: center;*/
    margin-left: 100px;
  }

  #sum {
    width: 1000px;
    height: 340px;
    position: absolute;
    top: 90px;
    right: 100px;
  }

  .pick {
    width: 800px;
    position: absolute;
    top: 0px;
    right: 200px;
    background-color: #f7f7f9;
  }

</style>
