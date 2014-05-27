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
	$(document).ready(function() {
		// 				getRemoteDataDrawChart(contextPath + '/temperature',
		// 						createNewLineChart('temperature-container'));
		// 								getRemoteSplineDataDrawChart(contextPath + '/tempsplinechart2',
		// 										createNewSplineChart('pwal:Thermometer'));
	});

	$(document).ready(function() {
		getLogTable(contextPath + '/log', "log-table");
	});
</script>




<script type="text/javascript">
	var contextPath = '<c:out value="${pageContext.request.contextPath}"/>';
	function load_sensor(divId) {
		var id1;
		clearInterval(id1);
		var url1 = (contextPath + '/tempsplinechart2');
		var c;

		function updateSensor() {
			$.getJSON(url1, function(data) {
				if (c.series.length == 0) {

					for (var i = 0; i < data.series.length; i++) {

						c.addSeries({
							"name" : data.series[i].name,
							"data" : []
						});

					}
				}

				var series = c.series[0];
				var shift = series.data.length > 5;

				for (var j = 0; j < c.series.length; j++) {

					if (c.series[j].name == data.series[j].name) {
						c.series[j].addPoint(data.series[j].data, true, shift);
					}
				}
			});
		}

		Highcharts.setOptions({
			global : {
				useUTC : false
			}
		});

		$.getJSON(url1, function(data) {
			var options = {
				chart : {
					renderTo : divId,
					type : 'spline',
					animation : Highcharts.svg,
					zoomType : 'x',
					events : {
						load : function() {
							updateSensor();
							id1 = setInterval(updateSensor, 3000);
						}
					}

				},

				title : {
					text : data.title
				},
				xAxis : {
					title : {
						text : data.xAxisTitle
					},
					type : 'datetime',
					labels : {
						formatter : function() {
							return new Date(this.value).toLocaleTimeString();
						}

					}
				},
				yAxis : {
					title : {
						text : data.yAxisTitle
					},
					labels : {
						formatter : function() {
							return this.value + '';
						}
					}
				},

				series : []
			};

			c = new Highcharts.Chart(options);

		});

	}
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

			<script>
				load_sensor('pwal:Thermometer');
			</script>
			<div class="row">
				<!-- This is for each device list -->
				<c:if test="${not empty devlist}">
					<c:forEach var="listValue" items="${devlist}">
						<div class="span4 bg-green padding20 text-center">
							<div id="${listValue.type}">${listValue.type}</div>
						</div>
					</c:forEach>
				</c:if>
			</div>
			<div class="span 10 padding20 text-center ">
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

			<div class="span 10 padding20 text-center" id="log-table">

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

		</div>
	</div>
</body>
</html>