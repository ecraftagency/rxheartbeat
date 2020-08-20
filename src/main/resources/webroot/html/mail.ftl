<#include "header.ftl">
<#include "navbar.ftl">

<div class="row top-buffer">
   <div class="float-left" class="col-xl-4">
      <input v-model="mailTitle" type="text" class="form-control" id="mailTitle" name="mailTitle"
      placeholder="Tiêu đề" v-on:keyup.enter="injectUser">
   </div>
</div>

<div class="row top-buffer">
   <div class="float-left" class="col-xl-4">
      <input v-model="mailContent" type="text" class="form-control" id="mailTitle" name="mailTitle"
      placeholder="Nội Dung" v-on:keyup.enter="injectUser">
   </div>
</div>

<div class="row top-buffer">
   <div class="float-left" class="col-xl-4">
      <input v-model="mailItems" type="text" class="form-control" id="mailItems" name="mailTitle"
      placeholder="Vật phẩm..." v-on:keyup.enter="injectUser">
   </div>
</div>

<#include "footer.ftl">

<script>
const host = 'http://localhost:3000/api/user'
var app = new Vue({
  el: '#app',
  data() {
    return {
        serverId: '0',
        mailTitle: '',
        mailContent: '',
        mailItems: ''
    }
  }
});
</script>

<style>
#mailContent {
  margin-left: 14px;
  width: 450px;
  width: 450px;
}

.top-buffer { margin-top:15px; }
</style>