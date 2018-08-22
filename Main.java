import java.util.ArrayList;
import java.util.Collections;

public class Main {
	public static void main(String[] args) {
		
//		Game secretHitler = new GameRun();
//		ArrayList<String> roles = secretHitler.getRoles();
//		ArrayList<Player> players = new ArrayList<Player>();
//		ArrayList<Integer> fascists = new ArrayList<Integer>();
//		for(int i =0; i < 2; i++) {
//			fascists.add(-1);
//		}
//		for(int i = 0; i < 5; i++) {
//			//RandomBot n_player = new RandomBot(roles.get(i), i);
//			MLBot n_player = new MLBot(roles.get(i), i+1);
//			players.add(n_player);
//			if(roles.get(i).equals("Hitler")) {
//				fascists.set(1, i+1);
//			} else if(roles.get(i).equals("Fascist")) {
//				fascists.set(0, i+1);
//			}
//		}
//		Collections.shuffle(players);
//		Collections.shuffle(players);
//		
//		/*GameRun game = new GameRun(players);*/
//		
//		/*ArrayList<String> showRoles = new ArrayList<String>();
//		showRoles = secretHitler.showRole(players);
//		printRoles(showRoles);*/
//		secretHitler.makePlayersState(players);
//		for(int i = 0; i < 5; i++) {
//			players.get(i).receiveRole(fascists);
//		}
//		secretHitler.start();
		
		
		NNTrainer n = new NNTrainer();
		int x = n.train();
		if(x < 0) {
			System.out.println("Training NOT SUCCESSFULL");
		} else {
			System.out.println("Training SUCCESSFULL");
		}
	}
	
	/*private static void printRoles(ArrayList<String> showRoles){
		if(showRoles.get(0).equals("Liberal")) {
			System.out.println("You are a Liberal");
		} else if(showRoles.get(0).equals("Hitler")) {
			System.out.println("You are Hitler and the other Fascist is Player "+ showRoles.get(1));
		} else {
			System.out.println("You are a Fascist and Player " + showRoles.get(2) + " is Hitler");
		}
	}*/
}
