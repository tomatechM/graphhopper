# Auteurs
Mohamed Atmani
Chiril Reabitchi

# Classe sélectionnée pour les tests : [GHUtility.java](./core/src/main/java/com/graphhopper/util/GHUtility.java)

## Classe où se trouvent les tests : [GHUtilityTest.java](./core/src/test/java/com/graphhopper/util/GHUtilityTest.java)

### 1. testEdgeKeyRoundtrip() : 

* Intention du test: vérifier l’encodage/décodage d’un edgeKey et la fonction reverseEdgeKey.
* Motivation des données du test:edgeId = 7 (valeur arbitraire non nulle).un id simple permet de vérifier bit‑shifts et opération paire/impair.
* Explication de l'oracle:GHUtility.getEdgeFromEdgeKey(keyTrue) et getEdgeFromEdgeKey(keyFalse) doivent retourner edgeId ; reverseEdgeKey(reverseEdgeKey(key)) doit rendre la même clé ; les deux clés (reverse vrai/faux) diffèrent d’1 (bit de direction).

### 2. testAsSet() : 

* Intention du test: vérifier que GHUtility.asSet élimine les doublons et retourne un Set correct.
* Motivation des données du test:valeurs [1,2,2,3,1] (doublons présents).couvrir suppression de doublons et ordre/présence.
* Explication de l'oracle:taille du Set == 3 et contient 1, 2, 3.

### 3. testRandomDoubleInRange_deterministic_and_bounds(): 

* Intention du test: valider la fonction randomDoubleInRange pour stabilité et bornes.
* Motivation des données du test:Random avec seed fixe (12345L), min=5.0, max=10.0.seed fixe rend le test déterministe ; bornes non triviales vérifient l’échelle.
* Explication de l'oracle:deux Random indépendants avec même seed produisent la même valeur (assertEquals avec delta très petit) ; résultat ∈ [min, max].

### 4. testRandomDoubleInRange_equalMinMax() : 

* Intention du test: vérifier le comportement quand min == max.
* Motivation des données du test:min == max == 2.71828.cas limite, doit renvoyer exactement la valeur.
* Explication de l'oracle:valeur retournée == fixed (delta 0.0).

### 5. testCreateRectangleAndCircle() : 

* Intention du test: vérifier création géométrique de rectangle et cercle (id, type et topologie des coordonnées).
* Motivation des données du test:createRectangle("rect-id", 1.0,2.0,3.0,4.0) et createCircle("circ-id", 1.0,2.0,1000.0).valeurs simples permettent de vérifier orientation/coordonées et nombre attendu de sommets.
* Explication de l'oracle:id correct ; geometry non nulle et instance de Polygon ; rectangle a 5 coordonnées (anneau fermé) ; createCircle utilise n=36 → 37 coordonnées (n+1 pour fermer).

### 6. testRunConcurrently() : 

* Intention du test: vérifier que runConcurrently exécute tous les Runnable en parallèle correctement.
* Motivation des données du test: AtomicInteger initialisé à 0 ; runnables qui ajoutent 1,2,3,4. threads=2. opérations atomiques simples faciles à totaliser et vérifier.
* Explication de l'oracle: après exécution la somme == 10.

### 7. testManyEdgeKeysUniqueAndRecoverable() : 

* Intention du test: vérifier que createEdgeKey produit des clés uniques par (id,direction) et que getEdgeFromEdgeKey retrouve l’id.
* Motivation des données du test: N=500, id = i*2+1 (variété et non zéro).grand N détecte collisions/erreurs d’encodage; choix d’id impair évite ambigüité sur le bit direction.
* Explication de l'oracle:pour chaque id, getEdgeFromEdgeKey(k1/k2) == id ; l’ensemble des clés a taille N*2 (pas de collisions).

## Score de mutation des tests originaux :
<img width="1185" height="503" alt="TestsMuatationsOrigaux" src="https://github.com/user-attachments/assets/596a9308-a3ac-48e0-a2ab-86ee1b3bc6ec" />
## Score de mutation avec les nouveaux tests : 
<img width="1244" height="450" alt="TestsMutationsNouveaux" src="https://github.com/user-attachments/assets/cf62e37b-af81-4bb2-8f8f-961ea2801c21" />
## Test qui utilise java-faker : [GHUtilityTest.java](./core/src/test/java/com/graphhopper/util/GHUtilityTest.java)

* Intention du test: utilisation de java‑faker pour générer coordonnées plausibles et vérifier que le polygone circle a un centroïde proche du centre fourni.
* Motivation des données du test: Faker avec seed fixe (12345L) → id généré, latitude/longitude normalisées (virgule→point), radius aléatoire entre 100 et 5000 m.tester createCircle sur valeurs réelles/variées tout en restant déterministe; vérifier cohérence géométrique du polygone généré.
* Explication de l'oracle: id égal ; geometry instance de Polygon ; calcul du centroïde → distance centroid‑centre ≤ (radius * 0.2 + 1.0). (Tolérance empirique pour approximation de centroïde vs projection/discrétisation du polygone.)
