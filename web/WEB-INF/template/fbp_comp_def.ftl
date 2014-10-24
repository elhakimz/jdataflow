/**
* generated by ${user}
*/

( function(Dataflow) {

// Dependencies
var BaseResizable = Dataflow.prototype.node("base-resizable");
var Test = Dataflow.prototype.node("${model.name}");
Test.description="${model.description}";
Test.icon='${model.icon}';
Test.Model = BaseResizable.Model.extend({
defaults: function(){

 var defaults = BaseResizable.Model.prototype.defaults.call(this);
 defaults.color="${model.color}";
 defaults.javaType="${model.javaType}";
 defaults.type = "${model.name}";
 defaults.w = ${model.width};
 defaults.h = ${model.height};
 return defaults;
 },

 initialize: function(options) {
   if (this.get("label")===""){
      var lbl="${model.label}";
      this.set({label:lbl+this.id});
   }
   // super
BaseResizable.Model.prototype.initialize.call(this, options);
 },

inputs:[
<#list model.inputs as input>
{
 id: "${input.name}",
 type: "${input.type}",
 description:"${input.description}",
 widget:"${input.widget}",
 multiple:${input.multiple}
},
</#list>

],


outputs:[
<#list model.outputs as output>
{
  id: "${output.name}",
  type: "${output.type}",
  description:"${output.description}",
  multiple:${output.multiple}
},
</#list>

]

});

} (Dataflow));
