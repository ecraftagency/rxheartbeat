<div class="row">
    <nav class="navbar navbar-expand-lg navbar-light bg-light">
      <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarNavAltMarkup" aria-controls="navbarNavAltMarkup" aria-expanded="false" aria-label="Toggle navigation">
        <span class="navbar-toggler-icon"></span>
      </button>
      <div class="collapse navbar-collapse" id="navbarNavAltMarkup">
        <div class="navbar-nav">
            <#list navList as navEntry>
                <a class="${navEntry.cssClasses}" href="${navEntry.href}">${navEntry.name}</a>
            </#list>
        </div>
        <#if activeNav.name == "User">
               <div class="float-left" class="col-sm-2">
                     <input v-model="sessionId" type="text" class="form-control" id="sessionId" name="sessionId" placeholder="User Id" v-on:keyup.enter="fetchUser">
               </div>

               <div class="float-left" class="col-xl-4">
                  <input v-model="codeVal" type="text" class="form-control" id="codeValue" name="codeValue"
                  placeholder="Great power comes with great responsibility..." v-on:keyup.enter="injectUser">
               </div>
        </#if>
        <#if activeNav.name == "Mail">
            <select class="form-control" v-model:value="serverId" name="serverList" id="serverList">
                <option value="0">Server</option>
                <#list nodes as node>
                  <option value="${node.id}">${node.name}
                </#list>
            </select>
        </#if>
      </div>
    </nav>
</div>