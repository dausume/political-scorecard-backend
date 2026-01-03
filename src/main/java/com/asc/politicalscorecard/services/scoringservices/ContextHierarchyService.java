package com.asc.politicalscorecard.services.scoringservices;

import com.asc.politicalscorecard.controllers.responses.ApiResponse;
import com.asc.politicalscorecard.databases.daos.scoringdaos.ContextHierarchyDAO;
import com.asc.politicalscorecard.databases.daos.scoringdaos.TermContextDAO;
import com.asc.politicalscorecard.json.dtos.scoringdto.ContextHierarchyChainDTO;
import com.asc.politicalscorecard.json.dtos.scoringdto.ContextHierarchyDTO;
import com.asc.politicalscorecard.json.dtos.scoringdto.TermContextDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Service layer for Context Hierarchy operations.
 * Handles traversing and building hierarchy chains.
 */
@Service
public class ContextHierarchyService {

    private static final Logger logger = Logger.getLogger(ContextHierarchyService.class.getName());
    private final ContextHierarchyDAO hierarchyDAO;
    private final TermContextDAO contextDAO;

    @Autowired
    public ContextHierarchyService(ContextHierarchyDAO hierarchyDAO, TermContextDAO contextDAO) {
        this.hierarchyDAO = hierarchyDAO;
        this.contextDAO = contextDAO;
    }

    /**
     * Get the complete hierarchy chain for a given context
     * Includes both ancestors (less specific) and descendants (more specific)
     */
    public ApiResponse<ContextHierarchyChainDTO> getHierarchyChain(String contextId) {
        try {
            // Get the main context
            ApiResponse<TermContextDTO> contextResponse = contextDAO.read(contextId);
            if (!contextResponse.isSuccess()) {
                return new ApiResponse<>(false, "Context not found: " + contextId, null);
            }

            TermContextDTO mainContext = contextResponse.getData();
            ContextHierarchyChainDTO chain = new ContextHierarchyChainDTO(
                    contextId,
                    mainContext.getType(),
                    mainContext.getLabel()
            );

            // Build ancestor chain (traversing upward to less specific contexts)
            List<ContextHierarchyChainDTO.ContextNode> ancestors = buildAncestorChain(contextId, new HashMap<>(), 0);
            chain.setAncestorChain(ancestors);

            // Build descendant chain (traversing downward to more specific contexts)
            List<ContextHierarchyChainDTO.ContextNode> descendants = buildDescendantChain(contextId, new HashMap<>(), 0);
            chain.setDescendantChain(descendants);

            // Calculate total depth
            chain.setTotalDepth(ancestors.size() + descendants.size() + 1);

            return new ApiResponse<>(true, "Hierarchy chain retrieved successfully", chain);

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error building hierarchy chain: ", e);
            return new ApiResponse<>(false, "Error building hierarchy chain: " + e.getMessage(), null);
        }
    }

    /**
     * Recursively build the ancestor chain (less specific contexts)
     * Returns list ordered from least specific to most specific
     */
    private List<ContextHierarchyChainDTO.ContextNode> buildAncestorChain(
            String contextId,
            Map<String, Boolean> visited,
            int currentLevel) {

        List<ContextHierarchyChainDTO.ContextNode> chain = new ArrayList<>();

        // Prevent infinite loops
        if (visited.containsKey(contextId)) {
            return chain;
        }
        visited.put(contextId, true);

        // Get all parents of this context
        ApiResponse<List<ContextHierarchyDTO>> parentsResponse = hierarchyDAO.getParents(contextId);
        if (!parentsResponse.isSuccess() || parentsResponse.getData().isEmpty()) {
            return chain;
        }

        // Process each parent
        for (ContextHierarchyDTO hierarchy : parentsResponse.getData()) {
            String parentId = hierarchy.getParentContextId();

            // Recursively get the parent's ancestors first
            List<ContextHierarchyChainDTO.ContextNode> parentAncestors =
                    buildAncestorChain(parentId, visited, currentLevel + 1);
            chain.addAll(parentAncestors);

            // Add the parent itself
            ApiResponse<TermContextDTO> parentResponse = contextDAO.read(parentId);
            if (parentResponse.isSuccess()) {
                TermContextDTO parentContext = parentResponse.getData();
                ContextHierarchyChainDTO.ContextNode node = new ContextHierarchyChainDTO.ContextNode(
                        parentId,
                        parentContext.getLabel(),
                        parentContext.getType(),
                        currentLevel,
                        parentContext.getValue()
                );
                chain.add(node);
            }
        }

        return chain;
    }

    /**
     * Recursively build the descendant chain (more specific contexts)
     * Returns list ordered from immediate children to most specific descendants
     */
    private List<ContextHierarchyChainDTO.ContextNode> buildDescendantChain(
            String contextId,
            Map<String, Boolean> visited,
            int currentLevel) {

        List<ContextHierarchyChainDTO.ContextNode> chain = new ArrayList<>();

        // Prevent infinite loops
        if (visited.containsKey(contextId)) {
            return chain;
        }
        visited.put(contextId, true);

        // Get all children of this context
        ApiResponse<List<ContextHierarchyDTO>> childrenResponse = hierarchyDAO.getChildren(contextId);
        if (!childrenResponse.isSuccess() || childrenResponse.getData().isEmpty()) {
            return chain;
        }

        // Process each child
        for (ContextHierarchyDTO hierarchy : childrenResponse.getData()) {
            String childId = hierarchy.getChildContextId();

            // Add the child itself
            ApiResponse<TermContextDTO> childResponse = contextDAO.read(childId);
            if (childResponse.isSuccess()) {
                TermContextDTO childContext = childResponse.getData();
                ContextHierarchyChainDTO.ContextNode node = new ContextHierarchyChainDTO.ContextNode(
                        childId,
                        childContext.getLabel(),
                        childContext.getType(),
                        currentLevel + 1,
                        childContext.getValue()
                );
                chain.add(node);

                // Recursively get the child's descendants
                List<ContextHierarchyChainDTO.ContextNode> childDescendants =
                        buildDescendantChain(childId, visited, currentLevel + 1);
                chain.addAll(childDescendants);
            }
        }

        return chain;
    }

    /**
     * Create a hierarchy relationship
     */
    public ApiResponse<ContextHierarchyDTO> createHierarchy(ContextHierarchyDTO dto) {
        return hierarchyDAO.create(dto);
    }

    /**
     * Get all hierarchies
     */
    public ApiResponse<List<ContextHierarchyDTO>> getAllHierarchies() {
        return hierarchyDAO.readAll();
    }

    /**
     * Get parents of a context
     */
    public ApiResponse<List<ContextHierarchyDTO>> getParents(String contextId) {
        return hierarchyDAO.getParents(contextId);
    }

    /**
     * Get children of a context
     */
    public ApiResponse<List<ContextHierarchyDTO>> getChildren(String contextId) {
        return hierarchyDAO.getChildren(contextId);
    }

    /**
     * Delete a hierarchy relationship
     */
    public ApiResponse<ContextHierarchyDTO> deleteHierarchy(String id) {
        return hierarchyDAO.delete(id);
    }
}
