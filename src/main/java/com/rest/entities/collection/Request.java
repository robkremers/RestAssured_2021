package com.rest.entities.collection;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Request {
    private String url;
    private String method;
    private List<Header> header;
    private Body body;
    private String description;
}
