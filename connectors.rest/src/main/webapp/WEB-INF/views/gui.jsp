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
<!--[if lte IE 8]><script src="resources/js/html5shiv.js"></script><![endif]-->
<script src="resources/js/jquery.min.js"></script>
<script src="resources/js/skel.min.js"></script>
<script src="resources/js/skel-panels.min.js"></script>
<script src="resources/js/init.js"></script>

<!-- Highcharts -->
<script type="text/javascript"
	src="<c:url value="/resources/js/custom-chart.js" />"></script>
<script type="text/javascript"
	src="<c:url value="/resources/js/highcharts-all.js" />"></script>
<!-- CSS -->
<link rel="stylesheet" type="text/css" href="resources/css/style.css" />
<link rel="stylesheet" type="text/css"
	href="resources/css/style-wide.css" />
<link rel="stylesheet" type="text/css"
	href="resources/css/skel-noscript.css" />

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

	$(document).ready( 
			function() {
				getLogTable(contextPath+'/log',"log-table");
			});
	
</script>

</head>

<!-- Header -->
<div id="header" class="skel-panels-fixed">

	<div class="top">

		<!-- Logo -->
		<div id="logo">
			<h1 id="title">PWAL</h1>
			<span class="byline">Physical World Adaptation Layer</span>
		</div>

		<!-- Nav -->
		<nav id="nav">
			<ul>
				<li><a href="#home" id="top-link"
					class="skel-panels-ignoreHref"><span class="fa fa-home">Intro</span></a></li>
				<li><a href="#log" id="log-link" class="skel-panels-ignoreHref"><span
						class="fa fa-tasks">PWAL Log</span></a></li>
				<li><a href="#sensor" id="sensor-link"
					class="skel-panels-ignoreHref"><span class="fa fa-sitemap">Sensor
							Devices</span></a></li>
				<li><a href="#contact" id="contact-link"
					class="skel-panels-ignoreHref"><span class="fa fa-envelope">Contact</span></a></li>
			</ul>

		</nav>

	</div>


	<div class="bottom">
		<a href="http://www.ismb.it" class="image centered" target="_blank"><img
			src="resources/images/ismb_page.png" alt="ismb logo" height="100"
			width="200" /></a>
		<!-- Social Icons -->
		<!-- 		<ul class="icons a"> -->
		<!-- 			<li><a href="http://www.ismb.it" class="fa fa-external-link" target="_blank"><span>ISMB</span></a></li> -->
		<!--  			&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; -->
		<!-- 			<li><a href="http://www.ismb.it/en/PeRT" class="fa fa-external-link" target="_blank"><span>Pervasive Technologies</span></a></li> -->
		<!-- 		</ul> -->

	</div>

</div>


<!-- Main -->
<div id="main">

	<!-- Intro -->
	<section id="home" class="one">
		<div class="container">


			<a href="#home" class="image centered"><img
				src="resources/images/pwalLOGO.jpg" alt="PWAL logo" height="300"
				width="200" /></a>

			<h1>
				This is <strong>PWAL</strong>, an invisible translation layer
				between the physical world of constrained devices and the Internet.
			</h1>

			<form action="<c:url value="${contextPath}/gui#sensor"/>">
				<button type="submit">Load Devices</button>
			</form>
			<%-- 			<input type="button"  onclick="<c:url value="${pageContext.request.contextPath}/sensor"/>" value="sensor" > --%>

			<div id="temperature-container"
				style="min-width: 300px; height: 300px; margin: 0 auto"></div>
		</div>
	</section>

	<!-- Logging Information -->
	<section id="log" class="two">
		<div class="container">
			<header>
				<h2>Log information</h2>
			</header>
			<p>This log shows the devices attached and detached.</p>

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
			<!-- 			<div id="chart1-container" -->
			<!-- 				style="min-width: 300px; max-width: 500px; height: 300px; margin: 0 auto"></div> -->

		</div>
	</section>

	<!-- here we need Table -->
	<section id="sensor" class="three">
		<div class="container">

			<header>
				<h1>Sensor Devices List</h1>
			</header>



			<div>

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
			<!-- 			<div id="chart2-container" -->
			<!-- 				style="min-width: 300px; max-width: 500px; height: 300px; margin: 0 auto"></div> -->

		</div>
	</section>

	<!-- Contact -->
	<section id="contact" class="four">
		<div class="container">

			<header>
				<h2>Contact US : <a href="mailto:pert@ismb.it"> pert@ismb.it </a></h2>
			</header>

		</div>
	</section>

</div>

<!-- Footer -->
<!-- <div id="footer"> -->

<!-- Copyright -->
<!-- 	<div class="copyright"> -->
<!-- 		<p>&copy; 2013 Jane Doe. All rights reserved.</p> -->
<!-- 		<ul class="menu"> -->
<!-- 			<li>Design: <a href="http://html5up.net">HTML5 UP</a></li> -->
<!-- 		</ul> -->
<!-- 	</div> -->

<!-- </div> -->

<!-- </div> -->


</body>
</html>