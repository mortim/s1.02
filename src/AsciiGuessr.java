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

    // --------------------------------------
    // Fonctions utiles
    String getFileContent(String filepath) {
        String output = "";
        File file = newFile(filepath);
        while(ready(file))
            output += readLine(file) + "\n";
        return output;
    }

    boolean isNumber(String num) {
        int numLength = length(num);
        for(int i = 0; i < numLength; i++) {
            if(charAt(num, i) < '0' || charAt(num, i) > '9') {
                return false;
            }
        }
        return true;
    }

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

    int getNewPts(String difficulty, int pts) {
        if(equals(difficulty,"1"))
            return pts+10;
        else if(equals(difficulty,"2"))
            return pts+100;
        else
            return pts+1000;
    }

    boolean isInArray(int n, int[] tab) {
        int sizeTab = length(tab);
        for(int i = 0; i < sizeTab; i++) {
            if(tab[i] == n) {
                return true;
            }
        }
        return false;
    }

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

    void refreshScreenWithCoords(int x, int y) {
        cursor(x,y);
        clearScreen();
    }
    
    // --------------------------------------
    // Fonctions pour les fichiers CSV
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

    int searchLig(Player player) {
        CSVFile playersCSV = loadCSV(RESSOURCES_PATH + "csv/Joueurs.csv");
        int lig = rowCount(playersCSV);
        int col = columnCount(playersCSV);
        
        for(int i = 1; i < lig; i++) {
            if(equals(getCell(playersCSV, i, 0), player.name)) {
                return i;
            }
        }
        return -1;
    }

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

    boolean save(Player player) {
        CSVFile playersCSV = loadCSV(RESSOURCES_PATH + "csv/Joueurs.csv");
        String[][] players;
        int lig = rowCount(playersCSV);
        int col = columnCount(playersCSV);
        int pos = searchLig(player);
        boolean existNickname = true;

        if(pos == -1) {
            lig++;
            pos = lig-1;
            existNickname = false;
        } 

        players = new String[lig][col];

        for(int i = 0; i < lig-1; i++) {
            for(int j = 0; j < col; j++) {
                players[i][j] = getCell(playersCSV, i, j);
            }
        }

        players[pos][0] = player.name;
        for(int i = 1; i <= 4; i++) {
            players[pos][i] = "" + player.pts[i-1];
        }
 
        saveCSV(players, RESSOURCES_PATH + "csv/Joueurs.csv");
        return existNickname;
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
        player.pts = new int[]{0,0,0,0};
        return player;
    }

    void setPts(Player player, int idxContinent, int pts) {
        if(pts > player.pts[idxContinent]) {
            player.pts[idxContinent] = pts;
        }
    }

    void authenticate(Player player) {
        String[][] players;
        int ligPlayer;

        ligPlayer = searchLig(player);
        if(ligPlayer == -1) {
            println("Vous venez de créer un nouveau compte...");
            delay(1000);
            save(player);
        } else {
            players = loadPlayers();
            for(int i = 0; i < 4; i++) {
                player.pts[i] = stringToInt(players[ligPlayer][i+1]);
            }
        }
    }

    String toString(Player player) {
        int sizePts = length(player.pts);
        String profile = player.name + " => ";
        for(int i = 0; i < sizePts; i++) {
            profile += toString(toEnum(i+1)) + " " + ANSI_BLUE + player.pts[i] + "pts" + ANSI_RESET + " | ";
        }
        return profile;
    }

    // --------------------------------------
    // Fonctions d'affichage / saisie
    int menu(Menu type, Player player) {
        String msg = "";
        refreshScreenWithCoords(1,1);
        if(type == Menu.GAME) {
            msg = titleAnimated(RESSOURCES_PATH + "ascii/NomJeu.txt") + "\n\n(1) Se défier en 1v1 (compte invité)\n(2) Se connecter\n(3) Quitter\n";
            return readChoice(msg, 3, player);
        } else if(type == Menu.PLAYER) {
            msg = "Votre score actuel: " + toString(player) + "\n\n(1) Commencer une partie\n(2) Classement des joueurs\n(3) Se déconnecter\n(4) Quitter\n";
            return readChoice(msg, 4, player);
        }
        msg = "Choisissez un continent: \n\n(1) Afrique\n(2) Europe\n(3) Amérique\n(4) Asie\n(5) Revenir en arrière\n";
        return readChoice(msg, 5, player);
    }

    String askNickname() {
        String nickname;
        do {
            print(PROMPT);
            nickname = readString();
        } while(equals(nickname, ""));
        return nickname;
    }

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

    String titleAnimated(String filepath) {
        File nom_jeu_ascii = newFile(filepath);
        String msg = "";
        while(ready(nom_jeu_ascii))
            msg += ANSI_CYAN + readLine(nom_jeu_ascii) + ANSI_RESET + "\n";
            printCharByChar(msg, 2);
        return msg;
    }

    void printLoadingScreen(String tip) {
        for(int i = 0; i < 3; i++) {
            refreshScreenWithCoords(1,1);
            cursor(4,20);
            println(ANSI_CYAN + "Astuce: " + ANSI_RESET +  ANSI_BLUE_BG + ANSI_BLACK + tip + ANSI_RESET);
            cursor(2,80);
            print("Chargement");
            for(int j = 0; j < 3; j++) {
                cursor(2,90+j);
                print('.');
                delay(500);
            }
        }
    }

    void startGame(Continent continent, Player player) {
        String[] countryRandomly;
        String msg = "";
        String msgChoiceCountry = "";
        int ptsBase = 0;
        int alea;
        int country;
        int pts = 0;
        int foundCountries = 0;
        int[] drawnCountries = new int[continent.nbCountries];
        int actualIdx = 0;

        printLoadingScreen("Selon la difficulté du pays, vous pouvez gagner de 10 à 1000pts (Facile : 10pts, Moyen : 100pts, Difficile: 1000pts)");
        refreshScreenWithCoords(1,1);
 
        for(int i = 0; i < 10; i++) {
            msg = toString(continent, pts, foundCountries, continent.nbCountries,10-i) + "\n" + msgChoiceCountry;
            alea = drawACountry(continent.nbCountries, drawnCountries, actualIdx);
            actualIdx++;
            countryRandomly = searchCountry(continent, alea);
            msg += "\nOù se trouve ce pays: " + countryRandomly[0];
            country = readChoice(msg, continent.nbCountries, player);
                
            if(country == alea) {
                foundCountries++;
                ptsBase = pts;
                pts = getNewPts(countryRandomly[1], pts);
                msgChoiceCountry = ANSI_GREEN + "Correct ! (+" + (pts-ptsBase) + "pts)" + ANSI_RESET;
            } else {
                msgChoiceCountry = ANSI_RED + "Incorrect ! (+0pts)" + ANSI_RESET;
            }
        }
        if(player != null) {
            setPts(player, getIndex(continent.name), pts);
            save(player);
        }
    }

    // --------------------------------------
    // Fonctions de test
    // ...

    // --------------------------------------
    // Fonction principale
    void algorithm() {
        int choice = -1;
        boolean trouve = false;
        Player player = null;
        Player player2 = null;

        while(!trouve) {
            // Menu principal
            if(choice == -1) {
                choice = menu(Menu.GAME, player);
            }

            if(choice == 1) {
                // Mode 1v1...
                trouve = true;
            } else if(choice == 2) { // Se connecter
                if(player == null) {
                    println("Entrez votre pseudo:");
                    player = newPlayer(askNickname());
                    authenticate(player);
                }
                // Menu du joueur
                choice = menu(Menu.PLAYER, player);
                
                if(choice == 1) { // Commencer une partie
                    choice = menu(Menu.CONTINENTS, player);
                    if(choice != 5) {
                        startGame(newContinent(choice), player);
                    }
                    // Pour revenir au menu du joueur après que la partie soit terminé ou si l'on est revenu en arrière
                    choice = 2;
                } else if(choice == 2) { // Consulter le classement des joueurs
                    // ...
                } else if(choice == 3) { // Se déconnecter
                    player = null;
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

// // TODO
// Répartition des tâches:
// - Trouver des astuces (N)
// - Mode 1v1 (K)
// - Système de classement, on classe les 10 meilleurs joueurs selon le nbr de pts (on charge le csv) (N)
//  - Afficher la moyenne des pts de chaque continent (N)
// - Mode quiz sur les drapeaux (https://github.com/maugier/ascii-flags) (K/N)
// - Commenter le code
// - Coder les fonctions de test
// - Colorier le numéro de pays choisi en bleu, en vert le numéro de pays trouvé ou rouge (s'il n'a pas été trouvé)
// - Optimiser la fonction save et searchLig avec loadPlayers
