package pl.marcinchwedczuk.paintme.domain;

import org.junit.jupiter.api.Test;
import pl.marcinchwedczuk.paintme.domain.Util;

import static org.assertj.core.api.Assertions.assertThat;

class UtilTest {
    @Test
    void quote_works() {
        assertThat(Util.quote("foo"))
                .isEqualTo("'foo'");
    }
}