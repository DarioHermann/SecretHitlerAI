import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

public class InteligBot extends Player{
	private int otherFascist;
	Random rnd;
	
//	GameVariables
	private int deckPolicies;
	private int libCardsEnacted;
	private int fasCardsEnacted;
	private int supposedLibCards;
	private int supposedFasCards;
	private int electionTracker;
	private int[] threeCards;
	private boolean saw3Cards;
	
//	NN input Variables
	private int pres;
	private int chanc;
	private int lastChanc;
	private int lastPres;
	private float lastPlayed;
	
//	Other Players
	private LinkedList<PlayerModel> otherPlayers;
//	private PlayerModel p1;
//	private PlayerModel p2;
//	private PlayerModel p3;
//	private PlayerModel p4;
	
	public InteligBot(String role, int state) {
		super(role, state);
		typeOfPlayer = 1;
		saw3Cards = false;
		
		deckPolicies = 17;
		libCardsEnacted = 0;
		fasCardsEnacted = 0;
		supposedLibCards = 0;
		supposedFasCards = 0;
		electionTracker = 0;
		threeCards = new int[3];
		for(int i = 0; i < 3; i++) {
			threeCards[i] = 0;
		}
		
		pres = 0;
		chanc = 0;
		lastChanc = 0;
		lastPres = 0;
		lastPlayed = 0;
		
		rnd = new Random(System.currentTimeMillis());
		otherPlayers = new LinkedList<PlayerModel>();
		for(int i = 0; i < 5; i++) {
			otherPlayers.add(new PlayerModel());
		}
		
		
	}
	
	public void receiveRole(ArrayList<Integer> showRoles) {
		if(role.equals("Hitler")) {
			otherFascist = showRoles.get(0);
			for(int i = 0; i < 5; i++) {
				otherPlayers.get(i).setRole("Liberal");
			}
			otherPlayers.get(otherFascist-1).setRole("Fascist");
			otherPlayers.get(otherFascist-1).setTrustLevel(2.0f);
		} else if(role.equals("Fascist")) {
			otherFascist = showRoles.get(1);
			for(int i = 0; i < 5; i++) {
				otherPlayers.get(i).setRole("Liberal");
			}
			otherPlayers.get(otherFascist-1).setRole("Hitler");
			otherPlayers.get(otherFascist-1).setTrustLevel(2.0f);
		} else {
			otherFascist = 0;
		}
		
		otherPlayers.get(state-1).setRole("Me");
	}
	
	public void isNotHitler(int chancellor) {
		otherPlayers.get(chancellor-1).detectHitler(false);
	}
	
	private void analyzePlay(float[] inps, float[] out) {
		
	}
	
	public int chooseChancellor(int president, int lastChancellor, ArrayList<Integer> players) {
		pres = president;
		chanc = 0;
		ArrayList<Integer> pls = new ArrayList<Integer>(players);
		pls.remove((Object) president);
		pls.remove((Object) lastChanc);
		
		float[] out = new float[5];
		int[] choose = new int[5];
		boolean[] confirmedNotHitler = new boolean[5];
		int confNoHitlers = 0;
		for(int i = 0; i < 5; i++) {
			confirmedNotHitler[i] = !otherPlayers.get(i).isHeHitler();
			if(confirmedNotHitler[i]) {
				confNoHitlers++;
			}
			out[i] = otherPlayers.get(i).getTrustLevel() + otherPlayers.get(i).getTheirTrustLevel();
			choose[i] = i+1;
		}
		
		for(int i = 0; i < 4; i++) {
			for(int j = 0; j < 4 - i; j++) {
				if(out[j] < out[j+1]) {
					float x = out[j];
					out[j] = out[j+1];
					out[j+1] = x;
					
					int y = choose[j];
					choose[j] = choose[j+1];
					choose[j+1] = y;
				}
			}
		}
		if(libCardsEnacted+fasCardsEnacted >= 0 && libCardsEnacted+fasCardsEnacted <= 3) {
			int res = state + 2 % 5 + 1;
			if(pls.contains(res)) {
				return res;
			}
		}
		if(role.equals("Fascist")) {
			if(pls.contains(otherFascist)) {
				for(int i = 0; i < 3; i++) {
					if(choose[i] == otherFascist) {
						return choose[i];
					}
				}
			}
		}
		if(confNoHitlers > 0) {
			for(int i = 0; i < 3; i++) {
				if(confirmedNotHitler[choose[i]-1] && pls.contains(choose[i])) {
					if(rnd.nextFloat() < 0.8) {
						return choose[i];
					}
				}
			}
			for(int i = 0; i < 5; i++) {
				if(pls.contains(choose[i])) {
					return choose[i];
				}
			}
		} else {
			for(int i = 0; i < 5; i++) {
				if(pls.contains(choose[i])) {
					return choose[i];
				}
			}
		}
		
		return choose[4];
	}
	
	public String vote(int president, int chancellor) {
		pres = president;
		chanc = chancellor;
		float[] out;
		
		if(president == state) {
			return "Y";
		} else if(chancellor == state) {
			if(otherPlayers.get(president-1).getTrustLevel() < 0) {
				if(role.equals("Hitler") && fasCardsEnacted >= 3) {
					return "Y";
				}
				return "N";
			} else {
				return "Y";
			}
		}
		
		if(role.equals("Liberal") && electionTracker == 2) {
			if(supposedFasCards + fasCardsEnacted > supposedLibCards + libCardsEnacted) {
				return "N";
			}
			return "Y";
			
		}
		
		if(libCardsEnacted+fasCardsEnacted >= 0 && libCardsEnacted+fasCardsEnacted <= 3) {
			if(president != chancellor+1 || president != chancellor-1) {
				return "Y";
			} else {
				return "N";
			}
		}
		
		if(role.equals("Fascist") && fasCardsEnacted >= 3) {
			if(chancellor == otherFascist) {
				return "Y";
			}
		}
		
		if(role.equals("Fascists") || role.equals("Hitler")) {
			if(president == otherFascist || chancellor == otherFascist) {
				if(otherPlayers.get(otherFascist-1).getTrustLevel() < 1.0) {
					return "N";
				} else {
					return "Y";
				}
			}
		}
		
		float[] trusts = new float[4];
		
		for(int i = 0; i < 4; i++) {
			if(i == state-1) {
				trusts[i] = otherPlayers.get(i).getTrustLevel();
			}
		}
		
		for(int i = 0; i < 3; i++) {
			for(int j = 0; j < 3-i; j++) {
				if(trusts[j] > trusts[j+1]) {
					float x = trusts[j];
					trusts[j] = trusts[j+1];
					trusts[j+1] = x;
				}
			}
		}
		
		for(int i = 0; i < 2; i++) {
			if(otherPlayers.get(president-1).getTrustLevel() <= trusts[i] || otherPlayers.get(chancellor-1).getTrustLevel() <= trusts[i]) {
				return "N";
			}
		}

		return "Y";
	}
	
	public int discardCard(String one, String two, String three) {
		int[] _pol = new int[3];
		int crd=0;
		String[] pol = new String[3];
		pol[0] = one;
		pol[1] = two;
		pol[2] = three;
		for(int i = 0; i < 3; i++) {
			_pol[i] = pol[i].equals("Liberal") ? 1 : -1;
			if(_pol[i] == 1) {
				supposedLibCards++;
			} else {
				supposedFasCards++;
			}
			crd += _pol[i];
		}
		
		
		if(crd == -3 || crd == 3) { //if FFF or LLL
			return 0;
		} else if(crd == -1) { //if LFF
			if(role.equals("Liberal")) { //if LFF and Liberal
				if(_pol[0] == -1) {
					return 0;
				} else {
					return 1;
				}
			} else { //if LFF and !Liberal
				if(fasCardsEnacted >= 3) { //if LFF, !Liberal and at least 3 fascist cards enacted
					if(fasCardsEnacted == 5) { //if LFF, !Liberal and 5 fascist cards enacted
						if(_pol[0] == 1) {
							return 0;
						} else if(_pol[1] == 1){
							return 1;
						} else {
							return 2;
						}
					} else if(supposedFasCards <= 4) { //if LFF, !Liberal, at least 3 fascist cards enacted and supposed fascist cards drawn <= 4
						if(_pol[0] == 1) {
							return 0;
						} else if(_pol[1] == 1){
							return 1;
						} else {
							return 2;
						}
					} else { //if LFF, !Liberal, at least 3 fascist cards enacted and supposed fascist cards drawn > 4
						if(_pol[0] == -1) {
							return 0;
						} else {
							return 1;
						}
					}
				} else { //if LFF, !Liberal and less than 3 fascist cards enacted
					if(_pol[0] == -1) {
						return 0;
					} else {
						return 1;
					}
				}
			}
		} else { //if LLF
			if(role.equals("Liberal")) { //if LLF and Liberal
				if(libCardsEnacted == 4 || fasCardsEnacted >= 4) { //if LLF, Liberal and libCards Enacted = 4 or fascist cards enacted >= 4
					if(_pol[0] == -1) {
						return 0;
					} else if(_pol[1] == -1){
						return 1;
					} else {
						return 2;
					}
				} else { //if LLF Liberal and libCards enacted < 4 & fascist cards enacted <4
					if(_pol[0] == 1) {
						return 0;
					} else {
						return 1;
					}
				}
			} else { //if LLF and !Liberal
				if(_pol[0] == 1) {
					return 0;
				} else {
					return 1;
				}
			}
		}	
	}
	
	public int discardCard(String one, String two, boolean veto) {
		int[] _pol = new int[2];
		String[] pol = new String[2];
		pol[0] = one;
		pol[1] = two;
		
		int crd = 0;
		
		
		for(int i = 0; i < 2; i++) {
			_pol[i] = pol[i].equals("Liberal") ? 1 : -1;
			crd += _pol[i];
		}
		
		
		if(crd == 2) { //if LL
			if((veto && !role.equals("Liberal")) && libCardsEnacted == 4){ //if LL, !Liberal, fas Cards Enacted = 5 and lib Cards Enacted = 4
				return 2; //Veto
			} else { // Don't Veto
				return 0;
			}
		} else if(crd == -2) { //if FF
			if(veto && role.equals("Liberal")) { //if FF, Liberal and fas Cards Enacted = 5
				return 2; //VETO
			} else { //if FF and fas Cards Enacted < 5
				return 0;
			}
		} else { //if LF
			if(role.equals("Liberal")) { //if LF and Liberal
				if(_pol[0] == -1) {
					return 0;
				} else {
					return 1;
				}
			} else { //if LF and !Liberal
				if(fasCardsEnacted == 5 || libCardsEnacted > 2) { //if LF, !Liberal and fasCardsEnacted = 5
					if(_pol[0] == 1) {
						return 0;
					} else {
						return 1;
					}
				} else {
					if(rnd.nextFloat() > 0.60) { //greater prob to choose discard Fascist
						if(_pol[0] == -1) {
							return 0;
						} else {
							return 1;
						}
					} else {
						return 0;
					}
				}
			}
		}
	}
	
	public boolean voteVeto(String one, String two) {
		int[] _pol = new int[2];
		String[] pol = new String[2];
		pol[0] = one;
		pol[1] = two;
		
		int crd = 0;
		
		for(int i = 0; i < 2; i++) {
			_pol[i] = pol[i].equals("Liberal") ? 1 : -1;
			crd += _pol[i];
		}
		
		if(crd == -2) {
			if(role.equals("Liberal")) {
				return true;
			} else {
				return false;
			}
		} else if(crd == 2) {
			if(role.equals("Liberal")) {
				return false;
			} else {
				return true;
			}
		} else {
			return false;
		}
	}
	
	
	
	public int killPlayer(ArrayList<Integer> players) {
		float[] pl = new float[5];
		int[] num = new int[5];
		
		for(int i = 0; i < 5; i++) {
			pl[i] = otherPlayers.get(i).getTrustLevel();
			num[i] = i+1;
		}
		
		for(int i = 0; i < 4; i++) {
			for(int j = 0; j < 4-i; j++) {
				if(pl[j] > pl[j+1]) {
					float x = pl[j];
					pl[j] = pl[j+1];
					pl[j+1] = x;
					
					int y = num[j];
					num[j] = num[j+1];
					num[j+1] = y;
				}
			}
		}
		
		if(role.equals("Liberal")) {
			if(num[1] != state && (otherPlayers.get(num[1]-1).isHeHitler() && !otherPlayers.get(num[1]-1).getDeathStatus())) {
				return num[1];
			} else {
				for(int i = 0; i < 5; i++) {
					if(num[i] != state && (otherPlayers.get(num[i]-1).isHeHitler() && !otherPlayers.get(num[i]-1).getDeathStatus())) {
						return num[i];
					}
				}
			}
		} else {
			for(int i = 0; i < 5; i++) {
				if(num[i] != state && num[i] != otherFascist) {
					if(otherPlayers.get(num[i]-1).isHeHitler() && !otherPlayers.get(num[i]-1).getDeathStatus()) {
						return num[i];
					}
				}
			}
		}
		
		return 0;
	}
	
	
	public String tellCards(int one, int two, int three, int enacted) {
		lastPlayed = enacted;
		int _pol[] = new int[3];
		_pol[0] = one;
		_pol[1] = two;
		_pol[2] = three;
		
		int crd = 0;
		
		int choice = 0;
		
		for(int i = 0; i < 3; i++) {
			crd+=_pol[i];
		}
		
		if(role.equals("Liberal")) {
			switch(crd) {
			case -3:
				choice = 3;
				break;
			case -1:
				choice = 2;
				break;
			case 1:
				choice = 1;
				break;
			case 3:
				choice = 0;
				break;
			default:
				choice = -1;
				break;
			}
		} else {
			switch(crd) {
			case -3:
				choice = 3;
				break;
			case -1:
				if(lastPlayed == -1) {
					if(otherFascist == chanc || !role.equals("Liberal")){
						choice = 3;
					} else {
						choice = 2;
					}
				} else {
					choice = 2;
				}
				break;
			case 1:
				if(lastPlayed == -1) {
					if(otherFascist == chanc) {
						choice = 3;
					} else {
						choice = 1;
					}
				} else {
					choice = 1;
				}
				break;
			case 3:
				choice = 0;
				break;
			default:
				choice = -1;
				break;
			}
		}
		
		String[] results = new String[4];
		
		results[0] = "3 Liberals 0 Fascists";
		results[1] = "2 Liberals 1 Fascist";
		results[2] = "1 Liberal 2 Fascists";
		results[3] = "0 Liberals 3 Fascists";
		
		return results[choice];
	}
	
	
	public String tellCards(int one, int two, int enacted) {
		lastPlayed = enacted;
		int[] _pol = new int[2];
		_pol[0] = one;
		_pol[1] = two;
		
		int crd = 0;
		
		for(int i = 0; i < 2; i++) {
			crd += _pol[i];
		}
		
		int choice = -1;
		
		if(role.equals("Liberal")) {
			switch(crd) {
			case -2:
				choice = 2;
				break;
			case 0:
				choice = 1;
				break;
			case 2:
				choice = 0;
				break;
			}
		} else {
			switch(crd) {
			case -2:
				choice = 2;
				break;
			case 0:
				if(enacted == -1) {
					choice = 2;
				} else {
					choice = 1;
				}
			case 2:
				choice = 0;
				break;
			}
		}
		
		
		
		String[] results = new String[3];
		results[0] = "2 Liberals 0 Fascists";
		results[1] = "1 Liberal 1 Fascists";
		results[2] = "0 Liberals 2 Fascists";
		
		return results[choice];
	}
	
	
	public void cardsTold(int president, int chancellor, String toldP, String toldC) {
		String[] linesP = toldP.split(" ");
		String[] linesC = toldC.split(" ");
		int libsP = Integer.parseInt(linesP[0]);
		int fasP = Integer.parseInt(linesP[2]);
		int libsC = Integer.parseInt(linesC[0]);
		int fasC = Integer.parseInt(linesC[2]);
		
		supposedLibCards += libsP;
		supposedFasCards += fasP;
		
		if(saw3Cards) {
			int libers = 0;
			int fascer = 0;
			for(int i = 0; i < 3; i++) {
				if(threeCards[i] == 1) {
					supposedLibCards++;
					libers++;
				} else {
					supposedFasCards++;
					fascer++;
				}
			}
			if(libers != libsP && role.equals("Liberal")) {
				otherPlayers.get(president-1).decreaseTrust();
				otherPlayers.get(president-1).decreaseTrust();
				otherPlayers.get(president-1).decreaseTrust();
			}
			if(!((libers == libsC && fascer == fasC+1) || (libers == libsC+1 && fascer == fasC))) {
				if(role.equals("Liberal")) {
					otherPlayers.get(chancellor-1).decreaseTrust();
					otherPlayers.get(chancellor-1).decreaseTrust();
					otherPlayers.get(chancellor-1).decreaseTrust();
				}
			}
			saw3Cards = false;
		} else {
			if((libsP == libsC && fasP == fasC+1)||(libsP == libsC+1 && fasP == fasC)) {
			
			} else {
				otherPlayers.get(president-1).decreaseTrust();
				otherPlayers.get(chancellor-1).decreaseTrust();
			}
		}
		
	}
	
	public void cardsTold(int president, String toldP, String toldC, int cardOne, int cardTwo) {
		String[] linesP = toldP.split(" ");
		String[] linesC = toldC.split(" ");
		int libsP = Integer.parseInt(linesP[0]);
		int fasP = Integer.parseInt(linesP[2]);
		int libC = Integer.parseInt(linesC[0]);
		int faC = Integer.parseInt(linesC[2]);
		
		int libsC = 0;
		int fasC = 0;
		
		if(cardOne == 1) {
			libsC++;
		} else {
			fasC++;
		}
		if(cardTwo == 1) {
			libsC++;
		} else {
			fasC++;
		}
		
		
		if(saw3Cards) {
			int libers = 0;
			int fascer = 0;
			for(int i = 0; i < 3; i++) {
				if(threeCards[i] == 1) {
					supposedLibCards++;
					libers++;
				} else {
					supposedFasCards++;
					fascer++;
				}
				if(libers != libsP && role.equals("Liberal")) {
					otherPlayers.get(president-1).decreaseTrust();
					otherPlayers.get(president-1).decreaseTrust();
					otherPlayers.get(president-1).decreaseTrust();
				}
			}
			saw3Cards = false;
		} else {
			if((libsP == libsC && fasP == fasC+1)|| (libsP == libsC+1 && fasP == libsC)) {
				supposedLibCards += libsP;
				supposedFasCards += fasP;
				if (!((libsP == libC && fasP == faC+1) || (libsP == libC+1 && fasP == faC))) {
					for(int i = 0; i < 5; i++) {
						if(i != state-1) {
							otherPlayers.get(i).decreaseTheirTrust();
							otherPlayers.get(i).decreaseTheirTrust();
						}
					}
				}
			} else if ((libsP == libC && fasP == faC+1) || (libsP == libC+1 && fasP == faC)){
				otherPlayers.get(president-1).increaseTrust();
				otherPlayers.get(president-1).increaseTrust();
				supposedLibCards += libsC;
				supposedFasCards += fasC+1;
			}else {
				otherPlayers.get(president-1).decreaseTrust();
				otherPlayers.get(president-1).decreaseTrust();
				for(int i = 0; i < 5; i++) {
					if(i != state-1) {
						otherPlayers.get(i).decreaseTheirTrust();
						otherPlayers.get(i).decreaseTheirTrust();
					}
				}
				supposedLibCards += libsC;
				supposedFasCards += fasC+1;
			
			}
		}
		
	}
	
	public void cardsTold(int chancellor, String toldP, String toldC, int cardOne, int cardTwo, int cardThree) {
		String[] linesP = toldP.split(" ");
		String[] linesC = toldC.split(" ");
		int libP = Integer.parseInt(linesP[0]);
		int faP = Integer.parseInt(linesP[2]);
		int libsC = Integer.parseInt(linesC[0]);
		int fasC = Integer.parseInt(linesC[2]);
		
		int libsP = 0;
		int fasP = 0;
		
		if(cardOne == 1) {
			libsP++;
		} else {
			fasP++;
		}
		if(cardTwo == 1) {
			libsP++;
		} else {
			fasP++;
		}
		if(cardThree == 1) {
			libsP++;
		} else {
			fasP++;
		}
		
		if((libsP == libsC && fasP == fasC+1)|| (libsP == libsC+1 && fasP == libsC)) {
			if(!((libP == libsC && faP == fasC+1) || (libP == libsC+1 && faP == fasC))){
				for(int i = 0; i < 5; i++) {
					if(i != state-1) {
						otherPlayers.get(i).decreaseTheirTrust();
						otherPlayers.get(i).decreaseTheirTrust();
					}
				}
			}
		}else if((libP == libsC && faP == fasC+1) || (libP == libsC+1 && faP == fasC)){
			otherPlayers.get(chancellor-1).increaseTrust();
			otherPlayers.get(chancellor-1).increaseTrust();
		}else {
			otherPlayers.get(chancellor-1).decreaseTrust();
			otherPlayers.get(chancellor-1).decreaseTrust();
			for(int i = 0; i < 5; i++) {
				if(i != state-1) {
					otherPlayers.get(i).decreaseTheirTrust();
					otherPlayers.get(i).decreaseTheirTrust();
				}
			}
		}
		
	}
	
	public void checkThreeCards(String one, String two, String three) {
		saw3Cards = true;
		String[] pl = new String[3];
		pl[0] = one;
		pl[1] = two;
		pl[2] = three;
		
		for(int i = 0; i < 3; i++) {
			threeCards[i] = pl[i].equals("Liberal") ? 1 : -1;
		}
	}
	
	public void policyEnacted(int policy) {
		if(policy == 1) {
			libCardsEnacted++;
		} else {
			fasCardsEnacted++;
		}
		electionTracker = 0;
	}
	
	public void checkPlay(String _play) {
		String[] play = _play.split(",");
		
		int gotKilled = 0;
		boolean fascistVictory = false;
		boolean liberalVictory = false;
		boolean hitlerKilled = false;
		boolean hitlerVoted = false;
		boolean _veto = false;
		boolean _noVeto = false;
		
		int optionTrust = -1;
		
		System.out.println(_play);
		int _pres = Integer.parseInt(play[0]);
		int _chanc = Integer.parseInt(play[1]);
		
		
		
		lastPres = _pres;
		lastChanc = _chanc;
		lastPlayed = 0;
		
		if(play[2].equals("Y")) { //president and chancellor got accepted
			if(deckPolicies < 3) {
				deckPolicies = 17 - libCardsEnacted - fasCardsEnacted;
				supposedFasCards = 0;
				supposedLibCards = 0;
			}
			deckPolicies -=3;
			if(play[3].equals("L")) { //played liberal policy
				lastPlayed = 1;
				if(_pres != state) {
					otherPlayers.get(_pres-1).increaseTrust();
				} else {
					optionTrust = 0;
				}
				if(_chanc != state) {
					otherPlayers.get(_chanc-1).increaseTrust();
				} else {
					optionTrust = 0;
				}
			} else if(play[3].equals("F")) { //played Fascist policy
				lastPlayed = -1;
				if(_pres != state) {
					if(_pres != otherFascist) {
						otherPlayers.get(_pres-1).decreaseTrust();
					} else {
						otherPlayers.get(_pres-1).increaseTrust();
					}
				} else {
					optionTrust = 1;
				}
				if(_chanc != state) {
					if(_chanc != otherFascist) {
						otherPlayers.get(_chanc-1).decreaseTrust();
					} else {
						otherPlayers.get(_chanc-1).increaseTrust();
					}
				} else {
					optionTrust = 1;
				}
				
				if(play.length > 4 && play[4].equals("FW")) { //if fascists win
					fascistVictory = true;
				}
				else if(play.length > 4) { //if someone got killed
					gotKilled = Integer.parseInt(play[4]);
					if(play.length > 5) { //if Hitler died
						hitlerKilled = true;
						liberalVictory = true;
					}
				}
			} else if(play[3].equals("HW")) { //if Hitler is voted Chancellor after 3 fascist policies are enacted
				hitlerVoted = true;
				fascistVictory = true;
			} else if(play[3].equals("V")) { //If Veto occurred
				_veto = true;
				if(play.length > 4) { //If election Tracker reached the limit
					if(deckPolicies < 1) {
						deckPolicies = 17 - libCardsEnacted - fasCardsEnacted;
						supposedFasCards = 0;
						supposedLibCards = 0;
					}
					deckPolicies--;
					if(play[4].equals("L")) {  //liberal policy came out
						saw3Cards = false;
						if(_pres != state) {
							otherPlayers.get(_pres-1).increaseTheirTrust();
						} else {
							optionTrust = 0;
						}
						if(_chanc != state) {
							otherPlayers.get(_chanc-1).increaseTheirTrust();
						} else {
							optionTrust = 0;
						}
						
						if(play.length > 5) {
							liberalVictory = true;
						}
					} else if(play[4].equals("F")) { //fascist policy came out, fascists win because if veto is on and fascist policy came out, they win because they enacted 6 policies
						saw3Cards = false;
						if(_pres != state) {
							if(_pres != otherFascist) {
								otherPlayers.get(_pres-1).decreaseTrust();
							}
						} else {
							optionTrust = 2;
						}
						if(_chanc != state) {
							if(_chanc != otherFascist) {
								otherPlayers.get(_chanc-1).decreaseTrust();
							}
						} else {
							optionTrust = 2;
						}
						
						fascistVictory = true;
					}
				}
			} else if (play[3].equals("NV")) { //If the President doesn't accept the Veto
				_noVeto = true;
				if(play[4].equals("F")) { //If the Chanc chooses Fascist policy
					lastPlayed = -1;
					if(_pres != state) {
						if(_pres != otherFascist) {
							otherPlayers.get(_pres-1).decreaseTrust();
							otherPlayers.get(_pres-1).decreaseTrust();
						}
						otherPlayers.get(_pres-1).decreaseTrust();
					} else {
						optionTrust = 4;
					}
					if(_chanc != state) {
						otherPlayers.get(_chanc-1).increaseTrust();
						otherPlayers.get(_chanc-1).increaseTrust();
						otherPlayers.get(_chanc-1).increaseTrust();
					} else {
						optionTrust = 0;
					}
				} else if (play[4].equals("L")) { //If the Chanc chooses Liberal Policy
					lastPlayed = 1;
					if(_pres != state) {
						otherPlayers.get(_pres-1).increaseTrust();
						otherPlayers.get(_pres-1).increaseTrust();
						otherPlayers.get(_pres-1).increaseTrust();
					} else {
						optionTrust = 3;
					}
					if(_chanc != state) {
						if(_chanc != otherFascist){
							otherPlayers.get(_chanc-1).decreaseTrust();
							otherPlayers.get(_chanc-1).decreaseTrust();
						}
						otherPlayers.get(_chanc-1).decreaseTrust();
					} else {
						optionTrust = 4;
					}
						
					if(play.length > 5) {
						liberalVictory = true;
					}
				}
			}
		} else { //if The voters voted No on the President and Chancellor
			if(play.length > 4) { //If tracker reached limit
				if(deckPolicies < 1) {
					deckPolicies = 17 - libCardsEnacted - fasCardsEnacted;
					supposedFasCards = 0;
					supposedLibCards = 0;
				}
				deckPolicies--;
				if(play[4].equals("LW")) {
					liberalVictory = true;
				} else if(play[4].equals("FW")) {
					fascistVictory = true;
				}
			}
		}
		
		if(play.length == 3) { //so existe no caso de ambos o presidente e o chencellor nao terem sido aceites e o election tracker nao ter chegado ao limite
			electionTracker++;
		}
		if(play.length > 3) {
			if(_veto) {
				lastPlayed = (float) (play.length > 4 ? (play[4].equals("L") ? 1.5 : -1.5) : 0.0);
				saw3Cards = false;
			} else if(_noVeto) {
				lastPlayed = (float) (play[4].equals("L") ? 0.5: -0.5);
				saw3Cards = false;
			}
			if(gotKilled != 0) {
				otherPlayers.get(gotKilled-1).died();
			}
			if(hitlerKilled) {
				for(int i = 0; i < 5; i++) {
					otherPlayers.get(i).detectHitler(false);
				}
				otherPlayers.get(gotKilled-1).detectHitler(true);
			}
		}
		
		if(optionTrust != -1) {
			if(optionTrust == 0) {
				for(int i = 0; i < 5; i++) {
					if(i != state-1) {
						otherPlayers.get(i).increaseTheirTrust();
					}
				}
			} else if (optionTrust <= 2) {
				for(int i = 0; i < 5; i++) {
					if(i != state -1) {
						if(optionTrust == 1 && i == otherFascist -1) {
							otherPlayers.get(i).increaseTheirTrust();
						} else {
							otherPlayers.get(i).decreaseTheirTrust();
						}
					}
				}
			} else if(optionTrust == 3) {
				for(int i = 0; i < 5; i++) {
					if(i != state -1) {
						otherPlayers.get(i).increaseTheirTrust();
						otherPlayers.get(i).increaseTheirTrust();
						otherPlayers.get(i).increaseTheirTrust();
					}
				}
			} else if (optionTrust == 4) {
				for(int i = 0; i < 5; i++) {
					if(i != state -1) {
						otherPlayers.get(i).decreaseTheirTrust();
						if(i != otherFascist -1) {
							otherPlayers.get(i).decreaseTheirTrust();
							otherPlayers.get(i).decreaseTheirTrust();
						}
					}
				}
			}
		}
	}
}
