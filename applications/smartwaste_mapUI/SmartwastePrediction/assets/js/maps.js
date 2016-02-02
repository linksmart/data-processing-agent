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
count = 0;

/*
Initializer marker locations.
This also makes sure that all the issues are within the map.
Map zoom level is changed according to number of issues
*/
 function initializeMarkers(issues, callback){
    console.log(count++);
    if  (map == undefined){
        //postpone the execution
            retVal = setTimeout(initializeMarkers, 50,issues, callback);
        return;
    }
    var latlngbounds = new google.maps.LatLngBounds();//To set boundaries such that all the markers are included
    for (var i = 0, l = issues.length; i < l; i++) {
       var issue = issues[i];
       var marker = new google.maps.Marker({
             position: issue.geolocation,
             map: map,
             title: issue.resource,

            });
       marker.issueid = issue.id;
       with ({ curmark: marker }) {
       curmark.addListener('click', function() {
          callback(curmark.issueid);
         });
        }
       issue.marker = marker;
       latlngbounds.extend(new google.maps.LatLng(issue.geolocation.lat,issue.geolocation.lng));
       updateMarkerProperties(issue);

       issueList[issuekey(issue)] = issue;
   }
    map.fitBounds(latlngbounds);
 }

function updateMarker(issue){
    issueToUpdate = issueList[issuekey(issue)];
    issueToUpdate.creator = issue.creator;
    issueToUpdate.resource = issue.resource;
    issueToUpdate.type= issue.type;
    issueToUpdate.assignee=issue.assigne;
    issueToUpdate.state=issue.state;
    issueToUpdate.FillLevel=issue.FillLevel;
    issueToUpdate.etc = issue.etc;
    issueToUpdate.priority=issue.priority;
    issueToUpdate.geolocation = issue.geolocationl
    updateMarkerProperties(issueToUpdate);
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
    switch(true){
    case (issue.FillLevel>=0 && issue.FillLevel <= 25):
        issue.marker.setIcon('assets/img/0-25.png');
        break;
    case  (issue.FillLevel> 25 && issue.FillLevel <= 50):
        issue.marker.setIcon('assets/img/25-50.png');
        break;
    case  (issue.FillLevel> 50 && issue.FillLevel <= 75):
        issue.marker.setIcon('assets/img/50-75.png');
        break;
    case  (issue.FillLevel> 75 && issue.FillLevel <= 100):
        issue.marker.setIcon('assets/img/75-100.png');
        break;
    }

}
 google.maps.event.addDomListener(window, 'load', initialize);


