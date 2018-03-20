<%@ include file="../00-header.jsp" %>
<script type="text/javascript">
    $(document).ready(function () {
        <%@ include file="../snippets/sortable.js" %>
        $("#unknown").click(function () {
            $("#unknownTable, #overview").slideToggle(250);
        });
    });
</script>
<div class="content">
    <div>
    <section><span id="unknown" style="cursor: pointer">
    Unknown Tags
    <a class="tooltip" href="#"><img src="${ctxPath}/static/images/info.png" style="vertical-align:middle">
        <span>A list of RFID tags that were used in authorization attempts but were not present in database.</span>
    </a>
    </span></section>
    <div id="unknownTable" style="display: none">
        <table class="res add-margin-bottom">
            <thead>
            <tr>
                <th data-sort="string">ID Tag</th>
                <th data-sort="int"># of Attempts</th>
                <th data-sort="date">Last Attempt</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach items="${unknownList}" var="item">
                <tr>
                    <td>${item.key}</td>
                    <td>${item.numberOfAttempts}</td>
                    <td data-sort-value="${item.lastAttemptTimestamp.millis}">${item.lastAttemptTimestamp}</td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </div>

    <section><span>
    OCPP Tag Overview
    <a class="tooltip" href="#"><img src="${ctxPath}/static/images/info.png" style="vertical-align:middle">
        <span>Deleting an OCPP tag causes losing all related information including transactions and reservations.</span>
    </a>
    </span></section>
    <div id="overview">
        <form:form action="${ctxPath}/manager/ocppTags/query" method="get" modelAttribute="params">
            <table class="userInput">
                <tr>
                    <td>ID Tag:</td>
                    <td><form:select path="idTag">
                        <option value="" selected>All</option>
                        <form:options items="${idTagList}"/>
                    </form:select>
                    </td>
                </tr>
                <tr>
                    <td>Parent ID Tag:</td>
                    <td><form:select path="parentIdTag">
                        <option value="" selected>All</option>
                        <form:options items="${parentIdTagList}"/>
                    </form:select>
                    </td>
                </tr>
                <tr>
                    <td>Expired?:</td>
                    <td><form:select path="expired">
                        <form:options items="${expired}" itemLabel="value"/>
                    </form:select>
                    </td>
                </tr>
                <tr>
                    <td>In Transaction?:</td>
                    <td><form:select path="inTransaction">
                        <form:options items="${inTransaction}" itemLabel="value"/>
                    </form:select>
                    </td>
                </tr>
                <tr>
                    <td>Blocked?:</td>
                    <td><form:select path="blocked">
                        <form:options items="${blocked}" itemLabel="value"/>
                    </form:select>
                    </td>
                </tr>
                <tr>
                    <td></td>
                    <td id="add_space">
                        <input type="submit" value="Get">
                    </td>
                </tr>
            </table>
        </form:form>
        <br>
        <table class="res action">
            <thead>
            <tr>
                <th data-sort="string">ID Tag</th>
                <th data-sort="string">Parent ID Tag</th>
                <th data-sort="date">Expiry Date/Time</th>
                <th data-sort="string">In Transaction?</th>
                <th data-sort="string">Blocked?</th>
                <th>
                    <form:form action="${ctxPath}/manager/ocppTags/add" method="get">
                        <input type="submit" class="blueSubmit" value="Add New"/>
                    </form:form>
                </th>
            </tr>
            </thead>
            <tbody>
            <c:forEach items="${ocppTagList}" var="item">
                <tr>
                    <td><a href="${ctxPath}/manager/ocppTags/details/${item.ocppTagPk}">${item.idTag}</a></td>
                    <td>
                        <c:if test="${not empty item.parentIdTag}">
                            <a href="${ctxPath}/manager/ocppTags/details/${item.parentOcppTagPk}">${item.parentIdTag}</a>
                        </c:if>
                    </td>
                    <td data-sort-value="${item.expiryDateDT.millis}">${item.expiryDate}</td>
                    <td>${item.inTransaction}</td>
                    <td>${item.blocked}</td>
                    <td>
                        <form:form action="${ctxPath}/manager/ocppTags/delete/${item.ocppTagPk}">
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