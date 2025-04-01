var updateSelection = $("#updateType");
changeMultiSelects(updateSelection.val());
updateSelection.change(function() {
    changeMultiSelects(this.value);
});

function changeMultiSelects(value) {
    if (value == 'DIFFERENTIAL') {
        $("#addUpdateList, #deleteList").prop("disabled", false);
        $("#sendEmptyListWhenFull").prop("disabled", true);

    } else if (value == 'FULL') {
        selectNone(document.getElementById('addUpdateList'));
        selectNone(document.getElementById('deleteList'));
        $("#addUpdateList, #deleteList").prop("disabled", true);
        $("#sendEmptyListWhenFull").prop("disabled", false);
    }
}