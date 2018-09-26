package cool;

public class Visitor{
	public void visitorProgram(AST.program p){
		List<AST.class_> classes = p.classes;
		
		// go to each class and add it to classTable
		for(AST.class_ iter : classes){
			if(classTable.containsKey(iter.name)){
				giveError("class redefined : " + iter.name, iter.lineNo);
			}
			else
				classTable.put(iter.name, "Object");
		}

		// iterate over each class and check for cycles
		for(AST.class_ iter : classes){

		}


	}
}