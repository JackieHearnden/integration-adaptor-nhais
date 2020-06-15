var headers = {
    'TransactionType': 'approval',
    // an approval is always a reply to a previous outbound Acceptance and needs to include an OperationId matching the
    // OperationId from the Acceptance that solicited the reply
    // OperationId should be treated as case-insensitive
    'OperationId': '30C28CBD81D5F8071D104DB54F645465EC9033AB8A047A97853677F2BDB431DC'
}

var body = {
  "parameter": [
    {
      "name": "gpTradingPartnerCode",
      "valueString": "TES5"
    }
    {
      "name": "patient",
      "resource": {
        "managingOrganization": {
          "reference": "XX1"
        },
        "generalPractitioner": [
          {
            "reference": "2750922,295"
          }
        ],
        "identifier": [
          {
            "system": "https://fhir.nhs.uk/Id/nhs-number",
            "value": "RAT56"
          }
        ]
        "resourceType": "Patient"
      }
    }
  ],
  "resourceType": "Parameters"
}