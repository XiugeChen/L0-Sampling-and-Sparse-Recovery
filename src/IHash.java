/**
 * Interface of all hash functions
 * 
 * xiugec 961392
 * Xiuge Chen
 * xiugec@student.unimelb.edu.au
 * @author xiugechen
 */
public interface IHash {
	/**
	 * Hash a specific key to range [0, ..., domain-1] based on the propriety of hash function.
	 * @param key Key
	 * @param domain Output domain 
	 * @return Hashed value within range [0, ..., domain-1]
	 */
	public abstract int hash(Object key, int domain);
}
