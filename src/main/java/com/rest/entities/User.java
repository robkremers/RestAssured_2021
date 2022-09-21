package com.rest.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * {
 *   "name": "Leanne Graham",
 *   "username": "Bret",
 *   "email": "Sincere@april.biz",
 *   "address": {
 *     "street": "Kulas Light",
 *     "suite": "Apt. 556",
 *     "city": "Gwenborough",
 *     "zipcode": "92998-3874",
 *     "geo": {
 *       "lat": "-37.3159",
 *       "lng": "81.1496"
 *     }
 *   }
 * }
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private String id;
    private String name;
    private String username;
    private String email;
    private Address address;

    public User( String name, String username, String email, Address address) {
        this.name = name;
        this.username = username;
        this.email = email;
        this.address = address;
    }
}
