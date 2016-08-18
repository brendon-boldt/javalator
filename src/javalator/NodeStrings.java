package javalator;

import java.util.HashMap;

public class NodeStrings {
	
	final public class BeginBlock { }
	final public class EndBlock { }
	
	static HashMap<Class<?>, String> strings = new HashMap<Class<?>, String>() {
		private static final long serialVersionUID = 1L;
	{
		put(Object.class,													"");
		put(BeginBlock.class,												"{");
		put(EndBlock.class,													"}");
		put(org.eclipse.jdt.core.dom.ReturnStatement.class,					"return ");
		put(org.eclipse.jdt.core.dom.QualifiedName.class,					"");
		put(org.eclipse.jdt.core.dom.SimpleName.class,						"NAME");
		put(org.eclipse.jdt.core.dom.PrimitiveType.class,					"PTYPE");
		put(org.eclipse.jdt.core.dom.SimpleType.class,						"RTYPE");
		put(org.eclipse.jdt.core.dom.VariableDeclarationStatement.class,	"");
		put(org.eclipse.jdt.core.dom.ExpressionStatement.class,				"");
		put(org.eclipse.jdt.core.dom.NumberLiteral.class,					"#");
		put(org.eclipse.jdt.core.dom.MethodInvocation.class,				"METHOD()");
		put(org.eclipse.jdt.core.dom.InfixExpression.class,					"");
	}};
	
	public static String get(Class<?> c) {
		if (strings.containsKey(c)) 
			return strings.get(c);
		else {
			String[] classStrings = c.toString().split("\\.");
			return classStrings[classStrings.length-1] + " ";
		}
	}
}
