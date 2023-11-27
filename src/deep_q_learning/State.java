package deep_q_learning;

public class State {

	private final double[] inputs;

	public State(final double[] inputs) {
		super();
		this.inputs = inputs;
	}
	
	public double[] getInputs() {
		return this.inputs;
	}
	
	public State clone() {
		return new State(inputs.clone());
	}
}
