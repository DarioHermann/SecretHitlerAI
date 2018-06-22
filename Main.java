import java.util.ArrayList;
import java.util.Collections;

public class Main {
	public static void main(String[] args) {
		
		Game secretHitler = new GameRun();
		ArrayList<String> roles = secretHitler.getRoles();
		ArrayList<Player> players = new ArrayList<Player>();
		for(int i = 0; i < 5; i++) {
			Player n_player = new Player(roles.get(i), i);
			players.add(n_player);
		}
		Collections.shuffle(players);
		Collections.shuffle(players);
		
		/*GameRun game = new GameRun(players);*/
		
		ArrayList<String> showRoles = new ArrayList<String>();
		showRoles = secretHitler.showRole(players);
		printRoles(showRoles);
		secretHitler.start();
	}
	
	private static void printRoles(ArrayList<String> showRoles){
		if(showRoles.get(0).equals("Liberal")) {
			System.out.println("You are a Liberal");
		} else if(showRoles.get(0).equals("Hitler")) {
			System.out.println("You are Hitler and the other Fascist is Player "+ showRoles.get(1));
		} else {
			System.out.println("You are a Fascist and Player " + showRoles.get(2) + " is Hitler");
		}
	}
}
