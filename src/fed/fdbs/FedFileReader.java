package fed.fdbs;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 * This class is used to read SQL statements from a file.
 *
 */
public class FedFileReader {

    /**
     * Read SQL statements from a file, remove all the comments but keeps the FedLogger INFO messages, 
     * and return the statements as a List object.
     * 
     * @param filename
     *            The file containing the SQL statements
     * @return A list with the SQL statements
     * @throws FileNotFoundException
     *             if the file can not be found
     */
    @SuppressWarnings("resource")
    public static List<String> getSQLStatements (String filename) throws FileNotFoundException {
        String sqlst;
        String statements = "";
        boolean multi_comment = false;
        Scanner scanner = new Scanner(new File(filename)).useDelimiter("\n");

        while (scanner.hasNext()) {
            sqlst = scanner.next().trim();
            
            if (sqlst.contains("[[") && sqlst.contains("]]")) {
                // Trace logger message
                sqlst = sqlst.substring(sqlst.indexOf("[["), sqlst.indexOf("]]")) + ";";
            }
            else if (sqlst.contains("--")) {
                // Single line comment
                sqlst = sqlst.substring(0, sqlst.indexOf("--"));
            } else if (sqlst.contains("/*") && sqlst.contains("*/")) {
                // Single line comment
                sqlst = sqlst.substring(0, sqlst.indexOf("/*"));
            } else if (sqlst.contains("/*")) {
                // Multi-line comment
                multi_comment = true;
            }

            if (!multi_comment && sqlst.trim().length() > 0) {
                sqlst = sqlst.replaceAll("\t", " ");
                statements += (sqlst.contains(";") ? sqlst.trim() : sqlst.trim() + " ");
            } else if (sqlst.contains("*/")) {
                multi_comment = false;
            }
        }

        return Arrays.asList(statements.split(";"));
    }

}
