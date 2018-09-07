/*************************************************************
 * GameRun.java
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
import java.util.Scanner;


/**
 * This class is an extension of Game.java and manages all the actions of the games run cycle
 */
public class GameRun extends Game{
	
	private int lastPresident; //Current or Last (before choosing a new one)
	private int lastChancellor;
	private boolean vetoPowerOn;
	private boolean power; //Presidential Power is on or off
	private String thePlay; //thePlay is a variable to send to every player at the end of the round so players can see what happened during a round

	private boolean gotKilled = false; //If someone gets killed during a round gotKilled get a true value
	private int whoGotKilled = 0; //and if gotKilled == true, this variable also get a value
	
	
	/**
	 * GameRun()
	 * GameRun.java's constructor
	 */
	public GameRun() {
		super();
		vetoPowerOn = false;
	}
	
	
	/**
	 * int start()
	 * This method starts the game and also start every round of the game
	 */
	public int start() {
		Scanner sc = new Scanner(System.in);
		power = false;
		int chancellor = 0;
		int president = -1;
		int gamewin = -2;
		lastChancellor = 0;
		do{
			do {
				thePlay = "";
				lastPresident = president;
				if(gotKilled) {
					if(whoGotKilled < president) { //this prevent that when, for example, player 2 get killed and the next president was to be 4, the presidency doesn't go to 5 instead
						president--;
					}
					gotKilled = false;
				}
				president = (president+1)%players.size(); //choosing the next president in the line
				chancellor = chooseChancellor(president); //calls the method to choose a chancellor
			}while(chancellor == -1); //while The voting didn't go succesfull and election tracker is still not 3
			if(chancellor != 6) { //If the voting went successfully
				if(fasPolicies >= 3 && players.get(playersState.indexOf(chancellor)).getRole().equals("Hitler")) { //If at least 3 fascist policies have been enacted and Hitler was chosen aas the chancellor
					thePlay += ",HW";
					System.out.println("Hitler was elected as the Chancellor after three fascist policies have been enacted");
					fasPolicies = 6;
				} else {
					if(fasPolicies >= 3) {
						for(int i = 0; i < players.size(); i++) {
							players.get(i).isNotHitler(chancellor); //if at least 3 fascist policies have been eacted and Hitler was not chosen as the Chancellor, the chancellor can be confirmed as not being Hitler
						}
					}
					choosePolicies(president, chancellor); //Calls the method to discard the cards
					checkPresidentialPower(president); //Calls the method to verify if a presidential can be used
				}
			}
			gamewin = checkWin(); //Checks if any of the teams have won
			System.out.println("\n\n");
		}while(gamewin<1); //If the return value of checkWin is smaller than 1, no team has won yet and the game continues, going back to the next president choosing the Chancellor
		if(gamewin == 1) { //Liberals win
			System.out.println("LIBERALS WON!");
			return 1;
		} else if(gamewin == 2){ //fascist wins
			System.out.println("FASCISTS WON!");
			return 2;
		} else { //error
			return 0;
		}
	}
	
	
	/**
	 * int chooseChancellor() 
	 * is used for the president to choose the Chancellor and everyone vote on the decision made
	 * 
	 * @param pl The president
	 * @return The Chancellor's player number if the voting was successful, -1 if the majority of the votes weren't yes and the election tracker < 3, or it can return 6 if the voting didn't go successfully but the election tracker reached the limit
	 */
	private int chooseChancellor(int pl) {
		int chosen;
		thePlay += playersState.get(pl);
		System.out.println("player " + playersState.get(pl) + " you're the President, choose the Chancellor");
		if(!playersState.contains(lastChancellor)) { //This is only affected on the first round and if the Chancellor of the last round is killed
			lastChancellor = playersState.get(pl);
		}
		chosen = players.get(pl).chooseChancellor(playersState.get(pl), lastChancellor, playersState); //calls the players method to choose the Chancellor
		lastChancellor = chosen;
		thePlay += "," + chosen;
		System.out.println("The President chose Player " + chosen + " as the Chancellor, let's vote this decision\nDo you agree with this decision? Ja!/Nein!(Y/N)");
		ArrayList<String> votes = new ArrayList<String>();
		for(int i = 0; i < players.size(); i++) {
			votes.add(players.get(i).vote(playersState.get(pl), chosen));
		}
		int ja = Collections.frequency(votes, "Y");
		int nein = Collections.frequency(votes, "N");
		String print = ja + " voted Ja! and " + nein + " voted Nein!\nTherefore, ";
		if(ja > nein) {
			thePlay += ",Y";
			print += "the President and the Chancellor have been elected";
			System.out.println(print);
			return chosen;
		} else {
			thePlay += ",N";
			print += "the Election Tracker will move one space and a new President and Chancellor have to be chosen";
			System.out.println(print);
			electionTracker++;
			if(electionTracker == 3) {
				if(policiesDeck.size() < 1) {
					shuffleCards();
				}
				String policy = policiesDeck.remove(0);
				enactPolicy(policy);
				power = false;
				return 6;
			}
			for(int i = 0; i < players.size(); i++) { //This has to happens here since if the voting didn't succeed and the election tracker didn't reach it's limit, enactPolicy doesn't get called this round.
				if(players.get(i).getTypeOfPlayer() == 1) {
					players.get(i).checkPlay(thePlay);
				}
			}
			return -1;
		}
	}
	
	
	/**
	 * choosePolicies()
	 * The President and Chancellor discard cards and tell other players what cards they had in their hand
	 * 
	 * @param president President's player number
	 * @param chancellor Chancellor's player number
	 */
	private void choosePolicies(int president, int chancellor) {
		if(policiesDeck.size() < 3) {
			shuffleCards();
		}
		boolean veto = false;
		int pone = policiesDeck.get(0).equals("Liberal") ? 1 : -1;
		int ptwo = policiesDeck.get(1).equals("Liberal") ? 1 : -1;
		int pthree = policiesDeck.get(2).equals("Liberal") ? 1 : -1;

		System.out.println("President, choose the card you want to discard:");
		int discard = players.get(president).discardCard(policiesDeck.get(0), policiesDeck.get(1), policiesDeck.get(2));
		policiesDeck.remove(discard);
		int cone = policiesDeck.get(0).equals("Liberal") ? 1 : -1;
		int ctwo = policiesDeck.get(1).equals("Liberal") ? 1 : -1;
		discard = players.get(playersState.indexOf(chancellor)).discardCard(policiesDeck.get(0), policiesDeck.get(1), vetoPowerOn);
		if(discard == 2) { //If the Chancellor chose 2 means that he tried to veto
			veto = players.get(president).voteVeto(policiesDeck.get(0), policiesDeck.get(1));
			if(veto) { //If the president accepter the veto
				thePlay +=",V";
				electionTracker++;
				if(electionTracker == 3) {
					if(policiesDeck.size() < 1) {
						shuffleCards();
					}
					String policy = policiesDeck.remove(0);
					enactPolicy(policy);
					policiesDeck.remove(0);
				}
				discard = 3;
			} else if(vetoPowerOn){ //else if the president didn't accept the veto
				thePlay +=",NV";
				System.out.println("The President didn't accept your veto, Chancellor choose one of the policies in you hand");
				discard = players.get(playersState.indexOf(chancellor)).discardCard(policiesDeck.get(0), policiesDeck.get(1), false);
			}
		}
		if(discard < 3) { //this if only doesn't happen if the president accepts a veto
			policiesDeck.remove(discard);
			String policy = policiesDeck.remove(0);
			enactPolicy(policy);
			System.out.println("President, tell what cards you had:");
			String presTold = players.get(president).tellCards(pone, ptwo, pthree, policy.equals("Liberal") ? 1 : -1);
			System.out.println("Chancellor, tell what cards you had:");
			String chancTold = players.get(playersState.indexOf(chancellor)).tellCards(cone, ctwo, policy.equals("Liberal") ? 1 : -1);
			
			for(int i = 0; i < players.size(); i++) {
				if(i != president && i != playersState.indexOf(chancellor)) {
					players.get(i).cardsTold(playersState.get(president), chancellor, presTold, chancTold);
				} else if(i != president) {
					players.get(i).cardsTold(playersState.get(president), presTold, chancTold, cone, ctwo);
				} else {
					players.get(i).cardsTold(chancellor, presTold, chancTold, pone, ptwo, pthree);
				}
			}
		}
		
	}
	
	
	/**
	 * enactPolicy()
	 * This method gets called to enact a policy and active powers
	 * 
	 * @param policy What policy the Chancellor didn't choose to discard or tne policy that came from top of the draw pile in case of the election tracker reaching its limit
	 */
	private void enactPolicy(String policy) {
		if(policy.equals("Liberal")) {
			thePlay +=",L";
			libPolicies++;
			System.out.println("Liberal policy enacted (" + libPolicies + "/5)");
			for(int i = 0; i < players.size(); i++) {
				players.get(i).policyEnacted(1);
			}
		} else {
			thePlay +=",F";
			fasPolicies++;
			power = true;
			System.out.println("Fascist policy enacted (" + fasPolicies + "/6)");
			for(int i = 0; i < players.size(); i++) {
				players.get(i).policyEnacted(-1);
			}
		}
		electionTracker = 0;
	}
	
	
	/**
	 * int checkWin()
	 * Checks is either liberals of fascists have won or if the game hasn't finished yet and also send the play of the game to all the players
	 * 
	 * @return if 1, Liberal won, if 2, Fascists won otherwise, the game continues
	 */
	private int checkWin() {
		int result;
		if(libPolicies == 5) {
			thePlay += ",LW";
			result = 1;
		} else if (fasPolicies == 6) {
			thePlay += ",FW";
			result = 2;
		} else {
			result = 0;
		}
		
		for(int i = 0; i < players.size(); i++) {
			if(players.get(i).getTypeOfPlayer() == 1) {
				players.get(i).checkPlay(thePlay);
			}
		}
		return result;
	}
	
	
	/**
	 * checkPresidentialPowers()
	 * This method checks if the President has any presidential power, it only occurs if the chancellor enacted a fascist policy 
	 * after 2 fascist policies have been enacted and before 6
	 * 
	 * @param president President's player number
	 */
	private void checkPresidentialPower(int president) {
		if(power && fasPolicies == 3) { //If 3 fascist policies have been enacted, the president can check the top 3 cards of the draw pile
			if(policiesDeck.size() < 3) {
				shuffleCards();
			}
			players.get(president).checkThreeCards(policiesDeck.get(0), policiesDeck.get(1), policiesDeck.get(2));
		}
		else if(power && (fasPolicies >= 4 && fasPolicies < 6)) { //If 4 or 5 fascist policies have been enacted, the president gets the right to kill a player
			int eliminate = players.get(president).killPlayer(playersState);
			eliminate = playersState.indexOf(eliminate);
			gotKilled = true;
			whoGotKilled = eliminate;
			System.out.println(playersState + "\n" + players);
			int psk = playersState.remove((int) eliminate);
			thePlay += "," + psk;
			Player shot = players.remove(eliminate);
			System.out.println(playersState + "\n" + players);
			if(shot.getRole().equals("Hitler")) { //If the player killed is Hitler, Liberals win
				thePlay+=",HD";
				libPolicies = 5;
				System.out.println("Congrats Liberals, Hitler has been shot!");
			}
		}
		if(fasPolicies == 5) { //if 5 fascist policies have been enacted, the power to veto gets activated
			vetoPowerOn = true;
		}
		power = false;
	}
	
}
