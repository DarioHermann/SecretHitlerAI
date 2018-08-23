import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.Scanner;

public class NNTrainer {
	
	private Random rnd = new Random(System.currentTimeMillis());
	private ArrayList<String> player_roles = new ArrayList<String>();
	private int otherFascist;
	Scanner sc = new Scanner(System.in);
	
	private int lastPresident = 0;
	private int lastChancellor = 0;
	private int lastPlayed = 0;
	private boolean vetoed = false;
	private boolean successVeto = false;
	
	private int fasPolicies = 0;
	private int libPolicies = 0;
	private int supposedLibCards = 0;
	private int supposedFasCards = 0;
	
	private int electionTracker = 0;
	
	private boolean breakStatus = false;
	
	private NN nn;
	private ArrayList<PlayerModel> pl_model = new ArrayList<PlayerModel>();
	private ArrayList<Integer> cardPile = new ArrayList<Integer>();
	
	public int train() {
		
		nn = new NN(48, 30);
		 
		float[] inp = new float[48];
		for(int i = 0; i < 48; i++) {
			inp[i] = 0;
		}
		
		int state = rnd.nextInt(5)+1;
		inp[0] = state; //my state
		
		for(int i = 0; i < 5; i++) {
			pl_model.add(new PlayerModel());
		}

		roles();
		if(player_roles.get(state-1).equals("Hitler")) {
			otherFascist = player_roles.indexOf("Fascist") + 1;
			inp[1] = -1; //my Role
			inp[2] = 1; //Am I Hitler?
			for(int i = 0; i < 5; i++) {
				if(i != state-1) {
					pl_model.get(i).detectHitler(false);
				}
			}
		} else if (player_roles.get(state-1).equals("Fascist")){
			otherFascist = player_roles.indexOf("Hitler") + 1;
			inp[1] = -1; //my Role
			inp[2] = 0; //Am I Hitler?
			for(int i = 0; i < 5; i++) {
				if(i != otherFascist-1) {
					pl_model.get(i).detectHitler(false);
				}
			}
		}else {
			otherFascist = 0;
			inp[1] = 1; //my Role
			inp[2] = 0; //Am I Hitler?
		}
		

		for(int i = 0; i < 5; i++) {
			inp[i*4+3] = pl_model.get(i).getTrustLevel(); // trust level
			inp[i*4+4] = pl_model.get(i).getTheirTrustLevel(); // their trust level
			inp[i*4+5] = pl_model.get(i).getDeathStatus() ? 0 : 1; // is he dead?
			inp[i*4+6] = pl_model.get(i).isHeHitler() ? 1 : -1; // is he hitler?
		}
		
		inp[23] = otherFascist; //Other Fascist
		inp[37] = 6; //Max Liberal
		inp[38] = 11; //Max Fascist
		
		int numb_rounds = rnd.nextInt(12)+1;
		
		System.out.println(numb_rounds);
		
		int president = 0;
		int chancellor = 0;
		int played = 0;
		boolean presidentPower = false;
		
		for(int i = 0; i < numb_rounds; i++) {
			String pl = "";
			System.out.println("fasPolicies: " + fasPolicies + "\nlibPolicies:" + libPolicies);
			vetoed = false;
			//successVeto = false;
			if(fasPolicies >= 6 || libPolicies >= 5) {
				breakStatus = true;
				break;
			}
			lastPresident = president;
			lastChancellor = chancellor;
			lastPlayed = played;
			played = 0;
			do{
				president = president %5 + 1;
			} while(pl_model.get(president - 1).getDeathStatus());
			pl += president;
			do {
				chancellor = rnd.nextInt(5) + 1;
			} while((chancellor == president || chancellor == lastChancellor) || pl_model.get(chancellor-1).getDeathStatus());
			pl += "," + chancellor;
			
			if(rnd.nextFloat() > 0.4) { //If yes is voted
				pl += ",Y";
				if(fasPolicies >= 3 && player_roles.get(chancellor-1).equals("Hitler")) { //if hitler is chancellor after 3 fas policies were enacted
					pl += ",HW";
					fasPolicies = 6;
					breakStatus = true;
					break;
				} else if(fasPolicies >= 3) {
					pl_model.get(chancellor-1).detectHitler(false);
				}
				if(fasPolicies >= 5 && rnd.nextFloat() < 0.2) { //if veto is on & it was tried to veto
					vetoed = true;
					if(rnd.nextFloat() > 0.5) { //veto is accepted
						pl +=",V";
						successVeto = true;
						played = 0;
						if(cardPile.size() < 3) {
							shuffle();
						}
						for(int j = 0; j < 3; j++) {
							if(cardPile.remove(j) == 1) {
								supposedLibCards++;
							} else {
								supposedFasCards++;
							}
						}
						electionTracker++;
						if(electionTracker == 3) {
							electionTracker = 0;
							if(cardPile.size() < 1) {
								shuffle();
							}
							if(cardPile.remove(0) == 1) {
								pl += ",L";
								supposedLibCards++;
								libPolicies++;
							} else {
								pl += ",F";
								supposedFasCards++;
								fasPolicies++;
							}
						}
					} else { // veto is not accepted
						pl += ",NV";
						successVeto = false;
						if(cardPile.size() < 3) {
							shuffle();
						}
						
						int c = rnd.nextInt(3);
						if(cardPile.remove(c) == 1) {
							pl += ",L";
							played = 1;
							supposedLibCards++;
							libPolicies++;
						} else {
							pl += ",F";
							played = -1;
							supposedFasCards++;
							presidentPower = true;
							fasPolicies++;
						}
						electionTracker = 0;
						
						for(int j = 0; j < 2; j++) {
							if(cardPile.remove(j) == 1) {
								supposedLibCards++;
							} else {
								supposedFasCards++;
							}
						}
					}
				} else { //if veto is off
					int c = rnd.nextInt(3);
					if(cardPile.size() < 3) {
						shuffle();
					}
					if(cardPile.remove(c) == 1) {
						pl += ",L";
						played = 1;
						supposedLibCards++;
						libPolicies++;
					} else {
						pl += ",F";
						played = -1;
						presidentPower = true;
						supposedFasCards++;
						fasPolicies++;
					}
					electionTracker = 0;
					for(int j = 0; j < 2; j++) {
						if(cardPile.remove(j) == 1) {
							supposedLibCards++;
						} else {
							supposedFasCards++;
						}
					}
				}
			} else { //the voting didn't go successfully
				pl += ",N";
				electionTracker++;
				if(electionTracker == 3) {
					electionTracker = 0;
					if(cardPile.size() < 1) {
						shuffle();
					}
					if(cardPile.remove(0) == 1) {
						supposedLibCards++;
						libPolicies++;
					} else {
						supposedFasCards++;
						fasPolicies++;
					}
				}
			}
			int k = presidencyPower(presidentPower, state, president);
			if(k != 0) {
				pl_model.get(k - 1).died();
				pl_model.get(k-1).detectHitler(false);
				pl += "," + k;
				if(player_roles.get(k-1).equals("Hitler")) {
					libPolicies = 5;
					breakStatus = true;
					break;
				}
			}
			checkPlays(pl, state);
			
		}
		
		if(breakStatus) {
			return -1;
		}
		
		lastPresident = president;
		lastChancellor = chancellor;
		lastPlayed = played; 
		
		do{
			president = president%5 + 1;
		} while(pl_model.get(president-1).getDeathStatus());
		chancellor = 0;
		
		int action = 0;
		
		if(president != state) { //if the player is not the president this next round
			if(rnd.nextFloat() <= 0.85 && lastChancellor != state) { //high probability of player being chosen as Chancellor
				chancellor = state;
				action = 2;
			} else { //If the player is neither the president nor the chancellor
				do { //choosing a random chancellor
					chancellor = rnd.nextInt(5) + 1; 
				} while(chancellor == president || chancellor == state || pl_model.get(chancellor-1).getDeathStatus());
				action = 3;
			}
			
		} else { //If the player is the president
			action = 1;
		}
		
		if(cardPile.size() < 3) {
			shuffle();
		}
		
		int[] threeCards = new int[3];
		
		threeCards[0] = cardPile.get(0);
		threeCards[1] = cardPile.get(1);
		threeCards[2] = cardPile.get(2);
		
		int randCard = rnd.nextInt(3);
		int randCardTwo;
		do {
			randCardTwo = rnd.nextInt(3);
		} while(randCardTwo == randCard);
		
		switch(action) {
		case 1:
			inputNN(state, true, false, false, false, false, false, president, chancellor, 0, 0, 0, false);
			do { //choosing a random chancellor
				chancellor = rnd.nextInt(5) + 1; 
			} while(chancellor == president || chancellor == state || pl_model.get(chancellor-1).getDeathStatus());
			inputNN(state, false, false, false, false, false, true, president, chancellor, 0, 0, 0, false);
			inputNN(state, false, false, true, false, false, false, president, chancellor, threeCards[0], threeCards[1], threeCards[2], false);
			if(fasPolicies >= 5 && rnd.nextFloat() >= 0.70) {
				inputNN(state, false, false, false, false, true, true, president, chancellor, randCard == 0 ? threeCards[1] : threeCards[0], randCard == 0 ? threeCards[2] : (randCard == 1 ? threeCards[2] : threeCards[1]), 0, false);
			} else {
				lastPlayed = threeCards[randCardTwo];
				electionTracker = 0;
				inputNN(state, false, false, false, false, false, false, president, chancellor, threeCards[0], threeCards[1], threeCards[2], true);
				if(threeCards[randCardTwo] == -1 && fasPolicies >= 4) {
					inputNN(state, false, true, false, false, false, false, president, chancellor, 0, 0, 0, false);
				}
			}
			break;
		case 2:
			inputNN(state, false, false, false, false, false, true, president, chancellor, 0, 0, 0, false);
			inputNN(state, false, false, true, fasPolicies == 5 ? true : false, false, false, president, chancellor, randCard == 0 ? threeCards[1] : threeCards[0], randCard == 0 ? threeCards[2] : (randCard == 1 ? threeCards[2] : threeCards[1]), 0, false);
			if(fasPolicies >= 5 && rnd.nextFloat() >= 0.70) {
			} else {
				lastPlayed = threeCards[randCardTwo];
				electionTracker = 0;
				inputNN(state, false, false, false, false, false, false, president, chancellor, randCard == 0 ? threeCards[1] : threeCards[0], randCard == 0 ? threeCards[2] : (randCard == 1 ? threeCards[2] : threeCards[1]), 0, true);
			}
			break;
		case 3:
			inputNN(state, false, false, false, false, false, true, president, chancellor, 0, 0, 0, false);
			break;
		default:
			break;	
		}
		
		return 1;
		
	}
	
	private void inputNN(int state, boolean chooseChanc, boolean killPlayer, boolean disCard, boolean canVeto, boolean vetoOn, boolean vote, int president, int chancellor, int cardOne, int cardTwo, int cardThree, boolean tellCards) {
		float[] inp = new float[48];
		for(int i = 0; i < 48; i++) {
			inp[i] = 0;
		}
		
		inp[0] = state; //my state
		
		if(player_roles.get(state-1).equals("Hitler")) {
			inp[1] = -1; //my Role
			inp[2] = 1; //Am I Hitler?
			for(int i = 0; i < 5; i++) {
				if(i != state-1) {
					pl_model.get(i).detectHitler(false);
				}
			}
		} else if (player_roles.get(state-1).equals("Fascist")){
			inp[1] = -1; //my Role
			inp[2] = 0; //Am I Hitler?
			for(int i = 0; i < 5; i++) {
				if(i != otherFascist-1) {
					pl_model.get(i).detectHitler(false);
				}
			}
		}else {
			inp[1] = 1; //my Role
			inp[2] = 0; //Am I Hitler?
		}
		
		for(int i = 0; i < 5; i++) {
			System.out.println(pl_model.get(i).getTrustLevel() + "---" + pl_model.get(i).getTheirTrustLevel());
			inp[i*4+3] = pl_model.get(i).getTrustLevel(); // trust level
			inp[i*4+4] = pl_model.get(i).getTheirTrustLevel(); // their trust level
			inp[i*4+5] = pl_model.get(i).getDeathStatus() ? 0 : 1; // is he dead?
			inp[i*4+6] = pl_model.get(i).isHeHitler() ? 1 : -1; // is he hitler?
		}
		
		System.out.println(libPolicies + " : " + fasPolicies);
		
		inp[23] = otherFascist; //Other Fascist
		inp[24] = chooseChanc ? 1 : 0;
		inp[25] = killPlayer ? 1 : 0;
		inp[26] = disCard ? 1 : 0;
		inp[27] = canVeto ? 1 : 0;
		inp[28] = vetoOn ? 1 : 0;
		inp[29] = vote ? 1 : 0;
		inp[30] = president;
		inp[31] = chancellor;
		inp[32] = lastPresident;
		inp[33] = lastChancellor;
		inp[34] = lastPlayed;
		inp[35] = libPolicies;
		inp[36] = fasPolicies;
		inp[37] = 6 - libPolicies;
		inp[38] = 11 - fasPolicies; //Max Fascist
		inp[39] = supposedLibCards;
		inp[40] = supposedFasCards;
		inp[41] = cardOne;
		inp[42] = cardTwo;
		inp[43] = cardThree;
		inp[44] = state == president ? 1 : 0;
		inp[45] = state == chancellor ? 1 : 0;
		inp[46] = electionTracker;
		inp[47] = tellCards ? 1 : 0;
		
		
		String filePath = "n_weights.txt";
		
		String content = null;
		 try{
			 content = new String ( Files.readAllBytes( Paths.get(filePath) ) );
		 }catch (IOException e){
			 e.printStackTrace();
		 }
		 
		 nn.initialWeights(content);
//		StringBuilder contentBuilder = new StringBuilder();
//	    try (BufferedReader br = new BufferedReader(new FileReader(filePath)))
//	    {
//	 
//	        String sCurrentLine;
//	        while ((sCurrentLine = br.readLine()) != null)
//	        {
//	            contentBuilder.append(sCurrentLine).append("\n");
//	        }
//	    }
//	    catch (IOException e)
//	    {
//	        e.printStackTrace();
//	    }
//	    String content = contentBuilder.toString();
	    
//		 String[] val = content.split(",");
//		 float[] _val = new float[val.length];
//		 System.out.println(val[1000]);
//		 for(int i = 0; i < val.length; i++) {
//			 _val[i] = Float.parseFloat(val[i]);
//
//			 System.out.print(_val[i] + ",");
//		 }
//
//		 System.out.println("\n" + _val[1000]);
//		 nn.initialWeights(content);
//		
//		
		float[] out = nn.calculateNN(inp);
		
		int counter = 0;
		
		for(int i = 0; i < inp.length; i++) {
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
			System.out.println(inp[i]);
		}
		
		System.out.println("\n---------------------------------------------------------\n---------------------------------------------------------\n");
		
		for(int i = 0; i < out.length; i++) {
			System.out.println(out[i]);
		}
		
		
		float[] correctValues = new float[18];
		
		for(int i = 0; i < correctValues.length; i++) {
			System.out.print(i + ": ");
			correctValues[i] = Float.parseFloat(sc.next());
		}
		
//		float totCost = 0;
//		for(int i = 0; i < out.length; i++) {
//			totCost += out[i];
//		}
//		if(totCost < 0.1) {
//			for(int i = 0; i < out.length; i++) {
//				if(rnd.nextFloat() >= 0.5) {
//					out[i] = rnd.nextFloat();
//				}
//			}
//		}
		
		
		float[] cost = nn.calculateCost(out, correctValues);
		nn.correctNN(inp, out, correctValues);
		
		
		
		
		try(FileWriter fw = new FileWriter("costtable.txt", true);
	    		BufferedWriter bw = new BufferedWriter(fw);
	    		PrintWriter out2 = new PrintWriter(bw)) {
			int i = 0;
			for(i = 0; i < out.length; i++) {
				out2.println(out[i] + "\t\t" + correctValues[i] + "\t\t" + cost[i]);
			}
			out2.println(cost[i]);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		File myFoo = new File("n_weights.txt");
		FileWriter fooWriter;
		try {
			fooWriter = new FileWriter(myFoo, false);
			fooWriter.write(nn.readBrain());
			fooWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // true to append
		                                                     // false to overwrite.
		
//		try(FileWriter fw = new FileWriter("n_weights.txt", true);
//	    		BufferedWriter bw = new BufferedWriter(fw);
//	    		PrintWriter out2 = new PrintWriter(bw)) {
//			out2.println(nn.readBrain());
//		} catch (IOException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
		
	}
	
	private void roles() {
		player_roles.add("Hitler");
		player_roles.add("Fascist");
		for(int i = 0; i < 3; i++) {
			player_roles.add("Liberal");
			Collections.shuffle(player_roles);
		}
		Collections.shuffle(player_roles);
	}
	
	private void shuffle() {
		for(int i = 0; i < 6-libPolicies; i++) {
			cardPile.add(1);
		}
		for(int i = 0; i < 11-fasPolicies; i++) {
			cardPile.add(-1);
			Collections.shuffle(cardPile);
		}
		supposedLibCards = 0;
		supposedFasCards = 0;
	}
	
	private int presidencyPower(boolean p, int me, int pr) {
		int kill = 0;
		if(p && (fasPolicies == 4 || fasPolicies == 5) ) {
			do{
				kill = rnd.nextInt(5)+1;
			} while ((kill == me || kill == pr) || pl_model.get(kill-1).getDeathStatus());
		}
		return kill;
	}
	
	private void checkPlays(String _play, int me) {
		String[] play = _play.split(",");
		for(int i = 0; i < play.length; i++) {
			System.out.print(play[i] + ", ");
		}
		if(play.length > 3) {
			System.out.println("\n" + play[3]);
		}
		
		System.out.println("\n\n");
		int _pres = Integer.parseInt(play[0]);
		int _chanc = Integer.parseInt(play[1]);
		
		if(play[2].equals("Y")) {
			if(play[3].equals("L")) {
				if(_pres != me) {
					pl_model.get(_pres-1).increaseTrust();
				} else {
					for(int i = 0; i < 5; i++) {
						if(me-1 != i) {
							pl_model.get(i).increaseTheirTrust();
						}
					}
				}
				if(_chanc != me) {
					pl_model.get(_chanc-1).increaseTrust();
				} else {
					for(int i = 0; i < 5; i++){
						if(me-1 != i) {
							pl_model.get(i).increaseTheirTrust();
						}
					}
				}
			} else if(play[3].equals("F")) {
				if(_pres != me && _pres != otherFascist) {
					pl_model.get(_pres-1).decreaseTrust();
				} else if(_pres == otherFascist) {
					pl_model.get(_pres-1).increaseTrust();
				} else {
					for(int i = 0; i < 5; i++) {
						if(me-1 != i && otherFascist-1 != i) {
							pl_model.get(i).decreaseTheirTrust();
						} else if (otherFascist-1 == i) {
							pl_model.get(i).increaseTheirTrust();
						}
					}
				}
			} else if (play[3].equals("V")) {
				if(play.length > 4) {
					if(play[4].equals("L")) {
						if(_pres != me) {
							pl_model.get(_pres-1).increaseTrust();
						} else {
							for(int i = 0; i < 5; i++) {
								if(me-1 != i) {
									pl_model.get(i).increaseTheirTrust();
								}
							}
						}
						if(_chanc != me) {
							pl_model.get(_chanc-1).increaseTrust();
						} else {
							for(int i = 0; i < 5; i++) {
								if(me-1 != i) {
									pl_model.get(i).increaseTheirTrust();
								}
							}
						}
					} else {
						if(_pres != me && _pres != otherFascist) {
							pl_model.get(_pres-1).decreaseTrust();
						} else if (_pres == otherFascist) {
							pl_model.get(_pres-1).increaseTrust();
						} else {
							for(int i = 0; i < 5; i++) {
								if(me-1 != i && otherFascist-1 != i) {
									pl_model.get(i).decreaseTheirTrust();
								} else if (otherFascist-1 == i) {
									pl_model.get(i).increaseTheirTrust();
								}
							}
						}
						if(_chanc != me && _chanc != otherFascist) {
							pl_model.get(_chanc-1).decreaseTrust();
						} else if (_chanc == otherFascist) {
							pl_model.get(_chanc-1).increaseTrust();
						} else {
							for(int i = 0; i < 5; i++) {
								if(me-1 != i && otherFascist-1 != i) {
									pl_model.get(i).decreaseTheirTrust();
								} else if (otherFascist-1 == i) {
									pl_model.get(i).increaseTheirTrust();
								}
							}
						}
					}
				}
			} else if(play[3].equals("NV")){
				if(play[4].equals("L")) {
					if(_pres != me) {
						pl_model.get(_pres-1).increaseTrust();
						pl_model.get(_pres-1).increaseTrust();
						pl_model.get(_pres-1).increaseTrust();
					} else {
						for(int i = 0; i < 5; i++) {
							if(i != me-1) {
								pl_model.get(i).increaseTheirTrust();
								pl_model.get(i).increaseTheirTrust();
								pl_model.get(i).increaseTheirTrust();
							}
						}
					}
					if(_chanc != me) {
						pl_model.get(_chanc-1).decreaseTrust();
						if(_chanc != otherFascist) {
							pl_model.get(_chanc-1).decreaseTrust();
							pl_model.get(_chanc-1).decreaseTrust();
						}
					} else {
						for(int i = 0; i < 5; i++) {
							if(i != me-1) {
								pl_model.get(i).decreaseTheirTrust();
								if(i != otherFascist-1) {
									pl_model.get(i).decreaseTheirTrust();
									pl_model.get(i).decreaseTheirTrust();
								}
							}
						}
					}
				} else {
					if(_pres != me) {
						pl_model.get(_pres-1).decreaseTrust();
						if(_pres != otherFascist) {
							pl_model.get(_pres-1).decreaseTrust();
							pl_model.get(_pres-1).decreaseTrust();
						}
					} else {
						for(int i = 0; i < 5; i++) {
							if(i != me-1) {
								pl_model.get(i).decreaseTheirTrust();
								if(i != otherFascist-1) {
									pl_model.get(i).decreaseTheirTrust();
									pl_model.get(i).decreaseTheirTrust();
								}
							}
						}
					}
					if(_chanc != me) {
						pl_model.get(_pres-1).increaseTrust();
						pl_model.get(_pres-1).increaseTrust();
						pl_model.get(_pres-1).increaseTrust();
					} else {
						for(int i = 0; i < 5; i++) {
							if(i != me-1) {
								pl_model.get(i).increaseTheirTrust();
								pl_model.get(i).increaseTheirTrust();
								pl_model.get(i).increaseTheirTrust();
							}
						}
					}
				}
			}
		}
	}

}
