import buffer.Buffer;
import lexer.Lexer;
import lexer.Token;
import lexer.TokenType;
import org.junit.Assert;
import org.junit.Test;

import java.io.StringReader;

public class LexerTest {

    @Test
    public void testEof(){
        Buffer buffer = new Buffer( new StringReader( "" ) );
        Lexer lexer = new Lexer( buffer );

        Token<?> token1 = lexer.getToken();
        Assert.assertEquals(TokenType.EOF, token1.getTokenType());
    }

    @Test
    public void testPlus(){
        Buffer buffer = new Buffer( new StringReader( "+" ) );
        Lexer lexer = new Lexer( buffer );

        Token<?> token1 = lexer.getToken();
        Assert.assertEquals(TokenType.PLUS, token1.getTokenType());
    }

    @Test
    public void testMinus(){
        Buffer buffer = new Buffer( new StringReader( "-" ) );
        Lexer lexer = new Lexer( buffer );

        Token<?> token1 = lexer.getToken();
        Assert.assertEquals(TokenType.MINUS, token1.getTokenType());
    }

    @Test
    public void testMultiplication(){
        Buffer buffer = new Buffer( new StringReader( "*" ) );
        Lexer lexer = new Lexer( buffer );

        Token<?> token1 = lexer.getToken();
        Assert.assertEquals(TokenType.MULTIPLICATION, token1.getTokenType());
    }

    @Test
    public void testDivision(){
        Buffer buffer = new Buffer( new StringReader( "/" ) );
        Lexer lexer = new Lexer( buffer );

        Token<?> token1 = lexer.getToken();
        Assert.assertEquals(TokenType.DIVISION, token1.getTokenType());
    }

    @Test
    public void testExponential(){
        Buffer buffer = new Buffer( new StringReader( "^" ) );
        Lexer lexer = new Lexer( buffer );

        Token<?> token1 = lexer.getToken();
        Assert.assertEquals(TokenType.EXPONENTIATION, token1.getTokenType());
    }

    @Test
    public void testBracketOpen(){
        Buffer buffer = new Buffer( new StringReader( "(" ) );
        Lexer lexer = new Lexer( buffer );

        Token<?> token1 = lexer.getToken();
        Assert.assertEquals(TokenType.BRACKET_OPEN, token1.getTokenType());
    }

    @Test
    public void testBracketClose(){
        Buffer buffer = new Buffer( new StringReader( ")" ) );
        Lexer lexer = new Lexer( buffer );

        Token<?> token1 = lexer.getToken();
        Assert.assertEquals(TokenType.BRACKET_CLOSE, token1.getTokenType());
    }

    @Test
    public void testBraceOpen(){
        Buffer buffer = new Buffer( new StringReader( "{" ) );
        Lexer lexer = new Lexer( buffer );

        Token<?> token1 = lexer.getToken();
        Assert.assertEquals(TokenType.BRACE_OPEN, token1.getTokenType());
    }

    @Test
    public void testBraceClose(){
        Buffer buffer = new Buffer( new StringReader( "}" ) );
        Lexer lexer = new Lexer( buffer );

        Token<?> token1 = lexer.getToken();
        Assert.assertEquals(TokenType.BRACE_CLOSE, token1.getTokenType());
    }

    @Test
    public void testBracetOpen(){
        Buffer buffer = new Buffer( new StringReader( "[" ) );
        Lexer lexer = new Lexer( buffer );

        Token<?> token1 = lexer.getToken();
        Assert.assertEquals(TokenType.BRACET_OPEN, token1.getTokenType());
    }

    @Test
    public void testBracetClose(){
        Buffer buffer = new Buffer( new StringReader( "]" ) );
        Lexer lexer = new Lexer( buffer );

        Token<?> token1 = lexer.getToken();
        Assert.assertEquals(TokenType.BRACET_CLOSE, token1.getTokenType());
    }

    @Test
    public void testComma(){
        Buffer buffer = new Buffer( new StringReader( "," ) );
        Lexer lexer = new Lexer( buffer );

        Token<?> token1 = lexer.getToken();
        Assert.assertEquals(TokenType.COMMA, token1.getTokenType());
    }

    @Test
    public void testNumber(){
        Buffer buffer = new Buffer( new StringReader( "42" ));
        Lexer lexer = new Lexer( buffer );
        Token<?> token1 = lexer.getToken();
        Assert.assertEquals(TokenType.NUMBER, token1.getTokenType());
        Assert.assertEquals(42, token1.getTokenValue());
    }

    @Test
    public void testPrint(){
        Buffer buffer = new Buffer( new StringReader( "print" ) );
        Lexer lexer = new Lexer( buffer );

        Token<?> token1 = lexer.getToken();
        Assert.assertEquals(TokenType.PRINT, token1.getTokenType());
    }

    @Test
    public void testRead(){
        Buffer buffer = new Buffer( new StringReader( "read" ) );
        Lexer lexer = new Lexer( buffer );

        Token<?> token1 = lexer.getToken();
        Assert.assertEquals(TokenType.READ, token1.getTokenType());
    }

    @Test
    public void testDO(){
        Buffer buffer = new Buffer( new StringReader( "do" ) );
        Lexer lexer = new Lexer( buffer );

        Token<?> token1 = lexer.getToken();
        Assert.assertEquals(TokenType.DO, token1.getTokenType());
    }

    @Test
    public void testEnd(){
        Buffer buffer = new Buffer( new StringReader( "end" ) );
        Lexer lexer = new Lexer( buffer );

        Token<?> token1 = lexer.getToken();
        Assert.assertEquals(TokenType.END, token1.getTokenType());
    }

    @Test
    public void testIf(){
        Buffer buffer = new Buffer( new StringReader( "if" ) );
        Lexer lexer = new Lexer( buffer );

        Token<?> token1 = lexer.getToken();
        Assert.assertEquals(TokenType.IF, token1.getTokenType());
    }

    @Test
    public void testElse(){
        Buffer buffer = new Buffer( new StringReader( "else" ) );
        Lexer lexer = new Lexer( buffer );

        Token<?> token1 = lexer.getToken();
        Assert.assertEquals(TokenType.ELSE, token1.getTokenType());
    }

    @Test
    public void testWhile(){
        Buffer buffer = new Buffer( new StringReader( "while" ) );
        Lexer lexer = new Lexer( buffer );

        Token<?> token1 = lexer.getToken();
        Assert.assertEquals(TokenType.WHILE, token1.getTokenType());
    }

    @Test
    public void testName(){
        Buffer buffer = new Buffer( new StringReader( "testname" ) );
        Lexer lexer = new Lexer( buffer );

        Token<?> token1 = lexer.getToken();
        Assert.assertEquals(TokenType.NAME, token1.getTokenType());
        Assert.assertEquals("testname", token1.getTokenValue());
    }

    @Test
    public void testName2(){
        Buffer buffer = new Buffer( new StringReader( "_testname" ) );
        Lexer lexer = new Lexer( buffer );

        Token<?> token1 = lexer.getToken();
        Assert.assertEquals(TokenType.NAME, token1.getTokenType());
        Assert.assertEquals("_testname", token1.getTokenValue());
    }

    @Test
    public void testCommentSingleLine(){
        Buffer buffer = new Buffer( new StringReader( "--test comment" ) );
        Lexer lexer = new Lexer( buffer );

        Token<?> token1 = lexer.getToken();
        Assert.assertEquals(TokenType.EOF, token1.getTokenType());
    }

    @Test
    public void testSing1(){
        Buffer buffer = new Buffer( new StringReader( "==" ) );
        Lexer lexer = new Lexer( buffer );

        Token<?> token1 = lexer.getToken();
        Assert.assertEquals(TokenType.SIGN, token1.getTokenType());
    }

    @Test
    public void testSing2(){
        Buffer buffer = new Buffer( new StringReader( "<" ) );
        Lexer lexer = new Lexer( buffer );

        Token<?> token1 = lexer.getToken();
        Assert.assertEquals(TokenType.SIGN, token1.getTokenType());
    }

    @Test
    public void testSing3(){
        Buffer buffer = new Buffer( new StringReader( "<=" ) );
        Lexer lexer = new Lexer( buffer );

        Token<?> token1 = lexer.getToken();
        Assert.assertEquals(TokenType.SIGN, token1.getTokenType());
    }

    @Test
    public void testSing4(){
        Buffer buffer = new Buffer( new StringReader( "!=" ) );
        Lexer lexer = new Lexer( buffer );

        Token<?> token1 = lexer.getToken();
        Assert.assertEquals(TokenType.SIGN, token1.getTokenType());
    }

    @Test
    public void testSing5(){
        Buffer buffer = new Buffer( new StringReader( ">" ) );
        Lexer lexer = new Lexer( buffer );

        Token<?> token1 = lexer.getToken();
        Assert.assertEquals(TokenType.SIGN, token1.getTokenType());
    }

    @Test
    public void testSing6(){
        Buffer buffer = new Buffer( new StringReader( ">=" ) );
        Lexer lexer = new Lexer( buffer );

        Token<?> token1 = lexer.getToken();
        Assert.assertEquals(TokenType.SIGN, token1.getTokenType());
    }

    @Test
    public void testAssigment(){
        Buffer buffer = new Buffer( new StringReader( "=" ) );
        Lexer lexer = new Lexer( buffer );

        Token<?> token1 = lexer.getToken();
        Assert.assertEquals(TokenType.ASSIGNMENT, token1.getTokenType());
    }

    @Test
    public void testLiteral(){
        Buffer buffer = new Buffer( new StringReader( "\"test\"" ) );
        Lexer lexer = new Lexer( buffer );

        Token<?> token1 = lexer.getToken();
        Assert.assertEquals(TokenType.LITERAL, token1.getTokenType());
    }

    @Test
    public void testEmptyLine() {
        Buffer buffer = new Buffer( new StringReader( "  2  + 3  \n   " ) );
        Lexer lexer = new Lexer( buffer );

        Token<?> token1 = lexer.getToken();
        Assert.assertEquals(TokenType.NUMBER, token1.getTokenType());
        Assert.assertEquals(2, token1.getTokenValue());

        Token<?> token2 = lexer.getToken();
        Assert.assertEquals(TokenType.PLUS, token2.getTokenType());

        Token<?> token3 = lexer.getToken();
        Assert.assertEquals(TokenType.NUMBER, token3.getTokenType());
        Assert.assertEquals(3, token3.getTokenValue());

        Token<?> token4 = lexer.getToken();
        Assert.assertEquals(TokenType.EOF, token4.getTokenType());

        Token<?> token5 = lexer.getToken();
        Assert.assertEquals(TokenType.EOF, token5.getTokenType());
    }

    @Test
    public void testManyLines() {
        Buffer buffer = new Buffer( new StringReader( "  2 \n   +\n   3\n" ) );
        Lexer lexer = new Lexer( buffer );

        Token<?> token1 = lexer.getToken();
        Assert.assertEquals(TokenType.NUMBER, token1.getTokenType());
        Assert.assertEquals(2, token1.getTokenValue());

        Token<?> token2 = lexer.getToken();
        Assert.assertEquals(TokenType.PLUS, token2.getTokenType());

        Token<?> token3 = lexer.getToken();
        Assert.assertEquals(TokenType.NUMBER, token3.getTokenType());
        Assert.assertEquals(3, token3.getTokenValue());

        Token<?> token4 = lexer.getToken();
        Assert.assertEquals(TokenType.EOF, token4.getTokenType());

        Token<?> token5 = lexer.getToken();
        Assert.assertEquals(TokenType.EOF, token5.getTokenType());
    }

    @Test
    public void testCommentSingleLine2() {
        Buffer buffer = new Buffer( new StringReader( " 3 --2\n   + 4 " ) );
        Lexer lexer = new Lexer( buffer );

        Token<?> token1 = lexer.getToken();
        Assert.assertEquals(TokenType.NUMBER, token1.getTokenType());
        Assert.assertEquals(3, token1.getTokenValue());

        Token<?> token2 = lexer.getToken();
        Assert.assertEquals(TokenType.PLUS, token2.getTokenType());

        Token<?> token3 = lexer.getToken();
        Assert.assertEquals(TokenType.NUMBER, token3.getTokenType());
        Assert.assertEquals(4, token3.getTokenValue());

        Token<?> token4 = lexer.getToken();
        Assert.assertEquals(TokenType.EOF, token4.getTokenType());

        Token<?> token5 = lexer.getToken();
        Assert.assertEquals(TokenType.EOF, token5.getTokenType());
    }
}
