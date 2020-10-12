<#include "header.ftl">
<#include "navbar.ftl">
<#include "servers.ftl">

<div id="accordion" v-if="isLoaded == true" class="top-buffer">
    <div class="card top-buffer">
      <div class="card-header" id="infoHeader">
          <h5 class="text-center">
            <button class="btn" data-toggle="collapse" data-target="#infoCollapse" aria-expanded="true" aria-controls="infoCollapse">
              Tổng các item
            </button>
          </h5>
      </div>
      <div id="infoCollapse" class="collapse" aria-labelledby="infoHeader" data-parent="#accordion">
        <div class="card-body">
          <table class="table table-bordered table-responsive-md table-striped">
              <thead>
                  <tr>
                    <th v-for="key in Object.keys(resp.statItem[0])">{{ key }}</th>
                  </tr>
              </thead>
              <tbody>
                  <tr v-for="stats in resp.statItem">
                    <td v-for="key in Object.keys(resp.statItem[0])">{{ stats[key] }}</td>
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
        isLoaded: false,
        resp: undefined,
    }
  },
  filters: {

  },
  methods: {
    serverSelect: function (event) {
       let data = { cmd:'getStats', serverId: this.serverId};
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
#mailContent {
  height: 200px;
}

.top-buffer { margin-top:15px; }
.big-buffer { margin-top:100px; }
</style>