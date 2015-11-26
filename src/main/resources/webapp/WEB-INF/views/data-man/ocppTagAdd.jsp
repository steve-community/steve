<%@ include file="../00-header.jsp" %>
<script type="text/javascript">
    $(document).ready(function() {
        <%@ include file="../snippets/dateTimePicker-future.js" %>
    });
</script>
<spring:hasBindErrors name="ocppTagForm">
    <div class="error">
        Error while trying to add an Ocpp Tag:
        <ul>
            <c:forEach var="error" items="${errors.allErrors}">
                <li>${error.defaultMessage}</li>
            </c:forEach>
        </ul>
    </div>
</spring:hasBindErrors>
<div class="content"><div>
<section><span>Add Ocpp Tag</span></section>
    <form:form action="/steve/manager/ocppTags/add" modelAttribute="ocppTagForm">

        <table class="userInput">
            <tbody>
            <tr>
                <td>ID Tag:</td>
                <td>
                    <form:input path="idTag"/>
                    <a class="tooltip" href="#"><img src="/steve/static/images/info.png" style="vertical-align:middle">
                        <span>This field is set when adding an Ocpp Tag, and cannot be changed later</span>
                    </a>
                </td>
            </tr>
            <tr>
                <td>Parent ID Tag:</td>
                <td>
                    <form:select path="parentIdTag">
                        <option value="-- Empty --" selected="selected">-- Empty --</option>
                        <form:options items="${idTagList}" />
                    </form:select>
                </td>
            </tr>
            <tr><td>Expiry Date/Time:</td><td><form:input path="expiration" cssClass="dateTimePicker"/></td></tr>
            <tr><td>Additional Note:</td><td><form:input path="note"/></td></tr>
            <tr><td></td>
                <td id="add_space">
                    <input type="submit" name="add" value="Add">
                    <input type="submit" name="backToOverview" value="Back to Overview">
                </td></tr>
            </tbody>
        </table>

    </form:form>
</div></div>
<%@ include file="../00-footer.jsp" %>