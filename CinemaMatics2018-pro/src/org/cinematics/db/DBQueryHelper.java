package org.cinematics.db;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class DBQueryHelper {
    
    public static Optional<ResultSet> executePreparedStatementQuery(String querySQL, Object... values) {
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
        } finally {
            DBUtils.closeConnection();
        }    
    }
    
    public static long executePreparedStatementUpdate(String updateSQL, Object... values) {
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
        } finally {
            DBUtils.closeConnection();
        }
    }
    

}
