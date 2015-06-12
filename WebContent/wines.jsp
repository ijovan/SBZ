<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page contentType="text/html; charset=UTF-8" %>
<jsp:useBean id="wines" type="java.util.List" scope="request" />

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
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

    <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.2/jquery.min.js"></script>

	<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.4/css/bootstrap.min.css">
	
	<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.4/js/bootstrap.min.js"></script>
	
</head>
<body>
<div class="container">
	<form action="./HomeController" method="post">
	<br/>
	  <div class="col-lg-6">
	  <h5>Unesite ime regije koju želite da pretražite ili pretražite regije koje su vam u blizini</h5>
	    <div class="input-group">
	     <input type="text" class="form-control" placeholder="Search for..." name="region">
	      <span class="input-group-btn">
	        <button name="submit" class="btn btn-default" type="submit">Find!</button>
	      </span>
	    </div><!-- /input-group -->
	   	<br/>
		<button class="btn btn-default" onclick="getLocation()" class="form-control">Use my location</button>
	  </div>
	  <div class="col-lg-6">
	  <h5>Dodatni parametri pretraživanja vina</h5>
		
	  </div>

	<p id="demo"></p>
	</form>
	<form id="coords" method="post" action="./HomeController">
		<input type="hidden" id="lat" name="lat">
		<input type="hidden" id="lng" name="lng">
	</form>
	<hr>
	<br/>
	<br/>
	<div class="col-lg-12"> 
		<table class="table table-striped">
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
	</div>
</div>
</body>
</html>