<#include "header.ftl">
<#include "navbar.ftl">
<#include "servers.ftl">

<div id="accordion" v-if="isLoaded == true">
    <div class="card top-buffer">
      <div class="card-header" id="shopHeader">
          <h5 class="text-center">
            <button class="btn" data-toggle="collapse" data-target="#shopCollapse" aria-expanded="true" aria-controls="shopCollapse">
              Shop Package
            </button>
          </h5>
      </div>
      <div id="shopCollapse" class="collapse show" aria-labelledby="shopHeader" data-parent="#accordion">
        <div class="card-body">
          <table class="table table-bordered table-responsive-md table-striped text-center">
              <thead>
                <tr><th class="text-center" v-for="key in Object.keys(resp.shop[0])">{{ key }}</th></tr>
              </thead>
              <tbody>
                <tr class="pt-3-half" v-for="pay in resp.shop" :key="pay.id">
                  <td v-for="key in Object.keys(resp.shop[0])" :key="pay[key]">{{ pay[key] }}</td>
                  <td>
                    <button type="button" class="btn btn-primary w-100" v-on:click="updateStatus(pay['status'])">Update</button>
                  </td>
                </tr>
              </tbody>
          </table>
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
        packageId: 'Shop Item',
        updatePID:''
    }
  },
  filters: {

  },
  methods: {
    selectUpdatePackage: function(){
        this.resp.shop.forEach(pkg => {
            if (this.updatePID == pkg.id) {
                this.status = '' + pkg.status;
            }
        })
    },
    serverSelect: function (event) {
       let data = { cmd:'getShopInfo', serverId: this.serverId};
       fetch(host, postOptions(data)).then(response => response.json())
       .then(data => this.success(data))
       .catch((error) => this.isLoaded = false);
    },
    updateStatus: function(pkgId) {
        alert(pkgId);
       let data = {
           cmd: 'updateShopStatus',
           serverId: this.serverId,
           updatePID:pkgId
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