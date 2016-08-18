package javalator.node;

public class OperatorNode extends PredictionNode {

	String operatorString;
	
	public OperatorNode(Class<?> type, String operatorString) {
		super(type);
		this.operatorString = operatorString;
	}

}
