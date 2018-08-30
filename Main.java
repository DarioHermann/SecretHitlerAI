import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;

public class Main {
	public static void main(String[] args) {
		
		Game secretHitler = new GameRun();
		Game secretHitler_two = new GameRun();
		Game secretHitler_three = new GameRun();
		Game secretHitler_four = new GameRun();
		ArrayList<String> roles = secretHitler.getRoles();
		ArrayList<Player> players = new ArrayList<Player>();
		ArrayList<Player> players_two = new ArrayList<Player>();
		ArrayList<Player> players_three = new ArrayList<Player>();
		ArrayList<Player> players_four = new ArrayList<Player>();
		ArrayList<Integer> fascists = new ArrayList<Integer>();
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
		String filePath = "weights6.txt";
			
		String content = null;
		try{
			content = new String ( Files.readAllBytes( Paths.get(filePath) ) );
		}catch (IOException e){
			e.printStackTrace();
		}
		
		values = content.split("\\r?\\n");
		
		
		for(int i = 0; i < 20; i++) {
			neuralNetworks.get(i).initialWeights(values[i]);
		}
		
		
		
//		try(FileWriter fw = new FileWriter("weights4.txt", true);
//	    		BufferedWriter bw = new BufferedWriter(fw);
//	    		PrintWriter out = new PrintWriter(bw)) {
//			for(int i = 0; i < values.length; i++) {
//				out.println(values[i]);
//			}
//		} catch (IOException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
		
		
		//---------------------------------------------------1
		
//		System.out.println(roles);
//		for(int i = 0; i < 5; i++) {
//			//RandomBot n_player = new RandomBot(roles.get(i), i);
//			MLBotTrainer n_player = new MLBotTrainer(roles.get(i), i+1, neuralNetworks.get(i));
//			players.add(n_player);
//			if(roles.get(i).equals("Hitler")) {
//				fascists.set(1, i+1);
//			} else if(roles.get(i).equals("Fascist")) {
//				fascists.set(0, i+1);
//			}
//		}
//		
//		secretHitler.makePlayersState(players);
//		for(int i = 0; i < 5; i++) {
//			players.get(i).receiveRole(fascists);
//		}
//		victory = secretHitler.start();
//		
//		if(victory == 1 ) { //LIBERALS WIN
//			for(int i = 0; i < 5; i++) {
//				if(roles.get(i).equals("Liberal")) {
//					players.get(i).didIWin(true);
//				} else {
//					players.get(i).didIWin(false);
//				}
//			}
//		} else {
//			for(int i = 0; i < 5; i++) {
//				if(roles.get(i).equals("Liberal")) {
//					players.get(i).didIWin(false);
//				} else {
//					players.get(i).didIWin(true);
//				}
//			}
//		}
		
		//--------------------------------------------------------2
		
//		Collections.shuffle(roles);
//		System.out.println(roles);
//		for(int i = 0; i < 5; i++) {
//			//RandomBot n_player = new RandomBot(roles.get(i), i);
//			MLBotTrainer n_player = new MLBotTrainer(roles.get(i), i+1, neuralNetworks.get(i+5));
//			players_two.add(n_player);
//			if(roles.get(i).equals("Hitler")) {
//				fascists_two.set(1, i+1);
//			} else if(roles.get(i).equals("Fascist")) {
//				fascists_two.set(0, i+1);
//			}
//		}
//		
//		secretHitler_two.makePlayersState(players_two);
//		for(int i = 0; i < 5; i++) {
//			players_two.get(i).receiveRole(fascists_two);
//		}
//		victory = secretHitler_two.start();
//		
//		if(victory == 1 ) { //LIBERALS WIN
//			for(int i = 0; i < 5; i++) {
//				if(roles.get(i).equals("Liberal")) {
//					players_two.get(i).didIWin(true);
//				} else {
//					players_two.get(i).didIWin(false);
//				}
//			}
//		} else {
//			for(int i = 0; i < 5; i++) {
//				if(roles.get(i).equals("Liberal")) {
//					players_two.get(i).didIWin(false);
//				} else {
//					players_two.get(i).didIWin(true);
//				}
//			}
//		}
		
		//--------------------------------------------------------3
		
//		Collections.shuffle(roles);
//		System.out.println(roles);		
//		for(int i = 0; i < 5; i++) {
//			//RandomBot n_player = new RandomBot(roles.get(i), i);
//			MLBotTrainer n_player = new MLBotTrainer(roles.get(i), i+1, neuralNetworks.get(i+10));
//			players_three.add(n_player);
//			if(roles.get(i).equals("Hitler")) {
//				fascists_three.set(1, i+1);
//			} else if(roles.get(i).equals("Fascist")) {
//				fascists_three.set(0, i+1);
//			}
//		}
//				
//		secretHitler_three.makePlayersState(players_three);
//		for(int i = 0; i < 5; i++) {
//			players_three.get(i).receiveRole(fascists_three);
//		}
//		victory = secretHitler_three.start();
//		
//		if(victory == 1 ) { //LIBERALS WIN
//			for(int i = 0; i < 5; i++) {
//				if(roles.get(i).equals("Liberal")) {
//					players_three.get(i).didIWin(true);
//				} else {
//					players_three.get(i).didIWin(false);
//				}
//			}
//		} else {
//			for(int i = 0; i < 5; i++) {
//				if(roles.get(i).equals("Liberal")) {
//					players_three.get(i).didIWin(false);
//				} else {
//					players_three.get(i).didIWin(true);
//				}
//			}
//		}
				
				
		//--------------------------------------------------------4
				
		Collections.shuffle(roles);
		System.out.println(roles);		
		for(int i = 0; i < 5; i++) {
			//RandomBot n_player = new RandomBot(roles.get(i), i);
			MLBotTrainer n_player = new MLBotTrainer(roles.get(i), i+1, neuralNetworks.get(i+15));
			players_four.add(n_player);
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
		
		//----------------------------------------------------------
		
		
		
		for(int i = 0; i < 5; i++) {
			positions[i] = i;
//			fitness[i] = players.get(i).getTotalCost();
			positions[i+5] = i+5;
//			fitness[i+5] = players_two.get(i).getTotalCost();
			positions[i+10] = i+10;
//			fitness[i+10] = players_three.get(i).getTotalCost();
			positions[i+15] = i+15;
			fitness[i+15] = players_four.get(i).getTotalCost();
		}
		
		
		//----------------------------------------------------------1
		
//		try(FileWriter fw = new FileWriter("fitnesses1.txt", false);
//				BufferedWriter bw = new BufferedWriter(fw);
//				PrintWriter out = new PrintWriter(bw)) {
//			for(int i = 0; i < 5; i++) {
//				out.println(fitness[i]);
//			}
//		} catch (IOException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
		
		//-----------------------------------------------2
		
//		String n_filePath = "fitnesses1.txt";
//		
//		String n_content = null;
//		try{
//			n_content = new String ( Files.readAllBytes( Paths.get(n_filePath) ) );
//		}catch (IOException e){
//			e.printStackTrace();
//		}
//		
//		try(FileWriter fw = new FileWriter("fitnesses2.txt", false);
//				BufferedWriter bw = new BufferedWriter(fw);
//				PrintWriter out = new PrintWriter(bw)) {
//			out.println(n_content);
//			for(int i = 5; i < 10; i++) {
//				out.println(fitness[i]);
//			}
//		} catch (IOException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
		
		//-----------------------------------------------3
		
//		String n_filePath = "fitnesses2.txt";
//				
//		String n_content = null;
//		try{
//			n_content = new String ( Files.readAllBytes( Paths.get(n_filePath) ) );
//		}catch (IOException e){
//			e.printStackTrace();
//		}
//		
//		try(FileWriter fw = new FileWriter("fitnesses3.txt", false);
//				BufferedWriter bw = new BufferedWriter(fw);
//				PrintWriter out = new PrintWriter(bw)) {
//			out.println(n_content);
//			for(int i = 10; i < 15; i++) {
//				out.println(fitness[i]);
//			}
//		} catch (IOException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
		
		//-----------------------------------------------4
		
		String n_filePath = "fitnesses3.txt";
		
		String n_content = null;
		try{
			n_content = new String ( Files.readAllBytes( Paths.get(n_filePath) ) );
		}catch (IOException e){
			e.printStackTrace();
		}
		
		
		
		String[] _cont = n_content.split("\\r?\\n");

		for(int i = 0; i < 15; i++) {
			fitness[i] = Float.parseFloat(_cont[i]);
		}
		
		
		try(FileWriter fw = new FileWriter("fitness_Final.txt", false);
				BufferedWriter bw = new BufferedWriter(fw);
				PrintWriter out = new PrintWriter(bw)) {
			for(int i = 0; i < 20; i++) {
				out.println(i+1 + "\t\t" + fitness[i]);
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		try(FileWriter fw = new FileWriter("fitnesses4.txt", false);
				BufferedWriter bw = new BufferedWriter(fw);
				PrintWriter out = new PrintWriter(bw)) {
			for(int i = 0; i < 20; i++) {
				out.println(fitness[i]);
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		n_filePath = "fitnesses4.txt";
		
		n_content = null;
		try{
			n_content = new String ( Files.readAllBytes( Paths.get(n_filePath) ) );
		}catch (IOException e){
			e.printStackTrace();
		}
		
		_cont = n_content.split("\\r?\\n");
		
				for(int i = 0; i < 20; i++) {
					fitness[i] = Float.parseFloat(_cont[i]);
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
		
		try(FileWriter fw = new FileWriter("fitness_Final2.txt", false);
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
		
		try(FileWriter fw = new FileWriter("weights7.txt", false);
				BufferedWriter bw = new BufferedWriter(fw);
				PrintWriter out = new PrintWriter(bw)) {
			for(int i = 0; i < newGeneration.length; i++) {
				out.println(newGeneration[i]);
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
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
