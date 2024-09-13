package com.belafon.worldsfactory.annotationProcessors;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.element.TypeElement;

import java.util.Set;

@SupportedAnnotationTypes("WorldsFactoryClass")
public class WorldsFactoryClassProcessor extends AbstractProcessor {
    /**
     * TODO
    *    - checks if the field name of object is presented
    *    - generates additional code to setters
    *        - checks that the method has body
    *        - checks that the method is not a constructor
    *        - checks the setter has only one parameter (return type can be set -> write it to the beginning)
    *        - calls the setter with or without optional story name
    */
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        return true;
    }
}