package com.example.threatguard_demo.constants;

public enum AuditAction {

    // =====================
    // AUTHENTICATION
    // =====================
    LOGIN_SUCCESS,
    LOGIN_FAILURE,
    LOGOUT,
    UNAUTHORIZED_ACCESS,
    FORBIDDEN_ACTION,

    // =====================
    // USER MANAGEMENT
    // =====================
    USER_CREATED,
    USER_UPDATED,
    USER_DELETED,
    ROLE_CHANGED,

    // =====================
    // PASSWORD MANAGEMENT
    // =====================
    PASSWORD_RESET_REQUEST,
    PASSWORD_RESET_SUCCESS,
    PASSWORD_RESET_FAILURE,

    // =====================
    // SESSION MANAGEMENT
    // =====================
    SESSION_CREATED,
    SESSION_CLOSED,
    SESSION_TIMEOUT,
    SESSION_VIEWED,

    // =====================
    // ALERT MANAGEMENT
    // =====================
    ALERT_GENERATED,
    ALERT_VIEWED,
    ALERT_RESOLVED,
    ALERT_DISMISSED,
}


