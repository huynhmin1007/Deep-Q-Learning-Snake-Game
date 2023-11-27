package neural_network.helper.activation;

public class Relu implements IActivation {

	@Override
	public double activation(double z) {
		return Math.max(0, z);
	}

	@Override
	public double activationDerivative(double z, double a) {
	    return z < 0 ? 0 : 1;
	}
}