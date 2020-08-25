<#include "header.ftl">
<#include "navbar.ftl">
<#include "servers.ftl">

<script>

</script>

<div v-if="isLoaded == true" class ="row top-buffer">
    <div class ="col-ml-4">
      <select class="form-control" v-model:value="eventType" name="eventType" id="eventType">
        <option value="0">Thưởng hạn giờ</option>
        <option value="1">Idol Đặc Biệt</option>
        <option value="2">Đua top cá nhân</option>
      </select>
    </div>
   <div class="float-left left-buffer" class="col-sm-4">
      <input v-model="userEventList" type="text" class="form-control" id="userEventList" name="userEventList"
      placeholder="Event list">
   </div>
   <div class="float-left left-buffer" class="col-sm-4">
      <input v-model="userEventStart" type="datetime-local" class="form-control" id="userEventStart" name="userEventStart"
      placeholder="Start Date">
   </div>
   <div class="float-left left-buffer" class="col-sm-4">
      <input v-model="userEventEnd" type="datetime-local" class="form-control" id="userEventEnd" name="userEventEnd"
      placeholder="End Date">
   </div>
   <div class="float-left left-buffer" class="col-sm-4">
      <input v-model="flushDelay" type="text" class="form-control" id="flushDelay" name="flushDelay"
      placeholder="TG đóng băng (giờ)">
   </div>
   <div class="float-left top-buffer" class="col-sm-4">
      <button type="button" class="btn btn-primary" v-on:click="setUserEventTime">Set Time</button>
   </div>
</div>

<#list evtType as type>
    <div v-if="isLoaded == true" class="row top-buffer">
      <table class="table table-dark">
        <thead>
          <tr>
            <th v-for="key in Object.keys(resp.${type}[0])">{{ key }}</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="event in resp.${type}">
            <td v-for="key in Object.keys(resp.${type}[0])">{{ event[key] }}</td>
          </tr>
        </tbody>
      </table>
    </div>
</#list>

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
        resp: undefined,
        userEventList: '',
        userEventStart: '',
        userEventEnd: '',
        flushDelay: '',
        eventType: '0'
    }
  },
  methods: {
    serverSelect: function (event) {
       let data = { cmd:'getEvents', serverId: this.serverId};
       fetch(host, postOptions(data)).then(response => response.json())
       .then(data => this.success(data))
       .catch((error) => this.isLoaded = false);
    },
    setUserEventTime: function(event) {
       if(!confirm("Coi chừng sự kiện loại này mà id thì của loại kia nha :-w"))
        return;
       let data = { cmd:'setUserEventTime', eventType: this.eventType, serverId: this.serverId, eventList: this.userEventList, startDate: this.userEventStart, endDate: this.userEventEnd, flushDelay: this.flushDelay};
       fetch(host, postOptions(data)).then(response => response.json())
       .then(data => this.success(data))
       .catch((error) => (error) => this.isLoaded = false);
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

<#include "footer.ftl">

<style>
.top-buffer { margin-top:15px; }

.left-buffer { margin-left:15px; }
</style>