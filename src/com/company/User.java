package com.company;

import javax.xml.crypto.Data;

public class User {
    Integer card;
    String name;
    String surname;

    public User(){
        this.card = 0;
        this.name = "";
        this.surname = "";
    }

    public User(Integer card, String name, String surname){
        this.card = card;
        this.name = name;
        this.surname = surname;
    }

    public User(String id){
        if(Database.ValidUser(id)){
            User u = Database.getUserInfo(id.toString());
            this.card = u.card;
            this.name = u.name;
            this.surname = u.surname;
        }
        else{
            this.card = 0;
            this.name = "";
            this.surname = "";
        }
    }

    public Boolean isEmpty(){
        if(card == 0 && name.equals("") && surname.equals(""))
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    @Override
    public String toString(){
        return String.format("%s %s ", surname, name);
    }
}
