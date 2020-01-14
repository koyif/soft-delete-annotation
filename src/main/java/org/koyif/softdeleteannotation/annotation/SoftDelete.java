package org.koyif.softdeleteannotation.annotation;

import java.lang.annotation.*;

@Inherited
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE)
public @interface SoftDelete {
}
