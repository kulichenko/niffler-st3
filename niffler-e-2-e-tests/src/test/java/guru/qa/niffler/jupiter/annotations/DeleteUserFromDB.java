package guru.qa.niffler.jupiter.annotations;


import guru.qa.niffler.jupiter.extensions.DeleteUserFromDBExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@ExtendWith(DeleteUserFromDBExtension.class)
public @interface DeleteUserFromDB {
    String user();
}
