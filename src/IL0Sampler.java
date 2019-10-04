/**
 * Common interface of all kinds of L0 Samplers
 * 
 * xiugec 961392
 * Xiuge Chen
 * xiugec@student.unimelb.edu.au
 * @author xiugechen
 */
public interface IL0Sampler {
	/**
	 * Update L0 sampler sketch by an item and its update
	 * @param item Item
	 * @param update Update of item 
	 */
	public void update(Object item, int update);
	
	/**
	 * Output a uniformly selected item with non-zero frequency.
	 * @return
	 */
	public Object output();
}
