
<%@page contentType="text/html" pageEncoding="UTF-8"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<html>
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

<link rel="stylesheet" type="text/css" href="resources/css/style.css" />
<link rel="stylesheet" type="text/css"
	href="resources/css/style-wide.css" />
<link rel="stylesheet" type="text/css"
	href="resources/css/skel-noscript.css" />


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
		<a href="http://www.ismb.it" class="image centered"><img
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
<c:out		value="${msg}" ></c:out>

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
				<table width="59%" border="1">
				<tr>
				   <th> Sensor Type</th>
				   <th> Sensor Value</th>
				</tr>
					<%
						int cols = 2;
						int rows =2;
						String[][] value = new String[rows][cols];
						value[0][0]="0.0";
						value[0][1]="0.1";
						value[1][0]="1.0";
						value[1][1]="1.1";
						
						for(int i=0;i<2;i++){
					
					%>
					<tr>
					<%		for(int j=0;j<2;j++){ %>
						<td><%=value[i][j]%></td>
					
					<%

					}
						}
					
					%>
					</tr>
				</table>
				
			</div>

		</div>
	</section>

	<!-- here we need Table -->
	<section id="sensor" class="three">
		<div class="container">

			<header>
				<h2>Sensor Devices List</h2>
			</header>




		</div>
	</section>

	<!-- Contact -->
	<section id="contact" class="four">
		<div class="container">

			<header>
				<h2>Contact</h2>
			</header>

			<p>Interested ? Please contact us.</p>

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