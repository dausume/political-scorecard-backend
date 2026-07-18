package com.asc.politicalscorecard.json.dtos.scoringdto;

import java.util.UUID;

/**
 * The PSC side of a group ↔ Polari-instance binding ("this instance
 * is the authoritative source for this group"). The Polari side is a
 * GroupInstanceBinding row over there; a binding is only 'active'
 * once BOTH sides exist — PSC creates its row and calls Polari's
 * confirm-remote in the same act, so an active PSC row implies the
 * Polari row is active too.
 */
public class GroupInstanceBindingDTO {

    private String id;
    private String groupName;
    private String groupType;
    private String instanceName;
    // The Polari-side row name ('binding--<group>--<instance>').
    private String polariBindingName;
    private String status; // 'active' | 'revoked'
    private String createdBySub;
    private String createdByUsername;
    private String createdAt;
    private String confirmedAt;

    public GroupInstanceBindingDTO() {
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

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupType() {
        return groupType;
    }

    public void setGroupType(String groupType) {
        this.groupType = groupType;
    }

    public String getInstanceName() {
        return instanceName;
    }

    public void setInstanceName(String instanceName) {
        this.instanceName = instanceName;
    }

    public String getPolariBindingName() {
        return polariBindingName;
    }

    public void setPolariBindingName(String polariBindingName) {
        this.polariBindingName = polariBindingName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreatedBySub() {
        return createdBySub;
    }

    public void setCreatedBySub(String createdBySub) {
        this.createdBySub = createdBySub;
    }

    public String getCreatedByUsername() {
        return createdByUsername;
    }

    public void setCreatedByUsername(String createdByUsername) {
        this.createdByUsername = createdByUsername;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getConfirmedAt() {
        return confirmedAt;
    }

    public void setConfirmedAt(String confirmedAt) {
        this.confirmedAt = confirmedAt;
    }
}
