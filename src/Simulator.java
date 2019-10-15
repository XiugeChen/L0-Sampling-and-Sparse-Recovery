import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.LineNumberReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Simulator {
	private static final String FOLDER = "resources/data/dataWithUpdate/";
	private static final String FILE_PATH = "resources/data/dataWithUpdate/space_time_test.txt";
	private static final double ERROR = 0.01;
	private static final double BAD_PROB = 0.01;
	private static final int DOMAIN = Integer.MAX_VALUE;
	private static final int NUM_EXPERIMENT = 10000;

	public static void main(String[] args) {
		System.out.println("Simulation starts");
		
		// loop through all files
		try (Stream<Path> walk = Files.walk(Paths.get(FOLDER))) {
			List<String> result = walk.map(x -> x.toString())
					.filter(f -> f.endsWith(".txt"))
					.filter(f -> !f.contains("result"))
					.filter(f -> !f.contains("test"))
					.filter(f -> !f.contains("88888"))
					.collect(Collectors.toList());

			for (String file: result) {
				System.out.println("test file: " + file);
				// run all of three L0 samplers to measure the performance
				runAll(file, DOMAIN, NUM_EXPERIMENT);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println("Simulation ends");

		/*
		// run specific L0 sampler to measure the time
		double[] time = {0.0, 0.0};
		double[] total_time = {0.0, 0.0};
		
		IL0Sampler sampler = new L0Sampler_InsertionOnly(DOMAIN, ERROR);
		//IL0Sampler sampler = new L0Sampler_General(DOMAIN, BAD_PROB);
		//IL0Sampler sampler = new L0Sampler_Turnstile(DOMAIN, BAD_PROB);
		
		for (int i = 0; i < NUM_EXPERIMENT; i++) {
			time = runOne(sampler);
			total_time[0] += time[0];
			total_time[1] += time[1];
		}
		System.out.println("####INFO: Update time: " + (double) total_time[0] / NUM_EXPERIMENT);
		System.out.println("####INFO: Output time: " + (double) total_time[1] / NUM_EXPERIMENT);
		*/
		/*
		// run specific cms to measure the space
		double[] time = {0.0, 0.0};
		IL0Sampler sampler = new L0Sampler_InsertionOnly(DOMAIN, ERROR);
		//IL0Sampler sampler = new L0Sampler_General(DOMAIN, BAD_PROB);
		//IL0Sampler sampler = new L0Sampler_Turnstile(DOMAIN, BAD_PROB);
		time = runOne(sampler);

		while (true) {}
		*/
	}

	private static void runAll(String filePath, int domain, int numExperiment) {
		// create output file
		String output_file = filePath;
		output_file = output_file.replaceAll("dataWithUpdate", "dataWithUpdate/result");
		output_file = output_file.replaceAll(".txt", "_runningResult_domain"
				+ domain + "_delta" + BAD_PROB + ".txt");
		
		File file = new File(output_file);
		FileWriter fw = null;
		try {
			file.createNewFile();
			fw = new FileWriter(file);
		} catch (IOException e1) {
			e1.printStackTrace();
			System.exit(1);
		}
		
		
		for (int i = 0; i < numExperiment; i++) {
			System.out.println("####INFO: start running experiment: " + i);
		
			L0Sampler_InsertionOnly insertionOnly = new L0Sampler_InsertionOnly(domain, ERROR);
			L0Sampler_General general = new L0Sampler_General(domain, BAD_PROB);
			L0Sampler_Turnstile turnstile = new L0Sampler_Turnstile(domain, BAD_PROB);
			
			// updating data
			try (LineNumberReader fp = new LineNumberReader(new FileReader(new File(filePath)))) {
				String s;
				
				while ((s = fp.readLine()) != null) {
					// query sketch
					if (s.startsWith("#")) {
						if (s.contains("####"))
							continue;

						// query items
						Object result_insertionOnly = insertionOnly.output();
						Object result_general = general.output();
						Object result_turnstile = turnstile.output();

						String output = String.format("insertionOnly:%s,general:%s,turnstile:%s\n", result_insertionOnly.toString(), 
								result_general.toString(), result_turnstile.toString());
						fw.write(output);
					}
					// update sketch
					else {
						String[] data = s.split(",");
						String sample = data[0];
						int update = Integer.parseInt(data[1]);

						insertionOnly.update(sample, update);
						general.update(sample, update);
						turnstile.update(sample, update);
					}
				}
				
				fp.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			System.out.println("####INFO: finish experiment: " + i);
		}
		
		try {
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	private static double[] runOne(IL0Sampler l0Sampler) {
		long numOutput = 0;
		long totalTimeOutput = 0;
		long numUpdate = 0;
		long totalTimeUpdate = 0;

		// updating data
		try (LineNumberReader fp = new LineNumberReader(new FileReader(new File(FILE_PATH)))) {
            String s;
            System.out.println("####INFO: started reading and querying");
            while ((s = fp.readLine()) != null) {
            	// query sketch
            	if (s.startsWith("#")) {
            		if (s.contains("####"))
            			continue;

            		// query items
            		long start = System.nanoTime();
            		l0Sampler.output();
            		long end = System.nanoTime();
            		totalTimeOutput += end - start;
            		numOutput++;
            	}
            	// update sketch
            	else {
            		String[] data = s.split(",");
            		String sample = data[0];
            		int update = Integer.parseInt(data[1]);

            		long start = System.nanoTime();
            		l0Sampler.update(sample, update);
            		long end = System.nanoTime();
            		totalTimeUpdate += end - start;
            		numUpdate++;
            	}
            }
            System.out.println("####INFO: finishing reading and querying");

            fp.close();
            System.out.println("####INFO: end application");

            double result[] = {0.0, 0.0};
            result[0] = (double) totalTimeUpdate / numUpdate;
            result[1] = (double) totalTimeOutput / numOutput;

            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
	}
}
