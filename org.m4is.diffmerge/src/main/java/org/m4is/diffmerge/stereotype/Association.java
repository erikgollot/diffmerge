package org.m4is.diffmerge.stereotype;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to mark a field as the business reference of a bean. Beans are equals if
 * they have the same value for the field marked as the business reference.
 * 
 * @author erik
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
public @interface Association {
	public String opposite() default "";
}
