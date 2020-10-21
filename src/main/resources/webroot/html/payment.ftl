<#include "header.ftl">
<#include "navbar.ftl">
<#include "servers.ftl">

<div id="accordion" v-if="isLoaded == true">
    <div class="card top-buffer">
      <div class="card-header" id="paymentHeader">
          <h5 class="text-center">
            <button class="btn" data-toggle="collapse" data-target="#paymentCollapse" aria-expanded="true" aria-controls="paymentCollapse">
              Gói Nạp
            </button>
          </h5>
      </div>
      <div id="paymentCollapse" class="collapse show" aria-labelledby="paymentHeader" data-parent="#accordion">
        <div class="card-body">
          <table class="table table-bordered table-responsive-md table-striped text-center">
              <thead>
                <tr><th class="text-center" v-for="key in Object.keys(resp.payment[0])">{{ key }}</th></tr>
              </thead>
              <tbody>
                <tr class="pt-3-half" v-for="pay in resp.payment" :key="pay.id">
                  <td v-for="key in Object.keys(resp.payment[0])" :key="pay[key]">{{ pay[key] }}</td>
                </tr>
              </tbody>
          </table>
        </div>
      </div>
    </div>

    <div class="card top-buffer">
      <div class="card-header" id="updatePayHeader">
          <h5 class="text-center">
            <button class="btn" data-toggle="collapse" data-target="#upadtePayCollapse" aria-expanded="false" aria-controls="upadtePayCollapse">
              Update Gói Nạp
            </button>
          </h5>
      </div>
      <div id="upadtePayCollapse" class="collapse" aria-labelledby="updatePayHeader" data-parent="#accordion">
        <div class="card-body">
           <div class="row">
             <div class="col-sm-2">
                  <select class="form-control" v-model:value="updatePID" name="updatePID" id="updatePID" v-on:change="selectUpdatePackage()">
                      <option>gói nạp</option>
                      <option v-for="pay in resp.payment" :value="pay.id">
                        {{ pay.id }}
                      </option>
                   </select>
             </div>
             <div class="col-sm-2">
                <input type="text" v-model="updateTime" class="form-control" id="updateTime" name="updateTime" placeholder="time">
             </div>
             <div class="col-sm-2">
                <input type="text" v-model="updateVIP" class="form-control" id="updateVIP" name="updateVIP" placeholder="vip">
             </div>
           </div>
           <div class="row top-buffer">
              <div class="col-sm-10">
                  <input type="text" v-model="updateItems" class="form-control" id="updateItems" name="updateItems" placeholder="items">
              </div>
              <div class="col-sm-2">
                  <button type="button" class="btn btn-primary w-100" v-on:click="updatePackage">Update</button>
              </div>
           </div>
        </div>
      </div>
    </div>

    <div class="card top-buffer">
      <div class="card-header" id="testPayHeader">
          <h5 class="text-center">
            <button class="btn" data-toggle="collapse" data-target="#testPayCollapse" aria-expanded="false" aria-controls="testPayCollapse">
              Test Nạp
            </button>
          </h5>
      </div>
      <div id="testPayCollapse" class="collapse" aria-labelledby="testPayHeader" data-parent="#accordion">
        <div class="card-body">
           <div class="row">
             <div class="col-sm-4">
                <input type="text" v-model="sessionId" class="form-control" id="sessionId" name="sessionId" placeholder="User Id">
             </div>
             <div class="col-sm-4">
                  <select class="form-control" v-model:value="packageId" name="packageId" id="packageId">
                      <option>gói nạp</option>
                      <option v-for="pay in resp.payment" :value="pay.id">
                        {{ pay.id }}
                      </option>
                   </select>
             </div>
             <div class="col-sm-2">
                 <button type="button" class="btn btn-primary w-100" v-on:click="genPaymentRequest">Tạo Link Nạp</button>
             </div>
           </div>
          <div class="row">
            <div class="col-sm-4">
               <input type="text" v-model="phoenixId" class="form-control" id="sessionId" name="phoenixId" placeholder="Phoenix Id">
            </div>
            <div class="col-sm-2">
                <button type="button" class="btn btn-primary w-100" v-on:click="genGetRoleRequest">Tạo Link Get Role</button>
            </div>
          </div>
           <div class="row">
                <div class="col-sm-12 top-buffer">
                    <a v-bind:href="payReq" target="_blank">{{ payReq }}</a>
                </div>
           </div>
        </div>
      </div>
    </div>
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
        serverId: '0',
        isLoaded: false,
        resp: undefined,
        packageId: 'gói nạp',
        payReq:'',
        sessionId: '',
        phoenixId: '',
        updateTime:'',
        updateVIP:'',
        updateItems:'',
        updatePID:''
    }
  },
  filters: {

  },
  methods: {
    selectUpdatePackage: function(){
        this.resp.payment.forEach(pkg => {
            if (this.updatePID == pkg.id) {
                this.updateTime = '' + pkg.time;
                this.updateVIP = '' + pkg.vip;
                this.updateItems = JSON.stringify(pkg.items);
            }
        })
    },
    serverSelect: function (event) {
       let data = { cmd:'getPaymentInfo', serverId: this.serverId};
       fetch(host, postOptions(data)).then(response => response.json())
       .then(data => this.success(data))
       .catch((error) => this.isLoaded = false);
    },
    updatePackage: function() {
       if(!confirm("Chắc nha, sai là tốn tiền lắm á nha!"))
            return;
       let data = {
           cmd: 'updatePaymentPackage',
           serverId: this.serverId,
           updatePID:this.updatePID,
           updateTime:this.updateTime,
           updateVIP:this.updateVIP,
           updateItems:this.updateItems
       }
       fetch(host, postOptions(data)).then(response => response.json())
       .then(data => this.success(data))
       .catch((error) => this.isLoaded = false);
    },
    genPaymentRequest: function(event) {
       let data = { cmd:'genWebPaymentLink', sessionId: this.sessionId, packageId: this.packageId};
       fetch(host, postOptions(data)).then(response => response.json())
       .then(data => {
         if (data.msg == "ok") {
            this.payReq = data.paymentRequest;
         }
         else {
            this.payReq = '';
            alert(data.msg);
         }
       })
       .catch((error) => alert(error));
    },
    genGetRoleRequest: function(event) {
       let data = { cmd:'genGetRoleLink', phoenixId: this.phoenixId, serverId: this.serverId};
       fetch(host, postOptions(data)).then(response => response.json())
       .then(data => {
         if (data.msg == "ok") {
            this.payReq = data.getRoleRequest;
         }
         else {
            this.payReq = '';
            alert(data.msg);
         }
       })
       .catch((error) => alert(error));
    },
    success: function(data) {
       if (data.msg == "ok") {
         this.resp = data;
         this.isLoaded = true;
       }
       else {
          alert(data.msg);
          this.isLoaded = false;
          this.serverId = '0';
       }
    }
  }
});
</script>

<style>
.top-buffer { margin-top:15px; }
</style>