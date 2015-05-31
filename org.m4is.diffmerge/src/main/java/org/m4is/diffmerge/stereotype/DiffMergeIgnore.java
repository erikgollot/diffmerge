package org.m4is.diffmerge.stereotype;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to ignore a field during diff & merge process of two beans
 * 1- If inContext and excludingContext are empty, the field will always be ignore no matter the 'context' of Reflection object.
 * 2- If inContext is not empty, the field will be ignored only in 'inContext' contexts
 * 3- If excludingContext is not empty, the field will be ignored except in 'excludingContext' contexts
 * 
 * You cannot set both inContext and excludingContext
 * @author erik
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
public @interface DiffMergeIgnore {	
	public String[] inContext() default "";
	public String[] excludingContext() default "";
}
