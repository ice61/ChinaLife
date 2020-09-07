<template>
  <el-card>
    <el-table
      ref="clientTable"
      :data="clientTable"
      @sort-change="sortChange"
    >

      <el-table-column
        prop="id"
        label="身份证"
        sortable="custom">
      </el-table-column>

      <el-table-column
        prop="name"
        label="姓名">
      </el-table-column>

      <el-table-column
        prop="birthday"
        label="生日"
        sortable="custom">
      </el-table-column>

      <el-table-column
        prop="phone"
        label="电话">
      </el-table-column>

      <el-table-column
        prop="update"
        label="最近交易日期"
        sortable="custom">
      </el-table-column>

      <el-table-column
        align="right">
        <template slot="header" slot-scope="scope">
          <el-input
            v-model="key"
            size="mini"
            placeholder="输入身份证号或姓名"/>
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

</template>

<script>
  export default {
    name: "Client",
    data() {
      return {
        clientTable: [],
        /*表格搜索分页查询相关数据*/
        key: '',
        page: '',
        sizes: '',
        sortBy: '',
        desc: false,
        total: 0,
      };
    },
    methods: {
      searchClient() {
        this.$http.get('/api/search/client/page', {
          params: {
            page: this.page,
            rows: this.sizes,
            sortBy: this.sortBy,
            desc: this.desc,
            key: this.key,
          },
        })
          .then((res) => {
            this.clientTable = res.data.data;
            this.total = res.data.total;
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

      verifyInfo() {
        this.$http.get('/api/manauth/verify')
          .then((res) => {
            this.$store.commit('updateInfo', res.data);
            this.haveAuth(6);
          }).catch((error) => {
          this.$message.warning(error.response.data.message);
          this.$store.commit('clear');
          this.$router.push('/login');
        });
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

    },

    watch: {
      sortBy() {
        this.searchClient();
      },
      desc() {
        this.searchClient();
      },
      sizes() {
        this.searchClient();
      },
      page() {
        this.searchClient();
      },
      key() {
        this.searchClient();
      },
    },

    beforeMount() {
      this.verifyInfo();
      this.searchClient();
    }
  }
</script>

<style scoped>

</style>
