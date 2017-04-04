
function openGETRequest(url, completeHandler){

    var request = new XMLHttpRequest();
	var received = false;

	request.open('GET', url, true);
	request.onreadystatechange = function(e){

		if(!received && request.readyState == 4){
			received = true;
			completeHandler(request.responseText);
		}

	}

	request.send(null);

}
