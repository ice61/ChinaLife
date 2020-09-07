<template>
  <div>
    <el-card>
      <el-card class="check">
        <el-select v-model="interval" placeholder="请选择时间间隔">
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
        <el-select v-model="rankdesc" placeholder="请选择排行顺序">
          <el-option
            label="降序"
            value=true>
          </el-option>
          <el-option
            label="升序"
            value=false>
          </el-option>
        </el-select>
        <el-input placeholder="请输入统计人数" style="width: 217px" v-model="ranknum"></el-input>
        <el-date-picker
          v-model="from"
          align="right"
          type="date"
          placeholder="请选择起始日期"
          value-format="yyyy-MM-dd">
        </el-date-picker>
        <el-date-picker
          v-model="to"
          align="right"
          type="date"
          placeholder="请选择终止日期"
          value-format="yyyy-MM-dd">
        </el-date-picker>
      </el-card>
      <div id="rank"></div>
      <div id="sort"></div>
      <div id="sum"></div>
    </el-card>


  </div>

</template>

<script>
  import echarts from 'echarts'

  export default {
    name: "Chart",

    data() {
      return {

        rank: '',
        rankXData: [],
        rankData: [],
        from: '',
        to: '',
        ranknum: '',
        rankdesc: '',
        desc: '',

        sort: '',
        sortXData: [],
        sortData: [],

        sum: '',
        sumXData: [],
        sumData: [],
        interval: '',

      }
    },

    methods: {
      getRank() {
        this.$http.get('/api/search/score/rank', {
          params: {
            from: this.from,
            to: this.to,
            num: this.ranknum,
            desc: this.rankdesc
          }
        }).then((res) => {
          let data = res.data;
          let x = [];
          let d = [];
          for (let d1 in data) {
            let temp = data[d1];
            for (let i in temp) {

              x.push(i.toString());
              d.push(temp[i]);
            }
          }
          this.rankXData = x;
          this.rankData = d;
        }).catch((error) => {
          this.$message.error(error.response.data.message());
        });
        /*console.log(this.rankXData);
        console.log(this.rankData);*/
        this.verifyInfo();
      },

      drawRank() {
        this.rank = echarts.init(document.getElementById('rank'));
        this.rank.setOption({
          title: {
            x: 'center',
            text: '员工业绩排行',
          },
          tooltip: {
            trigger: 'axis'
          },
          xAxis: [
            {
              name: '工号',
              type: 'category',
              show: true,
              data: this.rankXData,
            }
          ],
          yAxis: [
            {
              type: 'value',
              show: true,
              name: '业绩'
            }
          ],
          series: [
            {
              name: '销售量',
              data: this.rankData,
              type: 'bar',
              itemStyle: {
                normal: {
                  color: function (params) {
                    // build a color map as your need.
                    var colorList = [
                      '#C1232B', '#B5C334', '#FCCE10', '#E87C25', '#27727B',
                      '#FE8463', '#9BCA63', '#FAD860', '#F3A43B', '#60C0DD',
                      '#D7504B', '#C6E579', '#F4E001', '#F0805A', '#26C0C0'
                    ];
                    return colorList[params.dataIndex];
                  }
                }
              },
            }
          ]
        });
      },

      getSort() {
        this.sortXData = [];
        this.sortData = [];
        this.$http.get('/api/search/sort/all', {
          params: {
            from: this.from,
            to: this.to,
          }
        }).then((res) => {
          let temp = res.data;
          for (let i in temp) {
            this.sortXData.push(i);
            let obj = {};
            obj.value = temp[i];
            obj.name = i;
            this.sortData.push(obj);
          }
        }).catch((error) => {
          this.$message.error(error.response.data.message);
        });
        this.verifyInfo();
      },

      drawSort() {
        this.sort = echarts.init(document.getElementById('sort'));
        this.sort.setOption({
          title: {
            text: '售出险种比例',
            x: 'center'
          },
          tooltip: {
            trigger: 'item',
          },
          legend: {
            orient: 'vertical',
            x: 'left',
            data: this.sortXData,
          },
          calculable: true,
          series: [
            {
              name: '保险种类',
              type: 'pie',
              radius: '55%',
              center: ['50%', '60%'],
              data: this.sortData,
            }
          ]
        })
      },

      getSum() {
        this.sumXData = [];
        this.sumData = [];
        this.$http.get('/api/search/score/all', {
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
      },

      verifyInfo() {
        this.$http.get('/api/manauth/verify')
          .then((res) => {
            this.$store.commit('updateInfo', res.data);
            this.haveAuth(7);
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

    watch: {
      rankXData: {
        deep: true,
        handler() {
          this.drawRank();
        }
      },
      rankData: {
        deep: true,
        handler() {
          this.drawRank();
        }
      },
      sortXData: {
        deep: true,
        handler() {
          this.drawSort();
        }
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
      rankdesc() {
        this.getRank();
      },
      from() {
        this.getRank();
        this.getSort();
        this.getSum();
      },
      to() {
        this.getRank();
        this.getSort();
        this.getSum();
      },
      ranknum() {
        this.getRank();
      }
    },

    beforeMount() {
      this.verifyInfo();
    },

    mounted() {
      this.$nextTick(() => {
        this.getRank();
        this.getSort();
        this.getSum();
      })
    }
  }
</script>

<style scoped>

  #rank,
  #sort {
    width: 40%;
    height: 400px;
    display: inline-block;
    margin-left: 40px;
    margin-top: 30px;
  }

  #sum {
    width: 80%;
    height: 400px;
    margin-left: 40px;
  }

  .check {
    width: 75%;
    margin-left: 215px;
  }
</style>
