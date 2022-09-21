package com.rest.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *     "geo": {
 *       "lat": "-37.3159",
 *       "lng": "81.1496"
 *     }
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Geo {
    private String lat;
    private String lng;
}
