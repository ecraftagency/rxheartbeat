<#include "header.ftl">
<#include "navbar.ftl">

<div class="row />

<div class="row">
<table class="table table-dark">
  <thead>
    <tr>
      <th scope="col">Node Id</th>
      <th scope="col">Name</th>
      <th scope="col">IP</th>
      <th scope="col">Port</th>
      <th scope="col">Ccu</th>
      <th scope="col">cGroup</th>
    </tr>
  </thead>
  <tbody>
      <#list nodes as node>
        <tr><td>${node.id}<td>${node.name}<td>${node.ip}<td>${node.port}<td>${node.ccu}<td>${node.cacheGroup}
      </#list>
  </tbody>
</table>
</div>

<div class="row" style="white-space: pre-wrap">
    ${cacheStat}
</div>

<#include "footer.ftl">