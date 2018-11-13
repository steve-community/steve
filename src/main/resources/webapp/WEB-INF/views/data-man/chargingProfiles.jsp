<%@ include file="../00-header.jsp" %>
<script type="text/javascript">
    $(document).ready(function () {
        <%@ include file="../snippets/sortable.js" %>
    });
</script>
<div class="content">
    <div id="overview">
        <table class="res action">
            <thead>
            <tr>
                <th data-sort="string">Charging Profile ID</th>
                <th data-sort="string">Stack Level</th>
                <th data-sort="string">Description</th>
                <th data-sort="string">Profile Purpose</th>
                <th data-sort="string">Profile Kind</th>
                <th data-sort="string">Recurrency Kind</th>
                <th data-sort="date">Valid From</th>
                <th data-sort="date">Valid To</th>
                <th>
                    <form:form action="${ctxPath}/manager/chargingProfiles/add" method="get">
                        <input type="submit" class="blueSubmit" value="Add New">
                    </form:form>
                </th>
            </tr>
            </thead>
            <tbody>
            <c:forEach items="${profileList}" var="cp">
                <tr>
                    <td><a href="${ctxPath}/manager/chargingProfiles/details/${cp.chargingProfilePk}">${cp.chargingProfilePk}</a></td>
                    <td>${cp.stackLevel}</td>
                    <td>${cp.description}</td>
                    <td>${cp.profilePurpose}</td>
                    <td>${cp.profileKind}</td>
                    <td>${cp.recurrencyKind}</td>
                    <td data-sort-value="${cp.validFrom.millis}">${cp.validFrom}</td>
                    <td data-sort-value="${cp.validTo.millis}">${cp.validTo}</td>
                    <td>
                        <form:form action="${ctxPath}/manager/chargingProfiles/delete/${cp.chargingProfilePk}">
                            <input type="submit" class="redSubmit" value="Delete">
                        </form:form>
                    </td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </div>
</div>
</div>
<%@ include file="../00-footer.jsp" %>