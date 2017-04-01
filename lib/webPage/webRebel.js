function calculateCenter(){
  
  $(".mainImg").css({"left": ($(window).width() / 2 - $(".mainImg").get(0).clientWidth / 2) + "px", "top": ($(window).height() / 2 - 150) + "px"});
  var $paragraph = $(".paragraph");
  var paragraph = $paragraph.get(0);
  $paragraph.css({"left": $(window).width() / 2 - paragraph.clientWidth / 2, "top": $(window).height() / 2 + 30});
  
}

var consoleMethods = [
    "assert", "clear", "count", "debug",
    "dir", "dirxml", "error", "exception",
    "group", "groupCollapsed", "groupEnd",
    "info", "profile", "profileEnd",
    "table", "time", "timeEnd", "timeStamp",
    "trace", "warn", "log"
];



function domChangeEventHandler(event){

    //TODO mess with it
    console.warn("Bad state: DOM Change event handler is being used but is not supported yet!");

}

function mutationObserveHandler(mutations){

    for(var curMutationIndex in mutations){
        var curMutation = mutations[curMutationIndex];

        for(var insertedNodeIndex in curMutation.addedNodes){
            var insertedNode = curMutation.addedNodes[insertedNodeIndex];
            if(insertedNode.tagName == "SCRIPT") parseDOMElementScript(insertedNode);
        }
    }

}

var subscribedScripts = {};

function parseDOMElementScript(node){

    if(!node.src) return;

    var source = node.src;
    var subscriptionRelPath = undefined;

    //Finding sources within the server
    //Methods:
    //
    //The / is in the first character (URLs like /foo/bar.html)
    //There is no / (URLs like foobar.html)
    //The first TLD comes after the first / (URLs like /foo/bar.com) [Thanks to http://stackoverflow.com/a/21174423]
    //Starts with the URL of the page, prefixed with http:// and even https://
    
    if(source.indexOf("/") == 0) subscriptionRelPath = source.substring(1);
    else if(sourceIndex.indexOf("/") == -1 || (sourceIndex.indexOf(/[^.]*\.[^.]{2,3}(?:\.[^.]{2,3})?$/) > sourceIndex.indexOf("/"))) subscriptionRelPath = source;
    else if(source.indexOf(window.location.hostname) == 0) subscriptionRelPath = source.substring(window.location.hostname.length);
    else if(source.indexOf("http://" + window.location.hostname) == 0) subscriptionRelPath = source.substring(("http://" + window.location.hostname).length);
    else if(source.indexOf("https://" + window.location.hostname) == 0) subscriptionRelPath = source.substring(("https://" + window.location.hostname).length);
    else return;

    subscribedScripts[subscriptionRelPath] = node;

    //Setting the script's code internally for easily changing it when code updates
    node.src = "";
    openGETRequest(window.location.hostname + "/" + subscriptionRelPath, function(resText){
        node.innerHTML = resText;
    });


}

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

window.onload = function(){
    randomize();
    calculateCenter();
    window.addEventListener("resize", calculateCenter, false);

    $(".bottomBarWrapper").css({"top": $(window).height() - 30 + "px"});

    $(window).resize(function(event){
        $(".bottomBarWrapper").css({"top": $(window).height() - 30 + "px"});
    });

    console.log("Setting up WebRebel socket server...");
    connection = new WebSocket("ws://" + window.location.hostname + "/socket", ["client"]);

    connection.onopen = function(){
        var iframe = $("iframe").get(0);
        console.log("Sucessfully connected to WebSocket server.");
        iframe.src = "/folder/";

        $(iframe).load(function(){
            for(var curMethodIndex in consoleMethods){
                var curMethod = consoleMethods[curMethodIndex];
                if(iframe.contentWindow.console[curMethod]){                    
                    var oldLogger = iframe.contentWindow.console[curMethod];
                    iframe.contentWindow.console[curMethod] = function(){
                        var message = Array.prototype.slice.call(arguments).join(" ");
                        oldLogger.call(this, message);
                        new Action("CLIENT_CONSOLE_LOG", guid(), {
                            "message": message,
                            "type": curMethod
                        }).send();
                    }
                }else console.warn("Method console." + curMethod + " doesn't exist!");
            }

            iframe.contentWindow.console.log("Listening console events.");
            $("script").each(function(el){
                parseDOMElementScript(el);
            });

            //Thanks to http://stackoverflow.com/a/14570614
            var MutationObserver = iframe.contentWindow.window.MutationObserver || iframe.contentWindow.window.WebKitMutationObserver;
            if(!MutationObserver){
                iframe.contentWindow.window.addEventListener("DOMNodeInserted", domChangeNodeAdd, false);
                console.log("Using window events as DOM update handler.");
            }else{
                var mutOb = new MutationObserver(function(mutations, observer){
                    mutationObserveHandler(mutations);
                });
                mutOb.observe(iframe.contentWindow.document.body, {"childList": true, "subtree": true});
                console.log("Using mutation observer as DOM update handler.");
            }
        });
    }

    connection.onerror = function(error){
        console.log("Failed to connect to WebSocket server:", error);
    }

    connection.onmessage = function(message){
        var data = JSON.parse(message.data);
        var action = new Action(data.action, data.id, data.info);

        if(!shakedHands && data.action !== "SERVER_HANDSHAKE"){
            console.log("[Error] Server didn't handshake and is already asking stuff!");
            new Action("CLIENT_ERROR", guid(), {
                "error": "Never shaked hands",
                "message": "Must do handshake first."
            }).send();
            return;
        }

        if(pendingActionRequests[action.id]) pendingActionRequests[action.id](action);
        else if(data.action === "SERVER_ERROR_RESPONSE") console.log("[Error] " + data.info.error + ": " + data.info.errorMessage);
        else if(data.action === "SERVER_HANDSHAKE"){
            if(shakedHands){
                console.log("[Error] Server asked for handshake twice!");
                new Action("CLIENT_ERROR", guid(), {
                    "error": "Already Shaked Hands",
                    "message": "Handshake request was provided previously."
                }).send();
            }else{
                shakedHands = true;
                new Action("CLIENT_HANDSHAKE", guid(), {}).send();

                console.log("Shaked hands with server.");
                window.removeEventListener("resize", calculateCenter, false);
                
                $(".mainImg").css({"opacity": 1}).animate({"opacity": 0});
                $(".paragraph").css({"opacity": 1}).animate({"opacity": 0});
                $(".bottomBarWrapper").animate({"opacity": 1});
                $("body").animate({"background-color": "#FFFFFF"});

                $("iframe").animate({"opacity": 1}, 150, "linear", () => {
                    $("iframe").css({"opacity": 1});
                    $(".mainImg").remove();
                    $(".paragraph").remove();
                });
            }
        }else if(data.action === "SERVER_PING"){
            new Action("CLIENT_PONG", data.id, {}).send();
        }else if(data.action === "SERVER_CODE_UPDATE"){
            var json = data.info;
            console.log("Server code was updated: +" + Object.keys(json.insertions).length + " -" + json.deletions.length + " <>" + Object.keys(json.changes).length);
            console.log("Updating internal code...");
            serverCodeUpdate(json);
        }
    }

}

function serverCodeUpdate(json){



}

//Arg 0: source
//Arg 1: Background Color
//Arg 2: Right Color
//Arg 3: Left Color
//Arg 4: Dot Color
var map = [
  [
    "http://i.imgur.com/nukC5TQ.png", 
    "#FFFFFF",
    "#81A2BE",
    "#CC667D",
    "#323B74"
  ],
  [
    "http://i.imgur.com/4CWlKBI.png", 
    "#282C34",
    "#58AFEF",
    "#E05848",
    "#ABB2A9"
  ],
  [
    "http://i.imgur.com/JRDl6nD.png",
    "#1E1E1E",
    "#CE6706",
    "#326E9C",
    "#A5C2C6"
  ],
  [
    "http://i.imgur.com/sPmlkq4.png",
    "#002B36",
    "#839484",
    "#025DB6",
    "#839484"
  ],
  [
    "http://i.imgur.com/yDEsjEO.png", 
    "#000C18",
    "#DDBB88",
    "#397CC0",
    "#4280CC"
  ]
];

function randomize(){
  var value = Math.floor(Math.random() * Object.keys(map).length);
  console.log(value);

  $("body").css({"background-color": map[value][1]});
  $(".paragraph").css({"color": map[value][2]});
  $(".leftFakeSyntaxHighlight").css({"color": map[value][3]});
  $(".rightFakeSyntaxHighlight").css({"color": map[value][2]});
  $(".genericFakeSyntaxHighlight").css({"color": map[value][4]});
  $(".bottomBarWrapper").css({"background-color": map[value][1]});
}

var connection = undefined;
var shakedHands = false;

function guid(){
  
    function s4(){
        return Math.floor((1 + Math.random()) * 0x10000).toString(16).substring(1);
    }

    return s4() + s4() + '-' + s4() + '-' + s4() + '-' + s4() + '-' + s4() + s4() + s4();

}

var pendingActionRequests = {};

function Action(actionType, id, actionRequest){

    this.actionType = actionType;
    this.id = id;
    this.actionRequest = actionRequest;

}

Action.prototype.send = function(){

    connection.send(JSON.stringify({"action": this.actionType, "id": this.id, "info": this.actionRequest}));

}

Action.prototype.send = function(responseListener){

    connection.send(JSON.stringify({"action": this.actionType, "id": this.id, "info": this.actionRequest}));
    pendingActionRequests[this.id] = responseListener;

}