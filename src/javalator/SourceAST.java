package javalator;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;

public class SourceAST {

	CompilationUnit cu;
	ASTParser parser;
	ArrayList<String> tokens;
	StringBuilder currentToken = new StringBuilder();
	
	public boolean nameFlag 		= false;
	public boolean typeFlag 		= false;
	public boolean operatorFlag 	= false;
	public boolean literalFlag	 	= false;
	
	public String syntaxDelimiter = "_";
	public String infoDelimiter = ":";


    public static String getClassString(ASTNode node) {
        String[] list = node.getClass().toString().split("\\.");
        return list[list.length-1];
	}
    
    private static boolean hasOperator(ASTNode node) {
    	return node instanceof org.eclipse.jdt.core.dom.InfixExpression
    			|| node instanceof org.eclipse.jdt.core.dom.PrefixExpression
    			|| node instanceof org.eclipse.jdt.core.dom.PostfixExpression;
    }

    private static String getOperator(ASTNode node) {
    	if (node instanceof org.eclipse.jdt.core.dom.InfixExpression) {
    		return ((org.eclipse.jdt.core.dom.InfixExpression) node).getOperator().toString();
    	} else if (node instanceof org.eclipse.jdt.core.dom.PostfixExpression) {
    		return ((org.eclipse.jdt.core.dom.PostfixExpression) node).getOperator().toString();
    	} else if (node instanceof org.eclipse.jdt.core.dom.PrefixExpression) {
    		return ((org.eclipse.jdt.core.dom.PrefixExpression) node).getOperator().toString();
    	}
    	return "";
    }
    
    private static boolean isImportantNumber(String str) {
    	return str.equals("0")
    			|| str.equals("0L")
    			|| str.equals("0X0")
    			|| str.equals("1")
    			|| str.equals("1L");
    }
    
    
	ASTVisitor visitor = new ASTVisitor() {
		boolean inMethod = false;
		int depth = 0, prevDepth = 0;

		// Will need to account for methods/variables with the same name
		private Map<String, Integer> names = new HashMap<>();
		int nameCounter = 0;
		
		ASTNode originalMethodNode;

		int counter = 0;
		public void preVisit(ASTNode node) {
			++depth;
			if (!inMethod && node.getClass() == MethodDeclaration.class) {
				depth = 0;
				prevDepth = 0;
				inMethod = true;
				names = new HashMap<>();
				nameCounter = 0;
				originalMethodNode = (MethodDeclaration) node;
			} else if (inMethod == true) {
				if (node instanceof org.eclipse.jdt.core.dom.Statement) {
					depth = 0;
					prevDepth = 0;
					tokens.add(currentToken.toString());
					currentToken = new StringBuilder();
					//currentToken.append(" ");
				}
				if (prevDepth < depth) currentToken.append("(");
//				currentToken.append("_" + getClassString(node) + ":" + depth);
				if (node instanceof org.eclipse.jdt.core.dom.Block) {
					counter++;
					currentToken.append("{");
				} else {
					currentToken.append(syntaxDelimiter + node.getNodeType());
//					currentToken.append(syntaxDelimiter + getClassString(node));
				}
				
//				{
//					for (int i = 0; i < depth; i++) {
//						System.out.print("  ");
//					}
//					System.out.println(getClassString(node));
//				}
				
				if (nameFlag && node instanceof org.eclipse.jdt.core.dom.Name) {
					String name = node.toString();
					if (!names.containsKey(name)) {
						names.put(name, nameCounter++);
					}
					currentToken.append(infoDelimiter + names.get(name));
				} else if (typeFlag && node instanceof org.eclipse.jdt.core.dom.Type) {
					currentToken.append(infoDelimiter + node.toString().replace(' ', '-'));
				} else if (operatorFlag && hasOperator(node)) {
					String op = getOperator(node);
					currentToken.append(infoDelimiter
							+ op);
				} else if (literalFlag && node instanceof org.eclipse.jdt.core.dom.NumberLiteral) {
					if (isImportantNumber(node.toString())) {
						currentToken.append(infoDelimiter + node);
					}
				} else if (literalFlag && node instanceof org.eclipse.jdt.core.dom.BooleanLiteral) {
					currentToken.append(infoDelimiter + node);
				}

//				if (node instanceof org.eclipse.jdt.core.dom.Expression) {
//					currentToken.append("$" + ((Expression) node).resolveTypeBinding());
//				}
			}
		}

		public void postVisit(ASTNode node) {
			if (inMethod && prevDepth > depth && depth >= 0)
				currentToken.append(")");
			prevDepth = depth--;
			if (inMethod && node instanceof org.eclipse.jdt.core.dom.Block) {
				counter--;
				tokens.add(currentToken.toString());
				tokens.add("}");
				currentToken = new StringBuilder();
			//} else if (node.getClass() == MethodDeclaration.class) {
			} else if (node == originalMethodNode) {
				if (counter != 0) {
					System.exit(counter);
				}
				inMethod = false;
				tokens.add(currentToken.toString());
				tokens.add("\n");
				currentToken = new StringBuilder();
			}
		}

	};

	public SourceAST(String filename) throws FileNotFoundException {
		Scanner sourceFile = new Scanner(new File(filename));
		StringBuilder sourceString = new StringBuilder();
		while (sourceFile.hasNextLine()) {
			sourceString.append(sourceFile.nextLine() + "\n");
		}
		sourceFile.close();
		parser = ASTParser.newParser(AST.JLS8);
		parser.setSource(sourceString.toString().toCharArray());
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		
		cu = (CompilationUnit) parser.createAST(null);
	}
	
	public ArrayList<String> getMethodTokens() {
		tokens = new ArrayList<>();
		cu.accept(visitor);
		return tokens;
	}
	
	/**
	 * This AST constructor includes information for types.
	 * @param filename
	 * @param sourcepath
	 * @throws FileNotFoundException
	 */
	public SourceAST(String filename, String sourcepath) throws FileNotFoundException {
		Scanner sourceFile = new Scanner(new File(filename));
		StringBuilder sourceString = new StringBuilder();
		while (sourceFile.hasNextLine()) {
			sourceString.append(sourceFile.nextLine() + "\n");
		}
		sourceFile.close();
//		System.out.println(sourceString.toString());
		ASTParser parser = ASTParser.newParser(AST.JLS8);
		parser.setSource(sourceString.toString().toCharArray());
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setResolveBindings(true);
		parser.setBindingsRecovery(true);
		
		Map<String, String> options = JavaCore.getOptions();
		parser.setCompilerOptions(options);
		parser.setUnitName("Example.java");
		
		String[] sources = {sourcepath};
		String[] classpath = {};
		
		parser.setEnvironment(classpath, sources, new String[] {"UTF-8"}, false);
		
		cu = (CompilationUnit) parser.createAST(null);
		cu.accept(visitor);
		for (String t : tokens) {
			if (t.equals("\n"))
				System.out.println("<eos>");
			System.out.println(t);
		}
	}

}
