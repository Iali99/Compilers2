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
        public static final bool is_standard(string name){
            if(name.equals(IO_TYPE)) return true;
            if(name.equals(STRING_TYPE)) return true;
            if(name.equals(BOOL_TYPE)) return true;
            if(name.equals(INT_TYPE)) return true;
            return false;
        }
    }

	// ClassTable - maps class name to class parent
	public static HashMap<String, String> classTable;
  public static InheritanceGraph inheritanceGraph;

	// gives error message to the user
	public static void GiveError(String error,int lineNo){
		System.out.println("Error: at line no. " + Integer.toString(lineNo) + " : " + error);
	}
}
