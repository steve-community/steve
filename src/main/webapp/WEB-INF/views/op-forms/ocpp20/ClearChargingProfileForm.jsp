<form:form modelAttribute="params">
    <section><span>Charge Points</span></section>
    <%@ include file="../../00-cp-multiple.jsp" %>

    <section><span>Parameters</span></section>
    <table class="userInput">
        <tr>
            <td>Charging Profile ID:</td>
            <td><form:input path="chargingProfileId" placeholder="numeric value (optional)"/></td>
        </tr>
        <tr>
            <td></td>
            <td>
                <div class="submit-button"><input type="submit" value="Perform"></div>
            </td>
        </tr>
    </table>
</form:form>
