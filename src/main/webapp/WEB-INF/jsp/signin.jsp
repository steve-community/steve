<%@ page contentType="text/html" pageEncoding="utf-8" language="java" trimDirectiveWhitespaces="true" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
	<link rel="icon" href="${pageContext.request.contextPath}/resources/images/favicon.ico" type="image/x-icon" />
	<link rel="shortcut icon" href="${pageContext.request.contextPath}/resources/images/favicon.ico" type="image/x-icon" />
	<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/css/style.css">
	<title>SteVe - Steckdosenverwaltung</title>
</head>
<body>
<div class="main">
<div class="top-banner"><div class="container"><a href="${pageContext.request.contextPath}"><img src="${pageContext.request.contextPath}/resources/images/logo2.png" height="80"></a></div></div>
<div class="top-menu"></div>
<div class="main-wrapper">
<div class="content">
	<section><span>Sign In</span></section>
	<%-- Display after failed login attempt --%>
	<c:if test="${param.error == 'true'}">
	<div class="error"><b>Error:</b> Your name or password is incorrect.</div>
	</c:if>
	<form method="POST" action="j_security_check">
	<table class="userInput">
		<tr><td>Name:</td><td><input type="text" name="j_username" required /></td></tr>
		<tr><td>Password:</td><td><input type="password" name="j_password" required /></td></tr>
		<tr><td></td><td id="add_space"><input type="submit" value="Sign In"></td></tr>
	</table>
	</form>
</div>
</div>
</div>
<div class="footer">
	<a href="http://www.rwth-aachen.de"><img src="${pageContext.request.contextPath}/resources/images/logo_rwth.png"></a>
	<a href="http://dbis.rwth-aachen.de"><img src="${pageContext.request.contextPath}/resources/images/logo_i5.png"></a>
</div>
</body>
</html>