function common_showInfo(mytext){
    var htm='<div id="show-info" class="modal" ><div class="panel panel-info">'+
        '<div class="panel-heading">'
        +'<h3 class="panel-title">Information</h3></div'
        +'<div class="panel-body" id="common-panel-body">Panel content</div></div></div>';
    var pnl=$(htm);
    pnl.find('#common-panel-body').text(mytext);
    pnl.modal({show:true}).draggable();
}
