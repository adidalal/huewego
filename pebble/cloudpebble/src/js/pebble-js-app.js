function checkServer() {
	console.log("checkServer 2()");
    var req = new XMLHttpRequest();
	req.open('GET', 'http://redgreenlight.herokuapp.com', true);
    req.onload = function(e) {
      //console.log("event: " + JSON.stringify(e));
      if (req.readyState == 4 && req.status == 200) {
        console.log("http success! Got: " + req.responseText);
        var color = Number(req.responseText);
		Pebble.sendAppMessage({ 1: color }, function() {
			setTimeout(checkServer, 1000);
		}, function () {
			setTimeout(checkServer, 1000);
		});
      }
    };	
	req.send(null);
}

Pebble.addEventListener('ready', function() {
	console.log("ready starting...");
	checkServer();
});