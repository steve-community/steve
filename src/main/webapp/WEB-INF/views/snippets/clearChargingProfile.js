var updateSelection = $("#filterType");
changeMultiSelects(updateSelection.val());
updateSelection.change(function() {
    changeMultiSelects(this.value);
});

function changeMultiSelects(value) {
    if (value == 'ChargingProfileId') {
        $("#chargingProfilePk").prop("disabled", false);
        $("#connectorId, #chargingProfilePurpose, #stackLevel").prop("disabled", true);

    } else if (value == 'OtherParameters') {
        $("#chargingProfilePk").prop("disabled", true);
        $("#connectorId, #chargingProfilePurpose, #stackLevel").prop("disabled", false);
    }
}