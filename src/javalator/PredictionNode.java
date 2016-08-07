package javalator;

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
			string = NodeStrings.get(this.type);
		}
		return string;
	}
	
	public String toSourceString() {
		String string = "";

		String[] classStrings = this.type.toString().split("\\.");
		switch (classStrings[classStrings.length-1]) {
			case "VariableDeclarationFragment":
				if (children.size() == 2)
					string += children.get(0) + "= " + children.get(1);
				else
					string += children.get(0);
				break;
				
			case "SimpleType":
				string += this.toString();
				break;

			case "MethodInvocation":
				string += this.toString();
				break;

			case "Assignment":
				string += children.get(0) + "= " + children.get(1);
				break;
				
			default:
				string += this.toString();
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
