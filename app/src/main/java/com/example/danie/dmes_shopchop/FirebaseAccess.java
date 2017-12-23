package com.example.danie.dmes_shopchop;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


public class FirebaseAccess {
    private static FirebaseAuth auth;
    private static FirebaseUser user;
    private static FirebaseDatabase database;
    private static DatabaseReference databaseReference;
    private static FirebaseStorage storage;
    private static StorageReference storageReference;

    /**
     *
     * @return auth instance
     */
    public static FirebaseAuth getAuthInstance() {
        if(auth == null) {
            auth = FirebaseAuth.getInstance();
            return auth;
        }else{
            return auth;
        }
    }

    /**
     *
     * @return current user instance. Creates auth instance if needed.
     */
    public static FirebaseUser getUserInstance(){
        if(user == null){
            if(auth == null)
                getAuthInstance();
            user = auth.getCurrentUser();
            return user;
        }
        else{
            return user;
        }
    }

    /**
     *
     * @return return FirebaseDatabase instance.
     */
    public static FirebaseDatabase getDatabaseInstance(){
        if(database == null){
            database = FirebaseDatabase.getInstance();
            return database;
        }else{
            return database;
        }
    }

    /**
     *
     * @return DatabaseRefeference. creates FirebaseDatabase instance if needed.
     */
    public static DatabaseReference getDatabaseReference(){
        if(databaseReference == null) {
            if(database == null)
                getDatabaseInstance();
            databaseReference = database.getReference();
            return databaseReference;
        }else{
            return databaseReference;
        }
    }
    public static FirebaseStorage getStorageInstance(){
        if(storage == null){
            storage = FirebaseStorage.getInstance();
            return  storage;
        }else{
            return  storage;
        }
    }
    public static StorageReference getStorageReference(){
        if(storageReference == null){
            if(storage == null)
            getStorageInstance();
            storageReference = storage.getReference();
            return  storageReference;
        }else{
            return  storageReference;
        }
    }
}
