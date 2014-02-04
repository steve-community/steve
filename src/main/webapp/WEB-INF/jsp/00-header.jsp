<%@ page contentType="text/html" pageEncoding="utf-8" language="java" trimDirectiveWhitespaces="true" %>
<!DOCTYPE html>
<html>
<head>
	<link rel="icon" href="${contextPath}/images/favicon.ico" type="image/x-icon" />
	<link rel="shortcut icon" href="${contextPath}/images/favicon.ico" type="image/x-icon" />
	<link rel="stylesheet" type="text/css" href="${contextPath}/css/style.css">
	<link rel="stylesheet" type="text/css" href="${contextPath}/css/jquery-ui-1.10.4.custom.min.css" >
	<script type="text/javascript" src="${contextPath}/js/jquery-2.0.3.min.js" ></script>
	<script type="text/javascript" src="${contextPath}/js/jquery-ui-1.10.4.custom.min.js" ></script>
	<script type="text/javascript" src="${contextPath}/js/script.js" ></script>
	<title>SteVe - Steckdosenverwaltung</title>
</head>
<body>
<div class="main">
<div class="top-banner"><div class="container"><a href="${contextPath}"><img src="${contextPath}/images/logo2.png" height="80"></a></div></div>
<div class="top-menu">
<div class="container">
<ul class="navigation">
	<li><a href="${contextPath}/manager/home">HOME</a></li>
	<li><a>DATA MANAGEMENT &raquo;</a>
	<ul>
		<li><a href="${contextPath}/manager/chargepoints">CHARGE POINTS</a></li>
		<li><a href="${contextPath}/manager/users">USERS</a></li>
		<li><a href="${contextPath}/manager/reservations">RESERVATIONS</a></li>
		<li><a href="${contextPath}/manager/transactions">TRANSACTIONS</a></li>
	</ul>
	</li>
	<li><a>OPERATIONS &raquo;</a>
	<ul>
		<li><a href="${contextPath}/manager/operations/v1.2">OCPP v1.2</a></li>
		<li><a href="${contextPath}/manager/operations/v1.5">OCPP v1.5</a></li>
	</ul>
	</li>
	<li><a href="${contextPath}/manager/settings">SETTINGS</a></li>
	<li><a href="${contextPath}/manager/log">LOG</a></li>
	<li><a href="${contextPath}/manager/about">ABOUT</a></li>
	<li><a href="${contextPath}/manager/signout">SIGN OUT</a></li>
</ul>
</div></div>
<div class="main-wrapper">
<div class="content">
