import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class NNTrainer {
	
	Random rnd = new Random(System.currentTimeMillis());
	ArrayList<String> player_roles = new ArrayList<String>();
	int otherFascist;
	
	private NN nn;
	
	public void train() {
		float[] inp = new float[48];
		int state = rnd.nextInt()+1;
		inp[0] = state;
		
		roles();
		if(player_roles.get(state-1).equals("Hitler")) {
			otherFascist = player_roles.indexOf("Fascist") + 1;
			inp[1] = -1;
			inp[2] = 1;
		} else if (player_roles.get(state-1).equals("Fascist")){
			otherFascist = player_roles.indexOf("Hitler") + 1;
			inp[1] = -1;
			inp[2] = 0;
		}else {
			otherFascist = 0;
			inp[1] = 1;
			inp[2] = 0;
		}
		
		inp[23] = otherFascist;
		
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

}
