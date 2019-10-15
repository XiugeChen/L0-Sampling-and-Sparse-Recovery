import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.distribution.UniformIntegerDistribution;
import org.apache.commons.math3.distribution.ZipfDistribution;

/**
 * Generate data
 * xiugec 961392
 * Xiuge Chen
 * xiugec@student.unimelb.edu.au
 * @author xiugechen
 */
public class DataGenerator {
	private static String DIC_PATH = "resources/data/";
	private static int STREAM_LEN = (int) Math.pow(10, 7);
	
	public static void main(String[] args) {
		System.out.println("Initialization started");
		// initialization
		DataGenerator dataGenerator = new DataGenerator();
		double[] zipfS = {0.4, 0.8, 1.2, 1.6};
		
		ArrayList<Double> normalStd = new ArrayList<>();
		for (int i = 2; i < 9; i += 2) {
			normalStd.add(Math.pow(10, i));
		}
		
		ArrayList<Integer> uniqueNumItems = new ArrayList<>();
		for (int i = 2; i < 9; i += 2) {
			uniqueNumItems.add((int) Math.pow(10, i));
		}
		
		System.out.println("Data generating started");
		
		// generate zipf
		for (int i = 0; i < zipfS.length; i++) {
			dataGenerator.generateZipfData(STREAM_LEN, zipfS[i]);
		}
		System.out.println("Data generating finished: zipf");
		
		/*/ generate normal
		for (Double std: normalStd) {
			dataGenerator.generateNormalData(STREAM_LEN, std);
		}
		System.out.println("Data generating finished: normal"); */
			
		// generate unique
		for (Integer numItems: uniqueNumItems) {
			dataGenerator.generateUniformData(STREAM_LEN, numItems);
		}
		System.out.println("Data generating finished: unique");
		
		System.out.println("Data generating all finished");
	}
	
	private void generateUniformData(Integer length, Integer numDistinctItems) {
		UniformIntegerDistribution uniformDistribution = new UniformIntegerDistribution(0, numDistinctItems);
		
		try {
			File file = new File(DIC_PATH + "uniform_numItems" + numDistinctItems + "_len" + length + ".txt");
			file.createNewFile();
			FileWriter fw = new FileWriter(file);

			for (int i = 0; i < length; i++) {
				int sample = uniformDistribution.sample();
				
				String output = String.format("%d,%d\n", sample, 0);
				// System.out.println(output);
				fw.write(output);
			}
			
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		} 
	}
	
	private void generateZipfData(Integer length, Double s) {
		ZipfDistribution zipfDistribution = new ZipfDistribution(Integer.MAX_VALUE, s);
		
		try {
			File file = new File(DIC_PATH + "zipf_s" + s + "_len" + length + ".txt");
			file.createNewFile();
			FileWriter fw = new FileWriter(file);

			for (int i = 0; i < length; i++) {
				int sample = zipfDistribution.sample();
				
				String output = String.format("%d,%d\n", sample, 0);
				// System.out.println(output);
				fw.write(output);
			}
			
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}   
	}

	private void generateNormalData(Integer length, Double std) {
		int mean = (int) Integer.MAX_VALUE / 2;
		NormalDistribution normalDistribution = new NormalDistribution(mean, std);
		
		try {
			File file = new File(DIC_PATH + "normal_std" + std + "_len" + length + ".txt");
			file.createNewFile();
			FileWriter fw = new FileWriter(file);

			for (int i = 0; i < length; i++) {
				long sample = 0;
				
				do {
					sample = (long) normalDistribution.sample();
				} while (sample <= 0 || sample > Integer.MAX_VALUE);
				
				String output = String.format("%d,%d\n", sample, 0);
				// System.out.println(output);
				fw.write(output);
			}
			
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
}
