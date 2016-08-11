package javalator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTNode;


public class Main {
	
	
	
	static void generatePNT(PredictionNode parent, Iterator<String> it) {
		while (it.hasNext()) {
			String string;
			do {
				string = it.next();
			} while (string.isEmpty() && it.hasNext());
			if (string.isEmpty())
				return;
//			System.out.println("\"" + string + "\"");
			if (string.equals("("))
				continue;
			else if (string.equals("{"))
				parent.addChild(NodeStrings.BeginBlock.class);
			else if (string.equals("}"))
				parent.addChild(NodeStrings.EndBlock.class);
			else if (string.charAt(string.length()-1) == '(') {
				int typeInt = Integer.parseInt(string.substring(0, string.length()-1));
				PredictionNode node = new PredictionNode(ASTNode.nodeClassForType(typeInt));
				parent.addChild(node);
				generatePNT(node, it);
			} else if (string.charAt(string.length()-1) == ')') {
				int typeInt = Integer.parseInt(string.substring(0, string.indexOf(')')));
				parent.addChild(ASTNode.nodeClassForType(typeInt));
				break;
			} else {
				int typeInt = Integer.parseInt(string);
				parent.addChild(ASTNode.nodeClassForType(typeInt));
			}
		}
	}
	
	
	// Variable numbering!
	
	
	static void sourceifyTokens() throws IOException {
		List<String> lines = Files.readAllLines(Paths.get("out.txt"));
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
				System.out.println(pn.toTreeString());
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
	
	public static void main(String[] args) throws IOException {
		ArrayList<String> sourceDirs = new ArrayList<>(Arrays.asList(
				"/home/brendon/ht/jdk/src/share/classes/java/util/concurrent",
				"files/"
//				,"/home/brendon/ht/jdk/"
				));
		Tokenizer t = new Tokenizer(sourceDirs);
		t.tokenizeSources();
//		sourceifyTokens();
	}

}
