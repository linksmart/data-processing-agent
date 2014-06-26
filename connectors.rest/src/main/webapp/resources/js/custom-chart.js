/* Custom functions that help in getting remote data and drawing a chart to a div */
/*
 * @author Prabhakaran Kasinthan
 */

var id; // global ID to clear SetInterval

//table-reload function
function ajax_interval(divId) {
	clearInterval(id);
	id1=setInterval(reload_table, 1000);

}

function getLogTable(url, divID){
	clearInterval(id);
	$.ajax({
		url : url,
		success : function(data) {

		}
	});
	id=setInterval(getLogTable, 3000);

}

function createNewLineChart(divId) {
	var chart = {
			options: {
				chart: {
					renderTo: divId
				}
			}
	};
	chart = jQuery.extend(true, {}, getBaseChart(), chart);
	chart.init(chart.options);
	return chart;
}


function getBaseChart() {

	var baseChart = {
			highchart: null,
			defaults: {

				chart: {
					renderTo: null,
					shadow: true,
					borderColor: '#ebba95',
					borderWidth: 2,
					defaultSeriesType: 'line',
					width: 400,
					height: 250
				},
				credits: {
					enabled: false
				},
				exporting: {
					enabled: false
				},
				title: {
					text: null,
					x: -20,
					style: {
						color: '#3366cc',
						fontWeight: 'bold',
						fontSize: '16px',
						fontFamily: 'Trebuchet MS, Verdana, sans-serif'
					}
				},
				xAxis: {
					categories: [],
					gridLineDashStyle: 'dot',
					gridLineColor: '#197f07',
					gridLineWidth: 1,
					tickColor: '#ff40ff',
					tickWidth: 2,
					title: {
						text: null,
						style: {
							color: '#3366cc',
							fontWeight: 'bold',
							fontSize: '12px',
							fontFamily: 'Trebuchet MS, Verdana, sans-serif'
						}
					},
					labels: {
						rotation: -25,
						align: 'right',
						style: {
							color: '#3366cc',
							fontWeight: 'normal',
							fontSize: '9px',
							fontFamily: 'Trebuchet MS, Verdana, sans-serif'
						}
					}
				},
				yAxis: {
					min: 0,
					gridLineWidth: 1,
					gridLineColor: '#197F07',
					gridLineDashStyle: 'dot',
					title: {
						text: null,
						style: {
							color: '#3366cc',
							fontWeight: 'bold',
							fontSize: '12px',
							fontFamily: 'Trebuchet MS, Verdana, sans-serif'
						}
					},
					labels: {
						style: {
							color: '#3366cc',
							fontSize: '12px',
							fontFamily: 'Trebuchet MS, Verdana, sans-serif'
						}
					},
					plotLines: [{
						value: 0,
						width: 1
					}]
				},
				tooltip: {
					crosshairs: true,
					formatter: function() {
						return '<b>'+ this.series.name +'</b><br/>'+
						this.x +': '+ this.y;
					}
				},
				legend: {
					layout: 'horizontal',
					backgroundColor: '#ffffff',
					align: 'center',
					verticalAlign: 'top',
					borderWidth: 1,
					shadow: true,
					style: {
						color: '#3366cc',
						fontWeight: 'bold',
						fontSize: '9px',
						fontFamily: 'Trebuchet MS, Verdana, sans-serif'
					}
				},
				series: []

			},

			// here you'll merge the defaults with the object options
			init: function(options) {
				this.highchart = jQuery.extend({}, this.defaults, options);
			},

			create: function() {
				new Highcharts.Chart(this.highchart);
			}

	};
	return baseChart;
}//function end


function getRemoteDataDrawChart(url, linechart) {

	$.ajax({
		url: url,
		dataType: 'json',
		success: function(data) {

			var categories = data.categories;
			var title = data.title;
			var yTitle = data.yAxisTitle;
			var xTitle = data.xAxisTitle;
			var divId =  data.divId;

			//populate the lineChart options (highchart)
			linechart.highchart.xAxis.categories = categories;
			linechart.highchart.title.text = title;
			linechart.highchart.yAxis.title.text = yTitle;
			linechart.highchart.xAxis.title.text = xTitle;
			linechart.highchart.chart.renderTo = divId;

			$.each(data.series, function(i, seriesItem) {
				console.log(seriesItem) ;
				var series = {
						data: []
				};
				series.name = seriesItem.name;
				series.color = seriesItem.color;

				$.each(seriesItem.data, function(j, seriesItemData) {
					console.log("Data (" + j +"): "+seriesItemData) ;
					series.data.push(parseFloat(seriesItemData));
				});

				linechart.highchart.series[i] = series;
			});

			//draw the chart
			linechart.create();
		},
		error: function (xhr, ajaxOptions, thrownError) {
			alert(xhr.status);
			alert(thrownError);
		},
		cache: false
	});
} //function end


var GlobalcontextPath;

function getContextPath(contextPath){
	GlobalcontextPath = contextPath;
}

//For Distance spline Graphs	
function load_distsensor(divId) {
	var id1;
	clearInterval(id1);
	var url1 = (contextPath + '/distancesplinechart');
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
					type : 'area',
					animation : Highcharts.svg,
					zoomType : 'x',
					plotBackgroundImage: 'resources/images/Dustbin.jpg',
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
					min: 0,
			        max: 35,
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

function load_filllevel( devID) {
	var id1;
	clearInterval(id1);
	var url1 = (contextPath + '/smartsantander/'+devID );
	var c;
	var rootDiv=document.getElementById("tile-"+devID);
	$(rootDiv).removeClass("double").addClass("quadro quadro-vertical");
	
	var chartDiv=document.getElementById(devID);
	chartDiv.style.visibility = "visible";
	
	var showButton=document.getElementById("showchart-"+devID);
	var hideButton=document.getElementById("hidechart-"+devID);
	
	showButton.style.visibility="hidden";
	hideButton.style.visibility="visible";
	
	$(hideButton).addClass("offset4");
	
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
					renderTo : devID,
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
					text : "Fill level sensor"
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
				yAxis : [{
					title : {
						text : "Height(cm)"
					},
					labels : {
						formatter : function() {
							return this.value + '';
						}
					},
					opposite: true
				},
				{
					title: {
						text: "Fill level (%)"
					},
					labels : {
						formatter : function() {
							return this.value + '';
						}
					}
				}],

				series : []
		};

		c = new Highcharts.Chart(options);	
	});
}

function load_flowmeter( devID) {
	var id1;
	clearInterval(id1);
	var url1 = (contextPath + '/smartsantander/'+devID );
	var c;
	var rootDiv=document.getElementById("tile-"+devID);
	$(rootDiv).removeClass("double").addClass("quadro quadro-vertical");
	
	var chartDiv=document.getElementById(devID);
	chartDiv.style.visibility = "visible";
	
	var showButton=document.getElementById("showchart-"+devID);
	var hideButton=document.getElementById("hidechart-"+devID);
	
	showButton.style.visibility="hidden";
	hideButton.style.visibility="visible";
	
	$(hideButton).addClass("offset4");
	
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
					renderTo : devID,
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
					text : "Flow meter"
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
						text : "m^3/s"
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


// for smart Santander Graph with request param URL

//For spline Graphs	
function load_vehiclesensor( devID) {
	var id1;
	clearInterval(id1);
	var url1 = (contextPath + '/smartsantander/'+devID );
	var c;
	var rootDiv=document.getElementById("tile-"+devID);
	$(rootDiv).removeClass("double").addClass("quadro quadro-vertical");
	
	var chartDiv=document.getElementById(devID);
	chartDiv.style.visibility = "visible";
	
	var showButton=document.getElementById("showchart-"+devID);
	var hideButton=document.getElementById("hidechart-"+devID);
	
	showButton.style.visibility="hidden";
	hideButton.style.visibility="visible";
	
	$(hideButton).addClass("offset4");
	
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
					renderTo : devID,
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

function load_vehiclecounter( devID) {
	var id1;
	clearInterval(id1);
	var url1 = (contextPath + '/smartsantander/'+devID );
	var c;
	var rootDiv=document.getElementById("tile-"+devID);
	$(rootDiv).removeClass("double").addClass("quadro quadro-vertical");
	
	var chartDiv=document.getElementById(devID);
	chartDiv.style.visibility = "visible";
	
	var showButton=document.getElementById("showchart-"+devID);
	var hideButton=document.getElementById("hidechart-"+devID);
	
	showButton.style.visibility="hidden";
	hideButton.style.visibility="visible";
	
	$(hideButton).addClass("offset4");
	
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
					renderTo : devID,
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
					text : "Vehicle Counter Sensor",
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
				yAxis : [{
					title : {
						text : "#"
					},
					labels : {
						formatter : function() {
							return this.value + '';
						}
					},
					opposite: true
				},
				{
					title: {
						text: "%"
					},
					labels : {
						formatter : function() {
							return this.value + '';
						}
					}
				}],

				series : []
		};

		c = new Highcharts.Chart(options);	
	});

}

//For spline Graphs	
function load_tempsensor(divId) {
	var id1;
	clearInterval(id1);
	var url1 = (contextPath + '/tempsplinechart');
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

/// Accel sensor

function load_Accel(divId){
	clearInterval(id);
	var c;
	var url = (GlobalcontextPath + '/accel3dchart');

	function updateAccel() {
		$.getJSON(url, function(data) {

			console.debug(c.series.length);
			//create series if are not there
			if(c.series.length == 0)
			{
				for(var i=0;i<data.series.length;i++)
				{
					c.addSeries({"name":data.series[i].name,"data":[]});

				}
			}


			else{

				for(var i=0;i<data.series.length;i++)
				{
					var found=false;
					for(var j=0;j<c.series.length;j++)
					{
						if(c.series[j].name==data.series[i].name)
						{
							found=true;						
							c.series[j].setData(data.series[i].data);	
						}

						//console.debug("Found",found);
						//console.debug("Name",c.series[j].name); 

					}

					//I need to add a new serie
					if(found==false)
					{
						c.addSeries({"name":data.series[i].name,"data":data.series[i].data});
						i--; //retry adding the point
					}

				}

			}		    
		});							
	}  


	$.getJSON(url, function(data) {
		var options = {
				chart: {
					renderTo: divId,
					polar: true,
					type: 'line', 
					zoomType:'y',
					events:{
						load: function() {
							updateAccel();
							id=setInterval(updateAccel, 3000);
						}
					}

				},

				pane: {
					size: '75%'
				},
				
				title : {
					text : data.title
				},
				
				xAxis: {

					categories: ['x-Axis', 'y-Axis', 'z-Axis'],
					tickmarkPlacement: 'off',
					lineWidth: 0
				},

				yAxis: {

					gridLineInterpolation: 'polygon',
					lineWidth: 0,
					min: -1000,
					max: 1000
				},

				tooltip: {
					shared: true,
					pointFormat: '<span style="color:{series.color}">{series.name}: <b>Accel:{point.y:,.0f}</b><br/>'
				},

				legend: {
					align: 'center',
					verticalAlign: 'top',
					y: 70,
					layout: 'vertical'
				},   

				series: []
		};

		c = new Highcharts.Chart(options);
	});
}

