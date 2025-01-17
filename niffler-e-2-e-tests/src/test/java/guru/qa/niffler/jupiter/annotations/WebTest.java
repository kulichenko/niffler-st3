package guru.qa.niffler.jupiter.annotations;

import guru.qa.niffler.jupiter.extensions.ApiLoginExtension;
import guru.qa.niffler.jupiter.extensions.BrowserExtension;
import guru.qa.niffler.jupiter.extensions.DbCreateUserExtension;
import guru.qa.niffler.jupiter.extensions.JpaExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@ExtendWith({DbCreateUserExtension.class, ApiLoginExtension.class, BrowserExtension.class, JpaExtension.class})
public @interface WebTest {
}
