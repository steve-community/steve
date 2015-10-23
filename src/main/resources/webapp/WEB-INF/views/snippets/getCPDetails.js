$("#gdb").click(function() {
	var ddiv = $("#details-div");
	ddiv.html("<br>Loading...");
	$.getJSON("/steve/manager/ajax/" + $("#cbi").val() + "/details", function(data) {
		ddiv.html("<table id='details' class='cpd'><thead><tr><th>Charge Point Details</th><th></th></tr></thead></table>");
		var table = $("#details");
		$.each(data, function(key, val) {
			$("<tr>").appendTo(table)
				.append($("<td>").text(key))
				.append($("<td>").text(val));
		});
	});
});