/*************************************************************
 * Game.java
 * Secret Hitler
 *
 * MSc Computer Games Systems
 * Nottingham Trent University
 * Major Project
 * 
 * Dario Hermann N0773470
 * 2017/18
 *************************************************************/

import java.util.ArrayList;
import java.util.Collections;

/**
 * This class serves the purpose as the starter of the games variables and keeps up with some of them
 */
public class Game {
	protected ArrayList<Player> players;
	protected ArrayList<Integer> playersState = new ArrayList<Integer>();
	protected ArrayList<String> policiesDeck = new ArrayList<String>();
	protected ArrayList<String> roles = new ArrayList<String>();
	protected int libPolicies;
	protected int fasPolicies;
	protected int electionTracker;
	
	/**
	 * Game()
	 * Game.java constructor
	 * Initialises a new game and  creates the roles
	 */
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
	}
	
	
	/**
	 * int start()
	 * it starts the game, only use is in GameRun.java
	 */
	public int start() {
		return -1;
	}
	
	
	/**
	 * void shuffleCards()
	 * As the name suggests this method shuffles the draw pile
	 */
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

	
	/**
	 * ArrayList<String> getRoles()
	 * @return the list of roles to be given to the players in Main.java
	 */
	public ArrayList<String> getRoles(){
		return roles;
	}

	
	/**
	 * void MakePlayersState()
	 * Creates an ArrayList with the players available in the game,
	 * this is useful to keep up with who is still alive in the game
	 * @param pl a list of all the players.
	 */
	public void makePlayersState(ArrayList<Player> pl) {
		for(int i = 0; i < pl.size(); i++) {
			playersState.add(pl.get(i).getState());
		}
		players = new ArrayList<Player>(pl);
	}
}
