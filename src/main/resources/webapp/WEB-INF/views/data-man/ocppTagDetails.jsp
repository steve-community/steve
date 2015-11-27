<%@ include file="../00-header.jsp" %>
<script type="text/javascript">
    $(document).ready(function() {
        <%@ include file="../snippets/dateTimePicker-future.js" %>
    });
</script>
<spring:hasBindErrors name="ocppTagForm">
    <div class="error">
        Error while trying to update an Ocpp tag:
        <ul>
            <c:forEach var="error" items="${errors.allErrors}">
                <li>${error.defaultMessage}</li>
            </c:forEach>
        </ul>
    </div>
</spring:hasBindErrors>
<div class="content"><div>
    <section><span>Ocpp Tag Details</span></section>
    <form:form action="/steve/manager/ocppTags/update" modelAttribute="ocppTagForm">

        <table class="userInput">
            <tr>
                <td>ID Tag:</td>
                <td>
                    <form:input path="idTag" readonly="true"/>
                    <a class="tooltip" href="#"><img src="/steve/static/images/info.png" style="vertical-align:middle">
                        <span>This field is set when adding an Ocpp Tag, and cannot be changed later</span>
                    </a>
                </td>
            </tr>
            <tr>
                <td>Parent ID Tag:</td>
                <td><form:select path="parentIdTag" items="${idTagList}"/></td>
            </tr>
            <tr><td>Expiry Date/Time:</td><td><form:input path="expiration" cssClass="dateTimePicker"/></td></tr>
            <tr><td>In Transaction?:</td><td>${inTransaction}</td></tr>
            <tr><td>Block the ID Tag:</td>
                <td><form:radiobutton path="blocked" value="false"/> false</td>
            </tr>
            <tr>
                <td></td>
                <td><form:radiobutton path="blocked" value="true"/> true</td>
            </tr>
            <tr><td>Additional Note:</td><td><form:input path="note"/></td></tr>
            <tr><td></td>
                <td id="add_space">
                    <input type="submit" name="update" value="Update">
                    <input type="submit" name="backToOverview" value="Back to Overview">
                </td>
            </tr>
        </table>

    </form:form>
</div></div>
<%@ include file="../00-footer.jsp" %>