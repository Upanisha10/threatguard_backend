package com.example.threatguard_demo.annotations;

import com.example.threatguard_demo.constants.AuditAction;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AuditLog {

    AuditAction action();

    String entityType() default "";
}

