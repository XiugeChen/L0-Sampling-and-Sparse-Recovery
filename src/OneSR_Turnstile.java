import org.apache.log4j.Logger;

/**
 * 1 Sparse recovery data structure that could work over only turnstile stream.
 * 
 * xiugec 961392
 * Xiuge Chen
 * xiugec@student.unimelb.edu.au
 * @author xiugechen
 */
public class OneSR_Turnstile implements ISparseRecovery {
	/** Output message when there is 0 items in the stream */
	public static final String ZERO_SPARSE = "zero items in the stream";
	/** Output message when there are more than k items in the stream */
	public static final String MORE_K_SPARSE = "more than one items in the stream";
	
	/** logger */
	private final static Logger logger = Logger.getLogger(OneSR_General.class);
	
	/** the sum of frequencies in stream */
	private long F1;
	/** the average value of the items in the stream, multiplied by F1 */
	private long U;
	/** the average of the square of the items in the stream, multiplied by F1 */
	private long V;
	
	/**
	 * Constructor
	 */
	public OneSR_Turnstile() {
		this.F1 = 0;
		this.U = 0;
		this.V = 0;
	}
	
	@Override
	public void update(Object item, int update) {
		int itemNum = -1;
		
		try {
			itemNum = Integer.parseInt(item.toString());
		} catch (Exception e) {
			logger.warn("Directly parse item to int failed, use default 1");
			itemNum = 1;
		}
		
		this.F1 += update;
		this.U += update * itemNum;
		this.V += update * Math.pow(itemNum, 2);
	}
	
	@Override
	public Object output() {
		// if the stream has 0 item, output report message
		if (F1 == 0) {
			return ZERO_SPARSE;
		}
		
		// if the stream is 1 sparse, output a list contains this item and frequency pair
		if (Math.pow(U, 2) == V * F1) {
			String result = String.format("%d,%d", (int) (U / F1), (int) F1);
			return result;
		}
		
		// if the stream has more than one items, output report message
		return MORE_K_SPARSE;
	}
}
