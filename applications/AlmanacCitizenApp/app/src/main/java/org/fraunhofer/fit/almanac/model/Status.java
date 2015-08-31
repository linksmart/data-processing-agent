package org.fraunhofer.fit.almanac.model;

/**
 * Created by devasya on 19.08.2015.
 */
public enum Status {

    // Initial status
    NEW,
    // Ticket validation:
    // a) Ticket approved, waiting to become ACTIVE
    ACCEPTED,
    // b) Ticket rejected without attempting to process
    // Reasons: DUPLICATE, INCOMPLETE, NOT APPLICABLE, NOT REPRODUCIBLE
    REJECTED,
    // Task processing: ACTIVE <-> INACTIVE
    // a) Ticket task being actively processed
    ACTIVE,
    // b) Ticket processing task temporarily paused
    INACTIVE,
    // Task resolution
    // a) Ticket has been successful processed
    DONE,
    // b) The ticket processing failed
    FAILED,
    //Deleted ticket
    DELETED;
    public static Status fromInteger(int x) {
        switch(x) {
            case 0:
                return NEW;
            case 1:
                return ACCEPTED;
            case 2:
                return REJECTED;
            case 3:
                return ACTIVE;
            case 4:
                return INACTIVE;
            case 5:
                return DONE;
            case 6:
                return FAILED;
            case 7:
                return DELETED;
        }
        return null;
    }

    public  static String toReadableString(Status status)
    {
        switch(status) {
            case NEW:
                return "newly created";
            case ACCEPTED:
                return "Approved by authority";
            case REJECTED:
                return "Rejected without attempting to process";
            case ACTIVE:
                return "Action being taken";
            case INACTIVE:
                return "Temporarily paused";
            case DONE:
                return "Action taken";
            case FAILED:
                return "Processing failed";
        }
        return null;
    }

}
