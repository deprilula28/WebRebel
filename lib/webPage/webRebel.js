
function messageParser(message){

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
    else  if(data.action === "SERVER_HANDSHAKE"){
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
            });
        }
    }else if(data.action === "SERVER_PING"){
        new Action("CLIENT_PONG", data.id, {}).send();
    }else if(data.action === "SERVER_CODE_UPDATE"){
        var json = data.info;
        //console.log("Server code was updated: +" + Object.keys(json.insertions).length + " -" + json.deletions.length + " <>" + Object.keys(json.changes).length);
        console.log("Server code was streamed, updating internal code...");
        serverCodeUpdate(json);
        console.log("Done.");
    }else if(data.action === "SERVER_CLIENT_PING_INFO"){
        var ping = data.info.ping;
        if(lockdown){
            lockdown = false;
            $(".ping").css({"color": "#29db61"});
            $(".mainImg").css({"opacity": 1}).animate({"opacity": 0});
            $(".paragraph").css({"opacity": 1}).animate({"opacity": 0});
            $(".bottomBarWrapper").animate({"opacity": 1});
            $("body").animate({"background-color": "#FFFFFF"});
        }
        $(".greenIconMaster").css({"width": Math.min((-(ping / 1000.0) + 1.0) * 16.0, 16.0)});
        $(".ping").text(ping + "ms");

        if(timeoutTask) clearTimeout(timeoutTask);
        timeoutTask = setTimeout(function() {
            $(".mainImg").css({"opacity": 0}).animate({"opacity": 1});
            $(".paragraph").css({"opacity": 0}).animate({"opacity": 1});
            $(".bottomBarWrapper").animate({"opacity": 0});
            $("body").animate({"background-color": map[randomizedValue][1]});
            $(".greenIconMaster").css({"width": "0px"});
            $(".ping").text("Connection lost...").css({"color": "red"});
            $("iframe").animate({"opacity": 0}, 150, "linear", () => {
                $("iframe").css({"opacity": ""});
            });
            $(".paragraph").text("Connection lost, try reloading the page");
            lockdown = true;
        }, 10000);
    }else if(data.action === "SERVER_DOM_REQUEST"){
        var path = data.info.expand;
        var targetDOMElement = undefined;

        if(path){
             var topElement = path[path.length - 1];
             targetDOMElement = domElementIDs[topElement];
         }else targetDOMElement = frame.contentWindow.document.body;

        var children = targetDOMElement.children;
        var domElements = [];

        for(var curChildrenIndex in children) domElements.push(getJSONForElement(children[curChildrenIndex]));
        new Action("CLIENT_DOM_RESPONSE", data.id, {
            "domElements": domElements
        }).send();
    }else if(data.action === "SERVER_REQUEST_FULL_RELOAD"){
        frame.src = frame.src;
        console.log("Reloading frame...")
    }

}

function getJSONForElement(domElement){
    
    console.log("Getting JSON element for:", domElement)
    var uuid = guid();
    domElementIDs[uuid] = domElement;
    var json = {};

    json.elementType = domElement.tagName;
    json.attributes = {};
    json.hasChildren = domElement.children && domElement.children.length > 0;
    json.path = uuid;

    return json;

}

var domElementIDs = {};

function iframeLoadHandler(){
    var iframe = frame;

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
    iframe.contentWindow.$("script").each(function(index, el){
        parseDOMElementScript(el);
    });
    iframe.contentWindow.$("link").each(function(index, el){
        parseDOMElementCSSLink(el);
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
}

var connection = undefined;
var shakedHands = false;

var timeoutTask = undefined;
var frame = undefined;

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

var infoPanelShown = false;

window.onload = function(){

    randomize();
    calculateCenter();
    window.addEventListener("resize", calculateCenter, false);

    var paragraphs = $(".paragraphs");
    var webRebelInformationBox = $(".webRebelInformationBox")
    var pingFrame = $(".pingFrame")

    paragraphs.click(function(){
        infoPanelShown = !infoPanelShown;

        if(infoPanelShown){
            webRebelInformationBox.css({"display": "block"}).addClass("webRebelInfoBoxPopup");
            paragraphs.css({"position": "fixed", "top": "0px", "left": "18px", "font-size": "16px", "z-index": "6"})
                .animate({"top": "-300px", "font-size": "52px"});
            pingFrame.css({"position": "fixed", "top": "0px", "left": "18px"}).animate({"top": "-280px", "left": "265px"});
        }else{
            webRebelInformationBox.removeClass("webRebelInfoBoxPopup").addClass("webRebelInfoBoxPopdown");
            paragraphs.animate({"top": "0px", "font-size": "16px"}); 
            pingFrame.animate({"top": "0px", "left": "18px", "font-size": "16px"}); 

            setTimeout(function(){
                webRebelInformationBox.removeClass("webRebelInfoBoxPopdown").css({"display": ""});
                paragraphs.css({"position": "", "top": "", "left": "", "font-size": "", "z-index": ""});
                pingFrame.css({"position": "", "top": "", "left": "", "font-size": "", "z-index": ""});
            }, 500);
        }
    });
    $(".cellularSignalIcon").css({"width": "0px"});
    
    console.log("Setting up WebRebel socket server...");
    connection = new WebSocket("ws://" + window.location.hostname + "/socket", ["client"]);

    connection.onopen = function(){
        var $iframe = $("iframe");
        var iframe = $iframe.get(0);
        console.log("Sucessfully connected to WebSocket server.");
        iframe.src = "/folder/";
        frame = iframe;

        $iframe.load(iframeLoadHandler);
    }

    connection.onerror = function(error){
        console.log("Failed to connect to WebSocket server:", error);
    }

    connection.onmessage = messageParser;
}

var lockdown = false;

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

function serverCodeUpdate(json){

    var source = json.type;
    
    var originFile = undefined;

    if(source === "js") originFile = subscribedScripts["folder/" + json.source];
    if(source === "css") originFile = subscribedStyles["folder/" + json.source];

    console.log(originFile, source);
    if(typeof originFile === "undefined" && source !== "html"){
        console.warn("Detected code change for script not subscribed to");
        return;
    }

    var lines = json.lines;
    if(source === "html") frame.contentWindow.document.body.innerHTML = lines.join("\n").substring("<body>".length, lines.join("\n").length - "</body>".length);
    else if(source === "css") originFile.innerHTML = lines.join("\n");
    else if(source === "js"){
        $(originFile).remove();
        var newScript = document.createElement("script");
        newScript.innerHTML = lines.join("\n");
        frame.contentWindow.document.body.appendChild(newScript);
        subscribedScripts["folder/" + json.source] = newScript;
    }

}

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
            else if(insertedNode.tagName == "LINK") parseDOMElementCSSLink(insertedNode);
        }
    }

}

var subscribedScripts = {};
var subscribedStyles = {};

function getSubscriptionRelativePath(source){

    if(source.indexOf("/") == 0) return source.substring(1);
    else if(source.indexOf("/") == -1 || (source.indexOf(/[^.]*\.[^.]{2,3}(?:\.[^.]{2,3})?$/) > source.indexOf("/"))) return source;
    else if(source.indexOf(window.location.hostname) == 0) return source.substring((window.location.hostname + "/").length);
    else if(source.indexOf("http://" + window.location.hostname) == 0) return source.substring(("http://" + window.location.hostname + "/").length);
    else if(source.indexOf("https://" + window.location.hostname) == 0) return source.substring(("https://" + window.location.hostname + "/").length);
    else return undefined;

}

function parseDOMElementCSSLink(node){

    if(node.rel !== "stylesheet") return;
    if(node.type !== "text/css") return;

    var source = node.href;
    var subscriptionRelPath = getSubscriptionRelativePath(source);
    if(!subscriptionRelPath) return;
    
    //Setting the style's code internally for easily changing it when code updates
    $(node).remove();

    var style = frame.contentWindow.document.createElement("style");
    subscribedStyles[subscriptionRelPath] = style;

    $.ajax("http://" + window.location.hostname + "/" + subscriptionRelPath).done(function(data){
        var head = frame.contentWindow.document.head;

        style.innerHTML = data;
        head.appendChild(style);
    }).fail(function(jqXHR, textStatus, errorThrown){
        console.warn("Failed to get resource: " + subscriptionRelPath + " Error: " + textStatus + ", " + errorThrown);
    })
    
}

function parseDOMElementScript(node){

    if(!node.src) return;

    var source = node.src;
    var subscriptionRelPath = getSubscriptionRelativePath(source);
    if(!subscriptionRelPath) return;

    subscribedScripts[subscriptionRelPath] = node;

    //Setting the script's code internally for easily changing it when code updates
    node.src = "";
    
    $.ajax("http://" + window.location.hostname + "/" + subscriptionRelPath).done(function(data){
        node.innerHTML = data;
    }).fail(function(jqXHR, textStatus, errorThrown){
        console.warn("Failed to get resource: " + subscriptionRelPath + " Error: " + textStatus + ", " + errorThrown);
    })
    
}
