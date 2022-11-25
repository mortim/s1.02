import extensions.File;

class AsciiGuessr extends Program {
    // Constantes globales
    String RESSOURCES_PATH = "../ressources/";
    String CHOICE_PROMPT = "> ";

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
            print(CHOICE_PROMPT);
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

    // Fonctions pour les enumérations
    String toStringEnum(ContinentName name) {
        if(name == ContinentName.EUROPE)
            return "Europe";
        else if(name == ContinentName.AFRIQUE)
            return "Afrique";
        else if(name == ContinentName.ASIE)
            return "Asie";
        else
            return "Amerique";
    }

    // Constructeurs de classe
    Continent newContinent(ContinentName name) {
        Continent continent = new Continent();
        continent.name = name;
        continent.ascii = getFileContent(RESSOURCES_PATH + toStringEnum(continent.name));
        return continent;
    }

    // Fonctions pour la classe 'Continent'
    

    // Fonctions de test
    // ...
    
    // Fonction principale
    void algorithm() {
        int choice;

        println("Bienvenue dans AsciiGuessr ! Le jeu qui va vous faire aimer la géographie.");
        println("Si vous vous sentez prêt à tenter votre chance, choisissez parmi ces choix (1,2,3): \n");

        println("(1) Mode solo\n(2) Mode 1v1\n(3) Consulter ses records\n");
        choice = readChoice(3);

        if(choice == 1) {
            cursor(1,1);
            clearScreen();
            println("Niveau de difficulté: \n");

            println("(1) Simple (12 min)\n(2) Moyen (7 min)\n(3) Difficle (5 min)\n");
            choice = readChoice(3);
        }
    }

}