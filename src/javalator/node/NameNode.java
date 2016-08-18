package javalator.node;

public class NameNode extends PredictionNode {

	int number;
	
	public NameNode(Class<?> type, int number) {
		super(type);
		this.number = number;
	}

}
