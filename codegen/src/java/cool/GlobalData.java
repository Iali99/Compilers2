package cool;

public class GlobalData{
  public static class Const {
	public static final String ROOT_TYPE = "Object";
	public static final String IO_TYPE = "IO";
	public static final String INT_TYPE = "Int";
	public static final String BOOL_TYPE = "Bool";
	public static final String STRING_TYPE = "String";
	public static final String MAIN_TYPE = "Main";
	public static final String ZERO_ERROR = "\nError : Division by 0 is not permissible";
    public static final String ZERO_FUNCTION = "reportError0";
    public static final String VOID_ERROR = "\nError : Dispatch on void is not permissible";
    public static final String VOID_FUNCTION = "reportErrorVoid";
    public static final String ABORT = "\nAbort called from class ";
	public static final boolean is_structable(String name){
		if(name.equals(INT_TYPE)) return false;
        if(name.equals(STRING_TYPE)) return false;
        if(name.equals(BOOL_TYPE)) return false;
        return true;
	}
	public static final boolean is_standard(String name){
        if(name.equals(IO_TYPE)) return true;
        if(name.equals(STRING_TYPE)) return true;
        if(name.equals(BOOL_TYPE)) return true;
        if(name.equals(INT_TYPE)) return true;
        return false;
    }
    public static final boolean is_standard_class_name(String name){
        if(name.equals(ROOT_TYPE)) return true;
        if(name.equals(IO_TYPE)) return true;
        if(name.equals(STRING_TYPE)) return true;
        if(name.equals(BOOL_TYPE)) return true;
        if(name.equals(INT_TYPE)) return true;
        return false;
    }
    public static final boolean is_inheritable(String name){
        if(name.equals(MAIN_TYPE)) return false;
        if(name.equals(STRING_TYPE)) return false;
        if(name.equals(BOOL_TYPE)) return false;
        if(name.equals(INT_TYPE)) return false;
        return true;
    }
    public static final boolean is_inheritable_standard(String name){
      if(name.equals(ROOT_TYPE)) return true;
      if(name.equals(IO_TYPE)) return true;
      return false;
    }
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
  // default values
  public static String getDefaultValue(String type) {
        if(GlobalData.Constants.INT_TYPE.equals(type)) {
            return "0";
        } else if(GlobalData.Constants.BOOL_TYPE.equals(type)) {
            return "0";
        } else if(GlobalData.Constants.STRING_TYPE.equals(type)) {
            return IRInstructions.addGEPInstruction("");
        } else {
            return "undef";
        }
  }

  // makes struct name for class
  public static String makeStructName(String class){
  	return "%class." + class;
  }

  // ClassTable - maps class name to class parent
  public static HashMap<String, String> classTable;
  // maps a class to a list of it's variables
  // public static Hashmap<String, ArrayList<String>>
  // inheritance graph
  public static InheritanceGraph inheritanceGraph;
  //Stores the IR
  public static PrintWriter out;
  //Counter for register variables
  public static int Counter;
  // counter for loop
  public static int loopCounter;
  // counter for if else
  public static int ifCounter;
  // map from string constant to register
  public static Map<String, int> strConsToRegister;
  //Counter for Global string registers.
  public static int strCounter;
  // attribute mangled name with class to index map
  public static Hashmap<String, int> attrIndexMap;
  // variable name -> variable return type
  public static ScopeTable<String> attrScopeTable;
  // function name -> FunctionTypeMangledName
  public static ScopeTable<String> methodScopeTable;
  //Hashmap to store class size vs class name.
  public static HashMap<String,Integer> classNameToSize;
  //list to store mangled names of formals of methods.
  public static List<String> formalsMangledList;
  // makes address name for a name
  public static String makeAddressName(String name){
  	return "%"+name+".a_";
  }

  // given a class makes it's IR type or pointer
  public static String makeClassTypeOrPointer(String cl) {
    // same as getBasicType but the last one has an extra *
    if("i64".equals(cl)) {
        return "i64";
    } else if("i1".equals(cl)) {
        return "i1";
    } else if(GlobalData.Const.STRING_TYPE.equals(cl)) {
        return "i8*";
    } else if(GlobalData.Const.INT_TYPE.equals(cl)) {
        return "i32";
    } else if(GlobalData.Const.BOOL_TYPE.equals(cl))
        return "i8";
    return makeStructName(cl) + "*";
  }

  //Function to get the mangled name of a function.
  public static String mangledName(String classname, AST.method m)
  {
    StringBuilder mangledName = new StringBuilder();
    mangledName.append(classname).append("$");
    mangledName.append(m.name).append("$");
    return mangledName.toString();
  }
  //Function to get the mangled name of a function name.
  public static String mangledName(String classname, String m)
  {
    StringBuilder mangledName = new StringBuilder();
    mangledName.append(classname).append("$");
    mangledName.append(m).append("$");
    return mangledName.toString();
  }

  //function to get mangled name of a formal.
  public static String mangledFormalName(String class,String method,String formal){
    StringBuilder mangledName = new StringBuilder();
    mangledName.append(class).append("$");
    mangledName.append(method).append("$").append(formal).append("$");
    return mangledName.toStrign();
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
  		loopCounter = 0;
      ifCounter = 0;
  		attrIndexMap = new Hashmap<String, int>();
  		strConsToRegister = new Map<String, int>();
        classTable = new HashMap<String, String>();
        attrScopeTable = new ScopeTable<String>();
  		methodScopeTable = new ScopeTable<String>();
      formalsMangledList = new ArrayList<String>();
    }

}
