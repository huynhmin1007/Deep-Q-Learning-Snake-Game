package neural_network.helper.activation;

public class Linear implements IActivation {
	@Override
    public double activation(double z) {
        return z;
    }

    @Override
    public double activationDerivative(double z, double a) {
        return 1;
    }
}
