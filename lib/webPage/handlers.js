
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
         }else targetDOMElement = document.body;

        var children = $(targetDOMElement).children();
        var domElements = [];

        for(var curChildrenIndex in children) domElements.push(getJSONForElement(children[curChildrenIndex]));
        new Action("CLIENT_DOM_RESPONSE", data.id, {
            "domElements": domElements
        }).send();
    }

}

function getJSONForElement(domElement){

    var guid = guid();
    domElementIDs[guid] = domElement;
    var json = {};

    json.elementType = domElement.tagName;
    json.attributes = {};
    json.hasChildren = $(domElement).children().length > 0;
    json.path = guid;

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
