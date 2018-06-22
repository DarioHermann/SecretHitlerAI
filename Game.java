import java.util.ArrayList;
import java.util.Collections;

public class Game {
	static protected ArrayList<Player> players;
	static protected ArrayList<Integer> playersState = new ArrayList<Integer>();
	static protected ArrayList<String> policiesDeck = new ArrayList<String>();
	static protected ArrayList<String> roles = new ArrayList<String>();
	static protected int libPolicies;
	static protected int fasPolicies;
	static protected int electionTracker;
	
	public Game() {
		
		libPolicies = 0;
		fasPolicies = 0;
		electionTracker = 0;
		
		shuffleCards();
		
		roles.add("Hitler");
		roles.add("Fascist");
		for(int i = 0; i < 3; i++) {
			roles.add("Liberal");
			Collections.shuffle(roles);
		}
		Collections.shuffle(roles);
		System.out.println(roles);
	}
	
	public void start() {
	}
	
	protected void shuffleCards() {
		for(int i = 0; i < 11-fasPolicies; i++) {
			policiesDeck.add("Fascist");
		}
		for(int i = 0; i < 6-libPolicies; i++) {
			policiesDeck.add("Liberal");
			Collections.shuffle(policiesDeck);
		}
		Collections.shuffle(policiesDeck);
	}

	
	public ArrayList<String> showRole(ArrayList<Player> pl) {
		ArrayList<String> result = new ArrayList<String>();
		for(int i = 0; i < 3; i++) {
			result.add("hey");
		}
		for(int i = 0; i < pl.size(); i++) {
			playersState.add(pl.get(i).getState());
			if(pl.get(i).getRole().equals("Fascist")) {
				result.set(1, Integer.toString(pl.get(i).getState()));
			}
			else if(pl.get(i).getRole().equals("Hitler")) {
				result.set(2, Integer.toString(pl.get(i).getState()));
			}
			
			if(pl.get(i).getState() == 0) {
				result.set(0, pl.get(i).getRole());
			}
		}
		players = new ArrayList<Player>(pl);
		System.out.println(result);
		return result;
	}
	
	
	public ArrayList<String> getRoles(){
		return roles;
	}
}
