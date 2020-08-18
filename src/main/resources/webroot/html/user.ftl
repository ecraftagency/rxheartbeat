<#include "header.ftl">
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
      </div>
    </nav>
</div>

<div class="row">
  <div class="col-md-12 mt-1">
    <div class="float-right">
          <input type="text" class="form-control" id="name" name="name" placeholder="user id">
    </div>
  </div>
</div>

<script src="js/user.js"></script>
<#include "footer.ftl">