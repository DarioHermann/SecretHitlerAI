
public class NN {

	private float[] hiddenLayer;
	private float[][] hiddenLayerW;
	private float[] hiddenLayerB;
	private int inputs;
	private float[][] outputW;
	private float[] outputB;
	private float fitness;
	
	public NN(int numberOfInputs, int hiddenLayerNodes) {
		hiddenLayer = new float[hiddenLayerNodes];
		initialWeights(numberOfInputs);
	}
	
	public void setFitness(float fitness) {
		this.fitness = fitness;
	}
	
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
	
	public void initialWeights(String s) {
		float[] w = new float[weightCount()];
		
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
			for(int j = 0; j < outputW[i].length; i++) {
				outputW[i][j] = w[(hiddenLayer.length * (inputs+1)) + (i * outputW[i].length) + i + j];
			}
			outputB[i] = w[(hiddenLayer.length * (inputs+1)) + (outputW[i].length * (i+1)) + i];
		}
		
		
	}
	
	
	public float[] calculateNN(float[] inps) {
		for(int i = 0; i < hiddenLayer.length; i++) {
			hiddenLayer[i] = reLU(sum(inps, hiddenLayerW[i]) + hiddenLayerB[i]);
		}
		
		float[] output = new float[18];
		for(int i = 0; i < 18; i++) {
			output[i] = softSign(sum(outputW[i], hiddenLayer) + outputB[i]);
		}
		
		return output;
	}
	
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
	
	public String readBrain() {
		String dna = "";
		for(int i = 0; i < hiddenLayerW.length; i++) {
			for(int j = 0; j < hiddenLayerW[i].length; j++) {
				dna += hiddenLayerW[i][j] + ",";
			}
			dna += hiddenLayerB[i] + ",";
		}
		
		for(int i = 0; i < outputW.length; i++) {
			dna += outputW[i] + ",";
		}
		
		dna += outputB;
		
		return dna;
	}
	
	public float[] getBrain() {
		String s = readBrain();
		String[] dna = s.split(",");
		float[] float_dna = new float[dna.length];
		for(int i = 0; i < dna.length; i++) {
			float_dna[i] = Float.parseFloat(dna[i]);
		}
		
		return float_dna;
	}
	
	public float[] getHiddenLayer() {
		return hiddenLayer;
	}
	
	public int returnInputs() {
		return inputs;
	}
	
	public float getFitness() {
		return fitness;
	}
	
	private int weightCount() {
		return (hiddenLayerW[0].length * inputs) + outputW.length;
	}
}
