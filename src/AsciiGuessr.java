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

    String[] search(Continent continent, int num) {
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
        players[pos][1] = "" + player.pts;
        players[pos][2] = "" + player.intro;
  
        saveCSV(players, RESSOURCES_PATH + "csv/Joueurs.csv");
        return existNickname;
    }

    int update(String gotPts, int pts) {
        if(equals(gotPts,"1"))
            return pts+10;
        else if(equals(gotPts,"2"))
            return pts+100;
        else
            return pts+1000;
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

    // --------------------------------------
    // Fonctions pour la classe 'Continent'
    Continent newContinent(int choice) {
        Continent continent = new Continent();
        if(choice == 1) {
            continent.name = ContinentName.EUROPE;
        } else if(choice == 2) {
            continent.name = ContinentName.AFRICA;
        } else if(choice == 3) {
            continent.name = ContinentName.AMERICA;
        } else if(choice == 4) {
            continent.name = ContinentName.ASIA;
        }
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
        player.pts = 0;
        player.intro = false;
        return player;
    }

    String toString(Player player) {
        return player.name + " - " + player.pts + "pts\n";
    }

    // --------------------------------------
    // Fonctions d'affichage / saisie
    int gameModeMenu(Player player) {
        refreshScreenWithCoords(1,1);
        String msg = "Votre score actuel: " + toString(player) + "---\n(1) Mode solo\n(2) Mode 1v1\n(3) Consulter ses records\n(4) Quitter\n";
        return readChoice(msg, 4, player);
    }

    int continentsMenu(Player player) {
        refreshScreenWithCoords(1,1);
        String msg = "Choisissez un continent: \n\n(1) Europe\n(2) Afrique\n(3) Amérique\n(4) Asie\n(5) Revenir en arrière\n";
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
            player.pts = stringToInt(players[ligPlayer][1]);
            player.intro = players[ligPlayer][2] == "true";
        }
    }

    void startGame(Continent continent, Player player) {
        String[] countryRandomly;
        String msg = "";
        int alea;
        int country;
        int pts = 0;
        int foundCountries = 0;

        for(int i = 0; i < 10; i++) {
            msg = toString(continent, pts, foundCountries, continent.nbCountries);
            alea = (int)(random()*continent.nbCountries)+1;
            countryRandomly = search(continent, alea);
            msg += "\nOù se trouve ce pays: " + countryRandomly[0];
            country = readChoice(msg, continent.nbCountries, player);
            
            if(country == alea) {
                foundCountries++;
                pts = update(countryRandomly[1], pts);
            }
        }
        if(pts > player.pts) {
            player.pts = pts;
            save(player);
        }
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

        println("Bienvenue dans AsciiGuessr ! Le jeu qui va vous faire aimer la géographie.");
        println("Si vous vous sentez prêt à tenter votre chance, entrez votre pseudo: \n");
        
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
                
            } else if(choice == 3) {
        
            } else {
                trouve = true;
            }
        }
    }

}

// // TODO
// - Afficher les nbr de pts gagnés à chaque pays trouvé (10,100,1000)
// - Eviter de tirer 2 fois le même pays
// - Mode histoire (Introduction du mode solo pour un nouveau joueur avec une histoire en parcourant chaque continent puis les autres fois le joueur aura le choix du continent)
// - Mode 1v1 (Même principe que le mode solo pour les pts mais sur 2 continents aléatoires)
// - Système de classement, on classe les 10 meilleurs joueurs selon le nbr de pts (on charge le csv)
// - (optionnel: cinématique du joueur lorsqu'il débute une partie)