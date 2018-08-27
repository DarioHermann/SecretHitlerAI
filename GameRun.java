import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

public class GameRun extends Game{
	
	private int lastPresident; //Current or Last (before choosing a new one)
	private int lastChancellor;
	private boolean vetoPowerOn;
	private boolean power;
	private String thePlay;

	private boolean gotKilled = false;
	private int whoGotKilled = 0;
	
	public GameRun() {
		super();
		vetoPowerOn = false;
	}
	
	public int start() {
		Scanner sc = new Scanner(System.in);
		power = false;
		int chancellor = 0;
		int president = -1;
		int gamewin = -2;
		lastChancellor = 0;
		do{
			do {
				thePlay = "";
				lastPresident = president;
				if(gotKilled) {
					if(whoGotKilled < president) {
						president--;
					}
					gotKilled = false;
				}
				president = (president+1)%players.size();
				chancellor = chooseChancellor(president);
			}while(chancellor == -1);
			lastChancellor = chancellor;
			if(chancellor != players.size()+1) {
				if(fasPolicies >= 3 && players.get(playersState.indexOf(chancellor)).getRole().equals("Hitler")) {
					thePlay += ",HW";
					System.out.println("Hitler was elected as the Chancellor after three fascist policies have been enacted");
					fasPolicies = 6;
				} else {
					if(fasPolicies >= 3) {
						for(int i = 0; i < players.size(); i++) {
							players.get(i).isNotHitler(chancellor);
						}
					}
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
			return 1;
		} else if(gamewin == 2){
			System.out.println("FASCISTS WON!");
			return 2;
		} else {
			return 0;
		}
	}
	
	private int chooseChancellor(int pl) {
		int chosen;
		thePlay += playersState.get(pl);
		System.out.println("player " + playersState.get(pl) + " you're the President, choose the Chancellor");
		//System.out.println(players);
		if(!playersState.contains(lastChancellor)) {
			lastChancellor = playersState.get(pl);
		}
		chosen = players.get(pl).chooseChancellor(playersState.get(pl), lastChancellor, playersState);
		lastChancellor = chosen;
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
		int pone = policiesDeck.get(0).equals("Liberal") ? 1 : -1;
		int ptwo = policiesDeck.get(1).equals("Liberal") ? 1 : -1;
		int pthree = policiesDeck.get(2).equals("Liberal") ? 1 : -1;
		for(int i = 0; i < 3; i++) {
			System.out.println(pone + " " + policiesDeck.get(0));
			System.out.println(ptwo + " " + policiesDeck.get(1));
			System.out.println(pthree + " " + policiesDeck.get(2));
		}
		System.out.println("President, choose the card you want to discard:");
		int discard = players.get(president).discardCard(policiesDeck.get(0), policiesDeck.get(1), policiesDeck.get(2));
		policiesDeck.remove(discard);
		int cone = policiesDeck.get(0).equals("Liberal") ? 1 : -1;
		int ctwo = policiesDeck.get(1).equals("Liberal") ? 1 : -1;
		for(int i = 0; i < 3; i++) {
			System.out.println(cone + " " + policiesDeck.get(0));
			System.out.println(ctwo + " " + policiesDeck.get(1));
		}
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
				discard = players.get(playersState.indexOf(chancellor)).discardCard(policiesDeck.get(0), policiesDeck.get(1), false);
			}
		}
		if(discard < 3) {
			policiesDeck.remove(discard);
			String policy = policiesDeck.remove(0);
			enactPolicy(policy);
			System.out.println("President, tell what cards you had:");
			String presTold = players.get(president).tellCards(pone, ptwo, pthree, policy.equals("Liberal") ? 1 : -1);
			System.out.println("Chancellor, tell what cards you had:");
			String chancTold = players.get(playersState.indexOf(chancellor)).tellCards(cone, ctwo, policy.equals("Liberal") ? 1 : -1);
			
			for(int i = 0; i < players.size(); i++) {
				if(i != president && i != playersState.indexOf(chancellor)) {
					players.get(i).cardsTold(playersState.get(president), chancellor, presTold, chancTold);
				} else if(i != president) {
					players.get(i).cardsTold(playersState.get(president), presTold, chancTold, cone, ctwo);
				} else {
					players.get(i).cardsTold(chancellor, presTold, chancTold, pone, ptwo, pthree);
				}
			}
		}
		
	}
	
	
	private void enactPolicy(String policy) {
		if(policy.equals("Liberal")) {
			thePlay +=",L";
			libPolicies++;
			System.out.println("Liberal policy enacted (" + libPolicies + "/5)");
			for(int i = 0; i < players.size(); i++) {
				players.get(i).policyEnacted(1);
			}
		} else {
			thePlay +=",F";
			fasPolicies++;
			power = true;
			System.out.println("Fascist policy enacted (" + fasPolicies + "/6)");
			for(int i = 0; i < players.size(); i++) {
				players.get(i).policyEnacted(-1);
			}
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
			gotKilled = true;
			whoGotKilled = eliminate;
			System.out.println(playersState + "\n" + players);
			int psk = playersState.remove((int) eliminate);
			thePlay += "," + psk;
			Player shot = players.remove(eliminate);
			System.out.println(playersState + "\n" + players);
			if(shot.getRole().equals("Hitler")) {
				thePlay+=",HD";
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
