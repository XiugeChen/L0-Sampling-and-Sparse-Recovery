import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Logger;

/**
 * 1 Sparse recovery data structure that could work over arbitrary integer vectors.
 * 
 * xiugec 961392
 * Xiuge Chen
 * xiugec@student.unimelb.edu.au
 * @author xiugechen
 */
public class OneSparseRecovery implements ISparseRecovery {
	/** Output message when there is 0 items in the stream */
	public static final String ZERO_SPARSE = "zero items in the stream";
	/** Output message when there are more than k items in the stream */
	public static final String MORE_K_SPARSE = "more than one items in the stream";
	
	/** logger */
	private final static Logger logger = Logger.getLogger(OneSparseRecovery.class);
	/** large enough prime */
	private static final int P = Integer.MAX_VALUE;
	
	/** weighted sum of item identifiers */
	private long iota;
	/** sum of weights */
	private long phi;
	/** fingerprint */
	private long tau;
	/** parameter used in calculated fingerprint */
	private int z;
	
	/**
	 * Constructor
	 */
	public OneSparseRecovery() {
		this.iota = 0;
		this.phi = 0;
		this.tau = 0;
		this.z = StdRandom.uniform(P);
	}
	
	@Override
	public void update(Object item, int update) {
		int itemNum = -1;
		long new_update = (long) update;
		
		try {
			itemNum = Integer.parseInt(item.toString());
		} catch (Exception e) {
			logger.warn("Directly parse item to int failed, use default 1");
			itemNum = 1;
		}
		
		this.phi += update;
		this.iota += update * itemNum;
		this.tau += (long) ((long) new_update * powMod(z, itemNum, this.P));
	}
	
	@Override
	public Object output() {
		// if the stream has 0 item, output report message
		if (phi == 0) {
			return ZERO_SPARSE;
		}
		
		this.tau = this.tau % P;
		long estimated = (long) ((phi * powMod(z, (int) (iota/phi), this.P)) % P);
		
		// if the stream is 1 sparse, output a list contains this item and frequency pair
		if (tau == estimated) {
			ArrayList<HashMap<Object, Integer>> result = new ArrayList<>();
			
			HashMap<Object, Integer> pairMap = new HashMap<>();
			Object item = Long.toString((long) (iota / phi));
			pairMap.put(item, (int) phi);
			
			result.add(pairMap);
			return result;
		}
		
		// if the stream has more than one items, output report message
		return MORE_K_SPARSE;
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
