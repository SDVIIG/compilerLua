package lexer;

public enum TokenType {

        STRSTR,
        LITERAL,

        BODY_THEN,
        BODY_ELSE,
        BODY_WHILE,

        ARRAY,
        ARRAYINDEX,
        ARRAY_BODY,
        NUMBER,
        PLUS, MINUS,
        MULTIPLICATION, DIVISION,
        EXPONENTIATION,
        BRACKET_OPEN, BRACKET_CLOSE,

        EMPTY,

        PROGRAM,
        NAME,
        BRACET_OPEN, BRACET_CLOSE,
        BRACE_OPEN, BRACE_CLOSE,
        COMMA,

        ASSIGNMENT,
        CONDITION,
        SIGN,

        PRINT_BODY,
        READ_BODY,

        IF, ELSE, THEN,
        WHILE, DO,
        READ, PRINT,
        STRING,
        EOF, END,
}
