package neural_network.helper.activation;

public class Sigmoid implements IActivation {

	@Override
	public double activation(double z) {
		// TODO Auto-generated method stub
		return 1 / (1 + Math.exp(-z));
	}

	@Override
	public double activationDerivative(double z, double a) {
		// TODO Auto-generated method stub
		return a * (1 - a);
	}
}
