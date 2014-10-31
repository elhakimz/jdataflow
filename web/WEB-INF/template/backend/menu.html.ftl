<div class="navmenu navmenu-default navmenu-fixed-left offcanvas-sm">
    <a class="navmenu-brand visible-md visible-lg" href="#">Database Backend</a>
    <ul class="nav navmenu-nav">
        <li class="active"><a href="#" onclick="updateContent('home.html');">Home</a></li>
    <#list model as item >
        <li><#attempt><a href="#" onclick="updateContent('${item}.html');">${item}</a><#recover> ? </#attempt></li>
    </#list>
    </ul>
</div>