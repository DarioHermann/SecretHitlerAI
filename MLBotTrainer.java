/*************************************************************
 * MLBotTrainer.java
 * Secret Hitler
 *
 * MSc Computer Games Systems
 * Nottingham Trent University
 * Major Project
 * 
 * Dario Hermann N0773470
 * 2017/18
 *************************************************************/

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;


/**
 * MLBotTrainer is the class used to train 
 *
 */
public class MLBotTrainer extends Player{
	
	private int otherFascist;
	Random rnd;
	private NN nn; //Neural Network
	
//	GameVariables
	private int deckPolicies;
	private int libCardsEnacted;
	private int fasCardsEnacted;
	private int supposedLibCards;
	private int supposedFasCards;
	private int electionTracker;
	private int[] threeCards;
	private boolean saw3Cards;
	private int cardDiscarded;
	
	private float totalCost;
	private boolean won;
	private int choices;
	
	
//	NN input Variables
	private int pres;
	private int chanc;
	private int lastChanc;
	private int lastPres;
	private float lastPlayed;
	
//	Other Players
	private LinkedList<PlayerModel> otherPlayers;
	
	
	/**
	 * MLBotTrainer.java constructor
	 * 
	 * @param role 		the player's role (Liberal, Fascist or Hitler)
	 * @param state		The player's player number (1-5)
	 * @param nn		The neural network that is going to be used by this bot
	 */
	public MLBotTrainer(String role, int state, NN nn) {
		super(role, state);
		cardDiscarded = 0;
		typeOfPlayer = 1;
		totalCost = 0;
		choices = 1;
		won = false;
		saw3Cards = false;
		this.nn = nn;
		deckPolicies = 17;
		libCardsEnacted = 0;
		fasCardsEnacted = 0;
		supposedLibCards = 0;
		supposedFasCards = 0;
		electionTracker = 0;
		threeCards = new int[3];
		for(int i = 0; i < 3; i++) {
			threeCards[i] = 0;
		}
		
		pres = 0;
		chanc = 0;
		lastChanc = 0;
		lastPres = 0;
		lastPlayed = 0;
		
		rnd = new Random(System.currentTimeMillis());
		otherPlayers = new LinkedList<PlayerModel>();
		for(int i = 0; i < 5; i++) {
			otherPlayers.add(new PlayerModel());
		}
	}
	
	
	/**
	 * didIWin()
	 * It is going to update the won variable, which will add or subtract from the fitness depending on the victory condition
	 * 
	 * @param true if won false if lost
	 */
	public void didIWin(boolean v) {
		won = v;
	}
	
	
	/**
	 * float getTotalCost()
	 * 
	 * @return 	Fitness Value
	 */
	public float getTotalCost() {
		totalCost = totalCost / choices;
		if(won) {
			totalCost -= 0.5;
		} else {
			totalCost += 0.5;
		}
		return totalCost;
	}
	
	
	/**
	 * receiveRole()
	 * The Bot receives the information of the Fascists, but only stores the information if he himself belongs to the Fascist party
	 * If the bot is a fascist he will have a higher trust level for the other fascist
	 * 
	 * @param showRoles 	The player numbers of both fascists
	 */
	public void receiveRole(ArrayList<Integer> showRoles) {
		if(role.equals("Hitler")) {
			otherFascist = showRoles.get(0);
			for(int i = 0; i < 5; i++) {
				otherPlayers.get(i).setRole("Liberal");
				otherPlayers.get(i).detectHitler(false);
			}
			otherPlayers.get(otherFascist-1).setRole("Fascist");
			otherPlayers.get(otherFascist-1).setTrustLevel(2.0f);
			otherPlayers.get(state-1).detectHitler(true);
		} else if(role.equals("Fascist")) {
			otherFascist = showRoles.get(1);
			for(int i = 0; i < 5; i++) {
				otherPlayers.get(i).setRole("Liberal");
				otherPlayers.get(i).detectHitler(false);
			}
			otherPlayers.get(otherFascist-1).setRole("Hitler");
			otherPlayers.get(otherFascist-1).setTrustLevel(2.0f);
			otherPlayers.get(otherFascist-1).detectHitler(true);
		} else {
			otherFascist = 0;
		}
		
		otherPlayers.get(state-1).setRole("Me");
	}
	
	/**
	 * float[] inputNN()
	 * This is the method that will receive the values for the Input Layer of the NN and call the method to calculate the Output values
	 * vetoOn = veto is on, if 1 means the bot has to vote if he agrees with veto
	 * canVeto = the power to veto is on, so that's a choice the bot can make
	 * 
	 * @param isPres 				true if player is president, false otherwise
	 * @param isChanc				true if player is chancellor, false otherwise
	 * @param chooseChanc			true if the action required by the bot is to choose a chancellor
	 * @param abilityKillPlayer		true if the action required by the bot it to kill a player
	 * @param discCard				true if the action required by the bot is to discard a card
	 * @param vetoOn				true if the chancellor asked for a veto
	 * @param canVeto				true if there's the possibility to veto
	 * @param vote					true is the action requires by the bot is to vote on the presidency
	 * @param cardOne				value of the first card (0 if it is not to discard a card)
	 * @param cardTwo				value of the second card (0 if it is not to discard a card)
	 * @param cardThree				value of the third card (0 if it is not to discard a card or the bot is not the president)
	 * @param tellCardsInHand		true if the action required by the bit is to tell what cards it had in his hand
	 * @return	the output values of the NN
	 */
	private float[] inputNN(boolean isPres, boolean isChanc, boolean chooseChanc, boolean abilityKillPlayer, boolean discCard, boolean vetoOn, boolean canVeto, boolean vote, int cardOne, int cardTwo, int cardThree, boolean tellCardsInHand) {
		float[] inps = new float[48];
		for(int i = 0; i < inps.length; i++) {
			inps[i] = 0;
		}
		
		inps[0] = state;
		inps[1] = role.equals("Liberal") ? 1 : -1;
		inps[2] = role.equals("Hitler") ? 1 : 0;
		
		for(int i = 0; i < 5; i++) {
			inps[i*4+3] = otherPlayers.get(i).getTrustLevel();
			inps[i*4+4] = otherPlayers.get(i).getTheirTrustLevel();
			inps[i*4+5] = otherPlayers.get(i).getDeathStatus() ? 0 : 1;
			inps[i*4+6] = otherPlayers.get(i).isHeHitler() ? 1 : -1;
		}
		
		inps[23] = otherFascist;
	
		inps[24] = chooseChanc ? 1 : 0;
		inps[25] = abilityKillPlayer ? 1 : 0;
		inps[26] = discCard ? 1 : 0;
		inps[27] = canVeto ? 1 : 0;
		inps[28] = vetoOn ? 1 : 0;
		inps[29] = vote ? 1 : 0;
		
		inps[30] = pres;
		inps[31] = chanc;
		inps[32] = lastPres;
		inps[33] = lastChanc;
		inps[34] = lastPlayed;
		
		inps[35] = libCardsEnacted;
		inps[36] = fasCardsEnacted;
		inps[37] = 6 - libCardsEnacted;
		inps[38] = 11 - fasCardsEnacted;
		inps[39] = supposedLibCards;
		inps[40] = supposedFasCards;
		
		inps[41] = cardOne;
		inps[42] = cardTwo;
		inps[43] = cardThree;
		
		inps[44] = isPres ? 1 : 0;
		inps[45] = isChanc ? 1 : 0;
		inps[46] = electionTracker;
		
		inps[47] = tellCardsInHand ? 1 : 0;
		
		
		float[] output = nn.calculateNN(inps);
		analyzePlay(inps, output);
		return output;
	}
	
	
	/**
	 * isNotHitler()
	 * Confirms a player as not being Hitler after it had been elected as chancellor with 3 or more fascist policies enacted
	 * 
	 * @param chancellor 	The chancellor in question
	 */
	public void isNotHitler(int chancellor) {
		otherPlayers.get(chancellor-1).detectHitler(false);
		
	}
	
	
	/**
	 * analyzePlay()
	 * shows what the input and output values are and let's the trainer (the user of the program) decide what should have been the choices made by the bot
	 * which then gives an error value
	 * 
	 * @param inps	input nodes
	 * @param out	output nodes
	 */
	private void analyzePlay(float[] inps, float[] out) {
		int counter = 0;
		choices++;
		
		for(int i = 0; i < inps.length; i++) {
			if(i == 3 || i == 7 || i == 11 || i == 15 || i == 19 || i == 23 || i == 24 || i == 30 || i == 35 || i == 41 || i == 44 || i == 46) {
				System.out.println("\n");
				if(counter == 0) {
					System.out.println("PLAYER 1:");
				} else if(counter == 1) {
					System.out.println("PLAYER 2:");
				} else if(counter == 2) {
					System.out.println("PLAYER 3:");
				} else if(counter == 3) {
					System.out.println("PLAYER 4:");
				} else if(counter == 4) {
					System.out.println("PLAYER 5:");
				} else if(counter == 5) {
					System.out.println("OTHER FASCIST:");
				} else if(counter == 6) {
					System.out.println("ACTION TO MAKE:");
				} else if(counter == 7) {
					System.out.println("PRESIDENCY:");
				} else if(counter == 8) {
					System.out.println("POLICIES:");
				} else if(counter == 9) {
					System.out.println("CARDS:");
				} else if(counter == 10) {
					System.out.println("AM I PRESIDENT OR CHANCELLOR?:");
				} else {
					System.out.println("TRACKER AND ADMIT CARDS:");
				}
				counter++;
			}
			System.out.println(inps[i]);
		}
		
		System.out.println("\n---------------------------------------------------------\n---------------------------------------------------------\n");
		
		for(int i = 0; i < out.length; i++) {
			if(i == 5 || i == 7 || i == 11 || i == 15) {
				System.out.println("");
			}
			System.out.println(out[i]);
		}
		
		float[] correctValues = new float[18];
		
		System.out.print("How Many choices: ");
		int howMany = sc.nextInt();
		
		int[] pos = new int[howMany];
		
		for(int i = 0; i < correctValues.length; i++) {
			correctValues[i] = 0;
		}
		for(int i = 0; i < howMany; i++) {
			do{
				System.out.print("Value " + (i+1) + ": ");
				pos[i] = sc.nextInt();
			} while(pos[i] > 17);
		}
		
		for(int i = 0; i < howMany; i++) {
			correctValues[pos[i]] += (float) (1.0/howMany);
		}
		
		float[] cost = nn.calculateCost(out, correctValues);
		
		try(FileWriter fw = new FileWriter("costtable.txt", true);
	    		BufferedWriter bw = new BufferedWriter(fw);
	    		PrintWriter out2 = new PrintWriter(bw)) {
			int i = 0;
			for(i = 0; i < out.length; i++) {
				if(i==0) {
					out2.println(out[i] + "\t\t" + correctValues[i] + "\t\t" + cost[i] + "\t\t" + cost[cost.length-1]);
				} else {
					out2.println(out[i] + "\t\t" + correctValues[i] + "\t\t" + cost[i]);
				}
			}
			out2.println("\n");
			totalCost += cost[i];
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	
	/**
	 * int chooseChancellor()
	 * The action to choose a Chancellor
	 * the player that is chosen will depend on the values of the output
	 * 
	 * @param president 		President's player number
	 * @param lastChancellor 	Last round's chancellor player number
	 * @param players 			A list of all the players still in the game
	 * @return 	The player number of the chosen Chancellor
	 */
	public int chooseChancellor(int president, int lastChancellor, ArrayList<Integer> players) {
		pres = president;
		chanc = 0;
		ArrayList<Integer> pls = new ArrayList<Integer>(players);
		pls.remove((Object) president);
		pls.remove((Object) lastChanc);
		float[] out = inputNN(true, false, true, false, false, false, false, false, 0, 0, 0, false);
		int[] choose = new int[5];
		for(int i = 0; i < 5; i++) {
			choose[i] = i+1;
		}
		for(int i = 0; i < 4; i++) {
			for(int j = 0; j < 4 - i; j++) {
				if(out[j] < out[j+1]) {
					float x = out[j];
					out[j] = out[j+1];
					out[j+1] = x;
					
					int y = choose[j];
					choose[j] = choose[j+1];
					choose[j+1] = y;
				}
			}
		}
		int notHits = 0;
		for(int i =0; i < pls.size(); i++) {
			if(pls.get(i) != state) {
				if(!otherPlayers.get(pls.get(i)-1).isHeHitler()) {
					notHits++;
				}
			}
		}
		for(int i = 0; i < 5; i++) {
			if(pls.contains(choose[i])) {
				if(role.equals("Liberal")) {
					if(pls.size()-notHits == 1 && otherPlayers.get(pls.get(choose[i]-1)).isHeHitler()) { //If only one player is not confirmed as Hitler and the bot is a liberal and he chooses the only one not confirmed as not hitler, he get +5 points on its fitness
						totalCost += 5;
					}
				}
				return choose[i];
			}
		}
		return choose[4];
	}
	
	
	/**
	 * String vote()
	 * Vote on the Presidential election (President + Chancellor)
	 * The vote will depend on the output value of the NN
	 * 
	 * @param president 	President
	 * @param chancellor 	Chancellor
	 * @return "Y" for yes and "N" for No
	 */
	public String vote(int president, int chancellor) {
		pres = president;
		chanc = chancellor;
		float[] out = inputNN(state == president? true : false, state == chancellor? true : false, false, false, false, false, false, true, 0, 0, 0, false);
		
		
		if(out[5] >= out[6]) {
			return "Y";
		}
		else {
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
		int[] _pol = new int[3];
		String[] pol = new String[3];
		pol[0] = one;
		pol[1] = two;
		pol[2] = three;
		
		int crd = 0;
		
		for(int i = 0; i < 3; i++) {
			_pol[i] = pol[i].equals("Liberal") ? 1 : -1;
			crd += _pol[i];
			if(_pol[i] == 1) {
				supposedLibCards++;
			} else {
				supposedFasCards++;
			}
		}
		
		
		float[] out = inputNN(true, false, false, false, true, false, false, false, _pol[0], _pol[1], _pol[2], false);
		
		int[] numbs = new int[3];
		for(int i = 0; i < 3; i++) {
			numbs[i] = i;
		}
		
		for(int i = 0; i < 2; i++) {
			for(int j = 0; j < 2-i; j++) {
				if(out[j+7] < out[j+8]) {
					float x = out[j+7];
					out[j+7] = out[j+8];
					out[j+8] = x;
					
					int y = numbs[j];
					numbs[j] = numbs[j+1];
					numbs[j+1] = y;
				}
			}
		}
		
		if(role.equals("Liberal") && (crd == -1|| crd == 1)) {
			if(_pol[numbs[0]] == 1 && (fasCardsEnacted <= 2 || (fasCardsEnacted == 5 || libCardsEnacted == 4))) { //if player is liberal and either fascist cards enacted are less than 3, or 5 or there are 4 liberal cards enacted
				totalCost += 3;																					  //and given the opportunity to discard the fascist card he discards a liberal card, 3 points get added to his fitness		
			}
		} else if(!role.equals("Fascist") && (crd == 1 || crd == -1)) {
			if(_pol[numbs[0]] == -1 && fasCardsEnacted == 5) { //basically the same but for a fascists point of view
				totalCost += 3;
			}
		}
		
		cardDiscarded = _pol[numbs[0]];
		
		return numbs[0];
	}
	
	
	/**
	 * int discardCard()
	 * The action to discard a card if the bot is the Chancellor
	 * 
	 * @param one 	first card
	 * @param two	second card
	 * @return the card the bot wants to discard or veto if the opportunity so presents
	 */
	public int discardCard(String one, String two, boolean veto) {
		int[] _pol = new int[2];
		String[] pol = new String[2];
		pol[0] = one;
		pol[1] = two;
		
		int crd = 0;
		
		for(int i = 0; i < 2; i++) {
			_pol[i] = pol[i].equals("Liberal") ? 1 : -1;
			crd += _pol[i];
		}
		
		float[] out = inputNN(false, true, false, false, true, false, veto, false, _pol[0], _pol[1], 0, false);
		
		int[] numbs = new int[3];
		for(int i = 0; i < 3; i++) {
			numbs[i] = i;
		}
		
		out[9] = out[10];
		
		for(int i = 0; i < 2; i++) {
			for(int j = 0; j < 2-i; j++) {
				if(out[j+7] < out[j+8]) {
					float x = out[j+7];
					out[j+7] = out[j+8];
					out[j+8] = x;
					
					int y = numbs[j];
					numbs[j] = numbs[j+1];
					numbs[j+1] = y;
				}
			}
		}
		
		if(numbs[0] == 2 && !veto) {
			if(crd == 0) {
				if(role.equals("Liberal") && _pol[numbs[1]] == 1) {
					totalCost += 3;
				} else if(!role.equals("Liberal") && (_pol[numbs[1]] == -1 && (libCardsEnacted == 4 || fasCardsEnacted == 5))){
					totalCost += 3;
				}
			}
			return numbs[1];
		} else {
			if(veto && numbs[0] == 2) {
				if(role.equals("Liberal") && crd == 2) {
					totalCost += 3;
				} else if(!role.equals("Liberal") && crd == -2) {
					totalCost += 3;
				}
				if(_pol[0] == 1) {
					supposedLibCards++;
				} else {
					supposedFasCards++;
				}
				if(_pol[1] == 1) {
					supposedLibCards++;
				} else {
					supposedFasCards++;
				}
			}else {
				if(crd == 0) {
					if(role.equals("Liberal") && _pol[numbs[0]] == 1) {
						totalCost += 3;
					} else if(!role.equals("Liberal") && (_pol[numbs[0]] == -1 && (libCardsEnacted == 4 || fasCardsEnacted == 5))){
						totalCost += 3;
					}
				}
			}
			
			
			return numbs[0];
		}
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
		int[] _pol = new int[2];
		String[] pol = new String[2];
		pol[0] = one;
		pol[1] = two;
		
		int crd = 0;
		
		for(int i = 0; i < 2; i++) {
			_pol[i] = pol[i].equals("Liberal") ? 1 : -1;
			crd += _pol[i];
		}
		
		float[] out = inputNN(true, false, false, false, false, true, false, true, _pol[0], _pol[1], 0, false);
		
		if(out[5] >= out[6]) {
			if(role.equals("Liberal") && crd == 2) {
				totalCost += 4;
			} else if(!role.equals("Liberal") && crd == -2) {
				totalCost += 4;
			}
			return true;
		} else {
			if(role.equals("Liberal") && crd == -2) {
				totalCost += 4;
			} else if(!role.equals("Liberal") && crd == 2) {
				totalCost += 4;
			}
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
		float[] out = inputNN(true, false, false, true, false, false, false, false, 0, 0, 0, false);
		
		int[] numbs = new int[5];
		for(int i = 0; i < 5; i++) {
			numbs[i] = i+1;
		}
		
		for(int i = 0; i < 4; i++) {
			for(int j = 0; j < 4-i; j++) {
				if(out[j] < out[j+1]) {
					float x = out[j];
					out[j] = out[j+1];
					out[j+1] = x;
					
					int y = numbs[j];
					numbs[j] = numbs[j+1];
					numbs[j+1] = y;
				}
			}
		}
		
		for(int i = 0; i < 5; i++) {
			if(players.contains(numbs[i]) && numbs[i] != state) {
				if(role.equals("Liberal") && !otherPlayers.get(numbs[i]-1).isHeHitler()) {
					totalCost += 3;
				} else if(role.equals("Fascist") && numbs[i] == otherFascist) {
					totalCost += 5;
				}
				return numbs[i];
			}
		}
		return numbs[0];
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
		lastPlayed = enacted;
		int _pol[] = new int[3];
		_pol[0] = one;
		_pol[1] = two;
		_pol[2] = three;
		
		int crd = 0;
		for(int i = 0; i < 3; i++) {
			crd += _pol[i];
		}
		
		float[] out = inputNN(true, false, false, false, false, false, false, false, _pol[0], _pol[1], _pol[2], true);
		
		int[] choose = new int[4];
		for(int i = 0; i < 4; i++) {
			choose[i] = i;
		}
		
		for(int i = 0; i < 3; i++) {
			for(int j = 0; j < 3-i; j++) {
				if(out[11+j] < out[11+j+1]) {
					float x = out[11+j];
					out[11+j] = out[11+j+1];
					out[11+j+1] = x;
					
					int y = choose[j];
					choose[j] = choose[j+1];
					choose[j+1] = y;
				}
			}
		}
		
		String[] results = new String[4];
		
		results[0] = "3 Liberals 0 Fascists";
		results[1] = "2 Liberals 1 Fascist";
		results[2] = "1 Liberal 2 Fascists";
		results[3] = "0 Liberals 3 Fascists";
		
		if(role.equals("Liberal")) {
			if(crd == -3 && choose[0] != 3) {
				totalCost += 1.5;
			} else if(crd == 3 && choose[0] != 0) {
				totalCost += 1.5;
			} else if(crd == 1 && choose[0] != 1) {
				totalCost += 1.5;
			} else if(crd == -1 && choose[0] != 2) {
				totalCost += 1.5;
			}
		} else {
			if(crd == -3 && choose[0] != 3) {
				totalCost += 1.5;
			} else if(((crd == -1 || crd == 1) && enacted == -1) && (choose[0] != 3 && chanc == otherFascist)) {
				totalCost += 0.8;
			} else if((crd == 1 && choose[0] == 0) && cardDiscarded == 1) {
				totalCost += 0.8;
			} else if((crd == -1 && choose[0] == 3) && cardDiscarded == -1) {
				totalCost += 0.8;
			} else if(crd == 3 && (choose[0] == 3 || choose[0] == 2)) {
				totalCost += 0.8;
			}
		}
		
		
		return results[choose[0]];
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
		lastPlayed = enacted;
		int[] _pol = new int[2];
		_pol[0] = one;
		_pol[1] = two;
		
		int crd = 0;
		
		for(int i = 0; i < 2; i++) {
			crd += _pol[i];
		}
		
		float[] out = inputNN(false, true, false, false, false, false, false, false, _pol[0], _pol[1], 0, true);
		
		int[] choose = new int[3];
		for(int i = 0; i < 3; i++) {
			choose[i] = i;
		}
		
		for(int i = 0; i < 2; i++) {
			for(int j = 0; j < 2-i; j++) {
				if(out[15+j] < out[15+j+1]) {
					float x = out[15+j];
					out[15+j] = out[15+j+1];
					out[15+j+1] = x;
					
					int y = choose[j];
					choose[j] = choose[j+1];
					choose[j+1] = y;
				}
			}
		}
		
		String[] results = new String[3];
		results[0] = "2 Liberals 0 Fascists";
		results[1] = "1 Liberal 1 Fascists";
		results[2] = "0 Liberals 2 Fascists";
		
		if(role.equals("Liberal")) {
			if(crd == 2 && choose[0] != 0) {
				totalCost += 1.5;
			} else if(crd == -2 && choose[0] != 2) {
				totalCost += 1.5;
			} else if(crd == 0 && choose[0] != 1) {
				totalCost += 1.0;
			}
		} else {
			if(crd == 2 && choose[0] == 2) {
				totalCost += 1.0;
			}else if(crd == -2 && choose[0] != 2) {
				totalCost += 1.0;
			} else if(crd == 0 && ((enacted == -1 && choose[0] != 2) || (enacted == 1 && choose[0] == 2))) {
				totalCost += 1.0;
			}
		}
		
		return results[choose[0]];
	}
	
	
	/**
	 * cardsTold()
	 * Cards Told by the president and chancellor (when the player is none of those two)
	 * 
	 * @param president		president player number
	 * @param chancellor	chancellor player number
	 * @param toldP			what the president told
	 * @param toldC		 	what the chancellor told
	 */
	public void cardsTold(int president, int chancellor, String toldP, String toldC) {
		String[] linesP = toldP.split(" ");
		String[] linesC = toldC.split(" ");
		int libsP = Integer.parseInt(linesP[0]);
		int fasP = Integer.parseInt(linesP[2]);
		int libsC = Integer.parseInt(linesC[0]);
		int fasC = Integer.parseInt(linesC[2]);
		
		supposedLibCards += libsP;
		supposedFasCards += fasP;
		
		if(saw3Cards) {
			int libers = 0;
			int fascer = 0;
			for(int i = 0; i < 3; i++) {
				if(threeCards[i] == 1) {
					supposedLibCards++;
					libers++;
				} else {
					supposedFasCards++;
					fascer++;
				}
			}
			if(libers != libsP && role.equals("Liberal")) {
				otherPlayers.get(president-1).decreaseTrust();
				otherPlayers.get(president-1).decreaseTrust();
				otherPlayers.get(president-1).decreaseTrust();
			}
			if(!((libers == libsC && fascer == fasC+1) || (libers == libsC+1 && fascer == fasC))) {
				if(role.equals("Liberal")) {
					otherPlayers.get(chancellor-1).decreaseTrust();
					otherPlayers.get(chancellor-1).decreaseTrust();
					otherPlayers.get(chancellor-1).decreaseTrust();
				}
			}
			saw3Cards = false;
		} else {
			if((libsP == libsC && fasP == fasC+1)||(libsP == libsC+1 && fasP == fasC)) {
			
			} else {
				otherPlayers.get(president-1).decreaseTrust();
				otherPlayers.get(chancellor-1).decreaseTrust();
			}
		}
		
	}
	
	
	/**
	 * cardsTold()
	 * Cards Told by the president(when the player is the chancellor)
	 * 
	 * @param president		president player number
	 * @param toldP			what the president told
	 * @param toldC		 	what the chancellor told
	 * @param cardOne		first card
	 * @param cardTwo		second card
	 */
	public void cardsTold(int president, String toldP, String toldC, int cardOne, int cardTwo) {
		String[] linesP = toldP.split(" ");
		String[] linesC = toldC.split(" ");
		int libsP = Integer.parseInt(linesP[0]);
		int fasP = Integer.parseInt(linesP[2]);
		int libC = Integer.parseInt(linesC[0]);
		int faC = Integer.parseInt(linesC[2]);
		
		int libsC = 0;
		int fasC = 0;
		
		if(cardOne == 1) {
			libsC++;
		} else {
			fasC++;
		}
		if(cardTwo == 1) {
			libsC++;
		} else {
			fasC++;
		}
		
		
		if(saw3Cards) {
			int libers = 0;
			int fascer = 0;
			for(int i = 0; i < 3; i++) {
				if(threeCards[i] == 1) {
					supposedLibCards++;
					libers++;
				} else {
					supposedFasCards++;
					fascer++;
				}
				if(libers != libsP && role.equals("Liberal")) {
					otherPlayers.get(president-1).decreaseTrust();
					otherPlayers.get(president-1).decreaseTrust();
					otherPlayers.get(president-1).decreaseTrust();
				}
			}
			saw3Cards = false;
		} else {
			if((libsP == libsC && fasP == fasC+1)|| (libsP == libsC+1 && fasP == libsC)) {
				supposedLibCards += libsP;
				supposedFasCards += fasP;
				if (!((libsP == libC && fasP == faC+1) || (libsP == libC+1 && fasP == faC))) {
					for(int i = 0; i < 5; i++) {
						if(i != state-1) {
							otherPlayers.get(i).decreaseTheirTrust();
							otherPlayers.get(i).decreaseTheirTrust();
						}
					}
				}
			} else if ((libsP == libC && fasP == faC+1) || (libsP == libC+1 && fasP == faC)){
				otherPlayers.get(president-1).increaseTrust();
				otherPlayers.get(president-1).increaseTrust();
				supposedLibCards += libsC;
				supposedFasCards += fasC+1;
			}else {
				otherPlayers.get(president-1).decreaseTrust();
				otherPlayers.get(president-1).decreaseTrust();
				for(int i = 0; i < 5; i++) {
					if(i != state-1) {
						otherPlayers.get(i).decreaseTheirTrust();
						otherPlayers.get(i).decreaseTheirTrust();
					}
				}
				supposedLibCards += libsC;
				supposedFasCards += fasC+1;
			
			}
		}
		
	}
	
	
	/**
	 * cardsTold()
	 * Cards Told by the chancellor(when the player is the president)
	 * 
	 * @param chancellor	chancellor player number
	 * @param toldP			what the president told
	 * @param toldC		 	what the chancellor told
	 * @param cardOne		first card
	 * @param cardTwo		second card
	 * @param cardThree		third card
	 */
	public void cardsTold(int chancellor, String toldP, String toldC, int cardOne, int cardTwo, int cardThree) {
		String[] linesP = toldP.split(" ");
		String[] linesC = toldC.split(" ");
		int libP = Integer.parseInt(linesP[0]);
		int faP = Integer.parseInt(linesP[2]);
		int libsC = Integer.parseInt(linesC[0]);
		int fasC = Integer.parseInt(linesC[2]);
		
		int libsP = 0;
		int fasP = 0;
		
		if(cardOne == 1) {
			libsP++;
		} else {
			fasP++;
		}
		if(cardTwo == 1) {
			libsP++;
		} else {
			fasP++;
		}
		if(cardThree == 1) {
			libsP++;
		} else {
			fasP++;
		}
		
		if((libsP == libsC && fasP == fasC+1)|| (libsP == libsC+1 && fasP == libsC)) {
			if(!((libP == libsC && faP == fasC+1) || (libP == libsC+1 && faP == fasC))){
				for(int i = 0; i < 5; i++) {
					if(i != state-1) {
						otherPlayers.get(i).decreaseTheirTrust();
						otherPlayers.get(i).decreaseTheirTrust();
					}
				}
			}
		}else if((libP == libsC && faP == fasC+1) || (libP == libsC+1 && faP == fasC)){
			otherPlayers.get(chancellor-1).increaseTrust();
			otherPlayers.get(chancellor-1).increaseTrust();
		}else {
			otherPlayers.get(chancellor-1).decreaseTrust();
			otherPlayers.get(chancellor-1).decreaseTrust();
			for(int i = 0; i < 5; i++) {
				if(i != state-1) {
					otherPlayers.get(i).decreaseTheirTrust();
					otherPlayers.get(i).decreaseTheirTrust();
				}
			}
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
		saw3Cards = true;
		String[] pl = new String[3];
		pl[0] = one;
		pl[1] = two;
		pl[2] = three;
		
		for(int i = 0; i < 3; i++) {
			threeCards[i] = pl[i].equals("Liberal") ? 1 : -1;
		}
	}
	
	
	/**
	 * policyEnacted()
	 * Updates the variable of the liberal or fascist cards enacted according to what was played
	 * 
	 * @param policy 	The policy that was enacted
	 */
	public void policyEnacted(int policy) {
		if(policy == 1) {
			libCardsEnacted++;
		} else {
			fasCardsEnacted++;
		}
		electionTracker = 0;
	}
	
	
	/**
	 * checkPlay()
	 * updates some variable and trust levels depending on the round player
	 * 
	 * @param _play 	The occurrences of the last round played
	 */
	public void checkPlay(String _play) {
		String[] play = _play.split(",");
		
		int gotKilled = 0;
		boolean fascistVictory = false;
		boolean liberalVictory = false;
		boolean hitlerKilled = false;
		boolean hitlerVoted = false;
		boolean _veto = false;
		boolean _noVeto = false;
		
		int optionTrust = -1;
		
		int _pres = Integer.parseInt(play[0]);
		int _chanc = Integer.parseInt(play[1]);
		
		
		
		lastPres = _pres;
		lastChanc = _chanc;
		lastPlayed = 0;
		
		if(play[2].equals("Y")) { //president and chancellor got accepted
			if(deckPolicies < 3) {
				deckPolicies = 17 - libCardsEnacted - fasCardsEnacted;
				supposedFasCards = 0;
				supposedLibCards = 0;
			}
			deckPolicies -=3;
			if(play[3].equals("L")) { //played liberal policy
				lastPlayed = 1;
				if(_pres != state) {
					otherPlayers.get(_pres-1).increaseTrust();
				} else {
					optionTrust = 0;
				}
				if(_chanc != state) {
					otherPlayers.get(_chanc-1).increaseTrust();
				} else {
					optionTrust = 0;
				}
			} else if(play[3].equals("F")) { //played Fascist policy
				lastPlayed = -1;
				if(_pres != state) {
					if(_pres != otherFascist) {
						otherPlayers.get(_pres-1).decreaseTrust();
					} else {
						otherPlayers.get(_pres-1).increaseTrust();
					}
				} else {
					optionTrust = 1;
				}
				if(_chanc != state) {
					if(_chanc != otherFascist) {
						otherPlayers.get(_chanc-1).decreaseTrust();
					} else {
						otherPlayers.get(_chanc-1).increaseTrust();
					}
				} else {
					optionTrust = 1;
				}
				
				if(play.length > 4 && play[4].equals("FW")) { //if fascists win
					fascistVictory = true;
				}
				else if(play.length > 4) { //if someone got killed
					gotKilled = Integer.parseInt(play[4]);
					if(play.length > 5) { //if Hitler died
						hitlerKilled = true;
						liberalVictory = true;
					}
				}
			} else if(play[3].equals("HW")) { //if Hitler is voted Chancellor after 3 fascist policies are enacted
				hitlerVoted = true;
				fascistVictory = true;
			} else if(play[3].equals("V")) { //If Veto occurred
				_veto = true;
				if(play.length > 4) { //If election Tracker reached the limit
					if(deckPolicies < 1) {
						deckPolicies = 17 - libCardsEnacted - fasCardsEnacted;
						supposedFasCards = 0;
						supposedLibCards = 0;
					}
					deckPolicies--;
					if(play[4].equals("L")) {  //liberal policy came out
						saw3Cards = false;
						if(_pres != state) {
							otherPlayers.get(_pres-1).increaseTheirTrust();
						} else {
							optionTrust = 0;
						}
						if(_chanc != state) {
							otherPlayers.get(_chanc-1).increaseTheirTrust();
						} else {
							optionTrust = 0;
						}
						
						if(play.length > 5) {
							liberalVictory = true;
						}
					} else if(play[4].equals("F")) { //fascist policy came out, fascists win because if veto is on and fascist policy came out, they win because they enacted 6 policies
						saw3Cards = false;
						if(_pres != state) {
							if(_pres != otherFascist) {
								otherPlayers.get(_pres-1).decreaseTrust();
							}
						} else {
							optionTrust = 2;
						}
						if(_chanc != state) {
							if(_chanc != otherFascist) {
								otherPlayers.get(_chanc-1).decreaseTrust();
							}
						} else {
							optionTrust = 2;
						}
						
						fascistVictory = true;
					}
				}
			} else if (play[3].equals("NV")) { //If the President doesn't accept the Veto
				_noVeto = true;
				if(play[4].equals("F")) { //If the Chanc chooses Fascist policy
					lastPlayed = -1;
					if(_pres != state) {
						if(_pres != otherFascist) {
							otherPlayers.get(_pres-1).decreaseTrust();
							otherPlayers.get(_pres-1).decreaseTrust();
						}
						otherPlayers.get(_pres-1).decreaseTrust();
					} else {
						optionTrust = 4;
					}
					if(_chanc != state) {
						otherPlayers.get(_chanc-1).increaseTrust();
						otherPlayers.get(_chanc-1).increaseTrust();
						otherPlayers.get(_chanc-1).increaseTrust();
					} else {
						optionTrust = 0;
					}
				} else if (play[4].equals("L")) { //If the Chanc chooses Liberal Policy
					lastPlayed = 1;
					if(_pres != state) {
						otherPlayers.get(_pres-1).increaseTrust();
						otherPlayers.get(_pres-1).increaseTrust();
						otherPlayers.get(_pres-1).increaseTrust();
					} else {
						optionTrust = 3;
					}
					if(_chanc != state) {
						if(_chanc != otherFascist){
							otherPlayers.get(_chanc-1).decreaseTrust();
							otherPlayers.get(_chanc-1).decreaseTrust();
						}
						otherPlayers.get(_chanc-1).decreaseTrust();
					} else {
						optionTrust = 4;
					}
						
					if(play.length > 5) {
						liberalVictory = true;
					}
				}
			}
		} else { //if The voters voted No on the President and Chancellor
			if(play.length > 4) { //If tracker reached limit
				if(deckPolicies < 1) {
					deckPolicies = 17 - libCardsEnacted - fasCardsEnacted;
					supposedFasCards = 0;
					supposedLibCards = 0;
				}
				deckPolicies--;
				if(play[4].equals("LW")) {
					liberalVictory = true;
				} else if(play[4].equals("FW")) {
					fascistVictory = true;
				}
			}
		}
		
		if(play.length == 3) { //This case only happens if both the president and chancellor didn't get accepted and the election tracker limit didn't reach its limit.
			electionTracker++;
		}
		if(play.length > 3) {
			if(_veto) {
				lastPlayed = (float) (play.length > 4 ? (play[4].equals("L") ? 1.5 : -1.5) : 0.0);
				saw3Cards = false;
			} else if(_noVeto) {
				lastPlayed = (float) (play[4].equals("L") ? 0.5: -0.5);
				saw3Cards = false;
			}
			if(gotKilled != 0) {
				otherPlayers.get(gotKilled-1).died();
			}
			if(hitlerKilled) {
				for(int i = 0; i < 5; i++) {
					otherPlayers.get(i).detectHitler(false);
				}
				otherPlayers.get(gotKilled-1).detectHitler(true);
			}
		}
		
		if(optionTrust != -1) {
			if(optionTrust == 0) {
				for(int i = 0; i < 5; i++) {
					if(i != state-1) {
						otherPlayers.get(i).increaseTheirTrust();
					}
				}
			} else if (optionTrust <= 2) {
				for(int i = 0; i < 5; i++) {
					if(i != state -1) {
						if(optionTrust == 1 && i == otherFascist -1) {
							otherPlayers.get(i).increaseTheirTrust();
						} else {
							otherPlayers.get(i).decreaseTheirTrust();
						}
					}
				}
			} else if(optionTrust == 3) {
				for(int i = 0; i < 5; i++) {
					if(i != state -1) {
						otherPlayers.get(i).increaseTheirTrust();
						otherPlayers.get(i).increaseTheirTrust();
						otherPlayers.get(i).increaseTheirTrust();
					}
				}
			} else if (optionTrust == 4) {
				for(int i = 0; i < 5; i++) {
					if(i != state -1) {
						otherPlayers.get(i).decreaseTheirTrust();
						if(i != otherFascist -1) {
							otherPlayers.get(i).decreaseTheirTrust();
							otherPlayers.get(i).decreaseTheirTrust();
						}
					}
				}
			}
		}
	}
}
