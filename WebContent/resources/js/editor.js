resolution = 10;
editor = null;

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
});

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