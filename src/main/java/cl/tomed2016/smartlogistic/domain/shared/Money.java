package cl.tomed2016.smartlogistic.domain.shared;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;
import java.util.Objects;

public record Money(BigDecimal amount, Currency currency) {

    public static final Currency CLP = Currency.getInstance("CLP");

    public Money {
        Objects.requireNonNull(amount, "amount must not be null");
        Objects.requireNonNull(currency, "currency must not be null");
        amount = amount.setScale(0, RoundingMode.HALF_UP);
    }

    public static Money clp(long amount) {
        return new Money(BigDecimal.valueOf(amount), CLP);
    }
}
