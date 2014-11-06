( function (Dataflow) {

    var $formtemplate = $(
            '<ul id="tabs" class="nav nav-tabs">' +
            '<li><a href="#tabs-1" data-toggle="tab">Initial Form</a></li>' +
            '<li><a href="#tabs-2" data-toggle="tab">Log</a></li></ul>' +
            '<div id="tabContent" class="tab-content">' +
            '<div class="tab-pane fade active in" id="tabs-1">' +
            '<form id="initform" style="width: 95%"></form>' +
            '</div>' +
            '<div  class="tab-pane" id="tabs-2">' +
            '<button id="clearlog" class="btn btn-default">Clear</button>' +
            '<textarea id="message" class="form-control" rows="8"></textarea></div>' + +'</div>'
    );


    var RunGraph = Dataflow.prototype.plugin("rungraph");
    RunGraph.initialize = function (dataflow) {

        var dropdn = '<div class="btn-group">' +
            '<a href="#" class="btn btn-default dropdown-toggle" data-toggle="dropdown">Runtime<span class="caret"></span> </a>' +
            '<ul class="dropdown-menu">' +
            '<li><a href="#">Runtime</a></li>' +
            '<li><a href="#">Generate</a></li>' +
            '</ul></div>';

        var $form = $(
                '<div style="background-color: hsla(0, 0%, 0%, 0);border: none; padding: 10px; color: hsl(220, 10%, 45%);height:200px;vertical-align: top">' +
                '<table><tr><td><input type="button" id="run" class="btn btn-primary" value="Run"></td><td style="color:#CCCCCC;font-size: 12px">&nbsp;Mode&nbsp;</td>' +
                '<td>' + dropdn + '</td></tr></table>' +
                '<div style="border-bottom: 1px solid #333;border-top: 1px solid #333 ;margin: 10px ">IP Addr&nbsp;<input style="width: 200px" class="myinput" ><br/>Port&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input style="width:60px" class="myinput"></div>' +
                '<div><Scripts<br/><select id="scripts"  class="form-control"><option>Current</option></select></div>' +
                '</div>'
        );

        var $runButton = $form.find("#run");
        var $scriptSelect = $form.find("#scripts");
        var $selectMode = $form.find("#select-mode");
        var $runLog = $form.find("#runlog");

        $scriptSelect.selectmenu();
        //$selectMode.selectmenu();
        //$runButton.button();

        var modalView;
        var dialog;

        function afterInitForm(data) {
            var dlg = $('#dlg');
            var dialog = dlg.find('#dlg-body');
            var title = dlg.find('#dlg-title');
            var footer = dlg.find('#dlg-modal-footer');

            title.text('Initial Form and Log');
            dialog.empty();
            dialog.html($formtemplate);
            var runbtn = $('<button class="btn btn-default" id="run-with-param" style="width: 150px">Run with Parameters</button>');
            footer.html(runbtn);

            //$( "#tabs" ).tabs();
            $("#clearlog").button().click(function () {
                $('#message').val("");
            });

            buildForm(dialog.find('#initform'), data, dialog, modalView);
            dlg.modal('show');
        }

        function showResultLog(data) {
            //$runLog.val(data);
            $('#message').val(data);
            console.log($runLog + ":" + data);
        }

        function runWithForm(formdata) {
            var data = dataflow.graph.toJSON();
            var xdata = {};
            xdata.params = JSON.stringify(formdata);
            xdata.data = JSON.stringify(data);
            console.log("run with form " + formdata);
            jQuery.post("/runner", {func: "go-param", data: JSON.stringify(xdata)}).done(function (rcv) {
                alert(rcv);
                console.log(rcv);
                showResultLog(rcv)
            });

        }

        function run() {
            var data = JSON.stringify(dataflow.graph.toJSON());
            jQuery.post("/runner", {func: "get-param", data: data}).done(function (rcv) {
                afterInitForm(rcv);
                console.log(rcv);
            });

        }

        $runButton.click(run);

        function buildForm(tag, jsondata, dlg, modal) {
            if (tag.find('.ui-dform-p') != null) {
                tag.empty();
            }
            var form = tag.dform(jsondata);

            $('#run-with-param').click(function () {
                console.log("run form with data");
                var formdata = parseFormValues(tag);
                runWithForm(formdata);
            });

        }

        function parseFormValues(form) {
            console.log("form =" + form);
            var list = $('#initform').find('input');
            var data = [];
            list.each(function (index, input) {
                //do your thing. i is the index in the array, li is the li element
                console.log(input.id + " : " + input.value);
                var item = {};
                item.value = input.value;
                var dot = input.id.indexOf('.');
                item.port = input.id.substr(dot + 1);
                item.component = input.id.substr(0, dot);
                data[index] = item;
            });
            console.log(data);
            return data;
        }

        dataflow.addPlugin({
            id: "rungraph",
            label: "rungraph",
            name: "",
            menu: $form,
            icon: "play",
            pinned: true
        });
    };

}(Dataflow) );