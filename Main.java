/*************************************************************
 * Main.java
 * Secret Hitler
 *
 * MSc Computer Games Systems
 * Nottingham Trent University
 * Major Project
 * 
 * Dario Hermann N0773470
 * 2017/18
 *************************************************************/

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 * The starting class of the programm 
 *
 */

public class Main {
	public static void main(String[] args) {
		
		Random rnd = new Random(System.currentTimeMillis());
		
		Game secretHitler = new GameRun();
		ArrayList<String> roles = secretHitler.getRoles();
		ArrayList<Integer> fascists = new ArrayList<Integer>();
		ArrayList<Player> players = new ArrayList<Player>();
		
		int victory;
		float[] fitness = new float[20];
		int[] positions = new int[20];
		
		for(int i =0; i < 2; i++) {
			fascists.add(-1);
		}
		
		ArrayList<NN> neuralNetworks = new ArrayList<NN>(); 
		
		for(int i = 0; i < 20; i++) {
			neuralNetworks.add(new NN(48, 30)); //creates 20 new NN with the intention of later reading all the 20 individuals saved on a txt file
		}
		
		

		String[] values = new String[20];
		int weightNumb;
		String filePath = "weightCounter.txt";
			
		String content = null;
		try{
			content = new String ( Files.readAllBytes( Paths.get(filePath) ) ); //This the number of the file of the most recent population
		}catch (IOException e){
			e.printStackTrace();
		}
		
		weightNumb = Integer.parseInt(content);
				
		filePath = "weights" + weightNumb + ".txt";
			
		content = null;
		try{
			content = new String ( Files.readAllBytes( Paths.get(filePath) ) ); // this loads up the most recent population of weights
		}catch (IOException e){
			e.printStackTrace();
		}
		
		
		values = content.split("\\r?\\n");
		
		
		
		for(int i = 0; i < 20; i++) {
			neuralNetworks.get(i).initialWeights(values[i]); //adds the initial weights to the NN
		}
		
		
		filePath = "counter.txt";
			
		content = null;
		try{
			content = new String ( Files.readAllBytes( Paths.get(filePath) ) ); //a counter to see what individual the next ML Bot needs to use
		}catch (IOException e){
			e.printStackTrace();
		}
		
		int counter = Integer.parseInt(content);
		
		int chosen;
		
		boolean[] isML = new boolean[5]; //isML is used to know how many players were ML Bots
		
		for(int i=0; i < 5; i++) {
			isML[i] = false;
		}
		
		int[] typeOfPlayers = new int[5];
		
		Collections.shuffle(roles);
		System.out.println(roles);		
		for(int i = 0; i < 5; i++) { //choosing what type of players will play
			do {
				chosen = rnd.nextInt(7); //The types of player commented out are only used if the user wants
//				if(chosen == 0) {
//					HonestBot n_player = new HonestBot(roles.get(i), i+1);
//					players.add(n_player);
//				} else if(chosen == 1) {
//					RandomBot n_player = new RandomBot(roles.get(i), i+1);
//					players.add(n_player);
				/*} else*/ if(chosen == 2) {
					Human n_player = new Human(roles.get(i), i+1);
					players.add(n_player);
				} else if(chosen < 5) {
					InteligBot n_player = new InteligBot(roles.get(i), i+1);
					players.add(n_player);
				} else if(chosen < 7) {
					if(counter < 300) {
						MLBot n_player = new MLBot(roles.get(i), i+1, neuralNetworks.get(counter));
						players.add(n_player);
						isML[i] = true;
					}
				}
			} while(chosen >= 5 && counter == 300);
			typeOfPlayers[i] = chosen;
			if(chosen >= 5) {
				counter++;
			}
			
			if(roles.get(i).equals("Hitler")) {
				fascists.set(1, i+1);
			} else if(roles.get(i).equals("Fascist")) {
				fascists.set(0, i+1);
			}
		}
				
		secretHitler.makePlayersState(players);
		for(int i = 0; i < 5; i++) {
			players.get(i).receiveRole(fascists);
		}
		victory = secretHitler.start(); //start the game
		
		if(victory == 1 ) { //LIBERALS WIN
			for(int i = 0; i < 5; i++) {
				if(roles.get(i).equals("Liberal")) {
					players.get(i).didIWin(true);
				} else {
					players.get(i).didIWin(false);
				}
			}
		} else {
			for(int i = 0; i < 5; i++) {
				if(roles.get(i).equals("Liberal")) {
					players.get(i).didIWin(false);
				} else {
					players.get(i).didIWin(true);
				}
			}
		}
		
		int c = 0;
		
		for(int i = 0; i < 5; i++) {
			positions[i] = i;
			positions[i+5] = i+5;
			positions[i+10] = i+10;
			positions[i+15] = i+15;
			if(isML[i]) {
				fitness[c++] = players.get(i).getTotalCost();
			}
		}
		
		for(int i = 0; i < 5; i++) {
			System.out.print(typeOfPlayers[i] + "\t"); //prints out what type of players were playing
		}	
		
		
		
		/* FROM THIS POINT UNTIL THE END IT WAS ONLY USED WHILE TRAINING THE MLBOT, OUTSIDE OF TRAINING MODE THIS IS NOT NECESSARY
		
		try(FileWriter fw = new FileWriter("fitnesses.txt", true);
				BufferedWriter bw = new BufferedWriter(fw);
				PrintWriter out = new PrintWriter(bw)) {
			for(int i = 0; i < c; i++) {
				out.println(fitness[i]);
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		try(FileWriter fw = new FileWriter("counter.txt", false);
				BufferedWriter bw = new BufferedWriter(fw);
				PrintWriter out = new PrintWriter(bw)) {
			out.print(counter);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		
		
		
		
		
		//----------------------------------------------------
		if(counter >= 300) {
			String n_filePath = "fitnesses.txt";
		
			String n_content = null;
			try{
				n_content = new String ( Files.readAllBytes( Paths.get(n_filePath) ) );
			}catch (IOException e){
				e.printStackTrace();
			}
		
			String[] _cont = n_content.split("\\r?\\n");
		
			for(int i = 0; i < 20; i++) {
				fitness[i] = Float.parseFloat(_cont[i]);
				fitness[i] += Float.parseFloat(_cont[i+20]);
				fitness[i] += Float.parseFloat(_cont[i+40]);
				fitness[i] += Float.parseFloat(_cont[i+60]);
				fitness[i] += Float.parseFloat(_cont[i+80]);

				fitness[i] += Float.parseFloat(_cont[i+100]);
				fitness[i] += Float.parseFloat(_cont[i+120]);
				fitness[i] += Float.parseFloat(_cont[i+140]);
				fitness[i] += Float.parseFloat(_cont[i+160]);
				fitness[i] += Float.parseFloat(_cont[i+180]);

				fitness[i] += Float.parseFloat(_cont[i+200]);
				fitness[i] += Float.parseFloat(_cont[i+220]);
				fitness[i] += Float.parseFloat(_cont[i+240]);
				fitness[i] += Float.parseFloat(_cont[i+260]);
				fitness[i] += Float.parseFloat(_cont[i+280]);
			}

			n_filePath = "final_counter.txt";
			
			
			n_content = null;
			try{
				n_content = new String ( Files.readAllBytes( Paths.get(n_filePath) ) );
			}catch (IOException e){
				e.printStackTrace();
			}
			


			int page = Integer.parseInt(n_content);
			
			filePath = "fitness_Final_" + page + ".txt";
			
			try(FileWriter fw = new FileWriter(filePath, true);
					BufferedWriter bw = new BufferedWriter(fw);
					PrintWriter out = new PrintWriter(bw)) {
				for(int i = 0; i < 20; i++) {
					out.println(i+1 + "\t\t" + fitness[i]);
				}
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
				
		
			for(int i = 0; i < 20; i++)
			{
				positions[i] = i;
			}
			for(int i = 0; i < 19; i++) {
				for(int j = 0; j < 19 - i; j++) {
					if(fitness[j] > fitness[j+1]) {
						float x = fitness[j];
						fitness[j] = fitness[j+1];
						fitness[j+1] = x;
					
						int y = positions[j];
						positions[j] = positions[j+1];
						positions[j+1] = y;
					}
				}
			}
			
			filePath = "fitness_Final2_" + page + ".txt";
		
			try(FileWriter fw = new FileWriter(filePath, false);
					BufferedWriter bw = new BufferedWriter(fw);
					PrintWriter out = new PrintWriter(bw)) {
				for(int i = 0; i < 20; i++) {
					out.println(positions[i]+1 + "\t\t" + fitness[i]);
				}
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		
		
			float[][] survivors = new float[4][];
			for(int i = 0; i < 4; i++) {
				survivors[i] = neuralNetworks.get(positions[i]).getBrain();
			}
		
			GeneticAlgorithms ga = new GeneticAlgorithms(survivors);
		
			ga.generateChildren();
			String[] newGeneration = ga.getNew_Gen_String();
			
			
			
			filePath = "weights" + (weightNumb+1) + ".txt";
			
			try(FileWriter fw = new FileWriter(filePath, false);
					BufferedWriter bw = new BufferedWriter(fw);
					PrintWriter out = new PrintWriter(bw)) {
				for(int i = 0; i < newGeneration.length; i++) {
					out.println(newGeneration[i]);
				}
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			filePath = "weightCounter.txt";
			
			try(FileWriter fw = new FileWriter(filePath, false);
					BufferedWriter bw = new BufferedWriter(fw);
					PrintWriter out = new PrintWriter(bw)) {
				out.print(weightNumb+1);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			++page;
			filePath = "final_counter.txt";
			
			try(FileWriter fw = new FileWriter(filePath, false);
					BufferedWriter bw = new BufferedWriter(fw);
					PrintWriter out = new PrintWriter(bw)) {
				out.print(page);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			filePath = "counter.txt";
			
			try(FileWriter fw = new FileWriter(filePath, false);
					BufferedWriter bw = new BufferedWriter(fw);
					PrintWriter out = new PrintWriter(bw)) {
				out.print(0);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			filePath = "fitnesses.txt";
			
			try(FileWriter fw = new FileWriter(filePath, false);
					BufferedWriter bw = new BufferedWriter(fw);
					PrintWriter out = new PrintWriter(bw)) {
				out.print("");
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}*/
	}
}
