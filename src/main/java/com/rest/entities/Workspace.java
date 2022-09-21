package com.rest.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * This class should be able to represent:
 * {
 *     "workspace": {
 *         "name": "MyFifthWorkspace",
 *         "type": "personal",
 *         "description": "workspace for BDD Style testing the POST HTTP method"
 *     }
 * }
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
/**
 * Using 'allowSetters' mean that the variable 'id' will not be used during the serialization.
 * But it will be used during deserialization.
 */
@JsonIgnoreProperties(value = {"i", "id"}, allowSetters = true)
public class Workspace {
    // Example for showing a variable that should not be used for serialization at all.
//    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    @JsonIgnore
    private int i;
//    @JsonInclude(JsonInclude.Include.NON_NULL)/**
    /**
     * @JsonIgnore can not be used if a variable will be filled during deserialization.
     * In that case @JsonIgnoreProperties should be used. See above.
     */
    private String id;
    // Example of the use of a variable that should not be empty. If used it will include NON_NULL.
//    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonIgnore
    Map<String, String> myMap;
    private String name;
    private String type;
    private String description;

    public Workspace(String name, String type, String description) {
        this.name = name;
        this.type = type;
        this.description = description;
    }
}
