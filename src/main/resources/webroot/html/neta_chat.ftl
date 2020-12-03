<#include "header.ftl">
<#include "navbar.ftl">
<#include "servers.ftl">

<div class ="row top-buffer">
   <div class="col-sm-2">
         <input v-model="groupName" type="text" class="form-control" id="groupName" name="groupName" placeholder="Group Name">
   </div>

  <div class="float-left col-sm-2">
     <button type="button" class="btn btn-primary w-100" v-on:click="addGroup">Tạo Group Chat</button>
  </div>
</div>

<div id="accordion" v-if="isLoaded == true && resp.netaGroups.length > 0" class="top-buffer">
    <div class="card top-buffer">
      <div class="card-header" id="infoHeader">
          <h5 class="text-center">
            <button class="btn" data-toggle="collapse" data-target="#infoCollapse" aria-expanded="true" aria-controls="infoCollapse">
              Neta Group
            </button>
          </h5>
      </div>
      <div id="infoCollapse" class="collapse" aria-labelledby="infoHeader" data-parent="#accordion">
        <div class="card-body">
          <table class="table table-bordered table-responsive-md table-striped">
              <thead>
                  <tr>
                    <th v-for="key in Object.keys(resp.netaGroups[0])">{{ key }}</th>
                  </tr>
              </thead>
              <tbody>
                  <tr v-for="group in resp.netaGroups">
                    <td v-for="key in Object.keys(resp.netaGroups[0])">{{ group[key] }}</td>
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
        groupName:'',
        resp: undefined,
    }
  },
  filters: {

  },
  methods: {
    serverSelect: function (event) {
       let data = { cmd:'getNetaGroup', serverId: this.serverId};
       fetch(host, postOptions(data)).then(response => response.json())
       .then(data => this.success(data))
       .catch((error) => this.isLoaded = false);
    },
    addGroup: function(event) {
      let data = { cmd:'addNetaGroup', serverId: this.serverId, groupName:this.groupName};
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