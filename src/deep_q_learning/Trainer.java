package deep_q_learning;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

import my_application.application.App;
import my_application.util.GameUtil;
import my_application.view.Environment;
import my_application.viewhelper.GameMode;
import neural_network.helper.activation.Activation;
import neural_network.helper.lost_function.MSE;
import neural_network.helper.optimizer.Adam;
import neural_network.helper.optimizer.Optimizer;
import neural_network.network.Layer;
import neural_network.network.NeuralNetwork;

public class Trainer extends JFrame {
	
	private Environment env;
	private Agent agent;
	
	private int episode = 0;
	private int recordScore = 0;
	
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
		mainNetwork.setOptimizer(new Adam(0.001f));
		mainNetwork.setLostFuncton(new MSE());
		
		Layer l1 = new Layer(18, 64);
		l1.setActivation(Activation.RELU.getValue());
		
		Layer l2 = new Layer(64, 32);
		l2.setActivation(Activation.RELU.getValue());
		
		Layer l3 = new Layer(32, 3);
		l3.setActivation(Activation.LINEAR.getValue());
		
		mainNetwork.addLayer(l1);
		mainNetwork.addLayer(l2);
		mainNetwork.addLayer(l3);
		
		agent = new Agent.Builder(18, 3)
				.maxMemory(100_000)
				.batchSize(1000)
				.gamma(0.95)
				.network(mainNetwork)
				.build();
	}
	
	public void train() throws InterruptedException, IOException {
		int highestScore = 0;
		int maxScore = ((GameUtil.SCREEN_WIDTH * GameUtil.SCREEN_HEIGHT) / GameUtil.TILE_SIZE) - 3;
		
		recordScore = 55;
		
		loadWeigthsCreated();
		
		int totalTimeStep = 0;
		
		for(int i = 0; highestScore < maxScore; episode++) {
			State currentState = env.reset();
	
			while(true) {	

				env.repaint();
				Thread.sleep(30);
						
				totalTimeStep++;
				
				if(totalTimeStep > 100000) {
					agent.updateTargetNetwork();
				}
				else if(totalTimeStep % 50 == 0) {
					agent.updateTargetNetwork();
				}
				
				int action = agent.chooseAction(currentState, episode);
				
				State nextState = env.step(action);
						
				double reward = env.getReward();
	
				boolean done = env.isDone();
						
				Experience exp = new Experience(currentState.clone(), action, reward, nextState.clone(), done);
				
				currentState = nextState;
				
				highestScore = Math.max(highestScore, env.getScore());
				
				agent.addExperience(exp);

				agent.trainShort(exp);

				if(done) {
					if(highestScore > recordScore) {
						saveDataTrained(System.getProperty("user.dir") + "/data_trained_" + (++i) + ".txt", agent.getMainNetwork());
						recordScore = highestScore;
					}

					agent.trainMainNetwork();
	
					break;
				}
			}
			System.out.println(String.format("EPS = %d, Score = %d, Highest Score = %d", episode + 1, env.getScore(), highestScore));
		}
	}
	
	public void test() throws IOException, InterruptedException {
		loadDataTrained(System.getProperty("user.dir") + "/data_trained_1.txt", agent.getMainNetwork());
		
		int highestScore = 0;
		int totalTimeStep = 0;
		
		for(int e = 0; e < 100; e++) {
			State currentState = env.reset();
			while(true) {
				
				env.repaint();
				Thread.sleep(30);
				
				totalTimeStep++;
				
				if(totalTimeStep >= 100000) {
					agent.updateTargetNetwork();
				}
				else if(totalTimeStep % 50 == 0) {
					agent.updateTargetNetwork();
				}
				
				int action = agent.chooseAction(currentState);
				currentState = env.step(action).clone();
				highestScore = Math.max(highestScore, env.getScore());

				if(env.isDone()) {
					Thread.sleep(300);
					break;
				}
			}
			System.out.println("Score = " + env.getScore());
		}
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
	
	public void saveDataTrained(String path, NeuralNetwork network) throws FileNotFoundException {
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
		
		pr.println("Record Score: " + recordScore);
		
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
