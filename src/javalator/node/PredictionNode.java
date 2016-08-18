package javalator.node;

import java.util.ArrayList;

public class PredictionNode {
	Class<?> type;
	String string = null;
	private ArrayList<PredictionNode> children = new ArrayList<>();
	
	public PredictionNode(Class<?> type) {
		this.type = type;
	}
	
	public void addChild(Class<?> type) {
		children.add(new PredictionNode(type));
	}
	
	public void addChild(PredictionNode node) {
		children.add(node);
	}
	
	public PredictionNode getChild(int i) {
		return children.get(i);
	}
	
	@Override
	public String toString() {
		if (string == null) {
			string = javalator.NodeStrings.get(this.type);
			if (this instanceof TypeNode) {
				string += ":" + ((TypeNode) this).varType;
			} else if (this instanceof NameNode) {
				string += "-" + ((NameNode) this).number;
			} else if (this instanceof OperatorNode) {
				string += ((OperatorNode) this).operatorString;
			}
		}
		return string;
	}
	
	public String toSourceString() {
		String string = "";

		String[] classStrings = this.type.toString().split("\\.");
		switch (classStrings[classStrings.length-1]) {
			case "VariableDeclarationFragment":
				if (children.size() == 2)
					string += children.get(0).toSourceString()
						+ "= " + children.get(1).toSourceString();
				else
					string += children.get(0).toSourceString() + " ";
				break;
				
			case "SimpleType":
				string += this.toString() + " ";
				break;

			case "QualifiedName":
				string += children.get(1) + " ";
				break;

			case "MethodInvocation":
				string += this.toString() + " ";
				break;

			case "Assignment":
				// Sometimes only has one child?
				if (children.size() > 1)
					string += children.get(0).toSourceString()
						+ "= " + children.get(1).toSourceString();
				else
					string += children.get(0).toSourceString() + "= ";
				break;
				
			case "InfixExpression":
				string += children.get(0).toSourceString()
					+ this.toString() + " " + children.get(1).toSourceString();
				break;
				
			default:
				string += this.toString() + " "; 
				for (PredictionNode child : children) {
					string += child.toSourceString();
				}
		}
		return string;
	}
	
	public String toTreeString() {
		return toTreeString(0);
	}
	
	private String toTreeString(int depth) {
		String string = "";
		for (int i = 0; i < depth; i++)
			string += "\t";
		String[] classStrings = this.type.toString().split("\\.");
		string += classStrings[classStrings.length-1] + "\n";
//		if (this.toString().isEmpty())
//			--depth;
//	 	else {
//			string += this.toString();
//	 	}

		if (children.isEmpty())
			string += "";
		for (PredictionNode child : children) {
			string += child.toTreeString(depth + 1);
		}
		if (this.toString().isEmpty())
			return string;
		else
			return string;
	}
	
}
