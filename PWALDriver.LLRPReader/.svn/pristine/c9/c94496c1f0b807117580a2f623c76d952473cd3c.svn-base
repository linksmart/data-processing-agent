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

import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.llrp.ltk.generated.interfaces.AirProtocolTagData;
import org.llrp.ltk.generated.interfaces.EPCParameter;
import org.llrp.ltk.generated.messages.RO_ACCESS_REPORT;
import org.llrp.ltk.generated.parameters.AccessSpecID;
import org.llrp.ltk.generated.parameters.AntennaID;
import org.llrp.ltk.generated.parameters.C1G2_CRC;
import org.llrp.ltk.generated.parameters.C1G2_PC;
import org.llrp.ltk.generated.parameters.ChannelIndex;
import org.llrp.ltk.generated.parameters.EPCData;
import org.llrp.ltk.generated.parameters.EPC_96;
import org.llrp.ltk.generated.parameters.FirstSeenTimestampUTC;
import org.llrp.ltk.generated.parameters.FirstSeenTimestampUptime;
import org.llrp.ltk.generated.parameters.InventoryParameterSpecID;
import org.llrp.ltk.generated.parameters.LastSeenTimestampUTC;
import org.llrp.ltk.generated.parameters.LastSeenTimestampUptime;
import org.llrp.ltk.generated.parameters.PeakRSSI;
import org.llrp.ltk.generated.parameters.ROSpecID;
import org.llrp.ltk.generated.parameters.SpecIndex;
import org.llrp.ltk.generated.parameters.TagReportData;
import org.llrp.ltk.generated.parameters.TagSeenCount;
import org.llrp.ltk.types.BitArray_HEX;
import org.llrp.ltk.types.Integer96_HEX;

/**
 * Helper class to access and maintain the content of RO_ACCESS_REPORTS.
 * @author sawielan
 *
 */
public class ROAccessItem {
    
    // the log4j logger.
    private static Logger log = Logger.getLogger(ROAccessItem.class);
    
    // the log time.
    private Timestamp logTime;
    
    // the name of the adapter.
    private String adapterName;
    
    // the name of the reader.
    private String readerName;
    
    // the EPC.
    private String epc;
    
    // the ROSpecID
    private Long roSpecID;
    
    // spec index.
    private Integer specIndex;
    
    // inventory parameter spec ID.
    private Integer inventoryPrmSpecID;
    
    // antenna ID.
    private Integer antennaID;
    
    // peak RSSI.
    private Short peakRSSI;
    
    // channel index.
    private Integer channelIndex;
    
    // the first seen UTC time stamp.
    private Timestamp firstSeenUTC;
    
    // the first seen since uptime time stamp.
    private Timestamp firstSeenUptime;
    
    // the last seen time stamp UTC.
    private Timestamp lastSeenUTC;
    
    // the last seen time stamp since uptime.
    private Timestamp lastSeenUptime;
    
    // the tag count.
    private Integer tagSeenCount;
    
    // c1g2_crc
    private Integer c1g2_CRC;
    
    // c1g2_pc
    private Integer c1g2_PC;
    
    // the access spec ID.
    private Long accessSpecID;
    
    /**
     * parses the entries of an RO_ACCESS_REPORTS into a list of ROAccessItems.
     * @param report the RO_ACCESS_REPORTS message.
     * @param adapterName the name of the adapter.
     * @param readerName the name of the reader.
     * @return a list of ROAccessItem.
     */
    public static List<ROAccessItem> parse(
            RO_ACCESS_REPORT report, 
            String adapterName, 
            String readerName,
            long currentTime) {
        
        List<ROAccessItem> items = new LinkedList<ROAccessItem> ();
        
        List<TagReportData> tagDataList = report.getTagReportDataList();
        
        for (TagReportData tagData : tagDataList) {
            
            ROAccessItem item = new ROAccessItem();
            
            // log time.
            item.setLogTime(new Timestamp(currentTime));
            
            // adapter name.
            item.setAdapterName(adapterName);
            
            // reader name.
            item.setReaderName(readerName);
            
            // store the EPC as EPC96 or EPCData
            EPCParameter epcParameter = tagData.getEPCParameter();
            String epc = null;
            if (epcParameter instanceof EPC_96) {
                EPC_96 epc96 = (EPC_96) epcParameter;
                Integer96_HEX hex = epc96.getEPC();
                String hx = hex.toString();
                epc = hx;
            } else if (epcParameter instanceof EPCData){
                EPCData epcData = (EPCData) epcParameter;
                BitArray_HEX hex = epcData.getEPC();
                String hx = hex.toString();
                epc = hx;
            } else {
                log.error("Unknown EPCParameter encountered - ignoring.");
            }
            item.setEpc(epc);
            
            // RO Spec ID.
            ROSpecID roSpecID = tagData.getROSpecID();
            if ((null != roSpecID) && (null != roSpecID.getROSpecID())) {
                item.setRoSpecID(roSpecID.getROSpecID().toLong());
            }
            
            // spec index.
            SpecIndex specIndex = tagData.getSpecIndex();
            if ((null != specIndex) && (null != specIndex.getSpecIndex())) {
                item.setSpecIndex(specIndex.getSpecIndex().toInteger());
            }
            
            // inventory parameter spec ID.
            InventoryParameterSpecID inventoryPrmSpecID = 
                tagData.getInventoryParameterSpecID();
            if ((null != inventoryPrmSpecID) && 
                    (null != inventoryPrmSpecID.getInventoryParameterSpecID())) {
                item.setInventoryPrmSpecID( 
                        tagData.getInventoryParameterSpecID().
                        getInventoryParameterSpecID().toInteger());    
            }
            
            // antenna ID.
            AntennaID antennaID = tagData.getAntennaID();
            if ((null != antennaID) && (null != antennaID.getAntennaID())) {
                item.setAntennaID(antennaID.getAntennaID().toInteger());
            }
            
            // peak RSSI.
            PeakRSSI peakRSSI = tagData.getPeakRSSI();
            if ((null != peakRSSI) && (null != peakRSSI.getPeakRSSI())) {
                item.setPeakRSSI(new Short(peakRSSI.getPeakRSSI().toByte()));
            }
            
            // channel index.
            ChannelIndex channelIndex = tagData.getChannelIndex();
            if ((null != channelIndex) && (null != channelIndex.getChannelIndex())) {
                item.setChannelIndex(channelIndex.getChannelIndex().toInteger());
            }
            
            // extract the first seen UTC time stamp.
            FirstSeenTimestampUTC frstSnUTC = 
                tagData.getFirstSeenTimestampUTC();
            if ((null != frstSnUTC) && (null != frstSnUTC.getMicroseconds())) {
                item.setFirstSeenUTC( 
                        AbstractSQLROAccessReportsRepository.extractTimestamp(
                                frstSnUTC.getMicroseconds()));
            }
            
            // extract the first seen since uptime time stamp.
            FirstSeenTimestampUptime frstSnUptime = 
                tagData.getFirstSeenTimestampUptime();
            if ((null != frstSnUptime) && (null != frstSnUptime.getMicroseconds())) {
                item.setFirstSeenUptime( 
                        AbstractSQLROAccessReportsRepository.extractTimestamp(
                                frstSnUptime.getMicroseconds()));
            } 
            
            // extract the last seen time stamp UTC.
            LastSeenTimestampUTC lstSnUTC = 
                tagData.getLastSeenTimestampUTC();
            if ((null != lstSnUTC) && (null != lstSnUTC.getMicroseconds())) {
                item.setLastSeenUTC(
                        AbstractSQLROAccessReportsRepository.extractTimestamp(
                                lstSnUTC.getMicroseconds()));
            }
            
            // extract the last seen time stamp since uptime.
            LastSeenTimestampUptime lstSnUptime = 
                tagData.getLastSeenTimestampUptime();
            if ((null != lstSnUptime) && (null != lstSnUptime.getMicroseconds())) {
                item.setLastSeenUptime(
                        AbstractSQLROAccessReportsRepository.extractTimestamp(
                                lstSnUptime.getMicroseconds()));
            }
            
            // extract the tag count.
            TagSeenCount tagSeenCount = tagData.getTagSeenCount();
            if ((null != tagSeenCount) && (null != tagSeenCount.getTagCount())) {
                item.setTagSeenCount(tagSeenCount.getTagCount().toInteger());
            }
            
            List<AirProtocolTagData> airProtoTagData = 
                tagData.getAirProtocolTagDataList();
            
            for (AirProtocolTagData aptd : airProtoTagData) {
                if (aptd instanceof C1G2_CRC) {
                    C1G2_CRC c1g2CRC = (C1G2_CRC) aptd;
                    if ((null != c1g2CRC) && (null != c1g2CRC.getCRC())) {
                        item.setC1g2_CRC(c1g2CRC.getCRC().toInteger());
                    }
                } else if (aptd instanceof C1G2_PC) {
                    C1G2_PC c1g2PC = (C1G2_PC) aptd;
                    if ((null != c1g2PC) && (null != c1g2PC.getPC_Bits())) {
                        item.setC1g2_PC(c1g2PC.getPC_Bits().toInteger());
                    }
                } else {
                    log.error("Unknown AirProtocolTagData item encountered.");
                }                        
            }
            
            // extract the access spec ID.
            AccessSpecID accessSpecID = tagData.getAccessSpecID();
            if ((null != accessSpecID) && 
                    (null != accessSpecID.getAccessSpecID())) {
                item.setAccessSpecID(accessSpecID.getAccessSpecID().toLong());
            }
            
            items.add(item);
        }
        
        return items;
    }

    /**
     * @param logTime the logTime to set
     */
    public void setLogTime(Timestamp logTime) {
        this.logTime = logTime;
    }

    /**
     * @return the logTime
     */
    public Timestamp getLogTime() {
        return logTime;
    }

    /**
     * @param adapterName the adapterName to set
     */
    public void setAdapterName(String adapterName) {
        this.adapterName = adapterName;
    }

    /**
     * @return the adapterName
     */
    public String getAdapterName() {
        return adapterName;
    }

    /**
     * @param readerName the readerName to set
     */
    public void setReaderName(String readerName) {
        this.readerName = readerName;
    }

    /**
     * @return the readerName
     */
    public String getReaderName() {
        return readerName;
    }

    /**
     * @param epc the epc to set
     */
    public void setEpc(String epc) {
        this.epc = epc;
    }

    /**
     * @return the epc
     */
    public String getEpc() {
        return epc;
    }

    /**
     * @param roSpecID the roSpecID to set
     */
    public void setRoSpecID(Long roSpecID) {
        this.roSpecID = roSpecID;
    }

    /**
     * @return the roSpecID
     */
    public Long getRoSpecID() {
        return roSpecID;
    }

    /**
     * @param specIndex the specIndex to set
     */
    public void setSpecIndex(Integer specIndex) {
        this.specIndex = specIndex;
    }

    /**
     * @return the specIndex
     */
    public Integer getSpecIndex() {
        return specIndex;
    }

    /**
     * @param inventoryPrmSpecID the inventoryPrmSpecID to set
     */
    public void setInventoryPrmSpecID(Integer inventoryPrmSpecID) {
        this.inventoryPrmSpecID = inventoryPrmSpecID;
    }

    /**
     * @return the inventoryPrmSpecID
     */
    public Integer getInventoryPrmSpecID() {
        return inventoryPrmSpecID;
    }

    /**
     * @param antennaID the antennaID to set
     */
    public void setAntennaID(Integer antennaID) {
        this.antennaID = antennaID;
    }

    /**
     * @return the antennaID
     */
    public Integer getAntennaID() {
        return antennaID;
    }

    /**
     * @param peakRSSI the peakRSSI to set
     */
    public void setPeakRSSI(Short peakRSSI) {
        this.peakRSSI = peakRSSI;
    }

    /**
     * @return the peakRSSI
     */
    public Short getPeakRSSI() {
        return peakRSSI;
    }

    /**
     * @return the channelIndex
     */
    public Integer getChannelIndex() {
        return channelIndex;
    }

    /**
     * @param channelIndex the channelIndex to set
     */
    public void setChannelIndex(Integer channelIndex) {
        this.channelIndex = channelIndex;
    }

    /**
     * @return the firstSeenUTC
     */
    public Timestamp getFirstSeenUTC() {
        return firstSeenUTC;
    }

    /**
     * @param firstSeenUTC the firstSeenUTC to set
     */
    public void setFirstSeenUTC(Timestamp firstSeenUTC) {
        this.firstSeenUTC = firstSeenUTC;
    }

    /**
     * @return the firstSeenUptime
     */
    public Timestamp getFirstSeenUptime() {
        return firstSeenUptime;
    }

    /**
     * @param firstSeenUptime the firstSeenUptime to set
     */
    public void setFirstSeenUptime(Timestamp firstSeenUptime) {
        this.firstSeenUptime = firstSeenUptime;
    }

    /**
     * @return the lastSeenUTC
     */
    public Timestamp getLastSeenUTC() {
        return lastSeenUTC;
    }

    /**
     * @param lastSeenUTC the lastSeenUTC to set
     */
    public void setLastSeenUTC(Timestamp lastSeenUTC) {
        this.lastSeenUTC = lastSeenUTC;
    }

    /**
     * @return the lastSeenUptime
     */
    public Timestamp getLastSeenUptime() {
        return lastSeenUptime;
    }

    /**
     * @param lastSeenUptime the lastSeenUptime to set
     */
    public void setLastSeenUptime(Timestamp lastSeenUptime) {
        this.lastSeenUptime = lastSeenUptime;
    }

    /**
     * @return the tagSeenCount
     */
    public Integer getTagSeenCount() {
        return tagSeenCount;
    }

    /**
     * @param tagSeenCount the tagSeenCount to set
     */
    public void setTagSeenCount(Integer tagSeenCount) {
        this.tagSeenCount = tagSeenCount;
    }

    /**
     * @return the c1g2_CRC
     */
    public Integer getC1g2_CRC() {
        return c1g2_CRC;
    }

    /**
     * @param c1g2CRC the c1g2_CRC to set
     */
    public void setC1g2_CRC(Integer c1g2CRC) {
        c1g2_CRC = c1g2CRC;
    }

    /**
     * @return the c1g2_PC
     */
    public Integer getC1g2_PC() {
        return c1g2_PC;
    }

    /**
     * @param c1g2PC the c1g2_PC to set
     */
    public void setC1g2_PC(Integer c1g2PC) {
        c1g2_PC = c1g2PC;
    }

    /**
     * @return the accessSpecID
     */
    public Long getAccessSpecID() {
        return accessSpecID;
    }

    /**
     * @param accessSpecID the accessSpecID to set
     */
    public void setAccessSpecID(Long accessSpecID) {
        this.accessSpecID = accessSpecID;
    }
    
    /**
     * returns the item requested by the column index used in 
     * {@link AbstractSQLROAccessReportsRepository}. We recommend to use 
     * those constants.
     * @param index the index to use.
     * @return the object requested.
     */
    public Object get(int index) {
        switch (index) {
        case AbstractSQLROAccessReportsRepository.CINDEX_LOGTIME:
            return getLogTime();
        case AbstractSQLROAccessReportsRepository.CINDEX_ADAPTER:
            return getAdapterName();
        case AbstractSQLROAccessReportsRepository.CINDEX_READER:
            return getReaderName();
        case AbstractSQLROAccessReportsRepository.CINDEX_EPC:
            return getEpc();
        case AbstractSQLROAccessReportsRepository.CINDEX_RO_SPEC_ID:
            return getRoSpecID();
        case AbstractSQLROAccessReportsRepository.CINDEX_SPEC_INDEX:
            return getSpecIndex();
        case AbstractSQLROAccessReportsRepository.CINDEX_INVENTORY_PARAMETER_SPEC_ID:
            return getInventoryPrmSpecID();
        case AbstractSQLROAccessReportsRepository.CINDEX_ANTENNA_ID:
            return getAntennaID();
        case AbstractSQLROAccessReportsRepository.CINDEX_PEAK_RSSI:
            return getPeakRSSI();
        case AbstractSQLROAccessReportsRepository.CINDEX_CHANNEL_INDEX:
            return getChannelIndex();
        case AbstractSQLROAccessReportsRepository.CINDEX_FIRST_SEEN_TIMESTAMP_UTC:
            return getFirstSeenUTC();
        case AbstractSQLROAccessReportsRepository.CINDEX_FIRST_SEEN_TIMESTAMP_UP_TIME:
            return getFirstSeenUptime();
        case AbstractSQLROAccessReportsRepository.CINDEX_LAST_SEEN_TIMESTAMP_UTC:
            return getLastSeenUTC();
        case AbstractSQLROAccessReportsRepository.CINDEX_LAST_SEEN_TIMESTAMP_UP_TIME:
            return getLastSeenUptime();
        case AbstractSQLROAccessReportsRepository.CINDEX_TAG_SEEN_COUNT:
            return getTagSeenCount();
        case AbstractSQLROAccessReportsRepository.CINDEX_C1G2_CRC:
            return getC1g2_CRC();
        case AbstractSQLROAccessReportsRepository.CINDEX_C1G2_PC:
            return getC1g2_PC();
        case AbstractSQLROAccessReportsRepository.CINDEX_ACCESS_SPEC_ID:
            return getAccessSpecID();
        }
        return null;
    }
    
    /**
     * returns a string representation of the requested element. use the indices 
     * defined in {@link AbstractSQLROAccessReportsRepository}.
     * @param index the column index to use.
     * @return the string representation or null if exception.
     */
    public String getAsString(int index) {
        String ret;
        try {
            return get(index).toString();
        } catch (Exception e) {
            // null pointer if object is null
            ret = null;
        }
        return ret;
    }
    
    /**
     * @return generates a comma-separated-values representation of this item.
     */
    public String getAsCSV() {
        StringBuffer str = new StringBuffer();
        final int len = AbstractSQLROAccessReportsRepository.NUM_COLUMNS + 1;
        for (int i=1; i<len; i++) {
            String s = getAsString(i);
            if (null != s) {
                str.append(s);
            }
            str.append(",");
        }
        return str.toString();
    }
    
}
