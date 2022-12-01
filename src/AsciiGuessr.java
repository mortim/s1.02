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

    int readChoice(int nbChoice, Player player) {
        char choice = '1';
        String input;
        boolean goodChoice = false;

        while(!goodChoice) {
            print(player.name + PROMPT);
            input = readString();
    
            if(!equals(input, "")) {
                choice = charAt(input, 0);
                if(choice >= '1' && choice <= ('0' + nbChoice))
                    goodChoice = true;
                else
                    println("La saisie est incorrecte.");
            } else
                println("Vous n'avez pas entré de saisie utilisateur.");
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

    boolean savePlayer(Player player) {
        CSVFile playersCSV = loadCSV(RESSOURCES_PATH + "csv/Joueurs.csv");
        int lig = rowCount(playersCSV)+1;
        int col = columnCount(playersCSV);
        String[][] players = new String[lig][col];

        for(int i = 0; i < lig-1; i++) {
            for(int j = 0; j < col; j++) {
                if(equals(getCell(playersCSV, i, j), player.name)) {
                    return true;
                } else {
                    players[i][j] = getCell(playersCSV, i, j);
                }
            }
        }
        players[lig-1][0] = player.name;
        players[lig-1][1] = "" + player.pts;
        players[lig-1][2] = "" + player.intro;
        saveCSV(players, RESSOURCES_PATH + "csv/Joueurs.csv");
        return false;
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
        println("(1) Mode solo\n(2) Mode 1v1\n(3) Consulter ses records\n(4) Quitter\n");
        return readChoice(4, player);
    }

    int continentsMenu(Player player) {
        refreshScreenWithCoords(1,1);
        println("Choisissez un continent: \n\n(1) Europe\n(2) Afrique\n(3) Amérique\n(4) Asie\n(5) Revenir en arrière\n");
        return readChoice(5, player);
    }

    String getNickname() {
        String nickname;
        do {
            print(PROMPT);
            nickname = readString();
        } while(equals(nickname, ""));
        return nickname;
    }

    // --------------------------------------
    // Fonctions de test
    // ...

    // --------------------------------------
    // Fonction principale
    void algorithm() {
        int choice;
        boolean trouve = false;
        boolean badNickname;
        Continent continent;
        Player player;

        println("Bienvenue dans AsciiGuessr ! Le jeu qui va vous faire aimer la géographie.");
        println("Si vous vous sentez prêt à tenter votre chance, entrez votre pseudo: \n");
        
        do {
            player = newPlayer(getNickname());
            badNickname = savePlayer(player);
            if(badNickname) {
                println("Cet utilisateur existe déjà !");
            }
        } while(badNickname);
       
        while(!trouve) {
            choice = gameModeMenu(player);
            if(choice == 1) {
                choice = continentsMenu(player);
                if(choice != 5) {

                    continent = newContinent(choice);
                    cursor(2,75);
                    print("0/"+length(continent.countries) + " pays trouvés");
                    cursor(2,100);
                    print("0pts");
                    cursor(2,50);
                    println(ANSI_BLUE + toString(continent) + ANSI_RESET);
                    for(int i = 0; i < 20; i++) {

                    }
                    print("Choisissez un pays: ");
                    choice = readInt();

                }
            } else if(choice == 2) {
                
            } else if(choice == 3) {
        
            } else {
                trouve = true;
            }
        }
    }

    // - Implémententation la commande 'quitter' & 'revenir en arrière'   
    // - Enregistrement des pseudos dans le csv (gestion de la redondance)
    // - Factorisation du void algorithm()

}