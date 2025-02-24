package lsieun.annotation.todo;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;

@Retention(RetentionPolicy.SOURCE)
@Target({
        TYPE,
        FIELD,
        CONSTRUCTOR,
        METHOD,
        PARAMETER,
        LOCAL_VARIABLE,
        ANNOTATION_TYPE,
        PACKAGE,
        TYPE_PARAMETER,
        TYPE_USE,
//        MODULE,
//        RECORD_COMPONENT,
})
public @interface ToDo {
    String[] value();
}