function selectAll(selectBox) {    
	for (var i = 0; i < selectBox.options.length; i++) { 
		selectBox.options[i].selected = true; 
	} 	    
}
function selectNone(selectBox) {    
	for (var i = 0; i < selectBox.options.length; i++) { 
		selectBox.options[i].selected = false; 
	}
}
$(document).ready(function() {
	var menuItem = $("#dm-menu a");
	menuItem.click(function(){
		var attr = "#" + $(this).attr("name");
		$(attr).siblings().hide();
		$(attr).show();
		menuItem.removeAttr("class");
		$(this).addClass("highlight");
	});
});