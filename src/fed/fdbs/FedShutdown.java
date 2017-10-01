package fed.fdbs;

/**
 * It implements the Runnable interface. A thread is created by FDBSFacade and added to the ShutdownHook. 
 * This thread will then be executed at the end of the java application. This component calls the method 
 * to drop all the temporal tables.
 *
 */
public class FedShutdown implements Runnable {
    
    /* (non-Javadoc)
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run () {
        if (FedConfig.getDb1Name() != null) {
            FedCatalog.dropTempTables(); 
        }
    }

}
