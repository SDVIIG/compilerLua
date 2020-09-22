package idTable;

import lexer.TokenType;

public class Variable {
    String value;
    TokenType tokenType;

    public Variable(String value, TokenType tokenType) {
        this.value = value;
        this.tokenType = tokenType;
    }

    public String getValue() {
        return value;
    }

    public TokenType getTokenType() {
        return tokenType;
    }

    @Override
    public String toString() {
        return "Varible{" +
                "value='" + value + '\'' +
                ", tokenType=" + tokenType +
                '}';
    }
}
