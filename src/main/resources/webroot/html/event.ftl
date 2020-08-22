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

<div v-if="isLoaded == true" class ="row top-buffer">
   <div class="float-left" class="col-xl-4">
      <input v-model="userEventList" type="text" class="form-control" id="userEventList" name="userEventList"
      placeholder="Event list">
   </div>
   <div class="float-left left-buffer" class="col-xl-4">
      <input v-model="userEventStart" type="text" class="form-control" id="userEventStart" name="userEventStart"
      placeholder="Start Date">
   </div>
   <div class="float-left left-buffer" class="col-xl-4">
      <input v-model="userEventEnd" type="text" class="form-control" id="userEventEnd" name="userEventEnd"
      placeholder="End Date">
   </div>
   <div class="float-left left-buffer" class="col-xl-4">
      <button type="button" class="btn btn-primary" v-on:click="setUserEventTime">Set Time</button>
   </div>
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
        resp: undefined,
        userEventList: '',
        userEventStart: '',
        userEventEnd: ''
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
    },
    setUserEventTime: function(event) {
       let data = { cmd:'setUserEventTime', serverId: this.serverId, eventList: this.userEventList, startDate: this.userEventStart, endDate: this.userEventEnd};
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
           alert(data.msg);
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

.left-buffer { margin-left:15px; }
</style>