# Front BAN (Banque Adresse Nationale)

## Prérequis

* NodeJS 16
* Yarn

## Installation des dépendances (obligatoire avant toute autre action Yarn)

```
yarn install
```

## Démarrer le projet en mode Dev

```
yarn start
```

## Compiler le projet en static (fichiers HTML)

```
yarn build
```

Les fichiers seront disponibles dans le répertoire `./dist/webapp/`

## Personnalisation du Backend JAVA

Par défaut, le backend JAVA utilisé par le module front est `http://localhost:8080`

Pour modifier ceci, remplacer le fichier statique `assets/config.json` par le backend que vous souhaitez :

```json
{
  "url": "http://localhost:8080"
}
```
