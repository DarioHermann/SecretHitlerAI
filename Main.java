import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Main {
	public static void main(String[] args) {
		
		Random rnd = new Random(System.currentTimeMillis());
		
		Game secretHitler = new GameRun();
		ArrayList<String> roles = secretHitler.getRoles();
		ArrayList<Integer> fascists = new ArrayList<Integer>();
		ArrayList<Player> players = new ArrayList<Player>();
		
		
		
		Game secretHitler_four = new GameRun();
		ArrayList<Player> players_four = new ArrayList<Player>();
		ArrayList<Integer> fascists_two = new ArrayList<Integer>();
		ArrayList<Integer> fascists_three = new ArrayList<Integer>();
		ArrayList<Integer> fascists_four = new ArrayList<Integer>();
		int victory;
		float[] fitness = new float[20];
		int[] positions = new int[20];
		
		for(int i =0; i < 2; i++) {
			fascists.add(-1);
			fascists_two.add(-1);
			fascists_three.add(-1);
			fascists_four.add(-1);
		}
		
		ArrayList<NN> neuralNetworks = new ArrayList<NN>();
		
		for(int i = 0; i < 20; i++) {
			neuralNetworks.add(new NN(48, 30));
		}
		
		String[] values = new String[20];
		int weightNumb;
		String filePath = "weightCounter.txt";
			
		String content = null;
		try{
			content = new String ( Files.readAllBytes( Paths.get(filePath) ) );
		}catch (IOException e){
			e.printStackTrace();
		}
		
		weightNumb = Integer.parseInt(content);
		
		filePath = "weights" + weightNumb + ".txt";
			
		content = null;
		try{
			content = new String ( Files.readAllBytes( Paths.get(filePath) ) );
		}catch (IOException e){
			e.printStackTrace();
		}
		
		values = content.split("\\r?\\n");
		
		
		
		for(int i = 0; i < 20; i++) {
			neuralNetworks.get(i).initialWeights(values[i]);
		}
		
		
		filePath = "counter.txt";
			
		content = null;
		try{
			content = new String ( Files.readAllBytes( Paths.get(filePath) ) );
		}catch (IOException e){
			e.printStackTrace();
		}
		
		int counter = Integer.parseInt(content);
		
		int chosen;
		
		boolean[] isML = new boolean[5];
		
		for(int i=0; i < 5; i++) {
			isML[i] = false;
		}
		
		int[] typeOfPlayers = new int[5];
		
		Collections.shuffle(roles);
		System.out.println(roles);		
		for(int i = 0; i < 5; i++) {
			do {
				chosen = rnd.nextInt(8);
				if(chosen == 0) {
					HonestBot n_player = new HonestBot(roles.get(i), i+1);
					players_four.add(n_player);
				} else if(chosen == 1) {
					RandomBot n_player = new RandomBot(roles.get(i), i+1);
					players_four.add(n_player);
//				} else if(chosen == 2) {
//					Human n_player = new Human(roles.get(i), i+1);
//					players_four.add(n_player);
				} else if(chosen < 5) {
					InteligBot n_player = new InteligBot(roles.get(i), i+1);
					players_four.add(n_player);
				} else if(chosen < 8) {
					if(counter < 100) {
						MLBotTrainer n_player = new MLBotTrainer(roles.get(i), i+1, neuralNetworks.get(counter%20));
						players_four.add(n_player);
						isML[i] = true;
					}
				}
			} while(chosen >= 5 && counter == 100);
			typeOfPlayers[i] = chosen;
			if(chosen >= 5) {
				counter++;
			}
			
			if(roles.get(i).equals("Hitler")) {
				fascists_four.set(1, i+1);
			} else if(roles.get(i).equals("Fascist")) {
				fascists_four.set(0, i+1);
			}
		}
				
		secretHitler_four.makePlayersState(players_four);
		for(int i = 0; i < 5; i++) {
			players_four.get(i).receiveRole(fascists_four);
		}
		victory = secretHitler_four.start();
		
		if(victory == 1 ) { //LIBERALS WIN
			for(int i = 0; i < 5; i++) {
				if(roles.get(i).equals("Liberal")) {
					players_four.get(i).didIWin(true);
				} else {
					players_four.get(i).didIWin(false);
				}
			}
		} else {
			for(int i = 0; i < 5; i++) {
				if(roles.get(i).equals("Liberal")) {
					players_four.get(i).didIWin(false);
				} else {
					players_four.get(i).didIWin(true);
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
				fitness[c++] = players_four.get(i).getTotalCost();
			}
		}
		
		for(int i = 0; i < 5; i++) {
			System.out.print(typeOfPlayers[i] + "\t");
		}
//		
		
		
		
		
		
		
		
		
		
//		String n_filePath = "fitnesses.txt";
//		
//		String n_content = null;
//		try{
//			n_content = new String ( Files.readAllBytes( Paths.get(n_filePath) ) );
//		}catch (IOException e){
//			e.printStackTrace();
//		}
		
		
		
//		String[] _cont = n_content.split("\\r?\\n");
//
//		for(int i = 0; i < 15; i++) {
//			fitness[i] = Float.parseFloat(_cont[i]);
//		}
		
		
		
		
		
		
		
		
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
		if(counter >= 100) {
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
			}
			
			
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
		

			//System.out.println(newGeneration[0] + "\n" + newGeneration[1]);
			
			
			
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
		}
		/*
		NNTrainer n = new NNTrainer();
		int x = n.train();
		if(x < 0) {
			System.out.println("Training NOT SUCCESSFULL");
		} else {
			System.out.println("Training SUCCESSFULL");
		}*/
	}
	
	/*private static void printRoles(ArrayList<String> showRoles){
		if(showRoles.get(0).equals("Liberal")) {
			System.out.println("You are a Liberal");
		} else if(showRoles.get(0).equals("Hitler")) {
			System.out.println("You are Hitler and the other Fascist is Player "+ showRoles.get(1));
		} else {
			System.out.println("You are a Fascist and Player " + showRoles.get(2) + " is Hitler");
		}
	}*/
}
