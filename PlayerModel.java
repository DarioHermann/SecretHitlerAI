import java.util.LinkedList;

public class PlayerModel {
	private String role="";
	private float trustLevel;
	private float theirTrustLevel;
	private boolean isHitler;
	private boolean isDead;
	private LinkedList<String> moves;
	
	public PlayerModel() {
		trustLevel = 0.1f;
		theirTrustLevel = 0.1f;
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
		String r = "" + trustLevel;
		if(trustLevel > 1) {
			trustLevel = ((-1/10)* (trustLevel * trustLevel) + 2 * trustLevel - (1/15)); // (-1/10)x^2 + 2x - 1/15
		}
		else if (trustLevel < -1) {
			trustLevel *= (2/3);
		}
		else if(trustLevel > 0) {
			trustLevel *= 4;
		}
		else if(trustLevel < 0) {
			trustLevel = (float) ((trustLevel/4)+0.2);
		}
		else {
			trustLevel += 0.1;
		}
	}
	
	public void decreaseTrust() {
		String r = "" + trustLevel;
		if(trustLevel > 1) {
			trustLevel *= (2/3);
		}
		else if(trustLevel < -1) {
			trustLevel = ((-3/2) * (trustLevel * trustLevel) - 5 * trustLevel - (11/2));
			trustLevel -= 0.5;
		}
		else if(trustLevel > 0) {
			trustLevel = (float) ((trustLevel/4) - 0.2);
		}
		else if(trustLevel < 0) {
			trustLevel *= 4;
		}
		else {
			trustLevel -= 0.1;
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
		String r = "" + theirTrustLevel;
		if(theirTrustLevel > 1) {
			theirTrustLevel = ((-1/10)* (theirTrustLevel * theirTrustLevel) + 2 * theirTrustLevel - (1/15)); // (-1/10)x^2 + 2x - 1/15
		}
		else if (theirTrustLevel < -1) {
			theirTrustLevel *= (2/3);
		}
		else if(theirTrustLevel > 0) {
			theirTrustLevel *= 4;
		}
		else if(theirTrustLevel < 0) {
			theirTrustLevel = (float) ((theirTrustLevel/2)+0.2);
		}
		else {
			theirTrustLevel += 0.1;
		}
	}
	
	public void decreaseTheirTrust() {
		String r = "" + theirTrustLevel;
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
			theirTrustLevel *= 4;
		}
		else {
			theirTrustLevel -= 0.1;
		}
	}
	
	public float getTheirTrustLevel() {
		return theirTrustLevel;
	}
	
	public void died() {
		isDead = true;
	}
	
	public boolean getDeathStatus() {
		return isDead;
	}
}
