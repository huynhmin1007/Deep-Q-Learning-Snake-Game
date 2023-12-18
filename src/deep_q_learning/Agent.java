package deep_q_learning;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;
import neural_network.network.NeuralNetwork;

public class Agent {
	
	private int stateSize;
	private int numberOfAction;
	
	private int maxMemory;
	private int batchSize;
	private ArrayList<Experience> buffer;
	private int bufferSize;
	
	private double gamma;
	private double learningRate;
	
	private NeuralNetwork mainNetwork, targetNetwork;
	
	public Agent(final Builder builder) {
		this.stateSize = builder.stateSize;
		this.numberOfAction = builder.numberOfAction;
		this.maxMemory = builder.maxMemory;
		this.batchSize = builder.batchSize;
		this.gamma = builder.gamma;
		this.learningRate = builder.learningRate;
		this.mainNetwork = builder.mainNetwork;
		this.targetNetwork = builder.targetNetwork;
			
		buffer = new ArrayList<Experience>();
	}
	
	public void addExperience(Experience exp) {
		if(bufferSize > maxMemory) {
			bufferSize--;
			buffer.remove(0);
		}
		buffer.add(exp);
		bufferSize++;
	}
	
	public Experience[] getRandomBatch() {
		Collections.shuffle(buffer);
		return buffer.subList(0, batchSize).toArray(new Experience[0]);
	}
	
	public double[] getQValues(State state, NeuralNetwork network) {
		return network.predict(state.getInputs());
	}
	
	public int getBestAction(double[] qValues) {
		int maxIndex = 0;
		
		for(int i = 1; i < qValues.length; i++) {
			maxIndex = qValues[maxIndex] < qValues[i]? i : maxIndex;
		}

		return maxIndex;
	}
	
	public double getMaxQValue(double[] qValues) {
		return Arrays.stream(qValues).max().orElse(Double.MIN_VALUE);
	}
	
	public void trainMainNetwork() {
		
		Experience[] expBatch;
		
		if(bufferSize > batchSize) {
			expBatch = getRandomBatch();
		}
		else {
			expBatch = buffer.toArray(new Experience[0]);
		}
		
		for(Experience exp : expBatch) {
			
			double maxQNextState = getMaxQValue(getQValues(exp.getNextState(), targetNetwork));
			
			double targetQValue = exp.getReward();
			if(!exp.isDone()) {
				targetQValue += gamma * maxQNextState;
			}
			
			double[] qPredicts = getQValues(exp.getState(), mainNetwork);
			
			double[] targets = qPredicts.clone();
			targets[exp.getAction()] = targetQValue;
			
			mainNetwork.loss(qPredicts, targets);
		}
		
		mainNetwork.fit();
	}
	
	public void trainShort(Experience exp) {
		double maxQNextState = getMaxQValue(getQValues(exp.getNextState(), targetNetwork));
		
		double targetQValue = exp.getReward();
		if(!exp.isDone()) {
			targetQValue += gamma * maxQNextState;
		}
		
		double[] qPredicts = getQValues(exp.getState(), mainNetwork);

		double[] targets = qPredicts.clone();
		targets[exp.getAction()] = targetQValue;
		
		mainNetwork.loss(qPredicts, targets);
		
		mainNetwork.fit();
	}
	
	public void updateTargetNetwork() {
		targetNetwork = mainNetwork.clone();
	}
	
	public int chooseAction(State state, float epsilon) {
		Random random = new Random();
		if(random.nextDouble() < epsilon) {
			return random.nextInt(numberOfAction);
		}
		return getBestAction(getQValues(state, mainNetwork));
	}
	
	public int chooseAction(State state) {
		return getBestAction(getQValues(state, mainNetwork));
	}
	
	public NeuralNetwork getMainNetwork() {
		return this.mainNetwork;
	}
	
	public void clearMemory() {
		this.bufferSize = 0;
		this.buffer.clear();
	}
	
	public static class Builder {
		private int stateSize;
		private int numberOfAction;
		
		private int maxMemory;
		private int batchSize;
		
		private double gamma;
		private double learningRate;
		
		private NeuralNetwork mainNetwork, targetNetwork;
		
		public Builder(int stateSize, int numberOfAction) {
			this.stateSize = stateSize;
			this.numberOfAction = numberOfAction;
		}
		
		public Builder maxMemory(int maxMemory) {
			this.maxMemory = maxMemory;
			return this;
		}
		
		public Builder batchSize(int batchSize) {
			this.batchSize = batchSize;
			return this;
		}
		
		public Builder gamma(double gamma) {
			this.gamma = gamma;
			return this;
		}
		
		public Builder learningRate(double learningRate) {
			this.learningRate = learningRate;
			return this;
		}
		
		public Builder network(NeuralNetwork network) {
			this.mainNetwork = network;
			this.targetNetwork = mainNetwork.clone();
			return this;
		}
		
		public Agent build() {
			return new Agent(this);
		}
	}
}
