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
				index += (int) ((int) hash.hash(keys[j], domain) * Math.pow(domain, j));
			}
			
			results[index]++;
		}
		
		for (int i = 0; i < results.length; i++) {
			System.out.println(i + ": " + (double)results[i] / numExperiments);
		}
	}
}
