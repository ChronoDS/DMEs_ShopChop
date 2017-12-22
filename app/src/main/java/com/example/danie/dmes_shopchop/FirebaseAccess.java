package com.example.danie.dmes_shopchop;

// TODO import all of Firebase instances to this class and fix them in the code.
public class FirebaseAccess {
    private static final FirebaseAccess ourInstance = new FirebaseAccess();

    public static FirebaseAccess getInstance() {
        return ourInstance;
    }

    private FirebaseAccess() {
    }
}
