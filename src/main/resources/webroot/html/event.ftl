<#include "header.ftl">
<#include "navbar.ftl">

<div class ="row top-buffer">
    <select class="form-control" v-on:change="getEvents(event)" v-model:value="serverId" name="serverList" id="serverList">
        <option value="0">Server</option>
        <#list nodes as node>
          <option value="${node.id}">${node.name}
        </#list>
    </select>
</div>

<div v-if="isLoaded == true" class="row top-buffer">
  <table class="table table-dark">
    <thead>
      <tr>
        <th v-for="key in Object.keys(resp.userEvents[0])">{{ key }}</th>
      </tr>
    </thead>
    <tbody>
      <tr v-for="userEvent in resp.userEvents">
          <!-->each javascript object is a hash map :)<-->
          <td v-for="key in Object.keys(resp.userEvents[0])">{{ userEvent[key] }}</td>
      </tr>
    </tbody>
  </table>
</div>

<script>

const host = 'http://localhost:3000/api/fwd'

var app = new Vue({
  el: '#app',
  data() {
    return {
        serverId: '0',
        isLoaded: false,
        resp: undefined
    }
  },
  methods: {
    getEvents: function (event) {
       let data = { cmd:'getEvents', serverId: this.serverId};
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
         alert(error);
         this.isLoaded = false;
       });
    }
  }
});
</script>

<#include "footer.ftl">

<style>
.top-buffer { margin-top:15px; }
</style>