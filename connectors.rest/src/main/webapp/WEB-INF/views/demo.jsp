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
<script src="resources/js/metro.min.js"></script>
<script src="resources/js/jquery.min.js"></script>
<script src="resources/js/jquery.widget.min.js"></script>

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
	$(document).ready(
			function() {
				getRemoteDataDrawChart(contextPath + '/temperature',
						createNewLineChart('temperature-container'));
				getRemoteDataDrawChart(contextPath + '/linechart2',
						createNewLineChart('chart2-container'));
				getRemoteDataDrawChart(contextPath + '/linechart3',
						createNewLineChart('chart3-container'));
			});

	$(document).ready(function() {
		getLogTable(contextPath + '/log', "log-table");
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

			<ul class="element-menu">
				<li><a class="dropdown-toggle" href="#">Dropdown Menu</a>
					<ul class="dropdown-menu dark" data-role="dropdown">
						<li><a href="">Requirements</a></li>

					</ul>
					</li>
			</ul>

		</div>
	</div>

	<div class="container">
		<div class="grid fluid">
			<div class="row">
				
				<div class="span10 padding20 text-center">
					<div id="log-table">

						<table class="table default">
							<tr>
								<th>Time</th>
								<th>Log Messages</th>
							</tr>
							<div id="log-update">
								<c:if test="${not empty loglist}">
									<c:forEach var="log" items="${loglist}">
										<tr>
											<td>${log.date}</td>
											<td>${log.logMsg}</td>
										</tr>
									</c:forEach>
								</c:if>
							</div>
						</table>
					</div>
					
					<h2 class="fg-white">easy to use</h2>
				</div>


				<div class="span10 bg-green padding20 text-center">
					<div id="temperature-container"
						style="min-width: 300px; height: 300px; margin: 0 auto"></div>
				</div>
				
				
				<div class="span 10 bg-gray padding20 text-center ">
				<table class="table default">
					<tr>
						<th>Device Type</th>
						<th>Device ID</th>
						<th>Network Type</th>
					</tr>
					<c:if test="${not empty devlist}">
						<c:forEach var="listValue" items="${devlist}">
							<tr>
								<td>${listValue.id}</td>
								<td>${listValue.type}</td>
								<td>${listValue.networkType}</td>
							</tr>

						</c:forEach>
					</c:if>
				</table>
				</div>

			</div>
		</div>
	</div>
</body>
</html>