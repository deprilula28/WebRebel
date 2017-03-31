function calculateCenter(){
  
  $(".mainImg").css({"left": ($(window).width() / 2 - 375 / 2) + "px", "top": ($(window).height() / 2 - 150) + "px"});
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

window.onload = function(){
    randomize();
    calculateCenter();
    window.addEventListener("resize", calculateCenter, false);

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
        }
    }
}

var map = [
  [
    "http://i.imgur.com/nukC5TQ.png", 
    "#FFFFFF",
    "#81A2BE"
  ],
  [
    "http://i.imgur.com/4CWlKBI.png", 
    "#282C34",
    "#58AFEF"
  ],
  [
    "http://i.imgur.com/JRDl6nD.png",
    "#1E1E1E",
    "#CE6706"
  ],
  [
    "http://i.imgur.com/sPmlkq4.png",
    "#002B36",
    "#839484"
  ],
  [
    "http://i.imgur.com/yDEsjEO.png", 
    "#000C18",
    "#DDBB88"
  ]
];

function randomize(){
  var value = Math.floor(Math.random() * Object.keys(map).length);
  console.log(value);

  $(".mainImg").get(0).src = map[value][0];
  $("body").css({"background-color": map[value][1]});
  $(".paragraph").css({"color": map[value][2]});
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