/**
 * Created by abilhakim on 9/22/14.
 */
( function (Dataflow) {
    var OpenSave = Dataflow.prototype.plugin("opensave");
    OpenSave.initialize = function (dataflow) {

        OpenSave.updateAllowed = true;
        var $form = $(
                '<div style="margin-top: 20px;margin-left: 10px;margin-bottom: 10px"><a  id="btnNew" href="#" class="btn btn-default">New</a>' +
                '<a id="btnOpen" href="#" class="btn btn-default" >Open</a>' +
                '<a id="btnSave" href="#" class="btn btn-default" >Save</a>' +
                '</div>'
        );

        var $save = $form.find("#save");
        var $load = $form.find("#load");
        var $fileList = $form.find("#fileList");
        var $fileName = $form.find("#fileName");

        $form.find('#btnNew').click(function () {
            OpenSave.doNew(dataflow)
        });
        $form.find('#btnSave').click(function () {
            OpenSave.doSave(dataflow)
        });
        $form.find('#btnOpen').click(function () {
            OpenSave.doOpen(dataflow)
        });


        dataflow.addPlugin({
            id: "opensave",
            label: "opensave",
            name: "",
            menu: $form,
            icon: "folder-open",
            pinned: true
        });


    };

    var CURRENT_FILENAME = 'newflow.fbp';

    OpenSave.doNew = function (dataflow) {
        var templ = $('<form><fieldset><label>File Name</label><input id="fileName" class="form-control" type="text" value="newflow.fbp"></fieldset></form>');
        var btnact = $(' <button type="button" id="dlg-btn-action" class="btn btn-primary" data-dismiss="modal">'
            + 'New File </button>');
        var dlg = $('#dlg');
        var dialog = dlg.find('#dlg-body');
        var title = dlg.find('#dlg-title');
        var footer = $('#dlg-modal-footer');

        title.text('New Flow Diagram');
        dialog.empty();
        dialog.html(templ);
        footer.html(btnact);
        dlg.modal('show');
        btnact.on("click", function () {
            CURRENT_FILENAME = $('#fileName').val();
            OpenSave.updateGraph("{\"nodes\":[],\"edges\":[]}", dataflow);
            new PNotify({
                title: 'Created',
                text: 'New Flow Program had been initialized',
                opacity: .8
            });
        });

    };

    OpenSave.doList = function (dataflow) {
        $.ajax({ type: 'get',
            url: "/rest/fbp/listtree",
            dataType: "json"
        }).done(function (list) {
            //var dx=JSON.parse(data);
            console.log("get json data");
            $('#fileTree').treeview({
                data: list["nodes"], onNodeSelected: function (event, node) {
                    //$('#event_output').prepend('<p>You clicked ' + node.text + '</p>');
                    var str = node.path;
                    if (str.match(/json$/)) {
                        console.log(str);//getFile(str);
                        CURRENT_FILENAME = str;
                    }

                }
            });
        });
    };

    function replaceAll(find, replace, str) {
        console.log("replaceall:" + str);
        return str.replace(new RegExp(find, 'g'), replace);
    }


    OpenSave.doLoad = function (dataflow) {
        var xname = replaceAll("/", "|", CURRENT_FILENAME);
        $.ajax({type: 'get', dataType: "text", url: '/rest/fbp/get/' + xname})
            .done(function (data) {

                OpenSave.updateGraph(data, dataflow);
                $('.dataflow-port-label.in').tooltip();

                new PNotify({
                    title: 'Loaded',
                    text: 'Flow Program Data had been loaded',
                    opacity: .8
                });
            });
    };

    OpenSave.doSave = function (dataflow) {
        var xname = replaceAll("/", "|", CURRENT_FILENAME);
        var data = JSON.stringify(dataflow.graph.toJSON(), null, "  ");

        $.ajax({type: 'post', dataType: "json", url: '/rest/fbp/save/' + xname + "/" + encodeURIComponent(data)})
            .done(function (data) {
                new PNotify({
                    title: 'Saved',
                    text: 'Data had been saved to ' + data,
                    opacity: .8
                });
            });


    };

    OpenSave.doOpen = function (dataflow) {

        var templ = $('<div style="height: 400px;overflow: auto"><div id="fileTree" ></div></div>');
        var dlg = $('#dlg');
        var dialog = dlg.find('#dlg-body');
        var title = dlg.find('#dlg-title');
        var btnact = $(' <button type="button" id="dlg-btn-action" class="btn btn-primary" data-dismiss="modal">'
            + 'Open File </button>');
        title.text('Open Flow Diagram');
        dialog.empty();
        dialog.html(templ);
        var footer = $('#dlg-modal-footer');
        footer.html(btnact);
        dlg.modal('show');
        OpenSave.doList(dataflow);
        btnact.click(function () {
            OpenSave.doLoad(dataflow);
        });
    };

    OpenSave.updateGraph = function ($code, dataflow) {

        var graph;
        try {
            graph = JSON.parse($code);
        } catch (error) {
            dataflow.log("Invalid JSON");
            alert("failed loading :" + error);
            return false;
        }

        if (graph) {
            var g = dataflow.loadGraph(graph);
            g.trigger("change");
        }

    };

    OpenSave.loadJson = function (dataflow) {
        var fileTree = $('#fileTree');
        console.log(fileTree.val());

        if (fileTree.val() == null) {
            return;
        }

    };

}(Dataflow) );