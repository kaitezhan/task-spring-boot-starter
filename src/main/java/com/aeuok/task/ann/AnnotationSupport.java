package com.aeuok.task.ann;

import javax.lang.model.element.Element;
import java.util.HashSet;
import java.util.Set;

/**
 * @author: CQ
 */
public class AnnotationSupport {
    private static Set<Element> supportElement;

    static {
        supportElement = new HashSet<>();
    }

    private AnnotationSupport() {
    }

    public static void add(Element element) {
        supportElement.add(element);
    }

    public static Set<Element> getSupportElement() {
        return supportElement;
    }


}
