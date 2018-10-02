# WALLET MICROSERVICE PROJECT

It is a project which is a simple wallet microservice application running on the JVM that manages credit/debit transactions.

## Restrictions

The balance can be modified by registering transactions on the account, either debit transactions (removing funds) 
or credit transactions (adding funds). A debit transaction will only succeed if there are sufficient funds on the account (balance - debit amount >= 0). 
Unique global transaction id must be provided when registering a transaction on an account. 
It is also possible to fetch the users accounts and get current balance.

## Project Description

This project was implemented for creating wallet microservice application. Wallet microservice application was implemented
with Java9, Spring Boot and Maven2. You can find the detail information regarding the this project such as requirements, running procedure, 
testing procedure, api endpoints, out of scope and scalable system scope. There is an api rate limiter in this project to handle 
some dangerous attacks.

##Technologies
1. Java9
2. Spring Boot
3. Spring Data JPA
4. MySQL
5. Swagger for API endpoint tracking
6. RateLimiter
7. Actuator
8. slf4j
9. Maven
10. JUnit

## Requirements and steps to run this application
1. Install Java 9
2. Maven to build the application. 
3. Download and install MySQL server
4. Connect to the MySQL server
5. Make mysql configurations in application.properties

```
CHANGE THIS AREA IN APPLICATON.PROPERTIES

spring.datasource.url = jdbc:mysql://localhost:3306/mydb   
spring.datasource.username = root
spring.datasource.password = xL8bH8j*
```

6. Run this script on your database. Note that, db migration is out of scope. If it would be an enterprise application,
 we can add db migration procedure with Flyway tool.

```
CREATE TABLE IF NOT EXISTS process_type
(
	id INT AUTO_INCREMENT,
	description TEXT,
    PRIMARY KEY(id)
);

CREATE TABLE IF NOT EXISTS  wallet
(
	id BIGINT AUTO_INCREMENT,
	user_id BIGINT NOT NULL,
	balance DOUBLE DEFAULT 0 NOT NULL,
	last_updated TIMESTAMP DEFAULT now(),
	PRIMARY KEY(id)
);

CREATE TABLE IF NOT EXISTS wallet_transaction
(
	id BIGINT AUTO_INCREMENT,
	global_id VARCHAR(36) UNIQUE NOT NULL,
	type_id INT NOT NULL REFERENCES process_type(id),
	amount DOUBLE NOT NULL,
	wallet_id BIGINT REFERENCES wallet(id),
	last_updated TIMESTAMP DEFAULT now(),
	PRIMARY KEY(id)
);

insert into process_type (description) values ('Debit');
insert into process_type (description) values ('Credit');

-- Wallet creation for testing

insert into wallet (user_id, balance) values (1, 10);
insert into wallet (user_id, balance) values (2, 20);
insert into wallet (user_id, balance) values (3, 30);
insert into wallet (user_id, balance) values (4, 40);
insert into wallet (user_id, balance) values (5, 50);

-- Create transaction for testing

insert into wallet_transaction (global_id,type_id,amount,wallet_id) values ('212d26c8-c4fa-11e8-a355-529269fb1459',1,10,1);
insert into wallet_transaction (global_id,type_id,amount,wallet_id) values ('212d3082-c4fa-11e8-a355-529269fb1459',1,20,2);
insert into wallet_transaction (global_id,type_id,amount,wallet_id) values ('212d355a-c4fa-11e8-a355-529269fb1459',1,30,3);
insert into wallet_transaction (global_id,type_id,amount,wallet_id) values ('212d39ec-c4fa-11e8-a355-529269fb1459',1,40,4);
insert into wallet_transaction (global_id,type_id,amount,wallet_id) values ('212d41da-c4fa-11e8-a355-529269fb1459',1,50,5);

CREATE INDEX WALLET_INDEX ON wallet(user_id);
``` 

7. run these commands respectively

``` 
mvn clean install
mvn spring-boot:run
``` 

## Running

If you want to run this application you need to follow the "Requirements and steps to run this application" part.
You should run the "RestWebServiceApplication" class and this application is running on port 8080. You can change this
port from application.properties.

Additionally you can use swagger ui to trace api endpoints.

``` 
http://localhost:8080/swagger-ui.html#/
``` 

## Testing

There are unit tests regarding the wallet microservice application. For the enterprise application, we can implement
unit test for all functionalities. If you want to check unit test, you can see the details in test folder.

Run test command

``` 
mvn clean test -Dspring.profiles.active=h2-test2
``` 

## Some Endpoints
You can find the required parameters and detail information at swagger ui. Note that 
if typeId=1 this means debit else if typeId=2 this means credit
``` 
--- TRANSACTION CONTROLLER ----

POST /api/v1/transaction
Add transaction of a given transaction and add balance

GET /api/v1/wallets/{id}/transactions
Find all transactions by given walletId

--- WALLET CONTROLLER ---

GET /api/v1/user/{userId}/wallet
Find wallet of a given userId

POST /api/v1/user/{userId}/wallet/create
Create wallet of a given wallet

GET /api/v1/wallet
Find all wallets

GET /api/v1/wallet/{walletId}
Find wallet of a given walletI
``` 

## Out of Scope

1. ) Currency and currency convert procedure
2. ) User based approach (just work with accountId, I am giving userId manually)
3. ) Authentication
4. ) Authorization
5. ) Another security approaches (such as JWT)

## Think as a Large System

If we think this system is a large scalable system we need to design this microservice very clearly. For this application,
we need to ensure that this system supports high availability, high reliability and minimum latency. We are using globalId
for each transaction and this should be unique in our system. It can be UUID and it helps us to ensure transaction global id
is unique. For other keys, we can use KGS(Key generation service). KGS servers pregenerated keys to another services and
we can also ensure each key is unique. Microservice helps us to scale our system easily and we can easily increase and
decrease wallect microsercive application instance. If system is traffic-heavy, we can create new instance otherwise 
we can decrease wallet microservice instance. Another important part is load balancer. We can use hardware load balancer
such as NGINX at the first part of the system. NGINX can be located between client and web server. For another parts we 
can use software load balancer. (Between web server and application server, between application server and database, between
application server and cache). Another important point is for every server, the best approach is having 3 more replicas.
We can use Redis or Memcache for cache mechanism and it can be global(shared) cache mechanism. We can decide to setup
Redis to different servers or to application servers. We can use LRU for caching mechanism but we need to decide the 
cache structure very carefully since there are a lot of transaction operations for this microservice. Another part is 
we can use message broker like Kafka for payment procedure. Kafka acts as a queue and we can use Kafka at payment API 
Gateway part. For each payment service can get responsible queue task from Kafka topic. For this system, our database
shpuld support ACID mechanism because we don't have a chance to make a mistake in the wallet procedure. Good approach 
is using MySQL or PostGreSQL. These are supporting ACID properties in default. Another important procedure is we can
log every step to follow the transactions. We can use monitoring application such as Datadog and alert mechanism to eliminate
possible problems. Moreover, If you are using cloud service such as AWS, Azure or Google Cloud Platform, these are automatically
support %99 availability. But they are using Round Robin approach for load balancer procedure. Round robin approach
stop sending a request if server is death with Round Robin procedure but if your server is heavy, round robin
approach cannot handle the this problem and continue to send request to this server. You can implement intelligent
Round Robin algorithm to your own load balancer. 




 








