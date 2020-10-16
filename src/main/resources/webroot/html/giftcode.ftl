<#include "header.ftl">
<#include "navbar.ftl">

<div class ="row top-buffer">
   <div class="col-sm-2">
         <input v-model="prefix" type="text" class="form-control" id="prefix" name="prefix" placeholder="Tiền tố">
   </div>

   <div class="col-sm-2">
         <input v-model="server" type="text" class="form-control" id="server" name="server" placeholder="Server">
   </div>

   <div class="col-sm-2">
      <input v-model="total" type="text" class="form-control" id="total" name="total"
      placeholder="Tổng số giftcode">
   </div>

  <div class="float-left col-sm-4">
     <input v-model="rewardFormat" type="text" class="form-control" id="rewardFormat" name="rewardFormat"
     placeholder="reward format">
  </div>
  <div class="float-left col-sm-2">
     <button type="button" class="btn btn-primary w-100" v-on:click="addPrefix">Tạo tiền tố</button>
  </div>
</div>

<div id="accordion">
    <div class="card top-buffer">
      <div class="card-header" id="prefixHeader">
          <h5 class="text-center">
            <button class="btn" data-toggle="collapse show" data-target="#prefixCollapse" aria-expanded="true" aria-controls="prefixCollapse">
              Tiền Tố
            </button>
          </h5>
      </div>
      <div id="prefixCollapse" class="collapse show" aria-labelledby="prefixHeaders" data-parent="#accordion">
        <div class="card-body">
          <table v-if="prefixes.length > 0" class="table table-bordered table-responsive-md table-striped text-center">
              <thead>
                <tr><th class="text-center" v-for="key in Object.keys(prefixes[0])">{{ key }}</th></tr>
              </thead>
              <tbody>
                <tr class="pt-3-half" v-for="pref in prefixes" :key="pref.id">
                  <td v-for="key in Object.keys(prefixes[0])" :key="pref[key]">{{ pref[key] }}</td>
                  <td>
                    <button type="button" class="btn btn-primary w-100" v-on:click="listPostfix(pref['prefix'])">DS Mã</button>
                  </td>
                  <td>
                    <button type="button" class="btn btn-danger w-100" v-on:click="removePrefix(pref['prefix'])">Xóa</button>
                  </td>
                  <td>
                    <button type="button" class="btn btn-primary w-100" v-on:click="genCode(pref['prefix'])">Tạo mã</button>
                  </td>
                </tr>
              </tbody>
          </table>
        </div>
      </div>
    </div>
    <div class="card top-buffer">
      <div class="card-header" id="postfixHeader">
          <h5 class="text-center">
            <button class="btn" data-toggle="collapse show" data-target="#postfixCollapse" aria-expanded="true" aria-controls="postfixCollapse">
              Hậu Tố
            </button>
          </h5>
      </div>
      <div id="postfixCollapse" class="collapse show" aria-labelledby="postfixHeaders" data-parent="#accordion">
        <div class="card-body">
          <table v-if="postfix.length > 0" class="table table-bordered table-responsive-md table-striped text-center">
              <thead>
                <tr><th class="text-center" v-for="key in Object.keys(postfix[0])">{{ key }}</th></tr>
              </thead>
              <tbody>
                <tr class="pt-3-half" v-for="post in postfix" :key="post.id">
                  <td v-for="key in Object.keys(postfix[0])" :key="post[key]">{{ post[key] }}</td>
                </tr>
              </tbody>
          </table>
        </div>
      </div>
    </div>
</div>

<#include "footer.ftl">

<script>
const host = '${host}/api/fwd'
const giftSvc = 'http://a47233bd069ec42b69f101a5fa681eb6-1872285878.ap-southeast-1.elb.amazonaws.com/gift_api'
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
        total: "",
        rewardFormat: "",
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
    parsePrefix: function(prefixes) {
      prefixes.forEach(pref => {
        pref.createDate = (new Date(pref.createDate)).toLocaleTimeString();
        pref.expire = (new Date(pref.expire)).toLocaleTimeString();
      });
      this.prefixes = prefixes;
    },
    testCSV(postfix) {
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
        fetch(giftSvc + '/code/gen?prefix=' + prefix + '&&cnt=' + 100)
        .then(response => response.json())
        .then(resp => {
            if (resp.msg == 'ok') {
              this.testCSV(resp.data.postfix);
            }
            else {
              alert(resp.msg);
              this.postfix = [];
            }
        })
        .catch((error) => this.postfix = [])
    },
    addPrefix: function() {
        let uri = '/prefix/add?prefix=' + this.prefix + '&&total=' + this.total + '&&server=' + this.server + '&&reward=' + this.rewardFormat;
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