package cool;

public class GlobalData{
  public static class Const {
	public static final String ROOT_TYPE = "Object";
	public static final String IO_TYPE = "IO";
	public static final String INT_TYPE = "Int";
	public static final String BOOL_TYPE = "Bool";
	public static final String STRING_TYPE = "String";
	public static final String MAIN_TYPE = "Main";
  }

  public static AST.class_ ROOT_CLASS = new AST.class_(Const.ROOT_TYPE, "", Const.ROOT_TYPE, getRootFeatures(), 0);
  public static AST.class_ IO_CLASS   = new AST.class_(Const.IO_TYPE, "", Const.ROOT_TYPE, getIOFeatures(), 0);
  public static AST.class_ INT_CLASS = new AST.class_(Const.INT_TYPE, "", Const.ROOT_TYPE, getIntFeatures(), 0);
  public static AST.class_ BOOL_CLASS = new AST.class_(Const.BOOL_TYPE, "", Const.ROOT_TYPE, getBoolFeatures(), 0);
  public static AST.class_ STRING_CLASS = new AST.class_(Const.STRING_TYPE, "", Const.ROOT_TYPE, getStringFeatures(), 0);

  // features of ROOT_CLASS
  public static ArrayList<AST.feature> getRootFeatures(){
    ArrayList<AST.feature> featureList = new ArrayList<AST.feature>();
    featureList.add(new AST.method("abort", new ArrayList<>(), Const.ROOT_TYPE, null, 0));
    featureList.add(new AST.method("type_name", new ArrayList<>(), Const.STRING_TYPE, null, 0));
    featureList.add(new AST.method("copy", new ArrayList<>(), Const.ROOT_TYPE, null, 0));
    return featureList;
  }
  // features of STRING_CLASS
  public static ArrayList<AST.feature> getStringFeatures(){
    ArrayList<AST.feature> featureList = new ArrayList<AST.feature>();

    List<AST.formal> stringFormalList = new ArrayList<>(Arrays.asList(new AST.formal("x", Const.STRING_TYPE, 0)));
    List<AST.formal> intFormalList = new ArrayList<>(Arrays.asList(new AST.formal("x", Const.INT_TYPE, 0)
        ,new AST.formal("y", Const.INT_TYPE, 0)));

    featureList.add(new AST.method("length", new ArrayList<>(), Const.INT_TYPE, null, 0));
    featureList.add(new AST.method("concat", stringFormalList, Const.STRING_TYPE, null, 0));
    featureList.add(new AST.method("substr", intFormalList, Const.STRING_TYPE, null, 0));

    return featureList;
  }
  // features of BOOL_CLASS
  public static ArrayList<AST.feature> getBoolFeatures(){
    ArrayList<AST.feature> featureList = new ArrayList<AST.feature>();

    return featureList;
  }
  // features of INT_CLASS
  public static ArrayList<AST.feature> getIntFeatures(){
    ArrayList<AST.feature> featureList = new ArrayList<AST.feature>();

    return featureList;
  }
  // features of IO_CLASS
  public static ArrayList<AST.feature> getIOFeatures(){
    ArrayList<AST.feature> featureList = new ArrayList<AST.feature>();

    List<AST.formal> stringFormalList = new ArrayList<>(Arrays.asList(new AST.formal("x", Const.STRING_TYPE, 0)));
    List<AST.formal> intFormalList = new ArrayList<>(Arrays.asList(new AST.formal("x", Const.INT_TYPE, 0)));

    featureList.add(new AST.method("out_string", stringFormalList, Const.IO_TYPE, null, 0));
    featureList.add(new AST.method("out_int", intFormalList, Const.IO_TYPE, null, 0));
    featureList.add(new AST.method("in_string", new ArrayList<>(), Const.STRING_TYPE, null, 0));
    featureList.add(new AST.method("in_int", new ArrayList<>(), Const.INT_TYPE, null, 0));

    return featureList;
  }

  // ClassTable - maps class name to class parent
  public static HashMap<String, String> classTable;
  // inheritance graph 
  public static InheritanceGraph inheritanceGraph;
  //Stores the IR
  public static PrintWriter out;
  //Counter for register variables
  public static int Counter;
  // map from string constant to register
  public static Map<String,int> strConsToRegister;
  //Counter for Global string registers.
  public static int strCounter;
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
  static {
  		strCounter = 0;
  		strConsToRegister = new Map<String, int>();
        classTable = new HashMap<String, String>();
    }

}
