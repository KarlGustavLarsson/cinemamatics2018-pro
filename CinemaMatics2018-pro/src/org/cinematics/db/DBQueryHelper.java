package org.cinematics.db;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class DBQueryHelper {
    	
    public static Optional<ResultSet> prepareAndExecuteStatementQuery(String querySQL, Object... values) {
        
    	Connection connection = DBUtils.getConnection();
        try {
            PreparedStatement stmt = connection.prepareStatement(querySQL);
            for(int i = 0; i < values.length; i++) {
                stmt.setObject(i+1, values[i]);
            }
            ResultSet results = stmt.executeQuery();
            return Optional.of(results);
            
        } catch (SQLException e) {
            System.err.println(e);
            return Optional.empty();
        }    
    }
    
    public static long prepareAndExecuteStatementUpdate(String updateSQL, Object... values) {
    	Connection connection = DBUtils.getConnection();
        try {
            PreparedStatement stmt = connection.prepareStatement(updateSQL);
            for(int i = 0; i < values.length; i++) {
                stmt.setObject(i+1, values[i]);
            }
            long result = stmt.executeUpdate();
            return result;
            
        } catch (SQLException e) {
            System.err.println(e);
            return 0;
        }
    }
    
    public static ResultSet prepareAndExecuteStatementUpdateReturnKeys(String updateSQL, Object... values) {
    	Connection connection = DBUtils.getConnection();
        try {
            PreparedStatement stmt = connection.prepareStatement(updateSQL, PreparedStatement.RETURN_GENERATED_KEYS);
            for(int i = 0; i < values.length; i++) {
                stmt.setObject(i+1, values[i]);
            }
            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            return rs;
        } catch (SQLException e) {
            System.err.println(e);
            return null;
        }
    }

    public static long startTransactionUpdate(String updateSQL, Object... values) {
    	Connection connection = DBUtils.getConnection();
    	try {
    		connection.setAutoCommit(false);
            PreparedStatement stmt = connection.prepareStatement(updateSQL, PreparedStatement.RETURN_GENERATED_KEYS);
            for(int i = 0; i < values.length; i++) {
                stmt.setObject(i+1, values[i]);
            }
            long res = stmt.executeUpdate();
            return res;
        } catch (SQLException e) { // Exception setAutoCommit, prepareStatement, executeUpdate
            try {
				connection.rollback();			
			} catch (SQLException e1) { // Exception from rollback
				System.err.println(e1);		
			} finally { // If rollback happens, we need to set autocommit to true again
				try {
					connection.setAutoCommit(true);
				} catch (SQLException e1) {
					System.err.println(e1);
				}
			}
            return 0;
        }
    }
    
    public static boolean endTransactionUpdate(boolean doRollback) {
    	Connection connection = DBUtils.getConnection();
    	
    	try {
    		// Transaction has not been started, autoCommit is still true
    		if(connection.getAutoCommit()) {
        		return true;
        	}
    		if(doRollback) {
    			connection.rollback();
    			return false;
    		} else {
    			connection.commit();
        		return true;
    		}
        } catch (SQLException e) {
            System.err.println(e);
            return false;
        } finally {
        	try {
				connection.setAutoCommit(true);
			} catch (SQLException e) {
				System.err.println(e);
			}
        }
    }
    
}
