{
  "parameter": [
    {
      "name": "gpTradingPartnerCode",
      "valueString": "TES5"
    },
    {
      "name": "acceptanceCode",
      "valueString": "A"
    },
    {
      "name": "acceptanceType",
      "valueString": "1"
    },
    {
      "name": "acceptanceDate",
      "valueString": "1992-01-13"
    },
    {
      "name": "patient",
      "resource": {
        "managingOrganization": {
          "reference": "XX1"
        },
        "generalPractitioner": [
          {
            "reference": "4826940,281"
          }
        ],
        "identifier": [
          {
            "system": "https://fhir.nhs.uk/Id/nhs-number",
            "value": "N/10/10"
          }
        ],
        "name": [
          {
            "family": "STEVENS",
            "given": [
              "CHARLES",
              "ANTHONY",
              "JOHN"
            ],
            "prefix": [
              "MR"
            ]
          }
        ],
        "gender": "male",
        "birthDate": "1991-11-06",
        "address": [
          {
            "line": [
              "MOORSIDE FARM",
              "OLD LANE",
              "ST PAULS CRAY",
              "ORPINGTON",
              "KENT"
            ],
            "postalCode": "BR6  7EW",
            "use": "home"
          }
        ],
        "extension": [
          {
            "url": "http://hl7.org/fhir/StructureDefinition/patient-birthPlace",
            "valueString": "BURY"
          }
        ],
        "resourceType": "Patient"
      }
    }
  ],
  "resourceType": "Parameters"
}