import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

public class MLBot extends Player{
	
	private int otherFascist;
	Random rnd;
	private NN nn; //Neural Network
	
//	GameVariables
	private int numLibCards;
	private int numFasCards;
	
//	Other Players
	private LinkedList<PlayerModel> otherPlayers;
//	private PlayerModel p1;
//	private PlayerModel p2;
//	private PlayerModel p3;
//	private PlayerModel p4;
	
	public MLBot(String role, int state) {
		super(role, state);
		nn = new NN(5, 4);
		numLibCards = 6;
		numFasCards = 11;
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
		//inps[pls.size()] = 
		//float calcN = nn.calculateNN(inps);
		if(role.equals("Liberal")) {
			choose = rnd.nextInt(5);
		} else {
			choose = otherFascist;
		}
		while(choose == president || choose == lastChancellor || !players.contains(choose)) {
			choose = rnd.nextInt(5);
		}
		return choose;
	}
}
