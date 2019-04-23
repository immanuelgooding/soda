Soda is a simple demo project that simulates the interaction with a vending (soda pop/beverage) machine.

The project utilizes [Spring Boot](https://spring.io/projects/spring-boot). 

Import it into your favorite IDE and use the following guide to get started.

# Getting Started Guide


## How To Run
1. **_User Interface (web browser)_**

```
http://localhost:8080/drink/buy
```
  - ###### Some helpful bits:
    - ###### REST Data Repositories
    ```
    http://localhost:8080/
    ```
  
    - ###### Data Store Interface
    ```
    http://localhost:8080/h2-console
    ```

2. **_REST API (Postman)_**
  - ###### HTTP Post
    ```
    localhost:8080/drink/purchase
    ```
    **_Request body samples_**
    ```
    {
      "brand": "COKE_ZERO",
      "containerType": "BOTTLE",
      "cash": [
        "QUARTER",
        "QUARTER",
        "QUARTER",
        "QUARTER",
        "QUARTER"
      ]
    }

    {
      "brand": "COKE_ZERO",
      "containerType": "BOTTLE",
      "cash": [
        "QUARTER",
        "QUARTER",
        "QUARTER",
        "QUARTER"
      ]
    }

    {
      "brand": "COKE_ZERO",
      "containerType": "BOX",
      "cash": [
        "QUARTER",
        "QUARTER",
        "QUARTER",
        "QUARTER"
      ]
    }
    ```
    - ###### POST multiple forms of payment (e.g. add a charge card)
      ```
      {
        "brand": "COKE_ZERO",
        "containerType": "CAN",
        "cash": [
          "QUARTER",
          "QUARTER",
          "QUARTER",
          "QUARTER"
        ],
        "chargeCard": {
          "name": "Thirsty Person",
          "cardNumber": 1111222233334444,
          "expiryMonth": "03",
          "expiryYear": 2030,
          "provider": "VISA"
        }
      }

      {
        "brand": "COKE_ZERO",
        "containerType": "CAN",
        "cash": [
          "QUARTER",
          "QUARTER",
          "QUARTER",
          "QUARTER"
        ],
        "chargeCard": {
          "name": "Thirsty Person",
          "cardNumber": 1111222233334444,
          "expiryMonth": "03",
          "expiryYear": 2030,
          "provider": "DINERS_CLUB_PLATINUM"
        }
      }
      ```
