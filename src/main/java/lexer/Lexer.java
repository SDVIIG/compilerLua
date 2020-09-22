package lexer;

import buffer.Buffer;

public class Lexer {

    private Buffer buffer;

    private Token<?> currentToken = null;
    private Token<?> parentToken = null;

    private boolean isEndSourceCode = false;

    public Lexer(Buffer buffer) {
        this.buffer = buffer;
    }

    public Token<?> getParentToken() {
        return parentToken;
    }

    public Token<?> peekToken() {
        if (currentToken == null) {
            makeToken();
        }
        return currentToken;
    }

    public Token<?> getToken() {
        if (currentToken == null) {
            makeToken();
        }
        parentToken = currentToken;
        Token<?> result = currentToken;
        makeToken();

        return result;
    }

    private void makeToken() {
        if (isEndSourceCode) {
            currentToken = new Token<String>(TokenType.EOF, "Eof", buffer.getRow(), buffer.getRow());
            return;
        }

        readThroughSpacesAndComments();

        if (isEndSourceCode) {
            currentToken = new Token<String>(TokenType.EOF, "Eof", buffer.getRow(), buffer.getRow());
            return;
        }

        char curChar = buffer.getChar();
        switch (curChar) {
            case '+':
                currentToken = new Token<Object>(TokenType.PLUS, buffer.getRow(), buffer.getCol());
                break;
            case '-':
                currentToken = new Token<Object>(TokenType.MINUS, buffer.getRow(), buffer.getCol());
                break;
            case '*':
                currentToken = new Token<Object>(TokenType.MULTIPLICATION, buffer.getRow(), buffer.getCol());
                break;
            case '/':
                currentToken = new Token<Object>(TokenType.DIVISION, buffer.getRow(), buffer.getCol());
                break;
            case '^':
                currentToken = new Token<Object>(TokenType.EXPONENTIATION, buffer.getRow(), buffer.getCol());
                break;
            case '(':
                currentToken = new Token<Object>(TokenType.BRACKET_OPEN, buffer.getRow(), buffer.getCol());
                break;
            case ')':
                currentToken = new Token<Object>(TokenType.BRACKET_CLOSE, buffer.getRow(), buffer.getCol());
                break;
            case '{':
                currentToken = new Token<Object>(TokenType.BRACE_OPEN, buffer.getRow(), buffer.getCol());
                break;
            case '}':
                currentToken = new Token<Object>(TokenType.BRACE_CLOSE, buffer.getRow(), buffer.getCol());
                break;
            case '[':
                currentToken = new Token<Object>(TokenType.BRACET_OPEN, buffer.getRow(), buffer.getCol());
                break;
            case ']':
                currentToken = new Token<Object>(TokenType.BRACET_CLOSE, buffer.getRow(), buffer.getCol());
                break;
            case ',':
                currentToken = new Token<Object>(TokenType.COMMA, buffer.getRow(), buffer.getCol());
                break;

            default:
                if (Character.isDigit(curChar)) {
                    currentToken = getNumberFromBuffer(curChar);
                } else {
                    if (Character.isAlphabetic(curChar) || curChar == '_') {
                        currentToken = getIdentificatorFromBuffer(curChar);
                    } else {
                        currentToken = getConditionSignFromBuffer(curChar);
                    }
                }
                break;
        }
    }

    private Token<?> getNumberFromBuffer(char curChar) {
        int number = Character.getNumericValue(curChar);

        int shiftComma = -1;

        while (Character.isDigit(peekCharFromBuffer(0)) || (peekCharFromBuffer(0) == '.')) {
            if ((shiftComma > -1) || (peekCharFromBuffer(0) == '.')) {
                shiftComma++;
            }
            if (shiftComma != 0) {
                number = 10 * number + Character.getNumericValue(buffer.getChar());
            } else {
                buffer.getChar();
            }
        }

        switch (shiftComma) {
            case -1:
                return new Token<Integer>(TokenType.NUMBER, number, buffer.getRow(), buffer.getCol());
            case 0:
                throw new RuntimeException("L: число не может заканчиваться на точку");
            default:
                throw new RuntimeException("L: число с плавающей запятой не поддерживается");
        }
    }

    private Token<?> getIdentificatorFromBuffer(char curChar) {
        String ident = Character.toString(curChar);

        while (Character.isAlphabetic(peekCharFromBuffer(0))) {
            ident += buffer.getChar();
        }
        Token<?> result;

        switch (ident) {
            case "if":
                result = new Token<Object>(TokenType.IF, buffer.getRow(), buffer.getCol());
                break;
            case "then":
                result = new Token<Object>(TokenType.THEN, buffer.getRow(), buffer.getCol());
                break;
            case "else":
                result = new Token<Object>(TokenType.ELSE, buffer.getRow(), buffer.getCol());
                break;
            case "while":
                result = new Token<Object>(TokenType.WHILE, buffer.getRow(), buffer.getCol());
                break;
            case "do":
                result = new Token<Object>(TokenType.DO, buffer.getRow(), buffer.getCol());
                break;
            case "end":
                result = new Token<>(TokenType.END, buffer.getRow(), buffer.getCol());
                break;
            case "read":
                result = new Token<>(TokenType.READ, buffer.getRow(), buffer.getCol());
                break;
            case "print":
                result = new Token<>(TokenType.PRINT, buffer.getRow(), buffer.getCol());
                break;
            case "strstr":
                result = new Token<Object>(TokenType.STRSTR, buffer.getRow(), buffer.getCol());
                break;
            default:
                result = new Token<String>(TokenType.NAME, ident, buffer.getRow(), buffer.getCol());
                break;
        }
        return result;
    }

    private Token<?> getConditionSignFromBuffer(char curChar) {
        String sign = Character.toString(curChar);

        if (peekCharFromBuffer(0) == '=') {
            sign += buffer.getChar();
        }

        Token<?> result = null;

        switch (sign) {
            case "=":
                result = new Token<Object>(TokenType.ASSIGNMENT, buffer.getRow(), buffer.getCol());
                break;
            case "<":
                result = new Token<String>(TokenType.SIGN, "<", buffer.getRow(), buffer.getCol());
                break;
            case "<=":
                result = new Token<String>(TokenType.SIGN, "<=", buffer.getRow(), buffer.getCol());
                break;
            case "==":
                result = new Token<String>(TokenType.SIGN, "==", buffer.getRow(), buffer.getCol());
                break;
            case "!=":
                result = new Token<String>(TokenType.SIGN, "!=", buffer.getRow(), buffer.getCol());
                break;
            case ">":
                result = new Token<String>(TokenType.SIGN, ">", buffer.getRow(), buffer.getCol());
                break;
            case ">=":
                result = new Token<String>(TokenType.SIGN, ">=", buffer.getRow(), buffer.getCol());
                break;
            case "\"":
                sign = "";
                while (peekCharFromBuffer(0) != '"') {
                    if(peekCharFromBuffer(0) != '\n'){
                        sign += buffer.getChar();
                    } else {
                        throw new RuntimeException("L: незакрытый литерал");
                    }
                }
                buffer.getChar();
                result = new Token<String>(TokenType.LITERAL, sign, buffer.getRow(), buffer.getCol());
                break;
            default:
                throw new RuntimeException("L: неопределённая лексема");
        }
        return result;
    }

    private void readThroughSpacesAndComments() {
        while (true) {
            while (Character.isWhitespace(peekCharFromBuffer(0))) {
                buffer.getChar();
            }
            int nextChar = peekCharFromBuffer(0);
            if (nextChar == '-') {
                nextChar = peekCharFromBuffer(1);
                if (nextChar == '-') {
                    while (!isEndSourceCode) {
                        if ('\n' != peekCharFromBuffer(0)) {
                            buffer.getChar();
                        } else {
                            buffer.getChar();
                            break;
                        }
                    }
                } else {
                    if (isEndSourceCode) {
                        isEndSourceCode = false;
                    }
                    return;
                }
            } else {
                return;
            }
        }
    }

    private int peekCharFromBuffer(int serialIndex) {
        int ch = buffer.peekChar();

        if (ch == -1) {
            isEndSourceCode = true;
        } else {
            if (serialIndex == 1) {
                ch = buffer.peekSecondChar();
                if (ch == -1) {
                    isEndSourceCode = true;
                }
            }
        }
        return ch;
    }
}
