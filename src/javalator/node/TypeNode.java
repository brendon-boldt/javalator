package javalator.node;

public class TypeNode extends PredictionNode {

	String varType;
	
	public TypeNode(Class<?> type, String varType) {
		super(type);
		this.varType = varType;
	}

}
