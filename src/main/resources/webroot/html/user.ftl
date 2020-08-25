<#include "header.ftl">
<#include "navbar.ftl">

<div class ="row top-buffer">
   <div class="float-left" class="col-sm-2">
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
    <table id="inventory" class="table table-dark">
      <thead>
        <tr>
          <th scope="col">Đạo Cụ</th>
          <th scope="col">Số Lượng</th>
        </tr>
      </thead>
      <tbody>
          <tr v-for="(key, value) in resp.session.userInventory">
            <td>{{ value }}</td>
            <td>{{ key }}</td>
          </tr>
      </tbody>
    </table>
</div>

<#include "footer.ftl">

<script>
const host = '${host}/api/fwd'
var app = new Vue({
  el: '#app',
  data() {
    return {
        sessionId: '',
        codeVal: '',
        resp: undefined,
        isLoaded: false
    }
  },
  methods: {
    fetchUser: function (event){
       let data = { cmd:"getSession", sessionId: this.sessionId };

       fetch(host, {
         method: 'POST',
         headers: {
           'Content-Type': 'application/json',
         },
         body: JSON.stringify(data),
       })
       .then(response => response.json())
       .then(data => {
         if (data.msg == "ok") {
           this.resp = data;
           this.isLoaded = true;
         }
         else {
            alert(data.msg);
            this.isLoaded = false;
         }
       })
       .catch((error) => {
         this.isLoaded = false;
       });
    },
    injectUser: function (event){
       if (!confirm("Bạn có chắc ko? GMTool có log lại đó nha!"))
            return;

       let data = { cmd:"injectSession", sessionId: this.sessionId,  path: "", value:this.codeVal};

       fetch(host, {
         method: 'POST',
         headers: {
           'Content-Type': 'application/json',
         },
         body: JSON.stringify(data),
       })
       .then(response => response.json())
       .then(data => {
         if (data.msg == "ok") {
           this.resp = data;
           this.isLoaded = true;
         }
         else {
            this.isLoaded = false;
            alert(data.msg);
         }
       })
       .catch((error) => {
         this.isLoaded = false;
       });
    }
  }
});
</script>

<style>
#codeValue {
  margin-left: 14px;
  width: 800px;
}
.top-buffer { margin-top:15px; }
</style>