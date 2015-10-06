$("#update-chargeBoxId").change(function() {
    var cp = $(this).find("option:selected").text();
    $.getJSON("/steve/manager/ajax/" + cp + "/detailsForUpdate", function(data) {
        var note = $("#update-note");
        if (note.prop("disabled")) {
            $("#update-submit").prop("disabled", false);
            note.prop("disabled", false);
        }
        note.val(data.note);
    });
});
