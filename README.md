# FeedbackSettings
![bild](https://user-images.githubusercontent.com/75727533/158780157-78abc11a-ec21-4db0-ba08-989851783c05.png)


## Leverantör

Sundsvalls kommun

## Beskrivning
FeedbackSettings är en tjänst som hanterar aviseringsinställningar för privatpersoner och organisationer.


## Tekniska detaljer

### Starta tjänsten

|Miljövariabel|Beskrivning|
|---|---|
|**Databasinställningar**||
|`QUARKUS_DATASOURCE_JDBC_URL`|JDBC-URL för anslutning till databas|
|`QUARKUS_DATASOURCE_USERNAME`|Användarnamn för anslutning till databas|
|`QUARKUS_DATASOURCE_PASSWORD`|Lösenord för anslutning till databas|


### Paketera och starta tjänsten
Applikationen kan paketeras genom:

```
./mvnw package
```
Kommandot skapar filen `api-feedbacksettings-<version>-runner.jar` i katalogen `target`. Tjänsten kan nu köras genom kommandot `java -jar target/api-feedbacksettings-<version>-runner.jar`.

### Bygga och starta med Docker
Exekvera följande kommando för att bygga en Docker-image:

```
docker build -f src/main/docker/Dockerfile -t api.sundsvall.se/ms-feedbacksettings:latest .
```

Exekvera följande kommando för att starta samma Docker-image i en container:

```
docker run -i --rm -p 8080:9090 api.sundsvall.se/ms-feedbacksettings
```

#### Kör applikationen lokalt

<div style='border: solid 1px #0085A9; border-radius: 0.5em; padding: 0.5em 1em; background-color: #D6E0E3; margin: 0 0 0.8em 0 '>
  För att köra applikationen lokalt måste du ha Docker Desktop installerat och startat på din dator
</div>

Exekvera följande kommando för att bygga och starta en container i sandbox mode:  

```
docker-compose -f src/main/docker/docker-compose-sandbox.yaml build && docker-compose -f src/main/docker/docker-compose-sandbox.yaml up
```


## 
Copyright (c) 2021 Sundsvalls kommun
