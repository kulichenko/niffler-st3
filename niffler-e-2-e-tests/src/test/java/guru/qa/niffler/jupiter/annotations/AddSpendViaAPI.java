package guru.qa.niffler.jupiter.annotations;

import guru.qa.niffler.jupiter.extensions.SpendExtension;
import guru.qa.niffler.model.CurrencyValues;
import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@ExtendWith(SpendExtension.class)
public @interface AddSpendViaAPI {

    String username() default "";

    String description() default "";

    String category() default "";

    double amount() default -1.0;

    CurrencyValues currency() default CurrencyValues.RUB;

}