var currentFilterId = 0;
var currentFilterX;
var baseFilterX;
var graphAjax;

function addFilterLineX() {
        var filterLine = document.all.filterLineEmpty.innerHTML;
        filterLine = filterLine.replace(/__FILTER_ID__/g, currentFilterId);
        var newDiv = document.createElement("div");
        newDiv.id = "wholeFilterLine" + currentFilterId;
        newDiv.style.clear = "both";
        newDiv.innerHTML = filterLine;
        document.all.filterContent.appendChild(newDiv);
        currentFilterId++;
}

function setFilterContent(id) {
		
        var selectedFilter =  document.getElementById("filterSelect"+id).value;
        if (selectedFilter != 'delete') {
                var filterLine = document.getElementById(selectedFilter+"Empty").innerHTML;
                filterLine = filterLine.replace(/__FILTER_ID__/g, id);
                document.getElementById("filterContent" + id).innerHTML = filterLine;
        } else {
                var filterLine = document.getElementById("wholeFilterLine" + id);
                filterLine.parentNode.removeChild(filterLine);
        }
        document.getElementById("filter_anzahl").value = currentFilterId;
} 

function addFilterOnStart(count,wert,op,val ){
	addFilterLineX(); 
	for (var i=0; i < document.getElementById("filterSelect"+count).length; i++){ 
	//document.write(wert);
	 if (document.getElementById("filterSelect"+count).options[i].value == wert) {
 		document.getElementById("filterSelect"+count).options[i].selected = true;
 		} else {
 		document.getElementById("filterSelect"+count).options[i].selected = false;
 		}
 	} 
	//document.getElementById("filterSelect0").selectedIndex = 2
	setFilterContent(count)
	//Kategorie
	if ( wert=='category')
	{

	if (op=='eq')
	{
	//document.write("then");
	document.getElementById("categoryOp"+count).checked = true
	}
	else
	{
	//document.write("else");
	document.getElementById("categoryOp"+count+"_0").checked = true
	}
	for (var i=0; i < document.getElementById("categoryId"+count).length; i++){ 
	//document.write(wert);
	 if (document.getElementById("categoryId"+count).options[i].value == val) {
 		document.getElementById("categoryId"+count).options[i].selected = true;
 		} else {
 		document.getElementById("categoryId"+count).options[i].selected = false;
 		}
 	} 
	}//Kategorie
	//Name
	if ( wert=='title')
	{
	for (var i=0; i < document.getElementById("titleOperator"+count).length; i++){ 
	//document.write(wert);
	 if (document.getElementById("titleOperator"+count).options[i].value == op) {
 		document.getElementById("titleOperator"+count).options[i].selected = true;
 		} else {
 		document.getElementById("titleOperator"+count).options[i].selected = false;
 		}
 	} 
 	document.getElementById("title"+count).value =val;
	}//Name
	//Konto
	if ( wert=='konto')
	{
	
	if (op=='eq')
	{
	document.getElementById("kontoOp"+count).checked = true
	}
	else
	{
	document.getElementById("kontoOp"+count+"_0").checked = true
	}
	for (var i=0; i < document.getElementById("kontoId"+count).length; i++){ 
	//document.write(wert);
	 if (document.getElementById("kontoId"+count).options[i].value == val) {
 		document.getElementById("kontoId"+count).options[i].selected = true;
 		} else {
 		document.getElementById("kontoId"+count).options[i].selected = false;
 		}
 	} 
	}//Konto
	//Regel
	if ( wert=='rule')
	{
	
	if (op=='eq')
	{
	document.getElementById("ruleOp"+count).checked = true
	}
	else
	{
	document.getElementById("ruleOp"+count+"_0").checked = true
	}
	for (var i=0; i < document.getElementById("ruleId"+count).length; i++){ 
	//document.write(wert);
	 if (document.getElementById("ruleId"+count).options[i].value == val) {
 		document.getElementById("ruleId"+count).options[i].selected = true;
 		} else {
 		document.getElementById("ruleId"+count).options[i].selected = false;
 		}
 	} 
	}//Regel
	//Betrag
	if ( wert=='betrag')
	{
	//document.write(wert);
	for (var i=0; i < document.getElementById("betragOperator"+count).length; i++){ 
	//document.write(op);
	 if (document.getElementById("betragOperator"+count).options[i].value == op) {
 		document.getElementById("betragOperator"+count).options[i].selected = true;
 		} else {
 		document.getElementById("betragOperator"+count).options[i].selected = false;
 		}
 	} 
 	document.getElementById("betrag"+count).value =val;
	}//Name
}