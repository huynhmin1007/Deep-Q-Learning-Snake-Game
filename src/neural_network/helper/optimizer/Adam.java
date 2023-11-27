package neural_network.helper.optimizer;

import java.util.Arrays;

public class Adam extends Optimizer {
	
	private double beta1 = 0.9;
	private double beta2 = 0.999;
	private double epsilon = 1e-8;
	private int t = 0;
	
	private double[][] mWeights;
	private double[][] vWeights;
	
	private double[] mBiases;
	private double[] vBiases;
	
	public Adam(float learningRate) {
		super(learningRate);
	}

	@Override
	public double[][] calcGradientForWeights(double[][] totalErrorWeights, int mean) {
		t++;
		if(mWeights == null) {
			mWeights = new double[totalErrorWeights.length][totalErrorWeights[0].length];
		}
		if(vWeights == null) {
			vWeights = new double[totalErrorWeights.length][totalErrorWeights[0].length];
		}
		
		double[][] gradientForWeights = new double[totalErrorWeights.length][totalErrorWeights[0].length];
		
		for(int i = 0; i < totalErrorWeights.length; i++) {
			for(int j = 0; j < totalErrorWeights[i].length; j++) {
				double averageError =  totalErrorWeights[i][j] / mean;
				
				mWeights[i][j] = beta1 * mWeights[i][j] + (1 - beta1) * averageError;
				vWeights[i][j] = beta2 * vWeights[i][j] + (1 - beta2) * (averageError * averageError);

				double mHat = mWeights[i][j] / (1 - Math.pow(beta1, t));
		        double vHat = vWeights[i][j] / (1 - Math.pow(beta2, t));
		        
		        double step = learningRate * mHat / (Math.sqrt(vHat) + epsilon);
		        
		        gradientForWeights[i][j] = step;
			}
		}
		
		return gradientForWeights;
	}

	@Override
	public double[] calcGradientForBiases(double[] totalErrorBiases, int mean) {
		
		if(mBiases == null) {
			mBiases = new double[totalErrorBiases.length];
		}
		if(vBiases == null) {
			vBiases = new double[totalErrorBiases.length];
		}
		
		double[] gradientForBiases = new double[totalErrorBiases.length];
		
		for(int i = 0; i < gradientForBiases.length; i++) {
			double averageError = totalErrorBiases[i] / mean;

			mBiases[i] = beta1 * mBiases[i] + (1 - beta1) * averageError;
			vBiases[i] = beta2 * vBiases[i] + (1 - beta2) * (averageError * averageError);
			
			double mHat = mBiases[i] / (1 - Math.pow(beta1, t));
	        double vHat = vBiases[i] / (1 - Math.pow(beta2, t));
	       
	        double step = learningRate * mHat / (Math.sqrt(vHat) + epsilon);
	        
	        gradientForBiases[i] = step;
		}
		
		return gradientForBiases;
	}

	@Override
	public void update(double[][] weights, double[] biases, double[][] totalErrorWeights, double[] totalErrorBiases,
			int mean) {
		t++;
		super.update(weights, biases, totalErrorWeights, totalErrorBiases, mean);
	}

	@Override
	public Optimizer clone() {
		return new Adam(learningRate);
	}
}
