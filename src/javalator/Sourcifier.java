package javalator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTNode;

import javalator.node.NameNode;
import javalator.node.OperatorNode;
import javalator.node.PredictionNode;
import javalator.node.TypeNode;

public class Sourcifier {

	private String filename;
	
	public Sourcifier(String filename) {
		this.filename = filename;
	}
	
	private PredictionNode parseNodeString(String string) {
		System.out.println(string);
		int index;
		index = string.indexOf(':');
		if (index != -1) {
			int typeInt = Integer.parseInt(string.substring(0, index));
			Class<?> typeClass = ASTNode.nodeClassForType(typeInt);
			if (org.eclipse.jdt.core.dom.Type.class.isAssignableFrom(typeClass)) {
				String typeString =  string.substring(index+1);
				return new TypeNode(ASTNode.nodeClassForType(typeInt), typeString);
			} else if (org.eclipse.jdt.core.dom.Name.class.isAssignableFrom(typeClass)) {
				int number = Integer.parseInt(string.substring(index+1));
				return new NameNode(typeClass, number);
			} else if (org.eclipse.jdt.core.dom.InfixExpression.class
					.isAssignableFrom(typeClass)) {
				String op = string.substring(index+1);
				return new OperatorNode(typeClass, op);
			} else {
				return new PredictionNode(typeClass);
			}
		} else {
			int typeInt = Integer.parseInt(string);
//			System.out.println(ASTNode.nodeClassForType(typeInt));
			return new PredictionNode(ASTNode.nodeClassForType(typeInt));
		}
	}
	
	void generatePNT(PredictionNode parent, Iterator<String> it) {
		while (it.hasNext()) {
			String string;
			do {
				string = it.next();
			} while (string.isEmpty() && it.hasNext());
			if (string.isEmpty())
				return;
//			System.out.println("\"" + string + "\"");
			if (string.equals("(")) {
				continue;
			} else if (string.equals("{")) {
				parent.addChild(NodeStrings.BeginBlock.class);
			} else if (string.equals("}")) {
				parent.addChild(NodeStrings.EndBlock.class);
			} else if (string.charAt(string.length()-1) == '(') {
				PredictionNode node = parseNodeString(string.substring(0, string.length()-1));
				parent.addChild(node);
				generatePNT(node, it);
			} else if (string.charAt(string.length()-1) == ')') {
				PredictionNode node = parseNodeString(string.substring(0, string.indexOf(')')));
				parent.addChild(node);
				break;
			} else {
				PredictionNode node = parseNodeString(string);
				parent.addChild(node);
			}
		}
	}
	
	void sourcifyTokens() throws IOException {
		List<String> lines = Files.readAllLines(Paths.get(filename));
		for (String line : lines) {
			if (line.trim().isEmpty())
				continue;
			System.out.println("==Method==");
			String[] tokens = line.split(" ");
			int indent = 0;
			for (int i = 0; i < tokens.length; i++) {
				if (tokens[i].isEmpty())
					continue;
				PredictionNode pn = new PredictionNode(Object.class);
				Iterator<String> it = Arrays.asList(tokens[i].split("[_]")).listIterator();
				generatePNT(pn, it);
				
//				System.out.println(tokens[i] + "\n~~~");
//				System.out.println(pn.toTreeString());
				String sourceString = pn.toSourceString();
				if (sourceString.equals("{")) {
					for (int tab = 0; tab < indent; ++tab)
						System.out.print('\t');
					System.out.println(pn.toSourceString());
					indent++;
				} else if (sourceString.equals("}")) {
					indent--;
					for (int tab = 0; tab < indent; ++tab)
						;
//						System.out.print('\t');
					System.out.println(pn.toSourceString());
				} else {
					for (int tab = 0; tab < indent; ++tab)
						;
//						System.out.print('\t');
					if (indent == 0)
						System.out.println(pn.toSourceString());
					else
						System.out.println(pn.toSourceString() + ";");
				}
				System.out.println("\n");
			}
			System.out.println();
		}
	}
}
