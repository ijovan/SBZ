<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<jsp:useBean id="wines" type="java.util.List" scope="request" />

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<link rel="stylesheet" type="text/css" href="theme.css">
<title>Wines</title>
<script>
	var x = document.getElementById("demo");

	function getLocation() {
		if (navigator.geolocation) {
			navigator.geolocation.getCurrentPosition(showPosition);
		} else {
			x.innerHTML = "Geolocation is not supported by this browser.";
		}
	}

	function showPosition(position) {
		document.getElementById("lat").value = position.coords.latitude;
		document.getElementById("lng").value = position.coords.longitude;
		document.forms["coords"].submit();
	}
</script>
</head>
<body>
	<form action="./HomeController" method="post">
		<input type="text" name="region"> 
		<input type="submit" name="submit" value="Find">
		<br/>
		<button onclick="getLocation()">Use my location</button>
		<p id="demo"></p>
		<br/>
	</form>
	<form id="coords" method="post" action="./HomeController">
		<input type="hidden" id="lat" name="lat">
		<input type="hidden" id="lng" name="lng">
	</form>
	<table class="winesListTable">
		<thead>
			<tr>
				<th>Name</th>
				<th>Maker</th>
				<th>Body</th>
				<th>Flavor</th>
				<th>Type</th>
				<th>Sugar</th>
				<th>Region</th>
				<th>Distance</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${wines}" var="wine">
				<tr>
					<td>${wine.name}</td>
					<td>${wine.maker}</td>
					<td>${wine.body}</td>
					<td>${wine.flavor}</td>
					<td>${wine.type}</td>
					<td>${wine.sugar}</td>
					<td>${wine.region.name}</td>
					<td>${wine.distance}</td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
</body>
</html>