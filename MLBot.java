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
	
//	Other Players
	private LinkedList<PlayerModel> otherPlayers;
//	private PlayerModel p1;
//	private PlayerModel p2;
//	private PlayerModel p3;
//	private PlayerModel p4;
	
	public MLBot(String role, int state) {
		super(role, state);
		nn = new NN(5, 4);
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
		
		rnd = new Random(System.currentTimeMillis());
		otherPlayers = new LinkedList<PlayerModel>();
		for(int i = 0; i < 5; i++) {
			otherPlayers.add(new PlayerModel());
		}
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
	private float[] inputNN(boolean isPres, boolean isChanc, boolean abilityNextPres, boolean abilityKillPlayer, boolean vetoOn, boolean canVeto, boolean vote, int cardOne, int cardTwo, int cardThree) {
		float[] inps = new float[30];
		for(int i = 0; i < inps.length; i++) {
			inps[i] = 0;
		}
		for(int i = 0; i < 4; i++) {
			inps[i*2] = otherPlayers.get(i).getTrustLevel();
			inps[i*2 + 1] = otherPlayers.get(i).getTheirTrustLevel();
		}
		inps[14] = pres;
		inps[15] = chanc;
		inps[16] = lastChanc;
		
		inps[17] = libCardsEnacted;
		inps[18] = fasCardsEnacted;
		inps[19] = 6-libCardsEnacted;
		inps[20] = 11-fasCardsEnacted;
		inps[21] = supposedLibCards;
		inps[22] = supposedFasCards;
		
		inps[23] = cardOne;
		inps[24] = cardTwo;
		inps[25] = cardThree;
		
		inps[29] = electionTracker;
		
		if(isPres) {
			inps[8] = 1;
			inps[26] = 1;
		}
		else if(isChanc) {
			inps[27] = 1;
		}
		else {
			inps[28] = 1;
		}
		if(abilityNextPres) {
			inps[9] = 1;
		}
		if(abilityKillPlayer) {
			inps[10] = 1;
		}
		if(vetoOn) {
			inps[11] = 1;
		}
		if(canVeto) {
			inps[12] = 1;
		}
		if(vote) {
			inps[13] = 1;
		}
		
		
		float[] output = nn.calculateNN(inps);
		return output;
	}
	
	
	public int chooseChancellor(int president, int lastChancellor, ArrayList<Integer> players) {
		ArrayList<Integer> pls = new ArrayList<Integer>(players);
		pls.remove((Object) president);
		pls.remove((Object) lastChancellor);
		
		float[] lol = inputNN(true, false, false, false, false, false, false, 0, 0, 0);
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
		float[] out = inputNN(state == president? true : false, state == chancellor? true : false, false, false, false, false, true, 0, 0, 0);
		
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
		
		float[] out = inputNN(true, false, false, false, false, false, false, _pol[0], _pol[1], _pol[2]); //ADICIONAR A OPÇÃO DE DISCARD PARA O INPUT
		
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
	
	public int discardCards(String one, String two, boolean veto) {
		int[] _pol = new int[2];
		String[] pol = new String[2];
		pol[0] = one;
		pol[1] = two;
		
		for(int i = 0; i < 2; i++) {
			_pol[i] = pol[0].equals("Liberal") ? 1 : -1;
		}
		
		float[] out = inputNN(false, true, false, false, false, veto, false, _pol[0], _pol[1], 0);
		
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
		
		float[] out = inputNN(true, false, false, false, true, true, true, _pol[0], _pol[1], 0);
		
		if(out[4] >= out[5]) {
			return true;
		} else {
			return false;
		}
	}
	
	public int killPlayer(ArrayList<Integer> players) {
		float[] out = inputNN(true, false, false, true, false, false, false, 0, 0, 0);
		
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
}
