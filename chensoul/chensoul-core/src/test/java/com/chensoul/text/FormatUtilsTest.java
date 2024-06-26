package com.chensoul.text;

import com.chensoul.util.Maps;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
class FormatUtilsTest {

    @Test
    void formatVariables() {
        String message = FormatUtils.formatVariables("A,{1},C,{2},E", Maps.of("1", "B", "2", "D"));
        assertEquals("A,B,C,D,E", message);

        message = FormatUtils.formatVariables("A,{1},C,{2},E", Maps.of("1", "B"));
        assertEquals("A,B,C,{2},E", message);

        message = FormatUtils.formatVariables("A,{1},C,{2},E", Maps.of());
        assertEquals("A,{1},C,{2},E", message);

        message = FormatUtils.formatVariables("A,{1},C,{2},E", Maps.of("1", "B", "2", "D", "3", "C"));
        assertEquals("A,B,C,D,E", message);
    }

    @Test
    void format() {
        String message = FormatUtils.format("A,{},C,{},E", "B", "D");
        assertEquals("A,B,C,D,E", message);

        message = FormatUtils.format("A,{},C,{},E", "B");
        assertEquals("A,B,C,{},E", message);

        message = FormatUtils.format("A,{},C,{},E");
        assertEquals("A,{},C,{},E", message);

        message = FormatUtils.format("A,{},C,{},E", 1, 2, 3);
        assertEquals("A,1,C,2,E", message);
    }

    @Test
    void formatWithPlaceholder() {
        String message = FormatUtils.formatWithPlaceholder("A,<>,C,<>,E", "<>", "B", "D");
        assertEquals("A,B,C,D,E", message);

        message = FormatUtils.formatWithPlaceholder("A,<>,C,<>,E", "<>", "B");
        assertEquals("A,B,C,<>,E", message);

        message = FormatUtils.formatWithPlaceholder("A,<>,C,<>,E", "<>");
        assertEquals("A,<>,C,<>,E", message);

        message = FormatUtils.formatWithPlaceholder("A,<>,C,<>,E", "<>", 1, 2, 3);
        assertEquals("A,1,C,2,E", message);
    }
}
