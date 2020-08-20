<#include "header.ftl">
<#include "navbar.ftl">

<div class="row">
<table class="table table-dark">
  <thead>
    <tr>
      <th scope="col">Event Id</th>
      <th scope="col">Name</th>
      <th scope="col">Thời điềm bắt đầu</th>
      <th scope="col">Thời điểm kết thúc</th>
      <th scope="col">Trạng thái</th>
    </tr>
  </thead>
  <tbody>
      <#list userEvent as uEvent>
        <tr><td>${uEvent.id}<td>${uEvent.eventName}<td>${uEvent.startDate}<td>${uEvent.endDate}<td>${uEvent.active}
      </#list>
  </tbody>
</table>
</div>

<#include "footer.ftl">