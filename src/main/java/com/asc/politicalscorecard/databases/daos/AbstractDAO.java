package com.asc.politicalscorecard.databases.daos;

import com.asc.politicalscorecard.controllers.responses.ApiResponse;
import java.util.List;

public abstract class AbstractDAO<T> {

    // Create operation
    public abstract ApiResponse<T> create(T dto);

    // Read operation
    public abstract T read(String id);

    // Read all operation
    public abstract List<T> readAll();

    // Update operation
    public abstract boolean update(T dto);

    // Delete operation
    public abstract boolean delete(String id);
}
