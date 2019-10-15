
/**
 * K Sparse recovery data structure that could work over only turnstile stream.
 * 
 * xiugec 961392
 * Xiuge Chen
 * xiugec@student.unimelb.edu.au
 * @author xiugechen
 */
public class KSR_Turnstile extends KSR {
	public KSR_Turnstile(int k, double delta) {
		super(k, delta);
		
		for (int i = 0; i < cells.length; i++) {
			for (int j = 0; j < cells[i].length; j++) {
				cells[i][j] = new OneSR_Turnstile();
			}
		}
	}
}
