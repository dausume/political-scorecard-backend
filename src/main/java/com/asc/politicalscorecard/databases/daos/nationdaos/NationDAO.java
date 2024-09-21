package com.asc.politicalscorecard.databases.daos.nationdaos;

import com.asc.politicalscorecard.controllers.responses.ApiResponse;
import com.asc.politicalscorecard.databases.daos.AbstractDAO;
import com.asc.politicalscorecard.databases.daos.planetdaos.PlanetDAO;
import com.asc.politicalscorecard.exceptions.apiuserexceptions.InvalidInputException;
import com.asc.politicalscorecard.json.dtos.nationdto.NationDTO;
import com.asc.politicalscorecard.json.dtos.planetdto.PlanetDTO;
import com.asc.politicalscorecard.objects.location.Planet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

@Repository
public class NationDAO extends AbstractDAO<NationDTO> {

    private static final Logger logger = Logger.getLogger(NationDAO.class.getName());
    private final JdbcClient jdbcClient;

    @Autowired
    public NationDAO(@Qualifier("locationJdbcClient") JdbcClient jdbcClient, PlanetDAO planetDAO) {
        this.jdbcClient = jdbcClient;
    }

    @Override
    public ApiResponse<NationDTO> create(NationDTO dto) {
        String query = "INSERT INTO nation (id, nation_name, home_planet) VALUES (?, ?, ?)";
        System.out.println("In create Nation in NationDAO");
        System.out.println("dto.getId(): " + dto.getId());
        System.out.println("dto.getNationName(): " + dto.getNationName());
        System.out.println("dto.getHomePlanetId(): " + dto.getHomePlanetId());
        // Ensure that none of the required fields are null
        //Objects.requireNonNull(dto.getId(), "Nation ID must be not null"); // The nation uuid should be defined in the DTO.
        //Objects.requireNonNull(dto.getNationName(), "Nation Name must not be null");
        //Objects.requireNonNull(dto.getHomePlanetId(), "Home Planet ID must not be null");
        try {
            int rowsAffected = jdbcClient.sql(query)
                      .params(List.of(dto.getId(), dto.getNationName(), dto.getHomePlanetId()))
                      .update();
            if (rowsAffected > 0) {
                return new ApiResponse<>(true, "Nation created successfully", dto);
            } else {
                return new ApiResponse<>(false, "Failed to create nation in the NationDAO.", dto);
            }
        } catch (NullPointerException e) 
        {
            throw new InvalidInputException(e.getMessage());
        } catch (Exception e) 
        {
            logger.log(Level.SEVERE, "Error creating nation: ", e);
            return new ApiResponse<>(false, "Error creating nation: " + e.getMessage(), dto);
        }
    }

    @Override
    public NationDTO read(String id) {
        String query = "SELECT * FROM nation WHERE id = ?";
        try {
            List<NationDTO> dtoList = jdbcClient.sql(query)
                                                .params(List.of(id))
                                                .query(NationDTO.class)
                                                .list();
            if (dtoList.size() == 1) {
                return dtoList.get(0);
            } else {
                logger.log(Level.SEVERE, "Error: Query returned multiple Nations or none, should only be returning one.");
                return null;
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error reading nation: ", e);
            return null;
        }
    }

    @Override
    public List<NationDTO> readAll() {
        String query = "SELECT * FROM nation";
        try {
            return jdbcClient.sql(query)
                             .query(NationDTO.class)
                             .list();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error reading all nations: ", e);
            return null;
        }
    }

    @Override
    public boolean update(NationDTO dto) {
        String query = "UPDATE nation SET nation_name = ?, home_planet = ? WHERE id = ?";
        try {
            int rowsAffected = jdbcClient.sql(query)
                                        .params(List.of(dto.getNationName(), dto.getHomePlanetId(), dto.getId()))
                                        .update();
            return rowsAffected > 0;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error updating nation: ", e);
            return false;
        }
    }

    @Override
    public boolean delete(String id) {
        String query = "DELETE FROM nation WHERE id = ?";
        try {
            jdbcClient.sql(query)
                      .params(List.of(id))
                      .update();
            return true;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error deleting nation: ", e);
            return false;
        }
    }

    private static class NationRowMapper implements RowMapper<NationDTO> {
        private final PlanetDAO planetDAO;

        public NationRowMapper(PlanetDAO planetDAO) {
            this.planetDAO = planetDAO;
        }

        @Override
        public NationDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
            String id = rs.getString("id");
            String nationName = rs.getString("nation_name");
            String homePlanetId = rs.getString("home_planet");
            Planet homePlanet = null;

            if (homePlanetId != null) {
                PlanetDTO homePlanetDTO = planetDAO.read(homePlanetId);
                if (homePlanetDTO != null) {
                    homePlanet = homePlanetDTO.toEntity();
                } else {
                    logger.log(Level.WARNING, "No Planet found with ID {0}", homePlanetId);
                }
            }

            return new NationDTO(id, nationName, homePlanetId);
        }
    }
}
