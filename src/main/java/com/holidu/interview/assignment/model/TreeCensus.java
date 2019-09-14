package com.holidu.interview.assignment.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class TreeCensus {

    @JsonProperty("tree_id")
    private long treeId;

    @JsonProperty("spc_common")
    private String treeName;

    @JsonProperty("latitude")
    private double latitude;

    @JsonProperty("longitude")
    private double longitude;
}
