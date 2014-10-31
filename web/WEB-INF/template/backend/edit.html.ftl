<div>
    <div class="panel panel-default">
        <div class="panel-body">
            <form id="edit_form" class="form-horizontal"></form>
        </div>
    </div>
    <script type="text/javascript">
        $('#edit_form').jsonForm({
            schema: {
        <#list model.metadata as meta>
        ${meta.name}:
        {
            type: '${meta.type}',
                    title
        :
            '${meta.title}',
                    required
        :
            '${meta.required?c}'
        }
        ,
        </#list>
        },
        "params"
        :
        {
            "fieldHtmlClass"
        :
            "form-control"
        }
        })
        ;
        $('.control-label').addClass('col-lg-2');
        $('.control-group').addClass('form-group');
        $('.controls').addClass('col-lg-10');
        $('.control-label').css('white-space', 'nowrap');
        $('.control-label').css('text-align', 'left');
    </script>

</div>