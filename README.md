"# money-transfers-restful-api"

Used technologies:

- Java 8 
- Gradle 
- Spark Framework http://sparkjava.com/
- Google Gson https://github.com/google/gson
- REST-assured http://rest-assured.io/

Applications supports:
-create users and accounts
-put money to account, transfer money from one account to another
-receive users, accounts and all transfer history
-delete users, accounts and transfers

To run application:
1. gradlew build
2. java -jar ".\build\libs\money-transfers-restful-api-1.0-SNAPSHOT.jar"

OR: run jar file from root directory

Application uses port 8082

- root directory contains PostMan Collection with call samples "transfer.postman_collection"
- root directory contains test plan for integration tests
- in memory already created some test objects of users and accounts

Methods description:

# 1. Create operations

## 1.1 Create a user
- Description: creates a user for a system
- URL: /users
- Method: POST
- Receive:	Mandatory parameters: "name" - String, "passportId" - String
- Return: 	User object
 
 Sample Call:

	POST http://localhost:8082/users
	Body: {
			"name":"testUser",
			"passportId":"12341"
		   }

Success Response:
	
	HTTP Code: 200
	Body: {
			"id": 501566140860052,
			"name": "testUser",
			"passportId": "12341"
		   }

Error Responses:
	
	HTTP Code: 400
	Body: {
			"errorCode": "id2",
			"detailMessage": "User with provider passportId is already exist: passportId=12341"
		   }

	HTTP Code: 400
	Body: {
			"errorCode": "id1",
			"detailMessage": "Missing mandatory parameters: passportId"
		   }

## 1.2 Create an account
- Description: create an account for a system
- URL: /users
- Method: POST
- Receive:	Mandatory parameters: "passportId" - String; Optional parameters: "moneyBalance" - Number, "creditLimit" - Number
- Return:		Account object
 
 Sample Call:

	POST http://localhost:8082/accounts
	Body: {
			"passportId": "111",
			"moneyBalance": 1000,
			"creditLimit": 0
		   }
		
 Success Response:

	HTTP Code: 200
	Body: {
			"id": 11566142796482,
			"passportId": "111",
			"moneyBalance": 1000,
			"accountType": "DEBIT",
			"creditLimit": 0
		   }
		
 Error Responses:

	HTTP Code: 400
	Body: {
			"errorCode": "id1",
			"detailMessage": "Missing mandatory parameters: passportId"
		   }

	HTTP Code: 404
	Body: {
			"errorCode": "id3",
			"detailMessage": "User with provider passportId does not exist: passportId=passportId"
		   }

	HTTP Code: 400
	Body: {
			"errorCode": "id5",
			"detailMessage": "Money parameter should contain positive value: parameter=creditLimit"
		   }
	
	HTTP Code: 400
	Body: {
    		"errorCode": "id9",
    		"detailMessage": "accountFromId and accountToId parameters are equal. Please specify different."
		   }
		
# 2 Read operations

## 2.1 Retrieve all users
- Description: retrieves all users from system
- URL: /users/getAll
- Method: GET
- Return:		List of users
- Sample Call: GET http://localhost:8082/users/getAll
	
Success Response:
	
	HTTP Code: 200
	Body: [
			{
			 "id": 1,
			 "name": "John",
			 "passportId": "111"
			}, ...
		   ]

## 2.2 Retrieve user by passport id
- Description: retrieves user by user's passportId with all related accounts
- URL: /users/{passportId}
- Method: GET
- Return:		User object with List of accounts

Sample Call: GET http://localhost:8082/users/111

Success Response:
	
	HTTP Code: 200
	Body: {
			"id": 1,
			"name": "John",
			"passportId": "111",
    		"accounts": [
				{
					"id": 11,
					"passportId": "111",
					"moneyBalance": 1000,
					"accountType": "DEBIT",
					"creditLimit": 0
				},
				{
					"id": 12,
					"passportId": "111",
					"moneyBalance": 0,
					"accountType": "CREDIT",
					"creditLimit": 1000
				}
    		   ]
		   }
		
Error Response:

	HTTP Code: 404
	Body: {
			"errorCode": "id3",
			"detailMessage": "User with provider passportId does not exist: passportId=2221"
		   }

## 2.3 Retrieve all accounts
- Description: retrieves all accounts from system
- URL: /accounts/getAll
- Method: GET
- Return:		List of accounts

Sample Call: GET http://localhost:8082/accounts/getAll

Success Response:
	
	HTTP Code: 200
	Body: [
			{
			  "id": 11,
			  "passportId": "111",
			  "moneyBalance": 1000,
			  "accountType": "DEBIT",
			  "creditLimit": 0
			}, ...
		  ]

## 2.4 Retrieve account by account id
- Description: retrieves account by accounts id
- URL: /accounts/{accountId}
- Method: GET
- Return:		Account object

Sample Call: GET http://localhost:8082/accounts/11

Success Response:
	
	HTTP Code: 200
	Body: {
			"id": 12,
			"passportId": "111",
			"moneyBalance": 0,
			"accountType": "CREDIT",
			"creditLimit": 1000
		  }

Error Response:

	HTTP Code: 404
	Body: {
			"errorCode": "id4",
			"detailMessage": "Account with provider accountId does not exist: accountId=121"
		   }

## 2.5 Retrieve all transfers
- Description: retrieves all transfers
- URL: /account/transfers
- Method: GET
- Return:		List of transfers

Sample Call: GET http://localhost:8082/account/transfers

Success Response:
	
	HTTP Code: 200
	Body: [
			{
				"id": 31566140498480,
				"accountFromId": 11,
				"accountToId": 13,
				"amount": 100,
				"time": 1566140498479
			},...
		]

## 2.6 Retrieve transfers by receiver id
- Description: retrieves all transfers sent to account by accountToId
- URL: /account/transfers?accountToId={accountToId}
- Method: GET
- Return:		List of transfers

Sample Call: GET http://localhost:8082/account/transfers?accountToId=13

Success Response:
	
	HTTP Code: 200
	Body: [
			{
				"id": 31566140498480,
				"accountFromId": 11,
				"accountToId": 13,
				"amount": 100,
				"time": 1566140498479
			},...
		   ]

Error Response:

	HTTP Code: 404
	Body: {
			"errorCode": "id7",
			"message": "Transfers not found for account: accountId={accountToId}"
		   }

## 2.7 Retrieve transfers by sender id
- Description: retrieves all transfers sent from account by accountFromId
- URL: /account/transfers?accountFromId={accountFromId}
- Method: GET
- Return:		List of transfers

Sample Call: GET http://localhost:8082/account/transfers?accountFromId=11

Success Response:
	
	HTTP Code: 200
	Body: [
			{
				"id": 31566140498480,
				"accountFromId": 11,
				"accountToId": 13,
				"amount": 100,
				"time": 1566140498479
			},...
		   ]

Error Response:

	HTTP Code: 404
	Body: {
			"errorCode": "id7",
			"message": "Transfers not found for account: accountId={accountToId}"
		  }

# 3 Transfers Operations

## 3.1 Put money to account
- Description: increase balance of account
- URL: /accounts/recharge
- Method: PUT
- Receive: 	Mandatory parameters: "accountToId" - Number, "amount" - Number
- Return:		Transfer object

Sample Call: 

	PUT http://localhost:8082/accounts/recharge
	Body: {
			"accountToId": 12,
			"amount": 1050
		   }
			
Success Response:

	HTTP Code: 200
	Body: {
			"id": 21566143759070,
			"accountToId": 12,
			"amount": 1050,
			"time": 1566143759070
		   }

Error Response:

	HTTP Code: 400
	Body: {
			"errorCode": "id1",
			"detailMessage": "Missing mandatory parameters: request"
		   }

	HTTP Code: 400
	Body: {
			"errorCode": "id5",
			"detailMessage": "Money parameter should contain positive value: parameter=amount"
		   }
		  
	HTTP Code: 400
	Body: {
			"errorCode": "id6",
			"detailMessage": "Account with provided accountId does not exist: accountId=1112"
		   }

	HTTP Code: 400
	Body: {
			"errorCode": "id8",
			"detailMessage": "Bad Request. Cannot cast string to number value"
		   }

## 3.2 Transfers between accounts
- Description: taking money from one account and put it to another
- URL: /accounts/transfer
- Method: PUT
- Receive: 	Mandatory parameters: "accountToId" - Number, "accountFromId" - Number, "amount" - Number
- Return:	List of chosen accounts with updated money balance

Sample Call: 
	
	PUT http://localhost:8082/accounts/transfer
	Body: {
			"accountFromId": 11,
			"accountToId": 13,
			"amount": 100
		   }
		   
Success Response:
	
	HTTP Code: 200
	Body: [
			{
				"id": 11,
				"passportId": "111",
				"moneyBalance": 900,
				"accountType": "DEBIT",
				"creditLimit": 0
			},
			{
				"id": 13,
				"passportId": "222",
				"moneyBalance": 3100,
				"accountType": "DEBIT",
				"creditLimit": 0
			}
		  ]

Error Response:
	
	HTTP Code: 400
	Body: {
			"errorCode": "id1",
			"detailMessage": "Missing mandatory parameters: request"
		  }

	HTTP Code: 400
	Body: {
			"errorCode": "id5",
			 "detailMessage": "Money parameter should contain positive value: parameter=amount"
		  }

	HTTP Code: 404
	Body: {
			"errorCode": "id4",
			"detailMessage": "Account with provider accountId does not exist: accountId=1003"
		  }

	HTTP Code: 400
	Body: {
			"errorCode": "id6",
			"detailMessage": "Not enough money to complete the operation"
		  }

# 4 Delete Operations

## 4.1 Delete a user by user passport id
- Description: delete a user from a system by passport id and all accounts related to user
- URL: /users/{passportId}
- Method: DELETE

Sample Call: DELETE http://localhost:8082/users/111

Success Response: HTTP Code: 204 Body: no Content

Error Responses:

	HTTP Code: 404
	Body: {
			"errorCode": "id3",
			"detailMessage": "User with provider passportId does not exist: passportId=111"
		  }

## 4.2 Delete an account by account id
- Description: delete an account from the system
- URL: /accounts/{id}
- Method: DELETE

Sample Call: DELETE http://localhost:8082/accounts/13

Success Response: HTTP Code: 204 Body: no Content

Error Responses:

	HTTP Code: 404
	Body: {
			"errorCode": "id4",
			"detailMessage": "Account with provider accountId does not exist: accountId=131"
		  }

## 4.3 Delete a transfer by transfer id
- Description: delete a transfer from the system
- URL: /accounts/transfers/{id}
- Method: DELETE

Sample Call: DELETE http://localhost:8082/accounts/transfers/55

Success Response: HTTP Code: 204 Body: no Content

Error Responses:

	HTTP Code: 404
	Body: {
			"errorCode": "id6",
			"detailMessage": "Transfer with provided transferId does not exist: transferId=555"
		  }

# 5 Multi-Read operations

## 5.1 Retrieve users by id list
- Description: retrieves users from system by provided id list
- URL: /users/multi-read
- Method: POST
- Return: List of users
- Sample Call:

	PUT http://localhost:8082/users/multi-read
	Body: {
			"where": {
			            ids:[111, 222]
			        }
		   }

Success Response:

	HTTP Code: 200
	Body: [
              {
                  "id": 1,
                  "name": "John",
                  "passportId": "111"
              },
              {
                  "id": 2,
                  "name": "Mike",
                  "passportId": "222"
              }
          ]


Error Responses:

	HTTP Code: 404
	Body: {
              "errorCode": "id14",
              "results": [
                  {
                      "error": {
                          "errorCode": "id5",
                          "detailMessage": "User with provided passportId does not exist: passportId=1"
                      }
                  },
                  {
                      "error": {
                          "errorCode": "id5",
                          "detailMessage": "User with provided passportId does not exist: passportId=2"
                      }
                  }
              ],
              "detailMessage": "No records found"
          }

Partial result:

HTTP Code: 207
{
    "errorCode": "id13",
    "results": [
        {
            "success": {
                "id": 1,
                "name": "John",
                "passportId": "111"
            }
        },
        {
            "error": {
                "errorCode": "id5",
                "detailMessage": "User with provided passportId does not exist: passportId=54321"
            }
        }
    ],
    "detailMessage": "Some records not found"
}

## 5.2 Retrieve accounts by id list
- Description: retrieves accounts from system by provided id list
- URL: /accounts/multi-read
- Method: POST
- Return: List of accounts
- Sample Call:

	PUT http://localhost:8082/accounts/multi-read
	Body: {
			"where": {
			            ids:[11, 12]
			        }
		   }

Success Response:

	HTTP Code: 200
	Body: [
              {
                  "id": 11,
                  "passportId": "111",
                  "moneyBalance": 1000,
                  "accountType": "DEBIT",
                  "creditLimit": 0
              },
              {
                  "id": 12,
                  "passportId": "111",
                  "moneyBalance": 0,
                  "accountType": "CREDIT",
                  "creditLimit": 1000
              }
          ]


Error Responses:

	HTTP Code: 404
	Body: {
              "errorCode": "id14",
              "results": [
                  {
                      "error": {
                          "errorCode": "id7",
                          "detailMessage": "Account with provided accountId does not exist: accountId=1"
                      }
                  },
                  {
                      "error": {
                          "errorCode": "id7",
                          "detailMessage": "Account with provided accountId does not exist: accountId=2"
                      }
                  }
              ],
              "detailMessage": "No records found"
          }

Partial result:

HTTP Code: 207
{
    "errorCode": "id13",
    "results": [
        {
            "success": {
                "id": 11,
                "passportId": "111",
                "moneyBalance": 1000,
                "accountType": "DEBIT",
                "creditLimit": 0
            }
        },
        {
            "error": {
                "errorCode": "id7",
                "detailMessage": "Account with provided accountId does not exist: accountId=12345"
            }
        }
    ],
    "detailMessage": "Some records not found"
}