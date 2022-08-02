{
  "ServiceOperation": "Core-2007-09-DataManagement/expandGRMRelationsForPrimary",
  "InputType": "MyFirstModule.ExpandGRMPrimaryInput",
  "ResponseType": "MyFirstModule.ExpandGRMPrimaryResponse",
  "ObjectMapping": "",
  "OperationInput": {
    "primaryObjects": [ "$Input\/MyFirstModule.primaryObjects/TcConnector.ListOfModelObjects" ],
    "pref": {
      "expItemRev": false,
      "returnRelations": true,
      "info": [
        {
          "relationTypeName": "$Input\/relationTypeName",
          "otherSideObjectTypes": [ ]
        }
      ]
    }
  },
	"OperationResponse": {
		"output": [{
				"relationshipData": [{
						"relationshipObjects": "$Response\/MyFirstModule.relationshipObjects"
					}
				]
			}
		]
	}
}
