package com.example.danie.dmes_shopchop;

import android.content.Context;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

/**
 * Created by Elad Peleg on 11/01/2018.
 */


@RunWith(MockitoJUnitRunner.class)
public class LoginActivityTest {

    @Test
    public void emailValidator(){
        LoginActivity login = mock(LoginActivity.class);
        when(login.isEmailValid("ThisEmailValid@gmail.com")).thenReturn(true);
        assertEquals(login.isEmailValid("ThisEmailValid@gmail.com"), true);
    }

}
