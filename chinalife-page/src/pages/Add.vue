<!--
<template>
  <div>
    <el-button type="danger" @click="addLot">批量新增</el-button>
    <el-button type="primary" @click="tt">测试</el-button>
  </div>

</template>

<script>
  export default {
    name: "Add",
    data() {
      return {
        clients: [
          {name: '李健民', sex: true, birthday: '1961-03-25', id: '330724196103250014', phone: '18835522111'},
          {name: '李江海', sex: true, birthday: '1973-08-29', id: '33072219730829643X', phone: '18751993999'},
          {name: '李杰新', sex: true, birthday: '1960-02-06', id: '330722196002062134', phone: '13728957788'},
          {name: '李金暖', sex: false, birthday: '1951-04-09', id: '33072219510409671X', phone: '13103710555'},
          {name: '应志远', sex: true, birthday: '1977-02-20', id: '330722197702204513', phone: '13091020123'},
          {name: '应志军', sex: true, birthday: '1972-01-06', id: '330722197201063417', phone: '13824361777'},
          {name: '应正时', sex: true, birthday: '1957-03-10', id: '330722195703104910', phone: '18761677888'},
          {name: '应跃', sex: false, birthday: '1982-09-16', id: '330722198209162810', phone: '18603310123'},
          {name: '陈东林', sex: true, birthday: '1963-12-27', id: '330721196312274419', phone: '15819118899'},
          {name: '陈东川', sex: false, birthday: '1976-10-25', id: '330726197610252935', phone: '17661555551'},
        ],
        client1s: [
          {name: '王志惠', sex: false, birthday: '1975-12-30', id: '320222197512307598', phone: '13193012222'},
          {name: '王志平', sex: true, birthday: '1978-01-08', id: '330721197801085410', phone: '13883818381'},
          {name: '王志燕', sex: false, birthday: '1968-03-04', id: '330725196803040057', phone: '15610677770'},
          {name: '王志泽', sex: true, birthday: '1953-04-16', id: '330721195304162615', phone: '13193012222'},
          {name: '王中法', sex: true, birthday: '1969-09-07', id: '332624196909074431', phone: '13883113777'},
          {name: '王忠', sex: true, birthday: '1968-10-08', id: '360211196810085915', phone: '18883368168'},
          {name: '葛明明', sex: false, birthday: '1981-10-14', id: '339005198110144818', phone: '15610677773'},
          {name: '葛天军', sex: true, birthday: '1964-02-11', id: '330724196402111014', phone: '13983457000'},
          {name: '葛世美', sex: false, birthday: '1949-07-11', id: '33072419490711561X', phone: '13896669677'},
          {name: '程艳敏', sex: false, birthday: '1974-10-20', id: '330722197410200027', phone: '18523669900'},
        ],
        client2s: [
          {name: '程烨', sex: true, birthday: '1978-11-24', id: '612301197811242717', phone: '18320725999'},
          {name: '程永广', sex: true, birthday: '1966-05-29', id: '330722196605295914', phone: '18702095678'},
          {name: '朱爱琴', sex: false, birthday: '1951-05-09', id: '330722195105090424', phone: '13610068688'},
          {name: '周竹芳', sex: false, birthday: '1972-02-09', id: '330727197202094428', phone: '13533588668'},
          {name: '朱爱仙', sex: false, birthday: '1963-10-07', id: '330702196310070827', phone: '13609772888'},
          {name: '朱炳进', sex: true, birthday: '1969-05-11', id: '330722196905111216', phone: '19802062777'},
          {name: '孙伟戟', sex: true, birthday: '1961-08-22', id: 'A3072519610822411X', phone: '13640888883'},
          {name: '孙霞', sex: false, birthday: '1976-01-28', id: '330702197601286024', phone: '13143333194'},
          {name: '程雄建', sex: true, birthday: '1977-10-05', id: '330722197710053612', phone: '13144449341'},
          {name: '陈东东', sex: true, birthday: '1978-12-15', id: '330724197812150715', phone: '18200889998'},
        ],
        sort: [
          "分红险",
          "补充医疗险",
          "旅游险",
          "健康险",
          "财产险",
        ],
        year: ['2016', '2017', '2018'],
        month: ['01', '02', '03', '04', '05', '06', '07', '08', '09', '10', '11', '12'],
        day: [
          '01', '02', '03', '04', '05', '06', '07', '08', '09', '10',
          '11', '12', '13', '14', '15', '16', '17', '18', '19', '20',
          '21', '22', '23', '24', '25', '26', '27'
        ],
        hour: [
          '00', '01', '02', '03', '04', '05', '06', '07', '08', '09', '10',
          '11', '12', '13', '14', '15', '16', '17', '18', '19', '20',
          '21', '22', '23',
        ],
        min: [
          '00', '01', '02', '03', '04', '05', '06', '07', '08', '09', '10',
          '11', '12', '13', '14', '15', '16', '17', '18', '19', '20',
          '21', '22', '23', '24', '25', '26', '27', '28', '29', '30',
          '31', '32', '33', '34', '35', '36', '37', '38', '39', '40',
          '41', '42', '43', '44', '45', '46', '47', '48', '49', '50',
          '51', '52', '53', '54', '55', '56', '57', '58', '59'
        ],
        sec: [
          '00', '01', '02', '03', '04', '05', '06', '07', '08', '09', '10',
          '11', '12', '13', '14', '15', '16', '17', '18', '19', '20',
          '21', '22', '23', '24', '25', '26', '27', '28', '29', '30',
          '31', '32', '33', '34', '35', '36', '37', '38', '39', '40',
          '41', '42', '43', '44', '45', '46', '47', '48', '49', '50',
          '51', '52', '53', '54', '55', '56', '57', '58', '59'
        ],

        ins: {}

      }
    },
    methods: {
      tt() {
          let t = new Date().getTime();
          let newt = t + 1000;
          while (new Date().getTime() < newt) {}
          return;
      },

      addLot() {
        for (let i = 0; i < 10; i++) {
          let clMain = this.clients[Math.floor(Math.random() * this.clients.length)];
          this.ins.clientId = clMain.id;
          this.ins.clientName = clMain.name;
          this.ins.sex = clMain.sex;
          this.ins.phone = clMain.phone;
          this.ins.birthday = clMain.birthday;

          let cl1 = this.client1s[Math.floor(Math.random() * this.client1s.length)];
          this.ins.clientfId = cl1.id;
          this.ins.clientfName = cl1.name;

          let cl2 = this.client2s[Math.floor(Math.random() * this.client2s.length)];
          this.ins.clientsId = cl2.id;
          this.ins.clientsName = cl2.name;

          this.ins.sort = this.sort[Math.floor(Math.random() * this.sort.length)];
          this.ins.money = Math.floor(Math.random() * 10000 + 500);
          this.ins.image = "????";

          this.ins.created =
            this.year[Math.floor(Math.random() * this.year.length)] + '-'
            + this.month[Math.floor(Math.random() * this.month.length)] + '-'
            + this.day[Math.floor(Math.random() * this.day.length)] + ' '
            + this.hour[Math.floor(Math.random() * this.hour.length)] + ':'
            + this.min[Math.floor(Math.random() * this.min.length)] + ':'
            + this.sec[Math.floor(Math.random() * this.sec.length)];

          //console.log(this.ins);

          let params = this.ins;
          this.ins = {};
          this.$http.post('/api/insurance/add', this.$qs.stringify(params))
            .then((res) => {
              this.$message.success('添加成功');
            }).catch((error) => {
            this.$message.error(error.response.data.message);
          });

          this.tt();
        }


      },
    }

  }
</script>

<style scoped>

</style>
-->
