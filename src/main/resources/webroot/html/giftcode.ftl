<#include "header.ftl">
<#include "navbar.ftl">

<div class ="row top-buffer">
   <div class="col-sm-2">
         <input v-model="prefix" type="text" class="form-control" id="prefix" name="prefix" placeholder="Tiền tố">
   </div>
   <div class="col-sm-2">
         <input v-model="server" type="text" class="form-control" id="server" name="server" placeholder="Server">
   </div>
  <div class="float-left col-sm-4">
     <input v-model="rewardFormat" type="text" class="form-control" id="rewardFormat" name="rewardFormat"
     placeholder="reward format">
  </div>

</div>

<div class ="row top-buffer">
  <div class="col-sm-4">
     <input v-model="prefixStartDate" type="datetime-local" class="form-control" id="userEventStart" name="userEventStart"
     placeholder="Start Date">
  </div>
  <div class="col-sm-4">
     <input v-model="prefixEndDate" type="datetime-local" class="form-control" id="userEventEnd" name="userEventEnd"
     placeholder="End Date">
  </div>
  <div class="float-left col-sm-2">
     <button type="button" class="btn btn-primary w-100" v-on:click="addPrefix">Tạo tiền tố</button>
  </div>
</div>
<div class="row top-buffer">
  <table v-if="prefixes.length > 0" class="table table-bordered table-responsive-md table-striped text-center">
      <thead>
        <tr>
            <th>STT</th>
            <th>Tiền tố</th>
            <th>Gift đã tạo</th>
            <th>Hiệu lực từ</th>
            <th>Hiệu lực đến</th>
            <th>Quà</th>
            <th colspan="3">
                <input v-model="genCodeCnt" type="text" class="form-control" id="genCodeCnt" name="genCodeCnt"
                     placeholder="SL cần tạo">
            </th>
        </tr>
      </thead>
      <tbody>
        <tr class="pt-3-half" v-for="(pref,idx) in prefixes" :key="pref.id">
          <td>{{ idx + 1 }}</td>
          <td>{{ pref['prefix'] }}</td>
          <td>{{ pref['cnt'] }}</td>
          <td>{{ pref['createDate'] }}</td>
          <td>{{ pref['expire'] }}</td>
          <td>{{ pref['rewardFormat'] }}</td>
          <td>
            <button type="button" class="btn btn-primary w-100 btn-sm" v-on:click="listPostfix(pref['prefix'])">DS Mã</button>
          </td>
          <td>
            <button type="button" class="btn btn-danger w-100 btn-sm" v-on:click="removePrefix(pref['prefix'])">Xóa</button>
          </td>
          <td>
            <button type="button" class="btn btn-primary w-100 btn-sm" v-on:click="genCode(pref['prefix'])">Tạo mã</button>
          </td>
        </tr>
      </tbody>
  </table>
</div>

<div class="row top-buffer">
  <table v-if="postfix.length > 0" class="table table-bordered table-responsive-md table-striped text-center">
      <thead>
        <!--tr><th class="text-center" v-for="key in Object.keys(postfix[0])">{{ key }}</th></tr-->
        <tr>
            <th>STT</th>
            <th>Mã code</th>
            <th>Trạng Thái</th>
            <th>Người sử dụng</th>
            <th>Thời gian sử dụng</th>
            <th>
              <button type="button" class="btn btn-danger w-100 btn-sm" v-on:click="">Xóa Hết</button>
            </th>
        </tr>
      </thead>
      <tbody>
        <tr class="pt-3-half" v-for="(post,idx) in postfix" :key="post.id">
          <td>{{ idx + 1}}</td>
          <td>{{ post.prefix + post.postfix }}</td>
          <td>{{ post.use ? 'Đã sử dụng' : 'Chưa sử dụng' }}</td>
          <td>{{ post.use ? post.userBy : '' }}</td>
          <td>{{ post.use ? millis2DateStr(post.useDate) : '' }}</td>
          <td>
            <button type="button" class="btn btn-danger w-50 btn-sm" v-on:click="">Xóa</button>
          </td>
        </tr>
      </tbody>
  </table>
</div>

<#include "footer.ftl">

<script>
const host = '${host}/api/fwd'
const giftSvc = 'http://f901a710-default-defaultin-56c2-2091124869.ap-southeast-1.elb.amazonaws.com/gift_api'

const postOptions = function(data) {
return {
     method: 'POST',
     headers: {'Content-Type': 'application/json',},
     body: JSON.stringify(data),
  }
}

var app = new Vue({
  el: '#app',
  data() {
    return {
        prefix: "",
        server: "",
        prefixStartDate: '',
        prefixEndDate: '',
        rewardFormat: "",
        genCodeCnt: 0,
        prefixes:[],
        postfix:[]
    }
  },
  created() {
    this.listPrefix();
  },
  filters: {

  },
  methods: {
    millis2DateStr: function(millis) {
        let d    = new Date(millis);
        let date = d.toLocaleDateString();
        let time = d.toLocaleTimeString();
        return date + ' ' + time;
    },
    parsePrefix: function(prefixes) {
      prefixes.forEach(pref => {
        pref.createDate = this.millis2DateStr(pref.createDate);
        pref.expire = this.millis2DateStr(pref.expire);
      });
      this.prefixes = prefixes;
    },
    genCSV(postfix) {
        let csvContent = "data:text/csv;charset=utf-8,"
            + postfix.map(e => e.id + ',' + e.prefix + e.postfix).join("\n");

        var encodedUri = encodeURI(csvContent);
        console.log(postfix);
        window.open(encodedUri);
    },
    listPrefix: function() {
        fetch(giftSvc + '/prefixes')
        .then(response => response.json())
        .then(resp => {
            if (resp.msg == 'ok') {
              this.parsePrefix(resp.data.prefix);
            }
            else {
              alert(resp.msg);
            }
        })
        .catch((error) => this.prefixes = [])
    },
    listPostfix: function(prefix) {
        fetch(giftSvc + '/codes?prefix=' + prefix)
        .then(response => response.json())
        .then(resp => {
            if (resp.msg == 'ok') {
              this.postfix = resp.data.postfix;
            }
            else {
              alert(resp.msg);
              this.postfix = [];
            }
        })
        .catch((error) => this.postfix = [])
    },
    genCode: function(prefix) {
        fetch(giftSvc + '/code/gen?prefix=' + prefix + '&&cnt=' + this.genCodeCnt)
        .then(response => response.json())
        .then(resp => {
            if (resp.msg == 'ok') {
              this.genCSV(resp.data.postfix);
            }
            else {
              alert(resp.msg);
              this.postfix = [];
            }      resp.put("msg", "prefix already exist");
                   return resp;
        })
        .catch((error) => this.postfix = [])
    },
    addPrefix: function() {
        let startDate = new Date(this.prefixStartDate).getTime()
        let endDate = new Date(this.prefixEndDate).getTime()

        let uri = '/prefix/add?prefix=' + this.prefix + '&&start=' + startDate + '&&end=' + endDate + '&&server=' + this.server + '&&reward=' + this.rewardFormat;
        fetch(giftSvc + uri)
        .then(response => response.json())
        .then(resp => {
            if (resp.msg == 'ok') {
              this.parsePrefix(resp.data.prefix);
            }
            else {
              alert(resp.msg);
            }
        })
        .catch((error) => this.prefixes = [])
    },
    removePrefix: function(prefix) {
        let uri = '/prefix/remove?prefix=' + prefix;
        fetch(giftSvc + uri)
        .then(response => response.json())
        .then(resp => {
            if (resp.msg == 'ok') {
              this.parsePrefix(resp.data.prefix);
              this.postfix = [];
            }
            else {
              alert(resp.msg);
            }
        })
        .catch((error) => this.prefix = [])
    },
    parseDate: function getDateFormat(date){
        var result = this.twoDigit(date.getDate())+"/"+this.twoDigit(date.getMonth()+1)+"/"+this.twoDigit(date.getFullYear())+" "+this.twoDigit(date.getHours())+":"+this.twoDigit(date.getMinutes())+":"+this.twoDigit(date.getSeconds());
        console.log(result);
        return result;
    },
    success: function(data) {
       if (data.msg == "ok") {
         this.resp = data;
         this.isLoaded = true;
       }
       else {
          alert(data.msg);
          this.isLoaded = false;
          this.serverId = '0';
       }
    }
  }
});
</script>

<style>
.top-buffer { margin-top:15px; }
</style>