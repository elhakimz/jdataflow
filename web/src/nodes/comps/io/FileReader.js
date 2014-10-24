/**
 * Created by abiel on 9/18/14.
 */
( function(Dataflow) {

    // Dependencies
    var BaseResizable = Dataflow.prototype.node("base-resizable");
    var Test = Dataflow.prototype.node("io.FileReader");

    Test.Model = BaseResizable.Model.extend({
        defaults: function(){
            var defaults = BaseResizable.Model.prototype.defaults.call(this);
            defaults.type = "io.FileReader";
            defaults.w = 200;
            defaults.h = 100;
            return defaults;
        },

        inputstring: function(value){
            this.send("output", value + " test");
        },

        inputs:[
            {
                id: "file",
                type: "string"
            }
        ],
        outputs:[
            {
                id: "output",
                type: "all"
            }
        ]
    });

    // Test.View = BaseResizable.View.extend({
    //     initialize: function(options){
    //         BaseResizable.View.prototype.initialize.call(this, options);
    //        // this.$inner.text("view.$inner");
    //     }
    // });

}(Dataflow) );
