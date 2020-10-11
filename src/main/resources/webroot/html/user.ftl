<#include "header.ftl">
<#include "navbar.ftl">
<#include "servers.ftl">

<div class ="row top-buffer">
   <div class="col-sm-2">
         <input v-model="userName" type="text" class="form-control" id="userName" name="userName" placeholder="User name" v-on:keyup.enter="getUserId">
   </div>

   <div class="col-sm-2">
         <input v-model="sessionId" type="text" class="form-control" id="sessionId" name="sessionId" placeholder="User Id" v-on:keyup.enter="fetchUser">
   </div>

   <div class="col-sm-4">
      <input v-model="codeVal" type="text" class="form-control" id="codeValue" name="codeValue"
      placeholder="eg session.userGameInfo.exp += 200;" v-on:keyup.enter="injectUser">
   </div>

  <div class="float-left col-sm-3">
     <input v-model="banDate" type="datetime-local" class="form-control" id="banDate" name="banDate"
     placeholder="Ban Date">
  </div>

  <div class="float-left col-sm-1">
     <button type="button" class="btn btn-primary w-100" v-on:click="banUser">Ban</button>
  </div>
</div>

<div id="accordion" v-if="isLoaded == true" class="top-buffer">
    <div class="card top-buffer">
      <div class="card-header" id="infoHeader">
          <h5 class="text-center">
            <button class="btn" data-toggle="collapse" data-target="#infoCollapse" aria-expanded="true" aria-controls="infoCollapse">
              Thông tin user
            </button>
          </h5>
      </div>
      <div id="infoCollapse" class="collapse" aria-labelledby="infoHeader" data-parent="#accordion">
        <div class="card-body">
          <table class="table table-bordered table-responsive-md table-striped">
              <thead>
                <tr>
                  <th scope="col">Thuộc Tính [{{ resp.state }}]</th>
                  <th scope="col">Giá Trị</th>
                </tr>
              </thead>
              <tbody>
                <tr class="pt-3-half" v-for="(key, value) in resp.session.userGameInfo">
                  <td>{{ value }}</td>
                  <td>{{ key }}</td>
                </tr>
              </tbody>
          </table>
        </div>
      </div>
    </div>
    <div class="card top-buffer">
      <div class="card-header" id="inventoryHeader">
          <h5 class="text-center">
            <button class="btn" data-toggle="collapse" data-target="#inventoryCollapse" aria-expanded="false" aria-controls="inventoryCollapse">
              Đạo cụ
            </button>
          </h5>
      </div>
      <div id="inventoryCollapse" class="collapse" aria-labelledby="inventoryHeader" data-parent="#accordion">
        <div class="card-body">
          <table class="table table-bordered table-responsive-md table-striped">
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
      </div>
    </div>
    <div v-if="resp.session.userPayment.length > 0" class="card top-buffer">
      <div class="card-header" id="paymentHeader">
          <h5 class="text-center">
            <button class="btn" data-toggle="collapse" data-target="#paymentCollapse" aria-expanded="false" aria-controls="paymentCollapse">
              Lịch sử nạp
            </button>
          </h5>
      </div>
      <div id="paymentCollapse" class="collapse show" aria-labelledby="paymentHeader" data-parent="#accordion">
        <div class="card-body">
          <table class="table table-bordered table-responsive-md table-striped text-center">
              <thead>
                <tr><th class="text-center" v-for="key in Object.keys(resp.session.userPayment[0])">{{ key }}</th></tr>
              </thead>
              <tbody>
                <tr class="pt-3-half" v-for="pay in resp.session.userPayment" :key="pay.transID">
                  <td v-for="key in Object.keys(resp.session.userPayment[0])" :key="pay[key]">{{ pay[key] }}</td>
                </tr>
              </tbody>
          </table>
        </div>
      </div>
    </div>
    <div v-if="resp.session.userInbox.length > 0" class="card top-buffer">
      <div class="card-header" id="inboxHeader">
          <h5 class="text-center">
            <button class="btn" data-toggle="collapse" data-target="#inboxCollapse" aria-expanded="false" aria-controls="inboxCollapse">
              Inbox
            </button>
          </h5>
      </div>
      <div v-if="resp.session.userInbox.length > 0" id="inboxCollapse" class="collapse" aria-labelledby="inboxHeader" data-parent="#accordion">
        <div class="card-body">
          <table class="table table-bordered table-responsive-md table-striped">
              <thead>
                  <tr>
                    <th v-for="key in Object.keys(resp.session.userInbox[0])">{{ key }}</th>
                  </tr>
              </thead>
              <tbody>
                  <tr v-for="inb in resp.session.userInbox">
                    <td v-for="key in Object.keys(resp.session.userInbox[0])">{{ inb[key] }}</td>
                  </tr>
              </tbody>
          </table>
        </div>
      </div>
    </div>

    <!--div class="card top-buffer">
      <div class="card-header" id="sendHeader">
          <h5 class="text-center">
            <button class="btn" data-toggle="collapse" data-target="#sendCollapse" aria-expanded="false" aria-controls="sendCollapse">
              Gửi mail cá nhân
            </button>
          </h5>
      </div>
      <div id="sendCollapse" class="collapse" aria-labelledby="sendHeader" data-parent="#accordion">
        <div class="card-body">
          <div class="row">
             <div class="receipts col-sm-12">
                <input v-model="receipts" type="text" class="form-control" id="receipts" name="receipts"
                placeholder="id...">
             </div>
          </div>
          <div class="row top-buffer">
             <div class="col-sm-4">
                <input v-model="mailTitle" type="text" class="form-control" id="mailTitle" name="mailTitle"
                placeholder="Tiêu đề" v-on:keyup.enter="injectUser">
             </div>
             <div class="col-sm-7">
                <input v-model="mailItems" type="text" class="form-control" id="mailItems" name="mailItems"
                placeholder="Vật phẩm...">
             </div>
             <div class="col-sm-1">
                <button type="button" class="btn btn-primary w-100" v-on:click="sendMail">Send</button>
             </div>
          </div>
          <div class="row top-buffer">
             <div class="mailInput col-sm-12">
                <input v-model="mailContent" type="text" class="form-control" id="mailContent" name="mailTitle"
                placeholder="Nội Dung">
             </div>
          </div>
        </div>
      </div>
    </div-->
</div>

<div class="row big-buffer" >
 <div class="receipts col-sm-12">
    <input v-model="receipts" type="text" class="form-control" id="receipts" name="receipts"
    placeholder="Gửi mail..., user id cách nhau dấu phẩy ko khoảng trắng">
 </div>
</div>
<div class="row top-buffer">
 <div class="col-sm-4">
    <input v-model="mailTitle" type="text" class="form-control" id="mailTitle" name="mailTitle"
    placeholder="Tiêu đề" v-on:keyup.enter="injectUser">
 </div>
 <div class="col-sm-7">
    <input v-model="mailItems" type="text" class="form-control" id="mailItems" name="mailItems"
    placeholder="Vật phẩm...">
 </div>
 <div class="col-sm-1">
    <button type="button" class="btn btn-primary w-100" v-on:click="sendMail">Send</button>
 </div>
</div>
<div class="row top-buffer">
 <div class="mailInput col-sm-12">
    <textarea v-model="mailContent" class="form-control" id="mailContent" rows="10"></textarea>
 </div>
</div>

<#include "footer.ftl">
<script>
$(document).ready(function() {
    $(".toast").toast('show');
});
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
        banDate:'',
        mailTitle:'',
        mailContent:'',
        mailItems:'',
        receipts:'',
        resp: undefined,
        isLoaded: false
    }
  },
  methods: {
    serverSelect: function() {
    },
    sendMail: function (event){
       if(!confirm("Hãy kiểm tra kỹ format quà nha bạn!"))
            return;
       var receiver = this.receipts.split(',');
       for (var i = 0; i < receiver.length; i++) {
         let data = { cmd:'sendPrivateMail', sessionId: receiver[i] + '', mailTitle: this.mailTitle, mailContent: this.mailContent, mailItems: this.mailItems};

         fetch(host, postOptions(data))
         .then(response => response.json())
         .then(data => {
            alert(data.msg);
         })
         .catch(error => alert(error));
       }
    },
    banUser: function(event){
        let d = new Date(this.banDate);
        this.codeVal = 'session.gmtBan(' + Math.round(d.getTime()/1000) + ');';
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
    },
    parseDate: function getDateFormat(date){
        console.log(date);
        var result = this.twoDigit(date.getDate())+"/"+this.twoDigit(date.getMonth()+1)+"/"+this.twoDigit(date.getFullYear())+" "+this.twoDigit(date.getHours())+":"+this.twoDigit(date.getMinutes())+":"+this.twoDigit(date.getSeconds());
        console.log(result);
        return result;
    },
    twoDigit: function (num){
        if (num<10)
            return '0'+num;
        return num;
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