( function(Dataflow) {

    // Dependencies
    var Base = Dataflow.prototype.node("base");
    var DataflowOutput = Dataflow.prototype.node("io.Console");

    DataflowOutput.Model = Base.Model.extend({
        defaults: function () {
            var defaults = Base.Model.prototype.defaults.call(this);
            defaults.type = "io.Console";
            defaults["output-type"] = "all";
            return defaults;
        },
        initialize: function (options){
            if (this.get("label")===""){
                this.set({label:"console"+this.id});
            }
            // super
            Base.Model.prototype.initialize.call(this, options);
        },
        inputdata: function (data) {
            // Forward data to parent graph
            this.get("parentNode").send(this.id, data);
        },
        toJSON: function(){
            var json = Base.Model.prototype.toJSON.call(this);
            json["output-type"] = this.get("output-type");
            return json;
        },
        inputs:[
            {
                id: "data",
                type: "all"
            }
        ],
        outputs:[
            // {
            //   id: "data",
            //   type: "all"
            // }
        ]
    });

    // DataflowOutput.View = Base.View.extend({
    // });

}(Dataflow) );
