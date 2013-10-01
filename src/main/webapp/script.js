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

function showElements() {
	var elem = document.getElementById("diffElements");
	var s = "<input type='button' value='Add' onClick='addElement()'><br>";
	elem.innerHTML = s;
	addElement();
}

function removeElements() {
	var elem = document.getElementById("diffElements");
	elem.innerHTML = '';
}

function removeElement(node) {
	var elem = document.getElementById("diffElements");
	elem.removeChild(node);
}

function addElement() {
	var elem = document.getElementById("diffElements");	

	var input = document.createElement("input");
	input.setAttribute("type", "text");
	input.setAttribute("name", "idTag");
	input.setAttribute("placeholder", "idTag");

	var select = document.createElement("select");
	select.setAttribute("name", "idTagData");

	var option1 = document.createElement("option");
	option1.setAttribute("value", "AddUpdate");
	option1.innerHTML = "Add/Update";
	select.appendChild(option1);

	var option2 = document.createElement("option");
	option2.setAttribute("value", "Remove");
	option2.innerHTML = "Remove";
	select.appendChild(option2);
	
	var rem = document.createElement("a");
	rem.setAttribute("title", "Delete this row");
	rem.setAttribute("onclick", "removeElement(this.parentNode)");
	
	var text = document.createTextNode("x");
	rem.appendChild(text);

	var span = document.createElement("span");
	span.appendChild(input);
	span.appendChild(document.createTextNode(" "));
	span.appendChild(select);
	span.appendChild(document.createTextNode(" "));
	span.appendChild(rem);
	span.appendChild(document.createElement("br"));
	
	elem.appendChild(span);
}
