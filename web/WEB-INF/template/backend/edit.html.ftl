<div>
    <h3>Edit Datas</h3>

    <div class="panel panel-default">
        <div class="panel-body">
            <form id="edit_form" class="form-horizontal"></form>
        </div>
    </div>

    <div id='editor_holder'></div>


    <script type="text/javascript">
        $('#edit_form').jsonForm({
            schema: {
        <#list model.fielddefs as fielddef>
        ${fielddef.name}:
        {
            type: '${fielddef.type}',
                    title
        :
            '${fielddef.title}',
                    required
        : ${fielddef.required}
        }
        ,
        </#list>
        },
        onSubmit: function (errors, values) {
            if (errors) {
                $('#res').html('<p>I beg your pardon?</p>');
            }
            else {
                $('#res').html('<p>Hello ' + values.name + '.' +
                        (values.age ? '<br/>You are ' + values.age + '.' : '') +
                        '</p>');
            }
        }
        })
        ;
    </script>

</div>