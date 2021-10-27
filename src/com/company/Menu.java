package com.company;

import java.io.IOException;
import java.util.Scanner;

interface Action {
    void dispatch();
}

abstract class Screen{
    String description;

    public static void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    public void display(){
        clearScreen();
        System.out.println(description);
    }
}

class SelectScreen extends Screen{

    String[] optionLabels;
    Action[] options;

    public SelectScreen(String description, String[] optionLabels, Action[] options){
        if(options.length == optionLabels.length)
        {
            this.description = description;
            this.optionLabels = optionLabels;
            this.options = options;
        }
    }

    public void select(int index){
        options[index].dispatch();
    }

    @Override
    public void display(){
        super.display();
        for(Integer i=0; i<optionLabels.length; i++){
            System.out.println(String.format("%s - %s",i,optionLabels[i]));
        }
        Scanner myScanner = new Scanner(System.in);
        Integer choice = Integer.parseInt(myScanner.nextLine());
        while(choice >= options.length || choice < 0)
        {
            System.out.println("Nie ma takiej opcji\n\n prosze wybrac poprawnie:");
            choice = Integer.parseInt(myScanner.nextLine());
        }
        options[choice].dispatch();
    }

}

class InputScreen extends Screen{

    Action onEnter;

    public InputScreen(String description, Action onEnter){
        this.description = description;
        this.onEnter = onEnter;
    }

    @Override
    public void display(){
        super.display();
        onEnter.dispatch();
    }

}

public class Menu {
    User reader;

    public Menu(User user){
        this.reader = user;
    }

    public SelectScreen homeScreen(){
        return new SelectScreen(
                String.format("Witaj użytkowniku %s \n\n Aktualnie wypożyczone książki%s", reader, Database.getUserBorrows((reader.card).toString())),
                new String[] {
                    "Wyjdź",
                    "Wypożycz książkę",
                    "Oddaj ksiażkę"
                },
                new Action[] {
                    new Action() {public void dispatch(){System.exit(0);}},
                    new Action() {public void dispatch(){searchScreen().display();}},
                    new Action() {public void dispatch(){returnScreen().display();}}
                }
        );
    }

    public InputScreen searchScreen(){
        return new InputScreen(
                "Podaj tytuł książki: ",
                new Action(){public void dispatch(){
                    Scanner myScanner = new Scanner(System.in);
                    String key = myScanner.nextLine();
                    Book searchResult = Database.getBook(key);
                    if(!searchResult.isEmpty()){
                        searchResultScreen(searchResult).display();
                    }
                    else{
                        notFoundScreen().display();
                    }
                }}
        );
    }

    public SelectScreen notFoundScreen(){
        return new SelectScreen(
                String.format("Nie posiadamy aktualnie książki na stanie"),
                new String[] {
                        "Wróć do menu głównego",
                        "Wyszukaj ponownie"
                },
                new Action[] {
                        new Action() {public void dispatch(){homeScreen().display();}},
                        new Action() {public void dispatch(){searchScreen().display();}},
                }
        );
    }

    public SelectScreen searchResultScreen(Book result){
        return new SelectScreen(
                String.format("Książka:\n %s",result),
                new String[] {
                        "Wróć do menu głównego",
                        "Wypożycz książkę"
                },
                new Action[] {
                        new Action() {public void dispatch(){homeScreen().display();}},
                        new Action() {public void dispatch(){Database.borrowBook(result,reader);homeScreen().display();}},
                }
        );
    }

    public InputScreen returnScreen(){
        return new InputScreen(
                String.format("Aktualnie wypożyczone książki%s \n\n\nWpisz numer książki którą chciałbyś/abyś zwrócić: ", Database.getUserBorrows((reader.card).toString())),
                new Action(){public void dispatch(){
                    Scanner myScanner = new Scanner(System.in);
                    String bookId = myScanner.nextLine();
                    Database.returnBook(bookId,reader.card.toString());
                    homeScreen().display();
                }}
        );
    }

    public void display(){
        homeScreen().display();
    }
}
