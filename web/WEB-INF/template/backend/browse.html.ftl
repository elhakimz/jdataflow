<h3>Search</h3>
<div>
    <button id="button" class="btn btn-default">Edit</button>
</div>
<div id="browse" style="margin: 10px">
    <table cellpadding="0" cellspacing="0" border="0" id="browse-table"
           class="table table-striped table-bordered"></table>
</div>
<script>
    var dataSet = [
    <#list model.data as item>
        [  <#list item as x> <#attempt>'${x}'<#recover>'?'</#attempt>,</#list> ],
    </#list>
    ];

    var table = $('#browse-table').dataTable({
        "data": dataSet,
        "order": [
            [ 0, "asc" ]
        ],
        "columns": [
        <#list model.metadata as meta>
            { "title": "${meta.name}" },
        </#list>

        ]
    });

    $('#browse-table tbody').on('click', 'tr', function () {
        if ($(this).hasClass('selected')) {
            $(this).removeClass('selected');
        }
        else {
            table.$('tr.selected').removeClass('selected');
            $(this).addClass('selected');
        }
    });

    $('#button').click(function () {
        //table.row('.selected').remove().draw( false );
        var title = "${model.config.table}";
        var page = title + "_edit.html";

        showEdit(page, title);
    });

</script>