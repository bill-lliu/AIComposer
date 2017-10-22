var audio;

function initialize(ssid) {
	var pdf = window.open("http://13.82.60.131:8000/"+ssid+".pdf");
	pdf.blur();
	window.focus();
	audio = document.getElementById("audio");
	audio.crossOrigin = 'anonymous';
	audio.controls = true;
	
	var oReq = new XMLHttpRequest();
	oReq.open("POST", "http://13.82.60.131:8080/mp3", true);
	oReq.responseType = "blob";

	oReq.onload = function(oEvent) {
		var blob = oReq.response;
		audio.src=window.URL.createObjectURL(blob);
	};

	var formData = new FormData();
	formData.append("id",ssid);
	oReq.send(formData);
	
	//audio.src="http://13.82.60.131:8000/"+ssid+".mp3";
  console.log(audio);
}



function visualize() {
    audio.load();
    var playPromise = audio.play();
	
	if (playPromise !== undefined) {
  playPromise.then(function() {
    // Automatic playback started!
  }).catch(function(error) {
	  console.log(error);
    // Automatic playback failed.
    // Show a UI element to let the user manually start playback.
  });
}
	
    var context = new AudioContext();1
    var src = context.createMediaElementSource(audio);
    var analyser = context.createAnalyser();

    var canvas = document.getElementById("canvas");
    canvas.width = window.innerWidth;
    canvas.height = window.innerHeight;
    var ctx = canvas.getContext("2d");

    src.connect(analyser);
    analyser.connect(context.destination);

    analyser.fftSize = 256;

    var bufferLength = analyser.frequencyBinCount;
    console.log(bufferLength);

    var dataArray = new Uint8Array(bufferLength);

    var WIDTH = canvas.width;
    var HEIGHT = canvas.height;

    var barWidth = (WIDTH / bufferLength) * 2.5;
    var barHeight;
    var x = 0;
	
	function renderFrame() {
		requestAnimationFrame(renderFrame);

		x = 0;

		analyser.getByteFrequencyData(dataArray);

		ctx.fillStyle = "#000";
		ctx.fillRect(0, 0, WIDTH, HEIGHT);

		for (var i = 0; i < bufferLength; i++) {
			barHeight = dataArray[i];
        
			var r = barHeight + (25 * (i/bufferLength));
			var g = 250 * (i/bufferLength);
			var b = 50;

			ctx.fillStyle = "rgb(" + r + "," + g + "," + b + ")";
			ctx.fillRect(x, HEIGHT - barHeight, barWidth, barHeight);

			x += barWidth + 1;
		}
	}
    var playPromiseT = audio.play();
		if (playPromiseT !== undefined) {
  playPromiseT.then(function() {
    // Automatic playback started!
  }).catch(function(error) {
	  console.log(error);
    // Automatic playback failed.
    // Show a UI element to let the user manually start playback.
  });
}
    renderFrame();
}