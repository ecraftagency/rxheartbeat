<#include "header.ftl">
<#include "navbar.ftl">
<#include "servers.ftl">

<div class="row top-buffer">
   <div class="col-sm-4">
      <input v-model="mailTitle" type="text" class="form-control" id="mailTitle" name="mailTitle"
      placeholder="Tiêu đề" v-on:keyup.enter="injectUser">
   </div>
   <div class="col-sm-7">
      <input v-model="mailItems" type="text" class="form-control" id="mailItems" name="mailTitle"
      placeholder="Vật phẩm...">
   </div>
   <div class="col-sm-1 w-100">
      <button type="button" class="btn btn-primary" v-on:click="sendMail">Send</button>
   </div>
</div>

<div class="row top-buffer">
   <div class="col-sm-12">
      <textarea v-model="mailContent" class="form-control" id="mailContent" rows="10"></textarea>
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
  height: 250px;
}

.top-buffer { margin-top:15px; }
</style>