/**
 * Function to initialize the map
 */
var map
 function initialize() {
    var mapCanvas = document.getElementById('map-canvas');
    var mapOptions = {
      center: new google.maps.LatLng(50.769040, 7.198483),

      mapTypeId: google.maps.MapTypeId.ROADMAP
    }
    map = new google.maps.Map(mapCanvas, mapOptions);

  }

/*
Get the key for issue. Currently onlu ID is used
*/
function issuekey(issue){
  return issue.id;
}

var issueList = {};


/*
Initializer marker locations.
This also makes sure that all the issues are within the map.
Map zoom level is changed according to number of issues
*/
 function initializeMarkers(issues){
    var latlngbounds = new google.maps.LatLngBounds();//To set boundaries such that all the markers are included
   for (var i = 0, l = issues.length; i < l; i++) {
       var issue = issues[i];
       issue.marker = new google.maps.Marker({
             position: issue.geolocation,
             map: map,
             title: issue.resource,

            });
       latlngbounds.extend(new google.maps.LatLng(issue.geolocation.lat,issue.geolocation.lng));
       updateMarkerProperties(issue);

       issueList[issuekey(issue)] = issue;
   }
    map.fitBounds(latlngbounds);
 }

function updateMarker(issue){
    updateMarkerProperties(issueList[issuekey(issue)]);
}


/*
To update the marker properties on getting MQTT message from server
*/
function updateMarkerProperties(issue){
    if(issue.marker == undefined || issue.marker == null){
        return;
    }
    if(issue.state == State.CLOSED){
        issue.marker.setMap(null);
        issue.marker = null;

    }
    switch(issue.priority){
    case Priority.UNCLASSIFIED:
        issue.marker.setIcon('http://maps.google.com/mapfiles/ms/icons/green-dot.png');
        break;
    case Priority.MINOR:
        issue.marker.setIcon('http://maps.google.com/mapfiles/ms/icons/yellow-dot.png');
        break;
    case Priority.MAJOR:
        issue.marker.setIcon('http://maps.google.com/mapfiles/ms/icons/orange-dot.png');
        break;
    case Priority.CRITICAL:
        issue.marker.setIcon('http://maps.google.com/mapfiles/ms/icons/red-dot.png');
        break;
    }

}
 google.maps.event.addDomListener(window, 'load', initialize);


