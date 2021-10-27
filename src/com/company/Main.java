package com.company;
import javax.xml.crypto.Data;
import java.io.*;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        // write your code here

        System.out.print("\uD83D\uDCD5 " +
                "Witaj w wirtualnej bibliotece\n\n Podaj numer karty bibliotecznej:");

        Scanner myScanner = new Scanner(System.in);
        String card = myScanner.nextLine();
        User u = new User(card);
        if(!u.isEmpty()){
            Menu m = new Menu(u);
            m.display();
        }
        else{
            System.out.print("Nie znaleziono danego użytkownika");
        }
    }
}