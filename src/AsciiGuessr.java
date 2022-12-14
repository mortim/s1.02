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
    // Constantes/Variables globales
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

    int getContinentIndex(ContinentName name) {
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
            print(player.name + PROMPT);
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
            player.intro = equals(players[ligPlayer][5],"true");
        }
    }

    int update(String difficulty, int pts) {
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
        int lig, col;
        int additionalCell = 0;
        int pos = searchLig(player);
        boolean existNickname = true;

        if(pos == -1) {
            additionalCell++;
            existNickname = false;
        }
        
        lig = rowCount(playersCSV) + additionalCell;
        col = columnCount(playersCSV);
        players = new String[lig][col];

        if(pos == -1) {
            pos = lig-1;
        }

        for(int i = 0; i < lig-additionalCell; i++) {
            for(int j = 0; j < col; j++) {
                players[i][j] = getCell(playersCSV, i, j);
            }
        }

        players[pos][0] = player.name;
        for(int i = 1; i <= 4; i++) {
            players[pos][i] = "" + player.pts[i-1];
        }
        players[pos][5] = "" + player.intro;
  
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

    String toString(Continent continent, int pts, int foundCountries, int nbCountries) {
        String score = "\n      " + foundCountries + "/10 pays trouvés |  " + pts + " pts";
        String asciiContinent = "                             " + ANSI_BLUE + toString(continent.name) + "\n\n" + continent.ascii + ANSI_RESET;
        return score + asciiContinent;
    }

    // --------------------------------------
    // Fonctions pour la classe 'Player'
    Player newPlayer(String nickname) {
        Player player = new Player();
        player.name = nickname;
        player.pts = new int[]{0,0,0,0};
        player.intro = false;
        return player;
    }

    void setPts(Player player, int idxContinent, int pts) {
        if(pts > player.pts[idxContinent]) {
            player.pts[idxContinent] = pts;
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
    int gameModeMenu(Player player) {
        refreshScreenWithCoords(1,1);
        String msg = "Votre score actuel: " + toString(player) + "\n(1) Mode solo\n(2) Mode 1v1\n(3) Classement des joueurs\n(4) Quitter\n";
        return readChoice(msg, 4, player);
    }

    int continentsMenu(Player player) {
        refreshScreenWithCoords(1,1);
        String msg = "Choisissez un continent: \n\n(1) Afrique\n(2) Europe\n(3) Amérique\n(4) Asie\n(5) Revenir en arrière\n";
        return readChoice(msg, 5, player);
    }

    String getNickname() {
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

    void printTitleGameAnimated(String filepath) {
        File nom_jeu_ascii = newFile(filepath);
        String msg = "";
        while(ready(nom_jeu_ascii))
            msg += readLine(nom_jeu_ascii) + "\n";
            printCharByChar(msg, 2);
        println(msg);
    }

    boolean askTutorial(Player player) {
        String intro_choice;
        printCharByChar("Salut ! Mon nom est Mappy, heureux de te rencontrer " + player.name + " !", 60);
        printCharByChar("Ici tu es sur le tutoriel d'AsciiGuessr, une fois validé ce tutoriel vous pourrez jouer directement", 60);
        printCharByChar("Voulez-vous passer ce tutoriel ? (oui/non):", 50);
        print("Voulez-vous passer ce tutoriel ? (oui/non): ");
        intro_choice = readString();
        while(!equals(intro_choice,"oui") && !equals(intro_choice, "non")) {
            println("Veuillez entrer oui ou non.");
            print(player.name + PROMPT);
            intro_choice = readString();     
        }
        return equals(intro_choice,"oui");
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

        if(!player.intro) {
            boolean tutorial = askTutorial(player);
            player.intro = true;
            save(player);
            if(tutorial) {
                // Tutoriel...
            }
        } 

        for(int i = 0; i < 10; i++) {
            msg = toString(continent, pts, foundCountries, continent.nbCountries) + "\n" + msgChoiceCountry;
            alea = drawACountry(continent.nbCountries, drawnCountries, actualIdx);
            actualIdx++;
            countryRandomly = searchCountry(continent, alea);
            msg += "\nOù se trouve ce pays: " + countryRandomly[0];
            country = readChoice(msg, continent.nbCountries, player);
                
            if(country == alea) {
                foundCountries++;
                ptsBase = pts;
                pts = update(countryRandomly[1], pts);
                msgChoiceCountry = ANSI_GREEN + "Correct ! (+" + (pts-ptsBase) + "pts)" + ANSI_RESET;
            } else {
                msgChoiceCountry = ANSI_RED + "Incorrect ! (+0pts)" + ANSI_RESET;
            }
        }
        setPts(player, getContinentIndex(continent.name), pts);
        save(player);
    }

    // --------------------------------------
    // Fonctions de test
    // ...

    // --------------------------------------
    // Fonction principale
    void algorithm() {
        int choice;
        boolean trouve = false;
        Continent continent;
        Player player;

        printTitleGameAnimated(RESSOURCES_PATH + "ascii/NomJeu.txt");
        println("\nSi vous vous sentez prêt à tenter votre chance, entrez votre pseudo: \n");
        
        player = newPlayer(getNickname());
        authenticate(player);
        
        while(!trouve) {
            choice = gameModeMenu(player);
            if(choice == 1) {
                choice = continentsMenu(player);
                if(choice != 5) {
                    continent = newContinent(choice);
                    startGame(continent, player);
                }
            } else if(choice == 2) {
                // Mode 1v1...
            } else if(choice == 3) {
                // Consulter le classement des joueurs...
            } else {
                trouve = true;
            }
        }
    }
}

// Commit
// - Correction du bug d'affichage des pts par continent (Amérique/Asie) + refactor
// - Ajout du ficihier .gitignore pour cacher le dossier 'classes' (fichiers après compilation du projet)
// - Modification du compile.sh pour créer le dossier 'classes' (s'il n'existe pas)

// // TODO
// - Tutoriel pour guider le joueur
// - Mode 1v1
// - Système de classement, on classe les 10 meilleurs joueurs selon le nbr de pts (on charge le csv)
//  - Afficher la moyenne des pts de chaque continent
// - Commenter le code