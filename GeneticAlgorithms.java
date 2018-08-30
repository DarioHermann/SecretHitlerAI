import java.util.Random;

public class GeneticAlgorithms {
	private float[][] survivors;
	private float[][] new_gen;
	Random rnd;
	
	public GeneticAlgorithms(float[][] surv) {
		survivors = new float[4][surv[0].length];
		new_gen = new float[20][surv[0].length];
		rnd = new Random(System.currentTimeMillis());
		
		for(int i = 0; i < 4; i++) {
			for(int j = 0; j < surv[i].length; j++) {
				survivors[i][j] = 0;
				new_gen[i][j] = 0;
			}
		}
		
		for(int i = 4; i < 20; i++) {
			for(int j = 0; j < surv[0].length; j++) {
				new_gen[i][j] = 0;
			}
		}
		
		for(int i = 0; i < surv.length; i++) {
			for(int j = 0; j < surv[i].length;j++) {
				survivors[i][j] = surv[i][j];
				new_gen[i][j] = surv[i][j];
			}
		}
	}
	
	public void generateChildren() {
		crossover(0, 1, 4, 5);
		crossover(2, 3, 6, 7);
		for(int i = 0; i < 8; i++) {
			mutation(i, i+8);
		}
		
		for(int i = 0; i < 2; i++) {
			generateRandom(i+16);
		}
		
		int x = rnd.nextInt(2); int y = rnd.nextInt(2)+2;
		
		crossover(x, y, 18, 19);
		
	}
	
	private void crossover(int first, int second, int son, int daughter) {
		boolean cross = false;
		
		for(int i = 0; i < survivors[first].length; i++) {
			if(rnd.nextFloat()>0.6) {
				cross = !cross;
			}
			if(!cross) {
				new_gen[son][i] = survivors[first][i];
				new_gen[daughter][i] = survivors[second][i];
			} else {
				new_gen[son][i] = survivors[second][i];
				new_gen[daughter][i] = survivors[first][i];
			}
		}
	}
	
	
	private void mutation(int gene, int n_gene) {
		for(int i = 0; i < new_gen[gene].length; i++) {
			if(rnd.nextFloat() <= 0.9) {
				new_gen[n_gene][i] = new_gen[gene][i];
			} else {
				new_gen[n_gene][i] = rnd.nextFloat()*8 - 4;
			}
		}
	}
	
	private void generateRandom(int gene) {
		int x = 48*30 + 30 + 30*18 + 18;
		
		for(int i = 0; i < x; i++) {
			new_gen[gene][i] = rnd.nextFloat()*8 - 4;
			//System.out.println(new_gen[gene][i]);
		}
	}
	
	public float[][] getNew_Gen(){
		return new_gen;
	}
	
	public String[] getNew_Gen_String(){
		String[] result = new String[new_gen.length];
		for(int i = 0; i < new_gen.length; i++) {
			result[i] = "" + new_gen[i][0];
			for(int j = 1; j < new_gen[i].length; j++) {
				result[i] += "," + new_gen[i][j];
			}
		}
		return result;
	}
}
