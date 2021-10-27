package com.company;
import java.io.*;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Scanner; // Import the Scanner class to read text files
import java.time.temporal.ChronoUnit;

public class Database {

    public static Boolean ValidUser(String card){
        try {
            File myObj = new File("readers.csv");
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine().split(",")[0];
                if(data.equals(card))
                    return true;
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
            return false;
        }

        return false;
    }

    public static User getUserInfo(String card) {
        try {
            File myObj = new File("readers.csv");
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String[] data = myReader.nextLine().split(",");
                if(data[0].equals(card))
                    return new User(Integer.parseInt(data[0]),data[1],data[2]);
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
        }
        return new User();
    }

    static boolean isInteger(String str) {
        if (str == null) {
            return false;
        }
        int length = str.length();
        if (length == 0) {
            return false;
        }
        int i = 0;
        if (str.charAt(0) == '-') {
            if (length == 1) {
                return false;
            }
            i = 1;
        }
        for (; i < length; i++) {
            char c = str.charAt(i);
            if (c < '0' || c > '9') {
                return false;
            }
        }
        return true;
    }

    public static Book getBook(String key){
        try{
            key = key.toLowerCase();
            BufferedReader csvReader = new BufferedReader(new FileReader("books.csv"));
            String row;
            while ((row = csvReader.readLine()) != null) {
                String[] data = row.split(",");
                if(isInteger(data[2]))
                    if(Integer.parseInt(data[2]) > 0 && (data[0].toLowerCase().contains(key) || data[1].toLowerCase().contains(key)))
                        return new Book(data[0],data[1],data[4]);
            }
            csvReader.close();

        }catch(IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        return new Book();
    }

    public static Book getBookById(String bookId){
        try{
            BufferedReader csvReader = new BufferedReader(new FileReader("books.csv"));
            String row;
            while ((row = csvReader.readLine()) != null) {
                String[] data = row.split(",");
                if(data[4].equals(bookId))
                    return new Book(data[0],data[1],data[4]);
            }
            csvReader.close();

        }catch(IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        return new Book();
    }

    public static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static String getCurrentDate(){return LocalDate.now().format(formatter);}

    public static void borrowBook(Book b, User u){

        try {
            FileWriter myWriter = new FileWriter("borrows.csv", true);
            BufferedWriter bw = new BufferedWriter(myWriter);
            bw.newLine();
            bw.write(String.format("%s,%s,%s,%s",u.card,b.id,getCurrentDate(),"no"));
            bw.close();
            myWriter.close();


            //Wczytaj plik do pamięci i zamień w nim ilość ksiażek (wypożyczonej)
            ArrayList<String> file = new ArrayList<String>();

            File myObj = new File("books.csv");
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String line = myReader.nextLine();
                String[] cells = line.split(",");
                if(cells[4].equals(b.id)){
                    String available = String.valueOf(Integer.parseInt(cells[2])-1);
                    line = String.join(",",b.name,b.author,available,cells[3],b.id);
                }
                file.add(line);
            }
            myReader.close();

            //przepisz z pamięci plik na dysk
            myWriter = new FileWriter("books.csv");
            bw = new BufferedWriter(myWriter);
            for (String line : file){
                bw.write(line);
                bw.newLine();
            }
            bw.close();
            myWriter.close();

            System.out.println("Udało się wypozyczyć książkę");
        } catch (IOException e) {
            System.out.println("Nie udało się wypożyczyć książki");
        }
    }

    public static void returnBook(String bookId, String card){

        try {

            //Wczytaj plik do pamięci i zamień w nim ilość ksiażek (wypożyczonej)
            ArrayList<String> file = new ArrayList<String>();


            Boolean check = false;
            File myObj = new File("borrows.csv");
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String line = myReader.nextLine();
                if(!line.equals("")){
                    String[] cells = line.split(",");
                    if(cells[1].equals(bookId)&& cells[0].equals(card) && cells[3].equals("no") ){
                        line = String.join(",",cells[0],cells[1],cells[2],"yes");
                        check = true;
                    }
                    file.add(line);
                }
            }
            myReader.close();

            //przepisz z pamięci plik na dysk
            FileWriter myWriter = new FileWriter("borrows.csv");
            BufferedWriter bw = new BufferedWriter(myWriter);
            for (String line : file){
                bw.write(line);
                bw.newLine();
            }
            bw.close();
            myWriter.close();

            if(check){
                file = new ArrayList<String>();
                myObj = new File("books.csv");
                myReader = new Scanner(myObj);
                while (myReader.hasNextLine()) {
                    String line = myReader.nextLine();
                    String[] cells = line.split(",");
                    if(cells[4].equals(bookId)){
                        String available = String.valueOf(Integer.parseInt(cells[2])+1);
                        line = String.join(",",cells[0],cells[1],available,cells[3],bookId);
                    }
                    file.add(line);
                }
                myReader.close();

                //przepisz z pamięci plik na dysk
                myWriter = new FileWriter("books.csv");
                bw = new BufferedWriter(myWriter);
                for (String line : file){
                    bw.write(line);
                    bw.newLine();
                }
                bw.close();
                myWriter.close();
                System.out.println("Udało się zwórcić książkę");
            }
            else
            {
                System.out.println("Błąd nie masz takiej ksiażki wypożyczonej");
            }



        } catch (IOException e) {
            System.out.println("Nie udało się zwrócić książki");
        }
    }

    public static Integer getDaysBetween(String d1, String d2){
        long daysBetween = 0;
        try {
            LocalDate date1 = LocalDate.parse(d1, formatter);
            LocalDate date2 = LocalDate.parse(d2, formatter);
            daysBetween = ChronoUnit.DAYS.between(date1, date2);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (int) daysBetween;
    }

    public static String getUserBorrows(String card){
        String result = "Id; Nazwa; Autor; Czas na oddanie [dni]\n";
        try{
            BufferedReader csvReader = new BufferedReader(new FileReader("borrows.csv"));
            String row;
            while ((row = csvReader.readLine()) != null) {
                String[] data = row.split(",");
                if(card.equals(data[0]) && data[3].equals("no"))
                {
                    Integer daysForReturn = getDaysBetween(getCurrentDate(),data[2])+30;
                    Book b = getBookById(data[1]);
                    result+= String.format("%s - %s, %s - %s \n",b.id,b.name,b.author,daysForReturn.toString());
                }
            }
            csvReader.close();

        }catch(IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

        return result;
    }

}
