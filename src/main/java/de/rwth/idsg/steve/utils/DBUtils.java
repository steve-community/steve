package de.rwth.idsg.steve.utils;

import java.sql.PreparedStatement;

/**
*
* @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
* 
*/
public final class DBUtils {
    private DBUtils() {}

    /**
     * Validates the BATCH execution of Data Manipulation Language (DML) statements, such as INSERT, UPDATE or DELETE.
     *
     * If the row value in the updateCounts array is 0 or greater, the update was successfully executed.
     * A value of SUCCESS_NO_INFO means update was successfully executed, but MySQL server unable to determine the number of rows affected.
     * A value of EXECUTE_FAILED means that an error has occured.
     */
    public static boolean validateDMLChanges(int [] updateCounts) {
        boolean updatedAll = false;

        for (int updateCount : updateCounts) {
            if (updateCount >= 1) {
                updatedAll = true;
            } else if (updateCount == (PreparedStatement.SUCCESS_NO_INFO | PreparedStatement.EXECUTE_FAILED)) {
                updatedAll = false;
                break;
            }
        }
        return updatedAll;
    }
}