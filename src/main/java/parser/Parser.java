package parser;

import lexer.Lexer;
import lexer.Token;
import lexer.TokenType;

public class Parser {

    private final Lexer lexer;

    public Parser(Lexer lexer) {
        this.lexer = lexer;
    }

    public Node parseProgram() {
        Node result = new Node(TokenType.PROGRAM);
        try {
            Node curBody;
            do {
                curBody = parseBody();
                result.setRight(curBody);
            } while (!curBody.match(TokenType.EMPTY));
        } catch (RuntimeException e) {
            Token<?> errorToken = lexer.getParentToken();
            System.out.printf((char) 27 + "[31m P: %s LOC<%d:%d>\n", e.getMessage(), errorToken.getRow(), errorToken.getCol());
            System.exit(0);
        }
        return result;
    }

    public Node parseBody() {
        Node result;
        if (lexer.peekToken().match(TokenType.EOF)) {
            return new Node(TokenType.EMPTY);
        }
        Token<?> whatEver = lexer.getToken();
        switch (whatEver.getTokenType()) {
            case NAME:
                result = new Node(whatEver);
                parseName(result);
                break;
            case WHILE:
                result = parseWhile();
                break;
            case IF:
                result = parseIf();
                break;
            case READ:
            case PRINT:
                result = parseInOut(whatEver);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + whatEver.getTokenType());
        }
        return result;
    }

    public void parseName(Node result) {
        Token nameToken = lexer.getToken();
        switch (nameToken.getTokenType()) {
            case ASSIGNMENT:
                Node assigment = new Node(nameToken);
                if (lexer.peekToken().match(TokenType.STRSTR)) {
                    Node strstr = new Node(lexer.peekToken());
                    parseStrStr(strstr);
                    assigment.setRight(strstr);
                } else {
                    assigment.setLeft(parseExpr());
                }
                result.setRight(assigment);
                break;
            case BRACET_OPEN:
                result.setRight(parseArrayArg(false));
                break;
            default:
                throw new RuntimeException("Ошибка в парсинге имени");
        }
    }

    public Node parseInOut(Token out) {
        Node result;
        switch (out.getTokenType()) {
            case PRINT:
                result = new Node(TokenType.PRINT_BODY);
                break;
            case READ:
                result = new Node(TokenType.READ_BODY);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + out.getTokenType());
        }
        if (!lexer.getToken().match(TokenType.BRACKET_OPEN)) {
            throw new RuntimeException("Ожидалась (");
        }

        Token<?> literalToken = lexer.getToken();
        if (literalToken.match(TokenType.LITERAL)) {
            result.setRight(new Node(literalToken));
        }

        Token<?> commaToken = lexer.peekToken();

        if (commaToken.match(TokenType.BRACKET_CLOSE)) {
            lexer.getToken();
            return result;
        } else if (commaToken.match(TokenType.COMMA)) {
            lexer.getToken();
            while (!lexer.peekToken().match(TokenType.BRACKET_CLOSE)) {
                result.setRight(parseExpr());
            }
        }
        if (!lexer.getToken().match(TokenType.BRACKET_CLOSE)) {
            throw new RuntimeException("Ожидалась )");
        }
        return result;
    }

    public Node parseIf() {
        Node result = new Node(TokenType.IF);
        result.setRight(parseCondition());

        Token thenToken = lexer.getToken();
        if (!thenToken.match(TokenType.THEN)) {
            throw new RuntimeException("Ожидался then после условия");
        }
        result.setRight(parseIfThenBody());

        Token elseToken = lexer.getToken();
        switch (elseToken.getTokenType()) {
            case END:
                result.setRight(new Node(TokenType.EMPTY));
                return result;
            case ELSE:
                result.setRight(parseIfElseBody());
                result.setRight(new Node(TokenType.EMPTY));
                break;
            default:
                throw new RuntimeException("Отсутсвует end в конце if");
        }
        return result;
    }

    public Node parseIfThenBody() {
        Node result = new Node(TokenType.BODY_THEN);
        Node curBody;

        while (!lexer.peekToken().match(TokenType.END)) {
            if (lexer.peekToken().match(TokenType.EOF)) {
                throw new RuntimeException("Отсутсвует end после цикла");
            }
            if (lexer.peekToken().match(TokenType.ELSE)) {
                break;
            }
            curBody = parseBody();
            result.setRight(curBody);
        }
        result.setRight(new Node(TokenType.EMPTY));
        return result;
    }

    public Node parseIfElseBody() {
        Node result = new Node(TokenType.BODY_ELSE);
        Node curBody;

        while (!lexer.peekToken().match(TokenType.END)) {
            if (lexer.peekToken().match(TokenType.EOF)) {
                throw new RuntimeException("Отсутсвует end после цикла");
            }
            curBody = parseBody();
            result.setRight(curBody);
        }
        Token endToken = lexer.getToken();
        if (!endToken.match(TokenType.END)) {
            throw new RuntimeException("Отсутсвует end в конце if");
        }
        result.setRight(new Node(TokenType.EMPTY));
        return result;
    }

    public Node parseWhile() {

        Node result = new Node(TokenType.WHILE);
        result.setRight(parseCondition());

        Token doToken = lexer.getToken();
        if (!doToken.match(TokenType.DO)) {
            throw new RuntimeException("Ожидался do после условия");
        }
        result.setRight(parseWhileBody());
        return result;
    }

    private Node parseWhileBody() {

        Node result = new Node(TokenType.BODY_WHILE);
        Node curBody;

        while (!lexer.peekToken().match(TokenType.END)) {
            if (lexer.peekToken().match(TokenType.EOF)) {
                throw new RuntimeException("Отсутсвует end после цикла");
            }
            curBody = parseBody();
            result.setRight(curBody);
        }

        Token endToken = lexer.getToken();
        if (!endToken.match(TokenType.END)) {
            throw new RuntimeException("Отсутсвует end после цикла");
        }
        result.setRight(new Node(TokenType.EMPTY));
        return result;
    }

    private Node parseCondition() {
        Node result = new Node(TokenType.CONDITION);

        result.setLeft(parseExpr());
        Token<?> signToken = lexer.getToken();
        if (!signToken.match(TokenType.SIGN)) {
            throw new RuntimeException("P: ожидался знак условия");
        }
        result.setRight(new Node(signToken));
        result.setRight(parseExpr());
        result.setRight(new Node(TokenType.EMPTY));
        return result;
    }

    public Node parseArrayArg(boolean cost) {

        if (cost) {
            Token openBracetToken = lexer.getToken();
            if (!openBracetToken.match(TokenType.BRACET_OPEN)) {
                throw new RuntimeException("Ожидалась [");
            }
        }


        Token indexToken = lexer.getToken();
        Node result = new Node(new Token<>(TokenType.ARRAYINDEX, indexToken.getTokenValue()));

        Token closeBracetToken = lexer.getToken();
        if (!closeBracetToken.match(TokenType.BRACET_CLOSE)) {
            throw new RuntimeException("Ожидалась ]");
        }

        Token<?> assignToken = lexer.peekToken();

        if (assignToken.match(TokenType.ASSIGNMENT)) {
            lexer.getToken();
            Node assigment = new Node(assignToken);
            assigment.setRight(parseExpr());
            result.setRight(assigment);
        }
        return result;
    }

    public Node parseExpr() {
        Node result = parseTerm();
        Token<?> curToken = lexer.peekToken();
        while (curToken.match(TokenType.PLUS) ||
                curToken.match(TokenType.MINUS)) {
            lexer.getToken();
            Node sign = new Node(curToken);
            sign.setLeft(result);
            sign.setRight(parseTerm());
            result = sign;
            curToken = lexer.peekToken();
        }
        return result;
    }

    public Node parseTerm() {
        Node result = parseFactor();
        Token<?> curToken = lexer.peekToken();
        while (curToken.match(TokenType.MULTIPLICATION) ||
                curToken.match(TokenType.DIVISION)) {
            lexer.getToken();
            Node sign = new Node(curToken);
            sign.setLeft(result);
            sign.setRight(parseFactor());
            result = sign;
            curToken = lexer.peekToken();
        }
        return result;
    }

    public Node parseFactor() {
        Node result = parsePower();
        Token<?> curToken = lexer.peekToken();
        if (curToken.match(TokenType.EXPONENTIATION)) {
            lexer.getToken();
            Node exp = new Node(curToken);
            exp.setLeft(result);
            exp.setRight(parseFactor());
            result = exp;
        }
        return result;
    }

    public Node parsePower() {
        Token<?> curToken = lexer.peekToken();
        if (curToken.match(TokenType.MINUS)) {
            lexer.getToken();
            Node minus = new Node(curToken);
            minus.setLeft(parseAtom());
            return minus;
        }

        return parseAtom();
    }

    public Node parseAtom() {
        Node result;

        Token<?> token = lexer.getToken();
        switch (token.getTokenType()) {
            case BRACKET_OPEN:
                result = parseExpr();
                token = lexer.getToken();
                if (token.match(TokenType.BRACKET_CLOSE)) {
                    return result;
                } else {
                    throw new RuntimeException("проверить");
                }
            case BRACE_OPEN:
                result = parseArrayBody();
                break;
            case NUMBER:
            case LITERAL:
                result = new Node(token);
                break;
            case NAME:
                Token<?> nextToken = lexer.peekToken();
                switch (nextToken.getTokenType()) {
                    case BRACET_OPEN:
                        result = new Node(token);
                        result.setLeft(parseArrayArg(true));
                        break;
                    default:
                        result = new Node(token);
                        break;
                }
                break;
            default:
                throw new RuntimeException("проверить");
        }
        return result;
    }

    public Node parseArrayBody() {
        Node result = new Node(TokenType.ARRAY_BODY);

        while (!lexer.peekToken().match(TokenType.BRACE_CLOSE)) {
            Token<?> numberToken = lexer.getToken();
            if (numberToken.match(TokenType.NUMBER)) {
                result.setRight(new Node(numberToken));
            }
            Token<?> commaToken = lexer.getToken();
            if (commaToken.match(TokenType.BRACE_CLOSE)) {
                break;
            }
        }
        return result;
    }

    private void parseStrStr(Node assigment) {
        lexer.getToken();

        Token openBracketToken = lexer.getToken();
        if (!openBracketToken.match(TokenType.BRACKET_OPEN)) {
            throw new RuntimeException("ошибка ( скобки");
        }
        Token name1Token = lexer.getToken();

        if (!name1Token.match(TokenType.NAME)) {
            throw new RuntimeException("ошибка первого аргумента");
        }

        assigment.setRight(new Node(name1Token));

        Token commaToken = lexer.getToken();

        if (!commaToken.match(TokenType.COMMA)) {
            throw new RuntimeException("ошибка запятая между аргументами аргумента");
        }

        Token name2Token = lexer.getToken();

        if (!name2Token.match(TokenType.NAME)) {
            throw new RuntimeException("ошибка второго аргумента");
        }

        assigment.setRight(new Node(name2Token));

        Token closeBracketToken = lexer.getToken();
        if (!closeBracketToken.match(TokenType.BRACKET_CLOSE)) {
            throw new RuntimeException("ошибка ) скобки");
        }
    }
}
