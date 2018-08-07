import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

public class MLBot extends Player{
	
	private int otherFascist;
	Random rnd;
	private NN nn; //Neural Network
	
//	GameVariables
//	private int numLibCards;
//	private int numFasCards;
	private int libCardsEnacted;
	private int fasCardsEnacted;
	private int supposedLibCards;
	private int supposedFasCards;
	private int electionTracker;
	
	
//	NN input Variables
	private int pres;
	private int chanc;
	private int lastChanc;
	private int lastPres;
	private int lastPlayed;
	
//	Other Players
	private LinkedList<PlayerModel> otherPlayers;
//	private PlayerModel p1;
//	private PlayerModel p2;
//	private PlayerModel p3;
//	private PlayerModel p4;
	
	public MLBot(String role, int state) {
		super(role, state);
		nn = new NN(48, 30);
//		numLibCards = 6;
//		numFasCards = 11;
		libCardsEnacted = 0;
		fasCardsEnacted = 0;
		supposedLibCards = 0;
		supposedFasCards = 0;
		electionTracker = 0;
		
		pres = -1;
		chanc = -1;
		lastChanc = -1;
		lastPres = -1;
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
	
	public void receiveRole(ArrayList<Integer> showRoles) {
		if(role.equals("Hitler")) {
			otherFascist = showRoles.get(0);
			for(int i = 0; i < 5; i++) {
				otherPlayers.get(i).setRole("Liberal");
			}
			otherPlayers.get(otherFascist).setRole("Fascist");
			otherPlayers.get(otherFascist).setTrustLevel(1.0f);
		} else if(role.equals("Fascist")) {
			otherFascist = showRoles.get(1);
			for(int i = 0; i < 5; i++) {
				otherPlayers.get(i).setRole("Liberal");
			}
			otherPlayers.get(otherFascist).setRole("Hitler");
			otherPlayers.get(otherFascist).setTrustLevel(1.0f);
		} else {
			otherFascist = -1;
		}
		
		otherPlayers.get(state).setRole("Me");
	}
	
	//vetoOn = veto is on, if 1 means the bot has to vote if he agrees with veto
	//canVeto = the power to veto is on, so that's a choice the bot can make
	private float[] inputNN(boolean isPres, boolean isChanc, boolean chooseChanc, boolean abilityKillPlayer, boolean discCard, boolean vetoOn, boolean canVeto, boolean vote, int cardOne, int cardTwo, int cardThree, boolean tellCardsInHand) {
		float[] inps = new float[48];
		for(int i = 0; i < inps.length; i++) {
			inps[i] = 0;
		}
		
		inps[0] = state+1;
		inps[1] = role.equals("Liberal") ? 1 : -1;
		inps[2] = role.equals("Hitler") ? 1 : 0;
		
		for(int i = 0; i < 5; i++) {
			inps[i*4+3] = otherPlayers.get(i).getTrustLevel();
			inps[i*4+4] = otherPlayers.get(i).getTheirTrustLevel();
			inps[i*4+5] = otherPlayers.get(i).getDeathStatus() ? 1 : 0;
			inps[i*4+6] = otherPlayers.get(i).isHeHitler() ? -1 : 1;
		}
		
		inps[23] = otherFascist+1;
	
		inps[24] = chooseChanc ? 1 : 0;
		inps[25] = abilityKillPlayer ? 1 : 0;
		inps[26] = discCard ? 1 : 0;
		inps[27] = canVeto ? 1 : 0;
		inps[28] = vetoOn ? 1 : 0;
		inps[29] = vote ? 1 : 0;
		
		inps[30] = pres + 1;
		inps[31] = chanc + 1;
		inps[32] = lastPres + 1;
		inps[33] = lastChanc + 1;
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
		return output;
	}
	
	
	public int chooseChancellor(int president, int lastChancellor, ArrayList<Integer> players) {
		ArrayList<Integer> pls = new ArrayList<Integer>(players);
		pls.remove((Object) president);
		pls.remove((Object) lastChancellor);
		
		float[] lol = inputNN(true, false, true, false, false, false, false, false, 0, 0, 0, false);
		int[] numbs = new int[4];
		for(int i = 0; i < 4; i++) {
			numbs[i] = i;
		}
		for(int i = 0; i < 3; i++) {
			for(int j = 0; j < 3 - i; j++) {
				if(lol[j] < lol[j+1]) {
					float x = lol[j];
					lol[j] = lol[j+1];
					lol[j+1] = x;
					
					int y = numbs[j];
					numbs[j] = numbs[j+1];
					numbs[j+1] = y;
				}
			}
		}
		for(int i = 0; i < 4; i++) {
			if(numbs[i] != president && numbs[i] != lastChancellor && players.contains(numbs[i])) {
				return numbs[i];
			}
		}
		return numbs[3];
	}
	
	public String vote(int president, int chancellor) {
		float[] out = inputNN(state == president? true : false, state == chancellor? true : false, false, false, false, false, false, true, 0, 0, 0, false);
		
		if(out[4] >= out[5]) {
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
		
		float[] out = inputNN(true, false, false, false, true, false, false, false, _pol[0], _pol[1], _pol[2], false); //ADICIONAR A OPÇÃO DE DISCARD PARA O INPUT
		
		int[] numbs = new int[3];
		for(int i = 0; i < 3; i++) {
			numbs[i] = i;
		}
		
		for(int i = 0; i < 2; i++) {
			for(int j = 0; j < 2-i; j++) {
				if(out[j+6] < out[j+7]) {
					float x = out[j+6];
					out[j+6] = out[j+7];
					out[j+7] = out[j+6];
					
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
		
		out[8] = out[9];
		
		for(int i = 0; i < 2; i++) {
			for(int j = 0; j < 2-i; j++) {
				if(out[j+6] < out[j+7]) {
					float x = out[j+6];
					out[j+6] = out[j+7];
					out[j+7] = x;
					
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
		
		if(out[4] >= out[5]) {
			return true;
		} else {
			return false;
		}
	}
	
	public int killPlayer(ArrayList<Integer> players) {
		float[] out = inputNN(true, false, false, true, false, false, false, false, 0, 0, 0, false);
		
		int[] numbs = new int[4];
		for(int i = 0; i < 3; i++) {
			for(int j = 0; j < 3-i; j++) {
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
		
		for(int i = 0; i < 4; i++) {
			if(players.contains(numbs[i])) {
				return numbs[i];
			}
		}
		return 2;
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
}
