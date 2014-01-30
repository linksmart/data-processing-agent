package eu.ebbits.pwal.impl.driver.llrpreader.adaptor;

import java.util.List;

import org.llrp.ltk.types.LLRPMessage;
import org.llrp.ltk.generated.interfaces.AccessCommandOpSpecResult;
import org.llrp.ltk.generated.messages.RO_ACCESS_REPORT;

import org.llrp.ltk.generated.parameters.TagReportData;

import eu.ebbits.pwal.impl.driver.llrpreader.org.fosstrak.llrp.client.MessageHandler;

/**
 * Handler for a report message
 * <p> 
 * Copyright (c) 2010-2013 the ebbits project. All Rights Reserved.
 *
 * @author    ISMB
 * @version    %I%, %G%
 * @since   PWAL 0.2.0
 */
public class ReportHandler implements MessageHandler {

    private AdaptorManagerThread thread;
    
    /**
     * Constructor of the handler for report messages
     * 
     * @param thread - the <code>AdaptorManagerThread</code> that receives the messages
     */
    public ReportHandler(AdaptorManagerThread thread) {
        this.thread = thread;
    }
    
    @Override
    public void handle(String adaptorName, String readerName,
            LLRPMessage msg) {
        if (msg.getTypeNum() == RO_ACCESS_REPORT.TYPENUM) {
            // The message received is an Access Report.
            RO_ACCESS_REPORT report = (RO_ACCESS_REPORT) msg;
            // Get a list of the tags read.
            List <TagReportData> tags = report.getTagReportDataList();
            // Loop through the list and get the EPC of each tag.
            for (TagReportData tag : tags)
            {
                List <AccessCommandOpSpecResult> ops =
                        tag.getAccessCommandOpSpecResultList();
                // See if any operations were performed on
                // this tag (read, write, kill).
                // If so, print out the details.
                for (AccessCommandOpSpecResult op : ops) {
                    if(op.getClass().getName().equals("org.llrp.ltk.generated.parameters.C1G2ReadOpSpecResult")) {
                        thread.setReadData(((org.llrp.ltk.generated.parameters.C1G2ReadOpSpecResult) op).getReadData());
                    }
                }
            }
        }

        thread.sendEvent(msg);
    }
}