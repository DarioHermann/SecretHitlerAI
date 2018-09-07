/*************************************************************
 * Human.java
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

/**
 * Class for the human players 
 *
 */
public class Human extends Player{

	/**
	 * Human()
	 * Human.java constructor
	 * 
	 * @param role 		Its role (Liberal, Fascist or Hitler)
	 * @param state		Its player number
	 */
	public Human(String role, int state) {
		super(role, state);
		System.out.println("You're player " + state);
	}
	
	
	/**
	 * receiveRole()
	 * The human player receives the information of the Fascists, but can only see who the fascists are if he himself belongs to the Fascist party
	 * 
	 * @param showRoles 	The player numbers of both fascists
	 */
	public void receiveRole(ArrayList<Integer> showRoles) {
		if(role.equals("Liberal")) {
			System.out.println("You are a Liberal");
		} else if(role.equals("Hitler")) {
			System.out.println("You are Hitler and the other Fascist is Player "+ showRoles.get(0));
		} else {
			System.out.println("You are a Fascist and Player " + showRoles.get(1) + " is Hitler");
		}
	}
	
	
	/**
	 * int chooseChancellor()
	 * The action to choose a Chancellor
	 * 
	 * @param president 		President's player number
	 * @param lastChancellor 	Last round's chancellor player number
	 * @param players 			A list of all the players still in the game
	 * @return 	The player number of the chosen Chancellor
	 */
	public int chooseChancellor(int president, int lastChancellor, ArrayList<Integer> players) {
		int chosen;
		chosen = sc.nextInt();
		boolean canChoose = false;
		if(players.contains(chosen)) {
			canChoose = true;
		}
		while(!canChoose || (chosen == lastChancellor || chosen == president)){		
			if(chosen == president) {
				System.out.println("You cannot choose yourself as the Chancellor");
			}else if(chosen == lastChancellor) {
				System.out.println("You cannot choose Player " + lastChancellor + " as the Chancellor because he was the Chancellor last round");
			} else {
				System.out.println("That player doesn't exist or is already dead");
			}
			System.out.println("Choose another player");
			chosen = sc.nextInt();
			if(players.contains(chosen)) {
				canChoose = true;
			}
		}
		return chosen;
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
		String v = sc.next();
		v=v.toUpperCase();
		while(!v.equals("Y") && !v.equals("N")) {
			System.out.println("That is not a valid option, please choose \"Y\" for Ja!(Yes) or \"N\" for Nein!(No)");
			v = sc.next();
			v=v.toUpperCase();
		}
		return v;
	}
	
	
	/**
	 * int discardCard()
	 * The action to discard a card if the player is the president
	 * 
	 * @param one 	first card
	 * @param two	second card
	 * @param three third card
	 * @return the card the human player wants to discard
	 */
	public int discardCard(String one, String two, String three) {
		System.out.println(one + "(1)\n"+ two + "(2)\n" + three + "(3)");
		int discard = sc.nextInt();
		while(discard > 3 || discard < 1) {
			System.out.println("Please choose a valid option! 1/2/3");
			discard = sc.nextInt();
		}
		return discard-1;
	}
	
	
	/**
	 * int discardCard()
	 * The action to discard a card if the player is the Chancellor
	 * 
	 * @param one 	first card
	 * @param two	second card
	 * @return the card the human player wants to discard or veto if the opportinity so presents
	 */
	public int discardCard(String one, String two, boolean veto) {
		System.out.println(one + "(1)\n"+ two + "(2)");
		if(veto) {
			System.out.println("The Veto Power is On, if you don't like any of the policies and want to discard them both choose 3");
		}
		int discard = sc.nextInt();
		while((!veto && discard == 3) || (discard > 3 || discard < 1)) {
			System.out.print("Please choose a valid option! 1/2");
			if(veto) {
				System.out.print("/3");
			}
			System.out.println("");
			
			discard = sc.nextInt();
		}
		return discard-1;
	}
	
	
	/**
	 * boolean voteVeto
	 * Voting if the human player accepts the veto proposed by the chancellor
	 * 
	 * @param one 	first card
	 * @param two 	second card
	 * @return true if it agrees with the veto, false otherwise
	 */
	public boolean voteVeto(String one, String two) {
		System.out.println("President, do you want to veto? The policies are " + one + " and " + two + " Y/N");
		String vote = sc.next();
		vote = vote.toUpperCase();
		while(!vote.equals("Y") && !vote.equals("N")) {
			System.out.println("Not a valid input, please input \"Y\" for Yes and \"N\" for No");
			vote = sc.next();
			vote = vote.toUpperCase();
		}
		if(vote.equals("Y")) {
			return true;
		} else {
			return false;
		}
	}
	
	
	/**
	 * checkTreeCards
	 * Checks the three cards on top of the draw pile
	 * 
	 * @param one 		first card
	 * @param two 		second card
	 * @param three		third card
	 */
	public void checkThreeCards(String one, String two, String three) {
		System.out.println(one+"\n"+two+"\n"+three);
	}
	
	
	/**
	 * int killPlayer()
	 * The action to kill a player
	 * 
	 * @param players 	The players still in the game
	 * @return The player the human player wants to kill
	 */
	public int killPlayer(ArrayList<Integer> players) { //MAIS TARDE MUDAR POR CAUSA DA CENA DO PLAYER 0
		System.out.println("President, choose one player to kill");
		int choice = sc.nextInt();
		boolean canKill = false;
		if(players.contains(choice) && choice != state) {
			canKill = true;
		}
		while(!canKill) {
			System.out.println("That player doesn't exist, was killed or is you!");
			choice = sc.nextInt();
			canKill = players.contains(choice);			
		}
		
		return choice;
	}
	
	/**
	 * String tellCards
	 * Telling what cards the human player had in his "hand" (this method is for the president)
	 * 
	 * @param one		first card
	 * @param two 		second card
	 * @param three 	third card
	 * @param enacter	the policy that was enacted
	 * @return What cards the player had in his "hand" (obviously one can lie about it)
	 */
	public String tellCards(int one, int two, int three, int enacted) {
		
		
		String[] results = new String[4];
		
		results[0] = "3 Liberals 0 Fascists";
		results[1] = "2 Liberals 1 Fascist";
		results[2] = "1 Liberal 2 Fascists";
		results[3] = "0 Liberals 3 Fascists";

		return results[sc.nextInt()];
	}
	
	/**
	 * String tellCards
	 * Telling what cards the human player had in his "hand" (this method is for the chancellor)
	 * 
	 * @param one		first card
	 * @param two 		second card
	 * @param enacter	the policy that was enacted
	 * @return What cards the player had in his "hand" (obviously one can lie about it)
	 */
	public String tellCards(int one, int two, int enacted) {
		
		String[] results = new String[3];
		results[0] = "2 Liberals 0 Fascists";
		results[1] = "1 Liberal 1 Fascists";
		results[2] = "0 Liberals 2 Fascists";
		
		return results[sc.nextInt()];
	}
	
	/**
	 * cardsTold()
	 * Cards Told by the president and chancellor (when the player is none of those two)
	 * 
	 * @param president		president player number
	 * @param chancellor	chancellor player number
	 * @param presTold		what the president told
	 * @param chancTold 	what the chancellor told
	 */
	public void cardsTold(int president, int chancellor, String presTold, String chancTold) {
		System.out.println("The president, Player " + president + " told he had: " + presTold);
		System.out.println("The chancellor, Player " + chancellor + " told he had: " + chancTold);
	}

	
	/**
	 * cardsTold()
	 * Cards Told by the president(when the player is the chancellor)
	 * 
	 * @param president		president player number
	 * @param presTold		what the president told
	 * @param chancTold 	what the chancellor told
	 * @param cone			first card
	 * @param ctwo			second card
	 */
	public void cardsTold(int president, String presTold, String chancTold, int cone, int ctwo) {
		System.out.println("The president, Player " + president + " told he had: " + presTold);
		
	}

	
	/**
	 * cardsTold()
	 * Cards Told by the chancellor(when the player is the president)
	 * 
	 * @param chancellor	chancellor player number
	 * @param presTold		what the president told
	 * @param chancTold 	what the chancellor told
	 * @param cone			first card
	 * @param ctwo			second card
	 * @param cthree		third card
	 */
	public void cardsTold(int chancellor, String presTold, String chancTold, int cone, int ctwo, int cthree) {
		System.out.println("The chancellor, Player " + chancellor + " told he had: " + chancTold);
	}
		
}
