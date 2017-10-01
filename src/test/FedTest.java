package test;

import fed.facade.FDBSFacade;


/**
 * Test the FDBS implementation
 *
 */
public class FedTest {
    
    public static void main (String[] args) {
        try {
            
            new FDBSFacade().start();
            
        } catch (Exception e) {
            System.out.println("Oops! Unhandled exception! Reason:\n" + e.getMessage());;
        }
    }
    
}
