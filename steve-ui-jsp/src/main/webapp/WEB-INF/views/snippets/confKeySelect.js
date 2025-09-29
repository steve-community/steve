var keyTypeSelect = $("#keyType");
function togglePeriodType(keyTypeSelect) {
    if (keyTypeSelect.find("option:selected").text() == "Predefined") {
        $("#confKey").prop("disabled", false);
        $("#customConfKey").prop("disabled", true);
    } else {
        $("#confKey").prop("disabled", true);
        $("#customConfKey").prop("disabled", false);
    }
}

togglePeriodType(keyTypeSelect);
keyTypeSelect.change(function() {
    togglePeriodType(keyTypeSelect);
});