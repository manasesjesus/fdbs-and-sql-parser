package fed.fdbs;

/**
 * Interface provided by Prof. Dr. Peter Peinl
 */
public interface FedPseudoDriverInterface {
	public FedConnection getConnection(String username, String password) throws FedException;
}
