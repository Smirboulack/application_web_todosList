# Projet Conception d'Application Web
**Réalisé par : Benziane Abdeldjallil (P1919796) et Temirboulatov Koureich (P1707160)**

## Vue d'ensemble
Ce dépôt contient l'ensemble des travaux pratiques (TPs) réalisés au fil du semestre, de TP1 à TP7. Chaque branche du dépôt correspond à un TP spécifique.

## Accès à l'Application
- **Application Client (Tomcat):** [https://192.168.75.111/api/client/](https://192.168.75.111/api/client/)
- **Serveur (Nginx):** [https://192.168.75.111/](https://192.168.75.111/)

## Détails des TPs

### TP 1 : Programmation Java côté serveur
- Objectif : Corriger les erreurs de programmation dans le squelette de code.
- Découverte et utilisation des beans Java pour exécuter du code Java directement depuis HTML.

### TP 2 : infrastructure côté serveur
- Déploiement du serveur Tomcat sur une VM.
- Administration des utilisateurs.
- Mise en place de l'intégration continue avec configuration du fichier `gitlab-ci.yml`.

### TP 3 : Design patterns côté serveur en Java
- Mise en place du pattern MVC.
- Implémentation de la logique métier avec DAO.
- Séparation de la gestion des utilisateurs et des todos en différentes classes.
- Accomplissement jusqu'à la partie 2.5.

### TP 4 : Web API (programmation REST)

- Création d'une api à l'aide de requêtes dans swagger. Le but était de créer une api back-end rest pour l'application client du tp5.
- Utilisation de postman pour effectuer les requêtes contenu dans les différentes collections.

### TP 5 : Programmation côté client (requêtage asynchrone)

- Développement de l'application client, notre application effectue les requêtes sur l'api du professeur. Implémentation des fonctionnalités du sujet.
- Pour chaque besoin des fonctionnalités, nous avons effectué les différentes requêtes asynchrones sur les chemins de l'api. Les méthodes POST,PUT,GET,DELETE 
- ont été utilisés dans les headers des fonctions.
- Toutes les 5 secondes nous gérons le contenu du tableau de todos grâce à la fonction setTodos qui est appellée par intervalle.
- On a aussi pris la liberté d'ajouter des fonctions de tri par titre et par assignation pour le tableau de todos.
- Déploiement de l'application sur la vm grâce à l'intégration continue.

### TP 6 : Performance des applications Web
- Mesure des performances de sites web selon différents critères. Proposition de solution pour optimiser les performances du site web de l'université UCBL.
- Comparaison de performances du site web de l'université Lyon 2.
- Nous avons utilisés un script javascript à mettre dans la console après enregistrement de performances pour effectuer les mesures des critères demandés.

### TP 7 : Optimisation d'une Single-Page Application

- Dans ce tp, nous avons mesuré les performances de notre application client déployé sur Tomcat et celle déployé à la racine du serveur nginx.
- Nous avons aussi utilisés un script légérement différent du tp 6 pour effectuer nos mesures.
- Nous avons générés des audits lightouse pour tenter d'améliorer notre application.
- Ci-dessous figures les résultats de nos mesures avant et après optimisation : 

**1. Analyse de l'état initial de votre application**

- Déploiement sur Tomcat.
- Condition de test : VPN depuis domicile.

Avant optmisation audit lightouse : 

![avant optimisation](/tp7/audit-lightouse-avant-optmisation-1.png)

Script utilisé : 

```js
function MesureHtmlAppshellCrp() {
    var performanceData = performance.timing;
    // Temps de récupération de la page HTML
    var htmlFetchTime = performanceData.responseEnd - performance.timeOrigin;
    console.log("Temps de récupération de la page HTML : " + htmlFetchTime + " ms");
    console.log("--------------------------------------------------------------------------");
    const appShellReadyTime = performance.getEntriesByType('paint').find(paint => paint.name === 'first-contentful-paint').startTime;
    console.log('Temps jusqu\'à ce que l\'app shell soit prêt:', appShellReadyTime, 'ms');
    console.log("--------------------------------------------------------------------------");
    let crp = performance.timing.domComplete - performance.timeOrigin;
    console.log("temps load crp : " + crp);
}
```


html : 28.8 ms App shell : 136 ms CRP : 174 ms

**2. Déploiement des fichiers statiques sur nginx**

- Déploiement sur nginx.
- Condition de test : VPN depuis domicile

script : même script que mentionné plus haut.

html : 24.80 ms App shell : 115 ms CRP : 150 ms

(Amélioratin en moyenne)
```diff 
+ Amélioration temps de chargement de la page html : 13.889%
+ Amélioration temps d'affichage de l'app shell : 15.441 %
+ Amélioration temps d'affichage du chemin critique de rendu (CRP): 13,793 %
```


**3. Optimisation de votre application**

- Utilisation de CDN

avant utilisation de CDN : html : 24.80 ms App shell : 115 ms CRP : 150 ms
après utilisation de CDN pour bootstrap & mustache : html : 22.899 ms App shell : 115.899 ms CRP : 118.89990234375 ms

(Amélioratin en moyenne)
```diff 
+ Amélioration temps de chargement de la page html : 8,065 %
+ Amélioration temps d'affichage de l'app shell : 0 %
+ Amélioration temps d'affichage du chemin critique de rendu (CRP): 13,793 %
```
- Utilisation d'attributs async et/ou defer pour décaler le chargement de scripts non nécessaires au CRP

avant utilisation de defer : html : 22.899 ms App shell : 115.899 ms CRP : 118.899 ms 
après utilisation de defer : html : 24.69 ms App shell : 113.59 ms CRP : 116.6999 ms 

(Amélioratin en moyenne)
```diff 
- Amélioration temps de chargement de la page html : -7,864 % (dégradation)
+ Amélioration temps d'affichage de l'app shell :  1,984 %
+ Amélioration temps d'affichage du chemin critique de rendu (CRP): 1,85 %
```
- Réduction du nombre de ressources critiques

avant réduction : html : 24.69 ms App shell : 113.59 ms CRP : 116.6999 ms
après réduction : html : 25.89 ms App shell : 114.5 ms CRP : 111.39 ms

(Amélioratin en moyenne)
```diff 
- Amélioration temps de chargement de la page html : -4,86 % (dégradation)
+ Amélioration temps d'affichage de l'app shell : 0,801 %
+ Amélioration temps d'affichage du chemin critique de rendu (CRP): 4,549 %
```

- Minification des scripts (html/css/js)

avant Minification : html : 25.89 ms App shell : 114.5 ms CRP : 111.39 ms
après Minification : html : 20.5 ms App shell :  109.90 ms CRP : 109.80 ms 

(Amélioratin en moyenne)
```diff 
+ Amélioration temps de chargement de la page html : 20,819 %
+ Amélioration temps d'affichage de l'app shell : 4,017 %
+ Amélioration temps d'affichage du chemin critique de rendu (CRP): 1,427 %
```
- Refactoring de l'application pour charger plus rapidement : app shell, CRP

avant Minification : html : 20.5 ms App shell :  109.90 ms CRP : 109.80 ms 
après Minification : html : 23.19 ms App shell :  104.5 ms CRP : 109.19 ms 

(Amélioratin en moyenne)
```diff 
- Amélioration temps de chargement de la page html : -13,122 % (dégradation)
+ Amélioration temps d'affichage de l'app shell : 4,914 %
+ Amélioration temps d'affichage du chemin critique de rendu (CRP): 0,556 %
```

Audit lightouse après optimisation : 

![avant optimisation](/tp7/audit-lightouse-optimiser-1.jpg)
