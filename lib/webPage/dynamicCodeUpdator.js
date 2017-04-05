
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

    /*
    //Thanks to http://stackoverflow.com/a/56671 (Unescaped \n finder for finding lines)
    var lines = source === "html" ? 
        frame.contentWindow.document.body.innerHTML.split(/[^\\]\|/) : 
        originFile.innerHTML.split(/[^\\]\|/); 
    var insert = json.insertions;
    var deletions = json.deletions;
    var change = json.changes;

    for(var curDeletionIndex in deletions){
        var curDeletion = deletions[curDeletionIndex];
        lines.splice(curDeletion, 1);
    }

    jQuery.each(insert, function(key, value){
        var numb = parseInt(key);
        lines.splice(numb, 0, value);
    });
 
    jQuery.each(change, function(key, value){
        var numb = parseInt(key);
        lines[numb] = value;
    });
    */

    //TODO Find a better way to stream code changes

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
