import extensions.File;

class AsciiGuessr extends Program {
    // Constantes globales
    String RESSOURCES_PATH = "../ressources/";
    String PROMPT = "> ";
    String NICKNAME = "";
    String ANSI_END  = "\033[0m";

    // Fonctions utiles
    String getFileContent(String filepath) {
        String output = "";
        File file = newFile(filepath);
        while(ready(file))
            output += readLine(file) + "\n";
        return output;
    }

    int readChoice(int nbChoice) {
        char choice = '1';
        String input;
        boolean goodChoice = false;

        while(!goodChoice) {
            print(NICKNAME + PROMPT);
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

    void refreshScreenCoords(int x, int y) {
        cursor(x,y);
        clearScreen();
    }

    // Fonctions pour les enumérations
    String toStringEnum(ContinentName name) {
        if(name == ContinentName.EUROPE)
            return "Europe";
        else if(name == ContinentName.AFRICA)
            return "Afrique";
        else if(name == ContinentName.ASIA)
            return "Asie";
        else
            return "Amerique";
    }

    // Fonctions pour la classe 'Level'
    Level newLevel(int choice) {
        Level level = new Level();
        if(choice == 1) {
            level.name = LevelName.SIMPLE;
            level.time = 12;
        }
        if(choice == 2) {
            level.name = LevelName.MEDIUM;
            level.time = 7;
        } 
        if(choice == 3) {
            level.name = LevelName.HARD;
            level.time = 5;
        }
        return level;
    }

    // Fonctions pour la classe 'Continent'
    Continent newContinent(int choice) {
        Continent continent = new Continent();
        if(choice == 1)
            continent.name = ContinentName.EUROPE;
        if(choice == 2)
            continent.name = ContinentName.AFRICA;
        if(choice == 3)
            continent.name = ContinentName.AMERICA;
        if(choice == 4)
            continent.name = ContinentName.ASIA;
        continent.ascii = getFileContent(RESSOURCES_PATH + toStringEnum(continent.name) + ".txt");
        return continent;
    }

    String toStringContinent(Continent continent) {
        return toStringEnum(continent.name) + "\n\n" + continent.ascii;
    }

    // Fonctions d'affichage
    String updateTimer(int min, int sec) {
        if(min < 10 && sec < 10)
            return ("0" + min) + ":" + ("0" + sec);
        else if(min < 10)
            return ("0" + min) + ":" + sec;
        else if(sec < 10)
            return min + ":" + ("0" + sec);
        else
            return min + ":" + sec;
    }

    void startGameSession(Continent continent, Level level) {
        String timer = "";
        for(int min = level.time-1; min >= 0; min--) {
            for(int sec = 59; sec >= 0; sec--) {
                cursor(2,50);
                println(ANSI_BLUE + toStringContinent(continent) + ANSI_END);
                print(updateTimer(min,sec));
                delay(1000);
                clearScreen();
            }
        }
    }
    
    // Fonctions de test
    // ...

    // Fonction principale
    void algorithm() {
        int choice;
        Level level;
        Continent continent;
 
        println("Bienvenue dans AsciiGuessr ! Le jeu qui va vous faire aimer la géographie.");
        println("Si vous vous sentez prêt à tenter votre chance, entrez votre pseudo: \n");
        
        do {
            print(PROMPT);
            NICKNAME = readString();
        } while(equals(NICKNAME, ""));

        refreshScreenCoords(1,1);
        println("(1) Mode solo\n(2) Mode 1v1\n(3) Consulter ses records\n");
        choice = readChoice(3);

        if(choice == 1) {
            refreshScreenCoords(1,1);
            println("Niveau de difficulté: \n\n(1) Simple (12 min)\n(2) Moyen (7 min)\n(3) Difficle (5 min)\n");
            level = newLevel(readChoice(3));

            refreshScreenCoords(1,1);
            println("Choisissez un continent: \n\n(1) Europe\n(2) Afrique\n(3) Amérique\n(4) Asie\n");
            continent = newContinent(readChoice(4));

            startGameSession(continent, level);
        }
    }

}