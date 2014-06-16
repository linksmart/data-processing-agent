//
// Global variables in their name space (I hate you JS)
//
var Almanac = {
  //
  // API endpoints
  //

  // limit the query to 15 elements out of UX considerations
  waterConsumptionJsonAPI: "data/WastePlot.json",
  // limit the query to 15 elements out of UX considerations
  wasteCapacityJsonAPI: "data/WastePlot.json",
  // A listing of services (IoTEntities) for the virtualization page
  servicesJsonAPI: "data/IoTEntities.json",
  // should conform to GeoJSON format
  londonGeoJsonAPI: "data/MapLondon.json",
  // should conform to GeoJSON format
  santanderGeoJsonAPI: "data/MapLondon.json",

  //
  // Map objects
  //
  LondonMap: undefined,
  SantanderMap: undefined
}

$(document).ready(function() {
  console.debug(window.applicationCache);
  if (window.applicationCache) {
    applicationCache.addEventListener('updateready', function() {
      if (confirm('An update is available. Reload now?')) {
        window.location.reload();
      }
    });
  }
});

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

  $.getJSON(Almanac.waterConsumptionJsonAPI, function(data) {
    var labels = new Array();
    var values = new Array();
    $.each(data.IoTStateObservation, function(i, item) {
      var t = moment(item.PhenomenonTime);
      labels.push(t.format("MMMM Do, hh:mm"))
      values.push(item.Value);
    });
    createWaterChart(labels.slice(1, 15).reverse(), values.slice(1, 15).reverse());
  }).fail(function(err) {
    alert("Failed to fetch data: " + err.statusText);
  });

  $.getJSON(Almanac.wasteCapacityJsonAPI, function(data) {
    var labels = new Array();
    var values = new Array();
    $.each(data.IoTStateObservation, function(i, item) {
      var t = moment(item.PhenomenonTime);
      labels.push(t.format("MMMM Do, hh:mm"))
      values.push(item.Value);
    });
    createWasteChart(labels.slice(1, 15).reverse(), values.slice(1, 15).reverse());
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

  var jqxhr = $.getJSON(Almanac.servicesJsonAPI, function(data) {
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

  $.getJSON(Almanac.londonGeoJsonAPI, function(data) {
    if (data.features == undefined) {
      return;
    };
    var layer = L.geoJson(data, {
      style: function(feature) {
        return {
          color: feature.properties.color
        };
      },
      onEachFeature: function(feature, layer) {
        layer.bindPopup(feature.properties.description);
      }
    });
    layer.addTo(Almanac.LondonMap);
    Almanac.LondonMap.fitBounds(layer.getBounds());

  }).fail(function(err) {
    alert("Failed to fetch data: " + err.statusText);
  });

  $.getJSON(Almanac.santanderGeoJsonAPI, function(data) {
    if (data.IoTEntity == undefined) {
      return;
    };
    var layer = L.geoJson(data, {
      style: function(feature) {
        return {
          color: feature.properties.color
        };
      },
      onEachFeature: function(feature, layer) {
        layer.bindPopup(feature.properties.description);
      }
    });
    layer.addTo(Almanac.SantanderMap);
    Almanac.SantanderMap.fitBounds(layer.getBounds());

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
  Almanac.LondonMap = L.map('londonMap').setView([51.505, -0.09], 11);
  L.tileLayer('http://{s}.tiles.mapbox.com/v3/oleksandr.idl1bk7k/{z}/{x}/{y}.png', {
    attribution: 'Map data &copy; <a href="http://openstreetmap.org">OpenStreetMap</a> contributors, <a href="http://creativecommons.org/licenses/by-sa/2.0/">CC-BY-SA</a>, Imagery © <a href="http://mapbox.com">Mapbox</a>',
    maxZoom: 18
  }).addTo(Almanac.LondonMap);
}

//
// See more options at:
// http://leafletjs.com/examples.html
// 
function createSantanderMap() {
  Almanac.SantanderMap = L.map('santanderMap').setView([43.462778, -3.805], 11);
  L.tileLayer('http://{s}.tiles.mapbox.com/v3/oleksandr.idl1bk7k/{z}/{x}/{y}.png', {
    attribution: 'Map data &copy; <a href="http://openstreetmap.org">OpenStreetMap</a> contributors, <a href="http://creativecommons.org/licenses/by-sa/2.0/">CC-BY-SA</a>, Imagery © <a href="http://mapbox.com">Mapbox</a>',
    maxZoom: 18
  }).addTo(Almanac.SantanderMap);
}