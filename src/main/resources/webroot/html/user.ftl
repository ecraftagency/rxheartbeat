<#include "header.ftl">

</script>
<div class="row">
    <nav class="navbar navbar-expand-lg navbar-light bg-light">
      <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarNavAltMarkup" aria-controls="navbarNavAltMarkup" aria-expanded="false" aria-label="Toggle navigation">
        <span class="navbar-toggler-icon"></span>
      </button>
      <div class="collapse navbar-collapse" id="navbarNavAltMarkup">
        <div class="navbar-nav">
          <a class="nav-item nav-link" href="/">Server</a>
          <a class="nav-item nav-link active" href="#">User<span class="sr-only">(current)</span></a>
          <a class="nav-item nav-link" href="#">Event</a>
          <a class="nav-item nav-link" href="#">Config</a>
        </div>
        <div class="float-right">
              <input v-model="userId" type="text" class="form-control" id="name" name="name" placeholder="user id" v-on:keyup.enter="fetchUser">
        </div>

      </div>
    </nav>
</div>

<div class="row>

</div>

<div class="row">
   {{ userId }}
</div>
<#include "footer.ftl">

<script>
var app = new Vue({
  el: '#app',
  data: {
    userId: '',
    session: undefined
  },
  methods: {
    fetchUser: function (event){
       let data = { cmd:"getUser", username: this.userId };

       fetch('http://localhost:3000/api/user', {
         method: 'POST',
         headers: {
           'Content-Type': 'application/json',
         },
         body: JSON.stringify(data),
       })
       .then(response => response.json())
       .then(data => {
         console.log('Success:', data);
       })
       .catch((error) => {
         console.error('Error:', error);
       });
    }
  }
})
</script>