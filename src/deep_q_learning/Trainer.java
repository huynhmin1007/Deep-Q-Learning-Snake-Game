package deep_q_learning;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

import AStar.AStar;
import my_application.util.GameUtil;
import my_application.view.Environment;
import my_application.viewhelper.Action;
import my_application.viewhelper.Direction;
import my_application.viewhelper.GameMode;
import neural_network.helper.activation.Activation;
import neural_network.helper.lost_function.MSE;
import neural_network.helper.optimizer.Adam;
import neural_network.network.Layer;
import neural_network.network.NeuralNetwork;

public class Trainer extends JFrame {
	
	private Environment env;
	private Agent agent;
	
	private int episode = 0;
	
	public Trainer() {
		env = new Environment(GameMode.STEP);
		
		add(env);
		setTitle("Snake");
		setIconImage(new ImageIcon(System.getProperty("user.dir") + "/imgs/snake_icon.png").getImage());
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
		pack();
		setVisible(true);
		setLocationRelativeTo(null);
		 
		NeuralNetwork mainNetwork = new NeuralNetwork();
		
		mainNetwork.setOptimizer(new Adam(0.00025f));
		mainNetwork.setLostFuncton(new MSE());
		
		mainNetwork.addLayer(new Layer(28, 64, Activation.RELU.getValue()));
		mainNetwork.addLayer(new Layer(64, 32, Activation.RELU.getValue()));
		mainNetwork.addLayer(new Layer(32, 3, Activation.LINEAR.getValue()));
		
		agent = new Agent.Builder(28, 3)
				.maxMemory(100_000)
				.batchSize(1000)
				.gamma(0.99)
				.network(mainNetwork)
				.build();
	}
	
	public void trainAStar(AStar aStar) throws InterruptedException {
		while(true) {
			if(env.getScore() >= 100) {
				env.repaint();
				Thread.sleep(20);
			}
			
			int action = aStar.aSearch(env.getSnake(), env.getFood(), env.getDirection());
			if(action == -1) action = new Random().nextInt(3);
			
			env.step(action);
			
			if(env.isDone()) {
				env.repaint();
				System.out.println(env.getScore());
				break;
			}
		}
	}
	
	public void train() throws InterruptedException, IOException {
		int highestScore = 0;
		int maxScore = ((GameUtil.SCREEN_WIDTH * GameUtil.SCREEN_HEIGHT) / GameUtil.TILE_SIZE) - 3;
		
		int recordScore = 140;
		
		loadWeigthsCreated();
		
		int totalTimeStep = 0;
		float epsilon = 1;

		for(int i = 0; highestScore < maxScore; episode++) {
			State currentState = env.reset();
	
			while(true) {	
				
				// should turn it off if want to train faster
//				env.repaint();
//				Thread.sleep(10);
				
				if(env.getScore() >= 100) {
					env.repaint();
					Thread.sleep(20);
				}
				
				totalTimeStep++;
				
				if(totalTimeStep > 100000) {
					agent.updateTargetNetwork();
				}
				else if(totalTimeStep % 50 == 0) {
					agent.updateTargetNetwork();
				}
				
				int action = agent.chooseAction(currentState, epsilon);
				
				State nextState = env.step(action);
						
				double reward = env.getReward();
	
				boolean done = env.isDone();
						
				Experience exp = new Experience(currentState.clone(), action, reward, nextState.clone(), done);
				
				currentState = nextState;
				
				highestScore = Math.max(highestScore, env.getScore());
				
				agent.addExperience(exp);
				agent.trainShort(exp);
				
				if(highestScore > recordScore) {
					recordScore = highestScore;
					saveDataTrained(System.getProperty("user.dir") + "/data_trained_" + (++i) + ".txt", agent.getMainNetwork(), env.getScore());
				}
				if(done) {
					env.repaint();
					agent.trainMainNetwork();
					if(env.getScore() >= 140) {
						saveDataTrained(System.getProperty("user.dir") + "/data_trained_" + (++i) + ".txt", agent.getMainNetwork(), env.getScore());
					}
					break;
				}
			}
			if(epsilon >= 0) epsilon *= 0.96f;
			System.out.println(String.format("EPS = %d, Score = %d, Highest Score = %d", episode + 1, env.getScore(), highestScore));
		}
	}
	
	public void test(int i) throws IOException, InterruptedException {
		loadDataTrained(System.getProperty("user.dir") + "/data_trained_best_" + i + ".txt", agent.getMainNetwork());
		
		int highestScore = 0;
		double averageScore = 0;
		
		for(int e = 0; e < 20; e++) {
			State currentState = env.reset();
			while(true) {
				if(env.getScore() >= 100) {
					env.repaint();
					Thread.sleep(20);
				}

				int action = agent.chooseAction(currentState);
				
				currentState = env.step(action);
				highestScore = Math.max(highestScore, env.getScore());
				
				if(env.isDone()) {
					env.repaint();
					Thread.sleep(400);
					break;
				}
			}
			averageScore += env.getScore();
			System.out.println("Score = " + env.getScore());
		}
		System.out.println("Average Score = " + averageScore / 10);
	}
	
	public int changeDirection(int currentDirection, Action action) {
		switch(action) {
		case MOVE_RIGHT:
			currentDirection = (currentDirection + 1) % Direction.values().length;
			break;
		case MOVE_LEFT:
			currentDirection--;
			if(currentDirection < 0) {
				currentDirection = Direction.values().length - 1;
			}
			break;
		}
		return currentDirection;
	}
	
	public void loadWeigthsCreated() throws IOException {
		String path = System.getProperty("user.dir") + "/weights.txt";
		
		BufferedReader br = new BufferedReader(new FileReader(new File(path)));
		
		NeuralNetwork mainNetwork = agent.getMainNetwork();
		for(int i = 0; i < mainNetwork.getSize(); i++) {
			Layer layer = mainNetwork.getLayer(i);
			
			double[][] weights = new double[layer.getInputSize()][layer.getOutputSize()];
			
			for(int j = 0; j < weights.length; j++) {
				String weightsStr = br.readLine();
				weights[j] = Arrays.stream(weightsStr.split("\\s+"))
						.map(String::trim)
						.mapToDouble(Double::parseDouble)
						.toArray();
			}
			
			double[] biases = Arrays.stream(br.readLine().split("\\s+"))
					.map(String::trim)
					.mapToDouble(Double::parseDouble)
					.toArray();

			layer.setWeights(weights);
			layer.setBiases(biases);
		}
		
		agent.updateTargetNetwork();
		
		br.close();
	}
	
	public void saveDataTrained(String path, NeuralNetwork network, int score) throws FileNotFoundException {
		PrintWriter pr = new PrintWriter(new FileOutputStream(path), true);
		
		for(int i = 0; i < network.getSize(); i++) {
			Layer layer = network.getLayer(i);
			
			double[][] weigths = layer.getWeights();
			double[] biases = layer.getBiases();
			
			for(double[] w : weigths) {
				StringBuilder weightsStr = new StringBuilder();
				for(double d : w) {
					weightsStr.append(d + " ");
				}
				pr.println(weightsStr);
			}
			
			StringBuilder biasesStr = new StringBuilder();
			for(double d : biases) {
				biasesStr.append(d + " ");
			}
			pr.println(biasesStr);
		};
		
		pr.println("Score: " + score);
		
		pr.close();
	}
	
	public void loadDataTrained(String path, NeuralNetwork network) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(new File(path)));
		
		for(int i = 0; i < network.getSize(); i++) {
			Layer layer = network.getLayer(i);
			
			double[][] weights = new double[layer.getInputSize()][layer.getOutputSize()];
			
			for(int j = 0; j < weights.length; j++) {
				String weightsStr = br.readLine();
				weights[j] = Arrays.stream(weightsStr.split("\\s+"))
						.map(String::trim)
						.mapToDouble(Double::parseDouble)
						.toArray();
			}
			
			double[] biases = Arrays.stream(br.readLine().split("\\s+"))
					.map(String::trim)
					.mapToDouble(Double::parseDouble)
					.toArray();
			
			layer.setWeights(weights);
			layer.setBiases(biases);
		}
		
		br.close();
	}
	
	public static void main(String[] args) throws InterruptedException, IOException {
		Trainer trainer = new Trainer();
		trainer.train();
	}
}
