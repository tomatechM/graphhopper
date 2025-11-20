# Auteurs
Mohamed Atmani
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


- On peut voir que le score de mutation courant est de 9 %, ce qui est supérieur au score précedent de 2 %.

- Ensuite, on a fait un deuxième push, mais cette fois en mettant la majorité des tests dans GHUtilityTest.java en commentaires :


- On peut voir que le build échoue vu que le score de mutation diminue de 9% à 2%.
