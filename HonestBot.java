/*************************************************************
 * HonestBot.java
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
import java.util.Random;

/**
 * This class is a simple honest bot, used to train the ML Bot
 * 
 */
public class HonestBot extends Player{

	private int otherFascist;
	Random rnd;
	
	
	/**
	 * HonestBot()
	 * HonestBot.java constructor
	 * 
	 * @param role 		The bots role (Liberal, Fascist or Hitler)
	 * @param state		it's player number (1-5)
	 */
	public HonestBot(String role, int state) {
		super(role, state);
		rnd = new Random(System.currentTimeMillis());
	}
	
	
	/**
	 * receiveRole()
	 * The Bot receives the information of the Fascists, but only stores the information if he himself belongs to the Fascist party
	 * 
	 * @param showRoles 	The player numbers of both fascists
	 */
	public void receiveRole(ArrayList<Integer> showRoles) {
		if(role.equals("Hitler")) {
			otherFascist = showRoles.get(0);
		} else if(role.equals("Fascist")) {
			otherFascist = showRoles.get(1);
		} else {
			otherFascist = -1;
		}
	}
	
	
	/**
	 * int chooseChancellor()
	 * The action to choose a Chancellor, this bot always tries to choose the other fascist as the chancellor if he himself is either the Fascist or Hitler
	 * If he's a liberal or a fascist who cannot choose the other fascist, he'll choose someone at random.
	 * 
	 * @param president 		President's player number
	 * @param lastChancellor 	Last round's chancellor player number
	 * @param players 			A list of all the players still in the game
	 * @return 	The player number of the chosen Chancellor
	 */
	public int chooseChancellor(int president, int lastChancellor, ArrayList<Integer> players) {
		int choose;
		if(role.equals("Liberal")) {
			choose = rnd.nextInt(5)+1;
		} else {
			choose = otherFascist;
		}
		while(choose == president || choose == lastChancellor || !players.contains(choose)) {
			choose = rnd.nextInt(5)+1;
		}
		return choose;
	}
	
	
	/**
	 * String vote()
	 * Vote on the Presidential election (President + Chancellor)
	 * It is a random chosen vote with a higher probability of choosing yes
	 * 
	 * @param president 	President
	 * @param chancellor 	Chancellor
	 * @return "Y" for yes and "N" for No
	 */
	public String vote(int president, int chancellor) {
		if(president == state || chancellor == state) {
			return "Y";
		}
		else if((role.equals("Fascist") || role.equals("Hitler")) && (president == otherFascist || chancellor == otherFascist)) {
			return "Y";
		} else {
			int r = rnd.nextInt(7);
			if(r < 4) {
				return "Y";
			} else {
				return "N";
			}
		}
	}
	
	
	/**
	 * int discardCard()
	 * The action to discard a card if the bot is the president
	 * 
	 * @param one 	first card
	 * @param two	second card
	 * @param three third card
	 * @return the card the bot wants to discard
	 */
	public int discardCard(String one, String two, String three) {
		String[] policies = new String[3];
		policies[0] = one;
		policies[1] = two;
		policies[2] = three;
		
		if(role.equals("Liberal")) {
			for(int i = 0; i < 2; i++) {
				if(policies[i].equals("Fascist")) {
					return i;
				}
			}
		} else {
			for(int i = 0; i < 2; i++) {
				if(policies[i].equals("Liberal")) {
					return i;
				}
			}
		}
		
		return 2;
	}
	
	
	/**
	 * int discardCard()
	 * The action to discard a card if the bot is the Chancellor
	 * 
	 * @param one 	first card
	 * @param two	second card
	 * @return the card the bot wants to discard or veto if the opportinity so presents
	 */
	public int discardCard(String one, String two, boolean veto) {
		if(veto) {
			if(role.equals("Liberal")) {
				if(one.equals("Fascists") && two.equals("Fascist")) {
					return 2;
				}
			} else {
				if(one.equals("Liberal") && two.equals("Liberal")) {
					return 2;
				}
			}
		}
		if(role.equals("Liberal")) {
			if(one.equals("Fascist")) {
				return 0;
			}
		} else {
			if(one.equals("Liberal")) {
				return 0;
			}
		}
		return 1;
	}
	
	
	/**
	 * boolean voteVeto
	 * Voting if the bot accepts the veto proposed by the chancellor
	 * 
	 * @param one 	first card
	 * @param two 	second card
	 * @return true if it agrees with the veto, false otherwise
	 */
	public boolean voteVeto(String one, String two) {
		if(role.equals("Liberal")) {
			if(one.equals("Fascist") && two.equals("Fascist")) {
				return true;
			}
		} else {
			if(one.equals("Liberal") && two.equals("Liberal")) {
				return true;
			}
		}
		return false;
	}
	
	
	/**
	 * int killPlayer()
	 * The action to kill a player
	 * 
	 * @param players 	The players still in the game
	 * @return The player the bot wants to kill
	 */
	public int killPlayer(ArrayList<Integer> players) {
		ArrayList<Integer> n_pl = new ArrayList<Integer>(players);
		n_pl.remove((Object) state);
		if(!role.equals("Liberal")) {
			n_pl.remove((Object) otherFascist);
		}
		return n_pl.get(rnd.nextInt(n_pl.size()));
	}
	
	
	/**
	 * String tellCards
	 * Telling what cards the bot had in his "hand" (this method is for the president)
	 * 
	 * @param one		first card
	 * @param two 		second card
	 * @param three 	third card
	 * @param enacter	the policy that was enacted
	 * @return What cards the player had in his "hand"
	 */
	public String tellCards(int one, int two, int three, int enacted) {
		int _pol[] = new int[3];
		_pol[0] = one;
		_pol[1] = two;
		_pol[2] = three;
		
		int crd = 0;
		for(int i = 0; i < 3; i++) {
			crd += _pol[i];
		}
		
		String[] results = new String[4];
		
		results[0] = "3 Liberals 0 Fascists";
		results[1] = "2 Liberals 1 Fascist";
		results[2] = "1 Liberal 2 Fascists";
		results[3] = "0 Liberals 3 Fascists";
		
		if(crd == 3) {
			return results[0];
		} else if(crd == 1) {
			return results[1];
		} else if(crd == -1) {
			return results[2];
		} else {
			return results[3];
		}
		
	}
	
	/**
	 * String tellCards
	 * Telling what cards the bot had in his "hand" (this method is for the chancellor)
	 * 
	 * @param one		first card
	 * @param two 		second card
	 * @param enacter	the policy that was enacted
	 * @return What cards the player had in his "hand"
	 */
	public String tellCards(int one, int two, int enacted) {
		int[] _pol = new int[2];
		_pol[0] = one;
		_pol[1] = two;
		
		int crd = 0;
		
		for(int i = 0; i < 2; i++) {
			crd += _pol[i];
		}
		
		String[] results = new String[3];
		results[0] = "2 Liberals 0 Fascists";
		results[1] = "1 Liberal 1 Fascists";
		results[2] = "0 Liberals 2 Fascists";
		
		if(crd == 2) {
			return results[0];
		} else if(crd == 0) {
			return results[1];
		} else {
			return results[2];
		}
	}
	
}
