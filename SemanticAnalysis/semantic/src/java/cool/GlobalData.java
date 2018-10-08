package cool;
import java.util.*;
public class GlobalData{

	// Type constants
    public static class Const {
        public static final String ROOT_TYPE = "Object";
        public static final String IO_TYPE = "IO";
        public static final String INT_TYPE = "Int";
        public static final String BOOL_TYPE = "Bool";
        public static final String STRING_TYPE = "String";
        public static final String MAIN_TYPE = "Main";
        // public static final bool is_standard(String name){
        //     if(name.equals(IO_TYPE)) return true;
        //     if(name.equals(STRING_TYPE)) return true;
        //     if(name.equals(BOOL_TYPE)) return true;
        //     if(name.equals(INT_TYPE)) return true;
        //     return false;
        // }
    }

    // ROOT class Object
    public static AST.class_ ROOT_CLASS = new AST.class_(Const.ROOT_TYPE, "", Const.ROOT_TYPE, new List<feature>, 0); 
    public static AST.class_ IO_CLASS   = new AST.class_(Const.IO_TYPE, "", Const.ROOT_TYPE, new List<feature>, 0); 
    public static AST.class_ INT_CLASS = new AST.class_(Const.INT_TYPE, "", Const.ROOT_TYPE, new List<feature>, 0); 
    public static AST.class_ BOOL_CLASS = new AST.class_(Const.BOOL_TYPE, "", Const.ROOT_TYPE, new List<feature>, 0); 
    public static AST.class_ STRING_CLASS = new AST.class_(Const.STRING_TYPE, "", Const.ROOT_TYPE, new List<feature>, 0); 

	// ClassTable - maps class name to class parent
    public static ScopeTable<String> attrScopeTable;
    public static ScopeTable<String> methodScopeTable;
	public static HashMap<String, String> classTable;
    public static InheritanceGraph inheritanceGraph;

	// gives error message to the user
	public static void GiveError(String error,int lineNo){
		System.out.println("Error: at line no. " + Integer.toString(lineNo) + " : " + error);
	}
}
