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

package eu.ebbits.pwal.impl.driver.llrpreader.org.fosstrak.llrp.client.repository.sql.roaccess;


/**
 * Derby and MySQL implementation for the RO_ACCESS_REPORTS repository (table). 
 * Currently this implementation is used from two {@link Repository} - namely 
 * {@link DerbyRepository} and {@link MySQLRepository}.
 * @author sawielan
 *
 */
public class DerbyROAccessReportsRepository extends AbstractSQLROAccessReportsRepository {

    private static final String TIMESTAMP_STRING = "TIMESTAMP";
    private static final String INTEGER_STRING = "INTEGER";
    
    /**
     * the columns of the RO_ACCESS_REPORTS table and the data types used in the 
     * database to store the values from the LLRP message. The first entry 
     * in the two dimensional array encodes the name of the db column, the 
     * second entry reflects the data type chosen.<br/>
     * <strong>NOTICE:</strong> As java and MySQL both do not support unsigned 
     * values, we need to allocate extra large signed data types to store the 
     * unsigned ones. the allocation mapping is given below:<br/>
     * <ul>
     * <li>unsigned integer -> long -> BIGINT (8Byte value in derby)</li>
     * <li>unsigned short -> integer -> INTEGER (4Byte value in derby)</li>
     * <li>byte -> byte -> SMALLINT (2Byte value in derby)</li>
     * </ul>
     */
    public static final String[][] COLUMN_NAMES_AND_TYPES = new String[][] {
        {"LOG_Time", TIMESTAMP_STRING},
        {"Adapter", "CHAR(64)"},
        {"Reader", "CHAR(64)"},
        {"EPC", "VARCHAR (2048)"},    // allow variable length
                    // NOTICE THAT DERBY DOES NOT PAD TO THE GIVEN LENGTH
        {"ROSpecID", "BIGINT"},        // IN SPEC: UNSIGNED INTEGER
        {"SpecIndex", INTEGER_STRING},     // IN SPEC: UNSIGNED SHORT
        {"InventoryParameterSpecID", INTEGER_STRING},    // IN SPEC: UNSIGNED SHORT
        {"AntennaID", INTEGER_STRING},    // IN SPEC: UNSIGNED SHORT
        {"PeakRSSI", "SMALLINT"},    // IN SPEC: BYTE
        {"ChannelIndex", INTEGER_STRING},// IN SPEC: UNSIGNED SHORT 
        {"FirstSeenTimestampUTC", TIMESTAMP_STRING},     // IN SPEC: UNSIGNED LONG MICROSECONDS TIMESTAMP
        {"FirstSeenTimestampUptime", TIMESTAMP_STRING},     // IN SPEC: UNSIGNED LONG MICROSECONDS TIMESTAMP
        {"LastSeenTimestampUTC", TIMESTAMP_STRING},         // IN SPEC: UNSIGNED LONG MICROSECONDS TIMESTAMP
        {"LastSeenTimestampUptime", TIMESTAMP_STRING},     // IN SPEC: UNSIGNED LONG MICROSECONDS TIMESTAMP
        {"TagSeenCount", INTEGER_STRING},    // IN SPEC: UNSIGNED SHORT
        {"C1G2_CRC", INTEGER_STRING},        // IN SPEC: UNSIGNED SHORT
        {"C1G2_PC", INTEGER_STRING},            // IN SPEC: UNSIGNED SHORT
        {"AccessSpecID", "BIGINT"}         // IN SPEC: UNSIGNED INTEGER
    };

    @Override
    protected String sqlCreateTable() {
        String fields = "";
        final int len = COLUMN_NAMES_AND_TYPES.length;
        final int lenm = len - 1;
        for (int i=0; i<len; i++) {
            fields += String.format("%s %s", 
                    COLUMN_NAMES_AND_TYPES[i][0], 
                    COLUMN_NAMES_AND_TYPES[i][1]);
            // append a comma, if not last entry
            if (i < lenm) { 
                fields += ","; 
            }
        }
        return String.format(
                "create table %s (%s)", TABLE_RO_ACCESS_REPORTS, fields);
    }

    @Override
    protected String sqlInsert() {
        return String.format("insert into %s values ", 
                TABLE_RO_ACCESS_REPORTS) +
                "(?, ?, ?, ?, ?, ?, ?, ?, " +
                "?, ?, ?, ?, ?, ?, ?, ?, " +
                "?, ?)";
    }

    @Override
    protected String sqlDropTable() {
        return String.format("DROP TABLE %s", TABLE_RO_ACCESS_REPORTS);
    }
    
    /**
     * Constructor of the DerbyROAccesReportsRepository
     */
    public DerbyROAccessReportsRepository() {
        super();
    }
}
