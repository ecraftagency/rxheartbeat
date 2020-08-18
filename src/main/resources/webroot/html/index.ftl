<#include "header.ftl">
<div class="row">
    <nav class="navbar navbar-expand-lg navbar-light bg-light">
      <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarNavAltMarkup" aria-controls="navbarNavAltMarkup" aria-expanded="false" aria-label="Toggle navigation">
        <span class="navbar-toggler-icon"></span>
      </button>
      <div class="collapse navbar-collapse" id="navbarNavAltMarkup">
        <div class="navbar-nav">
          <a class="nav-item nav-link active" href="#">Server<span class="sr-only">(current)</span></a>
          <a class="nav-item nav-link" href="#">User</a>
          <a class="nav-item nav-link" href="#">Event</a>
          <a class="nav-item nav-link" href="#">Config</a>
        </div>
      </div>
    </nav>
</div>

<div class="row />

<div class="row">
<table class="table table-dark">
  <thead>
    <tr>
      <th scope="col">Node Id</th>
      <th scope="col">Name</th>
      <th scope="col">IP</th>
      <th scope="col">Port</th>
      <th scope="col">CCU</th>
    </tr>
  </thead>
  <tbody>
      <#list nodes as node>
        <tr><td>${node.id}<td>${node.name}<td>${node.ip}<td>${node.port}<td>${node.ccu}
      </#list>
  </tbody>
</table>
</div>

<#include "footer.ftl">