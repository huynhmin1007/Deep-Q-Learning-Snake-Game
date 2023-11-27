package neural_network.helper.activation;

public enum Activation {
    
	LINEAR(new Linear()), RELU(new Relu()), LEAKY_RELU(new LeakyRelu()), SIGMOID(new Sigmoid());
	
	private IActivation activation;
	
	private Activation(IActivation activation) {
		this.activation = activation;
	}
	
	public IActivation getValue() {
		return activation;
	}
}
