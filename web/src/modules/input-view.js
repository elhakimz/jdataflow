( function (Dataflow) {

    var Input = Dataflow.prototype.module("input");

    // Dependencies
    var Edge = Dataflow.prototype.module("edge");

    var template =
        '<span class="dataflow-port-plug in" title="drag to edit wire"></span>' + //i18n
        '<span class="dataflow-port-hole in" title="drag to make new wire"></span>' + //i18n
        '<label class="dataflow-port-label in" data-toggle="tooltip" data-placement="left" title="<%= description %>">' +
        '<%= label %>' +
        '</label>';

    var zoom = 1;

    Input.View = Backbone.View.extend({
        template: _.template(template),
        tagName: "li",
        className: "dataflow-port dataflow-in",
        events: {
            "click": "getTopEdge",
            "drop": "connectEdge",
            "dragstart .dataflow-port-hole": "newEdgeStart",
            "drag      .dataflow-port-hole": "newEdgeDrag",
            "dragstop  .dataflow-port-hole": "newEdgeStop",
            "dragstart .dataflow-port-plug": "changeEdgeStart",
            "drag      .dataflow-port-plug": "changeEdgeDrag",
            "dragstop  .dataflow-port-plug": "changeEdgeStop"
        },
        $input: null,
        initialize: function (options) {
            this.$el.html(this.template(this.model.toJSON()));
            this.$el.addClass(this.model.get("type"));

            this.parent = options.parent;

            // Reset hole position
            var node = this.parent.model;
            var graph = node.parentGraph;
            this.listenTo(node, "change:x change:y", function () {
                this._holePosition = null;
            }.bind(this));
            this.listenTo(graph, "change:panX change:panY", function () {
                this._holePosition = null;
            }.bind(this));

            var nodeState = node.get('state');
            if (nodeState && nodeState[this.model.id]) {
                this.$el.addClass('hasvalue');
            }

            if (!this.model.parentNode.parentGraph.dataflow.editable) {
                // No drag and drop
                return;
            }

            var self = this;
            this.$(".dataflow-port-plug").draggable({
                cursor: "pointer",
                helper: function () {
                    var helper = $('<span class="dataflow-port-plug in helper" />');
                    self.parent.graph.$el.append(helper);
                    return helper;
                },
                disabled: true,
                // To prevent accidental disconnects
                distance: 10,
                delay: 100
            });
            this.$(".dataflow-port-hole").draggable({
                cursor: "pointer",
                helper: function () {
                    var helper = $('<span class="dataflow-port-plug out helper" />')
                        .data({port: self.model});
                    self.parent.graph.$el.append(helper);
                    return helper;
                }
            });
            this.$el.droppable({
                accept: ".dataflow-port-plug.in, .dataflow-port-hole.out",
                activeClassType: "droppable-hover",
                refreshPositions: true
            });

            if (!this.model.parentNode.parentGraph.dataflow.inputs) {
                // No direct inputs
                return;
            }

            // Initialize direct input
            var type = this.model.get("type");
            var state = this.model.parentNode.get("state");
            var widget = this.model.get("widget");


            options = this.model.get("options");
            if (options !== undefined) {
                // Normalize options
                if (_.isString(options)) {
                    options = options.split(' ');
                    this.model.set('options', options);
                }
                if (_.isArray(options)) {
                    var o = {};
                    for (var i = 0; i < options.length; i++) {
                        o[options[i]] = options[i];
                    }
                    options = o;
                    this.model.set('options', options);
                }
            }
            var input = this.renderInput(type, options, widget);

            var val;
            if (state && state[this.model.id] !== undefined) {
                // Use the stored state
                val = state[this.model.id];
            } else if (this.model.get("value") !== undefined) {
                // Use the default
                val = this.model.get("value");
            }

            this.setInputValue(input, type, val);

            this.model.parentNode.on('change:state', function () {
                var state = this.model.parentNode.get('state');
                if (!state || state[this.model.id] === undefined) {
                    this.$el.removeClass('hasvalue');
                    return;
                }
                this.setInputValue(input, type, state[this.model.id]);
                this.$el.addClass('hasvalue');
            }.bind(this));

            var label = $('<label class="input-type-' + type + '">')
                .append(input)
                .prepend('<span>' + this.model.get("label") + "</span> ");
            this.$input = label;

            // Update connection state on the input field
            if (this.model.connected.length) {
                label.addClass('connected');
            }
            this.model.on('connected', function () {
                this.$input.addClass('connected');
            }, this);
            this.model.on('disconnected', function () {
                this.$input.removeClass('connected');
            }, this);
        },
        renderInput: function (type, options, widget) {
            var input;
            if (options) {
                input = $('<select class="input input-select">');
                for (var name in options) {
                    var option = $('<option value="' + options[name] + '">' + name + '</option>')
                        .data("val", options[name]);
                    input.append(option);
                }

                input.change(this.inputSelect.bind(this));
                input.find('select').selectmenu();
                return input;
            }


            function displayJsEditor(input, lang) {
                var actionButton = $('<button type="button" id="dlg-btn-action" class="btn btn-primary" data-dismiss="modal">Save changes</button>');
                var dlg = $(document).find('#dlg');
                var dialog = dlg.find('#dlg-body');
                var title = dlg.find('#dlg-title');
                var footer = dlg.find('#dlg-modal-footer');


                var foo = input.val();
                var textarea = $('<textarea class="js-code-area"  style="width: 100%; height: 400px">' + foo + '</textarea>');
                title.text('Edit JSON/JS');
                dialog.empty();
                dialog.html(textarea);
                dlg.draggable();
                dlg.modal('show');
                var jsCodeArea = dlg.find('.js-code-area');
                jsCodeArea.ace({ theme: 'twilight', lang: lang });
                footer.html(actionButton);
                actionButton.on("click", function () {
                    input.val(textarea.val());
                    input.trigger("change");
                });

            }

            function displaySelectFile(input, type) {
                var actionButton = $('<button type="button" id="dlg-btn-action" class="btn btn-primary" data-dismiss="modal">Select File</button>');
                var templ = $('<div style="height: 400px;overflow: auto"><div id="fileTree" ></div></div>');
                var dlg = $('#dlg');
                var dialog = dlg.find('#dlg-body');
                var title = dlg.find('#dlg-title');
                title.text('Open Flow Diagram');
                dialog.empty();
                dialog.html(templ);
                dlg.modal('show');
                var footer = dlg.find('#dlg-modal-footer');
                footer.html(actionButton);
                var sUrl = "/rest/common/" + type;

                $.ajax({ type: 'get',
                    url: sUrl,
                    dataType: "json"
                }).done(function (list) {
                    $(document).find('#fileTree').treeview({
                        data: list["nodes"],
                        onNodeSelected: function (event, node) {
                            input.val(node.path);
                            input.trigger("change");
                        }
                    });
                });

            }

            switch (type) {
                case 'int':
                case 'float':
                case 'number':
                    var attributes = {};
                    if (this.model.get("min") !== undefined) {
                        attributes.min = this.model.get("min");
                    }
                    if (this.model.get("max") !== undefined) {
                        attributes.max = this.model.get("max");
                    }
                    if (type === "int") {
                        attributes.step = 1;
                    }
                    input = $('<input type="number" class="input input-number">')
                        .attr(attributes)
                        .addClass(type === "int" ? "input-int" : "input-float");

                    if (type == 'int') {
                        input.change(this.inputInt.bind(this));
                    } else {
                        input.change(this.inputFloat.bind(this));
                    }

                    return input;
                case 'boolean':
                    input = $('<input type="checkbox" class="input input-boolean"><div class="input-boolean-checkbox"/>');
                    input.change(this.inputBoolean.bind(this));
                    return input;

                case 'bang':
                    input = $('<button class="input input-bang btn btn-sm" style="width: 20px;height: 30px">!</button>');
                    input.click(this.inputBang.bind(this));
                    return input;
                case 'date':
                    input = $('<input type="text" class="datepicker">');
                    input.datepicker();
                    input.change(this.inputDate.bind(this));
                    return input;

                default:
                    var xid = this.model.get("id");
                    var xWidget = '';
                    if (widget != '') {
                        xWidget = '<button id="btn-' + xid + '" class="btn btn-default glyphicon glyphicon-question-sign"></button>'
                    }

                    if (type == 'text') {
                        input = $('<textarea class="input input-string" style="width: 120px" rows="3"></textarea>' + xWidget);
                    } else {
                        input = $('<input class="input input-string" style="width: 120px">' + xWidget);
                    }
                    if (type == 'object' || type == 'conn') {
                        input.prop('disabled', true);
                    }

                    input.change(this.inputString.bind(this));
                    if (widget != '') {
                        $(document).on('click', '#btn-' + xid, function (event) {
                            if (widget == 'groovy-edit') {
                                displayJsEditor(input, 'groovy');
                            } else if (widget == 'xml-edit') {
                                displayJsEditor(input, 'xml');
                            } else if (widget == 'sql-edit') {
                                displayJsEditor(input, 'sql');
                            } else if (widget == 'js-edit') {
                                displayJsEditor(input, 'javascript');
                            } else if (widget == 'select-templ') {
                                displaySelectFile(input, 'listtempl');
                            } else if (widget == 'select-dir') {
                                displaySelectFile(input, 'listdir');
                            }
                        });
                    }
                    //input.after(xWidget);
                    return input;
            }
        },


        setInputValue: function (input, type, value) {
            if (!input) {
                return;
            }
            if ((input[0] != null) && (input[0].tagName === 'SELECT')) {
                $('option', input).each(function () {
                    var selectVal = $(this).data('val');
                    $(this).prop('selected', selectVal == value);
                });
                return;
            }
            if (type === 'boolean') {
                input.prop('checked', value);
                return;
            }
            if (type === 'object') {
                input.text(JSON.stringify(value, null, 2));
                return;
            }
            input.val(value);
        },
        inputSelect: function (e) {
            var val = $(e.target).find(":selected").data("val");
            this.model.parentNode.setState(this.model.id, val);
        },
        inputInt: function (e) {
            this.model.parentNode.setState(this.model.id, parseInt($(e.target).val(), 10));
        },
        inputFloat: function (e) {
            this.model.parentNode.setState(this.model.id, parseFloat($(e.target).val()));
        },

        inputString: function (e) {
            this.model.parentNode.setState(this.model.id, $(e.target).val());
        },

        inputDate: function (e) {
            this.model.parentNode.setState(this.model.id, $(e.target).val());
        },

        inputBoolean: function (e) {
            this.model.parentNode.setState(this.model.id, $(e.target).prop("checked"));
        },

        inputObject: function (e) {
            try {
                var obj = JSON.parse($(e.target).text());
                this.model.parentNode.setState(this.model.id, obj);
            } catch (err) {
                // TODO: We need error handling in the form
            }
        },
        inputBang: function () {
            this.model.parentNode.setBang(this.model.id);
        },
        render: function () {
            return this;
        },
        newEdgeStart: function (event, ui) {
            if (!ui) {
                return;
            }
            // Don't drag node
            event.stopPropagation();

            ui.helper.data({
                route: this.topRoute
            });
            this.previewEdgeNew = new Edge.Model({
                target: {
                    node: this.model.parentNode.id,
                    port: this.model.id
                },
                parentGraph: this.model.parentNode.parentGraph,
                preview: true,
                route: this.topRoute
            });
            this.previewEdgeNewView = new Edge.View({
                model: this.previewEdgeNew
            });
            var graphSVGElement = this.model.parentNode.parentGraph.view.$('.dataflow-svg-edges')[0];
            graphSVGElement.appendChild(this.previewEdgeNewView.el);

            zoom = this.model.parentNode.parentGraph.get('zoom');

            this.model.parentNode.parentGraph.view.startHighlightCompatible(this.model, true);
        },
        newEdgeDrag: function (event, ui) {
            if (!this.previewEdgeNewView || !ui) {
                return;
            }
            // Don't drag node
            event.stopPropagation();

            ui.position.top = event.clientY / zoom;
            ui.position.left = event.clientX / zoom;
            var df = this.model.parentNode.parentGraph.view.el;
            ui.position.left += df.scrollLeft;
            ui.position.top += df.scrollTop;
            this.previewEdgeNewView.render({
                left: ui.position.left - df.scrollLeft,
                top: ui.position.top - df.scrollTop
            });
            this.model.parentNode.parentGraph.view.sizeSVG();
        },
        newEdgeStop: function (event, ui) {
            // Don't drag node
            event.stopPropagation();

            // Clean up preview edge
            this.previewEdgeNewView.remove();
            delete this.previewEdgeNew;
            delete this.previewEdgeNewView;
            this.model.parentNode.parentGraph.view.stopHighlightCompatible(this.model, true);
        },
        getTopEdge: function () {
            var topEdge;
            var topZ = -1;
            if (this.isConnected) {
                // Will get the top matching edge
                this.model.parentNode.parentGraph.edges.each(function (edge) {
                    var thisZ = edge.get("z");
                    if (edge.target === this.model && thisZ > topZ) {
                        topEdge = edge;
                        topZ = thisZ;
                    }
                    if (edge.view) {
                        edge.view.unhighlight();
                    }
                }, this);
                if (topEdge && topEdge.view) {
                    topEdge.view.bringToTop();
                }
            }
            return topEdge;
        },
        changeEdgeStart: function (event, ui) {
            if (!ui) {
                return;
            }
            // Don't drag node
            event.stopPropagation();

            if (this.isConnected) {
                var changeEdge = this.getTopEdge();
                if (changeEdge) {
                    // Remove edge
                    changeEdge.remove();

                    // Make preview
                    if (ui) {
                        ui.helper.data({
                            port: changeEdge.source,
                            route: changeEdge.get("route")
                        });
                    }
                    this.previewEdgeChange = new Edge.Model({
                        source: changeEdge.get("source"),
                        route: changeEdge.get("route"),
                        parentGraph: this.model.parentNode.parentGraph,
                        preview: true
                    });
                    this.previewEdgeChangeView = new Edge.View({
                        model: this.previewEdgeChange
                    });
                    var graphSVGElement = this.model.parentNode.parentGraph.view.$('.dataflow-svg-edges')[0];
                    graphSVGElement.appendChild(this.previewEdgeChangeView.el);

                    zoom = this.model.parentNode.parentGraph.get('zoom');
                }
            }
        },
        changeEdgeDrag: function (event, ui) {
            if (!ui) {
                return;
            }
            // Don't drag node
            event.stopPropagation();

            if (this.previewEdgeChange) {
                this.previewEdgeChangeView.render(ui.offset);
                this.model.parentNode.parentGraph.view.sizeSVG();
            }
        },
        changeEdgeStop: function (event, ui) {
            // Don't drag node
            event.stopPropagation();

            // Clean up preview edge
            if (this.previewEdgeChange) {
                this.previewEdgeChangeView.remove();
                delete this.previewEdgeChange;
                delete this.previewEdgeChangeView;
            }
        },
        blur: function () {
            this.$el.addClass('blur');
        },
        unblur: function () {
            this.$el.removeClass('blur');
        },
        connectEdge: function (event, ui) {
            // Dropped to this el
            var otherPort = ui.helper.data("port");
            var oldLength = this.model.parentNode.parentGraph.edges.length;

            if (otherPort.parentNode.parentGraph.dataflow !== this.model.parentNode.parentGraph.dataflow) {
                // from another widget
                return false;
            }

            if (!this.model.canConnect()) {
                // Port declined the connection, abort
                return;
            }

            function getRouteForType(type) {
                switch (type) {
                    case 'int':
                    case 'float':
                    case 'number':
                        return 1;
                    case 'boolean':
                        return 2;
                    case 'date':
                    case 'object':
                        return 3;
                    case 'string':
                    case 'text':
                        return 4;
                    case 'dbconn':
                        return 5;
                    case 'map':
                        return 6;
                    case 'list':
                        return 7;
                    default:
                        return 0;
                }
            }

            function getDefaultRoute(fromType, toType) {
                if (fromType === 'all' && toType === 'all') {
                    return 0;
                }
                if (fromType === 'all') {
                    return getRouteForType(toType);
                }
                return getRouteForType(fromType);
            }

            var route = getDefaultRoute(this.model.get('type'), otherPort.get('type'));
            this.model.parentNode.parentGraph.edges.add({
                id: otherPort.parentNode.id + ":" + otherPort.id + "::" + this.model.parentNode.id + ":" + this.model.id,
                parentGraph: this.model.parentNode.parentGraph,
                source: {
                    node: otherPort.parentNode.id,
                    port: otherPort.id
                },
                target: {
                    node: this.model.parentNode.id,
                    port: this.model.id
                },
                route: route
            });
            // Tells changeEdgeStop to remove to old edge
            ui.helper.data("removeChangeEdge", (oldLength < this.model.parentNode.parentGraph.edges.length));
        },
        _holePosition: null,
        holePosition: function () {
            // this._holePosition gets reset when graph panned or node moved
            if (!this._holePosition) {
                if (!this.parent) {
                    this.parent = this.options.parent;
                }
                var node = this.parent.model;
                var graph = node.parentGraph;
                var $graph = this.parent.graph.$el;
                var index = this.$el.index();
                var left = graph.get("panX") + node.get("x") + 18;
                var top = graph.get("panY") + node.get("y") + 48 + index * 20;
                this._holePosition = { left: left, top: top };
            }
            return this._holePosition;
        },
        isConnected: false,
        plugSetActive: function () {
            try {
                this.$(".dataflow-port-plug").draggable("enable");
            } catch (e) {
            }
            this.$(".dataflow-port-plug, .dataflow-port-hole").addClass("active");
            this.isConnected = true;
        },
        plugCheckActive: function () {
            var topEdge;
            var topEdgeZ = -1;
            this.model.parentNode.parentGraph.edges.each(function (edge) {
                if (edge.target === this.model) {
                    var z = edge.get("z");
                    if (z > topEdgeZ) {
                        topEdge = edge;
                        topEdgeZ = z;
                    }
                }
            }, this);
            if (topEdge) {
                this.bringToTop(topEdge);
            } else {
                try {
                    this.$(".dataflow-port-plug").draggable("disable");
                } catch (e) {
                }
                this.$(".dataflow-port-plug, .dataflow-port-hole").removeClass("active");
                this.isConnected = false;
            }
        },
        topRoute: 0,
        bringToTop: function (edge) {
            var route = edge.get("route");
            if (route !== undefined) {
                this.$(".dataflow-port-hole, .dataflow-port-plug").removeClass("route" + this.topRoute);
                this.$(".dataflow-port-hole, .dataflow-port-plug").addClass("route" + route);
                this.topRoute = route;
            }
        }
    });

    Input.CollectionView = Backbone.CollectionView.extend({
        tagName: "ul",
        itemView: Input.View
    });

}(Dataflow) );
