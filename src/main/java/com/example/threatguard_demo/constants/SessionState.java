package com.example.threatguard_demo.constants;


public enum SessionState {

    NEW,        // session just created
    ACTIVE,     // receiving events
    TERMINATED, // manually stopped or blocked
    EXPIRED     // ended due to inactivity
}
