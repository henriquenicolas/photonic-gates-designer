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
    "    (center "+(-((rows/2)+lattice))+" 2 ) (size 0 0.875 0))\n"+
    "(make source\n"+
    "    (src (make gaussian-src (frequency fr) (fwidth df) )) (component Ez)\n"+
    "    (center "+(-((rows/2)+lattice))+" -2 ) (size 0 0.875 0))\n"+
    ")\n" +
    ");end of sources list\n\n";
	
	text+="(set! pml-layers (list (make pml (thickness "+pml+"))))\n";
	text+="(set! resolution "+resolution+")\n";
	
	text+="(define x ; transmitted flux in x\n"+                                          
		  " (add-flux fr df nfreq\n\n"+
          " (make flux-region\n"+
          " (center "+(((rows/2)+lattice))+" 2)\n"+
          "	(size 0.0 1 0.0)\n"+
          " (direction X)\n"+
          "	)\n"+
          " )\n"+
          ")\n\n";

	text+="(define y ; transmitted flux in y\n"+                                          
	      " (add-flux fr df nfreq\n\n"+
	      " (make flux-region\n"+
	      " (center "+(((rows/2)+lattice))+" -2)\n"+
	      "	(size 0.0 1 0.0)\n"+
	      " (direction X)\n"+
	      "	)\n"+
	      " )\n"+
	      ")\n\n";
	
	text+="(run-sources+ 150\n"+
          "		(at-beginning output-epsilon)\n"+
          "		(at-end output-efield-z)\n"+
          ")\n\n";
	text+="(display-fluxes x y)";
	editor.setValue(text);
	PF("new-dialog").hide();
}