<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page contentType="text/html; charset=UTF-8" %>
<c:if test="${wines != null}">
	<jsp:useBean id="wines" type="java.util.List" scope="request" />
</c:if>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>Wines</title>
<style>
#map-canvaswines {
    width: 500px;
    height: 400px;
    background-color: #CCC;
     }

</style>


<script src="https://maps.googleapis.com/maps/api/js"></script>    

<script>
	var x = document.getElementById("demo");

	window.onload = function() {
		if (navigator.geolocation) {
			navigator.geolocation.getCurrentPosition(centerMap);
		} else {
			x.innerHTML = "Geolocation is not supported by this browser.";
		}
    };
    
	
	function centerMap(position)
	{
		  var mapCanvas = document.getElementById('map-canvaswines');
		  var homelat = '${homeLat}';
		  if (homelat!=null && homelat!="")
			{
			    var mapOptions = {
			      center: new google.maps.LatLng('${homeLat}','${homeLng}'),//(position.coords.latitude, position.coords.longitude),
			      zoom: 1,
			      mapTypeId: google.maps.MapTypeId.ROADMAP
			    };
			    var map = new google.maps.Map(mapCanvas, mapOptions);
			    
			    var image = 'imgs/home.png';
			    var myLatlng = new google.maps.LatLng('${homeLat}','${homeLng}');
			    var marker = new google.maps.Marker({
			        position: myLatlng,
			        map: map,
			        title: "Home",
			        icon: image
			    });
			}
		  else
			  {
			  var mapOptions = {
				      center: new google.maps.LatLng(position.coords.latitude, position.coords.longitude),
				      zoom: 8,
				      mapTypeId: google.maps.MapTypeId.ROADMAP
				    };
				    var map = new google.maps.Map(mapCanvas, mapOptions);
				   
			  }
		    
		    var table = document.getElementById('winesTable');
	        for (var r = 1, n = table.rows.length; r < n; r++) {
	                var lat = table.rows[r].cells[9].innerHTML;
	                var lng = table.rows[r].cells[8].innerHTML;
	                var name = table.rows[r].cells[0].innerHTML;
	                var myLatlng = new google.maps.LatLng(lat,lng);
				    var marker = new google.maps.Marker({
				        position: myLatlng,
				        map: map,
				        title: name
				    }); 
	        }
	}
	
	
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
		//document.forms["userInput"].submit();
		justSubmit();
	}
	
	function justSubmit()
	{
		document.forms["userInput"].action="./HomeController";
		document.forms["userInput"].method="post";
		document.forms["userInput"].submit();	
	}
	
	
</script>

    <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.2/jquery.min.js"></script>

	<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.4/css/bootstrap.min.css">
	
	<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.4/js/bootstrap.min.js"></script>
	
</head>
<body>
<div class="container">
<form id="userInput">
	<br/>
	  <div class="col-lg-6">
	  <h5>Input region name:</h5>
	    <div class="input-group">
	     <input type="text" class="form-control" placeholder="Search for..." name="region">
	      <span class="input-group-btn">
	         <button class="btn btn-default" type="button" onclick="justSubmit()">Find!</button>     
	      </span>
	    </div><!-- /input-group -->
	   	<br/>
		<button class="btn btn-default" type="button" onclick="getLocation()" class="form-control">Use my current location</button>
	  </div>

	  <div class="col-lg-6">
	  <h5>Filter by:</h5>

		<!-- Select Basic -->
		<div class="control-group">
		  <label class="control-label" for="selectBody">Body</label>
		  <div class="controls">
		    <select id="selectBody" name="selectBody" class="input-xlarge">
		      <option selected value="Any">Any</option>
		      <option>Full</option>
		      <option>Medium</option>
		      <option>Light</option>
		    </select>
		  </div>
		</div>
		
		<!-- Select Basic -->
		<div class="control-group">
		  <label class="control-label" for="selectFlavor">Flavor</label>
		  <div class="controls">
		    <select id="selectFlavor" name="selectFlavor" class="input-xlarge">
		      <option selected value="Any">Any</option>
		      <option>Strong</option>
		      <option>Moderate</option>
		      <option>Delicate</option>
		      </select>
		  </div>
		</div>
		
		<!-- Select Basic -->
		<div class="control-group">
		  <label class="control-label" for="selectSugar">Sugar</label>
		  <div class="controls">
		    <select id="selectSugar" name="selectSugar" class="input-xlarge">
		      <option selected value="Any">Any</option>
		      <option>Sweet</option>
		      <option>Dry</option>
		      <option>Off Dry</option>
		    </select>
		  </div>
		</div>
		
	</div>


	<p id="demo"></p>
	
	<div class="col-lg-12"><hr></div>

	<input type="hidden" id="lat" name="lat"> 
	<input type="hidden" id="lng" name="lng">
	</form>
	<hr>
	<br/>
	
	<div class="col-lg-6" id="map-canvaswines" style="margin-bottom:20px;display: inline-block;"></div>
	
	<br/>
	<div class="col-lg-12"> 
	<c:if test="${wines != null}">
		<table class="table table-striped" id="winesTable">
			<thead>
				<tr>
					<th>Name</th>
					<th>Maker</th>
					<th>Body</th>
					<th>Flavor</th>
					<th>Sugar</th>
					<th>Type</th>
					<th>Region</th>
					<th>Distance</th>
					<th style="display:none;">Long</th>
					<th style="display:none;">Lat</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${wines}" var="wine">
					<tr>
						<td>${wine.name}</td>
						<td>${wine.maker}</td>
						<td>${wine.body}</td>
						<td>${wine.flavor}</td>
						<td>${wine.sugar}</td>
						<td>${wine.type}</td>
						<td>${wine.region.name}</td>
						<td><fmt:formatNumber type="number" maxFractionDigits="0" 
							value="${wine.distance}"/>km</td>
						<td style="display:none;">${wine.region.loc.longitude}</td>
						<td style="display:none;">${wine.region.loc.latitude}</td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
	</c:if>
	</div>
</div>
</body>
</html>