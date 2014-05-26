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
<!--[if lte IE 8]><script src="resources/js/html5shiv.js"></script><![endif]-->
<script src="resources/js/jquery.min.js"></script>
<script src="resources/js/jquery.widget.min.js"></script>

<!-- Highcharts -->
<script type="text/javascript"
	src="<c:url value="/resources/js/custom-chart.js" />"></script>
<script type="text/javascript"
	src="<c:url value="/resources/js/highcharts-all.js" />"></script>
<!-- CSS -->
<link rel="stylesheet" href="css/metro-bootstrap.css">

<script type="text/javascript">
/*
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

	$(document).ready( 
			function() {
				getLogTable(contextPath+'/log',"log-table");
			});
	*/
</script>

</head>

<body class="metro">
<div class="navigation-bar dark">
    <div class="navigation-bar-content container">
			<div class="element">
				<a class="dropdown-toggle" href="#">METRO UI CSS</a>
				<ul class="dropdown-menu" data-role="dropdown">
					<li><a href="#">Main</a></li>
					<li><a href="#sensor">Sensor list</a></li>
					<li class="divider"></li>
					<li><a href="#log">log</a></li>
					<li class="divider"></li>
					<li><a href="#">Exit</a></li>
				</ul>
			</div>

		</div>
</div>
</body>
</html>