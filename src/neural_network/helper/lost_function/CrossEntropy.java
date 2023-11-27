package neural_network.helper.lost_function;

public class CrossEntropy implements ILostFunction {

	@Override
	public double lostFunction(double predict, double target) {
		// TODO Auto-generated method stub
		return - (target * Math.log(predict) + (1 - target) * Math.log(1 - predict));
	}

	@Override
	public double lostFunctionDerivative(double predict, double target) {
		return -((target / predict) - (1 - target) / (1 - predict));
	}
}