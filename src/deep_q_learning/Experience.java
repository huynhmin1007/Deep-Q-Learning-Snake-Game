package deep_q_learning;

public class Experience extends Object {
	
	private State state;
	private int action;
	private double reward;
	private State nextState;
	private boolean done;
	
	public Experience(State state, int action, double reward, State nextState, boolean done) {
		super();
		this.state = state;
		this.action = action;
		this.reward = reward;
		this.nextState = nextState;
		this.done = done;
	}

	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}

	public int getAction() {
		return action;
	}

	public void setAction(int action) {
		this.action = action;
	}

	public double getReward() {
		return reward;
	}

	public void setReward(double reward) {
		this.reward = reward;
	}

	public State getNextState() {
		return nextState;
	}

	public void setNextState(State nextState) {
		this.nextState = nextState;
	}

	public boolean isDone() {
		return done;
	}

	public void setDone(boolean done) {
		this.done = done;
	}
}
