
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
