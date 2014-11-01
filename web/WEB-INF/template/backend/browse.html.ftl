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

        ],
        "columnDefs": [
            {
                "targets": -1,
                "data": null,
                "defaultContent": '<button class="btn btn-sm" onclick="clickedit()">Edit</button>'
            }
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

    function clickedit() {
//        var data = table.row( $(this).parents('tr') ).data();
//        alert( data[0] +"'s salary is: "+ data[ 5 ] );
//        var id=  data[0];
        var title = "${model.config.table}";
        var page = title + "_edit.html";
        showEdit(page, title);
    }
    ;

    $('#button').click(function () {
        //table.row('.selected').remove().draw( false );
        var title = "${model.config.table}";
        var page = title + "_edit.html";
        showEdit(page, title);
    });

</script>