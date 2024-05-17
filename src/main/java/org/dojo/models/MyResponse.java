package org.dojo.models;

public class MyResponse<T> {
    public Metadata metadata;
    public T data;

    public MyResponse(Metadata metadata, T data){
        this.metadata = metadata;
        this.data = data;
    }
}
