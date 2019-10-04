/**
 * Test functions
 * 
 * xiugec 961392
 * Xiuge Chen
 * xiugec@student.unimelb.edu.au
 * @author xiugechen
 */
public class Test {
	public static void main(String[] args) {
		// KWiseHashTest();
		L0Sampler_InsertionOnlyTest();
	}
	
	/**
	 * Test whether implemented KWiseHash class satisfy its property from definition
	 */
	public static void KWiseHashTest() {
		// settings, do not set k or domain to be too large
		int k = 2;
		int domain = 3;
		int numExperiments = 1000000;
		
		// keys initialization
		Object[] keys = new Object[k];
		for (int i = 0; i < k; i++) {
			keys[i] = Integer.toString(i);
		}
		
		int results[] = new int[(int) Math.pow(domain, k)];
		
		// run numExperiments number of experiments and summarize the k hash values the keys are hashed into
		// and see if all pairs (size k) of values are equally like to be hashed
		for (int i = 0; i < numExperiments; i++) {
			IHash hash = new KWiseHash(k);
			int index = 0;
			
			for (int j = 0; j < keys.length; j++) {
				index += (int) (hash.hash(keys[j], domain) * Math.pow(domain, j));
			}
			
			results[index]++;
		}
		
		for (int i = 0; i < results.length; i++) {
			System.out.println(i + ": " + (double)results[i] / numExperiments);
		}
	}
	
	/**
	 * Test whether L0Sampler_InsertionOnly performs in expectation (uniform selection over insertion-only stream)
	 */
	public static void L0Sampler_InsertionOnlyTest() {
		// settings
		int domain = Integer.MAX_VALUE;
		double epsilon = 0.01;
		double percentage0 = 0.1;
		int numExperiments = 1000;
		int numDraw = 100;
		
		int realDistribution[] = {0, 0};
		int results[] = {0, 0};
		
		// run numExperiments number of experiments on designed unequally distributed stream
		// see if items are selected uniformly
		for (int i = 0; i < numExperiments; i++) {
			IL0Sampler sampler = new L0Sampler_InsertionOnly(domain, epsilon);
			
			for (int j = 0; j < numDraw; j++) {
				if (StdRandom.uniform((int)(1 / percentage0) ) >= 1) {
					sampler.update("1", 1);
					realDistribution[1]++;
				}
				else {
					sampler.update("0", 1);
					realDistribution[0]++;
				}
			}
			
			Object output = sampler.output();
			
			if (output.equals("1")) {
				results[1]++;
			}
			else if (output.equals("0")) {
				results[0]++;
			}
		}
		
		for (int i = 0; i < results.length; i++) {
			System.out.println("Real " + i + ": " + (double)results[i] / numExperiments);
			System.out.println("Sampled " + i + ": " + (double)realDistribution[i] / numExperiments / numDraw);
		}
	}
}
