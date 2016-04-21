<%@ include file="../00-header.jsp" %>
<div class="content"><div>
        <section><span>
        OCPP Tag Overview
        <a class="tooltip" href="#"><img src="${ctxPath}/static/images/info.png" style="vertical-align:middle">
            <span>Deleting an OCPP tag causes losing all related information including transactions and reservations.</span>
        </a>
        </span></section>
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
            <tr><th>ID Tag</th>
                <th>Parent ID Tag</th>
                <th>Expiry Date/Time</th>
                <th>In Transaction?</th>
                <th>Blocked?</th>
                <th>
                    <form:form action="${ctxPath}/manager/ocppTags/add" method="get">
                        <input type="submit" class="blueSubmit" value="Add New"/>
                    </form:form>
                </th>
            </tr>
        </thead>
        <tbody>
        <c:forEach items="${ocppTagList}" var="item">
            <tr><td><a href="${ctxPath}/manager/ocppTags/details/${item.ocppTagPk}">${item.idTag}</a></td>
                <td>
                    <c:if test="${not empty item.parentIdTag}">
                        <a href="${ctxPath}/manager/ocppTags/details/${item.parentOcppTagPk}">${item.parentIdTag}</a>
                    </c:if>
                </td>
                <td>${item.expiryDate}</td>
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
</div></div>
<%@ include file="../00-footer.jsp" %>