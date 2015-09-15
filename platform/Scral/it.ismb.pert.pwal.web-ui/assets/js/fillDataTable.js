var dataJSON;
function fillDataTable(){
// 			$.getJSON("http://localhost:8080/connectors.rest/detaileddevices", function(data)
// 				{
// 				console.log(data);
				$.getJSON("http://localhost:8080/connectors.rest/detaileddevices2", function(data){
					this.dataJSON = data;
					console.log(this.dataJSON);
					$('#devices_table').dataTable( {
						"bProcessing": true,
				        "sAjaxSource": dataJSON,
				        "aoColumns": [
		                    { "mData": "pwalId" },
		                    { "mData": "type" },
		                    { "mData": "networkType" },
		                    { "mData": "location.lon" + " " + "location.lan" }
						]
					});	
				});
// 				});
	});
}