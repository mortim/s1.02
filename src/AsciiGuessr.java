import extensions.File;
import extensions.CSVFile;

class AsciiGuessr extends Program {
    // Constantes/Variables globales
    String RESSOURCES_PATH = "../ressources/";
    String PROMPT = "> ";

    // --------------------------------------
    // Fonctions utiles
    String getFileContent(String filepath) {
        String output = "";
        File file = newFile(filepath);
        while(ready(file))
            output += readLine(file) + "\n";
        return output;
    }

    int readChoice(String msg, int nbChoice, Player player) {
        char choice = '1';
        String input;
        boolean goodChoice = false;

        while(!goodChoice) {
            println(msg);
            print(player.name + PROMPT);
            input = readString();
    
            if(!equals(input, "")) {
                choice = charAt(input, 0);
                if(choice >= '1' && choice <= ('0' + nbChoice))
                    goodChoice = true;
                else {
                    println("La saisie est incorrecte.");
                    delay(500);
                }
            } else {
                println("Vous n'avez pas entré de saisie utilisateur.");
                delay(500);
            }
            refreshScreenWithCoords(1,1);
        }
        return (choice - '0');
    }

    void refreshScreenWithCoords(int x, int y) {
        cursor(x,y);
        clearScreen();
    }

    // --------------------------------------
    // Fonctions pour les fichiers CSV
    String[] loadCountries(ContinentName name) {
        CSVFile countries = loadCSV(RESSOURCES_PATH + "csv/Pays.csv");
        int lig = rowCount(countries);
        int col = columnCount(countries);
        String[] tmp_result = new String[lig];
        String[] result;
        int k = 0;
        for(int i = 0; i < lig; i++) {
            for(int j = 0; j < col; j++) {
                if(equals(getCell(countries, i, j), toString(name))) {
                    tmp_result[k] = getCell(countries, i, j+1);
                    k++;
                }
            }
        }
        result = new String[k];
        for(int i = 0; i < k; i++) {
            result[i] = tmp_result[i];
        }
        return result;
    }

    int search(Player player) {
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

    String search(Continent continent, int num) {
        CSVFile countries = loadCSV(RESSOURCES_PATH + "csv/Pays.csv");
        int lig = rowCount(countries);
        int col = columnCount(countries);
        for(int i = 1; i < lig; i++) {
            if(equals(getCell(countries, i, 0),toString(continent.name)) && equals(getCell(countries, i, 2), ""+num)) {
                return getCell(countries, i, 1);
            }
        }
        return "";
    }

    boolean save(Player player) {
        CSVFile playersCSV = loadCSV(RESSOURCES_PATH + "csv/Joueurs.csv");
        String[][] players;
        int lig, col;
        int additionalCell = 0;
        int pos = search(player);
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
        continent.countries = loadCountries(continent.name);
        continent.ascii = getFileContent(RESSOURCES_PATH + "ascii/" + toString(continent.name) + ".txt");
        return continent;
    }

    String toString(Continent continent) {
        return toString(continent.name) + "\n\n" + continent.ascii;
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
        String msg = "(1) Mode solo\n(2) Mode 1v1\n(3) Consulter ses records\n(4) Quitter\n";
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

    String map(Continent continent, int pts, int foundCountries, int nbCountries) {
        String mapStr = "\n";
        mapStr = "      " + foundCountries + "/" + nbCountries + " pays trouvés |  ";
        mapStr += pts + " pts";
        mapStr += "                             " + ANSI_BLUE + toString(continent) + ANSI_RESET;
        return mapStr;
    }

    void startGame(Continent continent, Player player) {
        String msg = "";
        int alea;
        int nbCountries = length(continent.countries);
        int country;
        int pts = 0;
        int foundCountries = 0;

        for(int i = 0; i < 10; i++) {
            msg = map(continent, pts, foundCountries, nbCountries);
            alea = (int)(random()*nbCountries);
            msg += "\nOù se trouve ce pays: " + search(continent, alea);
            country = readChoice(msg, nbCountries, player);
            if(country == alea) {
                foundCountries++;
            }
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
        boolean existNickname;
        Continent continent;
        Player player;

        println("Bienvenue dans AsciiGuessr ! Le jeu qui va vous faire aimer la géographie.");
        println("Si vous vous sentez prêt à tenter votre chance, entrez votre pseudo: \n");
        
        player = newPlayer(getNickname());
        existNickname = save(player);

        if(!existNickname) {
            println("Vous venez de créer un nouveau compte...");
            delay(1000);
        }

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