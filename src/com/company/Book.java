package com.company;

public class Book {

    public String name;
    public String author;
    public String id;

    public Book(String name, String author, String id){
        this.name = name;
        this.author = author;
        this.id = id;
    }

    public Book(){
        name = "";
        author = "";
        id = "";
    }

    @Override
    public String toString(){
        return String.format("%s - %s",name, author);
    }

    public Boolean isEmpty(){
        if(name.equals("") && author.equals("") && id.equals(""))
        {
            return true;
        }
        else
        {
            return false;
        }
    }
}