var resolution = 20;
var editor = null;
var ws = null;
var speed = 500;

//Configuring editor
$(document).ready(()=>{
	editor = ace.edit("cli-editor");
	editor.$blockScrolling = true;
	editor.setTheme("ace/theme/monokai");
	editor.getSession().setMode("ace/mode/clojure");
	editor.getSession().on('change', function(e) {
		try {
			geditor = $("#gui-editor");
			geditor.empty();
			if(ws) ws.close();
			var text = editor.getValue();
			var ele = ctlParser.parse(text);
			if(ele==undefined) ele = ctlParser.parse(text);
			geditor.append(ele);
			setTimeout(function(){
				editor.session.setAnnotations([]);
				editor.clearSelection();
			},0);
		} catch (error) {
			geditor = $("#gui-editor");
			geditor.empty();
			setTimeout(function(){
				if(error.location) {
					var sr = error.location.start.line-1;
					var sc = error.location.start.column-1;
					editor.session.setAnnotations([{row:sr,column: sc, text: error.message,type:"error"}]);
				}
			},0);
		}
	});
	editor.setValue(PF("ctl").jq.val());
});

function simulate() {
	var info = document.getElementById("info");
	$(info).empty();
	$("#gui-editor>canvas").remove();
	var footer = document.getElementById("footerLayoutPane");
	var gui = document.getElementById("gui-editor");
	var h = $("#gui-editor>div").height()
	var w = $("#gui-editor>div").width();
	
	function addInfo(m) {
		var p = document.createElement("pre");
		p.innerText = m;
		info.appendChild(p);
		footer.scrollTop = footer.scrollHeight;
		return p;
	}
	
	function addWarn(m) {
		addInfo(m).classList.add("warn");
	}
	
	function addError(m) {
		addInfo(m).classList.add("error");
	}
	
	function addSucc(m) {
		addInfo(m).classList.add("succ");
	}
	
	if(ws) ws.close();
	
	ws = new WebSocket(PF('meep-server').jq.val());
	ws.binaryType = 'arraybuffer';
	
	ws.onopen = () => {
		var lines = editor.getSession().getDocument().getAllLines();
		for(var i in lines) {
			ws.send(lines[i]);
		}
		ws.send("(exit)");
	}
	
	ws.onmessage = e => {
		var m = e.data;
		if(typeof(m)=="string") {
			if(m.startsWith("info:meep> ")) {
				addInfo(m.substring(11,m.length))
			} else if(m.startsWith("info:")) {
				addInfo(m.substring(5,m.length))
			} else if(m.startsWith("error:")) {
				addError(m.substring(6,m.length))
			} else if(m.startsWith("succ:")) {
				addSucc(m.substring(5,m.length))
			} else if(m.startsWith("warn:")) {
				addWarn(m.substring(5,m.length))
			} else if(m == "end") {
				ws.close();
			} else {
				console.log(e);
			}
		} else {
			var byteArray = new Uint8Array(m);
			
			var canvas = document.createElement('canvas');
			canvas.height = h;
			canvas.width = w;
			var ctx = canvas.getContext('2d');
			var imageData = ctx.getImageData(0, 0, w, h);
			l = imageData.data.length;
			for (var i = 0; i < l; i++) {
			    imageData.data[i] = byteArray[i];
			}
			ctx.putImageData(imageData, 0, 0);
			gui.appendChild(canvas);
		}
	}
	ws.onclose = () => {
		
	}
	ws.onerror = e => {
		addError("Conection to meep server failed");
		console.error(e);
	}
}

var animate = ()=>{
	var a = $(".opaque");
	if(a.length) {
		a.toggleClass("opaque");
		a = a.next();
	} else {
		a = $("#gui-editor>canvas").first();
	}
	if(a.length) {
		a.toggleClass("opaque");
	}
	setTimeout(animate,speed);
}

animate();

function editorKeydown(e) {
	if(e.keyCode == 90) {
		if(e.ctrlKey) {
			if(e.shiftKey) {
				editor.redo();
			} else {
				editor.undo();
			}
		}
	} if(e.keyCode == 46) {
		var remotions = $(".selected").map(function(a,c){
			return {
			        start:parseInt(c.getAttribute("start")),
			        end:parseInt(c.getAttribute("end"))
			};
		}).get();
		var belongs = function(c){
			for(var i = 0; i<remotions.length;i++){
				var range = remotions[i];
				if(range.start<= c && c < range.end) {
					return false;
				}
			}
			return true;
		}
		var text = editor.getValue();
		var result = "";
		for(var i = 0; i< text.length; i++) {
			if(belongs(i)) {
				result+=text[i];
			}
		}
		editor.setValue(result);
	}
}

function handleFileSelect(evt) {
	var file = evt.target.files[0];

	var reader = new FileReader();

	reader.onload = (e)=>{
		editor.setValue(reader.result);
	}

	reader.readAsText(file);
}