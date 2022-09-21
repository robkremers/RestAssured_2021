package com.rest.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 *   "address": {
 *     "street": "Kulas Light",
 *     "suite": "Apt. 556",
 *     "city": "Gwenborough",
 *     "zipcode": "92998-3874",
 *     "geo": {
 *       "lat": "-37.3159",
 *       "lng": "81.1496"
 *     }
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Address {
    private String street;
    private String suite;
    private String city;
    private String zipcode;
    private Geo geo;
}
