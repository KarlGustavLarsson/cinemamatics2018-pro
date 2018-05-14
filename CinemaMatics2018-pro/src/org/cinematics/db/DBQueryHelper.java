package org.cinematics.db;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class DBQueryHelper {
    
    public static Optional<ResultSet> prepareAndExecuteStatementQuery(String querySQL, Object... values) throws SQLException {
        
    	Connection connection = DBUtils.getConnection();
        PreparedStatement stmt = connection.prepareStatement(querySQL);
        for(int i = 0; i < values.length; i++) {
            stmt.setObject(i+1, values[i]);
        }
        ResultSet results = stmt.executeQuery();
        return Optional.of(results);
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
    
    public static ResultSet prepareAndExecuteStatementUpdateReturnKeys(String updateSQL, Object... values) throws SQLException {
		Connection connection = DBUtils.getConnection();
		PreparedStatement stmt = connection.prepareStatement(updateSQL, PreparedStatement.RETURN_GENERATED_KEYS);
		for (int i = 0; i < values.length; i++) {
			stmt.setObject(i + 1, values[i]);
		}
		stmt.executeUpdate();
	    ResultSet rs = stmt.getGeneratedKeys();
	    return rs;
	}
}
