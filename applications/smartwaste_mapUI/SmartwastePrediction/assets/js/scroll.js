

var issue1 = {
   id:"Jodfkrgldfgmldfbldblbldfknbldfbhn1",
   date:new Date(),
   creator:"Shreekantha",
   resource:"Bin@Augustinum",
   type:"bin",
   assignee:"XYZ",
   state:State.OPEN,
   FillLevel:10,
   etc:new Date(),
   priority:Priority.MINOR,
   geolocation:{lat:50.769040, lng: 7.198483}
};


var issue2 = {
   id:"fgjkfgndflgevnldfgvdfmng2",
   date:new Date(),
   creator:"Shreekantha",
   resource:"Bin@Augustinum",
   type:"bin",
   assignee:"XYZ",
   state:State.OPEN,
   FillLevel:5,
   etc:new Date(),
   priority:Priority.UNCLASSIFIED,
   geolocation:{lat:50.764534, lng:7.205693}
};
var issue3 = {
  id:"fgjkfgndflegvfnldfgvdfmng3",
  date:new Date(),
  creator:"Shreekantha",
  resource:"Bin@Augustinum",
  type:"bin",
  assignee:"XYZ",
  state:State.OPEN,
  FillLevel:99,
  etc:new Date(),
  priority:Priority.MAJOR,
  geolocation:{lat:50.760679, lng: 7.195222}
};
var issue4 = {
    id:"fgjkfgfndeflgvnldfgvdfmng4",
    date:new Date(),
    creator:"Shreekantha",
    resource:"Bin@Augustinum",
    type:"bin",
    assignee:"XYZ",
    state:State.OPEN,
    FillLevel:80,
    etc:new Date(),
    priority:Priority.CRITICAL,
    geolocation:{lat:50.765783, lng: 7.207753}
};

var issue5 = {
    id:"fgjkfgfndeflgvnldfgvfmng44",
    date:new Date(),
    creator:"Shreekantha",
    resource:"Bin@Augustinum",
    type:"bin",
    assignee:"XYZ",
    state:State.OPEN,
    FillLevel:80,
    etc:new Date(),
    priority:Priority.CRITICAL,
    geolocation:{lat:50.7673744, lng: 7.2110523}
};

var issues = [issue1,issue2,issue3,issue4,issue5];

var scroller = document.getElementById('labeled-slider');
var status_msg = document.getElementById('status-msg');

scroller.addEventListener('change', function () {

  for(i=0;i<issues.length;i++){
    issues[i].FillLevel =  Math.floor((Math.random() * 100) )
    updateMarker(issues[i]);
  }

}, false);

