<#include "header.ftl">
<#include "navbar.ftl">
<#include "servers.ftl">

<div class ="row top-buffer">
   <div class="float-left" class="col-sm-2">
         <input v-model="userName" type="text" class="form-control" id="userName" name="userName" placeholder="User name" v-on:keyup.enter="getUserId">
   </div>

   <div class="float-left col-sm-2 left-buffer">
         <input v-model="sessionId" type="text" class="form-control" id="sessionId" name="sessionId" placeholder="User Id" v-on:keyup.enter="fetchUser">
   </div>

   <div class="float-left" class="col-xl-4">
      <input v-model="codeVal" type="text" class="form-control" id="codeValue" name="codeValue"
      placeholder="eg session.userGameInfo.exp += 200; (Great power comes with great responsibility...)" v-on:keyup.enter="injectUser">
   </div>
</div>

<div v-if="isLoaded == true" class="row top-buffer">
    <table id="gameInfo" class="table table-dark">
      <thead>
        <tr>
          <th scope="col">Thuộc Tính [{{ resp.state }}]</th>
          <th scope="col">Giá Trị</th>
        </tr>
      </thead>
      <tbody>
          <tr v-for="(key, value) in resp.session.userGameInfo">
            <td>{{ value }}</td>
            <td>{{ key }}</td>
          </tr>
          <tr><td>Easy, more to come...</td></tr>
      </tbody>
    </table>
</div>

<div v-if="isLoaded == true" class="row top-buffer">
  <table class="table table-dark">
    <thead>
      <tr>
        <th v-for="key in Object.keys(resp.session.userInventory[0])">{{ key }}</th>
      </tr>
    </thead>
    <tbody>
      <tr v-for="inv in resp.session.userInventory">
        <td v-for="key in Object.keys(resp.session.userInventory[0])">{{ inv[key] }}</td>
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
        serverId: '',
        sessionId: '',
        codeVal: '',
        userName: '',
        resp: undefined,
        isLoaded: false
    }
  },
  methods: {
    serverSelect: function() {
    },
    getUserId: function(event){
       let data = { cmd:"getSessionId", serverId: this.serverId, userName: this.userName };

       fetch(host, postOptions(data))
       .then(response => response.json())
       .then(data => {
          if (data.msg == "ok") {
            this.sessionId = data.sessionId;
          }
          else {
             alert(data.msg);
          }
       })
       .catch(error => this.isLoaded = false);
    },
    fetchUser: function (event){
       let data = { cmd:"getSession", sessionId: this.sessionId };

       fetch(host, postOptions(data))
       .then(response => response.json())
       .then(data => this.success(data))
       .catch(error => this.isLoaded = false);
    },
    injectUser: function (event){
       if (!confirm("Bạn có chắc ko? GMTool có log lại đó nha!"))
            return;

       let data = { cmd:"injectSession", sessionId: this.sessionId,  path: "", value:this.codeVal};

       fetch(host, postOptions(data))
       .then(response => response.json())
       .then(data => this.success(data))
       .catch(error => this.isLoaded = false);
    },
    success: function(data) {
      if (data.msg == "ok") {
        this.resp = data;
        this.isLoaded = true;
      }
      else {
         alert(data.msg);
         this.isLoaded = false;
      }
    }
  }
});
</script>

<style>
#codeValue {
  margin-left: 14px;
  width: 700px;
}
.left-buffer {
  margin-left: 14px;
}

.top-buffer { margin-top:15px; }
</style>