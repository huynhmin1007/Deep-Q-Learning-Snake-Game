package neural_network.helper.optimizer;

import java.util.Arrays;

public abstract class Optimizer {
	
	protected float learningRate = 0.001f;
	
	public Optimizer(float learningRate) {
		this.learningRate = learningRate;
	}
	
	public void setLearningRate(float learningRate) {
		this.learningRate = learningRate;
	}
	
	public abstract double[][] calcGradientForWeights(double[][] totalErrorWeights, int mean);
	public abstract double[] calcGradientForBiases(double[] totalErrorBiases, int mean);
	
	public void update(double[][] weights, double[] biases, double[][] totalErrorWeights, double[] totalErrorBiases, int mean) {
		double[][] gradientForWeights = calcGradientForWeights(totalErrorWeights, mean);
		double[] gradientForBiases = calcGradientForBiases(totalErrorBiases, mean);
		
		for(int i = 0; i < gradientForBiases.length; i++) {
			for(int j = 0; j < gradientForWeights.length; j++) {
				weights[j][i] -= gradientForWeights[j][i];
				totalErrorWeights[j][i] = 0;
			}
			biases[i] -= gradientForBiases[i];
			totalErrorBiases[i] = 0;
		}
	}
	
	public abstract Optimizer clone();
}
