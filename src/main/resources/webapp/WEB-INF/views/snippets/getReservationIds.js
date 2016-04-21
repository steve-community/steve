$("#chargePointSelectList").change(function() {
	var cp = $(this).find("option:selected").text();
	$.getJSON("${ctxPath}/manager/ajax/" + cp + "/reservationIds", function(data) {
		var options = "";
		$.each(data, function() {
			options += "<option value='" + this + "'>" + this + "</option>";
		});
		var select = $("#reservationId");
		select.prop("disabled", false);
		select.html(options);
	});
});
