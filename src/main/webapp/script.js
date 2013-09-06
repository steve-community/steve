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