1. Analyse de l'état initial de votre application

--Déploiement sur Tomcat.
--Condition de test : VPN depuis domicile.

Script utilisé : 

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


html : 28.8 ms App shell : 136 ms CRP : 174 ms

2. Déploiement des fichiers statiques sur nginx

--Déploiement sur nginx.
--Condition de test : VPN depuis domicile

script : même script que mentionné plus haut.

html : 24.80 ms App shell : 115 ms CRP : 150 ms

(Amélioratin en moyenne)
Amélioration temps de chargement de la page html : 13.889%
Amélioration temps d'affichage de l'app shell : 15.441 %
Amélioration temps d'affichage du chemin critique de rendu (CRP): 13,793 %


3. Optimisation de votre application

--Utilisation de CDN--

avant utilisation de CDN : html : 24.80 ms App shell : 115 ms CRP : 150 ms
après utilisation de CDN pour bootstrap & mustache : html : 22.899 ms App shell : 115.899 ms CRP : 118.89990234375 ms

(Amélioratin en moyenne)
Amélioration temps de chargement de la page html : 8,065 %
Amélioration temps d'affichage de l'app shell : 0 %
Amélioration temps d'affichage du chemin critique de rendu (CRP): 13,793 %

--Utilisation d'attributs async et/ou defer pour décaler le chargement de scripts non nécessaires au CRP--

avant utilisation de defer : html : 22.899 ms App shell : 115.899 ms CRP : 118.899 ms 
après utilisation de defer : html : 24.69 ms App shell : 113.59 ms CRP : 116.6999 ms 

(Amélioratin en moyenne)
Amélioration temps de chargement de la page html : -7,864 % (dégradation)
Amélioration temps d'affichage de l'app shell :  1,984 %
Amélioration temps d'affichage du chemin critique de rendu (CRP): 1,85 %

--Réduction du nombre de ressources critiques--

avant réduction : html : 24.69 ms App shell : 113.59 ms CRP : 116.6999 ms
après réduction : html : 25.89 ms App shell : 114.5 ms CRP : 111.39 ms

(Amélioratin en moyenne)
Amélioration temps de chargement de la page html : -4,86 % (dégradaton)
Amélioration temps d'affichage de l'app shell : 0,801 %
Amélioration temps d'affichage du chemin critique de rendu (CRP): 4,549 %


--Minification des scripts (html/css/js)--

avant Minification : html : 25.89 ms App shell : 114.5 ms CRP : 111.39 ms
après Minification : html : 20.5 ms App shell :  109.90 ms CRP : 109.80 ms 

(Amélioratin en moyenne)
Amélioration temps de chargement de la page html : 20,819 %
Amélioration temps d'affichage de l'app shell : 4,017 %
Amélioration temps d'affichage du chemin critique de rendu (CRP): 1,427 %

--Refactoring de l'application pour charger plus rapidement : app shell, CRP--

avant Minification : html : 20.5 ms App shell :  109.90 ms CRP : 109.80 ms 
après Minification : html : 23.19 ms App shell :  104.5 ms CRP : 109.19 ms 

(Amélioratin en moyenne)
Amélioration temps de chargement de la page html : -13,122 % (dégradation)
Amélioration temps d'affichage de l'app shell : 4,914 %
Amélioration temps d'affichage du chemin critique de rendu (CRP): 0,556 %