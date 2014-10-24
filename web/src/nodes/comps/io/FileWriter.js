
/**
 * Created by abiel on 9/18/14.
 */
( function(Dataflow) {

    // Dependencies
    var BaseResizable = Dataflow.prototype.node("base-resizable");
    var Test = Dataflow.prototype.node("io.FileWriter");

    Test.Model = BaseResizable.Model.extend({
        defaults: function(){
            var defaults = BaseResizable.Model.prototype.defaults.call(this);
            defaults.type = "io.FileWriter";
            defaults.w = 200;
            defaults.h = 150;
            return defaults;
        },

        inputstring: function(value){
            this.send("output", value + " test");
        },

        inputs:[
            {
                id: "content",
                type: "all"
            },
            {
                id: "file",
                type: "string"
            }
        ],
        outputs:[
            {
                id: "status",
                type: "int"
            }
        ]
    });

    // Test.View = BaseResizable.View.extend({
    //     initialize: function(options){
    //         BaseResizable.View.prototype.initialize.call(this, options);
    //         this.$inner.text("view.$inner");
    //     }
    // });

}(Dataflow) );
