/*************************************************************
 * RandomBot.java
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
 * This class is a Random Bot, all of its decision are random 
 *
 */
public class RandomBot extends Player {

	Random rnd;

	
	/**
	 * RandomBot.java constructor 
	 * 
	 * @param role		The bots role (Liberal, Fascist or Hitler)
	 * @param state		it's player number (1-5)
	 */
	public RandomBot(String role, int state) {
		super(role, state);
		rnd = new Random(System.currentTimeMillis());
	}

	
	/**
	 * int chooseChancellor()
	 * The action to choose a Chancellor which is randomly chosen
	 * 
	 * @param president 		President's player number
	 * @param lastChancellor 	Last round's chancellor player number
	 * @param players 			A list of all the players still in the game
	 * @return 	The player number of the chosen Chancellor
	 */
	public int chooseChancellor(int president, int lastChancellor, ArrayList<Integer> players) {
		int choose = rnd.nextInt(5) + 1;
		while (choose == president || choose == lastChancellor || !players.contains(choose)) {
			choose = rnd.nextInt(5) + 1;
		}
		return choose;
	}

	
	/**
	 * String vote()
	 * Vote on the Presidential election (President + Chancellor)
	 * 
	 * @param president 	President
	 * @param chancellor 	Chancellor
	 * @return "Y" for yes and "N" for No
	 */
	public String vote(int president, int chancellor) {
		int r = rnd.nextInt(7);
		if (r < 4) {
			return "Y";
		} else {
			return "N";
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

		if (role.equals("Liberal")) {
			for (int i = 0; i < 2; i++) {
				if (policies[i].equals("Fascist")) {
					return i;
				}
			}
		}
		return rnd.nextInt(3);
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
		int wantToVeto = rnd.nextInt(6);
		if(veto && wantToVeto<4) {
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
		}
		return rnd.nextInt(2);
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
		int wantToVeto = rnd.nextInt(7);
		if(wantToVeto < 3) {
			return true;
		} else {
			return false;
		}
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
		
		return results[rnd.nextInt(4)];
		
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
		

		return results[rnd.nextInt(3)];
	}
}
