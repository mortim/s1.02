//                     _ _  _____                          
//      /\            (_|_)/ ____|                         
//     /  \   ___  ___ _ _| |  __ _   _  ___  ___ ___ _ __ 
//    / /\ \ / __|/ __| | | | |_ | | | |/ _ \/ __/ __| '__|
//   / ____ \\__ \ (__| | | |__| | |_| |  __/\__ \__ \ |   
//  /_/    \_\___/\___|_|_|\_____|\__,_|\___||___/___/_|   
//
// Developpé par: AOULAD-TAYAB Karim, BOUKEBOUT Nassim
                                                                                       
import extensions.File;
import extensions.CSVFile;

class AsciiGuessr extends Program {
    // Constantes
    final String RESSOURCES_PATH = "../ressources/";
    final String PROMPT = "> ";
    // Ces couleurs ne sont pas définies dans l'interface 'Curses' de iJava
    final String ANSI_BROWN = "\033[0;33m";
    final String ANSI_DARKGRAY = "\033[1;30m";

    // --------------------------------------
    // Fonctions utiles

    // Récupère le contenu d'un fichier
    String getFileContent(String filepath) {
        String output = "";
        File file = newFile(filepath);
        while(ready(file))
            output += readLine(file) + "\n";
        return output;
    }

    // Vérifie si la chaîne de caractères est un nombre (ne comporte que des chiffres en caractère)
    boolean isNumber(String num) {
        int numLength = length(num);
        for(int i = 0; i < numLength; i++) {
            if(charAt(num, i) < '0' || charAt(num, i) > '9') {
                return false;
            }
        }
        return true;
    }

    // Retourne un entier associé à chaque élement de l'énumeration 'ContinentName' (entier qui représente l'indice dans le tableau de pts tel qu'il est défini dans la classe 'Player')
    int getIndex(ContinentName name) {
        if(name == ContinentName.AFRICA)
            return 0;
        else if(name == ContinentName.EUROPE)
            return 1;
        else if(name == ContinentName.AMERICA)
            return 2;
        else
            return 3;
    }

    // Fonction qui demande en entrée un choix entre 1 et nbChoice (utilisé pour les menus mais également pour une partie de jeu quand il faut deviner le pays d'un continent selon un entier qui le représente)
    int readChoice(String msg, int nbChoice, Player player) {
        int choice = 1;
        String input = "";
        boolean goodChoice = false;

        while(!goodChoice) {
            println(msg);
            if(player != null) {
                print(player.name);
            }
            print(PROMPT);
            input = readString();
    
            if(!equals(input, "")) {
                if(isNumber(input)) {
                    choice = stringToInt(input);
                    if(choice >= 1 && choice <= nbChoice)
                        goodChoice = true;
                    else {
                        println("La saisie ne figure pas parmi les nombres proposés");
                        delay(500);
                    }
                } else {
                    println("Entrez un nombre.");
                    delay(500);
                }
            } else {
                println("Vous n'avez pas entré de saisie utilisateur.");
                delay(500);
            }
            refreshScreenWithCoords(1,1);
        }
        return choice;
    }

    // Retourne le nombre de points obtenu selon le niveau de difficulté du pays trouvé
    int getPtsByDifficulty(String difficulty) {
        if(equals(difficulty,"1"))
            return 10;
        else if(equals(difficulty,"2"))
            return 100;
        else
            return 1000;
    }

    // Vérifie si l'élement est dans le tableau (utilisé pour vérifier si le pays qu'on a tiré est dans le tableau des pays déjà tirés)
    boolean isInArray(int n, int[] tab) {
        int sizeTab = length(tab);
        for(int i = 0; i < sizeTab; i++) {
            if(tab[i] == n) {
                return true;
            }
        }
        return false;
    }

    // Tire aléatoirement un pays (en évitant de tomber plusieurs fois le même pays dans la même session de jeu)
    int drawACountry(int nbCountries, int[] drawnCountries, int actualIdx) {
        int alea = -1;
        boolean termine = false;
        int i = actualIdx;
        // Reprendre au dernier indice du tableau
        while(!termine) {
            alea = (int)(random()*nbCountries)+1;
            if(!isInArray(alea,drawnCountries)) {
                drawnCountries[i] = alea;
                i++;
                termine = true;
            }
        }
        return alea;
    }

    // Trie les joueurs par ordre décroissant selon leur moyenne de pts sur chaque continent en utilisant le tri par séléction
    void sortPlayers(String[] names, int[] avgPts) {
        int size = length(names);
        String name_tmp;
        int avg_tmp;
        int iMax;
        for(int i = 0; i < size-1; i++) {
            iMax = i;
            for(int j = i+1; j < size; j++) {
                if(avgPts[j] > avgPts[iMax]) {
                    iMax = j;
                }
            }
            if(iMax != i) {
                name_tmp = names[i];
                avg_tmp = avgPts[i];

                names[i] = names[iMax];
                names[iMax] = name_tmp;

                avgPts[i] = avgPts[iMax];
                avgPts[iMax] = avg_tmp;
            }
        }
    }

    // Remet le curseur du terminal sur les cordonnées donnés en paramètre et efface le terminal (utilisé pour changer de 'scène' => passer du menu à une session de jeu par exemple) 
    void refreshScreenWithCoords(int x, int y) {
        cursor(x,y);
        clearScreen();
    }

    // Générer aléatoirement une astuce
    String[] generateTip() {
        // Premier élement => L'astuce, Deuxième élement => Sa position de curseur dans le terminal (sur le plan vertical)
        String[] tipInfos = new String[2]; 
        int alea = (int)(random()*3);

        if(alea == 0) {
            tipInfos[0] = "Selon la difficulté du pays, vous pouvez gagner de 10 à 1000pts (Facile : 10pts, Moyen : 100pts, Difficile: 1000pts)";
            tipInfos[1] = "20";
        } else if(alea == 1) {
            tipInfos[0] = "Mémorise tes bonnes réponses pour tes prochaines parties !";
            tipInfos[1] = "50";
        } else {
            tipInfos[0] = "Consultez le classement des joueurs pour évaluer votre niveau";
            tipInfos[1] = "45";
        }
        return tipInfos;
    }
    
    // --------------------------------------
    // Fonctions pour les fichiers CSV

    // Compte le nombre de pays (dans le CSV 'Pays.csv') qui sont issus du même continent que celui passé en paramètres (utilisé pour la fonction readChoice())
    int countCountries(ContinentName name) {
        CSVFile countries = loadCSV(RESSOURCES_PATH + "csv/Pays.csv");
        int lig = rowCount(countries);
        int k = 0;
        for(int i = 1; i < lig; i++) {
            if(equals(getCell(countries, i, 0), toString(name))) {
                k++;
            }
        }
        return k;
    }

    // Récupère tous les joueurs inscrit au jeu (dans le CSV 'Joueurs.csv')
    String[][] loadPlayers() {
        CSVFile players = loadCSV(RESSOURCES_PATH + "csv/Joueurs.csv");
        int lig = rowCount(players);
        int col = columnCount(players);
        String[][] result = new String[lig][col];
        for(int i = 0; i < lig; i++) {
            for(int j = 0; j < col; j++) {
                result[i][j] = getCell(players, i, j);
            }
        }
        return result;
    }

    // Retourner la ligne (dans le CSV 'Joueurs.csv') associé au joueur passé en paramètre (utilisé pour opérer des mis à jours sur le joueur ou pour l'authentification)
    int searchLig(Player player) {
        String[][] players = loadPlayers();
        int lig = length(players, 1);

        for(int i = 1; i < lig; i++) {
            if(equals(players[i][0], player.name)) {
                return i;
            }
        }
        return -1;
    }

    // Retourne toutes les informations sur le pays qu'on a choisi (dans le CSV 'Pays.csv') pendant la session de jeu (selon le numéro choisi et dans quel continent on joue)
    String[] searchCountry(Continent continent, int num) {
        String[] result = new String[]{"", ""};
        CSVFile countries = loadCSV(RESSOURCES_PATH + "csv/Pays.csv");
        int lig = rowCount(countries);
        int col = columnCount(countries);
        for(int i = 1; i < lig; i++) {
            if(equals(getCell(countries, i, 0),toString(continent.name)) && equals(getCell(countries, i, 2), ""+num)) {
                result[0] = getCell(countries, i, 1);
                result[1] = "" + getCell(countries, i, 3);
                return result;
            }
        }
        return result;
    }

    // Ajouter une nouvelle ligne au fichier CSV 'Joueurs.csv' (utilisé dans l'authetification du joueur, si le joueur n'existe pas)
    void addNewCSVRow(String[][] players, Player player) {
        int lig = length(players, 1);
        int col = length(players, 2);
        String[][] newPlayers = new String[lig+1][col];

        for(int i = 0; i < lig; i++) {
            for(int j = 0; j < col; j++) {
                newPlayers[i][j] = players[i][j];
            }
        }

        newPlayers[lig][0] = player.name;
        newPlayers[lig][1] = "" + player.pts[0];
        newPlayers[lig][2] = "" + player.pts[1];
        newPlayers[lig][3] = "" + player.pts[2];
        newPlayers[lig][4] = "" + player.pts[3];

        saveCSV(newPlayers, RESSOURCES_PATH + "csv/Joueurs.csv");
    }   

    // Met à jour les informations du joueur
    void save(Player player) {
        String[][] players = loadPlayers();
        int lig = length(players, 1);
        int col = length(players, 2); 
        int pos = searchLig(player);

        players[pos][0] = player.name;

        for(int i = 1; i <= 4; i++) {
            players[pos][i] = "" + player.pts[i-1];
        }
 
        saveCSV(players, RESSOURCES_PATH + "csv/Joueurs.csv");
    }

    // --------------------------------------
    // Fonctions pour les enumérations
    String toString(ContinentName name) {
        if(name == ContinentName.EUROPE)
            return "Europe";
        else if(name == ContinentName.AFRICA)
            return "Afrique";
        else if(name == ContinentName.ASIA)
            return "Asie";
        else
            return "Amérique";
    }

    // Retourne l'équivalent du choix du continent du menu (numéroté de 1 à 4) en une valeur de l'énumeration 'ContinentName'
    ContinentName toEnum(int n) {
        if(n == 1) {
            return ContinentName.AFRICA;
        } else if(n == 2) {
            return ContinentName.EUROPE;
        } else if(n == 3) {
            return ContinentName.AMERICA;
        } else {
            return ContinentName.ASIA;
        }
    }

    // --------------------------------------
    // Fonctions pour la classe 'Continent'
    Continent newContinent(int choice) {
        Continent continent = new Continent();
        continent.name = toEnum(choice);
        continent.nbCountries = countCountries(continent.name);
        continent.ascii = getFileContent(RESSOURCES_PATH + "ascii/" + toString(continent.name) + ".txt");
        return continent;
    }

    // Réprésentation du score et du jeu en chaîne de caractères (session de jeu)
    String toString(Continent continent, int pts, int foundCountries, int nbCountries, int remainderCountries) {
        String score = "\n      " + foundCountries + "/10 pays trouvés (" + remainderCountries + " pays restants) |  " + pts + " pts";
        String asciiContinent = "                             " + ANSI_YELLOW  + toString(continent.name) + "\n\n" + continent.ascii + ANSI_RESET;
        return score + asciiContinent;
    }

    // --------------------------------------
    // Fonctions pour la classe 'Player'
    Player newPlayer(String nickname) {
        Player player = new Player();
        player.name = nickname;
        player.pts = new int[]{0,0,0,0}; // Afrique, Europe, Amérique, Asie (même ordre que les colonnes du CSV 'Joueurs.csv')
        return player;
    }

    // Met à jour le nombre de points du joueur uniquement s'il a dépassé son score de pts actuel
    void setPts(Player player, int idxContinent, int pts) {
        if(pts > player.pts[idxContinent]) {
            player.pts[idxContinent] = pts;
        }
    }

    // Authentification via un pseudo, si le nom n'est pas présent dans le CSV 'Joueurs.csv', on crée un nouveau joueur
    void authenticate(Player player) {
        String[][] players = loadPlayers();
        int ligPlayer = searchLig(player);
        if(ligPlayer == -1) {
            println("Vous venez de créer un nouveau compte...");
            delay(1000);
            addNewCSVRow(players, player);
        } else {
            // Récupère les points du joueur existant et les affecte aux attributs de la classe 'Joueur' associé à ce joueur
            for(int i = 0; i < 4; i++) {
                player.pts[i] = stringToInt(players[ligPlayer][i+1]);
            }
        }
    }

    // Représentation en chaîne de caractères du profil du joueur (utilisé après authentification et pour le classement des meilleurs joueurs)
    String toString(Player player) {
        int sizePts = length(player.pts);
        String profile = player.name + " => \n";
        for(int i = 0; i < sizePts; i++) {
            profile += toString(toEnum(i+1)) + " " + ANSI_BLUE + player.pts[i] + "pts" + ANSI_RESET + " | ";
        }
        return profile;
    }

    // --------------------------------------
    // Fonctions d'affichage / saisie

    // Affichage du menu selon le type de menu et le joueur
    int menu(Menu type, Player player) {
        String msg = "";
        refreshScreenWithCoords(1,1);
        if(type == Menu.GAME) {
            msg = titleAnimated(RESSOURCES_PATH + "ascii/NomJeu.txt");
            msg += "\n╔════════════════════════════════════╗\n║(1) Se défier en 1v1 (compte invité)║\n║(2) Se connecter                    ║\n║(3) Quitter                         ║\n╚════════════════════════════════════╝\n";
            return readChoice(msg, 3, player);
        } else if(type == Menu.PLAYER) {
            msg = "Votre score actuel: " + toString(player);
            msg += "\n\n╔════════════════════════════════════╗\n║(1) Commencer une partie            ║\n║(2) Classement des joueurs          ║\n║(3) Se déconnecter                  ║\n║(4) Quitter                         ║\n╚════════════════════════════════════╝\n";
            return readChoice(msg, 4, player);
        }
        msg = "Choisissez un continent: \n";
        msg += "\n╔════════════════════════════════════╗\n║(1) Afrique                         ║\n║(2) Europe                          ║     \n║(3) Amérique                        ║\n║(4) Asie                            ║\n║(5) Revenir en arrière              ║\n╚════════════════════════════════════╝\n";
        return readChoice(msg, 5, player);
    }

    String askNickname() {
        String nickname;
        println("Entrez votre pseudo:");
        do {
            print(PROMPT);
            nickname = readString();
        } while(equals(nickname, ""));
        return nickname;
    }

    // Affiche caractère par caractère avec un délai le texte pour un affichage avec plus d'immersion, s'inspirant du style RPG
    void printCharByChar(String msg, int delay) {
        int sizeMsg = length(msg);
        String m = "";
        for(int i = 0; i < sizeMsg; i++) {
            m += charAt(msg,i);
            print(m);
            delay(delay);
            refreshScreenWithCoords(1,1);
        }
    }

    // Affiche le titre du jeu animé par la fonction printCharByChar
    String titleAnimated(String filepath) {
        File nom_jeu_ascii = newFile(filepath);
        String msg = "";
        while(ready(nom_jeu_ascii))
            msg += ANSI_CYAN + readLine(nom_jeu_ascii) + ANSI_RESET + "\n";
            printCharByChar(msg, 1);
        return msg;
    }

    // Affiche un écran de chargement avant le lancement de jeu avecu un conseil (pour plus de 'réalisme' dans le jeu)
    void printLoadingScreen() {
        int alea = (int)(random()*3);
        String[] tip = generateTip();

        for(int i = 0; i < 3; i++) {
            refreshScreenWithCoords(4,stringToInt(tip[1]));
            println(ANSI_CYAN + "Astuce: " + ANSI_RESET +  ANSI_BLUE_BG + ANSI_BLACK + tip[0] + ANSI_RESET);

            cursor(2,80);
            print("Chargement");
            for(int j = 0; j < 3; j++) {
                cursor(2,90+j);
                print('.');
                delay(400);
            }
        }
    }

    // Affiche le classement de tous les joueurs par ordre décroissant
    int ranking(Player player) {
        String rank = "";
        String[][] players = loadPlayers();
        int size;
        int sizePlayers = length(players, 1);
        Player p;

        // Tableaux triés dans l'ordre décroissant
        String[] names = new String[sizePlayers-1];
        int[] avgPts = new int[sizePlayers-1];

        for(int i = 1; i < sizePlayers; i++) {
            names[i-1] = players[i][0];
            avgPts[i-1] = (stringToInt(players[i][1]) + stringToInt(players[i][2]) + stringToInt(players[i][3]) + stringToInt(players[i][4]))/4;
        }

        sortPlayers(names, avgPts);

        rank += "Classement des 5 meilleurs joueurs de AsciiGuessr:\n\n";

        // Dans le cas où le nombre de joueurs dans le CSV est inférieur à 5, pour éviter d'itérer trop de fois et provoquer une erreur
        if(length(names) < 5) {
            size = length(names);
        } else {
            size = 5;
        }

        for(int i = 0; i < size; i++) {
            p = newPlayer(names[i]);
            authenticate(p);

            if((i+1) == 1) {
                rank += ANSI_YELLOW + (i+1) + ANSI_RESET;
            } else if((i+1) == 2) {
                rank += ANSI_DARKGRAY + (i+1) + ANSI_RESET;
            } else if((i+1) == 3) {    
                rank += ANSI_BROWN + (i+1) + ANSI_RESET;
            } else {
                rank += (i+1);
            }
            rank += " - ";
            if(equals(names[i], player.name)) {
                rank += ANSI_GREEN + "(Vous)" + " " + ANSI_RESET;
            }

            rank += toString(p) + "Moyenne: " + ANSI_CYAN + avgPts[i] + "pts" + ANSI_RESET + "\n\n";
        }

        rank += "\n(1) Revenir en arrière\n";

        // J'ajoute 1 pour avoir l'indice 2 qui correspond au menu du joueur
        return readChoice(rank, 1, player)+1;
    }

    // Fonction qui lance une session de jeu
    void startGame(Continent continent, Player[] players, Mode mode) {
        String[] countryRandomly;
        String msg = "";
        String msgChoiceCountry = "";
        // Nombre de pts selon la difficulté de localisation de chaque pays trouvé
        int ptsDifficulty;
        int alea;
        int country;
        int pts = 0;
        int foundCountries = 0;
        int[] drawnCountries = new int[continent.nbCountries];
        int actualIdx = 0;
        int indexContinent = getIndex(continent.name);
        // Pour le mode multi (1v1)
        int playerNumber = 0;
        
        printLoadingScreen();
        refreshScreenWithCoords(1,1);

        for(int i = 0; i < 10; i++) {       
            // C'est une condition ternaire pour vérifier si l'on affiche l'accumumulation des pts sur un seul joueur (mode solo) où si l'on affiche les pts selon le joueur (mode 1v1)
            msg = toString(continent, (mode == mode.SOLO ? pts : players[playerNumber].pts[indexContinent]), foundCountries, continent.nbCountries,10-i) + "\n" + msgChoiceCountry;

            alea = drawACountry(continent.nbCountries, drawnCountries, actualIdx);
            actualIdx++;
            countryRandomly = searchCountry(continent, alea);

            msg += "\nOù se trouve ce pays: " + countryRandomly[0];
            country = readChoice(msg, continent.nbCountries, players[playerNumber]);

            if(country == alea) {
                foundCountries++;
                ptsDifficulty = getPtsByDifficulty(countryRandomly[1]);

                // Accumuler les pts d'un seul joueur si c'est le mode solo ou de chaque joueur (au tour par tour) si c'est le mode multi (1v1)
                if(mode == mode.SOLO) {
                    pts += ptsDifficulty;
                } else {
                    players[playerNumber].pts[indexContinent] = players[playerNumber].pts[indexContinent] + ptsDifficulty;
                }
                
                msgChoiceCountry = ANSI_GREEN + "Correct ! (+" + ptsDifficulty + "pts)" + ANSI_RESET;
            } else {
                msgChoiceCountry = ANSI_RED + "Incorrect ! (+0pts)" + ANSI_RESET + " | La réponse correcte était: " + ANSI_UNDERLINE + ANSI_CYAN + alea + ANSI_RESET;
            }

            // Afficher du nom du joueur (tour par tour) pour le mode multi (1v1)
            if(mode == mode.MULTI) {
                playerNumber = playerNumber == 1 ? 0 : 1;
            } 
        }
        if(mode == Mode.SOLO) {
            setPts(players[0], indexContinent, pts);
            save(players[0]);
        }
    }

    // Fonction qui lance le mode 1v1 (en utilisant la fonction startGame qui va lancer une session de jeu pour chaque joueur)
    void multiGame(Player[] players) {
        String msg;
        int alea_continent = (int)(random()*4)+1;

        refreshScreenWithCoords(1,1);
        startGame(newContinent(alea_continent), players, Mode.MULTI);
        
        if(players[0].pts[alea_continent-1] > players[1].pts[alea_continent-1]) {
            msg = "Le vainqueur est... " + ANSI_GREEN + players[0].name +  ANSI_RESET + " !";
        } else if(players[0].pts[alea_continent-1] < players[1].pts[alea_continent-1]) {
            msg = "Le vainqueur est... " + ANSI_GREEN + players[1].name + ANSI_RESET + " !";
        } else {
            msg = "Les joueurs sont à égalité !";
        }

        cursor(3,50);
        printCharByChar(msg, 60);
        println(msg);
        println("---\n=> " + players[0].name + " | " + ANSI_BLUE + players[0].pts[alea_continent-1] + ANSI_RESET + " pts\n=> " + players[1].name + " | " + ANSI_BLUE + players[1].pts[alea_continent-1] + ANSI_RESET + " pts");
    }

    // --------------------------------------
    // Fonctions de test
    void testIsNumber() {
        assertEquals(true, isNumber("15"));
        assertEquals(false, isNumber("hello"));
        assertEquals(false, isNumber("15a"));
    }

    void testGetIndex() {
        assertEquals(0, getIndex(ContinentName.AFRICA));
        assertEquals(1, getIndex(ContinentName.EUROPE));
        assertEquals(2, getIndex(ContinentName.AMERICA));
        assertEquals(3, getIndex(ContinentName.ASIA));
    }

    void testGetPtsByDifficulty() {
        assertEquals(10, getPtsByDifficulty("1"));
        assertEquals(100, getPtsByDifficulty("2"));
        assertEquals(1000, getPtsByDifficulty("3"));
    }

    void testIsInArray() {
        int[] tab = new int[]{1,2,3};
        assertEquals(true, isInArray(1, tab));
        assertEquals(false, isInArray(5, tab));
    }

    void testDrawACountry() {
        int nbCountries = 13;
        int[] drawnCountries = new int[nbCountries];
        int actualIdx = 0;

        int country = drawACountry(nbCountries, drawnCountries, actualIdx);
        actualIdx++;

        assertEquals(true, country >= 1 && country <= nbCountries);
        // Après le tirage le nombre est placé dans le tableau des nombres déjà tirés
        assertEquals(true, isInArray(country, drawnCountries));
        
        // On retire à nouveau pour comparer s'il est différent par rapport à l'ancien nombre tiré
        country = drawACountry(nbCountries, drawnCountries, actualIdx);
        // On ne prend pas le dernier car c'est le nouveau nombre différent qu'on a tiré et qu'on met dans le tableau des nombres déjà tirés, on vérifie donc avant ce nombre
        for(int i = 0; i < 1; i++) {
            assertEquals(true, drawnCountries[i] != country);
        }
    }

    void testCountCountries() {
        assertEquals(25, countCountries(ContinentName.AFRICA));
        assertEquals(13, countCountries(ContinentName.AMERICA));
        assertEquals(17, countCountries(ContinentName.ASIA));
        assertEquals(23, countCountries(ContinentName.EUROPE));
    }

    void testSearchLig() {
        Player player = newPlayer("karim");
        Player player2 = newPlayer("test");
        assertEquals(1, searchLig(player));
        assertEquals(-1, searchLig(player2));
    }

    void testSearchCountry() {
        Continent continent = newContinent(1);
        String[] tab = new String[]{"Egypte", "1"};
        assertArrayEquals(tab, searchCountry(continent, 4));
    }

    // --------------------------------------
    // Fonction principale
    void algorithm() {
        int choice = -1;
        boolean trouve = false;
        Player connectedPlayer = null;
        // Permet de gérer le cas ou il y a plusieurs joueurs (le mode 1v1) lors d'une session de jeu
        Player[] players = null;
        
        while(!trouve) {
            // Menu principal
            if(choice == -1) {
                choice = menu(Menu.GAME, connectedPlayer);
            }

            if(choice == 1) {
                // Mode 1v1
                players = new Player[]{newPlayer(askNickname()), newPlayer(askNickname())};
                multiGame(players);
                choice = readChoice("\n(1) Recommencer une partie\n(2) Revenir au menu principal\n", 2, null);
                if(choice == 2) {
                    choice = -1;
                }
            } else if(choice == 2) { // Se connecter
                if(connectedPlayer == null) {
                    connectedPlayer = newPlayer(askNickname());
                    authenticate(connectedPlayer);
                }
                // Menu du joueur
                choice = menu(Menu.PLAYER, connectedPlayer);
                
                if(choice == 1) { // Commencer une partie
                    choice = menu(Menu.CONTINENTS, connectedPlayer);
                    if(choice != 5) {
                        players = new Player[]{connectedPlayer};
                        startGame(newContinent(choice), players, Mode.SOLO);
                    }
                    // Pour revenir au menu du joueur après que la partie soit terminé ou si l'on est revenu en arrière
                    choice = 2;
                } else if(choice == 2) { // Consulter le classement des joueurs
                    choice = ranking(connectedPlayer);
                } else if(choice == 3) { // Se déconnecter
                    connectedPlayer = null;
                    choice = -1;
                } else { // Quitter
                    trouve = true;
                }
            } else { // Quitter
                trouve = true;
            }
        }
    } 
}