import org.apache.log4j.Logger;

/**
 * L0 sampler that is designed to deal with all kinds of general stream
 * 
 * xiugec 961392
 * Xiuge Chen
 * xiugec@student.unimelb.edu.au
 * @author xiugechen
 */
public class L0Sampler_General implements IL0Sampler {
	/** logger */
	private final static Logger logger = Logger.getLogger(L0Sampler_General.class);
	/** Fail message */
	public final static String FAIL = "FAIL";
	
	/** domain of hashed value */
	private int hashedDomain;
	/** number of level */
	private int L;
	/** t-wise hash function, where t >= k/2 */
	private IHash hash;
	/** L levels of k-sparse recovery structure */
	private KSparseRecovery[] K;
	
	public L0Sampler_General(int domain, double delta) {
		// check if n^3 is still smaller than Max int
		if (Integer.MAX_VALUE < Math.pow(domain, 3)) {
			logger.fatal("domain can't be larger than the quartic root of largest integer value");
			System.exit(1);
		}
		
		// k-sparse parameter k and t-wise hash parameter t
		int k = (int) (12 * Math.log(1 / delta));
		int t = k / 2;
		
		this.hashedDomain = (int) Math.pow(domain, 3);
		this.hash = new KWiseHash(t);
		this.L = (int) Math.log(domain);
		
		this.K = new KSparseRecovery[L];
		for (int i = 0; i < L; i++) {
			K[i] = new KSparseRecovery(k, delta);
		}
	}

	@Override
	public void update(Object item, int update) {
		int hx = this.hash.hash(item, this.hashedDomain);
		
		// update this item from level 1 to level l with probability 2^{-l}
		for (int l = 0; l < this.L; l++) {
			if (hx < Math.pow(2, -l) * this.hashedDomain) {
				this.K[l].update(item, update);
			}
			else {
				break;
			}
		}
	}

	@Override
	public Object output() {
		String A = null;
		
		for (int l = 0; l < this.L; l++) {
			
			if (K[l].output() != KSparseRecovery.MORE_K_SPARSE && K[l].output() != KSparseRecovery.ZERO_SPARSE) {
				A = (String) K[l].output();
			}
		}
		
		if (A == null) {
			return FAIL;
		}
		
		// return the item has minimum hashed value (mimic randomly choose)
		String[] itemList = A.split("\n");
		int minHash = this.hashedDomain;
		String minItem = null;
		
		for (int i = 0; i < itemList.length; i++) {
			String item = itemList[i].split(",")[0];
			int itemHash = this.hash.hash(item, hashedDomain);
			
			if (itemHash < minHash) {
				minHash = itemHash;
				minItem = item;
			}
		}
		
		return minItem;
	}

}
