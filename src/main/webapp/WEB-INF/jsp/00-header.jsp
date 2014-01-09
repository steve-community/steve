<!DOCTYPE html>
<%@ page trimDirectiveWhitespaces="true" %>
<html>
<head>
	<link rel="stylesheet" type="text/css" href="${contextPath}/style.css">
	<script type="text/javascript" src="${contextPath}/jquery-2.0.3.min.js" ></script>
	<script type="text/javascript" src="${contextPath}/script.js" ></script>
	<title>SteVe - Steckdosenverwaltung</title>
</head>
<body>
<div class="main">
<div class="top-banner"><div class="container"><a href="${contextPath}/manager"><img src="${contextPath}/images/logo2.png" height="80"></a></div></div>
<div class="top-menu">
<div class="container">
<ul class="navigation">
	<li><a href="${contextPath}/manager">HOME</a></li>
	<li><a>DATA MANAGEMENT &raquo;</a>
	<ul>
		<li><a href="${contextPath}/manager/chargepoints">CHARGE POINTS</a></li>
		<li><a href="${contextPath}/manager/users">USERS</a></li>
		<li><a href="${contextPath}/manager/reservations">RESERVATIONS</a></li>
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
</ul>
</div></div>
<div class="main-wrapper">
<div class="content">
