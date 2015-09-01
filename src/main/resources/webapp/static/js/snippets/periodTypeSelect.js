var ptSel = $("#periodTypeSelect");
function togglePeriodType(ptSel) {
    if (ptSel.find("option:selected").text() == "From/To") {
        $("#intervalPeriodTypeFrom").prop("disabled", false);
        $("#intervalPeriodTypeTo").prop("disabled", false);
    } else {
        $("#intervalPeriodTypeFrom").prop("disabled", true);
        $("#intervalPeriodTypeTo").prop("disabled", true);
    }
}

togglePeriodType(ptSel);
ptSel.change(function() {
    togglePeriodType(ptSel);
});