package neural_network.helper.optimizer;

public class GradientDescent extends Optimizer {

	public GradientDescent(float learningRate) {
		super(learningRate);
		// TODO Auto-generated constructor stub
	}

	@Override
	public double[][] calcGradientForWeights(double[][] totalErrorWeights, int mean) {
		double[][] gradientForWeights = new double[totalErrorWeights.length][totalErrorWeights[0].length];
		
		for(int i = 0; i < gradientForWeights.length; i++) {
			for(int j = 0; j < gradientForWeights[i].length; j++) {
				gradientForWeights[i][j] = learningRate * (totalErrorWeights[i][j] / mean);
			}
		}
		
		return gradientForWeights;
	}

	@Override
	public double[] calcGradientForBiases(double[] totalErrorBiases, int mean) {
		double[] gradientForBiases = new double[totalErrorBiases.length];
		
		for(int i = 0; i < gradientForBiases.length; i++) {
			gradientForBiases[i] = learningRate * (totalErrorBiases[i] / mean);
		}
		
		return gradientForBiases;
	}

	@Override
	public Optimizer clone() {
		// TODO Auto-generated method stub
		return new GradientDescent(learningRate);
	}
}
