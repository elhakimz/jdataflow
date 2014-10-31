<%@ page import="org.hakim.fbp.common.FbpCompJsBuilder" %>
<%--
  Developer: abiel
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <title>Dataflow graph editor</title>
    <meta name="viewport" content="user-scalable=no, width=device-width, initial-scale=1, maximum-scale=1">
    <script src="libs/jquery-2.1.1.min.js"></script>
    <script src="libs/modernizr-latest.js"></script>
    <script src="libs/jquery.json-2.2.min.js"></script>


    <link rel="stylesheet" href="themes/default/font-proximanova.css"/>
    <link rel="stylesheet" href="themes/default/font-awesome.css"/>
    <link rel="stylesheet" href="themes/default/dataflow.css"/>
    <link rel="stylesheet" href="themes/default/modules/node.css"/>
    <link rel="stylesheet" href="themes/default/modules/edge.css"/>
    <link rel="stylesheet" href="themes/default/modules/port.css"/>
    <link rel="stylesheet" href="themes/default/modules/card.css"/>
    <link rel="stylesheet" href="themes/default/modules/jqui.css"/>
    <link rel="stylesheet" href="themes/default/modules/search.css"/>
    <link rel="stylesheet" href="libs/custom.css"/>
    <link href="themes/bootstrap.min.css" rel="stylesheet"/>
    <link rel="stylesheet" href="libs/pnotify.custom.min.css"/>
    <link href="libs/treeview/bootstrap-treeview.css" rel="stylesheet">

</head>

<body>
<!-- Third-Party Libraries -->

<script src="libs/jquery-ui-1.11.1/jquery-ui.js"></script>
<%--<link href="libs/jquery-ui-1.11.1/jquery-ui.css" rel="stylesheet">--%>
<script type="application/javascript" src="themes/bootstrap.js"></script>
<script type="application/javascript" src="libs/pnotify.custom.min.js"></script>
<script type="application/javascript" src="libs/treeview/bootstrap-treeview.js"></script>

<script src="libs/jquery.ui.touch-punch.js"></script>
<script src="libs/underscore.js"></script>
<script src="libs/backbone-min.js"></script>

<script src="libs/hammer.min.js"></script>
<script src="libs/CircularBuffer.js"></script>
<script src="libs/jquery.dform-1.1.0.min.js"></script>

<!--MAIN -->
<script src="src/dataflow.js"></script>
<script src="src/state.js"></script>

<!-- MODEL -->
<script src="src/modules/graph.js"></script>
<script src="src/modules/node.js"></script>
<script src="src/modules/input.js"></script>
<script src="src/modules/output.js"></script>
<script src="src/modules/edge.js"></script>

<!-- VIEWS -->
<script src="src/modules/graph-view.js"></script>
<script src="src/modules/node-view.js"></script>
<script src="src/modules/input-view.js"></script>
<script src="src/modules/output-view.js"></script>
<script src="src/modules/edge-view.js"></script>

<!-- CARDS -->
<script src="src/modules/card.js"></script>
<script src="src/modules/card-view.js"></script>
<script src="src/modules/menucard.js"></script>
<script src="src/modules/menucard-view.js"></script>
<script src="src/modules/node-inspect-view.js"></script>
<script src="src/modules/edge-inspect-view.js"></script>

<!--  PLUGINS -->
<script src="src/plugins/menu.js"></script>
<script src="src/plugins/edit.js"></script>
<script src="src/plugins/elements.js"></script>
<script src="src/plugins/library.js"></script>
<script src="src/plugins/view-source.js"></script>
<script src="src/plugins/log.js"></script>
<script src="src/plugins/inspector.js"></script>
<script src="src/plugins/keybinding.js"></script>
<script src="src/plugins/notification.js"></script>
<script src="src/plugins/search.js"></script>
<script src="src/plugins/open-save.js"></script>
<script src="src/plugins/run-graph.js"></script>


<!-- Nodes (some basics to extend) -->
<script src="src/nodes/base.js"></script>
<script src="src/nodes/base-resizable.js"></script>

<!-- Nodes (subgraph functionality) -->
<script src="src/nodes/dataflow-subgraph.js"></script>

<script src="src/nodes/test.js"></script>
<script src="src/nodes/dataflow-input.js"></script>
<script src="src/nodes/dataflow-output.js"></script>
<!-- components, can be generated from servlet -->

<%= FbpCompJsBuilder.getInstance().getFbpCompScriptList("com.jpmorrsn.fbp.components")%>
<%= FbpCompJsBuilder.getInstance().getFbpCompScriptList("com.jpmorrsn.fbp.text")%>
<%= FbpCompJsBuilder.getInstance().getFbpCompScriptList("com.jpmorrsn.fbp.examples.components")%>
<%= FbpCompJsBuilder.getInstance().getFbpCompScriptList("org.javafbp.runtime.components.base")%>
<%= FbpCompJsBuilder.getInstance().getFbpCompScriptList("org.javafbp.runtime.components.flow")%>
<%= FbpCompJsBuilder.getInstance().getFbpCompScriptList("org.javafbp.runtime.components.db")%>
<%= FbpCompJsBuilder.getInstance().getFbpCompScriptList("org.javafbp.runtime.components.ftl")%>
<%= FbpCompJsBuilder.getInstance().getFbpCompScriptList("org.javafbp.runtime.components.json")%>
<%= FbpCompJsBuilder.getInstance().getFbpCompScriptList("org.javafbp.runtime.components.os")%>
<%= FbpCompJsBuilder.getInstance().getFbpCompScriptList("org.javafbp.runtime.components.net")%>
<%= FbpCompJsBuilder.getInstance().getFbpCompScriptList("org.javafbp.runtime.components.zen")%>
<%= FbpCompJsBuilder.getInstance().getFbpCompScriptList("org.javafbp.runtime.components.io")%>

<script src="libs/ace/ace.js"></script>
<script src="libs/ace/theme-eclipse.js"></script>
<script src="libs/ace/theme-solarized_dark.js"></script>
<script src="libs/ace/theme-twilight.js"></script>

<script src="libs/ace/mode-groovy.js"></script>
<script src="libs/ace/mode-javascript.js"></script>
<script src="libs/ace/mode-sql.js"></script>
<script src="libs/ace/mode-xml.js"></script>
<script src="libs/jquery-ace.min.js"></script>
<script src="libs/jsoneditor.min.js"></script>
<script>
    $(document).ready(function ($) {
        // Spin up app
        var dataflow = new window.Dataflow();
        // Load test graph
        var g;
        g = dataflow.loadGraph(
                {
                    "nodes": [
                        {
                            "id": 2,
                            "label": "WriteToConsole2",
                            "type": "core.WriteToConsole",
                            "javaType": "com.jpmorrsn.fbp.components.WriteToConsole",
                            "description": "",
                            "x": 685,
                            "y": 135,
                            "state": {},
                            "w": 200,
                            "h": 75
                        },
                        {
                            "id": 1,
                            "label": "StartsWith1",
                            "type": "core.StartsWith",
                            "javaType": "com.jpmorrsn.fbp.components.StartsWith",
                            "description": "",
                            "x": 397,
                            "y": 135,
                            "state": {},
                            "w": 200,
                            "h": 150
                        }
                    ],
                    "edges": [
                        {
                            "source": {
                                "node": 1,
                                "port": "ACC"
                            },
                            "target": {
                                "node": 2,
                                "port": "IN"
                            },
                            "route": 4
                        }
                    ]
                }
        );
        g.trigger("change");
        $.dform.addType("xbutton", function (options) {
            // Return a new button element that has all options that
            // don't have a registered subscriber as attributes
            return $("<button>").dform('attr', options).button();
        });
        $('.dataflow-port-label.in').tooltip();

    });

</script>

<div id="dlg" class="modal">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
                <h4 id="dlg-title" class="modal-title">Modal title</h4>
            </div>
            <div id="dlg-body" class="modal-body">
                <p>One fine body…</p>
            </div>
            <div id="dlg-modal-footer" class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
                <button type="button" id="dlg-btn-action" class="btn btn-primary" data-dismiss="modal">Save changes
                </button>
            </div>
        </div>
    </div>
</div>
</body>
</html>