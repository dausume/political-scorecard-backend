package com.asc.politicalscorecard.json.dtos.scoringdto;

import java.util.UUID;

/**
 * Provenance of a term admitted through an authority-checked
 * term-availability signal: "asserted by <group> via <instance>",
 * with the full Polari verdict retained as evidence.
 */
public class TermProvenanceDTO {

    private String id;
    private String termId;
    private String termName;
    private String contextName;
    private String groupName;
    private String instanceName;
    // The Polari-side TermAvailabilitySignal row name.
    private String signalName;
    private String requestedBy;
    // The full authority_check verdict JSON at admission time.
    private String verdictJson;
    private String admittedAt;

    public TermProvenanceDTO() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setId() {
        this.id = UUID.randomUUID().toString();
    }

    public String getTermId() {
        return termId;
    }

    public void setTermId(String termId) {
        this.termId = termId;
    }

    public String getTermName() {
        return termName;
    }

    public void setTermName(String termName) {
        this.termName = termName;
    }

    public String getContextName() {
        return contextName;
    }

    public void setContextName(String contextName) {
        this.contextName = contextName;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getInstanceName() {
        return instanceName;
    }

    public void setInstanceName(String instanceName) {
        this.instanceName = instanceName;
    }

    public String getSignalName() {
        return signalName;
    }

    public void setSignalName(String signalName) {
        this.signalName = signalName;
    }

    public String getRequestedBy() {
        return requestedBy;
    }

    public void setRequestedBy(String requestedBy) {
        this.requestedBy = requestedBy;
    }

    public String getVerdictJson() {
        return verdictJson;
    }

    public void setVerdictJson(String verdictJson) {
        this.verdictJson = verdictJson;
    }

    public String getAdmittedAt() {
        return admittedAt;
    }

    public void setAdmittedAt(String admittedAt) {
        this.admittedAt = admittedAt;
    }
}
