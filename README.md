# axx2oauth
OAuth2 server implementing Resource Owner Password Credential Flow including fine grain right controle and DSA token validation.

<h2>Prerequisites</h2>

<ul>
	<li>Java 8</li>
	<li>Maven 3</li>
</ul>

<h2>Build</h2>

This Server is a SpringBoot application. After download you can build the application by the following command line in axx2oauth root folder:

<code>
	mvn clean install spring-boot:repackage spring-boot:run -DskipTests=true
</code>

Some master data are already included to start playing around with server immediately.

<h2>Configuration</h2>

There are three roles defined:

<ul>
	<li>admin,   pwd hash is d033e22ae348aeb5660fc2140aec35850c4da997</li>
	<li>manager, pwd hash is 1a8565a9dc72048ba03b4156be3e569f22771f23</li>
	<li>user,    pwd hash is 12dea96fec20593566ab75692c9949596833adc9</li>
</ul>

There are also two scopes defined:
<ul>
	<li>customer</li>
	<li>order</li>
</ul>

For each role different rights per scope are defined.
<ul>
  <li>admin -> customer=crud, order=crud</li>
  <li>manager -> customer=cru, order=cru</li>
  <li>user -> customer=r, order=r</li>
</ul>

<h2>Usage</h2>

To play around with OAuth2 server you now can submit several http requests:

<h3>Create Token</h3>

<h4>Request</h4>

<pre>
method: POST
url:    http://localhost:8080/services/oauth2/token
header: Accept application/json
header: Content-Type application/x-www-form-urlencoded
header: Authorization Basic c3lzdGVtOnN5c3RlbQ==
body:   grant_type=password&username=admin&password=d033e22ae348aeb5660fc2140aec35850c4da997&scope=~customer~order
</pre>

<h4>Response</h4>

By submitting this request you got the following response:

<b>Header</b>
<pre>
Status Code: 200 OK
Cache-Control: no-store
Content-Type: application/json;charset=UTF-8
Date: Sat, 19 Sep 2015 09:07:38 GMT
Pragman: no-cache
Server: Apache-Coyote/1.1
Signature-Algorithm: DSASHA1
Signature-Encoding: base64
Signature-Value: MCwCFGdGAMW4Ybu0B4DF/gtTJmEv9OdLAhRAMQAfLfczK2cOdMCFx0hGfXhUbw==
Token-Encoding: base64
Token-Value: eyJhY2Nlc3NfdG9rZW4iOiI2OGVlOTU0M2ZiNjIwYmY5ZGYwOTc5ODliMzYzMzgzYiIsImNyZWF0ZWRfYXQiOjE0NDI2NTM2NTgyNDcsImV4cGlyZXNfaW4i    OjM2MDAsInJlZnJlc2hfdG9rZW4iOiJjZDNhYWM2NzJmOWUyNDNmOTkzZDljNDVjZTU5MTkwYWI4MjU3MzNjIiwic2NvcGUiOnsiY3VzdG9tZXIiOiJjcnVk    Iiwib3JkZXIiOiJjcnVkIn0sInRva2VuX3R5cGUiOiJiZWFyZXIifQ==
Transfer-Encoding: chunked
</pre>

<b>Body</b>
<pre>
{
	"access_token": "68ee9543fb620bf9df097989b363383b",
	"created_at": 1442653658247,
	"expires_in": 3600,
	"refresh_token": "cd3aac672f9e243f993d9c45ce59190ab825733c",
	"scope":
	{
		"customer": "crud",
		"order": "crud"
	},
	"token_type": "bearer"
}
</pre>

As you can see, token is delivered via header and body. Header also holds a token signature. To make a call against a protected resource server now you have to add Token-Value and Signature-Value as header with every request. 

A code sample for validation of a token secured request please see JUnit test case <code>com.axxessio.oauth2.server.ApplicationTest</code>.

<h3>Check Token</h3>

Next you can validate a token is still valid, that means, user didn't log out. To check token in this way you have to call OAuth2Service like described here:

<h4>Request</h4>

<pre>
method: GET
url:    http://localhost:8080/services/oauth2/token?access_token=d################################
header: Accept application/json
header: Content-Type application/x-www-form-urlencoded
header: Authorization Basic c3lzdGVtOnN5c3RlbQ==
</pre>

<h4>Response</h4>

By submitting this request you got the following response:

<b>Header for successful check</b>

<pre>
Status Code: 200 OK
Content-Length: 14
Content-Type: application/json;charset=UTF-8
Date: Thu, 24 Sep 2015 12:51:23 GMT
Server: Apache-Coyote/1.1
</pre>

Status Code 200 indicates, that axxess_token is still valid and user is online. Otherwise you will receive the following response:

<b>Header for unsuccessful check</b>

<pre>
Status Code: 404 Not Found
Content-Type: application/json;charset=UTF-8
Date: Thu, 24 Sep 2015 12:52:44 GMT
Server: Apache-Coyote/1.1
Transfer-Encoding: chunked
</pre>

<h3>Delete Token</h3>

Next you can delete an existing token which is equal to user logout. By calling OAuth2-Server with the following parameters you will delete a token:

<h4>Request</h4>

<pre>
method: DELETE
url:    http://localhost:8080/services/oauth2/token?refresh_token=#########################
header: Accept application/json
header: Content-Type application/x-www-form-urlencoded
header: Authorization Basic c3lzdGVtOnN5c3RlbQ==
</pre>

<h4>Response</h4>

By submitting this request you got the following response:

<b>Header</b>

<pre>
Status Code: 200 OK
Content-Length: 12
Content-Type: application/json;charset=UTF-8
Date: Thu, 24 Sep 2015 12:55:53 GMT
Server: Apache-Coyote/1.1
</pre>

Submitting this request twice gives you the same result.

<h3>Get public key for token validation base on DSA</h3>

Finally OAuth2-Server offers an interface to get public key for DSA. By calling OAuth2-Server with the following parameters you will get the public key:

<h4>Request</h4>

<pre>
method: GET
url:    http://localhost:8080/services/oauth2/pubkey
header: Accept application/json
header: Content-Type application/x-www-form-urlencoded
header: Authorization Basic c3lzdGVtOnN5c3RlbQ==
</pre>

<h4>Request</h4>

By submitting this request you got the following response:

<b>Header</b>

<pre>
Status Code: 200 OK
Content-Type: application/json;charset=UTF-8
Date: Thu, 24 Sep 2015 12:59:50 GMT
Server: Apache-Coyote/1.1
Transfer-Encoding: chunked
</pre>

<b>Body</b>

<pre>
{
    "algorithm": "DSA",
    "encoding": "base64",
    "format": "X.509",
    "y": "Mr/uM5KW3/jajbTApIH0JZ7qvp4hrMxpa1O6oDcmzw2D+tgYDZFcCT22Lfi2oIqIE4IJXbQrVPw0IFunCUhcNYnrqbKPkMfNSzigt34o7i+w6OFf6rF9IMCPqDtv6fYGNap/3QazgnDwp2MavqRG+CoMBB1cLtAuQRrYQQvzTVs=",
    "g": "APfhoIXWmz3ey7yrXDa4V7l5lK+7+jrqgvlXTAs9B4JnUVlXjrrUWU/mcQcQgYC0SRZxI+hMKBYTt88JMozIpuE8FnqLVHyNKOCjrh4rs6Z1kW6jfwv6ITVi8ftiegEkO8yk8b6oUZCJqIPf4VrlnwaSi2ZegHtVJWQBTDv+z0kq",
    "p": "AP1/U4EddRIpUt9KnC7s5Of2EbdSPO9EAMMeP4C2USZpRV1AIlH7WT2NWPq/xfW6MPbLm1Vs14E7gB00b/JmYLdrmVClpJ+f6AR7ECLCT7up1/63xhv4O1fnxqimFQ8E+4P208UewwI1VBNaFpEy9nXzrith1yrv8iIDGZ3RSAHH",
    "q": "AJdgUI8VIwvMspK5gqLrhAvwWBz1"
}
</pre>

There is a helper class TokenHandler, together with JUnit test case ApplicationTest which shows, how to convert public key in Java representation and how to validate signature associated with token.

<h3>Persistence</h3>

Currently Apache derby is configured as embedded data base. There is a comment in persistence.xml to use an external, network based Apache derby database. By using an external instance you can add roles, rights and scopes easily. To generate a salted password hash in user table you have to follow these steps:
<ol>
	<li>Create individual salt for user, e.g. a secure random with at least 40 characters.</li>
	<li>Generate SHA1 hash from user password.</li>
	<li>Finally this password hash and salt will be stored as SHA384 hash in DB.</li>
	<li>To create your own passwords for new accounts use <code>org.apache.shiro.crypto.hash.Sha384Hash sh = new Sha384Hash(password, salt, 100);</code></li>
</ol>
	
