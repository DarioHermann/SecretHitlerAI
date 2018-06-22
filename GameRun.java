import java.util.ArrayList;
import java.util.Collections;

public class GameRun extends Game{
	
	private int lastPresident; //Current or Last (before choosing a new one)
	private int lastChancellor;
	
	public GameRun() {
		super();
	}
	
	public void start() {
		int chancellor = 0;
		int president = -1;
		int gamewin = -2;
		do{
			do {
				president = (president+1)%5;
				lastPresident = president;
				chancellor = chooseChancellor(president);
			}while(chancellor == -1);
			lastChancellor = chancellor;
			if(chancellor != players.size()) {
				choosePolicies(president, chancellor);
				checkPresidentialPower(president);
			}
			gamewin = checkWin();
		}while(gamewin<1);
		if(gamewin == 1) {
			System.out.println("LIBERALS WON!");
		} else if(gamewin == 2){
			System.out.println("FASCISTS WON!");
		} else {
			
		}
	}
	
	private int chooseChancellor(int pl) {
		int chosen;
		System.out.println("player " + playersState.get(pl) + " you're the President, choose the Chancellor");
		chosen = players.get(pl).chooseChancellor(pl, lastChancellor);
		System.out.println("The President chose Player " + chosen + " as the Chancellor, let's vote this decision\nDo you agree with this decision? Ja!/Nein!(Y/N)");
		ArrayList<String> votes = new ArrayList<String>();
		for(int i = 0; i < players.size(); i++) {
			votes.add(players.get(i).vote());
		}
		int ja = Collections.frequency(votes, "Y");
		int nein = Collections.frequency(votes, "N");
		String print = ja + " voted Ja! and " + nein + " voted Nein!\nTherefore, ";
		if(ja > nein) {
			print += "the President and the Chancellor have been elected";
			System.out.println(print);
			electionTracker=0;
			return chosen;
		} else {
			print += "the Election Tracker will move one space and a new President and Chancellor have to be chosen";
			System.out.println(print);
			electionTracker++;
			if(electionTracker == 3) {
				if(policiesDeck.size() < 1) {
					shuffleCards();
				}
				String policy = policiesDeck.remove(0);
				enactPolicy(policy);
				electionTracker = 0;
				return players.size();
			}
			return -1;
		}
	}
	
	private void choosePolicies(int president, int chancellor) {
		if(policiesDeck.size() < 3) {
			shuffleCards();
		}
		System.out.println("President, choose the card you want to discard:");
		int discard = players.get(president).discardCard(policiesDeck.get(0), policiesDeck.get(1), policiesDeck.get(2));
		policiesDeck.remove(discard);
		discard = players.get(chancellor).discardCard(policiesDeck.get(0), policiesDeck.get(1));
		policiesDeck.remove(discard);
		String policy = policiesDeck.remove(0);
		enactPolicy(policy);
	}
	
	
	private void enactPolicy(String policy) {
		if(policy.equals("Liberal")) {
			libPolicies++;
			System.out.println("Liberal policy enacted (" + libPolicies + "/5)");
		} else {
			fasPolicies++;
			System.out.println("Fascist policy enacted (" + fasPolicies + "/6)");
			
		}
	}
	
	private int checkWin() {
		if(libPolicies == 5) {
			return 1;
		} else if (fasPolicies == 6) {
			return 1;
		} else {
			return 0;
		}
	}
	
	private void checkPresidentialPower(int president) {
		if(fasPolicies == 3) {
			if(policiesDeck.size() < 3) {
				shuffleCards();
			}
			players.get(president).checkThreeCards(policiesDeck.get(0), policiesDeck.get(1), policiesDeck.get(2));
		}
		else if(fasPolicies == 4 || fasPolicies == 5) { // INCLUDE THE VETO POWER!
			int eliminate = players.get(president).killPlayer(playersState);
			eliminate = playersState.indexOf(eliminate);
			playersState.remove((int) eliminate);
			players.remove(eliminate);
		}
		if(fasPolicies == 5) {
			
		}
	}
	
}
