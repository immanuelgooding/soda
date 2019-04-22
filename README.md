# soda
Soda machine simulation


How to run:
Via Browser
http://localhost:8080/drink/select

Some helpful bits:
http://localhost:8080/
http://localhost:8080/h2-console

Via PostMAN
POST to localhost:8080/drink/purchase
Request Body Samples:
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

Add a charge card...
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
