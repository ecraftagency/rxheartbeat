<#include "header.ftl">
<#include "navbar.ftl">

<div class="row">

</div>

<#include "footer.ftl">

<script>
const host = 'http://localhost:3000/api/user'
var app = new Vue({
  el: '#app',
  data() {
    return {
        serverId: ''
    }
  }
});
</script>