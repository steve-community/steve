var idd = $("#idTagUpdate");
idd.change(function(){ 
	// get the selected idTag
	var str = "#" + idd.find("option:selected").text();
	// get the row
	var row = $("#usersTable").find(str);
	// enable input fields
	$("#update-pid, #update-exdateTime, #update-block-false, #update-block-true, #update-submit").prop("disabled", false);

    var parentIdCell = row.find("td:eq(1)").html();
    if (parentIdCell == "") {
        $("#update-pid").val("EMPTY-OPTION");
    } else {
        $("#update-pid").val(parentIdCell);
    }

	var expiryRow = row.find("td:eq(2)").html();
    if (expiryRow == "") {
        $("#update-exdateTime").val("");
    } else {
        var dateTime = expiryRow.split(' at ');
        $("#update-exdateTime").val(dehumanizeDate(dateTime[0]) + ' ' + dateTime[1]);
    }

	if (row.find("td:eq(4)").html() == "false") {
		$("#update-block-false").prop("checked", true);
	} else {
		$("#update-block-true").prop("checked", true);
	}

    var note = row.find("td:eq(5)").html();
    $("#update-note").val(note);
});

function dehumanizeDate(str) {
    var now = new Date();
    var day = now.getDate();
    var month = now.getMonth() + 1; // because internal representation of months is 0...11
    var year = now.getFullYear();

    if (str == 'Yesterday') {
        return format(day - 1, month, year);
    } else if (str == 'Today') {
        return format(day, month, year);
    } else if (str == 'Tomorrow') {
        return format(day + 1, month, year);
    } else {
        return str;
    }
}

function format(day, month, year) {
    return year + "-" + addLeadingZero(month) + "-" + addLeadingZero(day);
}

function addLeadingZero(num) {
    return num < 10 ? '0' + num : num;
}
