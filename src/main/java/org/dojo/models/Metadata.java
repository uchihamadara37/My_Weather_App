package org.dojo.models;

public class Metadata {
    public int status;
    public String message;

    public Metadata(int status, String message){
        this.message = message;
        this.status = status;
    }
}
