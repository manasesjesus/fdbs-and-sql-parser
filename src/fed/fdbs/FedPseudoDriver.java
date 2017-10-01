package fed.fdbs;

/**
 * This class creates a FedPseudoDriver object to retrieve the established 
 * connections to the databases (contained in a FedConnection object).
 *
 */
public class FedPseudoDriver implements FedPseudoDriverInterface {

    /**
     * Return a FedConnection object using the default configurations.
     * 
     * @return FedConnection
     * @throws FedException
     */
    public FedConnection getConnection () throws FedException {
        return this.getConnection(FedConfig.getUser(), FedConfig.getPasswd());
    }
    
    /* (non-Javadoc)
     * @see fedjdbc.FedPseudoDriverInterface#getConnection(java.lang.String, java.lang.String)
     */
    @Override
    public FedConnection getConnection (String username, String password) throws FedException {
        return new FedConnection(username, password);
    }

}
