package guru.qa.niffler.test.grpc;

import guru.qa.grpc.niffler.grpc.CalculateRequest;
import guru.qa.grpc.niffler.grpc.CalculateResponse;
import guru.qa.grpc.niffler.grpc.CurrencyValues;
import guru.qa.niffler.jupiter.annotations.GrpsTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static guru.qa.grpc.niffler.grpc.CurrencyValues.EUR;
import static guru.qa.grpc.niffler.grpc.CurrencyValues.KZT;
import static guru.qa.grpc.niffler.grpc.CurrencyValues.RUB;
import static guru.qa.grpc.niffler.grpc.CurrencyValues.USD;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@GrpsTest
public class NifflerCurrencyGrpcTest extends BaseGrpcTest {

    @Test
    void getAllCurrenciesTest() {
        var allCurrencies = currencyStub.getAllCurrencies(EMPTY);
        assertEquals(4, allCurrencies.getAllCurrenciesList().size());
        var currencyList = allCurrencies.getAllCurrenciesList();
        assertAll(
                () -> assertEquals(RUB, currencyList.get(0).getCurrency()),
                () -> assertEquals(KZT, currencyList.get(1).getCurrency()),
                () -> assertEquals(EUR, currencyList.get(2).getCurrency()),
                () -> assertEquals(USD, currencyList.get(3).getCurrency()));
    }

    @CsvSource(
            {
                    "100.0, RUB, KZT, 714.29",
                    "-100.0, RUB, KZT, -714.29",
                    "1400.0, KZT, RUB, 196.00",
                    "100.0, USD, RUB, 6666.67",
                    "100.0, RUB, USD, 1.5",
                    "10000.0, RUB, EUR, 138.89",
                    "100.0, EUR, RUB, 7200.0",
                    "100.0, EUR, KZT, 51428.57",
                    "100.0, KZT, EUR, 0.19",
                    "100.0, USD, KZT, 47619.05",
                    "100.0, KZT, USD, 0.21",
                    "500.0, EUR, USD, 540.0",
                    "500.0, USD, EUR, 462.96",
                    "0.0, USD, EUR, 0.0",
            }
    )
    @ParameterizedTest
    void calculateRateTest(double amount,
                           CurrencyValues spendCurrency,
                           CurrencyValues desiredCurrency,
                           double expectedCalculatedAmount) {
        final CalculateRequest cr = CalculateRequest.newBuilder()
                .setAmount(amount)
                .setSpendCurrency(spendCurrency)
                .setDesiredCurrency(desiredCurrency)
                .build();

        CalculateResponse calculateResponse = currencyStub.calculateRate(cr);
        Assertions.assertEquals(expectedCalculatedAmount, calculateResponse.getCalculatedAmount());

    }
}
