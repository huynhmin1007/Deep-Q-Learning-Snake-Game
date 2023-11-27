package neural_network.helper.activation;

public interface IActivation {
	
	double activation(double z);
	double activationDerivative(double z, double a);
}
