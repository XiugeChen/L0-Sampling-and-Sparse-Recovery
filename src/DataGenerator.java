import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

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
	private static int STREAM_LEN = (int) (1 * Math.pow(10, 3));
	
	public static void main(String[] args) {
		System.out.println("Initialization started");
		// initialization
		DataGenerator dataGenerator = new DataGenerator();
		double[] zipfS = {0.9};
		
		ArrayList<Integer> uniqueNumItems = new ArrayList<>();
		for (int i = 2; i < 3; i += 2) {
			uniqueNumItems.add((int) Math.pow(10, i));
		}
		
		System.out.println("Data generating started");
			
		// generate unique
		for (Integer numItems: uniqueNumItems) {
			for (int i = 0; i < zipfS.length; i++) {
				dataGenerator.generateZipfData(STREAM_LEN, zipfS[i], numItems);
			}
			
			dataGenerator.generateUniformData(STREAM_LEN, numItems);
		}
		
		System.out.println("Data generating all finished");
	}
	
	private void generateUniformData(Integer length, Integer numDistinctItems) {
		UniformIntegerDistribution uniformDistribution = new UniformIntegerDistribution(1, numDistinctItems);
		
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
	
	private void generateZipfData(Integer length, Double s, int domain) {
		ZipfDistribution zipfDistribution = new ZipfDistribution(domain, s);
		
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
}
