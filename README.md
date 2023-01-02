# AsciiGuessr

Développé par Karim AOULAD-TAYAB, Nassim BOUKEBOUT

Contacts : <karim.aouladtayab.etu@univ-lille.fr> , <nassim.boukebout@univ-lille.fr>

## Présentation de AsciiGuessr

AsciiGuessr est un jeu ludo-pédagogique en mode ASCII dans lequel on doit deviner les pays du monde selon 4 continents (Europe,Afrique,Amérique,Asie), ce jeu offre plusieurs fonctionnalités tels que:
- **Le mode classique (solo)**: Le joueur choisit une continent parmi les 4 et tente de deviner le plus de pays parmi les 10 pays choisis aléatoirement (pour cela il a à sa disposition le continent affiché en ASCII et chaque pays numéroté et le joueur doit les localiser en entrant un nombre).
	- Le niveau de pts gagné à pour chaque pays dépend de sa difficulté à le localiser (qui va de 10pts à 1000pts).
	- Ce jeu propose un moyen de s'exercer sur de la géographie car il indique la localisation du bon pays lorsque l'on se trompe pour mieux apprendre.
- **Un système d'authentification**: les scores du joueur, les continents, sont stockés dans un csv, il y a une persistance des données qui permet entre autres de pouvoir distinguer chaque joueur et donc de créer un système d'authentification.
- **Le mode 1v1**: ce mode de jeu reprend le même système que le mode classique mais s'éxécute 2 fois, 1 pour chaque jour et c'est une fois que la partie du premier se termine que la seconde partie commence, ce mode de jeu ne nécessite pas d'authentification. Une fois les 2 parties terminées, le vainqueur est désigné et le bilan des pts est affiché.
- **Un classement des meilleurs joueurs** : Les 5 meilleurs joueurs de AsciiGuessr sont affichés dans le classement du jeu.

*Des captures d'écran illustrant le fonctionnement du logiciel sont proposées dans le répertoire shots.*

Quelques illsustrations:

![menu_principal](shots/menu_principal.png)

![menu_joueur](shots/menu_joueur.png)

## Utilisation de AsciiGuessr

Afin d'utiliser le projet, il suffit de taper les commandes suivantes dans un terminal :

**Compilation des fichiers présents dans 'src' et création des fichiers '.class' dans 'classes'**

```
./compile.sh
```

**Lancement du jeu**

```
./run.sh AsciiGuessr
```
