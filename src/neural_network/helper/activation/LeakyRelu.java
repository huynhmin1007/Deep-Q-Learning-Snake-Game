package neural_network.helper.activation;

public class LeakyRelu implements IActivation {

	private double alpha = 0.3;

    public LeakyRelu(double alpha) {
        this.alpha = alpha;
    }
    
    public LeakyRelu() {
    	
    }
    
	@Override
	public double activation(double z) {
		// TODO Auto-generated method stub
		return z < 0? alpha * z : z;
	}

	@Override
	public double activationDerivative(double z, double a) {
		// TODO Auto-generated method stub
		return z < 0? alpha : 1;
	}
}
