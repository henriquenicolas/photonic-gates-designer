function newAnalysis() {
	var v = PF('ctl').jq.val();
	var r = $("#region");
	r.empty();
	resolution = 20;
	var element = ctlParser.parse(v);
	if(element==undefined) {
		element = ctlParser.parse(v);
	}
	r.append(element);
	PF('analysisDialog').show();
}

function clickCylinder(c,e) {
	e.stopPropagation();
	if(e.shiftKey) {
		this.cylinder.JQ.addClass("selected");
		var grid = $(".cylinder.selected");
		if(grid.length > 1){
			var g = this.cylinder.JQ;
			var minx = parseInt(g.css("left"));
			var miny = parseInt(g.css("top"));
			var maxx = minx;
			var maxy = miny;
			grid.each(function(){
				var g = $(this);
				var x = parseInt(g.css("left"));
				var y = parseInt(g.css("top"));
				if(x < minx) minx = x;
				if(y < miny) miny = y;
				if(x > maxx) maxx = x;
				if(y > maxy) maxy = y;
			});
			grid = $(".cylinder");
			grid.each(function(){
				var g = $(this);
				var x = parseInt(g.css("left"));
				var y = parseInt(g.css("top"));
				g.removeClass("selected");
				if(x >= minx && x <= maxx && y >= miny && y <= maxy) g.addClass("selected");
			});
		}
	} else if(e.ctrlKey) {
		if(c.classList.contains("selected")) {
			c.classList.remove("selected");
		} else {
			c.classList.add("selected");
		}
	} else {
		$(".selected").removeClass("selected");
		c.classList.add("selected");
	}
	return false;
}

function clickBlock(c,e) {
	e.stopPropagation();
}

function clickLattice(c,e) {
	e.stopPropagation();
}