package neural_network.helper.lost_function;

public class MSE implements ILostFunction {

	@Override
	public double lostFunction(double predict, double target) {
		// TODO Auto-generated method stub
		return Math.pow(target - predict, 2);
	}

	@Override
	public double lostFunctionDerivative(double predict, double target) {
		return 2 * (predict - target);
	}
}