/*************************************************************
 * GeneticAlgorithms.java
 * Secret Hitler
 *
 * MSc Computer Games Systems
 * Nottingham Trent University
 * Major Project
 * 
 * Dario Hermann N0773470
 * 2017/18
 *************************************************************/

import java.util.Random;

/**
 * This class works as the Genetic Algorithms methods for the project 
 *
 */
public class GeneticAlgorithms {
	private float[][] survivors;
	private float[][] new_gen;
	Random rnd;
	
	/**
	 * GeneticAlgorithms()
	 * Constructor for the class GeneticAlgorithms.java
	 * It creates a new Matrix for the new generation and copies the 4 fittest individuals from the previous generation to this new generation
	 * 
	 * @param surv The four survivors of the last generation
	 */
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
	
	
	/**
	 * generateChildren()
	 * Generates all other 16 individuals of the new generation
	 * 6 by crossover, 8 by mutation and 2 randomly generated
	 */
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
	
	
	/**
	 * crossover()
	 * Realizes the n-point crossover between two individual and thus generates two new individuals
	 * 
	 * @param first 		The first parent (position it occupies in the population)
	 * @param second		The second parent (position it occupies in the population)
	 * @param son			The first child (position it will occupy in the population)
	 * @param daughter		The secoond child (position it will occupy in the population)
	 */
	private void crossover(int first, int second, int son, int daughter) {
		boolean cross = false;
		
		for(int i = 0; i < survivors[first].length; i++) {
			if(rnd.nextFloat()>0.6) { //with a probability of 40% it interchanges between the genes of one parent and another
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
	
	
	/**
	 * mutation()
	 * Runs through every gene in the original genetic code and with a 10% chance, changes the gene with a randomly generated number
	 * 
	 * @param gene 		The original genetic code
	 * @param n_gene	The position the new genetic code will occupy in the population
	 */
	private void mutation(int gene, int n_gene) {
		for(int i = 0; i < new_gen[gene].length; i++) {
			if(rnd.nextFloat() <= 0.9) {
				new_gen[n_gene][i] = new_gen[gene][i];
			} else {
				new_gen[n_gene][i] = rnd.nextFloat()*8 - 4;
			}
		}
	}
	
	
	/**
	 * generateRandom()
	 * Creates a new genetic code completely random generated
	 * 
	 * @param gene	The position the new genetic code will occupy in the population
	 */
	private void generateRandom(int gene) {
		int x = 48*30 + 30 + 30*18 + 18;
		
		for(int i = 0; i < x; i++) {
			new_gen[gene][i] = rnd.nextFloat()*8 - 4;
		}
	}
	
	/**
	 * float[][] getNew_Gen()
	 * returns the new population
	 * 
	 * @return	new population
	 */
	public float[][] getNew_Gen(){
		return new_gen;
	}
	
	
	/**
	 * String[] getNew_Gen_String()
	 * The same as getNew_Gen but in String[] format instead of float[][]
	 * 
	 * @return new population
	 */
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
