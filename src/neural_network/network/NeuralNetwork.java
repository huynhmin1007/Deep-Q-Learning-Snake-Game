package neural_network.network;

import java.util.ArrayList;
import java.util.Arrays;

import neural_network.helper.lost_function.ILostFunction;
import neural_network.helper.optimizer.Optimizer;

public class NeuralNetwork {
	
	private ArrayList<Layer> network;
	private ILostFunction lostFuncton;
	private Optimizer optimizer;
	
	private int size;
	
	private Layer outputLayer;

	public NeuralNetwork() {
		network = new ArrayList<Layer>();
	}
	
	public NeuralNetwork(ArrayList<Layer> network, ILostFunction lostFuncton, Optimizer optimizer,
			int size, Layer outputLayer) {
		super();
		this.network = network;
		this.lostFuncton = lostFuncton;
		this.optimizer = optimizer;
		this.size = size;
		this.outputLayer = outputLayer;
	}

	public void addLayer(Layer layer) {
		layer.setOptimizer(optimizer.clone());
		network.add(layer);
		size++;
		
		outputLayer = network.get(size - 1);
	}
	
	public double[] predict(double[] input) {
		forwardPropagation(input);
		return outputLayer.getOutputs().clone();
	}
	
	public void forwardPropagation(double[] input) {
		network.get(0).setInputs(input);
		network.get(0).forwardPropagation();
		
		for(int i = 1; i < size; i++) {
			Layer layer = network.get(i);
			
			layer.setInputs(network.get(i - 1).getOutputs());
			layer.forwardPropagation();
		}
	}
	
	public void backPropagation(double[] error) {
		for(int i = size - 1; i >= 0; i--) {
			error = network.get(i).backPropagation(error);
		}
	}
	
	public void loss(double[] predict, double[] target) {
		double[] error = new double[predict.length];
		
		for(int i = 0; i < predict.length; i++) {
			error[i] = lostFuncton.lostFunctionDerivative(predict[i], target[i]);
		}
		backPropagation(error);
	}
	
	public void fit() {
		for(int i = 0; i < size; i++) {
			network.get(i).updateWeights();
		}
	}
	
	public void setLostFuncton(ILostFunction lostFuncton) {
		this.lostFuncton = lostFuncton;
	}

	public void setOptimizer(Optimizer optimizer) {
		this.optimizer = optimizer;
	}
	
	public Layer getLayer(int i) {
		return network.get(i);
	}
	
	public Optimizer getOptimizer() {
		return this.optimizer;
	}
	
	public int getSize() {
		return this.size;
	}
	
	public void setWeights(NeuralNetwork targetNetwork) {
		for(int i = 0; i < size; i++) {
			Layer layer = this.getLayer(i);
			Layer layerTarget = targetNetwork.getLayer(i);
			
			double[][] weigthsTarget = layerTarget.getWeights();
			double[][] weightsClone = new double[weigthsTarget.length][weigthsTarget[0].length];
			
			for(int j = 0; j < weightsClone.length; j++) {
				weightsClone[j] = weigthsTarget[j].clone();
			}
			
			layer.setWeights(weightsClone);
			layer.setBiases(layerTarget.getBiases().clone());
		}
	}
	
	public NeuralNetwork clone() {
		ArrayList<Layer> networkClone = new ArrayList<Layer>();
		for(Layer layer : network) {
			networkClone.add(layer.clone());
		}
		Layer outClone = networkClone.get(size - 1);
		return new NeuralNetwork(networkClone, lostFuncton, optimizer.clone(), size, outClone);
	}
}
