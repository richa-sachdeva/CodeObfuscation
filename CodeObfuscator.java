// Code Obfuscation - Master of Computer Application (MCA), Semester 5 - Jul 2011 - Dec 2011
// C-Dac, IP University, Delhi, India
// By Richa Sachdeva, Gagan Singh, Harsh Chiki

import javax.swing.*;
import java.awt.event.*;
import static java.lang.System.*;
import java.awt.*;
import javax.swing.border.*;
import java.io.*;
import java.util.*;
import java.util.regex.*;
import javax.swing.tree.*;
public class CodeObfuscatorVersion1
{
	public static void main(String... args)
	{
		EventQueue.invokeLater(new Runnable()
		{
			public void run()
			{
				TFrame tf1=new TFrame();
				tf1.setVisible(true);
				tf1.setDefaultCloseOperation(tf1.EXIT_ON_CLOSE);
			}
			
		});
	}
}
class TFrame extends JFrame
{
	private JTextArea jta1,jta2;
	private JEditorPane jep1;
	private JFileChooser jfc1;
	private String path;
	private Font f1;
	TFrame()
	{
		
		Toolkit kit=Toolkit.getDefaultToolkit();
		Dimension d=kit.getScreenSize();
		out.println(d.height);
		setSize(d);
		setLayout(null);
		
		JMenuBar jmb1=new JMenuBar();
		setJMenuBar(jmb1);
		Dimension d1=calculateSize(d);
		if(path==null)
		out.println("hello");
		
		JMenu file=new JMenu("File");
		jmb1.add(file);
		
		JMenuItem open_menu=new JMenuItem("Open",'O');
		file.add(open_menu);
		JMenuItem get_menu=new JMenuItem("get");
		file.add(get_menu);
				
		JPanel jp1=new JPanel();
		jp1.setLayout(new BorderLayout());
		Border b1=BorderFactory.createEtchedBorder();
		Border b2=BorderFactory.createTitledBorder(b1,"Original Code");
		jp1.setBorder(b2);
		add(jp1);
		jp1.setBounds(0,0,d1.width-10,d1.height-90);
		jp1.setBackground(Color.GRAY);
			
		final JPanel jp2=new JPanel();
		jp2.setLayout(new BorderLayout());
		b2=BorderFactory.createTitledBorder(b1,"Obfuscated Code");
		jp2.setBorder(b2);
		add(jp2);
		jp2.setBounds(d1.width,0,d1.width-10,d1.height-90);
		jp2.setBackground(Color.WHITE);
		
		f1=new Font("SansSerif",Font.PLAIN,15);
		
		jta1=new JTextArea();
		jta1.setFont(f1);
		jta1.setLineWrap(true);
		JScrollPane jsp1=new JScrollPane(jta1);
		jp1.add(jta1,BorderLayout.CENTER);		
	
		jta2=new JTextArea();
		jta2.setFont(f1);
		jta2.setLineWrap(true);
		JScrollPane jsp2=new JScrollPane(jta2);
		
		/* Adding Menu Items*/
		jfc1=new JFileChooser();
		jfc1.setCurrentDirectory(new File("."));
		
		open_menu.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				int result=jfc1.showOpenDialog(TFrame.this);
				if(result==jfc1.APPROVE_OPTION)
				path=jfc1.getSelectedFile().getPath();
			}
		});
		final DefaultMutableTreeNode root=new DefaultMutableTreeNode("program");	//general purpose node in a tree data structure
		DefaultTreeModel model=new DefaultTreeModel(root);
		JTree jt=new JTree(model);		//jtree to provide view of the data.
		//KeywordId kid=new KeywordId(list);
		jp2.add(jt,BorderLayout.CENTER);

		get_menu.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{	
				String textdata=jta1.getText();
				Separator sep=new Separator(textdata);
				ArrayList<String> list=(ArrayList<String>)sep.getProgramLines();
				//we need a separate thread to use this coz otherwise the application will become very slow.
				MakeTree mt=new MakeTree(list,root);
				//code to be edited          				DefaultTreeModel model=mt.getModel();
				
				jp2.setBackground(Color.GRAY);
				//FunctionIdentification fi1=new 				FunctionIdentification(list);						
			}
		});
	
	}
	public Dimension calculateSize(Dimension d)
	{
		return new Dimension(d.width/2,d.height);
	}
}

class Separator
{
private ArrayList<String> list;
private String tosplit;
	Separator(String testString)
	{
		tosplit=testString;
		
		splitString(tosplit);
	}
	public ArrayList splitString(String textdata)
	{ 
		Scanner in=new Scanner(textdata);
		
		in.useDelimiter(Pattern.compile("\n"));
		list=new ArrayList<String>();
		
		while(in.hasNext())
		{
			list.add(in.next());
		}
		
		ListIterator li=list.listIterator();
		
		return list;
	}
	public ArrayList getProgramLines()
	{
		return list;
	}
	
}

class MakeTree
{
	DefaultTreeModel theTreeModel;
	DefaultMutableTreeNode root;
	ArrayList<String> prog;
	DefineNode define;
	IncludeNode include;
	PragmaNode pragma;
	LineNode line;
	ErrorNode error;
	int rj;
	
	MakeTree(ArrayList<String> aprog,DefaultMutableTreeNode root)//calls makeRoot()
	{
		include=new IncludeNode("include");
		define=new DefineNode("Define");
		pragma = new PragmaNode("pragma");
		line = new LineNode("line");
		error = new ErrorNode("error");
		prog=aprog;
		
		//this will only add main root node and #include Section
		this.root=root;
		makeRoot();
				
	}
	void makeRoot()//                       make root calls getInclude()
	{
		
		HashNode preprocessor=new HashNode("#");
		root.add(preprocessor);
		preprocess(preprocessor);
		//DefaultMutableTreeNode include=new DefaultMutableTreeNode("include");
		//preprocessor.add(include);
		//addInInclude(include);
					
	}
	void preprocess(HashNode hash)	//this will iterate through all the strings till main() is encountered
	{		
		boolean foundmain=false;//to check if main has been found
		for(rj=0;rj<prog.size();rj++)
		{
			String g=prog.get(rj);
			if(g.charAt(0)=='#')
			{				
				//search for the type of preprocessor node
				if(g.indexOf("include")!=(-1))
				{
					//out.println(g.indexOf("include"));
					hash.add(include);
					addInInclude(include,g);
					
				}
				else if(g.indexOf("define")!=(-1))
				{
					hash.add(define);
					addInDefine(define,g);
				}
				else if(g.indexOf("pragma") != (-1))
				{
					hash.add(pragma);
					addInPragma(pragma,g);
					
				}
				else if (g.indexOf("line") != (-1))
				{
					hash.add(line);
					addInLine(line,g);
					
				}
				else if (g.indexOf("error") != (-1))
				{
					hash.add(error);
					addInError(error,g);
					
				}
			}
			else // this is where we identify for func node , structnode and mainnode
			{
				//first break the string into tokens and we need only first token to identify if it's a  or union and rest will be used if it's not
				//it could be a function declaration or definition or declaration
				//finally we could have encountered main
					
				Scanner in=new Scanner(g);
				String token=in.next();
				
				if(token.equals("struct"))
				{
					
					StructNode st=new StructNode("struct");
					root.add(st);
					String checkstructname=new String(in.next());
					
					StructTree try1=new StructTree();
					try1.addStruct(st,rj);//passing structnode and the current index of ArrayList										
					
				}
				else if(!foundmain&&!token.equals("main()"))//this was the part that was causing the problems
				{
					Identify idobj=new Identify();
					idobj.id(rj);
					
				}
				else if(token.equals("main()")&&!foundmain)//changes made here 
				{
					//out.println("inside main");
					MainNode mn=new MainNode("MainNode");//mainnode being instantiated and makemainnode being called to make the mainnode
					root.add(mn);
					Obfuscation o=new Obfuscation(rj);
					MakeMainNode makeMain=new MakeMainNode(mn,rj);
					foundmain=true;
				}
			}
		}
			
	}
	class Obfuscation
	{
		KeywordId key;
		int index;
		Obfuscation(int rj)
		{
			index=rj;
			key=new KeywordId();
			obfuscate();
		}
		String hexRep(String s)
		{
			String obf="";
			obf=obf+s.charAt(0);
			for(int i=0;i<s.length();i++)
			{
				int c=s.charAt(i);
				String hexConvert=new String(String.format("%x",c));
				obf=obf+hexConvert;
			} 
			return obf;
		}
		void obfuscate()
		{
			
			String temp="";
			String g="";
			for(int i=index;i<prog.size();i++)
			{
				temp=" ";
				Scanner in=new Scanner(prog.get(i));
				while(in.hasNext())
				{
					g=in.next();
					if(!key.checkWhetherKeyword(g))                    // if not a keyword
					temp=temp+" "+hexRep(g);//call hexadecimal method for "g" temp=temp+obfuscate(in.next())
					else //if a keyword
					temp=temp+g;
				}
				out.println(temp);
			}
		}
	}
	class MakeMainNode
	{
		MakeMainNode(MainNode node,int index)
		{
			for(int i=index;i<prog.size();i++)
			{
				DataNode dn=new DataNode(prog.get(i));
				node.add(dn);
			}
		}
	}

class Parameter extends AbstractNode
{
	String name;
	String type;
	String value;
	
	public Parameter(){}
	public Parameter(Object o){super(o);}
	public String getName()
	{
		return name;
	}
	public void setName(String n)
	{
		name=n;
	}
	public void setType(String t)
	{
		type=t;
	}
	public String getType()
	{
		return type;
	}
	public String getValue()
	{
		return value;
	}
	public void setValue(String set)
	{
		value=set;
	}
}
	class Identify
	{
		void id(int index)
		{
			boolean found=false;//checks whether a func has been found
			//
			int equali=-1,bracei=-1;//this checks if '=' or '(' has been found , if so their index values are compared and we see what comes first telling us about 			//the variable
			String g=prog.get(index);
			//out.println(g);
  			if(g.indexOf("=")!=-1)
			{
				equali=g.indexOf("=");	
			}
			if(g.indexOf("(")!=-1)
			{
				bracei=g.indexOf("(");	
			}
			if(equali!=-1||bracei!=-1)
			{
				found=true;
			}
			if(equali<bracei||bracei==-1&&equali!=-1)//variable has been found
			{
				Scanner in=new Scanner(g);
				String temp=g;
				DataNode dn=new DataNode("Global Variable");
				root.add(dn);
				dn.add(new DataNode(g));
				
			}
		}
	}

	class StructTree
	{
		public void addStruct(StructNode st,int index)
		{
			
			Stack myStack=new Stack();
			boolean found=false;
			Scanner in;
			ArrayList<DataNode> bodyNode=st.getBodyNode();
			int i;
			
			for(i=index;i<4+index&&!found;i++)//can't be more than 3 lines long ,we search for '{',put it in a stack then copy the whole of the struct statements inalistof datanodes
			{
				//out.println(i);
				in=new Scanner(prog.get(i));
				while(in.hasNext())
				{
					String str=in.next();
					if(str.equals("{"))
					{
						
						found=true;
					}
					else
					{
						DataNode dn=new DataNode(str);
 						st.add(dn);
					}
				}
				
			}
//wat "found" signifies is that we found "{"  and we will use found so that once a complete struct is found the program doesn't keep on iteraating
			found=false;
			//out.println(i+"index");
			for(int j=i-1;j<prog.size()+(i-1)&&!found;j++)
			{
				int d;
				String str=prog.get(j);
				for(int k=0;k<str.length();k++)
				{
					if((d=str.indexOf("{"))!=-1)
					{
						myStack.push('{');
						DataNode dn=new DataNode(str.substring(d));
						st.add(dn);
						bodyNode.add(dn); //adding to the body node
						break;
					}
					else if((d=str.indexOf("}"))!=-1)
					{
						if(!myStack.empty())
						{
							if(((Character)myStack.peek())=='{')
							{
								myStack.pop();
								
							}
							if(myStack.empty())
							{ 
								st.add(new DataNode(str.substring(0)));/*this will check if the closing '}' is within the statement 												    itself*/
								//out.println(str.substring(0,d));
								
								found=true;
								break;
							} //so that the loop ends
							
							bodyNode.add(new DataNode(str.substring(0,d-1)));    //adding to the body node{turned out to be unnecessary}
						}
					}
					else
					{
						st.add(new DataNode(prog.get(j)));
						bodyNode.add(new DataNode(prog.get(j)));
						break;
					}
				}			

			}
		}
	}
	void addInDefine(DefineNode define,String g)
	{
		int i=g.indexOf("define");
		String s=g.substring(i+6);
		define.add(new DataNode(s));
	} 
	
	
	void addInInclude(IncludeNode include,String g)
	{
		
		String s=identify(g);
		DataNode n1=new DataNode(s);
		include.add(n1);
	
	}
	
	void addInPragma(PragmaNode include,String g)
	{
		int i = g.indexOf("pragma");
		String s= g.substring(i+6);
		include.add(new DataNode(s));
		
	}
	
	void addInLine(LineNode include, String g)
	{
		int i = g.indexOf("line");
		String s= g.substring(i+4);
		include.add(new DataNode(s));
	}
	
	void addInError(ErrorNode include, String g)
	{
		int i = g.indexOf("error");
		String s= g.substring(i+5);
		include.add(new DataNode(s));
	}
	String identify(String g)
	{
		int i;
		String s=null;	
		i=g.indexOf("include");
		s=g.substring(i+7);
		
		return s;
	}
	
}
//the make tree class is suitable for preprocessing only coz we need more string processing in other cases which will not include '#' so preprocess function creates object of the class below //and makes the rest of the tree

class StructNode extends AbstractNode
{
	private boolean type;//whether it's a new data type
	private ArrayList<DataNode> bodyNode;
	StructNode(Object o)
	{
		super(o);
		bodyNode=new ArrayList<DataNode>();
	}
	public ArrayList getBodyNode()
	{
		return bodyNode;
	}
	
}

//this is where we start defining all the nodes
class AbstractNode extends DefaultMutableTreeNode
{
	private int flag;
	public AbstractNode(){}
	public AbstractNode(Object o)
	{
		super(o);
	}
	public int getFlag()
	{
		return flag;
	}
	public void setFlag(int flag)
	{
		this.flag=flag;
	}

}

class DataNode extends AbstractNode
{
	private String data;
	public DataNode(){}
	public DataNode(Object o)
	{
		super(o);
	}
	public String getData()
	{
		return data;
	}
	public void setData(String s)
	{
		data=s;
	}
}

class Prog extends AbstractNode
{
	public Prog(){}
	public Prog(Object o){super(o);}
}

class HashNode extends AbstractNode
{
	public HashNode(){};
	public HashNode(Object o){super(o);}
}
class IncludeNode extends AbstractNode
{
	public IncludeNode(){}
	public IncludeNode(Object o){super(o);}
}

class DefineNode extends AbstractNode
{
	
	DefineNode(Object o)
	{
		super(o);
	}
}

class PragmaNode extends AbstractNode
{
	
	PragmaNode( Object o)
	{
		super(o);
	}
}

class LineNode extends AbstractNode
{
	LineNode( Object o)
	{
		super (o);
	}
}

class ErrorNode extends AbstractNode
{
	ErrorNode (Object o)
	{
		super (o);
	}
}

class KeywordId
{
	private HashSet<String> keyword;//this is a hashset which contains keywords and hopefully the constants and functions

	KeywordId()
	{
		keyword =new HashSet<String>();
		addKeywords();
	}
	public void addKeywords()
	{	
		try
		{
			Scanner in=new Scanner(new File("keyword.txt"));
			while(in.hasNext())
			{
				//out.println("hello");
				keyword.add(in.next());
			}
			//out.println(keyword); this merely prints out the keywords from a file in the c-language
		}
		catch(FileNotFoundException e)
		{
			out.println("file not found");
		}
	}
	public boolean checkWhetherKeyword(String x)   //this function is needed to return whether the word being checked is a keyword or not
	{
		boolean flag;
		if(Character.isDigit(x.charAt(0)))
		flag=true;
		else
		flag= keyword.contains(x);
		return flag;
	}
}
/*1.  this class checks whether the function is userdefined or has been taken from a library ,example printf would be returned as false while xyz() as true . It should take the strings from the arraylist and check for functions . If a function is found it is added to hashset and then using regular expressions we see whether it is user defined or not
2. It is temporarily being called by the Innerclass TFrame$2
3. It takes the parameters arraylist of strings and a keywordId object as parameter*/
class FunctionIdentification 
{
	private HashSet<String> allfunctions,userdefined;	
	private StringBuilder str;
	private KeywordId k1;
	private Stack myStack;
	private Character left,right,bracket,quote;
	
	FunctionIdentification(ArrayList<String> list)
	{
		left=new Character('(');								
		right=new Character(')');
		quote=new Character('"');
		myStack=new Stack<Character>();
		allfunctions=new HashSet<String>();
		addFunctions(list);
	}
	 /* 1. xyz() - done  
	    2. xyz(x,y,z,x,c,k,b,,n,m,,f,d,s,s) - done
	    3. xyz("void(),xyz(),kzy()") - done
 	    4. xyz("hello",wow())  - done
	    5. left.equals(str.charAt(i))||quote.equals(str.charAt(i)))&&test==true	*/
	public void addFunctions(ArrayList<String> list)            
	{
		int index=0,current=0,len;
		boolean test;
		char previous;
		for(String g: list)
		{
			test=true;//this will prevent the function from testing the arguments inside string like "hello()"
			str=new StringBuilder(g);
			out.println(str);
			len=str.length();
			out.println(len+"fdhgfkghfhgflgjhs;fth");
			for(int i=0;i<len;i++)
			{
				
				if(left.equals(str.charAt(i)))      //this will push '(' on the stack
				{
					myStack.push(str.charAt(i));
					out.println("push "+myStack.peek());
					if(test)
					{
						current=i;
						test=false;
					}
				}
				else if(right.equals(str.charAt(i))||quote.equals(str.charAt(i)))
				{                                   
					if(!myStack.isEmpty())
					{
						if(quote.equals(myStack.peek())||right.equals(str.charAt(i)))
						{
							out.println("pop "+myStack.pop());
							if(myStack.isEmpty())
							{
								allfunctions.add(str.substring(index,current));
							}
						}
						else if(!quote.equals(myStack.peek()))
						{
							myStack.push(str.charAt(i));						
							out.println("push "+myStack.peek());
						}
					}
				}
			}
		
		}
		out.println(allfunctions);
	}
}
class MainNode extends DefaultMutableTreeNode
{
	MainNode(Object o)
	{
		super(o);
	}
}


















