/**
 * Common interface of all kinds of k-space recovery strategies
 * 
 * xiugec 961392
 * Xiuge Chen
 * xiugec@student.unimelb.edu.au
 * @author xiugechen
 */
public interface ISparseRecovery {
	/**
	 * Update k-Space recovery structure by an item and its update
	 * @param item Item
	 * @param update Update of item 
	 */
	public void update(Object item, int update);
	
	/**
	 * Output k item-frequency pair object if the stream is k-sparse
	 * Otherwise, output warning String indicates whether there are 0 or more than k items in the stream
	 * @return An String contains k rows of item-frequency pair (item,frequency) or a String message that reports non k-sparse.
	 */
	public Object output();
}
