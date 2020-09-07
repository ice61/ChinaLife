<template>
  <div>
    <!--新建文件夹弹窗-->
    <el-dialog title="新建文件夹" :visible.sync="createShow">
      <el-form :model="newDir" label-width="150px" labelPosition="left" style="margin-left: 150px">
        <el-form-item label="文件夹名称：">
          <el-input v-model.number="newDir.name" style="width: 400px"></el-input>
        </el-form-item>
      </el-form>
      <el-button
        type="primary"
        @click="createDir"
        style="width: 200px;margin-left: 350px"
        class="green-button">
        创建
      </el-button>
    </el-dialog>

    <!--文件重命名弹窗-->
    <el-dialog title="修改保单" :visible.sync="renameShow">
      <el-form :model="renameFile" label-width="150px" labelPosition="left" style="margin-left: 150px">
        <el-form-item label="新文件名：">
          <el-input v-model.number="renameFile.newName" style="width: 400px"></el-input>
        </el-form-item>
      </el-form>
      <el-button
        type="primary"
        @click="rename"
        style="width: 200px;margin-left: 350px"
        class="green-button">
        保存
      </el-button>
    </el-dialog>

    <!--功能栏-->
    <el-card>
      <div style="overflow: hidden">
        <el-button
          icon="el-icon-refresh-left"
          type="primary"
          plain
          circle
          @click="returnPre"></el-button>
        <el-input
          style="width: 15%"
          v-model="key"
          size="primary"
          class="funRight"
          placeholder="输入关键字搜索"/>
        <el-switch
          style="height: 40px"
          v-model="isSearch"
          class="funRight"
          active-text="全文检索"
          inactive-text="普通浏览">
        </el-switch>
        <el-upload class="upload-demo funRight"
                   action
                   :limit="1"
                   :file-list="formFileList"
                   :http-request="handleUploadForm">
          <el-button
            type="primary"
            plain>上传文件
          </el-button>
        </el-upload>
        <el-button
          type="primary"
          plain
          class="funRight"
          @click="handleCreate()">新建文件夹
        </el-button>
      </div>
    </el-card>

    <!--表格-->
    <el-card style="margin-top: 1%">
      <el-breadcrumb separator-class="el-icon-arrow-right">
        <el-breadcrumb-item>
          <span @click="handleSkip(-1)">我的网盘</span>
        </el-breadcrumb-item>
        <el-breadcrumb-item v-for="(path,i) in pathStack" v-bind:key="i">
          <span v-text="path"@click="handleSkip(i)"></span>
        </el-breadcrumb-item>
      </el-breadcrumb>
      <el-table
        :data="fileTable"
        @sort-change="sortChange">
        <el-table-column
          prop="name"
          label="文件名">
          <template slot-scope="scope">
            <i v-show="scope.row.isdirectory == false" class="el-icon-s-order"></i>
            <i v-show="scope.row.isdirectory == true" class="el-icon-folder"></i>
            <span v-text="scope.row.name"></span>
          </template>
        </el-table-column>
        <el-table-column
          prop="pathName"
          label="全路径名"
          sortable="custom">
        </el-table-column>

        <el-table-column
          prop="create"
          label="创建日期"
          sortable="custom">
        </el-table-column>
        <el-table-column>
          <template slot="header" slot-scope="scope">

          </template>
          <template slot-scope="scope">
            <el-button
              v-if="scope.row.isdirectory == true"
              size="mini"
              type="primary"
              class="green-button"
              @click="handleSee(scope.row)">进入
            </el-button>
            <el-button
              v-if="scope.row.isdirectory == false"
              size="mini"
              type="primary"
              class="green-button"
              @click="handleDownload(scope.row)">下载
            </el-button>
            <el-button
              size="mini"
              @click="handleRename(scope.row)">重命名
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
  </div>

</template>

<script>
  export default {
    name: "Pan",
    data() {
      return {
        //表格和分页搜索的相关数据
        fileTable: [],
        key: '',
        page: '',
        sizes: '',
        sortBy: '',
        desc: false,
        total: 0,
        pathStack: [],
        isSearch: false,

        //新建文件夹
        newDir: {},
        createShow: false,

        //重命名
        renameFile: {},
        renameShow: false,

        //上传和下载所使用到的参数
        downFile: {},
        formFileList: [],
      }
    },
    methods: {

      returnPre() {
        this.pathStack.pop();
        this.listFiles();
      },

      getPath() {
        for (let i = 0; i < this.pathStack.length; i++)
          console.log(this.pathStack[i]);
        let path = '/';
        path += this.pathStack.map(c => c).join("/");
        return path;
      },

      handleSkip(val) {
        let i = this.pathStack.length - 1;
        while (i > val && i >= 0) {
          this.pathStack.pop();
          i--;
        }
        this.listFiles();
      },

      listFiles() {
        let path = this.getPath();
        this.$http.get('/api/pan/browse', {
          params: {
            directorName: path,
            page: this.page,
            rows: this.sizes,
            sortBy: this.sortBy,
            desc: this.desc,
          }
        }).then((res) => {
          this.fileTable = res.data.data;
          this.total = res.data.total;
        });
        this.verifyInfo();
      },

      searchFiles() {
        this.$http.get('/api/pan/searchfiles', {
          params: {
            page: this.page,
            rows: this.sizes,
            sortBy: this.sortBy,
            desc: this.desc,
            key: this.key,
          }
        }).then((res) => {
          this.fileTable = res.data.data;
          this.total = res.data.total;
        });
        this.verifyInfo();
      },

      updateFiles() {
        if (this.isSearch == true) {
          this.searchFiles();
        } else {
          this.listFiles();
        }
      },

      handleCreate() {
        this.newDir = {};
        this.createShow = true;
      },

      createDir() {
        let path = this.getPath();
        this.newDir.path = path;
        this.createShow = false;
        this.$http.post('/api/pan/mkdir', this.$qs.stringify(this.newDir)).then((res) => {
          this.listFiles();
          this.$message.success('删除成功');
        }).catch((error) => {
          this.$message.error(error.response.data.message);
        });
      },

      handleDelete(val) {
        this.$confirm('确认删除？', '提示', {
          type: 'warning',
        }).then(() => {
          let form = {};
          form.pathName = val.pathName;
          this.$http.post('/api/pan/delete', this.$qs.stringify(form)
          ).then(() => {
            this.$message.success('删除成功');
            this.updateFiles();
          }).catch((error) => {
            this.$message.error(error.response.data.message);
          });
        }).catch(() => {
          this.$message({
            type: 'info',
            message: '已取消删除'
          });
        });
      },

      handleRename(val) {
        this.renameFile = {};
        this.renameFile.oldPathName = val.pathName;
        this.renameShow = true;
      },

      rename() {
        this.$http.post('/api/pan/rename', this.$qs.stringify(this.renameFile)
        ).then((res) => {
          this.renameShow = false;
          this.$message.success('修改成功');
          this.updateFiles();
        }).catch((error) => {
          this.$message.error(error.response.data.message);
        });
      },

      handleSee(val) {
        this.pathStack = [];
        this.pathStack = val.pathName.split("/").filter(c => c != '');
        this.listFiles();
      },

      handleDownload(val) {
        this.downFile = {};
        this.downFile.pathName = val.pathName;
        this.$http.post('/api/pan/download', this.$qs.stringify(this.downFile), {
          responseType: 'blob'
        }).then((res) => {
          var blob = new Blob([res.data], {type: res.headers['content-type']});
          var downloadElement = document.createElement('a');
          var href = window.URL.createObjectURL(blob); //创建下载的链接
          downloadElement.href = href;
          downloadElement.download = val.name; //下载后文件名
          document.body.appendChild(downloadElement);
          downloadElement.click(); //点击下载
          document.body.removeChild(downloadElement); //下载完成移除元素
          window.URL.revokeObjectURL(href); //释放掉blob对象
        }).catch((error) => {
        });
      },

      handleUploadForm(param) {
        let path = this.getPath();
        this.formFileList = [];
        let formData = new FormData();
        formData.append('path', path); // 额外参数
        formData.append('file', param.file);
        let loading = this.$loading({
          target: document.querySelector(".el-container"),
          lock: true,
          text: '上传中，请稍候...',
          spinner: 'el-icon-loading',
          background: 'rgba(0, 0, 0, 0.7)'
        });
        this.$http.post('/api/pan/upload', formData)
          .then((res) => {
            loading.close();
            this.formFileList = [];
            this.listFiles();
            this.$message.success('文件上传成功');
          })
          .catch((error) => {
            loading.close();
            this.formFileList = [];
            this.$message.error(error.response.data.message);
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

      verifyInfo() {
        this.$http.get('/api/manauth/verify')
          .then((res) => {
            this.$store.commit('updateInfo', res.data);
            this.haveAuth(8);
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
    },

    beforeMount() {
      this.verifyInfo();
      this.listFiles();
    },
    watch: {
      sortBy() {
        this.updateFiles();
      },

      desc() {
        this.updateFiles();
      },

      sizes() {
        this.updateFiles();
      },

      page() {
        this.updateFiles();
      },

      key() {
        if (this.isSearch == true) {
          this.updateFiles();
        }
      },

      isSearch() {
        this.updateFiles();
      }
    }
  }
</script>

<style scoped>
  @import url('../assets/myStyle.css');

  .funRight {
    float: right;
    margin-right: 3%;
  }
</style>
