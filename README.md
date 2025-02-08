# How to
1. Retrieve DeekSeek API key in below link:
[DeekSeek API|https://identity.getpostman.com/client-auth/confirm?auth_challenge=885e1d947fa17ff16f57d30cecdf1e85adfca690a31a22f13f7b4b6924eba1c1&auth_device=app_native&auth_device_version=11.31.3]
2. Add API token into below application.properties:
```
llm.deepSeek-api-key:<api-key>
```
3. Build and launch:
```
$ mvn clean install -DskipTests
$java -jar target/my.workflow-0.0.1-SNAPSHOT.jar
```
4. Use curl to test it:
```
curl -i -XPOST http://localhost:8080/api/v1/commands -d'{"input": "请把最近1周注册的4台机器加入到资源池子A"}' -H"Content-Type: application/json" -H"Accept: application/json"
```
Enjoy it
