import java.util.ArrayList;
import java.util.Scanner;

public class Player {
	private String role;
	private int state;
	Scanner sc;
	
	public Player(String role, int state) {
		this.role = role;
		this.state = state;
		sc = new Scanner(System.in);
	}
	
	public String getRole() {
		return role;
	}
	
	public int getState() {
		return state;
	}
	
	public int chooseChancellor(int president, int lastChancellor) {
		int chosen;
		chosen = sc.nextInt();		
		while(chosen == lastChancellor || chosen == president){		
			if(chosen == president) {
				System.out.println("You cannot choose yourself as the Chancellor");
			}else if(chosen == lastChancellor) {
				System.out.println("You cannot choose Player " + lastChancellor + " as the Chancellor because he was the Chancellor last round");
			}
			chosen = sc.nextInt();
		}
		return chosen;
	}
	
	public String vote() {
		return sc.next();
	}
	
	public int discardCard(String one, String two, String three) {
		System.out.println(one + "(1)\n"+ two + "(2)\n" + three + "(3)");
		return sc.nextInt()-1;
	}
	
	public int discardCard(String one, String two) {
		System.out.println(one + "(1)\n"+ two + "(2)");
		return sc.nextInt()-1;
	}
	
	public void checkThreeCards(String one, String two, String three) {
		System.out.println(one+"\n"+two+"\n"+three);
	}
	
	public int killPlayer(ArrayList<Integer> players) { //MAIS TARDE MUDAR POR CAUSA DA CENA DO PLAYER 0
		System.out.println("President, choose one player to kill");
		int choice = sc.nextInt();
		boolean canKill = false;
		players.remove(state);
		if(players.contains(choice)) {
			canKill = true;
		}
		while(!canKill) {
			System.out.println("That player doesn't exist, was killed or is you!");
			choice = sc.nextInt();
			canKill = players.contains(choice);			
		}
		
		return choice;
	}
}
