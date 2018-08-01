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
	private boolean canVeto;
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
		
		canVeto = false;
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
	
	private float[] inputNN(boolean isPres, boolean isChanc, boolean abilityNextPres, boolean abilityKillPlayer, boolean vetoOn, boolean vote, int cardOne, int cardTwo, int cardThree) {
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
		
		
		
		return inps;
	}
	
	
	public int chooseChancellor(int president, int lastChancellor, ArrayList<Integer> players) {
		int choose;
		ArrayList<Integer> pls = new ArrayList<Integer>(players);
		pls.remove((Object) president);
		pls.remove((Object) lastChancellor);
		float[] inps = new float[pls.size()];
		for(int i = 0; i < pls.size(); i++)
		{
			inps[i] = otherPlayers.get(pls.get(i)).getTrustLevel();
		}
		float[] lol = nn.calculateNN(inps);
		float[] lool = new float[4];
		int[] numbs = new int[4];
		for(int i = 0; i < 4; i++) {
			lool[i] = lol[i];
			numbs[i] = i;
		}
		for(int i = 0; i < 3; i++) {
			for(int j = 0; j < 3 - i; j++) {
				if(lool[j] > lool[j+1]) {
					float x = lool[j];
					lool[j] = lool[j+1];
					lool[j+1] = x;
					
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
}
