var idd = $("#idTagUpdate");
idd.change(function(){ 
	// get the selected idTag
	var str = "#" + idd.find("option:selected").text();
	// get the row
	var row = $("#usersTable").find(str);
	// enable input fields
	$("#update-pid, #update-exdate, #update-extime, #update-block-false, #update-block-true, #update-submit").prop("disabled", false);		
	// iterate over the row cells and populate inputs
	$("#update-pid").val(row.find("td:eq(1)").html());
	var dateTime = row.find("td:eq(2)").html().split(' at ');
	$("#update-exdate").val(dateTime[0]);
	$("#update-extime").val(dateTime[1]);		
	if (row.find("td:eq(4)").html() == "false") {
		$("#update-block-false").prop("checked", true);
	} else {
		$("#update-block-true").prop("checked", true);
	}
});
