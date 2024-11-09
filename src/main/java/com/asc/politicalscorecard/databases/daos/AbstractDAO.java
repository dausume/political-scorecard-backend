package com.asc.politicalscorecard.databases.daos;

import com.asc.politicalscorecard.controllers.responses.ApiResponse;
import java.util.List;

public abstract class AbstractDAO<T> {

    // Create operation
    public abstract ApiResponse<T> create(T dto);

    // Read operation
    public abstract ApiResponse<T> read(String id);

    // Read all operation
    public abstract ApiResponse<List<T>> readAll();

    // Update operation
    public abstract ApiResponse<T> update(T dto);

    // Delete operation
    public abstract ApiResponse<T> delete(String id);
}
