package org.cinematics.db;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class DBQueryHelper {
	
    public static Logger logger = LogManager.getLogManager().getLogger(Logger.GLOBAL_LOGGER_NAME);
    
    public static Optional<ResultSet> prepareAndExecuteStatementQuery(String querySQL, Object... values) {
    	Connection connection = DBUtils.getConnection();
        try {
            PreparedStatement stmt = connection.prepareStatement(querySQL);
            logger.log(Level.INFO, "PrepareQ: "+querySQL);
            
            for(int i = 0; i < values.length; i++) {
                stmt.setObject(i+1, values[i]);
                logger.log(Level.INFO, " -> "+values[i]);
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
            logger.log(Level.INFO, "PrepareU: "+updateSQL);
            
            for(int i = 0; i < values.length; i++) {
                stmt.setObject(i+1, values[i]);
                logger.log(Level.INFO, " -> "+values[i]);
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
            logger.log(Level.INFO, "PrepareUK: "+updateSQL);
            
            for(int i = 0; i < values.length; i++) {
                stmt.setObject(i+1, values[i]);
                logger.log(Level.INFO, " -> "+values[i]);
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
    		logger.log(Level.INFO, "Starting Transaction Update");

    		PreparedStatement stmt = connection.prepareStatement(updateSQL, PreparedStatement.RETURN_GENERATED_KEYS);
            logger.log(Level.INFO, "PrepareTUK: "+updateSQL);
            
            for(int i = 0; i < values.length; i++) {
                stmt.setObject(i+1, values[i]);
                logger.log(Level.INFO, " -> "+values[i]);
            }
            
            long res = stmt.executeUpdate();
            return res;
            
        } catch (SQLException e) { // Exception setAutoCommit, prepareStatement, executeUpdate
            try {
				connection.rollback();
				logger.log(Level.INFO, "Exception Transaction rollback");
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
				logger.log(Level.INFO, "Transaction rollback");
    			return false;
    		} else {
    			connection.commit();
				logger.log(Level.INFO, "Transaction commit");
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
