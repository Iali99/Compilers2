package cool;

public class GlobalData{

  //Stores the IR
  public static PrintWriter out;
  //Counter for register variables
  public static int Counter;

  public static String mangledName(String classname, AST.method m)
  {
    StringBuilder mangledName = new StringBuilder();
    mangledName.append(classname).append("$");
    mangledName.append(m.name).append("$");
    return mangledName.toString();
  }

}
