# Projet intégration de fichier BAN (Banque Adresse National)

## Prérequis

* JAVA 11
* Maven 3

## Paramètre par défaut

Vous pouvez retrouver les valeurs des paramètres dans les fichiers `src/main/resources/application*.yml`.  
Cela vous permettra de configurer les SGBD correctement pour que l'applicatif se connecte correctement à ce dernier.

### Mode postgres (par défault)
Au préalable, vous devez avoir une base PostgreSQL dans votre environnement. 

```shell
# Lancement à l'aide de maven
mvn spring-boot:run
```

### Mode elastic
```shell
# Lancement à l'aide de maven
mvn spring-boot:run -Dspring-boot.run.profiles=elastic
```

### Compilation du projet (sans les tests)
```
mvn package -DskipTests=True
```

Le binaire `.jar` sera disponible dans le répertoire `./target/`.

### Lancement du service depuis le JAR

```
java -jar mon-fichier.jar
```


### Lancement des tests
```
mvn test
```

## Intégrer un fichier depuis une commande Curl
```
curl -v -F file=@addresse-38+69.csv "http://localhost:8080/address"
```

## Fichiers BAN
Vous pouvez retrouver les fichiers BAN de chaque département [ici, sur le site de l'IGN/Data.gouv.fr](https://adresse.data.gouv.fr/data/ban/adresses/latest/csv/)

