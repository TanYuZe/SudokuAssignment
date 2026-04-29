package com.sudoku.service;

import com.sudoku.command.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class CommandParserTest {

    private CommandParser parser;

    @BeforeEach
    void setUp() {
        parser = new CommandParser();
    }

    // --- single-word commands ---

    @Test
    void parse_hint_returnsHintCommand() {
        assertInstanceOf(HintCommand.class, parser.parse("hint"));
    }

    @Test
    void parse_hintUpperCase_returnsHintCommand() {
        assertInstanceOf(HintCommand.class, parser.parse("HINT"));
    }

    @Test
    void parse_hintMixedCase_returnsHintCommand() {
        assertInstanceOf(HintCommand.class, parser.parse("Hint"));
    }

    @Test
    void parse_check_returnsCheckCommand() {
        assertInstanceOf(CheckCommand.class, parser.parse("check"));
    }

    @Test
    void parse_checkUpperCase_returnsCheckCommand() {
        assertInstanceOf(CheckCommand.class, parser.parse("CHECK"));
    }

    @Test
    void parse_quit_returnsQuitCommand() {
        assertInstanceOf(QuitCommand.class, parser.parse("quit"));
    }

    @Test
    void parse_quitUpperCase_returnsQuitCommand() {
        assertInstanceOf(QuitCommand.class, parser.parse("QUIT"));
    }

    // --- place number ---

    @Test
    void parse_A3_4_returnsCorrectPlaceCommand() {
        PlaceNumberCommand cmd = (PlaceNumberCommand) parser.parse("A3 4");
        assertEquals(0, cmd.row());  // A = row 0
        assertEquals(2, cmd.col());  // col 3 = index 2
        assertEquals(4, cmd.value());
    }

    @Test
    void parse_B1_lowerCase_returnsCorrectPlaceCommand() {
        PlaceNumberCommand cmd = (PlaceNumberCommand) parser.parse("b1 7");
        assertEquals(1, cmd.row());
        assertEquals(0, cmd.col());
        assertEquals(7, cmd.value());
    }

    @Test
    void parse_I9_9_returnsCorrectPlaceCommand() {
        PlaceNumberCommand cmd = (PlaceNumberCommand) parser.parse("I9 9");
        assertEquals(8, cmd.row());
        assertEquals(8, cmd.col());
        assertEquals(9, cmd.value());
    }

    @Test
    void parse_A1_1_minBoundaries_returnsPlaceCommand() {
        PlaceNumberCommand cmd = (PlaceNumberCommand) parser.parse("A1 1");
        assertEquals(0, cmd.row());
        assertEquals(0, cmd.col());
        assertEquals(1, cmd.value());
    }

    @Test
    void parse_withExtraWhitespace_succeeds() {
        PlaceNumberCommand cmd = (PlaceNumberCommand) parser.parse("  A3   4  ");
        assertEquals(0, cmd.row());
        assertEquals(2, cmd.col());
        assertEquals(4, cmd.value());
    }

    // --- clear ---

    @Test
    void parse_C5_clear_returnsClearCommand() {
        ClearCellCommand cmd = (ClearCellCommand) parser.parse("C5 clear");
        assertEquals(2, cmd.row());
        assertEquals(4, cmd.col());
    }

    @Test
    void parse_clearUpperCase_returnsClearCommand() {
        ClearCellCommand cmd = (ClearCellCommand) parser.parse("A1 CLEAR");
        assertEquals(0, cmd.row());
        assertEquals(0, cmd.col());
    }

    // --- invalid inputs ---

    @Test
    void parse_null_throws() {
        assertThrows(IllegalArgumentException.class, () -> parser.parse(null));
    }

    @Test
    void parse_blank_throws() {
        assertThrows(IllegalArgumentException.class, () -> parser.parse("   "));
    }

    @Test
    void parse_empty_throws() {
        assertThrows(IllegalArgumentException.class, () -> parser.parse(""));
    }

    @Test
    void parse_unknownWord_throws() {
        assertThrows(IllegalArgumentException.class, () -> parser.parse("play"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"Z3 4", "J1 5", "a 4"})
    void parse_invalidRow_throws(String input) {
        assertThrows(IllegalArgumentException.class, () -> parser.parse(input));
    }

    @ParameterizedTest
    @ValueSource(strings = {"A0 4", "A10 4", "Ax 4"})
    void parse_invalidColumn_throws(String input) {
        assertThrows(IllegalArgumentException.class, () -> parser.parse(input));
    }

    @Test
    void parse_valueZero_throws() {
        assertThrows(IllegalArgumentException.class, () -> parser.parse("A3 0"));
    }

    @Test
    void parse_valueTen_throws() {
        assertThrows(IllegalArgumentException.class, () -> parser.parse("A3 10"));
    }

    @Test
    void parse_valueNegative_throws() {
        assertThrows(IllegalArgumentException.class, () -> parser.parse("A3 -1"));
    }

    @Test
    void parse_valueAlpha_throws() {
        assertThrows(IllegalArgumentException.class, () -> parser.parse("A3 abc"));
    }

    @Test
    void parse_missingAction_throws() {
        assertThrows(IllegalArgumentException.class, () -> parser.parse("A3"));
    }

    @Test
    void parse_tooManyTokens_throws() {
        assertThrows(IllegalArgumentException.class, () -> parser.parse("A3 4 extra"));
    }
}
