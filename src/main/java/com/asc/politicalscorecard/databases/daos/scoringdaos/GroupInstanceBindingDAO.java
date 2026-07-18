package com.asc.politicalscorecard.databases.daos.scoringdaos;

import com.asc.politicalscorecard.controllers.responses.ApiResponse;
import com.asc.politicalscorecard.json.dtos.scoringdto.GroupInstanceBindingDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Repository
public class GroupInstanceBindingDAO {

    private static final Logger logger = Logger.getLogger(GroupInstanceBindingDAO.class.getName());
    private static final String SELECT = "SELECT id, group_name AS groupName, group_type AS groupType, " +
            "instance_name AS instanceName, polari_binding_name AS polariBindingName, status, " +
            "created_by_sub AS createdBySub, created_by_username AS createdByUsername, " +
            "created_at AS createdAt, confirmed_at AS confirmedAt FROM group_instance_binding";
    private final JdbcClient jdbcClient;

    @Autowired
    public GroupInstanceBindingDAO(@Qualifier("scoringJdbcClient") JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public ApiResponse<GroupInstanceBindingDTO> create(GroupInstanceBindingDTO dto) {
        if (dto.getId() == null || dto.getId().isEmpty()) {
            dto.setId();
        }
        if (dto.getStatus() == null || dto.getStatus().isEmpty()) {
            dto.setStatus("active");
        }
        String query = "INSERT INTO group_instance_binding (id, group_name, group_type, instance_name, " +
                "polari_binding_name, status, created_by_sub, created_by_username, confirmed_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try {
            int rowsAffected = jdbcClient.sql(query)
                    .params(List.of(
                        dto.getId(), dto.getGroupName(),
                        dto.getGroupType() != null ? dto.getGroupType() : "",
                        dto.getInstanceName(), dto.getPolariBindingName(), dto.getStatus(),
                        dto.getCreatedBySub() != null ? dto.getCreatedBySub() : "",
                        dto.getCreatedByUsername() != null ? dto.getCreatedByUsername() : "",
                        dto.getConfirmedAt() != null ? dto.getConfirmedAt() : ""))
                    .update();
            if (rowsAffected > 0) {
                return new ApiResponse<>(true, "Binding created successfully", dto);
            } else {
                return new ApiResponse<>(false, "Failed to create binding.", dto);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error creating binding: ", e);
            return new ApiResponse<>(false, "Error creating binding: " + e.getMessage(), dto);
        }
    }

    public ApiResponse<List<GroupInstanceBindingDTO>> readAll() {
        try {
            List<GroupInstanceBindingDTO> dtoList = jdbcClient.sql(SELECT + " ORDER BY created_at DESC")
                    .query(GroupInstanceBindingDTO.class).list();
            return new ApiResponse<>(true, "All bindings retrieved successfully", dtoList);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error reading bindings: ", e);
            return new ApiResponse<>(false, "Error reading bindings: " + e.getMessage(), null);
        }
    }

    public ApiResponse<GroupInstanceBindingDTO> readByGroupAndInstance(String groupName, String instanceName) {
        try {
            List<GroupInstanceBindingDTO> dtoList = jdbcClient.sql(SELECT +
                    " WHERE group_name = ? AND instance_name = ?")
                    .params(List.of(groupName, instanceName))
                    .query(GroupInstanceBindingDTO.class).list();
            if (dtoList.size() == 1) {
                return new ApiResponse<>(true, "Binding found", dtoList.get(0));
            }
            return new ApiResponse<>(false, "No binding for group '" + groupName +
                    "' and instance '" + instanceName + "'", null);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error reading binding: ", e);
            return new ApiResponse<>(false, "Error reading binding: " + e.getMessage(), null);
        }
    }

    public ApiResponse<GroupInstanceBindingDTO> updateStatus(String id, String status) {
        String query = "UPDATE group_instance_binding SET status = ? WHERE id = ?";
        try {
            int rowsAffected = jdbcClient.sql(query).params(List.of(status, id)).update();
            if (rowsAffected > 0) {
                return new ApiResponse<>(true, "Binding status updated", null);
            }
            return new ApiResponse<>(false, "Failed to update binding status.", null);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error updating binding status: ", e);
            return new ApiResponse<>(false, "Error updating binding status: " + e.getMessage(), null);
        }
    }
}
