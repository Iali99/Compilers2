package cool;

public class GlobalData{
	// ClassTable - maps class name to class parent
	public static HashMap<String, String> classTable;

	// gives error message to the user
	public void GiveError(String error,int lineNo){
		System.out.Println("Error: " + Integer.toString(lineNo) + " " + error);
	}
}

