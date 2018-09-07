/*************************************************************
 * Player.java
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
import java.util.Scanner;


/**
 * The parent class of all the types of players. 
 *
 */
public class Player {
	protected String role;
	protected int state;
	protected Scanner sc;
	protected int typeOfPlayer;
	
	
	/**
	 * Player.java constructor
	 * 
	 * @param role		the player's role (Liberal, Fascist or Hitler)
	 * @param state		The player's player number (1-5)
	 */
	public Player(String role, int state) {
		this.role = role;
		this.state = state;
		typeOfPlayer = 0;
		sc = new Scanner(System.in);
	}
	
	
	/**
	 * int getTypeOfPlayer
	 * 
	 * @return value to control which Players receive the round play
	 */
	public int getTypeOfPlayer() {
		return typeOfPlayer;
	}
	
	
	/**
	 * String getRole()
	 * 
	 * @return	player's role
	 */
	public String getRole() {
		return role;
	}
	
	
	/**
	 * int getState()
	 * 
	 * @return	return number of player
	 */
	public int getState() {
		return state;
	}
	
	
	/**
	 * This method is not used in this class
	 */
	public int chooseChancellor(int president, int lastChancellor, ArrayList<Integer> players) {
		/*int chosen;
		chosen = sc.nextInt();
		boolean canChoose = false;
		if(players.contains(chosen)) {
			canChoose = true;
		}
		while(!canChoose && (chosen == lastChancellor || chosen == president)){		
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
		return chosen;*/
		return 500;
	}
	
	
	/**
	 * This method is not used in this class
	 */
	public String vote(int president, int chancellor) {
		/*String v = sc.next();
		v=v.toUpperCase();
		while(!v.equals("Y") && !v.equals("N")) {
			System.out.println("That is not a valid option, please choose \"Y\" for Ja!(Yes) or \"N\" for Nein!(No)");
			v = sc.next();
			v=v.toUpperCase();
		}
		return v;*/
		return "heyo";
	}
	
	
	/**
	 * This method is not used in this class
	 */
	public int discardCard(String one, String two, String three) {
		/*System.out.println(one + "(1)\n"+ two + "(2)\n" + three + "(3)");
		int discard = sc.nextInt();
		while(discard > 3 || discard < 1) {
			System.out.println("Please choose a valid option! 1/2/3");
			discard = sc.nextInt();
		}
		return sc.nextInt()-1;*/
		return 500;
	}
	
	
	/**
	 * This method is not used in this class
	 */
	public int discardCard(String one, String two, boolean veto) {
		/*System.out.println(one + "(1)\n"+ two + "(2)");
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
		return discard-1;*/
		return 500;
	}
	
	
	/**
	 * This method is not used in this class
	 */
	public boolean voteVeto(String one, String two) {
		/*System.out.println("President, do you want to veto? The policies are " + one + " and " + two + " Y/N");
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
		}*/
		return false;
	}
	
	
	/**
	 * This method is not used in this class
	 */
	public void checkThreeCards(String one, String two, String three) {
		//System.out.println(one+"\n"+two+"\n"+three);
	}
	
	
	/**
	 * This method is not used in this class
	 */
	public int killPlayer(ArrayList<Integer> players) { //MAIS TARDE MUDAR POR CAUSA DA CENA DO PLAYER 0
		/*System.out.println("President, choose one player to kill");
		int choice = sc.nextInt();
		boolean canKill = false;
		players.remove(state);
		if(players.contains(choice)) {
			canKill = true;
		}
		while(!canKill) {
			System.out.println("That player doesn't exist, was killed or is you!");
			choice = sc.nextInt();
			canKill = players.contains(choice);			
		}
		
		return choice;*/
		return 500;
	}

	
	/**
	 * This method is not used in this class
	 */
	public void receiveRole(ArrayList<Integer> fascists) {
		// TODO Auto-generated method stub
		
	}
	
	
	/**
	 * This method is not used in this class
	 */
	public void checkPlay(String play) {
		
	}

	
	/**
	 * This method is not used in this class
	 */
	public float getTotalCost() {
		// TODO Auto-generated method stub
		return -1;
	}
	
	
	/**
	 * This method is not used in this class
	 */
	public void didIWin(boolean b) {
		
	}

	
	/**
	 * This method is not used in this class
	 */
	public String tellCards(int pone, int ptwo, int pthree, int enacted) {
		// TODO Auto-generated method stub
		return "hello";
	}

	
	/**
	 * This method is not used in this class
	 */
	public String tellCards(int cone, int ctwo, int enacted) {
		// TODO Auto-generated method stub
		return null;
	}

	
	/**
	 * This method is not used in this class
	 */
	public void cardsTold(int i, int j, String presTold, String chancTold) {
		// TODO Auto-generated method stub
		
	}

	
	/**
	 * This method is not used in this class
	 */
	public void cardsTold(int i, String presTold, String chancTold, int cone, int ctwo) {
		// TODO Auto-generated method stub
		
	}

	
	/**
	 * This method is not used in this class
	 */
	public void cardsTold(int i, String presTold, String chancTold, int pone, int ptwo, int pthree) {
		// TODO Auto-generated method stub
		
	}

	
	/**
	 * This method is not used in this class
	 */
	public void policyEnacted(int i) {
		// TODO Auto-generated method stub
		
	}

	
	/**
	 * This method is not used in this class
	 */
	public void isNotHitler(int chancellor) {
		// TODO Auto-generated method stub
		
	}

}
