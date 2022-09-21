This package concerns the setup for the handling of:

{
  "collection": {
    "info": {
      "name": "Sample Collection",
      "description": "This is just a sample collection.",
      "schema": "https://schema.getpostman.com/json/collection/v2.1.0/collection.json"
    },
    "item": [
      {
        "name": "This is a folder",
        "item": [
          {
            "name": "Sample POST Request",
            "request": {
              "url": "https://postman-echo.com/post",
              "method": "POST",
              "header": [
                {
                  "key": "Content-Type",
                  "value": "application/json"
                }
              ],
              "body": {
                "mode": "raw",
                "raw": "{\"data\": \"123\"}"
              },
              "description": "This is a sample POST Request"
            }
          }
        ]
      },
      {
        "name": "Sample GET Request",
        "request": {
          "url": "https://postman-echo/get",
          "method": "GET",
          "description": "This is a sample GET Request"
        }
      }
    ]
  }
}