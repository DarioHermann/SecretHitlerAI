/*************************************************************
 * NN.java
 * Secret Hitler
 *
 * MSc Computer Games Systems
 * Nottingham Trent University
 * Major Project
 * 
 * Dario Hermann N0773470
 * 2017/18
 *************************************************************/

/**
 * This class is represented by the Neural Network  
 *
 */
public class NN {

	private float[] hiddenLayer;
	private float[][] hiddenLayerW;
	private float[] hiddenLayerB;
	private int inputs;
	private float[][] outputW;
	private float[] outputB;
	
	/**
	 * NN.java constructor
	 * 
	 * @param numberOfInputs		How many inout nodes the NN will have
	 * @param hiddenLayerNodes		How many hidden layer nodes the NN will have
	 */
	public NN(int numberOfInputs, int hiddenLayerNodes) {
		hiddenLayer = new float[hiddenLayerNodes];
		initialWeights(numberOfInputs);
	}
	
	/**
	 * initialWeights()
	 * Creating the matrixes and arrays for the NN
	 * 
	 * @param inps how many input nodes there will be
	 */
	private void initialWeights(int inps) {
		inputs = inps;
		outputW = new float[18][];
		outputB = new float[18];
		hiddenLayerW = new float[hiddenLayer.length][];
		hiddenLayerB = new float[hiddenLayer.length];
		
		for(int i = 0; i < outputW.length; i++) {
			outputW[i] = new float[hiddenLayer.length];
			outputB[i] = 0;
			for(int j = 0; j < outputW[i].length; j++) {
				outputW[i][j] = 0;
			}
		}
		
		for(int i = 0; i < hiddenLayerW.length; i++) {
			hiddenLayerW[i] = new float[inps];
			hiddenLayerB[i] = 0;
			
			for(int j = 0; j < hiddenLayerW[i].length; j++) {
				hiddenLayerW[i][j] = 0;
			}
		}
	}
	
	
	/**
	 * initialWeights()
	 * assigning weight values to the connections and biases
	 * 
	 * @param w		an array with the weight values
	 */
	public void initialWeights(float[] w) {
		for(int i = 0; i < hiddenLayerW.length; i++) {
			for(int j = 0; j < hiddenLayerW[i].length; j++) {
				hiddenLayerW[i][j] = w[j+(i*(inputs+1))];
			}
			hiddenLayerB[i] = w[inputs + (i*(inputs+1))];
		}
		
		
		for(int i = 0; i < outputW.length; i++) {
			for(int j = 0; j < outputW[i].length; j++) {
				outputW[i][j] = w[(hiddenLayer.length * (inputs+1)) + (i * outputW[i].length) + i + j];
			}
			outputB[i] = w[(hiddenLayer.length * (inputs+1)) + (outputW[i].length * (i+1)) + i];
		}
	}
	
	
	/**
	 * initialWeights()
	 * assigning weight values to the connections and biases
	 * 
	 * @param s		a string with the weight values
	 */
	public void initialWeights(String s) {
		float[] w = new float[2028];
		
		String[] new_s = s.split(",");
		for(int i = 0; i < w.length; i++) {
			w[i] = Float.parseFloat(new_s[i]);
		}
		
		for(int i = 0; i < hiddenLayerW.length; i++) {
			for(int j = 0; j < hiddenLayerW[i].length; j++) {
				hiddenLayerW[i][j] = w[j + (i*(inputs+1))];
			}
			hiddenLayerB[i] = w[inputs + (i*(inputs+1))];
		}
		
		for(int i = 0; i < outputW.length; i++) {
			for(int j = 0; j < outputW[i].length; j++) {
				outputW[i][j] = w[(hiddenLayer.length * (inputs+1)) + (i * outputW[i].length) + i + j];
			}
			outputB[i] = w[(hiddenLayer.length * (inputs+1)) + (outputW[i].length * (i+1)) + i];
		}
		
		
	}
	
	
	/**
	 * float[] calculateNN
	 * calculates the values of the output nodes
	 * 
	 * @param inps	input nodes
	 * @return	output nodes
	 */
	public float[] calculateNN(float[] inps) {
		for(int i = 0; i < hiddenLayer.length; i++) {
			hiddenLayer[i] = reLU(sum(inps, hiddenLayerW[i]) + hiddenLayerB[i]);
		}
		
		float[] output = new float[18];
		for(int i = 0; i < 18; i++) {
			output[i] = softSign(reLU(sum(outputW[i], hiddenLayer) + outputB[i]));
		}
		
		return output;
	}
	
	
	/**
	 * float sum()
	 * 
	 * 
	 * @param x nodes or connection weights
	 * @param y	nodes or connection weights
	 * @return	adds up every multiplication between a node and the respective connection weight
	 */
	private float sum(float[] x, float[] y) {
		float result = 0;
		for(int i = 0; i < x.length; i++) {
			result += x[i] * y[i];
		}
		return result;
	}
	
	private float reLU(float inp) {
		return Math.max(0,  inp);
	}
	
	private float softSign(float inp) {
		return inp / (1 + Math.abs(inp));
	}
	
	
	/**
	 * String readBrain()
	 *  
	 * @return 	the connection and biases weights of the NN
	 */
	public String readBrain() {
		String dna = "";
		for(int i = 0; i < hiddenLayerW.length; i++) {
			for(int j = 0; j < hiddenLayerW[i].length; j++) {
				dna += hiddenLayerW[i][j] + ",";
			}
			dna += hiddenLayerB[i] + ",";
		}
		
		for(int i = 0; i < outputW.length; i++) {
			for(int j = 0; j < outputW[i].length; j++) {
				dna += outputW[i][j] + ",";
			}
			if(i == outputW.length - 1) {
				dna += outputB[i];
			} else {
				dna += outputB[i] + ",";
			}
		}
		
		return dna;
	}
	
	/*
	 * float[] getBrain
	 * the same as readBrain but as array of floats instead of a String
	 * 
	 * 	@return 	the connection and biases weights of the NN
	 */
	public float[] getBrain() {
		String s = readBrain();
		String[] dna = s.split(",");
		float[] float_dna = new float[dna.length];
		for(int i = 0; i < dna.length; i++) {
			//System.out.println(dna[i]);
			float_dna[i] = Float.parseFloat(dna[i]);
		}
		
		System.out.println(float_dna[0] + "\n" + float_dna[1]);
		
		return float_dna;
	}
	
	
	/*
	 * float[] getHiddenLayer()
	 * 
	 * @return hidden Layer nodes
	 */
	public float[] getHiddenLayer() {
		return hiddenLayer;
	}
	
	/*
	 * int returnInputs()
	 * 
	 * @return input Layer nodes
	 */
	public int returnInputs() {
		return inputs;
	}
	
	
	/**
	 * float[] calculateCost
	 * Calculates the error cost
	 * 
	 * @param output 			output nodes
	 * @param correctValues		what the output nodes should have been
	 * @return	the error cost
	 */
	public float[] calculateCost(float[] output, float[] correctValues) {
		float tCost = 0;
		float[] cost = new float[output.length+1];
		for(int i = 0; i < output.length; i++) {
			cost[i] = (float) (0.5 * ((correctValues[i] - output[i]) * (correctValues[i] - output[i])));
			tCost += cost[i];
		}
		cost[cost.length-1] = tCost;
		return cost;
		
	}
	
	
	/**
	 * correctNN()
	 * Used to make backpropaation but had to give up on it since most of the weight values turned 0
	 * 
	 * @param inp				input nodes
	 * @param out				output nodes
	 * @param correctValues		what the output nodes shoul have been
	 */
	public void correctNN(float[] inp, float[] out, float[] correctValues) {
		int min = 48*30+30;
		int max = min + 30*18+18;
		float[] brain = getBrain();
		float corr;
		float[][] newOutputW = new float[outputW.length][outputW[0].length];
		float[] newOutputB = new float[outputB.length];
		float[][] totrespectout = new float[hiddenLayer.length][outputW.length];
		for(int i = 0; i < outputW.length; i++) {
			for(int j = 0; j < outputW[i].length; j++) {
				totrespectout[j][i] = -1 * (correctValues[i] - out[i]) * out[i] * (1-out[i]);
				corr = totrespectout[j][i] * hiddenLayer[j];
				newOutputW[i][j] = (float) (outputW[i][j] - 0.5* corr);
			}
			corr = -1 * (correctValues[i] - out[i]) * out[i] * (1-out[i]);
			newOutputB[i] = (float) (outputB[i] - 0.5*corr);
		}
		
		float[] derValue = new float[hiddenLayer.length];
		for(int i = 0; i < hiddenLayer.length; i++) {
			derValue[i] = 0;
		}
		
		for(int i = 0; i < hiddenLayerW.length; i++) {
			for(int j = 0; j < outputW.length; j++) {
				derValue[i] += totrespectout[i][j] * outputW[j][i];
			}
			
			derValue[i] = derValue[i] * (hiddenLayer[i] * (1-hiddenLayer[i]));
		}
		
		float[][] newVals = new float[hiddenLayerW.length][hiddenLayerW[0].length];
		
		for(int i = 0; i < hiddenLayerW.length; i++) {
			for(int j = 0; j < hiddenLayerW[i].length; j++) {
				newVals[i][j] = derValue[i] * inp[j];
			}
		}
		
		
		for(int i = 0; i < hiddenLayerW.length; i++) {
			for(int j = 0; j < hiddenLayerW[i].length; j++) {
				hiddenLayerW[i][j] = newVals[i][j];
			}
		}
		
		for(int i = 0; i < outputW.length; i++) {
			for(int j = 0; j < outputW[i].length; j++) {
				outputW[i][j] = newOutputW[i][j];
			}
			outputB[i] = newOutputB[i];
		}
	}
}
