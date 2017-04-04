
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

var randomizedValue = undefined;

function randomize(){

  var value = Math.floor(Math.random() * Object.keys(map).length);
  console.log(value);

  $("body").css({"background-color": map[value][1]});
  $(".paragraph").css({"color": map[value][2]});
  $(".leftFakeSyntaxHighlight").not(".ping").css({"color": map[value][3]});
  $(".rightFakeSyntaxHighlight").css({"color": map[value][2]});
  $(".genericFakeSyntaxHighlight").css({"color": map[value][4]});
  $(".bottomBarWrapper").css({"background-color": map[value][1]});
  $(".webRebelInformationBox").css({"background-color": map[value][1]})

  randomizedValue = value;    

}
