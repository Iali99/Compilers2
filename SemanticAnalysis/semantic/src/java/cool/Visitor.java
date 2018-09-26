package cool;

public class Visitor{
	public void visitorProgram(AST.program p){
		List<AST.class_> classes = p.classes;
		
		// init classTable
		GlobalData.classTable = new HashMap<String, String>;

		// go to each class and add it to classTable
		for(AST.class_ iter : classes){
			if(GlobalData.classTable.containsKey(iter.name)){
				GlobalData.GiveError("class redefined : " + iter.name, iter.lineNo);
			}
			else
				GlobalData.classTable.put(iter.name, "Object");
		}

		// iterate over each class and check for cycles
		for(AST.class_ iter : classes){
			String parent = iter.parent;
			
			if(parent == Object){
				continue;
			}
			// check existence of parent
			if(!GlobalData.classTable.containsKey(parent)){
				GlobalData.GiveError("class does not exist : " + parent, iter.lineNo);
			}
			else{
				// check for loops
				String grandparent = GlobalData.classTable.get(parent);
				while(grandparent != Object){
					if(grandparent == iter.name){
						GlobalData.GiveError("cycles detected", iter.lineNo);
						return;
					}
					grandparent = GlobalData.classTable.get(grandparent);
				}
				GlobalData.classTable.put(iter.name, parent);
			}

		}


	}
}