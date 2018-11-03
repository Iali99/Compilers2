package cool;

public class GlobalData{

  //Stores the IR
  public static PrintWriter out;
  //Counter for register variables
  public static int Counter;
  // map from string constant to register
  public static Map<String,String> strConsToRegister;

  //Hashmap to store class size vs class name.
  public static HashMap<String,Integer> classNameToSize;
  //Function to get the mangled name of a function.
  public static String mangledName(String classname, AST.method m)
  {
    StringBuilder mangledName = new StringBuilder();
    mangledName.append(classname).append("$");
    mangledName.append(m.name).append("$");
    return mangledName.toString();
  }

  public static void addStringsAsGlobal(){
    GlobalData.out.println("");
    StringBuilder builder = new StringBuilder("");
    Iterator it = GlobalData.strConsToRegister.entrySet().iterator();
    while (it.hasNext()) {
        Map.Entry pair = (Map.Entry)it.next();
        builder.append("@globalstring").append(pair.getValue())
        .append(" = private unnamed_addr constant [").append(pair.getKey().length() + 1)
        .append(" x i8] c\"").append(pair.getKey()).append("\\00\", align 1\n");
    }
    GlobalData.out.println(builder.toString());
  }

}
