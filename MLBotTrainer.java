import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

public class MLBotTrainer extends Player{
	
	private int otherFascist;
	Random rnd;
	private NN nn; //Neural Network
	
//	GameVariables
//	private int numLibCards;
//	private int numFasCards;
	private int deckPolicies;
	private int libCardsEnacted;
	private int fasCardsEnacted;
	private int supposedLibCards;
	private int supposedFasCards;
	private int electionTracker;
	private int[] threeCards;
	
	private float totalCost;
	private int choices;
	
	
//	NN input Variables
	private int pres;
	private int chanc;
	private int lastChanc;
	private int lastPres;
	private float lastPlayed;
	
//	Other Players
	private LinkedList<PlayerModel> otherPlayers;
//	private PlayerModel p1;
//	private PlayerModel p2;
//	private PlayerModel p3;
//	private PlayerModel p4;
	
	public MLBotTrainer(String role, int state, NN nn) {
		super(role, state);
		typeOfPlayer = 1;
		totalCost = 0;
		choices = 0;
		this.nn = nn;
		//nn = new NN(48, 30);
//		numLibCards = 6;
//		numFasCards = 11;
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
		
		int x = 48*30 + 30 + 30*18 + 18;
		
		float[] weights = new float[x];
		
		for(int i = 0; i < x; i++) {
			weights[i] = rnd.nextFloat()*8 - 4;
		}
		
		nn.initialWeights(weights);
		
	}
	
	
	
	public float getTotalCost() {
		totalCost = totalCost / choices;
		return totalCost;
	}
	
	public void receiveRole(ArrayList<Integer> showRoles) {
		if(role.equals("Hitler")) {
			otherFascist = showRoles.get(0);
			for(int i = 0; i < 5; i++) {
				otherPlayers.get(i).setRole("Liberal");
				otherPlayers.get(i).detectHitler(false);
			}
			otherPlayers.get(otherFascist-1).setRole("Fascist");
			otherPlayers.get(otherFascist-1).setTrustLevel(10.0f);
			otherPlayers.get(state-1).detectHitler(true);
		} else if(role.equals("Fascist")) {
			otherFascist = showRoles.get(1);
			for(int i = 0; i < 5; i++) {
				otherPlayers.get(i).setRole("Liberal");
				otherPlayers.get(i).detectHitler(false);
			}
			otherPlayers.get(otherFascist-1).setRole("Hitler");
			otherPlayers.get(otherFascist-1).setTrustLevel(10.0f);
			otherPlayers.get(otherFascist-1).detectHitler(true);
		} else {
			otherFascist = 0;
		}
		
		otherPlayers.get(state-1).setRole("Me");
	}
	
	//vetoOn = veto is on, if 1 means the bot has to vote if he agrees with veto
	//canVeto = the power to veto is on, so that's a choice the bot can make
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
	
	private void analyzePlay(float[] inps, float[] out) {
		int counter = 0;
		
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
		
		for(int i = 0; i < correctValues.length; i++) {
			System.out.print(i + ": ");
			correctValues[i] = Float.parseFloat(sc.next());
		}
		
		float[] cost = nn.calculateCost(out, correctValues);
		
		try(FileWriter fw = new FileWriter("costtable.txt", true);
	    		BufferedWriter bw = new BufferedWriter(fw);
	    		PrintWriter out2 = new PrintWriter(bw)) {
			int i = 0;
			for(i = 0; i < out.length; i++) {
				out2.println(out[i] + "\t\t" + correctValues[i] + "\t\t" + cost[i]);
			}
			out2.println(cost[i]);
			totalCost += cost[i];
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	public int chooseChancellor(int president, int lastChancellor, ArrayList<Integer> players) {
		ArrayList<Integer> pls = new ArrayList<Integer>(players);
		pls.remove((Object) president);
		pls.remove((Object) lastChancellor);
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
		for(int i = 0; i < 5; i++) {
			if(pls.contains(choose[i])) {
				return choose[i];
			}
		}
		return choose[4];
	}
	
	public String vote(int president, int chancellor) {
		float[] out = inputNN(state == president? true : false, state == chancellor? true : false, false, false, false, false, false, true, 0, 0, 0, false);
		
		if(out[5] >= out[6]) {
			return "Y";
		}
		else {
			return "N";
		}
	}
	
	public int discardCard(String one, String two, String three) {
		int[] _pol = new int[3];
		String[] pol = new String[3];
		pol[0] = one;
		pol[1] = two;
		pol[2] = three;
		for(int i = 0; i < 3; i++) {
			_pol[i] = pol[i].equals("Liberal") ? 1 : -1; 
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
		
		return numbs[0];
	}
	
	public int discardCard(String one, String two, boolean veto) {
		int[] _pol = new int[2];
		String[] pol = new String[2];
		pol[0] = one;
		pol[1] = two;
		
		for(int i = 0; i < 2; i++) {
			_pol[i] = pol[0].equals("Liberal") ? 1 : -1;
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
			return numbs[1];
		} else {
			return numbs[0];
		}
	}
	
	public boolean voteVeto(String one, String two) {
		int[] _pol = new int[2];
		String[] pol = new String[2];
		pol[0] = one;
		pol[1] = two;
		for(int i = 0; i < 2; i++) {
			_pol[i] = pol[i].equals("Liberal") ? 1 : -1;
		}
		
		float[] out = inputNN(true, false, false, false, false, true, false, true, _pol[0], _pol[1], 0, false);
		
		if(out[5] >= out[6]) {
			return true;
		} else {
			return false;
		}
	}
	
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
				return numbs[i];
			}
		}
		return numbs[0];
	}
	
	public String tellCards(String one, String two, String three) {
		int _pol[] = new int[3];
		String pol[] = new String[3];
		pol[0] = one;
		pol[1] = two;
		pol[2] = three;
		
		for(int i = 0; i < 3; i++) {
			_pol[i] = pol[0].equals("Liberal") ? 1 : -1;
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
		
		return results[choose[0]];
	}
	
	
	public String tellCards(String one, String two) {
		int[] _pol = new int[2];
		String[] pol = new String[2];
		pol[0] = one;
		pol[1] = two;
		
		for(int i = 0; i < 2; i++) {
			_pol[i] = pol[i].equals("Liberal") ? 1 : -1;
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
		
		return results[choose[0]];
	}
	
	public void checkThreeCards(String one, String two, String three) {
		String[] pl = new String[3];
		pl[0] = one;
		pl[1] = two;
		pl[2] = three;
		
		for(int i = 0; i < 3; i++) {
			threeCards[i] = pl[i].equals("Liberal") ? 1 : -1;
		}
	}
	
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
		
		if(play[2].equals("Y")) { //president and chancellor got accepted
			if(deckPolicies < 3) {
				deckPolicies = 17 - libCardsEnacted - fasCardsEnacted;
				supposedFasCards = 0;
				supposedLibCards = 0;
			}
			deckPolicies -=3;
			if(play[3].equals("L")) { //played liberal policy
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
				if(_pres != state) {
					if(_pres != otherFascist) {
						otherPlayers.get(_pres-1).decreaseTrust();
					} else {
						otherPlayers.get(otherFascist).increaseTrust();
					}
				} else {
					optionTrust = 1;
				}
				if(_chanc != state) {
					if(_chanc != otherFascist) {
						otherPlayers.get(_chanc-1).decreaseTrust();
					} else {
						otherPlayers.get(otherFascist).increaseTrust();
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
					} else {
						optionTrust = 0;
					}
				} else if (play[4].equals("L")) { //If the Chanc chooses Liberal Policy
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
				if(play[5].equals("LW")) {
					liberalVictory = true;
				} else if(play[5].equals("FW")) {
					fascistVictory = true;
				}
			}
		}
		
		if(play.length == 3) { //so existe no caso de ambos o presidente e o chencellor nao terem sido aceites e o election tracker nao ter chegado ao limite
			electionTracker++;
		}
		if(play.length > 3) {
			lastPres = _pres;
			lastChanc = _chanc;
			if(_veto) {
				lastPlayed = (float) (play[4].equals("L") ? 1.5 : -1.5);
			} else if(_noVeto) {
				lastPlayed = (float) (play[4].equals("L") ? 0.5: -0.5);
			}
			if(gotKilled != 0) {
				otherPlayers.get(gotKilled-1).died();
			}
			if(play[2].equals("Y") && fasCardsEnacted >= 3 && !hitlerVoted) {
				otherPlayers.get(_chanc-1).detectHitler(false);
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
