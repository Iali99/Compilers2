package cool;

public class GlobalData{

	// Type constants
    public static class Const {
        public static final String ROOT_TYPE = "Object";
        public static final String IO_TYPE = "IO";
        public static final String INT_TYPE = "Int";
        public static final String BOOL_TYPE = "Bool";
        public static final String STRING_TYPE = "String";
        public static final String MAIN_TYPE = "Main";
    }

	// ClassTable - maps class name to class parent
	public static HashMap<String, String> classTable;

	// gives error message to the user
	public void GiveError(String error,int lineNo){
		System.out.Println("Error: " + Integer.toString(lineNo) + " " + error);
	}
}

