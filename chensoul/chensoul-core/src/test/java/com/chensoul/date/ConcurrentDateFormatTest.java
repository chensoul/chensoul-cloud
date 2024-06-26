package com.chensoul.date;

import com.chensoul.constant.DateConstants;
import java.text.ParseException;
import java.util.Date;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ConcurrentDateFormatTest {
    @Test
    public void testParseWithValidSource() {
        String source = "2022-08-01";

        Date result = ConcurrentDateFormat.of(DateConstants.NORM_DATE).parse(source);

        Assertions.assertNotNull(result);
    }

    @Test
    public void testParseWithInvalidSource() {
        String source = "Invalid Date";
        Assertions.assertThrows(ParseException.class, () -> ConcurrentDateFormat.of(DateConstants.NORM_DATE).parse(source));
    }
}
