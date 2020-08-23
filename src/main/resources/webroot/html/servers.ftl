<div class ="row top-buffer">
    <select class="form-control" v-on:change="serverSelect(event)" v-model:value="serverId" name="serverList" id="serverList">
        <option value="0">Server</option>
        <#list nodes as node>
          <option value="${node.id}">${node.name}
        </#list>
    </select>
</div>