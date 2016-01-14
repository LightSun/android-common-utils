package org.heaven7.core.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * this indicate the class/method/filed called by system or other framework.
 * so user should not call it.
 * Created by heaven7 on 2016/1/8.
 */
@Target( { ElementType.TYPE, ElementType.FIELD, ElementType.METHOD,ElementType.CONSTRUCTOR })
@Retention( RetentionPolicy.CLASS )
public @interface Hide {
}
