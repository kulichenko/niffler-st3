package guru.qa.niffler.jupiter.annotations;


import guru.qa.niffler.jupiter.extensions.DBCategoryExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@ExtendWith(DBCategoryExtension.class)
public @interface AddCategoryToDB {
    String category() default "";

    String username() default "";

}
