import java.util.ArrayList;
import java.util.Random;

public class RandomBot extends Player {

	Random rnd;

	public RandomBot(String role, int state) {
		super(role, state);
		rnd = new Random(System.currentTimeMillis());
	}

	public int chooseChancellor(int president, int lastChancellor, ArrayList<Integer> players) {
		int choose = rnd.nextInt(5) + 1;
		while (choose == president || choose == lastChancellor || !players.contains(choose)) {
			choose = rnd.nextInt(5) + 1;
		}
		return choose;
	}

	public String vote(int president, int chancellor) {
		int r = rnd.nextInt(7);
		if (r < 4) {
			return "Y";
		} else {
			return "N";
		}
	}

	public int discardCard(String one, String two, String three) {
		String[] policies = new String[3];
		policies[0] = one;
		policies[1] = two;
		policies[2] = three;

		if (role.equals("Liberal")) {
			for (int i = 0; i < 2; i++) {
				if (policies[i].equals("Fascist")) {
					return i;
				}
			}
		}
		return rnd.nextInt(3);
	}
	
	public int discardCard(String one, String two, boolean veto) {
		int wantToVeto = rnd.nextInt(6);
		if(veto && wantToVeto<4) {
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
		}
		return rnd.nextInt(2);
	}
	
	public boolean voteVeto(String one, String two) {
		int wantToVeto = rnd.nextInt(7);
		if(wantToVeto < 3) {
			return true;
		} else {
			return false;
		}
	}
	
	public int killPlayer(ArrayList<Integer> players) {
		ArrayList<Integer> n_pl = new ArrayList<Integer>(players);
		n_pl.remove((Object) state);
		return n_pl.get(rnd.nextInt(n_pl.size()));
	}
}
