<#include "header.ftl">
<#include "navbar.ftl">
<#include "servers.ftl">

<div class ="row top-buffer">
    <div class="col-sm-4">
        <select class="form-control" v-on:change="serverSelect(event)" v-model:value="ldbId" name="ldbId" id="ldbId">
            <option value="0">Tổng tài năng</option>
            <option value="1">Ải</option>
        </select>
    </div>
</div>

<div v-if="isLoaded == true" class="row top-buffer">
  <table class="table table-dark">
    <thead>
      <tr>
        <th v-for="key in Object.keys(resp.ldb[0])">{{ key }}</th>
      </tr>
    </thead>
    <tbody>
      <tr v-for="ld in resp.ldb">
        <td v-for="key in Object.keys(resp.ldb[0])">{{ ld[key] }}</td>
      </tr>
    </tbody>
  </table>
</div>

<#include "footer.ftl">

<script>
const host = '${host}/api/fwd'
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
        serverId: '0',
        ldbId:'0',
        resp: undefined,
        isLoaded: false
    }
  },
  methods: {
    serverSelect: function (event) {
       let data = { cmd:'getLDB', serverId: this.serverId, ldbId: this.ldbId};
       fetch(host, postOptions(data)).then(response => response.json())
       .then(data => this.success(data))
       .catch((error) => this.isLoaded = false);
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