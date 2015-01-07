$("#chargePointSelectList").change(function() {
	var cp = $(this).val().toString().split(';')[0];
	$.getJSON("/steve/manager/ajax/getReservationIds?chargeBoxId=" + cp, function(data) {
		var options = "";
		$.each(data, function() {
			options += "<option value='" + this + "'>" + this + "</option>";
		});
		var select = $("#reservationId");
		select.prop("disabled", false);
		select.html(options);
	});
});
