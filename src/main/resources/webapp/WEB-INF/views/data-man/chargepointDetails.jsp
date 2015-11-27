<%@ include file="../00-header.jsp" %>
<spring:hasBindErrors name="chargePointForm">
    <div class="error">
        Error while trying to update a charge point:
        <ul>
            <c:forEach var="error" items="${errors.allErrors}">
                <li>${error.defaultMessage}</li>
            </c:forEach>
        </ul>
    </div>
</spring:hasBindErrors>
<div class="content"><div>
    <section><span>
        Charge Point Details
        <a class="tooltip" href="#"><img src="/steve/static/images/info.png" style="vertical-align:middle">
            <span>Read-only fields are updated by the charge point.</span>
        </a>
    </span></section>
        <form:form action="/steve/manager/chargepoints/update" modelAttribute="chargePointForm">

            <form:hidden path="chargeBoxPk" readonly="true"/>
            <table class="userInput">
                <thead><tr><th>OCPP</th><th></th></thead>
                <tbody>
                    <tr>
                        <td>ChargeBox ID:</td>
                        <td>
                            <form:input path="chargeBoxId" readonly="true" />
                            <a class="tooltip" href="#"><img src="/steve/static/images/info.png" style="vertical-align:middle">
                                <span>This field is set when adding a charge point, and cannot be changed later</span>
                            </a>
                        </td>
                    </tr>
                    <tr><td>Endpoint Address:</td><td>${cp.chargeBox.endpointAddress}</td></tr>
                    <tr><td>Ocpp Protocol:</td><td>${cp.chargeBox.ocppProtocol}</td></tr>
                    <tr><td>Charge Point Vendor:</td><td>${cp.chargeBox.chargePointVendor}</td></tr>
                    <tr><td>Charge Point Model:</td><td>${cp.chargeBox.chargePointModel}</td></tr>
                    <tr><td>Charge Point Serial Number:</td><td>${cp.chargeBox.chargePointSerialNumber}</td></tr>
                    <tr><td>Charge Box Serial Number:</td><td>${cp.chargeBox.chargePointSerialNumber}</td></tr>
                    <tr><td>Firewire Version:</td><td>${cp.chargeBox.fwVersion}</td></tr>
                    <tr><td>Firewire Update Timestamp:</td><td>${cp.chargeBox.fwUpdateTimestamp}</td></tr>
                    <tr><td>Iccid:</td><td>${cp.chargeBox.iccid}</td></tr>
                    <tr><td>Imsi:</td><td>${cp.chargeBox.imsi}</td></tr>
                    <tr><td>Meter Type:</td><td>${cp.chargeBox.meterType}</td></tr>
                    <tr><td>Meter Serial Number:</td><td>${cp.chargeBox.meterSerialNumber}</td></tr>
                    <tr><td>Diagnostics Status:</td><td>${cp.chargeBox.diagnosticsStatus}</td></tr>
                    <tr><td>Diagnostics Timestamp:</td><td>${cp.chargeBox.diagnosticsTimestamp}</td></tr>
                    <tr><td>Last Hearbeat Timestamp:</td><td>${cp.chargeBox.lastHeartbeatTimestamp}</td></tr>
                </tbody>
            </table>

            <form:hidden path="address.addressPk" readonly="true"/>
            <table class="userInput">
                <thead><tr><th>Address</th><th></th></thead>
                <tr><td>Street and House Number:</td><td><form:input path="address.streetAndHouseNumber"/></td></tr>
                <tr><td>Zip code:</td><td><form:input path="address.zipCode"/></td></tr>
                <tr><td>City:</td><td><form:input path="address.city"/></td></tr>
                <tr><td>Country:</td><td><form:input path="address.country"/></td></tr>
            </table>

            <table class="userInput">
                <thead><tr><th>Misc.</th><th></th></thead>
                <tr><td>Description:</td><td><form:input path="description"/></td></tr>
                <tr><td>Latitude:</td><td><form:input path="locationLatitude"/></td></tr>
                <tr><td>Longitude:</td><td><form:input path="locationLongitude"/></td></tr>
                <tr><td>Additional Note:</td><td><form:input path="note"/></td></tr>
                <tr><td></td>
                    <td id="add_space">
                        <input type="submit" name="update" value="Update">
                        <input type="submit" name="backToOverview" value="Back to Overview">
                    </td></tr>
            </table>

    </form:form>
</div></div>
<%@ include file="../00-footer.jsp" %>