**How to Run the API**
1. mvn clean package
2. java -jar target/challanges-0.0.1-SNAPSHOT-jar-with-dependencies.jar

**Sample Curl Commands**
1. Fetch the account
curl http://localhost:8090/account/1222
2. Amount transfer
curl -X PUT http://localhost:8090/account -d '{ "fromAccount" : 1, "toAccount": 2, "transferAmount": 10}'

**Notes**
Refer AccountDataSourceImpl.java to find the account information available in the system