<#include "header.ftl">
<div class="row">
    <nav class="navbar navbar-expand-lg navbar-light bg-light">
      <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarNavAltMarkup" aria-controls="navbarNavAltMarkup" aria-expanded="false" aria-label="Toggle navigation">
        <span class="navbar-toggler-icon"></span>
      </button>
      <div class="collapse navbar-collapse" id="navbarNavAltMarkup">
        <div class="navbar-nav">
          <a class="nav-item nav-link" href="/">Server</a>
          <a class="nav-item nav-link active" href="#">User<span class="sr-only">(current)</span></a>
          <a class="nav-item nav-link" href="#">Event</a>
          <a class="nav-item nav-link" href="#">Config</a>
        </div>
           <div class="float-left" class="col-sm-2">
                 <input v-model="sessionId" type="text" class="form-control" id="sessionId" name="sessionId" placeholder="User Id" v-on:keyup.enter="fetchUser">
           </div>

           <div class="float-left" class="col-xl-4">
              <input v-model="codeVal" type="text" class="form-control" id="codeValue" name="codeValue"
              placeholder="Great power comes with great responsibility..." v-on:keyup.enter="injectUser">
           </div>
      </div>
    </nav>
</div>

<div v-if="isLoaded == true" class="row top-buffer">
    <table id="gameInfo" class="table table-dark">
      <thead>
        <tr>
          <th scope="col">Thuộc Tính [{{ state }}]</th>
          <th scope="col">Giá Trị</th>
        </tr>
      </thead>
      <tbody>
          <tr v-for="(key, value) in session.userGameInfo">
            <td>{{ value }}</td>
            <td>{{ key }}</td>
          </tr>
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
          <tr v-for="(key, value) in session.userInventory">
            <td>{{ value }}</td>
            <td>{{ key }}</td>
          </tr>
      </tbody>
    </table>
</div>

<#include "footer.ftl">

<script>
const host = 'http://18.141.216.52:3000/api/user'
var app = new Vue({
  el: '#app',
  data() {
    return {
        sessionId: '',
        codeVal: '',
        session: undefined,
        state: '',
        isLoaded: false
    }
  },
  methods: {
    fetchUser: function (event){
       let data = { cmd:"getUserInfo", sessionId: this.sessionId };

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
           this.session = data;
           this.isLoaded = true;
           this.state = data.ctx;
         }
         else {
            this.isLoaded = false;
         }
         this.codeVal = '';
       })
       .catch((error) => {
         this.isLoaded = false;
         this.codeVal = '';
       });
    },
    injectUser: function (event){
       let data = { cmd:"inject", sessionId: this.sessionId,  path: "", value:this.codeVal};

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
           this.session = data;
           this.isLoaded = true;
           this.state = data.ctx;

         }
         else {
            this.isLoaded = false;
            alert(data.msg);
            console.log(data.msg);
         }
         this.codeVal = '';
       })
       .catch((error) => {
         this.isLoaded = false;
         this.codeVal = '';
       });
    }
  }
});
</script>

<style>
#codeValue {
  margin-left: 14px;
  width: 450px;
}

.top-buffer { margin-top:15px; }
</style>