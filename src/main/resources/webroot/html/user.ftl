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

<div v-if="isLoaded == true" class="row">
    <table id="gameInfo" class="table table-dark">
      <thead>
        <tr>
          <th scope="col">Thuộc Tính</th>
          <th scope="col">Giá Trị</th>
        </tr>
      </thead>
      <tbody>
          <tr v-for="(key, value) in session.userGameInfo">
            <td>{{ value }}</td>
            <td>{{ key }}</td>
          </tr>
      </tbody>
    </table>
</div>

<div v-if="isLoaded == true" class="row">
    <table id="inventory" class="table table-dark">
      <thead>
        <tr>
          <th scope="col">Đạo Cụ</th>
          <th scope="col">Số Lượng</th>
        </tr>
      </thead>
      <tbody>
          <tr v-for="(key, value) in session.userInventory">
            <td>{{ value }}</td>
            <td>{{ key }}</td>
          </tr>
      </tbody>
    </table>
</div>

<#include "footer.ftl">

<script>
var app = new Vue({
  el: '#app',
  data() {
    return {
        userId: '',
        session: undefined,
        isLoaded: false
    }
  },
  methods: {
    fetchUser: function (event){
       let data = { cmd:"getUserInfo", username: this.userId };

       fetch('http://18.141.216.52:3000/api/user', {
         method: 'POST',
         headers: {
           'Content-Type': 'application/json',
         },
         body: JSON.stringify(data),
       })
       .then(response => response.json())
       .then(data => {
         this.session = data;
         this.isLoaded = true;
       })
       .catch((error) => {
         alert(error)
         this.isLoaded = false;
       });
    }
  }
});
</script>