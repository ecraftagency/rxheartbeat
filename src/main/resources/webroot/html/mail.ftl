<#include "header.ftl">
<#include "navbar.ftl">
<#include "servers.ftl">

<div class="row top-buffer">
   <div class="float-left" class="col-xl-4">
      <input v-model="mailTitle" type="text" class="form-control" id="mailTitle" name="mailTitle"
      placeholder="Tiêu đề" v-on:keyup.enter="injectUser">
   </div>
</div>

<div class="row top-buffer">
   <div class="float-left" class="col-xl-4">
      <input v-model="mailContent" type="text" class="form-control" id="mailContent" name="mailTitle"
      placeholder="Nội Dung">
   </div>
</div>

<div class="row top-buffer">
   <div class="float-left" class="col-xl-4">
      <input v-model="mailItems" type="text" class="form-control" id="mailItems" name="mailTitle"
      placeholder="Vật phẩm...">
   </div>
</div>

<div class="row top-buffer">
   <div class="float-left" class="col-xl-4">
      <button type="button" class="btn btn-primary" v-on:click="sendMail">Send</button>
   </div>
</div>

<#include "footer.ftl">

<script>
const host = '${host}/api/fwd'
var app = new Vue({
  el: '#app',
  data() {
    return {
        serverId: '0',
        mailTitle: '',
        mailContent: '',
        mailItems: ''
    }
  },
  methods: {
    sendMail: function (event){
       if(!confirm("Hãy kiểm tra kỹ format quà nha bạn!"))
            return;
       let data = { cmd:'sendMail', serverId: this.serverId , mailTitle: this.mailTitle, mailContent: this.mailContent, mailItems: this.mailItems};

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
           alert("ok");
         }
         else {
            alert(data.msg);
         }
       })
       .catch((error) => {
         alert(error);
         console.log(error);
       });
    },
    serverSelect: function(event) {

    }
  }
});
</script>

<style>
#mailContent {
  height: 450px;
}

.float-left {
    width: 450px;
}
.top-buffer { margin-top:15px; }
</style>