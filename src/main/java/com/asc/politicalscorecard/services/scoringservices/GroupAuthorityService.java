package com.asc.politicalscorecard.services.scoringservices;

import com.asc.politicalscorecard.controllers.responses.ApiResponse;
import com.asc.politicalscorecard.databases.daos.scoringdaos.GroupInstanceBindingDAO;
import com.asc.politicalscorecard.databases.daos.scoringdaos.PolariInstanceDAO;
import com.asc.politicalscorecard.databases.daos.scoringdaos.TermDAO;
import com.asc.politicalscorecard.databases.daos.scoringdaos.TermProvenanceDAO;
import com.asc.politicalscorecard.json.dtos.scoringdto.GroupInstanceBindingDTO;
import com.asc.politicalscorecard.json.dtos.scoringdto.PolariInstanceDTO;
import com.asc.politicalscorecard.json.dtos.scoringdto.TermDTO;
import com.asc.politicalscorecard.json.dtos.scoringdto.TermProvenanceDTO;
import com.asc.politicalscorecard.objects.auth.UserInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * PSC-side orchestration of the group↔instance authority flow.
 *
 * Binding: the PSC row is created ONLY when Polari's confirm-remote
 * succeeds — so an existing PSC binding row implies the Polari side
 * is active too ("defined on both sides" with no dangling halves).
 *
 * Term-availability: the signal is forwarded with the citizen's own
 * bearer; Polari runs the three-check authority verdict. Only an
 * ADMITTED verdict makes the term real on the PSC (term row ensured,
 * provenance recorded with the full verdict as evidence). Refusals
 * are returned verbatim — the evidence names the failing check.
 */
@Service
public class GroupAuthorityService {

    private static final Logger logger = Logger.getLogger(GroupAuthorityService.class.getName());

    private final PolariInstanceDAO polariInstanceDAO;
    private final GroupInstanceBindingDAO bindingDAO;
    private final TermProvenanceDAO termProvenanceDAO;
    private final TermDAO termDAO;
    private final PolariAuthorityService polariAuthorityService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public GroupAuthorityService(PolariInstanceDAO polariInstanceDAO,
                                 GroupInstanceBindingDAO bindingDAO,
                                 TermProvenanceDAO termProvenanceDAO,
                                 TermDAO termDAO,
                                 PolariAuthorityService polariAuthorityService) {
        this.polariInstanceDAO = polariInstanceDAO;
        this.bindingDAO = bindingDAO;
        this.termProvenanceDAO = termProvenanceDAO;
        this.termDAO = termDAO;
        this.polariAuthorityService = polariAuthorityService;
    }

    public ApiResponse<List<PolariInstanceDTO>> listInstances() {
        return polariInstanceDAO.readAll();
    }

    public ApiResponse<PolariInstanceDTO> registerInstance(PolariInstanceDTO dto) {
        return polariInstanceDAO.create(dto);
    }

    public ApiResponse<List<GroupInstanceBindingDTO>> listBindings() {
        return bindingDAO.readAll();
    }

    /**
     * Create the PSC side of a binding. Requires the Polari side to
     * already be 'confirmed-local' (proposed there by someone
     * authoritative on both the group and the instance); this call
     * remote-confirms it with the citizen's own token, and only on
     * success does the PSC row exist.
     */
    public Map<String, Object> createBinding(String groupName, String groupType, String instanceName,
                                             UserInfo userInfo, String bearerToken) {
        Map<String, Object> result = new HashMap<>();
        ApiResponse<PolariInstanceDTO> instance = polariInstanceDAO.readByName(instanceName);
        if (!instance.isSuccess()) {
            result.put("ok", false);
            result.put("error", "No registered Polari instance named '" + instanceName
                    + "' — register it under /api/authority/instances first");
            return result;
        }
        ApiResponse<GroupInstanceBindingDTO> existing =
                bindingDAO.readByGroupAndInstance(groupName, instanceName);
        if (existing.isSuccess() && "active".equals(existing.getData().getStatus())) {
            result.put("ok", false);
            result.put("error", "Binding for group '" + groupName + "' and instance '"
                    + instanceName + "' is already active");
            return result;
        }

        // Binding names use the instance's OWN identity (its
        // POLARI_INSTANCE_NAME), not the PSC registry label — the two
        // need not match.
        String selfName = polariAuthorityService.getSelfInstanceName(
                instance.getData().getBaseUrl(), bearerToken);
        String polariBindingName = "binding--" + groupName + "--"
                + (selfName.isEmpty() ? instanceName : selfName);
        Map<String, Object> polariResult = polariAuthorityService.confirmBindingRemote(
                instance.getData().getBaseUrl(), polariBindingName, bearerToken);
        if (!Boolean.TRUE.equals(polariResult.get("ok"))) {
            result.put("ok", false);
            result.put("error", "Polari refused the remote confirmation — the binding must be "
                    + "PROPOSED on the Polari side first, by someone holding authority on both "
                    + "the group and the instance");
            result.put("polariVerdict", polariResult);
            return result;
        }

        GroupInstanceBindingDTO dto = new GroupInstanceBindingDTO();
        dto.setGroupName(groupName);
        dto.setGroupType(groupType);
        dto.setInstanceName(instanceName);
        dto.setPolariBindingName(polariBindingName);
        dto.setStatus("active");
        dto.setCreatedBySub(userInfo.getId());
        dto.setCreatedByUsername(userInfo.getUsername());
        dto.setConfirmedAt(Instant.now().toString());
        ApiResponse<GroupInstanceBindingDTO> created = bindingDAO.create(dto);
        if (!created.isSuccess()) {
            result.put("ok", false);
            result.put("error", "Polari side confirmed but PSC row failed: " + created.getMessage());
            return result;
        }
        result.put("ok", true);
        result.put("binding", created.getData());
        result.put("polariBinding", polariResult.get("binding"));
        return result;
    }

    /**
     * The signal: "we want to make this term available for this
     * context on the PSC, asserted by this group."
     */
    public Map<String, Object> submitTermAvailability(String termName, String contextName,
                                                      String groupName, String instanceName,
                                                      String conceptName, String description,
                                                      String category, UserInfo userInfo,
                                                      String bearerToken) {
        Map<String, Object> result = new HashMap<>();
        ApiResponse<PolariInstanceDTO> instance = polariInstanceDAO.readByName(instanceName);
        if (!instance.isSuccess()) {
            result.put("ok", false);
            result.put("error", "No registered Polari instance named '" + instanceName + "'");
            return result;
        }
        ApiResponse<GroupInstanceBindingDTO> binding =
                bindingDAO.readByGroupAndInstance(groupName, instanceName);
        if (!binding.isSuccess() || !"active".equals(binding.getData().getStatus())) {
            result.put("ok", false);
            result.put("error", "No active PSC-side binding for group '" + groupName
                    + "' and instance '" + instanceName + "' — the PSC side of the binding "
                    + "must exist before signals are accepted");
            return result;
        }

        // The Polari side keys grants/bindings by its OWN instance
        // name — recover it from the binding row rather than sending
        // the PSC registry label.
        String polariBindingName = binding.getData().getPolariBindingName();
        int suffix = polariBindingName != null ? polariBindingName.lastIndexOf("--") : -1;
        String polariInstanceName = suffix >= 0
                ? polariBindingName.substring(suffix + 2) : instanceName;

        Map<String, Object> signal = new HashMap<>();
        signal.put("term_name", termName);
        signal.put("context_name", contextName);
        signal.put("group_name", groupName);
        signal.put("instance_name", polariInstanceName);
        signal.put("concept_name", conceptName != null ? conceptName : "");
        // Identity travels as the bearer token — Polari re-verifies it;
        // these payload fields are only the unverified fallback.
        signal.put("requested_by_subject", userInfo.getId());
        signal.put("requested_by_username", userInfo.getUsername());
        Map<String, Object> polariResult = polariAuthorityService.submitTermSignal(
                instance.getData().getBaseUrl(), signal, bearerToken);

        result.put("polariVerdict", polariResult);
        boolean admitted = Boolean.TRUE.equals(polariResult.get("ok"))
                && Boolean.TRUE.equals(polariResult.get("admitted"));
        result.put("ok", Boolean.TRUE.equals(polariResult.get("ok")));
        result.put("admitted", admitted);
        if (!admitted) {
            return result;
        }

        TermDTO term = ensureTerm(termName, description, category, groupName, instanceName);
        TermProvenanceDTO provenance = new TermProvenanceDTO();
        provenance.setTermId(term != null ? term.getId() : "");
        provenance.setTermName(termName);
        provenance.setContextName(contextName);
        provenance.setGroupName(groupName);
        provenance.setInstanceName(instanceName);
        Object signalSummary = polariResult.get("signal");
        if (signalSummary instanceof Map) {
            Object name = ((Map<?, ?>) signalSummary).get("name");
            provenance.setSignalName(name != null ? name.toString() : "");
        }
        provenance.setRequestedBy(userInfo.getUsername());
        try {
            provenance.setVerdictJson(objectMapper.writeValueAsString(polariResult.get("verdict")));
        } catch (Exception e) {
            provenance.setVerdictJson("{}");
        }
        termProvenanceDAO.create(provenance);
        result.put("term", term);
        result.put("provenance", provenance);
        return result;
    }

    public ApiResponse<List<TermProvenanceDTO>> listProvenance(String termId) {
        if (termId != null && !termId.isEmpty()) {
            return termProvenanceDAO.readByTermId(termId);
        }
        return termProvenanceDAO.readAll();
    }

    /** Ensure the asserted term exists as a real PSC term row. */
    private TermDTO ensureTerm(String termName, String description, String category,
                               String groupName, String instanceName) {
        try {
            ApiResponse<TermDTO> existing = termDAO.readByName(termName);
            if (existing.isSuccess()) {
                return existing.getData();
            }
            TermDTO term = new TermDTO();
            term.setName(termName);
            term.setDescription(description != null && !description.isEmpty()
                    ? description
                    : "Asserted through an authority-checked term-availability signal.");
            term.setSource("group:" + groupName + " via polari:" + instanceName);
            term.setCategory(category != null && !category.isEmpty() ? category : "polari-asserted");
            ApiResponse<TermDTO> created = termDAO.create(term);
            return created.isSuccess() ? created.getData() : null;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error ensuring term '" + termName + "': ", e);
            return null;
        }
    }
}
