import java.util.LinkedList;

public class PlayerModel {
	private String role="";
	private float trustLevel;
	private float theirTrustLevel;
	private boolean isHitler;
	private boolean isDead;
	private LinkedList<String> moves;
	
	public PlayerModel() {
		trustLevel = 0;
		theirTrustLevel = 0;
		isHitler = true;
		isDead = false;
		moves = new LinkedList<String>();
	}
	
	public void setRole(String s) {
		role = s;
	}
	
	public String getRole() {
		return role;
	}
	
	public void increaseTrust() {
		if(theirTrustLevel > 1) {
			theirTrustLevel = ((-1/10)* (theirTrustLevel * theirTrustLevel) + 2 * theirTrustLevel - (1/15)); // (-1/10)x^2 + 2x - 1/15
		}
		else if (theirTrustLevel < -1) {
			theirTrustLevel *= (2/3);
		}
		else if(theirTrustLevel > 0) {
			theirTrustLevel *= 2;
		}
		else if(theirTrustLevel < 0) {
			theirTrustLevel = (float) ((theirTrustLevel/2)+0.2);
		}
		else {
			theirTrustLevel += 0.1;
		}
	}
	
	public void decreaseTrust() {
		if(theirTrustLevel > 1) {
			theirTrustLevel *= (2/3);
		}
		else if(theirTrustLevel < -1) {
			theirTrustLevel = ((-3/2) * (theirTrustLevel * theirTrustLevel) - 5 * theirTrustLevel - (11/2));
			theirTrustLevel -= 0.5;
		}
		else if(theirTrustLevel > 0) {
			theirTrustLevel = (float) ((theirTrustLevel/2) + 0.2);
		}
		else if(theirTrustLevel < 0) {
			theirTrustLevel *= 2;
		}
		else {
			theirTrustLevel -= 0.1;
		}
	}
	
	public void setTrustLevel(float trust) {
		trustLevel = trust;
	}
	
	public float getTrustLevel() {
		return trustLevel;
	}
	
	public void detectHitler(boolean b) {
		isHitler = b;
	}
	
	public boolean isHeHitler() {
		return isHitler;
	}
	
	public void setMoves(String s) {
		moves.add(s);
	}
	
	public void increaseTheirTrust() {
		if(theirTrustLevel > 1) {
			theirTrustLevel = ((-1/10)* (theirTrustLevel * theirTrustLevel) + 2 * theirTrustLevel - (1/15)); // (-1/10)x^2 + 2x - 1/15
		}
		else if (theirTrustLevel < -1) {
			theirTrustLevel *= (2/3);
		}
		else if(theirTrustLevel > 0) {
			theirTrustLevel *= 2;
		}
		else if(theirTrustLevel < 0) {
			theirTrustLevel = (float) ((theirTrustLevel/2)+0.2);
		}
		else {
			theirTrustLevel += 0.1;
		}
	}
	
	public void decreaseTheirTrust() {
		if(theirTrustLevel > 1) {
			theirTrustLevel *= (2/3);
		}
		else if(theirTrustLevel < -1) {
			theirTrustLevel = ((-3/2) * (theirTrustLevel * theirTrustLevel) - 5 * theirTrustLevel - (11/2));
			theirTrustLevel -= 0.5;
		}
		else if(theirTrustLevel > 0) {
			theirTrustLevel = (float) ((theirTrustLevel/2) + 0.2);
		}
		else if(theirTrustLevel < 0) {
			theirTrustLevel *= 2;
		}
		else {
			theirTrustLevel -= 0.1;
		}
	}
	
	public float getTheirTrustLevel() {
		return theirTrustLevel;
	}
	
	public boolean getDeathStatus() {
		return isDead;
	}
}
