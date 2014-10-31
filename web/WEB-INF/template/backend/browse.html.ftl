<h3>Search</h3>
<div id="browse" style="margin: 10px"></div>
<script>
    var dataSet = [
    <#list model.data as item>
        [  <#list item as x> <#attempt>'${x}'<#recover>'?'</#attempt>,</#list> ],
    </#list>
    ];
    $('#browse').html('<table cellpadding="0" cellspacing="0" border="0" id="browse-table" class="table table-striped table-bordered"></table>');
    $('#browse-table').dataTable({
        "data": dataSet,
        "columns": [
        <#list model.metadata as meta>
            { "title": "${meta.name}" },
        </#list>
        ]
    });

</script>