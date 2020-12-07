<#include "header.ftl">
<#include "navbar.ftl">
<#include "servers.ftl">

<div class="row top-buffer" v-if="isLoaded == true">
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

<!--div id="accordion" v-if="isLoaded == true" class="top-buffer">
    <div class="card top-buffer">
      <div class="card-header" id="infoHeader">
          <h5 class="text-center">
            <button class="btn" data-toggle="collapse" data-target="#infoCollapse" aria-expanded="true" aria-controls="infoCollapse">
              Thống kê hôm nay
            </button>
          </h5>
      </div>
      <div id="infoCollapse" aria-labelledby="infoHeader" data-parent="#accordion">
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
</div-->

<div class="top-buffer row" v-if="isLoaded == true">
  <div class="col-sm-2">
     <input v-model="date" type="text" class="form-control" id="date" name="date"
     placeholder="eg 20201207" v-on:keyup.enter="queryStats">
  </div>
</div>

<div class="row top-buffer" v-if="stats !== undefined && stats.statItem.length > 0">
  <table class="table table-bordered table-responsive-md table-striped">
      <thead>
          <tr>
            <th v-for="key in Object.keys(stats.statItem[0])">{{ key }}</th>
          </tr>
      </thead>
      <tbody>
          <tr v-for="stat in stats.statItem">
            <td v-for="key in Object.keys(stats.statItem[0])">{{ stat[key] }}</td>
          </tr>
      </tbody>
  </table>
</div>

<!--div id="accordion" v-if="stats !== undefined && stats.statItem.length > 0" class="top-buffer">
    <div class="card top-buffer">
      <div class="card-header" id="statHeader">
          <h5 class="text-center">
            <button class="btn" data-toggle="collapse" data-target="#statCollapse" aria-expanded="true" aria-controls="statCollapse">
              {{ date }}
            </button>
          </h5>
      </div>
      <div id="statCollapse" class="collapse" aria-labelledby="statHeader" data-parent="#accordion">
        <div class="card-body">
          <table class="table table-bordered table-responsive-md table-striped">
              <thead>
                  <tr>
                    <th v-for="key in Object.keys(stats.statItem[0])">{{ key }}</th>
                  </tr>
              </thead>
              <tbody>
                  <tr v-for="stat in stats.statItem">
                    <td v-for="key in Object.keys(stats.statItem[0])">{{ stat[key] }}</td>
                  </tr>
              </tbody>
          </table>
        </div>
      </div>
    </div>
</div-->

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
        date:'',
        stats:undefined
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
    queryStats: function(event) {
       let data = { cmd:'queryStats', serverId: this.serverId, date:this.date};
       fetch(host, postOptions(data)).then(response => response.json())
       .then(data => {
         if (data.msg == "ok") {
           this.stats = data;
           alert('ok')
         }
         else {
            alert(data.msg);
            this.stats = undefined;
         }
       })
       .catch((error) => alert(error));
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