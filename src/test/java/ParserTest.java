import buffer.Buffer;
import lexer.Lexer;
import lexer.Token;
import lexer.TokenType;
import org.junit.Assert;
import org.junit.Test;
import parser.Node;
import parser.Parser;

import java.io.StringReader;

public class ParserTest {

    @Test
    public void testEmptyProgram() {
        Buffer buffer = new Buffer(new StringReader(""));
        Lexer lexer = new Lexer(buffer);
        Parser parser = new Parser(lexer);

        Node tree = new Node(TokenType.PROGRAM);
        tree.setLeft(new Node(TokenType.EMPTY));

        Assert.assertEquals(tree, parser.parseProgram());
    }

    @Test
    public void testVar(){
        Buffer buffer = new Buffer(new StringReader("a = 1"));
        Lexer lexer = new Lexer(buffer);
        Parser parser = new Parser(lexer);

        Node tree = new Node(TokenType.PROGRAM);
        Node name = new Node(new Token<>(TokenType.NAME, "a"));
        Node assigment = new Node(TokenType.ASSIGNMENT);
        assigment.setRight(new Node(new Token<>(TokenType.NUMBER, 1)));

        name.setRight(assigment);
        tree.setRight(name);
        tree.setRight(new Node(TokenType.EMPTY));
        Node realTree = parser.parseProgram();
        Assert.assertEquals(tree, realTree);
    }

    @Test
    public void testArray(){
        Buffer buffer = new Buffer(new StringReader("a = {1}"));
        Lexer lexer = new Lexer(buffer);
        Parser parser = new Parser(lexer);

        Node tree = new Node(TokenType.PROGRAM);
        Node name = new Node(new Token<>(TokenType.NAME, "a"));
        Node assigment = new Node(TokenType.ASSIGNMENT);
        Node arrayBody = new Node(TokenType.ARRAY_BODY);
        arrayBody.setRight(new Node(new Token<>(TokenType.NUMBER, 1)));

        name.setRight(assigment);
        assigment.setRight(arrayBody);

        tree.setRight(name);
        tree.setRight(new Node(TokenType.EMPTY));
        Node realTree = parser.parseProgram();
        Assert.assertEquals(tree, realTree);
    }

    @Test
    public void testPrint() {
        Buffer buffer = new Buffer(new StringReader("print(\"test\")"));
        Lexer lexer = new Lexer(buffer);
        Parser parser = new Parser(lexer);

        Node tree = new Node(TokenType.PROGRAM);
        Node printBody = new Node(TokenType.PRINT_BODY);
        printBody.setRight(new Node(new Token<>(TokenType.LITERAL, "test")));
        tree.setLeft(printBody);
        tree.setRight(new Node(TokenType.EMPTY));

        Node realTree = parser.parseProgram();
        Assert.assertEquals(tree, realTree);
    }

    @Test
    public void testPrintArg() {
        Buffer buffer = new Buffer(new StringReader("print(\"test\", a)"));
        Lexer lexer = new Lexer(buffer);
        Parser parser = new Parser(lexer);

        Node tree = new Node(TokenType.PROGRAM);
        Node printBody = new Node(TokenType.PRINT_BODY);
        printBody.setRight(new Node(new Token<>(TokenType.LITERAL, "test")));
        printBody.setRight(new Node(new Token<>(TokenType.NAME, "a")));
        tree.setLeft(printBody);
        tree.setRight(new Node(TokenType.EMPTY));

        Node realTree = parser.parseProgram();

        Assert.assertEquals(tree, realTree);
    }

    @Test
    public void testRead() {
        Buffer buffer = new Buffer(new StringReader("read(\"test\", a)"));
        Lexer lexer = new Lexer(buffer);
        Parser parser = new Parser(lexer);

        Node tree = new Node(TokenType.PROGRAM);
        Node printBody = new Node(TokenType.READ_BODY);
        printBody.setRight(new Node(new Token<>(TokenType.LITERAL, "test")));
        printBody.setRight(new Node(new Token<>(TokenType.NAME, "a")));
        tree.setLeft(printBody);
        tree.setRight(new Node(TokenType.EMPTY));

        Node realTree = parser.parseProgram();

        Assert.assertEquals(tree, realTree);
    }

    @Test
    public void arrayTest() {
        Buffer buffer = new Buffer(new StringReader("a[1] = 1"));
        Lexer lexer = new Lexer(buffer);
        Parser parser = new Parser(lexer);
        Node tree = new Node(TokenType.PROGRAM);
        Node name = new Node(new Token<>(TokenType.NAME, "a"));
        Node arrayIndex = new Node(new Token<>(TokenType.ARRAYINDEX, 1));
        Node assigment = new Node(TokenType.ASSIGNMENT);
        assigment.setRight(new Node(new Token<>(TokenType.NUMBER, 1)));
        arrayIndex.setRight(assigment);
        name.setRight(arrayIndex);
        tree.setRight(name);
        tree.setRight(new Node(TokenType.EMPTY));
        Node realTree = parser.parseProgram();
        Assert.assertEquals(tree, realTree);
    }

    @Test
    public void testNumber() {
        Buffer buffer = new Buffer(new StringReader("1"));
        Lexer lexer = new Lexer(buffer);
        Parser parser = new Parser(lexer);

        Node tree = new Node(new Token<>(TokenType.NUMBER, 1));

        Assert.assertEquals(tree, parser.parseExpr());
    }

    @Test
    public void testNegativeNumber() {
        Buffer buffer = new Buffer(new StringReader("-1"));
        Lexer lexer = new Lexer(buffer);
        Parser parser = new Parser(lexer);

        Node numNode = new Node(new Token<>(TokenType.NUMBER, 1));

        Node tree = new Node(TokenType.MINUS);
        tree.setLeft(numNode);

        Assert.assertEquals(tree, parser.parseExpr());
    }

    @Test
    public void testMULTIPLICATION() {
        Buffer buffer = new Buffer(new StringReader("1*1"));
        Lexer lexer = new Lexer(buffer);
        Parser parser = new Parser(lexer);

        Node numNode1 = new Node(new Token<>(TokenType.NUMBER, 1));
        Node numNode2 = new Node(new Token<>(TokenType.NUMBER, 1));

        Node tree = new Node(TokenType.MULTIPLICATION);
        tree.setLeft(numNode1);
        tree.setRight(numNode2);

        Assert.assertEquals(tree, parser.parseExpr());
    }

    @Test
    public void testDIVISION() {
        Buffer buffer = new Buffer(new StringReader("1/1"));
        Lexer lexer = new Lexer(buffer);
        Parser parser = new Parser(lexer);

        Node numNode1 = new Node(new Token<>(TokenType.NUMBER, 1));
        Node numNode2 = new Node(new Token<>(TokenType.NUMBER, 1));

        Node tree = new Node(TokenType.DIVISION);
        tree.setLeft(numNode1);
        tree.setRight(numNode2);

        Assert.assertEquals(tree, parser.parseExpr());
    }

    @Test
    public void testEXPONENTIATION() {
        Buffer buffer = new Buffer(new StringReader("1^1"));
        Lexer lexer = new Lexer(buffer);
        Parser parser = new Parser(lexer);

        Node numNode1 = new Node(new Token<>(TokenType.NUMBER, 1));
        Node numNode2 = new Node(new Token<>(TokenType.NUMBER, 1));

        Node tree = new Node(TokenType.EXPONENTIATION);
        tree.setLeft(numNode1);
        tree.setRight(numNode2);

        Assert.assertEquals(tree, parser.parseExpr());
    }

    @Test
    public void testBRACKET() {
        Buffer buffer = new Buffer(new StringReader("(1)"));
        Lexer lexer = new Lexer(buffer);
        Parser parser = new Parser(lexer);

        Node tree = new Node(new Token<>(TokenType.NUMBER, 1));

        Assert.assertEquals(tree, parser.parseExpr());
    }

    @Test
    public void testManyBRACKET() {
        Buffer buffer = new Buffer(new StringReader("(((1)))"));
        Lexer lexer = new Lexer(buffer);
        Parser parser = new Parser(lexer);

        Node tree = new Node(new Token<>(TokenType.NUMBER, 1));

        Assert.assertEquals(tree, parser.parseExpr());
    }

    @Test
    public void whileTest() {
        Buffer buffer = new Buffer(new StringReader("while i < len do\n" +
                "i = 1\n" +
                "end"));
        Lexer lexer = new Lexer(buffer);
        Parser parser = new Parser(lexer);

        Node tree = new Node(TokenType.PROGRAM);
        Node nodeWhile = new Node(TokenType.WHILE);

        Node condition = new Node(TokenType.CONDITION);
        condition.setRight(new Node(new Token<>(TokenType.NAME, "i")));
        condition.setRight(new Node(new Token<>(TokenType.SIGN, "<")));
        condition.setRight(new Node(new Token<>(TokenType.NAME, "len")));
        condition.setRight(new Node(TokenType.EMPTY));

        Node bodyWhile = new Node(TokenType.BODY_WHILE);
        Node nameBodyWhile = new Node(new Token<>(TokenType.NAME, "i"));
        Node assigmentName = new Node(TokenType.ASSIGNMENT);
        assigmentName.setRight(new Node(new Token<>(TokenType.NUMBER, 1)));

        nameBodyWhile.setRight(assigmentName);

        bodyWhile.setRight(nameBodyWhile);
        bodyWhile.setRight(new Node(TokenType.EMPTY));

        nodeWhile.setRight(condition);
        nodeWhile.setRight(bodyWhile);

        tree.setRight(nodeWhile);
        tree.setRight(new Node(TokenType.EMPTY));

        Node realTree = parser.parseProgram();

        Assert.assertEquals(tree, realTree);
    }

    @Test
    public void ifTest() {
        Buffer buffer = new Buffer(new StringReader("if a < b then\n" +
                "else\n" +
                "end"));
        Lexer lexer = new Lexer(buffer);
        Parser parser = new Parser(lexer);

        Node tree = new Node(TokenType.PROGRAM);
        Node ifNode = new Node(TokenType.IF);

        Node condition = new Node(TokenType.CONDITION);
        condition.setRight(new Node(new Token<>(TokenType.NAME, "a")));
        condition.setRight(new Node(new Token<>(TokenType.SIGN, "<")));
        condition.setRight(new Node(new Token<>(TokenType.NAME, "b")));
        condition.setRight(new Node(TokenType.EMPTY));

        ifNode.setRight(condition);

        Node bodyTHEN = new Node(TokenType.BODY_THEN);
        bodyTHEN.setRight(new Node(TokenType.EMPTY));

        Node bodyELSE = new Node(TokenType.BODY_ELSE);
        bodyELSE.setRight(new Node(TokenType.EMPTY));

        ifNode.setRight(bodyTHEN);
        ifNode.setRight(bodyELSE);
        ifNode.setRight(new Node(TokenType.EMPTY));

        tree.setRight(ifNode);
        tree.setRight(new Node(TokenType.EMPTY));

        Node realTree = parser.parseProgram();
        Assert.assertEquals(tree, realTree);
    }
}
