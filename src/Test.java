/**
 * Test functions
 * 
 * xiugec 961392
 * Xiuge Chen
 * xiugec@student.unimelb.edu.au
 * @author xiugechen
 */
public class Test {
	private static final String INSERTION_ONLY = "insertion-only";
	private static final String TURNSTILE = "turnstile";
	private static final String GENERAL = "general";
	private static final String PAIRWISE = "pairwise";
	
	public static void main(String[] args) {
		System.out.println("####INFO: Test starts");
		
		// KWiseHashTest();
		// L0SamplerTest(GENERAL);
		// SparseRecoveryTest(1);
		// SparseRecoveryTest(4);
		// DistinctElementTest(9009);
		
		for (int k = 0; k < 10; k++) {
			L0Sampler_InsertionOnly sampler =  new L0Sampler_InsertionOnly(100, 0.01);
		
			for (int i = 1; i <= 10; i++) {
				sampler.update(Integer.toString(i), 1);
			}
		
			System.out.println(sampler.output().toString());
		}
		
		System.out.println("####INFO: Test ends");
	}
	
	/**
	 * Test whether implemented KWiseHash class satisfy its property from definition
	 */
	public static void KWiseHashTest() {
		// settings, do not set k or domain to be too large
		int k = 2;
		int domain = 2;
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
	 * Test whether L0Sampler performs in expectation (uniform selection over insertion-only stream)
	 * @param streamType The type of stream, insertion-only, turnstile, or general
	 */
	public static void L0SamplerTest(String streamType) {
		// settings
		int domain = 3;
		double epsilon = 0.01;
		double delta = 0.01;
		double percentage0 = 0.1;
		int numExperiments = 100;
		int numDraw = 100;
		
		int realDistribution[] = {0, 0, 0};
		int results[] = {0, 0, 0};
		
		// run numExperiments number of experiments on designed unequally distributed stream
		// see if items are selected uniformly
		for (int i = 0; i < numExperiments; i++) {
			IL0Sampler sampler = null;
			
			if (streamType.equals(INSERTION_ONLY)) {
				sampler = new L0Sampler_InsertionOnly(domain, epsilon);
			}
			else if (streamType.equals(TURNSTILE)) {
				sampler = new L0Sampler_Turnstile(domain, epsilon);
			}
			else if (streamType.equals(GENERAL)){
				sampler = new L0Sampler_General(domain, epsilon);
			}
			else {
				sampler = new L0Sampler_Pairwise_General(domain, epsilon, delta);
			}
			
			
			for (int j = 0; j < numDraw; j++) {
				int update1 = 1;
				int update2 = 1;
				
				if (!streamType.equals(INSERTION_ONLY)) {
					update1 = StdRandom.uniform(2) == 0 ? 5 : -1;
					update2 = StdRandom.uniform(2) == 0 ? 5 : -1;
				}
				
				if (StdRandom.uniform((int)(1 / percentage0) ) >= 1) {
					sampler.update("2", update2);
					realDistribution[1]++;
				}
				else {
					sampler.update("1", update1);
					realDistribution[0]++;
				}
			}
			
			String output = (String) sampler.output();
			
			if (output.equals(L0Sampler.FAIL)) {
				results[2]++;
			}
			else if (output.equals("2")) {
				results[1]++;
			}
			else if (output.equals("1")) {
				results[0]++;
			}
		}
		
		for (int i = 0; i < results.length - 1; i++) {
			System.out.println("Sampled " + (i+1) + ": " + (double)results[i] / numExperiments);
			System.out.println("Real " + (i+1) + ": " + (double)realDistribution[i] / numExperiments / numDraw);
		}
		System.out.println("Failed: " + (double)results[results.length - 1] / numExperiments);
	}
	
	/**
	 * Test whether k-Sparse Recovery performs in expectation (output item-value pair if the stream only has one item,
	 * message report if the stream has 0 or more than 1 items)
	 */
	public static void SparseRecoveryTest(int k) {
		// settings
		int maxNumItems = 8;
		int numDraw = 100;
		int numExperiments = 10000;
		double delta = 0.001;
		
		// run numExperiments experiments for different number of items from 1 to maxNumItems
		for (int i = 1; i <= maxNumItems; i++) {
			ISparseRecovery sparseRecovery = null;
			double zeroCount = 0;
			double moreCount = 0;
			double kCount = 0;
			
			for (int l = 0; l < numExperiments; l++) {
				if (k == 1) {
					sparseRecovery = new OneSR_General();
				} 
				else {
					sparseRecovery = new KSR_General(k, delta);
				}
				
				for (int j = 0; j < numDraw; j++) {
					// randomly init item and update
					Object item = Integer.toString(StdRandom.uniform(i) + 1);
					int update = StdRandom.uniform(2) == 0 ? 5 : 1;
					
					sparseRecovery.update(item, update);
				}
				
				Object output = sparseRecovery.output();
				
				if (output.equals(OneSR_General.ZERO_SPARSE) || output.equals(KSR.ZERO_SPARSE)) {
					zeroCount++;
				}
				else if (output.equals(OneSR_General.MORE_K_SPARSE) || output.equals(KSR.MORE_K_SPARSE)) {
					moreCount++;
				}
				else {
					kCount++;
				}
			}
			
			System.out.println("####INFO: result for " + i + " items");
			System.out.println("Count for zero items output: " + zeroCount / numExperiments);
			System.out.println("Count for more than k items output: " + moreCount / numExperiments);
			System.out.println("Count for k items output: " + kCount / numExperiments);
		}
	}
	
	private static void DistinctElementTest(int numDistinctItems) {
		System.out.println("####INFO: DistinctElementTest started");
		
		int domain = (int) Math.pow(10, 8);
		double epsilon = 0.01;
		double delta = 0.01;
		
		DistinctElement de = new DistinctElement(domain, epsilon, delta);
		
		for (int i = 1; i <= numDistinctItems; i++) {
			String item = Integer.toString(i);
			int update = 1;
			
			de.update(item, update);
		}
		
		System.out.println("Real distinct items: " + numDistinctItems);
		System.out.println("Estimated distinct items: " + de.output());
	}
}
