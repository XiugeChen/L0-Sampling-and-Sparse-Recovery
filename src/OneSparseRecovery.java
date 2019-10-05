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
		this.tau += (long) ((long) new_update * Math.pow(z, itemNum));
	}
	
	@Override
	public Object output() {
		// if the stream has 0 item, output report message
		if (phi == 0) {
			return "zero items in the stream";
		}
		
		this.tau = this.tau % P;
		long estimated = (long) ((phi * Math.pow(z, iota / phi)) % P);
		
		// if the stream is 1 sparse, output this item and update
		if (tau == estimated) {
			HashMap<Object, Integer> result = new HashMap<>();
			Object item = Long.toString((long) (iota / phi));
			result.put(item, (int) phi);
			return result;
		}
		
		// if the stream has more than one items, output report message
		return "more than one item in the stream";
	}
}
