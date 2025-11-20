# Auteurs
Mohamed Atmani,
Chiril Reabitchi


## Modification du workflow Github Actions - Justification

Objectif :
- Exécuter l'analyse de mutation (PIT) dans la CI et faire échouer le build si le score diminue par rapport au score précedent.

Choix de conception :

- On a ajouté un job "mutation" séparé du job build et test.
- Au début, nous avions essayé d'utiliser un artifact pour sauvegarder le score mais ça n'avait pas marché.On a fini par utiliser le cache pour sauvegarder le score de mutation.
- Tout d'abord, récupération du score de mutation précedent (appelé baseline) à partir du cache.
- Ensuite, lecture du fichier mutation_baseline.txt dans le cache pour récupérer le score précedent.
- Build du module approprié avant le PIT.
- Exécution du PIT 
- On a utilisé un parse en python qui permet d'extraire le score du rapport HTML de PIT (index.html)
- Comparaison du score de mutation avec le score précédent. Si le score est plus petit -> le build échoue
- sauvegarder le nouveau score dans le mutation_baseline.txt dans le cache avec une nouvelle clé unique.

Décisions d'implémentation :

- Pourquoi utiliser le cache ? Car si on sauvegarde le score directement dans le repo, il y aura besoin de faire un commit à chaque mise à jour.
- Pourquoi parser HTML au lieu de la sortie console ? Car c'est plus simple et fiable.
- Pourquoi est-ce qu'une clé unique est nécessaire pour le cache ? Car une fois qu'un cache est crée, on ne peut pas le modifier, seulement le lire, donc il faut à chaque fois créer un nouveau cache, d'où le concept de clé unique.

Validation de la modification : 

- On a fait une analyse de mutation sur [GHUtilityTest.java](./core/src/test/java/com/graphhopper/util/GHUtilityTest.java)
- On a fait un premier push avec tous les tests de GHUtilityTest.java actifs : 

<img width="1380" height="177" alt="build success" src="https://github.com/user-attachments/assets/d76a8495-0536-4ef3-ac18-fe86101299f2" />


- On peut voir que le score de mutation courant est de 9 %, ce qui est supérieur au score précedent de 2 %.
- Ensuite, on a fait un deuxième push, mais cette fois en mettant la majorité des tests dans GHUtilityTest.java en commentaires :

<img width="1357" height="197" alt="build failed" src="https://github.com/user-attachments/assets/9eb5f263-f080-4164-9b54-8f76bb0197e0" />

- On peut voir que le build échoue vu que le score de mutation diminue de 9% à 2%.


# Classes simulés (Mockito)

## Première classe testée et simulée : [Downloader.java](./core/src/main/java/com/graphhopper/util/Downloader.java)

Justifications du choix :
- La classe fait des requêtes HTTP, ouvre des streams et lit/écrit des fichiers.
- Elle parle à un URL extérieur (http://graphhopper.com/public/maps/0.1/europe_germany_berlin.ghz)
- La classe gère la décompression, les timeouts, les codes HTTP, ce qui est trop complexe pour un test unitaire.

Méthodes simulées :
- downloadAsString(String url, boolean)
- fetch(String url)

### Fichier de test mockito : [DownloaderMockitoTest.java](./core/src/test/java/com/graphhopper/util/DownloaderMockitoTest.java)

Définition des mocks :
* ```Downloader mockedDownloader = Mockito.mock(Downloader.class)```
    - Crée un mock complet de la classe Downloader ce qui permet de définir le comportement attendu des méthodes sans effectuer de vrais requêtes réseau.

* ```InputStream fakeStream = new ByteArrayInputStream("Hello".getBytes())```
    - Simule la lecture d'un fichier

* ```when(mockedDownloader.fetch("http://test.com")).thenReturn(fakeStream)```
    - Simule une réponse HTTP. Retourne "Hello" si on fetch le faux url

Choix des valeurs simulées :
- "http://fake-url.com" et "http://test.com" : URL fictives pour éviter des dépendances réseau.
- "FAKE_RESPONSE" et "Hello" : String simulés pour valider que chaque méthode retourne exactement ce qui est attendu.


## Deuxième classe testée et simulée : [NativeFSLockFactory.java](./core/src/main/java/com/graphhopper/storage/NativeFSLockFactory.java)

Justifications du choix :
- Les verrous dépendent du système de fichiers et peuvent échouer selon l’état réel du fichier.
- Les mocks permettent de tester la logique interne tels que le tryLock, release, de façon fiable.

Méthodes simulées :
- tryLock()
- isLocked()
- release()

### Fichier de test mockito : [NativeFSLockFactoryMockitoTest.java](./core/src/test/java/com/graphhopper/storage/NativeFSLockFactoryMockitoTest.java)

Définition des mocks :
* ```NativeFSLockFactory.NativeLock mockLock = mock(NativeFSLockFactory.NativeLock.class)```
    - Remplace un vérrou réel par un mock
    - Permet de vérifier la logique de isLocked et release si le lock est obtenu avec succès ou non

* ```when(mockLock.tryLock()).thenReturn(true/false)```, ```when(mockLock.isLocked()).thenReturn(true/false)```
    - Dépendamment de si le lock est obtenu ou non, on retourne true ou false

* ```doNothing().when(mockLock).release()```
    - Si le lock a été obtenu, il devrait release(), sinon, il ne devrait pas y avoir d'appels de la fonction.

Choix des valeurs simulées : 
- true ou false pour tryLock et isLocked : permet de tester toutes les branches du code (succès et échec).
- doNothing() pour release : permet de simuler la libération du verrou sans toucher au système de fichiers.

