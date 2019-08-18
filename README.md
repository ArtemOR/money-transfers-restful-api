"# money-transfers-restful-api"

Used technologies:
    Java 8
    Gradle
    Spark Framework
    Google Gson
    io.rest-assured

Applications supports:
-create users and accounts
-put money to account, transfer money from one account to another
-receive user objects, account objects and all transfer history
-delete users and accounts

To run application:
1. gradlew build
2. java -jar ".\build\libs\money-transfers-restful-api-1.0-SNAPSHOT.jar"
or just run jar added to root directory

- in root directory there is PostMan Collection with call examples "transfer.postman_collection"

- already in memory created some test objects of users and accounts

Methods description:
1. Create operation
1.1 Create a user
Description: create a user for a system
URL: /users
Method: POST
Sample Call:
	POST http://<host>/users
	Body:
		{
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
			"message": "User with provider passportId is already exist: passportId=12341"
			}

	HTTP Code: 400
	Body: {
			"errorCode": "id1",
			"message": "Missing mandatory parameters: passportId"
		  }

1.2 Create an account
Description: create a user for a system
URL: /users
Method: POST
Sample Call:
	POST http://<host>/accounts
	Body:
		{
			"userPassportId": "111",
			"moneyBalance": 1000,
			"creditLimit": 0
		}
Success Response:
	HTTP Code: 200
	Body: {
			"id": 11566142796482,
			"userPassportId": "111",
			"moneyBalance": 1000,
			"accountType": "DEBIT",
			"creditLimit": 0
		  }
Error Responses:
	HTTP Code: 400
	Body: {
			"errorCode": "id1",
			"message": "Missing mandatory parameters: passportId"
		  }

	HTTP Code: 404
	Body: {
			"errorCode": "id3",
			"message": "User with provider passportId does not exist: passportId=userPassportId"
		  }

		  HTTP Code: 400
	Body: {
			"errorCode": "id5",
			"message": "Money parameter should contain positive value: parameter=creditLimit"
	}

2 Read operations
2.1 Retreive all users
Description: retrieves all users from system
URL: /users/getAll
Method: GET
Sample Call: GET http://<host>/users/getAll
Success Response:
	HTTP Code: 200
	Body: [
			{
			 "id": 1,
			 "name": "John",
			 "passportId": "111"
			}, ...
		  ]

2.2 Retreive user by passport id
Description: retrieves user by user's passportId
URL: /users/{passportId}
Method: GET
Sample Call: GET http://<host>/users/111
Success Response:
	HTTP Code: 200
	Body: {
			"id": 1,
			"name": "John",
			"passportId": "111"
		}
Error Response:
	HTTP Code: 404
	Body: {
			"errorCode": "id3",
			"message": "User with provider passportId does not exist: passportId=2221"
		  }

2.3 Retrieve all accounts
Description: retrieves all accounts from system
URL: /accounts/getAll
Method: GET
Sample Call: GET http://<host>/accounts/getAll
Success Response:
	HTTP Code: 200
	Body: [
			{
			  "id": 11,
			  "userPassportId": "111",
			  "moneyBalance": 1000,
			  "accountType": "DEBIT",
			  "creditLimit": 0
			}, ...
		  ]

2.4 Retrieve account by account id
Description: retrieves account by accounts id
URL: /accounts/{accountId}
Method: GET
Sample Call: GET http://<host>/accounts/11
Success Response:
	HTTP Code: 200
	Body: {
			"id": 12,
			"userPassportId": "111",
			"moneyBalance": 0,
			"accountType": "CREDIT",
			"creditLimit": 1000
		  }

Error Response:
	HTTP Code: 404
	Body: {
			"errorCode": "id4",
			"message": "Account with provider accountId does not exist: accountId=121"
		  }

2.5.1 Retrieve all transfers
Description: retrieves all transfers
URL: /account/transfers
Method: GET
Sample Call: GET http://<host>/account/transfers
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
			"message": "Transfers not found"
		  }

2.5.2 Retrieve transfers by reciever id
Description: retrieves all transfers sent to account by accountToId
URL: /account/transfers?accountToId={accountToId}
Method: GET
Sample Call: GET http://<host>/account/transfers?accountToId=13
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

2.5.3 Retrieve transfers by sender id
Description: retrieves all transfers sent from account by accountFromId
URL: /account/transfers?accountFromId={accountFromId}
Method: GET
Sample Call: GET http://<host>/account/transfers?accountFromId=11
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

3 Transfers Operations (Update for account, create for transfers history)
3.1 Put money to account
Description: increase balanse of account
URL: /accounts/recharge
Method: PUT
Sample Call: PUT http://<host>/accounts/recharge
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
			"message": "Missing mandatory parameters: request"
		  }

		  HTTP Code: 400
	Body: {
			"errorCode": "id5",
			 "message": "Money parameter should contain positive value: parameter=amount"
		  }

3.2 Transfers between accounts
Description: taking mone from one account and put it to an0ther
URL: /accounts/transfer
Method: PUT
Sample Call: PUT http://<host>/accounts/transfer
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
				"userPassportId": "111",
				"moneyBalance": 900,
				"accountType": "DEBIT",
				"creditLimit": 0
			},
			{
				"id": 13,
				"userPassportId": "222",
				"moneyBalance": 3100,
				"accountType": "DEBIT",
				"creditLimit": 0
			}
		  ]

Error Response:
	HTTP Code: 400
	Body: {
			"errorCode": "id1",
			"message": "Missing mandatory parameters: request"
		  }

	HTTP Code: 400
	Body: {
			"errorCode": "id5",
			 "message": "Money parameter should contain positive value: parameter=amount"
		  }

	HTTP Code: 404
	Body: {
			"errorCode": "id4",
			"message": "Account with provider accountId does not exist: accountId=1003"
		  }

	HTTP Code: 400
	Body: {
			"errorCode": "id6",
			"message": "Not enough money to complete the operation"
		  }


4 Delete Operations
4.1 Delete a user by user passport id
Description: delete a user from a system by passport id and all accounts related to user
URL: /users/{passportId}
Method: DELETE
Sample Call:
	DELETE http://<host>/users/111

Success Response:
	HTTP Code: 204
	Body: no Content

Error Responses:
	HTTP Code: 404
	Body: {
			"errorCode": "id3",
			"message": "User with provider passportId does not exist: passportId=111"
		  }

4.2 Delete an account by account id
Description: delete an acount from the system
URL: /accounts/{id}
Method: DELETE
Sample Call:
	DELETE http://<host>/accounts/13

Success Response:
	HTTP Code: 204
	Body: no Content

Error Responses:
	HTTP Code: 404
	Body: {
			"errorCode": "id4",
			"message": "Account with provider accountId does not exist: accountId=131"
		  }