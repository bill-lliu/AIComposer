var musicType;

function leftAnimationOut(a,b){
	var oldId=a.id;
	a.id='leftAnimateOut';
	setTimeout(function() {
		a.style.display = "none";
		a.id=oldId;
	}, 1000);

	if(b){
		if(oldId=="romanticLeft"){
			rightAnimationOut(document.getElementById("fugueRight"),false);
			leftAnimationIn(document.getElementById("recordLeft"));
			rightAnimationIn(document.getElementById("keyboardRight"));
			musicType="ml";
		}else if(oldId=="recordLeftS"){
			rightAnimationOut(document.getElementById("keyboardRightS"),false);
			document.body.style="margin:0px;padding:0px;background: DarkGrey ";
			setTimeout(function(){document.getElementById("microphoneRecord").style.visibility="visible"},750);
		}
	}
}

function rightAnimationOut(a,b){
var oldId=a.id;
	a.id='rightAnimateOut';
	setTimeout(function() {
		a.style.display = "none";
		a.id=oldId;
	}, 1000);

	if(b){
		if(oldId=="fugueRight"){
			leftAnimationOut(document.getElementById("romanticLeft"),false);
			leftAnimationIn(document.getElementById("recordLeft"));
			rightAnimationIn(document.getElementById("keyboardRight"));
			musicType="fugue";
		}else if(oldId=="keyboardRightS"){
			leftAnimationOut(document.getElementById("recordLeftS"),false);
			
			var xhttp = new XMLHttpRequest();
			xhttp.addEventListener("load", function(e) {
					uuid=this.responseText;
					displayVisual(uuid);
			});
			xhttp.open("POST", "http://13.82.60.131:8080/compute",true);
			var formData = new FormData();
			document.getElementById("microphoneRecord").style.visiblity = "none";
			document.getElementById("loading").style.visibility = "visible";
			formData.append("type","auto");
			formData.append("task",musicType);
			xhttp.send(formData);
			console.log(xhttp.responseText);
		}
	}
}

function leftAnimationIn(a){
	var oldId=a.id;
	a.id='leftAnimateIn';
	a.style.display="block";
	setTimeout(function() {
		a.id=oldId+"S";
		a.style.display="block";
	}, 1000);
}

function rightAnimationIn(a){
	var oldId=a.id;
	a.id='rightAnimateIn';
	a.style.display="block";
	setTimeout(function() {
		a.id=oldId+"S";
		a.style.display="block";
	}, 1000);
}

var flag =false;
function micRecord(){
	if(!flag){
		recorder.start();
		flag = true;
		document.getElementById("microphoneRecord").id="microphoneRecordActive";
	}else{
		document.getElementById("microphoneRecordActive").style.visibility="hidden";
		recorder.stop(callBack);
		document.getElementById("loading").style.visibility="visible";
	}
}

function callBack(i){
}

function displayVisual(uuid){
	if(document.getElementById("microphoneRecordActive")!=null){
		document.getElementById("microphoneRecordActive").id="microphoneRecord";
	}
	initialize(uuid);
	console.log("Here");
	document.getElementById("canvas").style.visibility="visible";
	document.getElementById("audio").style.visibility="visible";
	visualize();
}

