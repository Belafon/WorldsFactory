package com.belafon.worldsfactory.api.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface WorldsFactoryClass {
    String className();
    String story() default "";
    boolean autoRegister() default true;
}
