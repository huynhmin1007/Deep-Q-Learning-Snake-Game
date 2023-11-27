package neural_network.network;

import java.util.Random;

import neural_network.helper.activation.IActivation;
import neural_network.helper.optimizer.Optimizer;

public class Layer {
    
	private double[] inputs;
	private double[] outputs;
	private double[] z;
	private double[][] weights;
	private double[] biases;
	
	private IActivation activation;
	private Optimizer optimizer;

	private double[][] totalErrorWeights;
	private double[] totalErrorBiases;
	
	private int mean;
	
	public Layer(int inputSize, int outputSize) {
		this.inputs = new double[inputSize];
		this.outputs = new double[outputSize];
		this.z = new double[outputSize];
		this.weights = new double[inputSize][outputSize];
		this.biases = new double[outputSize];
		
		totalErrorWeights = new double[inputSize][outputSize];
		totalErrorBiases = new double[outputSize];
		
		initializeWeights(inputSize, outputSize);
		initializeBiases();
	}
	
	public Layer(double[] inputs, double[] outputs, double[] z, double[][] weights, double[] biases,
			IActivation activation, Optimizer optimizer) {
		super();
		this.inputs = inputs;
		this.outputs = outputs;
		this.z = z;
		this.weights = weights;
		this.biases = biases;
		this.activation = activation;
		this.optimizer = optimizer;
	}

	public void initializeWeights(int inputSize, int outputSize) {
	    Random random = new Random();
	    double limit = Math.sqrt(6.0 / (inputSize + outputSize));

	    for (int i = 0; i < inputSize; i++) {
	        for (int j = 0; j < outputSize; j++) {
	            weights[i][j] = random.nextDouble(-1, 1) * 2 * limit - limit;
	        }
	    }
	}
	
	public void initializeBiases() {
		
	}
	
	public void forwardPropagation() {
		for(int i = 0; i < outputs.length; i++) {
			double weightedSum = 0;
			for(int j = 0; j < inputs.length; j++) {
				weightedSum += inputs[j] * weights[j][i];
			}
			weightedSum += biases[i];
			z[i] = weightedSum;
			outputs[i] = activation.activation(z[i]);
		}
	}

	public double[] backPropagation(double[] error) {
		double[] prevError = new double[inputs.length];
		for(int i = 0; i < prevError.length; i++) {
			for(int j = 0; j < error.length; j++) {
				prevError[i] += error[j] * weights[i][j] * activation.activationDerivative(z[j], outputs[j]);
			}
		}
		
		for(int i = 0; i < error.length; i++) {
			double gradient = error[i] * activation.activationDerivative(z[i], outputs[i]);
			for(int j = 0; j < weights.length; j++) {
				totalErrorWeights[j][i] += inputs[j] * gradient;
			}
			totalErrorBiases[i] += gradient;
		}
		
		mean++;
		
		return prevError;
	}
	
	public void updateWeights() {
		optimizer.update(weights, biases, totalErrorWeights, totalErrorBiases, mean);
		mean = 0;
	}
	
	public double[] getInputs() {
		return this.inputs;
	}
	
	public void setInputs(double[] inputs) {
		this.inputs = inputs;
	}

	public void setActivation(IActivation activation) {
		this.activation = activation;
	}

	public void setOptimizer(Optimizer optimizer) {
		this.optimizer = optimizer;
	}

	public double[] getOutputs() {
		return outputs;
	}
	
	public double[] getZ() {
		return this.z;
	}
	
	public double[][] getWeights() {
		return weights;
	}

	public void setWeights(double[][] weights) {
		this.weights = weights;
	}

	public double[] getBiases() {
		return biases;
	}

	public void setBiases(double[] biases) {
		this.biases = biases;
	}

	public IActivation getActivation() {
		return this.activation;
	}
	
	public int getInputSize() {
		return weights.length;
	}
	
	public int getOutputSize() {
		return weights[0].length;
	}
	
	public Layer clone() {
		double[][] weightsClone = new double[weights.length][weights[0].length];
		for(int i = 0; i < weightsClone.length; i++) {
			weightsClone[i] = weights[i].clone();
		}
		return new Layer(inputs.clone(), outputs.clone(), z.clone(), weightsClone, biases.clone(), activation, optimizer.clone());
	}
}

