import org.apache.log4j.Logger;

/**
 * A hash function draw from k-wise independent hash family on the domain {1, . . . , p}, 
 * where p is the largest value of integer in java.
 * K-wise independent is achieved through Polynomial Hashing
 * 
 * xiugec 961392
 * Xiuge Chen
 * xiugec@student.unimelb.edu.au
 */
public class KWiseHash implements IHash {
	/** logger */
	private final static Logger logger = Logger.getLogger(KWiseHash.class);
	/** large enough prime number p */
	private final static int P = 2147483647;
	
	private Integer[] a = null;
	
	/**
	 * Constructor of hash function from k-wise independent hash family
	 * @param k K, int
	 */
	public KWiseHash(int k) {
		// choose ak−1, . . . , a0 randomly from [1, ... , P]
		a = new Integer[k];
		
		for (int i = 0; i < k; i++) {
			a[i] = StdRandom.uniform(P);
		}
	}

	@Override
	public int hash(Object key, int domain) {
		if (a == null) {
			logger.fatal("Hash function uninitialized");
			System.exit(1);
		}
		
		int key_int = Math.abs(key.hashCode());
		
		// get Polynomial Hashing results
		long results = 0;
		for (int i = 0; i < a.length; i++) {
			results += a[i] * Math.pow(key_int, i) % domain;
		}
		
		return (int) results % domain;
	}
}
