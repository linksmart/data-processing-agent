/*
 *  
 *  Fosstrak LLRP Commander (www.fosstrak.org)
 * 
 *  Copyright (C) 2008 ETH Zurich
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/> 
 *
 */

package eu.ebbits.pwal.impl.driver.llrpreader.org.fosstrak.llrp.client.repository.sql;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.log4j.Logger;

/**
 * The {@link MySQLRepository} provides the basis for a MySQL repository 
 * back-end. The SQL-server is accessed via the MySQL-JDBC connector.<br/>
 * <h3>NOTICE:</h3>
 * We share most SQL statements with the Derby implementation of the SQL 
 * repository. However, the statement to create the LLRP messages table, and 
 * the way how the connection gets established, differ.<br/>
 * The user credentials as well as the JDBC connector URL are both obtained 
 * from the eclipse preference store (The user can configure the settings 
 * in the Preferences page in the tab LLRP-Commander).
 * @author sawielan
 *
 */
public class MySQLRepository extends AbstractSQLRepository {

    /** the MySQL JDBC driver. */
    protected static final String DB_DRIVER = "com.mysql.jdbc.Driver";
    
    /** default JDBC Connector URL. */
    public static final String JDBC_STR = 
        String.format("jdbc:mysql://localhost:3306/%s", DB_NAME);
    
    // log4j instance.
    private static Logger log = Logger.getLogger(MySQLRepository.class);
    
    @Override
    protected String getDBDriver() {
        return DB_DRIVER;
    }

    @Override
    protected Connection openConnection() throws SQLException {
        return defaultOpenConnection("MySQL", log);
    }
    
    @Override
    protected String sqlCreateTable() {
        return CREATE_TABLE_PREFIX +"MEDIUMTEXT)";
    } 
}
