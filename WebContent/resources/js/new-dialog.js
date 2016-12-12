function newCtl(){
	var type = PF('a').getSelectedValue();
	var radius = parseFloat(PF('b').getJQ().val());
	var db = parseFloat(PF('c').getJQ().val());
	var dh = parseFloat(PF('d').getJQ().val());
	var lattice = parseFloat(PF('e').getJQ().val());
	var rows = parseInt(PF('f').getJQ().val()); 
	var columns = parseInt(PF('g').getJQ().val()); 
	var resolution = parseFloat(PF('h').getJQ().val()); 
	var pml = parseFloat(PF('m').getJQ().val());
	
	var latticeWidth = rows * lattice + radius + lattice;
	var latticeHeight = columns * lattice + radius + lattice;
	
	var text = "(reset-meep)\n\n";
	text += "(set! geometry-lattice (make lattice "+"(size " + (latticeWidth+pml+(resolution*0.25)) + " "+ (latticeHeight+pml+(resolution*0.25)) + " no-size" + ")"+"))\n\n";
	text += "(define-param a " + lattice + ")\n";
	text += "(define-param r " + radius + ")\n\n";
	text += "(define-param db " + db + ")\n";
	text += "(define-param dh " + dh + ")\n\n";
	
	text += "(set! geometry (list\n"+
            "	(make block (center 0 0) (size "+latticeWidth + " " + latticeHeight+" infinity)\n"+ 
            "		(material (make dielectric (epsilon "+db+"))) )\n\n";

	
	var startx = latticeWidth/2 - lattice;
	var starty = latticeHeight/2 - lattice;
	if(type === "0") {
		for(var y = 0; y<columns;y++){
			for(var x = 0; x<rows; x++){
				text+="(make cylinder (center (- "+startx+" (* a "+x+")) (- "+starty+" (* a "+y+"))) (radius r) (height infinity) (material (make dielectric (epsilon dh))))\n";
			}
		}	
	} else {
		for(var y = 0; y<columns;y++){
			var posx = -1*startx + ((y%2==1)?lattice/2:0);
			for(var x = y%2; x<rows; x++){
				text+="(make cylinder (center "+posx+" (- "+starty+"(* a "+y+"))) (radius r) (height infinity) (material (make dielectric (epsilon dh))))\n";
				posx+=lattice;
			}
		}
	}

	text+="))\n\n"
		
	text+="(define-param fr 0.564516129)\n"+ 
	"(define-param df 0.3)\n"+
	"(define-param nfreq 1000)\n" 
	"(define-param w 1)\n\n";
	
	text+="(set! sources (list\n"+
    "(make source\n"+
    "    (src (make gaussian-src (frequency fr) (fwidth df) )) (component Ez)\n"+
    "    (center -15 2.625 ) (size 0 0.875 0))\n"+
    "(make source\n"+
    "    (src (make gaussian-src (frequency fr) (fwidth df) )) (component Ez)\n"+
    "    (center -15 -6.125 ) (size 0 0.875 0))\n"+
    "))\n\n";
	
	text+="(set! pml-layers (list (make pml (thickness "+pml+"))))\n";
	text+="(set! resolution "+resolution+")\n";
	
	text+="(define x ; transmitted flux in x\n"+                                          
		  " (add-flux fr df nfreq\n\n"+
          " (make flux-region\n"+
          " (center 16 6.125)\n"+
          "	(size 0.0 1 0.0)\n"+
          " (direction X)\n"+
          "	)\n"+
          " )\n"+
          ")\n\n";

	text+="(define y ; transmitted flux in y\n"+                                          
	      " (add-flux fr df nfreq\n\n"+
	      " (make flux-region\n"+
	      " (center 16 -0.875)\n"+
	      "	(size 0.0 1 0.0)\n"+
	      " (direction X)\n"+
	      "	)\n"+
	      " )\n"+
	      ")\n\n";
	
	text+="(run-sources+ 500\n"+
          "		(at-beginning output-epsilon)\n"+
          "		(at-end output-efield-z)\n"+
          ")\n\n";
	text+="(display-fluxes x y)";
	editor.setValue(text);
	PF("new-dialog").hide();
}


function createObjetct(){
	PF("new-dialog").hide();
	setTimeout(function(){
		var newSimulation = {
				type : PF('a').getSelectedValue(),
				radius : parseFloat(PF('b').getJQ().val()),
				db : parseFloat(PF('c').getJQ().val()),
				dh : parseFloat(PF('d').getJQ().val()),
				lattice : parseFloat(PF('e').getJQ().val()),
				rows : parseInt(PF('f').getJQ().val()), 
				columns : parseInt(PF('g').getJQ().val()), 
				resolution : parseFloat(PF('h').getJQ().val()), 
				pml : PF('m').getJQ().val(), 
		};
	
		function calcBlockSizeWidth(obj){
			return obj.rows*obj.lattice + obj.radius;
		}
	
		function calcBlockSizeHeight(obj){
			return obj.columns*obj.lattice + obj.radius;
		}
	
		
		CtlObject.clearCache();
		simulation = new Simulation();
		loadSimulation(simulation);
			
		var l = new Lattice(simulation); 
		l.size = new Vector3((calcBlockSizeWidth(newSimulation)+ newSimulation.lattice),(calcBlockSizeHeight(newSimulation)+ newSimulation.lattice),new NoSize()); 
		simulation.params["geometry-lattice"] = l;
	
		var list = new CtlList(simulation);
	
		var b = new Block(simulation);
		b.center.x = 0;
		b.center.y = 0;
		b.size = new Vector3(calcBlockSizeWidth(newSimulation),calcBlockSizeHeight(newSimulation),Infinity);
		b.material = new Medium(simulation);
		b.material.epsilon = newSimulation.db;
		list.add(b);
	
		var startx = (-b.size.x/2) + newSimulation.lattice/2;
		var starty = (-b.size.y/2) + newSimulation.lattice/2;
		if(newSimulation.type === "0") {
			var posy = starty;
			for(var y = 0; y<newSimulation.columns;y++){
				var posx = startx;
				for(var x = 0; x<newSimulation.rows; x++){
					var c = new Cylinder(simulation);
					c.radius = newSimulation.radius;
					c.center = new Vector3(posx,posy,null);
					c.height = Infinity;
					c.material = new Medium(simulation);
					c.material.epsilon = newSimulation.dh;
					list.add(c);
					posx+=newSimulation.lattice;
				}
				posy+=newSimulation.lattice;
			}	
		} else {
			var posy = starty;
			for(var y = 0; y<newSimulation.columns;y++){
				var posx = startx + ((y%2==1)?newSimulation.lattice/2:0);
				for(var x = y%2; x<newSimulation.rows; x++){
					var c = new Cylinder(simulation);
					c.radius = newSimulation.radius;
					c.center = new Vector3(posx,posy,Infinity);
					c.height = Infinity;
					c.material = new Medium(simulation);
					c.material.epsilon = newSimulation.dh;
					list.add(c);
					posx+=newSimulation.lattice;
				}
				posy+=newSimulation.lattice;
			}
		}
	
		simulation.params.geometry = list;
		simulation.params.sources = new CtlList(simulation);
		
		var p = new Pml(simulation);
		p.thickness = newSimulation.pml;
		var list_pml = new CtlList(simulation);
		list_pml.add(p);
		simulation.params["pml-layers"] = list_pml;
		
		
		simulation.params.resolution = newSimulation.resolution;
		
		PF("simulate-button").enable();
		clear();
	},0);
}