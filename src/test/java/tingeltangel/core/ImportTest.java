/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tingeltangel.core;

import java.util.LinkedList;
import java.util.List;
import java.util.Iterator;
import java.io.File;
import java.io.IOException;
import org.junit.Test;
import tingeltangel.core.scripting.SyntaxError;
import tingeltangel.tools.FileEnvironment;

/**
 *
 * @author martin
 */
public class ImportTest {
    
    @Test
    public void testImportOuf() throws IOException, SyntaxError {
        
        // switch to test mode
        FileEnvironment.test();
        
        List<Integer> success = new LinkedList<Integer>();
	List<Integer> failed = new LinkedList<Integer>();

        Integer[] iDs = Repository.getIDs();
        
        // int[] iDs = {5236, 5246};
        
        for(int i = 0; i < iDs.length; i++) {
            int id = iDs[i];
            if(id > 0) {
                File ouf = Repository.getBookOuf(id);
                if(ouf != null) {
                    System.out.println("\nImporting " + id + " ...");
                    try {
                        Importer.importOuf(ouf, null, null, new Book(id, null), null);
			success.add(id);
                    } catch(Exception e) {
                        System.out.println("********** ERROR: " + e.getMessage() + " ****************");
                        failed.add(id);
                    }
                }
            }
        }
        
	System.out.println("success:");
	Iterator<Integer> i = success.iterator();
	while(i.hasNext()) {
		System.out.print(" " + i.next());
	}
	System.out.println();

	System.out.println("failed:");
	i = failed.iterator();
	while(i.hasNext()) {
		System.out.print(" " + i.next());
	}
	System.out.println();


        if(failed.size() > 0) {
		System.out.println("imported " + success.size() + " out of " + (success.size() + failed.size()) + " books");
		// throw new IOException("imported " + success.size() + " out of " + (success.size() + failed.size()) + " books");
	} else if((success.size() + failed.size()) == 0) {
		System.out.println("no books found in repository, so no books imported for testing");
        } else {
		System.out.println("all " + success.size() + " books successfully imported for testing");
	}

    }
    
}
