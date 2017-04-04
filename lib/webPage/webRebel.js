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

    $(".paragraphs").click(function(){
        infoPanelShown = !infoPanelShown;

        if(infoPanelShown){
            $(".webRebelInformationBox").css({"display": "block"}).addClass("webRebelInfoBoxPopup");
            $(".paragraphs").css({"position": "fixed", "top": "0px", "left": "18px", "font-size": "16px", "z-index": "6"})
                .animate({"top": "-300px", "font-size": "52px"});

        }else{
            $(".webRebelInformationBox").removeClass("webRebelInfoBoxPopup").addClass("webRebelInfoBoxPopdown");
            $(".paragraphs").animate({"top": "0px", "font-size": "16px"}); 

            setTimeout(function(){
                $(".webRebelInformationBox").removeClass("webRebelInfoBoxPopdown").css({"display": ""});
                $(".paragraphs").css({"position": "", "top": "", "left": "", "font-size": "", "z-index": ""});
            }, 500);
        }
    });
    $(".cellularSignalIcon").css({"width": "0px"});
    
    console.log("Setting up WebRebel socket server...");
    connection = new WebSocket("ws://" + window.location.hostname + "/socket", ["client"]);

    connection.onopen = function(){
        var iframe = $("iframe").get(0);
        console.log("Sucessfully connected to WebSocket server.");
        iframe.src = "/folder/";
        frame = iframe;

        $(iframe).load(iframeLoadHandler);
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
