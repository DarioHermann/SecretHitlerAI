import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

public class GameRun extends Game{
	
	private int lastPresident; //Current or Last (before choosing a new one)
	private int lastChancellor;
	private boolean vetoPowerOn;
	private boolean power;
	private String thePlay;
	
	public GameRun() {
		super();
		vetoPowerOn = false;
	}
	
	public void start() {
		Scanner sc = new Scanner(System.in);
		power = false;
		int chancellor = 0;
		int president = -1;
		int gamewin = -2;
		lastChancellor = 0;
		do{
			do {
				thePlay = "";
				president = (president+1)%players.size();
				lastPresident = president;
				chancellor = chooseChancellor(president);
			}while(chancellor == -1);
			lastChancellor = chancellor;
			if(chancellor != players.size()+1) {
				if(fasPolicies >= 3 && players.get(playersState.indexOf(chancellor)).getRole().equals("Hitler")) {
					thePlay += ",HW";
					System.out.println("Hitler was elected as the Chancellor after three fascist policies have been enacted");
					fasPolicies = 6;
				} else {
					choosePolicies(president, chancellor);
					checkPresidentialPower(president);
				}
			}
			gamewin = checkWin();
			System.out.println("\n\n");
			//sc.next();
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
		thePlay += pl;
		System.out.println("player " + playersState.get(pl) + " you're the President, choose the Chancellor");
		System.out.println(players);
		if(!playersState.contains(lastChancellor)) {
			lastChancellor = playersState.get(pl);
		}
		chosen = players.get(pl).chooseChancellor(playersState.get(pl), lastChancellor, playersState);
		thePlay += "," + chosen;
		System.out.println("The President chose Player " + chosen + " as the Chancellor, let's vote this decision\nDo you agree with this decision? Ja!/Nein!(Y/N)");
		ArrayList<String> votes = new ArrayList<String>();
		for(int i = 0; i < players.size(); i++) {
			votes.add(players.get(i).vote(playersState.get(pl), chosen));
		}
		int ja = Collections.frequency(votes, "Y");
		int nein = Collections.frequency(votes, "N");
		String print = ja + " voted Ja! and " + nein + " voted Nein!\nTherefore, ";
		if(ja > nein) {
			thePlay += ",Y";
			print += "the President and the Chancellor have been elected";
			System.out.println(print);
			return chosen;
		} else {
			thePlay += ",N";
			print += "the Election Tracker will move one space and a new President and Chancellor have to be chosen";
			System.out.println(print);
			electionTracker++;
			if(electionTracker == 3) {
				if(policiesDeck.size() < 1) {
					shuffleCards();
				}
				String policy = policiesDeck.remove(0);
				enactPolicy(policy);
				power = false;
				return players.size()+1;
			}
			for(int i = 0; i < players.size(); i++) {
				if(players.get(i).getTypeOfPlayer() == 1) {
					players.get(i).checkPlay(thePlay);
				}
			}
			return -1;
		}
	}
	
	private void choosePolicies(int president, int chancellor) {
		if(policiesDeck.size() < 3) {
			shuffleCards();
		}
		boolean veto = false;
		System.out.println("President, choose the card you want to discard:");
		int discard = players.get(president).discardCard(policiesDeck.get(0), policiesDeck.get(1), policiesDeck.get(2));
		policiesDeck.remove(discard);
		discard = players.get(playersState.indexOf(chancellor)).discardCard(policiesDeck.get(0), policiesDeck.get(1), vetoPowerOn);
		if(discard == 2) {
			veto = players.get(president).voteVeto(policiesDeck.get(0), policiesDeck.get(1));
			if(veto) {
				thePlay +=",V";
				electionTracker++;
				if(electionTracker == 3) {
					if(policiesDeck.size() < 1) {
						shuffleCards();
					}
					String policy = policiesDeck.remove(0);
					enactPolicy(policy);
					policiesDeck.remove(0);
				}
				discard = 3;
			} else if(vetoPowerOn){
				thePlay +=",NV";
				System.out.println("The President didn't accept your veto, Chancellor choose one of the policies in you hand");
				discard = players.get(chancellor).discardCard(policiesDeck.get(0), policiesDeck.get(1), false);
			}
		}
		if(discard < 3) {
			policiesDeck.remove(discard);
			String policy = policiesDeck.remove(0);
			enactPolicy(policy);
		}
	}
	
	
	private void enactPolicy(String policy) {
		if(policy.equals("Liberal")) {
			thePlay +=",L";
			libPolicies++;
			System.out.println("Liberal policy enacted (" + libPolicies + "/5)");
		} else {
			thePlay +=",F";
			fasPolicies++;
			power = true;
			System.out.println("Fascist policy enacted (" + fasPolicies + "/6)");
			
		}
		electionTracker = 0;
	}
	
	private int checkWin() {
		int result;
		if(libPolicies == 5) {
			thePlay += ",LW";
			result = 1;
		} else if (fasPolicies == 6) {
			thePlay += ",FW";
			result = 2;
		} else {
			result = 0;
		}
		
		for(int i = 0; i < players.size(); i++) {
			if(players.get(i).getTypeOfPlayer() == 1) {
				players.get(i).checkPlay(thePlay);
			}
		}
		return result;
	}
	
	private void checkPresidentialPower(int president) {
		if(power && fasPolicies == 3) {
			if(policiesDeck.size() < 3) {
				shuffleCards();
			}
			players.get(president).checkThreeCards(policiesDeck.get(0), policiesDeck.get(1), policiesDeck.get(2));
		}
		else if(power && (fasPolicies >= 4 && fasPolicies < 6)) {
			int eliminate = players.get(president).killPlayer(playersState);
			eliminate = playersState.indexOf(eliminate);
			System.out.println(playersState + "\n" + players);
			int psk = playersState.remove((int) eliminate);
			thePlay += "," + psk;
			Player shot = players.remove(eliminate);
			System.out.println(playersState + "\n" + players);
			if(shot.getRole().equals("Hitler")) {
				thePlay=",HD";
				libPolicies = 5;
				System.out.println("Congrats Liberals, Hitler has been shot!");
			}
		}
		if(fasPolicies == 5) {
			vetoPowerOn = true;
		}
		power = false;
	}
	
}
