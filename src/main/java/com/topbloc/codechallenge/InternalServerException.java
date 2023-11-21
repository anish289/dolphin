package com.topbloc.codechallenge;

// Class to catch SQL Exception to return to the client (made for simplicity of code)
public class InternalServerException extends RuntimeException {

    int statusCode;

    public InternalServerException(){ this.statusCode=500; }
}
