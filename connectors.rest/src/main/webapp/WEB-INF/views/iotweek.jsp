<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<!DOCTYPE html>
<html lang="en">
<head>
<title>SCRAL</title>
<meta http-equiv="content-type" content="text/html; charset=utf-8" />
<meta name="description" content="" />
<meta name="keywords" content="" />
<link
	href="http://fonts.googleapis.com/css?family=Source+Sans+Pro:200,300,400,600"
	rel="stylesheet" type="text/css" />

<!-- Scripts -->
<script src="resources/js/jquery.min.js"></script>
<script src="resources/js/jquery.widget.min.js"></script>
<script src="resources/js/metro.min.js"></script>

<!-- Highcharts -->
<script type="text/javascript"
	src="<c:url value="/resources/js/custom-chart.js" />"></script>
<script type="text/javascript"
	src="<c:url value="/resources/js/highcharts-all.js" />"></script>
<!-- CSS -->
<link rel="stylesheet" type="text/css"
	href="resources/css/metro-bootstrap.css">
<link rel="stylesheet" type="text/css"
	href="resources/css/metro-bootstrap-responsive.css">

<script type="text/javascript">
	var contextPath = '<c:out value="${pageContext.request.contextPath}"/>';
	$(document).ready(function() {
		// 				getRemoteDataDrawChart(contextPath + '/temperature',
		// 						createNewLineChart('temperature-container'));
		// 								getRemoteSplineDataDrawChart(contextPath + '/tempsplinechart2',
		// 										createNewSplineChart('pwal:Thermometer'));
	});

	$(document).ready(function() {
		//getLogTable(contextPath + '/log', "log-table");
	});

	$(document).ready(function() {
		getContextPath(contextPath);
	});
</script>

<script type="text/javascript">
	function colorTile(devId, devType)
	{
		console.log("colortile: " + devId + " " + devType);
		switch(devType)
		{
		case "VehicleCounter":
			console.log("it's a vehicle counter");
			var element = document.getElementById("tile-"+devId);
			$(element).addClass("bg-lightBlue");
			break;
		case "VehicleSpeed":
			console.log("it's a vehicle speed");
			var element = document.getElementById("tile-"+devId);
			$(element).addClass("bg-darkCyan");
		}
	}
</script>

<script type="text/javascript">
	function resizeTile(devId)
	{
		console.log("resize tile: " + devId);
		var showButton = document.getElementById("showchart-"+devId);
		var hideButton = document.getElementById("hidechart-"+devId);
				
		hideButton.style.visibility = "hidden";
		showButton.style.visibility = "visible";
		var rootDiv=document.getElementById("tile-"+devId);
		$(rootDiv).removeClass("quadro quadro-vertical").addClass("double");
		$(hideButton).removeClass("offset4");
		var chartDiv = document.getElementById(devId);
		
//		$(chartDiv).empty();
		$('#'+devId).highcharts().destroy();
// 		for(var i=0;i<children.length;i++){
// 			$(children[i]).destroy();
// 		}
		chartDiv.style.visibility = "hidden";
		
		

	}
</script>
</head>

<body class="metro">
	<div class="navigation-bar bg-darkCyan">
		<div class="navigation-bar-content container">
			<a href="<c:url value="${contextPath}/iotweek"/>" class="element"><span
				class="icon-grid-view"></span> SCRAL (Smart City Resources Adaptation Layer) <sup>0.0.1</sup></a> 
				<span class="element-divider"></span> <span
				class="element-divider"></span> <a class="element1 pull-menu"
				href="#"></a>
		</div>
	</div>

	<div class="container">
		<div class="main content clearfix">
			<div class="row span6 offset3">
				<div class="tile quadro">
					<div class="tile-content image">
<!-- 						<div class="image-container"> -->
<!-- 							<div class="image-container shadow">  -->
								<img src='<c:url value="/resources/images/logoalmanac.jpg" />' />
<!-- 							</div> -->
<!-- 						</div> -->
					</div>
					<!-- <div class="overlay-fluid">
						Almanac
					</div> -->
				</div>
			</div>
			<div class="row  span6 offset3">
				<h2 class="fg-darkCyan">Smart City Resources Adptation Layer</h2>
			</div>
			<div class="tile-area no-padding clearfix">
				<!-- This is for each device list -->
				<div class="tile-group no-margin no-padding clearfix">
					<!-- <a href="#">
					    <span class="tile-group-title fg-darkCyan">
						    Adapted devices
						    <span class="icon-arrow-right-5"></span>
					    </span>
					 </a> -->
						<c:if test="${not empty devlist}">
	
						<c:set var="temp" value="false" />
						<c:set var="accel" value="false" />
						<c:set var="dist" value="false" />
						<c:forEach var="listValue" items="${devlist}">
							<c:if
								test="${listValue.type == 'Thermometer' || listValue.type == 'DistanceSensor' || listValue.type == 'Accelerometer'  || listValue.type == 'VehicleSpeed' || listValue.type == 'VehicleCounter' }">
								<div class="tile double" id="tile-${listValue.id }">
<!-- 								<script type="text/javascript">colorTile('${listValue.id}','${listValue.type }')</script> -->
									<div class="tile-content" >
										<div id="grid-${listValue.id }" class="grid fluid">
											<div class="row">
												<div class="offset2 fg-white"><span class="text">Network:${listValue.networkType} Type:${listValue.type}</span></div>
											</div>
											<div class="row">
												<c:if
													test="${listValue.type == 'Thermometer' && temp eq false}">
													<c:set var="temp" value="true" />
													<input id="clickMe" type="button" value="Get Graph"
														onclick="load_tempsensor('${listValue.type}');" />
												</c:if>
			
												<c:if
													test="${listValue.type == 'DistanceSensor' && dist eq false}"> 
												<c:set var="dist" value="true" />
													<img src="<c:url value="/resources/images/Dustbin.jpg" />" alt="Dustbin" width="110" height="90">
													<input id="clickMe" type="button" value="Get Graph"
														onclick="load_distsensor('${listValue.type}');" /> 
												</c:if> 
			
												<c:if
													test="${listValue.type == 'Accelerometer' && accel eq false}">
													<c:set var="accel" value="true" /> 
												<input id="${listValue.id}" type="button" value="Get Graph"
													onclick="load_Accel('${listValue.type}');" />
												</c:if> 
										
												<c:if test="${listValue.type == 'VehicleSpeed'}"> 
													<div class="grid fluid">
														<button id="hidechart-${listValue.id }" class="small" onclick="resizeTile('${listValue.id}');" style="visibility: hidden">Close chart</button>
														<button id="showchart-${listValue.id }" class="small" onclick="load_vehiclesensor('${listValue.id}');">Show chart</button>
													</div>
												</c:if> 
												
												<c:if test="${listValue.type == 'VehicleCounter'}"> 
													<div class="grid fluid">
														<button id="hidechart-${listValue.id }" class="small" onclick="resizeTile('${listValue.id}');" style="visibility: hidden">Close chart</button>
														<button id="showchart-${listValue.id }" class="small" onclick="load_vehiclecounter('${listValue.id}');">Show chart</button>
													</div>
												</c:if> 
											</div>
											<div class="row">
												<div class="tile quadro triple-vertical bg-transparent" id="${listValue.id}"></div>
											</div>
										</div>									
									</div>
								</div>
								<script type="text/javascript">colorTile('${listValue.id}','${listValue.type }')</script> 
							</c:if>
	
						</c:forEach>
					</c:if>
				</div>
			</div>
			<div class="span 10 padding20 text-center ">
				<table class="table default">
					<c:if test="${not empty devlist}">
						<tr>
							<th>Device Type</th>
							<th>Device ID</th>
							<th>Network Type</th>
						</tr>

						<c:forEach var="listValue" items="${devlist}">
							<tr>
								<td>${listValue.type}</td>
								<td>${listValue.id}</td>
								<td>${listValue.networkType}</td>
							</tr>

						</c:forEach>
					</c:if>
				</table>
			</div>

			<div class="span 10 padding20 text-center" id="log-table">

				<table class="table default">
					<c:if test="${not empty loglist}">
						<tr>
							<th>Time</th>
							<th>Log Messages</th>
						</tr>
						<c:forEach var="log" items="${loglist}">
							<tr>
								<td>${log.date}</td>
								<td>${log.logMsg}</td>
							</tr>
						</c:forEach>
					</c:if>

				</table>
			</div>

		</div>
	</div>
</body>
</html>