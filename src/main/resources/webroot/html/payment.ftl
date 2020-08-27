<#include "header.ftl">
<#include "navbar.ftl">
<#include "servers.ftl">

<div id="accordion" v-if="isLoaded == true" class="top-buffer">
    <div class="card">
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
                <tr class="pt-3-half" contenteditable="true" v-for="pay in resp.payment">
                  <td v-for="key in Object.keys(resp.payment[0])">{{ pay[key] }}</td>
                  <td>
                    <span class="table-remove"><button type="button"
                        class="btn btn-danger btn-rounded btn-sm my-0">Remove</button></span>
                  </td>
                </tr>
              </tbody>
          </table>
        </div>
      </div>
    </div>
    <div class="card">
      <div class="card-header" id="testPayHeader">
          <h5 class="text-center">
            <button class="btn" data-toggle="collapse" data-target="#testPayCollapse" aria-expanded="false" aria-controls="testPayCollapse">
              Test Nạp
            </button>
          </h5>
      </div>
      <div id="testPayCollapse" class="collapse show" aria-labelledby="testPayHeader" data-parent="#accordion">
        <div class="card-body">
           <div class="row">
             <div class="col-sm-4">
                <input type="text" class="form-control" id="sessionId" name="sessionId" placeholder="User Id">
             </div>
             <div class="col-sm-4">
                  <select class="form-control" v-on:change="serverSelect(event)" v-model:value="serverId" name="serverList" id="serverList">
                      <option value="0">Server</option>
                      <#list nodes as node>
                        <option value="${node.id}">${node.name}
                      </#list>
                   </select>
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
        resp: undefined
    }
  },
  methods: {
    serverSelect: function (event) {
       let data = { cmd:'getPaymentInfo', serverId: this.serverId};
       fetch(host, postOptions(data)).then(response => response.json())
       .then(data => this.success(data))
       .catch((error) => this.isLoaded = false);
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