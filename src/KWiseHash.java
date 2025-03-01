/**
 * A hash function draw from k-wise independent hash family on the domain {1, . . . , p}, 
 * where p is the largest value of integer (2^31 - 1) in java, k must be between [0, 2^31 - 1].
 * K-wise independent is achieved through Polynomial Hashing
 * 
 * xiugec 961392
 * Xiuge Chen
 * xiugec@student.unimelb.edu.au
 * @author xiugechen
 */
public class KWiseHash implements IHash {
	/** large enough prime number p */
	private final static int P = 2147483647;
	/** coefficients of polynomial hashing */
	private Integer[] a = null;
	
	/**
	 * Constructor of hash function from k-wise independent hash family
	 * @param k K, int
	 */
	public KWiseHash(int k) {
		// parameter checking
		if (k < 0) {
			System.out.println("####FATAL: k must be between [0, 2^31 - 1]");
			System.exit(1);
		}
		
		// choose ak−1, . . . , a0 randomly from [1, ... , P]
		a = new Integer[k];
		
		for (int i = 0; i < k; i++) {
			a[i] = StdRandom.uniform(P);
		}
	}

	@Override
	public int hash(Object key, int domain) {
		// parameter checking
		if (domain < 0) {
			System.out.println("####FATAL: Domain has to be between [0, 2^31 - 1]");
			System.exit(1);
		}
		
		if (a == null) {
			System.out.println("####FATAL: Hash function uninitialized");
			System.exit(1);
		}
		
		int key_int = Math.abs(key.hashCode());
		
		// get Polynomial Hashing results
		long results = 0;
		for (int i = 0; i < a.length; i++) {
			results += (a[i] * powMod(key_int, i, P)) % P;
		}
		
		return (int) ((results % P) % domain);
	}
	
	/**
	 * Calculate the result of the power of some int number mod another number
	 * This implementation avoids potential overflow
	 * @param base Base number, int
	 * @param power Power number, int
	 * @param modNum Number used to mod, int
	 * @return base^power mod modNum
	 */
	private long powMod(int base, int power, int modNum) {
		long result = 1;
		for (int i = 0; i < power; i++) {
			result *= base;
			result %= modNum;
		}
		
		return result;
	}
}
