package neural_network.helper.lost_function;

public interface ILostFunction {
	
	double lostFunction(double predict, double target);
	double lostFunctionDerivative(double predict, double target);
}
