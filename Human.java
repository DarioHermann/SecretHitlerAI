import java.util.ArrayList;

public class Human extends Player{

	public Human(String role, int state) {
		super(role, state);
		System.out.println("You're player " + state);
	}
	
	public void receiveRole(ArrayList<Integer> showRoles) {
		if(role.equals("Liberal")) {
			System.out.println("You are a Liberal");
		} else if(role.equals("Hitler")) {
			System.out.println("You are Hitler and the other Fascist is Player "+ showRoles.get(0));
		} else {
			System.out.println("You are a Fascist and Player " + showRoles.get(1) + " is Hitler");
		}
	}
	
	public int chooseChancellor(int president, int lastChancellor, ArrayList<Integer> players) {
		int chosen;
		chosen = sc.nextInt();
		boolean canChoose = false;
		if(players.contains(chosen)) {
			canChoose = true;
		}
		while(!canChoose || (chosen == lastChancellor || chosen == president)){		
			if(chosen == president) {
				System.out.println("You cannot choose yourself as the Chancellor");
			}else if(chosen == lastChancellor) {
				System.out.println("You cannot choose Player " + lastChancellor + " as the Chancellor because he was the Chancellor last round");
			} else {
				System.out.println("That player doesn't exist or is already dead");
			}
			System.out.println("Choose another player");
			chosen = sc.nextInt();
			if(players.contains(chosen)) {
				canChoose = true;
			}
		}
		return chosen;
	}
	
	public String vote(int president, int chancellor) {
		String v = sc.next();
		v=v.toUpperCase();
		while(!v.equals("Y") && !v.equals("N")) {
			System.out.println("That is not a valid option, please choose \"Y\" for Ja!(Yes) or \"N\" for Nein!(No)");
			v = sc.next();
			v=v.toUpperCase();
		}
		return v;
	}
	
	public int discardCard(String one, String two, String three) {
		System.out.println(one + "(1)\n"+ two + "(2)\n" + three + "(3)");
		int discard = sc.nextInt();
		while(discard > 3 || discard < 1) {
			System.out.println("Please choose a valid option! 1/2/3");
			discard = sc.nextInt();
		}
		return discard-1;
	}
	
	public int discardCard(String one, String two, boolean veto) {
		System.out.println(one + "(1)\n"+ two + "(2)");
		if(veto) {
			System.out.println("The Veto Power is On, if you don't like any of the policies and want to discard them both choose 3");
		}
		int discard = sc.nextInt();
		while((!veto && discard == 3) || (discard > 3 || discard < 1)) {
			System.out.print("Please choose a valid option! 1/2");
			if(veto) {
				System.out.print("/3");
			}
			System.out.println("");
			
			discard = sc.nextInt();
		}
		return discard-1;
	}
	
	public boolean voteVeto(String one, String two) {
		System.out.println("President, do you want to veto? The policies are " + one + " and " + two + " Y/N");
		String vote = sc.next();
		vote = vote.toUpperCase();
		while(!vote.equals("Y") && !vote.equals("N")) {
			System.out.println("Not a valid input, please input \"Y\" for Yes and \"N\" for No");
			vote = sc.next();
			vote = vote.toUpperCase();
		}
		if(vote.equals("Y")) {
			return true;
		} else {
			return false;
		}
	}
	
	public void checkThreeCards(String one, String two, String three) {
		System.out.println(one+"\n"+two+"\n"+three);
	}
	
	public int killPlayer(ArrayList<Integer> players) { //MAIS TARDE MUDAR POR CAUSA DA CENA DO PLAYER 0
		System.out.println("President, choose one player to kill");
		int choice = sc.nextInt();
		boolean canKill = false;
		if(players.contains(choice) && choice != state) {
			canKill = true;
		}
		while(!canKill) {
			System.out.println("That player doesn't exist, was killed or is you!");
			choice = sc.nextInt();
			canKill = players.contains(choice);			
		}
		
		return choice;
	}
	
	public String tellCards(int one, int two, int three, int enacted) {
		
		
		String[] results = new String[4];
		
		results[0] = "3 Liberals 0 Fascists";
		results[1] = "2 Liberals 1 Fascist";
		results[2] = "1 Liberal 2 Fascists";
		results[3] = "0 Liberals 3 Fascists";

		return results[sc.nextInt()];
	}
	
	
	public String tellCards(int one, int two, int enacted) {
		
		String[] results = new String[3];
		results[0] = "2 Liberals 0 Fascists";
		results[1] = "1 Liberal 1 Fascists";
		results[2] = "0 Liberals 2 Fascists";
		
		return results[sc.nextInt()];
	}
}
