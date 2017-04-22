
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
    
    console.log("Subscribed to " + subscriptionRelPath + " with node", node);
    
    //Setting the style's code internally for easily changing it when code updates
    $(node).remove();

    var style = frame.contentWindow.document.createElement("style");
    subscribedStyles[subscriptionRelPath] = style;
    openGETRequest("http://" + window.location.hostname + "/" + subscriptionRelPath, function(resText){
        var head = frame.contentWindow.document.head;

        style.innerHTML = resText;
        head.appendChild(style);
    });
    
}

function parseDOMElementScript(node){

    if(!node.src) return;

    var source = node.src;
    var subscriptionRelPath = getSubscriptionRelativePath(source);
    if(!subscriptionRelPath) return;

    subscribedScripts[subscriptionRelPath] = node;

    //Setting the script's code internally for easily changing it when code updates
    node.src = "";
    openGETRequest("http://" + window.location.hostname + "/" + subscriptionRelPath, function(resText){
        node.innerHTML = resText;
    });

}
