var connection = undefined;
var shakedHands = false;

$(window).load(() => {
    console.log("Setting up WebRebel socket server...");
    connection = new WebSocket("ws://" + window.location.hostname + "/socket", ["client"]);

    connection.onopen = function(){
        console.log("Sucessfully connected to WebSocket server.");
        var iframe = $("iframe").get(0);
        iframe.src = "/folder/";
        var oldLog = iframe.console.log;
        iframe.console.log = function(){
            var message = Array.slice(arguments);
            oldLog.call(this, message);
            new Action("CLIENT_CONSOLE_LOG", guid(), {
                "message": message
            });
        }
    }

    connection.onerror = function(error){
        console.log("Failed to connect to WebSocket server:", error);
    }

    connection.onmessage = function(message){
        var data = JSON.parse(message.data);
        var action = new Action(data.action, data.id, data.info);

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
            }
        }else if(data.action === "SERVER_PING"){
            console.log("Ping received! Sending pong...");
            new Action("CLIENT_PONG", data.id, {}).send();
        }
    }
});

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