/**
 * L0 sampler that is designed to deal with insertion-only stream (only positive update allowed)
 * General stream could be test on this sampler but the performance is not ideal
 * 
 * xiugec 961392
 * Xiuge Chen
 * xiugec@student.unimelb.edu.au
 * @author xiugechen
 */
public class L0Sampler_InsertionOnly implements IL0Sampler {
	/** smallest hashed value being tracked */
	private long m;
	/** object that has the smallest hashed value */
	private Object a;
	/** hash function being used, randomly select at initialization */
	private IHash hash;
	/** domain of objects */ 
	private int domain;
	
	/**
	 * Initialize L0 Sampler for insertion-only stream
	 * Here the ε-min-wise independent hash family was replaced by equal log 1/ε-wise independent family
	 * 
	 * @param n Domain of items
	 * @param eplison ε for ε-min-wise independent hash family)
	 */
	public L0Sampler_InsertionOnly(int n, double eplison) {
		this.m = (long) n + 1;
		this.domain = n;
		this.a = null;
		hash = new KWiseHash((int) (1 / eplison));
	}
	
	@Override
	public void update(Object item, int update) {
		// if negative update output warnings
		if (update < 0) {
			//System.out.println("####WARN: L0Sampler_InsertionOnly is designed to handle insertion-only stream"
			//		+ ", negative update will affect the performance");
		}
		
		// Store the item if it has the minimum hashed value
		int hashed_value = hash.hash(item, this.domain);
		
		if (hashed_value < m) {
			this.a = item;
			this.m = hashed_value;
		}
		
		return;
	}
	
	@Override
	public Object output() {
		return this.a;
	}
}
