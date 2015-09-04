<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<!DOCTYPE html>
<html lang="en">
<head>
<title>PWAL</title>
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



</head>

<body class="metro">
	<div class="navigation-bar dark">
		<div class="navigation-bar-content container">
			<a href="<c:url value="${contextPath}/demo"/>" class="element"><span
				class="icon-grid-view"></span> PWAL (Physical World Adaptation
				Layer) <sup>2.0</sup></a> <span class="element-divider"></span> <span
				class="element-divider"></span> <a class="element1 pull-menu"
				href="#"></a>
		</div>
	</div>

	<div class="container">
		<div class="grid fluid">
			<div class="span10 padding20 text-center">

				<h2 class="fg-gray">Ready to use PWAL GUI</h2>
			</div>


			<div class="row">
				<!-- This is for each device list -->
				<c:if test="${not empty devlist}">

					<c:set var="temp" value="false" />
					<c:set var="accel" value="false" />
					<c:set var="dist" value="false" />
					<c:forEach var="listValue" items="${devlist}">

						<c:if
							test="${listValue.type == 'pwal:Thermometer' || listValue.type == 'pwal:DistanceSensor' || listValue.type == 'pwal:Accelerometer' }">
							<div class="span4 bg-green padding20 text-center">

								<div id="${listValue.type}">
									${listValue.type} <br>

									<c:if
										test="${listValue.type == 'pwal:Thermometer' && temp eq false}">
										<c:set var="temp" value="true" />
										<input id="clickMe" type="button" value="Get Graph"
											onclick="load_tempsensor('${listValue.type}');" />
									</c:if>

									<c:if
										test="${listValue.type == 'pwal:DistanceSensor' && dist eq false}">
										<c:set var="dist" value="true" />
										<img src="<c:url value="/resources/images/Dustbin.jpg" />" alt="Dustbin" width="110" height="90">
										<input id="clickMe" type="button" value="Get Graph"
											onclick="load_distsensor('${listValue.type}');" />
									</c:if>

									<c:if
										test="${listValue.type == 'pwal:Accelerometer' && accel eq false}">
										<c:set var="accel" value="true" />
										<input id="clickMe" type="button" value="Get Graph"
											onclick="load_Accel('${listValue.type}');" />
									</c:if>

								</div>
							</div>
						</c:if>

					</c:forEach>
				</c:if>
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