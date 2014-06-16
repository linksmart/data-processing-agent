//
// Assigns custom navigation handlers to make sure
// the app from the iPad's homescreen does not open new window
// 
function SetupNavigation() {
  console.debug("SetupNavigation()");

  $('.mainBtn').click(function(event) {
    console.debug("Main");
    event.preventDefault();
    window.location.replace($(this).attr('href'));
  });
  $('.dataMgmtBtn').click(function(event) {
    console.debug("Data");
    event.preventDefault();
    window.location.replace($(this).attr('href'));
  });
  $('.virtMgmtBtn').click(function(event) {
    console.debug("Virtualization");
    event.preventDefault();
    window.location.replace($(this).attr('href'));
  });
  $('.resMgmtBtn').click(function(event) {
    console.debug("Adaptation");
    event.preventDefault();
    window.location.replace($(this).attr('href'));
  });
}

//
// Home page initialization
// 
function InitHomePage() {
  console.debug("InitHomePage()");
  SetupNavigation();
}

//
// Data page initialization
// 
function InitDataPage() {
  console.debug("InitDataPage()");
  SetupNavigation();

  $.getJSON("data/PlotExample.json", function(data) {
    var labels = new Array();
    var values = new Array();
    $.each(data, function(i, item) {
      var t = moment(item.created_at);
      labels.push(t.format("MMMM Do, hh:mm"))
      values.push(item.amount);
    });
    createWaterChart(labels, values);
  }).fail(function(err) {
    alert("Failed to fetch data: " + err.statusText);
  });

  $.getJSON("data/PlotExample.json", function(data) {
    var labels = new Array();
    var values = new Array();
    $.each(data, function(i, item) {
      var t = moment(item.created_at);
      labels.push(t.format("MMMM Do, hh:mm"))
      values.push(item.amount);
    });
    createWasteChart(labels, values);
  }).fail(function(err) {
    alert("Failed to fetch data: " + err.statusText);
  });
}

//
// Virtualization page initialization
// 
function InitVirtualizationPage() {
  console.debug("InitVirtualizationPage()");
  SetupNavigation();

  var jqxhr = $.getJSON("data/IoTEntities.json", function(data) {
      $.each(data.IoTEntity, function(i, item) {
        //console.debug(i, item);
        var measured = item.Properties[0].IoTStateObservation[0];
        if (measured != "" && measured != undefined) {
          measured = moment(measured.PhenomenonTime).format('MMMM Do YYYY, hh:mm:ss');
        } else {
          measured = 'N/A';
        }
        var odd = false;
        if (i % 2 == 0) {
          odd = true;
        }
        addServiceToTable("CNet", item.TypeOf[0], item.Name, measured, odd);
      });
    })
    .fail(function(err) {
      alert("Failed to fetch data: " + err.statusText);
    });
}

// 
// Adaptation page initialization
// 
function InitAdaptationPage() {
  console.debug("InitAdaptationPage()");
  SetupNavigation();

  createLondonMap();
  createSantanderMap();

  $.getJSON("data/MapLondon.json", function(data) {
    if (data.IoTEntity == undefined) {
      return;
    };
    $.each(data.IoTEntity, function(i, item) {
      $.each(item.Properties, function(i, p) {
        if (p.DataType != "xs:geojson") {
          return;
        };
        console.debug(p);
      });
    });
    //var labels = new Array();
    //var values = new Array();
    /*
    $.each(data, function(i, item) {
      var t = moment(item.created_at);
      labels.push(t.format("MMMM Do, hh:mm"))
      values.push(item.amount);
    });
    */
  }).fail(function(err) {
    alert("Failed to fetch data: " + err.statusText);
  });

  $.getJSON("data/MapSantander.json", function(data) {
    if (data.IoTEntity == undefined) {
      return;
    };
    $.each(data.IoTEntity, function(i, item) {
    });
  }).fail(function(err) {
    alert("Failed to fetch data: " + err.statusText);
  });

}

//
// Function to add a new row to the Virtualization page listing of services
// 
function addServiceToTable(network, type, name, measured, style) {
  $('.dataTable').each(function() {
    var $table = $(this);
    var tds = '<tr>';
    if (style) {
      tds = '<tr style="background-color:#ffffff;">';
    }
    tds += '<td>' + network + '</td>';
    tds += '<td>' + type + '</td>';
    tds += '<td>' + name + '</td>';
    tds += '<td>' + measured + '</td>';
    tds += '</tr>';
    if ($('tbody', this).length > 0) {
      $('tbody', this).append(tds);
    } else {
      $(this).append(tds);
    }
  });
}

//
// See Line configuration options here:
// http://www.chartjs.org/docs/
// 
function createWaterChart(labels, data) {
  var ctx = $("#waterChart").get(0).getContext("2d");
  var myNewChart = new Chart(ctx);
  var data = {
    labels: labels,
    datasets: [
      /*{
      fillColor: "rgba(220,220,220,0.5)",
      strokeColor: "rgba(220,220,220,1)",
      pointColor: "rgba(220,220,220,1)",
      pointStrokeColor: "#fff",
      data: [65, 59, 90, 81, 56, 55, 40]
    },*/
      {
        fillColor: "rgba(151,187,205,0.5)",
        strokeColor: "rgba(151,187,205,1)",
        pointColor: "rgba(151,187,205,1)",
        pointStrokeColor: "#fff",
        data: data
      }
    ]
  }
  new Chart(ctx).Line(data, {
    pointDot: false,
    bezierCurve: false
  });
}

//
// See Line configuration options here:
// http://www.chartjs.org/docs/
// 
function createWasteChart(labels, data) {
  var ctx = $("#wasteChart").get(0).getContext("2d");
  var myNewChart = new Chart(ctx);
  var data = {
    labels: labels,
    datasets: [
      /*{
      fillColor: "rgba(220,220,220,0.5)",
      strokeColor: "rgba(220,220,220,1)",
      pointColor: "rgba(220,220,220,1)",
      pointStrokeColor: "#fff",
      data: [65, 59, 90, 81, 56, 55, 40]
    },*/
      {
        fillColor: "rgba(151,187,205,0.5)",
        strokeColor: "rgba(151,187,205,1)",
        pointColor: "rgba(151,187,205,1)",
        pointStrokeColor: "#fff",
        data: data
      }
    ]
  }
  new Chart(ctx).Line(data, {
    pointDot: false,
    bezierCurve: false
  });
}

//
// See more options at:
// http://leafletjs.com/examples.html
// 
function createLondonMap() {
  var map = L.map('londonMap').setView([51.505, -0.09], 9);
  L.tileLayer('http://{s}.tiles.mapbox.com/v3/oleksandr.idl1bk7k/{z}/{x}/{y}.png', {
    attribution: 'Map data &copy; <a href="http://openstreetmap.org">OpenStreetMap</a> contributors, <a href="http://creativecommons.org/licenses/by-sa/2.0/">CC-BY-SA</a>, Imagery © <a href="http://mapbox.com">Mapbox</a>',
    maxZoom: 18
  }).addTo(map);
}

//
// See more options at:
// http://leafletjs.com/examples.html
// 
function createSantanderMap() {
  var map = L.map('santanderMap').setView([43.462778, -3.805], 9);
  L.tileLayer('http://{s}.tiles.mapbox.com/v3/oleksandr.idl1bk7k/{z}/{x}/{y}.png', {
    attribution: 'Map data &copy; <a href="http://openstreetmap.org">OpenStreetMap</a> contributors, <a href="http://creativecommons.org/licenses/by-sa/2.0/">CC-BY-SA</a>, Imagery © <a href="http://mapbox.com">Mapbox</a>',
    maxZoom: 18
  }).addTo(map);
}
