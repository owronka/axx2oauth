package com.axxessio.oauth2.server.service.pdo;

import java.util.ArrayList;

public class ValidationException extends Exception {
    private static final long serialVersionUID = 1L;

    private ArrayList<ValidationError> al = null;
 
    public ValidationException() {
    	this.al = new ArrayList<ValidationError>();
    }

    public ValidationException(ValidationError ve) {
    	this.al = new ArrayList<ValidationError>();
    	
    	add (ve);
    }

    public ValidationException(ArrayList<ValidationError> al) {
    	this.al = al;
    }

    public void add(ValidationError ve) {
        al.add(ve);
    }

    public ValidationError get() {
    	return get(0);
    }

    public ValidationError get(int i) {
        if (i >= 0 && i < al.size()) {
        	return al.get(i);
        }
    	
    	return new ValidationError(0, "");
    }
    
    public ArrayList<ValidationError> getList () {
    	return al;
    }

    public boolean isEmpty () {
    	return al.size() == 0;
    }
}
