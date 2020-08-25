<#include "header.ftl">
<#include "navbar.ftl">
<#include "servers.ftl">

<div v-if="isLoaded == true" class="row top-buffer">
  <input v-model="codeVal" type="text" class="form-control" id="codeValue" name="codeValue"
  placeholder="eg USER_GAME_INFO.INIT_TIME_GIFT = 86400; (Make sure you know exactly what you are doing)" v-on:keyup.enter="injectConstant">
</div>


<div v-if="isLoaded == true" class="row top-buffer">
  <table class="table table-dark">
    <thead>
      <tr>
        <th v-for="key in Object.keys(resp.config[0])">{{ key }}</th>
      </tr>
    </thead>
    <tbody>
      <tr v-for="cfg in resp.config">
        <td v-for="key in Object.keys(resp.config[0])">{{ cfg[key] }}</td>
      </tr>
       <tr><td>Easy, more to come...</td></tr>
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
        resp: undefined,
        isLoaded: false,
        codeVal: ''
    }
  },
  methods: {
    serverSelect: function (event) {
       let data = { cmd:'getConfig', serverId: this.serverId};
       fetch(host, postOptions(data)).then(response => response.json())
       .then(data => this.success(data))
       .catch((error) => this.isLoaded = false);
    },
    injectConstant: function(event) {
       if (!confirm("Chắc nha thím T___T"))
            return;
       let data = { cmd:"injectConstant", serverId: this.serverId,  path: "", value:this.codeVal};
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

