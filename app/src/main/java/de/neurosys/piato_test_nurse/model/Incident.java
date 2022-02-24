package de.neurosys.piato_test_nurse.model;

public class Incident {

    private long id;
    private long incidentId;
    private long patientCall;
    private long patientCallPortalReceived;
    private long patientCallNurseReceived;
    private long nurseReply;
    private long nurseReplyPortalReceived;
    private long nurseReplyPatientReceived;
    private int patientCallSent;
    private int patientCallPortalReceivedSent;
    private int patientCallNurseReceivedSent;
    private int nurseReplySent;
    private int nurseReplyPortalReceivedSent;
    private int nurseReplyPatientReceivedSent;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getIncidentId() {
        return incidentId;
    }

    public void setIncidentId(long incidentId) {
        this.incidentId = incidentId;
    }

    public long getPatientCall() { return patientCall; }

    public void setPatientCall(long patientCall) {
        this.patientCall = patientCall;
    }

    public long getPatientCallPortalReceived() { return patientCallPortalReceived; }

    public void setPatientCallPortalReceived(long patientCallPortalReceived) { this.patientCallPortalReceived = patientCallPortalReceived; }

    public long getPatientCallNurseReceived() { return patientCallNurseReceived; }

    public void setPatientCallNurseReceived(long patientCallNurseReceived) { this.patientCallNurseReceived = patientCallNurseReceived; }

    public long getNurseReply() { return nurseReply; }

    public void setNurseReply(long nurseReply) {
        this.nurseReply = nurseReply;
    }

    public long getNurseReplyPortalReceived() { return nurseReplyPortalReceived; }

    public void setNurseReplyPortalReceived(long nurseReplyPortalReceived) { this.nurseReplyPortalReceived = nurseReplyPortalReceived; }

    public long getNurseReplyPatientReceived() { return nurseReplyPatientReceived; }

    public void setNurseReplyPatientReceived(long nurseReplyPatientReceived) { this.nurseReplyPatientReceived = nurseReplyPatientReceived; }

    public int getPatientCallSent() { return patientCallSent; }

    public void setPatientCallSent(int patientCallSent) {
        this.patientCallSent = patientCallSent;
    }

    public int getPatientCallPortalReceivedSent() { return patientCallPortalReceivedSent; }

    public void setPatientCallPortalReceivedSent(int patientCallPortalReceivedSent) { this.patientCallPortalReceivedSent = patientCallPortalReceivedSent; }

    public int getPatientCallNurseReceivedSent() { return patientCallNurseReceivedSent; }

    public void setPatientCallNurseReceivedSent(int patientCallNurseReceivedSent) { this.patientCallNurseReceivedSent = patientCallNurseReceivedSent; }

    public int getNurseReplySent() { return nurseReplySent; }

    public void setNurseReplySent(int nurseReplySent) {
        this.nurseReplySent = nurseReplySent;
    }

    public int getNurseReplyPortalReceivedSent() { return nurseReplyPortalReceivedSent; }

    public void setNurseReplyPortalReceivedSent(int nurseReplyPortalReceivedSent) { this.nurseReplyPortalReceivedSent = nurseReplyPortalReceivedSent; }

    public int getNurseReplyPatientReceivedSent() { return nurseReplyPatientReceivedSent; }

    public void setNurseReplyPatientReceivedSent(int nurseReplyPatientReceivedSent) { this.nurseReplyPatientReceivedSent = nurseReplyPatientReceivedSent; }
}