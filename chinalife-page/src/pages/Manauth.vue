<template>
  <div style="position: relative">
    <el-row>
      <el-col :span="10">
        <!--授权人员表-->
        <el-card class="clerk-manauth-card">
          <el-table :data="clerks" style="width: 100%">
            <el-table-column
              label="工号"
              prop="id">
            </el-table-column>
            <el-table-column
              label="姓名"
              prop="name">
            </el-table-column>
            <el-table-column
              label="机构号"
              prop="instiution">
            </el-table-column>
            <el-table-column
              align="right">
              <template slot="header" slot-scope="scope">
                <el-input
                  v-model="key"
                  width="10px"
                  placeholder="搜索"/>
              </template>
              <template slot-scope="scope">
                <el-button
                  type="primary"
                  class="green-button"
                  size="mini"
                  @click="findAuths(scope.row.id,scope.row.name)"
                >编辑
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
      <el-col :span="7">
        <!--已有权限表-->
        <el-card class="auth-have">
          <div slot="header">
            {{name}} <span>已有权限</span>
          </div>
          <el-tree
            ref="have"
            :data="authHave"
            show-checkbox
            node-key="id"
            :props="defaultProps">
          </el-tree>
          <el-button
            @click="deleteCheckedNodes"
            type="primary"
            size=""
            class="yellow-button"
            style="width: 100px;">
            解除权限
          </el-button>
        </el-card>
      </el-col>
      <el-col :span="7">
        <!--未有权限表-->
        <el-card class="auth-without">
          <div slot="header" class="clearfix">
            {{name}} <span>未有权限</span>
          </div>
          <el-tree
            ref="without"
            :data="authWithout"
            show-checkbox
            node-key="id"
            :props="defaultProps">
          </el-tree>
          <el-button
            @click="addCheckedNodes"
            type="primary"
            class="green-button"
            style="width: 100px;">
            增添权限
          </el-button>
        </el-card>
      </el-col>
    </el-row>

    <!--授权记录-->
    <el-card class="auth-record">
      <div slot="header">
        <span>授权记录</span>
        <div style="width: 200px;float: right;margin-top: 10px;margin-right: 10px">
          <el-input
            v-model="recordKey"
            size="mini"
            placeholder="输入关键字搜索"/>
        </div>
        <el-table
          :data="authRecords"
          @sort-change="sortChange"
        >
          <el-table-column
            prop="clerkId"
            label="员工工号"
            sortable="custom"
          >
          </el-table-column>
          <el-table-column
            prop="authAlias"
            label="权限名称"
          >
          </el-table-column>
          <el-table-column
            prop="time"
            label="授权时间"
            sortable="custom"
          >
          </el-table-column>
        </el-table>
      </div>
      <el-pagination
        small
        :page-sizes="[5,10,15]"
        :page-size="5"
        layout="total, sizes, prev, pager, next,->,jumper"
        :total="total"
        :pager-count="7"
        @size-change="pageSizeChange"
        @current-change="pageChange"
      >
      </el-pagination>
    </el-card>




  </div>
</template>

<script>
  export default {
    name: "Manauth",
    data() {
      return {
        clerks: [],     //员工
        key: '',        //员工表搜索关键字
        authHave: [],
        authWithout: [],
        add: [],
        delete: [],
        id: '',      //员工工号
        form: {},
        name:'',

        authRecords: [],
        page: '',
        sizes: '',
        sortBy: '',
        recordKey: '',
        desc: false,
        total: 0,

        defaultProps: {
          children: 'auths',
          label: 'alias',
        }
      }
    },

    methods: {
      verifyInfo() {
        this.$http.get('/api/manauth/verify')
          .then((res) => {
            this.$store.commit('updateInfo', res.data);
            this.haveAuth(3);
          }).catch((error) => {
          this.$message.warning(error.response.data.message);
          this.$store.commit('clear');
          this.$router.push('/login');
        });
      },
      search() {
        this.$http.get('/api/search/clerk/page', {
          params: {
            key: this.key
          }
        }).then((res) => {
          this.clerks = res.data.data;
        });
        this.verifyInfo()
      },
      findAuths(id,name) {
        this.name = name;
        this.id = id;
        this.$http.get('/api/manauth/choose/' + id)
          .then((res) => {
            this.authHave = res.data.hava;
            this.authWithout = res.data.without;
          });
        this.verifyInfo()
      },
      deleteCheckedNodes() {
        let temp = this.$refs.have.getCheckedNodes();
        if (temp.length != 0) {
          for (let i = 0; i < temp.length; i++) {
            if (temp[i].auths !== undefined) {
              continue;
            }
            this.delete.push(temp[i].id);
            /*this.form.ids.push(temp[i].id);*/
          }
          this.form.id = this.id;
          this.form.ids = this.delete.map(c => c).join(",");
          this.delete = [];
          this.$http.post('/api/manauth/del', this.$qs.stringify(this.form))
            .then(() => {
              this.$message.success('操作成功');
              this.findAuths(this.id);
            }).catch((error) => {
            this.$message.error(error.response.data.message);
          })
        }
        ;
        this.verifyInfo()
      },
      addCheckedNodes() {
        let temp = this.$refs.without.getCheckedNodes();
        if (temp.length != 0) {
          for (let i = 0; i < temp.length; i++) {
            if (temp[i].auths !== undefined) {
              continue;
            }
            this.add.push(temp[i].id);
          }
          this.form.id = this.id;
          this.form.ids = this.add.map(c => c).join(",");
          this.add = [];
          this.$http.post('/api/manauth/add', this.$qs.stringify(this.form))
            .then(() => {
              this.$message.success('操作成功');
              this.findAuths(this.id);
            }).catch((error) => {
            this.$message.error(error.response.data.message);
          })
        }
        ;
        this.verifyInfo()
      },

      haveAuth(id) {
        let temp = this.$store.getters.getAuth;
        if (!temp.includes(id)) {
          this.$emit('logout');
          this.$store.commit('clear');
          this.$router.push('/login');
        }
      },

      findRecords() {
        this.$http.get('/api/manauth/page', {
          params: {
            page: this.page,
            rows: this.sizes,
            sortBy: this.sortBy,
            desc: this.desc,
            key: this.recordKey,
          }
        }).then((res) => {
          this.total = res.data.total;
          this.authRecords = res.data.data;
        });
        this.verifyInfo()
      },
      sortChange(date) {
        this.desc = date.order === 'descending' ? true : false;
        this.sortBy = date.prop === 'clerkId' ? 'clerk_id' : date.prop;
      },
      pageSizeChange(size) {
        this.sizes = size;
      },
      pageChange(page) {
        this.page = page;
      }
    },

    watch: {
      key() {
        this.search();
      },
      desc() {
        this.findRecords();
      },
      sortBy() {
        this.findRecords();
      },
      sizes() {
        this.findRecords();
      },
      page() {
        this.findRecords();
      },
      recordKey() {
        this.findRecords();
      }
    },

    beforeMount() {
      this.verifyInfo();
      this.search();
      this.findRecords();
    },
  }
</script>

<style scoped>

  @import url('../assets/myStyle.css');

  .clerk-manauth-card {
    width: 600px;
    float: left;
  }

  .auth-have,
  .auth-without {
    width: 400px;
    margin-left: 20px;
    float: left;
  }

  .auth-record {
    width: 600px;
    position: absolute;
    top: 400px;
    left: 0;

  }

</style>
