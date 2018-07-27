import java.util.ArrayList;
import java.util.Random;

public class HonestBot extends Player{

	private int otherFascist;
	Random rnd;
	public HonestBot(String role, int state) {
		super(role, state);
		rnd = new Random(System.currentTimeMillis());
	}
	
	public void receiveRole(ArrayList<Integer> showRoles) {
		if(role.equals("Hitler")) {
			otherFascist = showRoles.get(0);
		} else if(role.equals("Fascist")) {
			otherFascist = showRoles.get(1);
		} else {
			otherFascist = -1;
		}
	}
	
	public int chooseChancellor(int president, int lastChancellor, ArrayList<Integer> players) {
		int choose;
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
	
	public String vote(int president, int chancellor) {
		if(president == state || chancellor == state) {
			return "Y";
		}
		else if((role.equals("Fascist") || role.equals("Hitler")) && (president == otherFascist || chancellor == otherFascist)) {
			return "Y";
		} else {
			int r = rnd.nextInt(7);
			if(r < 4) {
				return "Y";
			} else {
				return "N";
			}
		}
	}
	
	public int discardCard(String one, String two, String three) {
		String[] policies = new String[3];
		policies[0] = one;
		policies[1] = two;
		policies[2] = three;
		
		if(role.equals("Liberal")) {
			for(int i = 0; i < 2; i++) {
				if(policies[i].equals("Fascist")) {
					return i;
				}
			}
		} else {
			for(int i = 0; i < 2; i++) {
				if(policies[i].equals("Liberal")) {
					return i;
				}
			}
		}
		
		return 2;
	}
	
	public int discardCard(String one, String two, boolean veto) {
		if(veto) {
			if(role.equals("Liberal")) {
				if(one.equals("Fascists") && two.equals("Fascist")) {
					return 2;
				}
			} else {
				if(one.equals("Liberal") && two.equals("Liberal")) {
					return 2;
				}
			}
		}
		if(role.equals("Liberal")) {
			if(one.equals("Fascist")) {
				return 0;
			}
		} else {
			if(one.equals("Liberal")) {
				return 0;
			}
		}
		return 1;
	}
	
	public boolean voteVeto(String one, String two) {
		if(role.equals("Liberal")) {
			if(one.equals("Fascist") && two.equals("Fascist")) {
				return true;
			}
		} else {
			if(one.equals("Liberal") && two.equals("Liberal")) {
				return true;
			}
		}
		return false;
	}
	
	public int killPlayer(ArrayList<Integer> players) {
		ArrayList<Integer> n_pl = new ArrayList<Integer>(players);
		n_pl.remove((Object) state);
		if(!role.equals("Liberal")) {
			n_pl.remove((Object) otherFascist);
		}
		return n_pl.get(rnd.nextInt(n_pl.size()));
	}
	
}
