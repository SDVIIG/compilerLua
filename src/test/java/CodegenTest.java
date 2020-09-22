import buffer.Buffer;
import codeGen.CodeGen;
import idTable.IdTable;
import lexer.Lexer;
import org.junit.Assert;
import org.junit.Test;
import parser.Node;
import parser.Parser;
import semantic.Semantic;

import java.io.StringReader;
import java.util.List;

public class CodegenTest {

    @Test
    public void helloTest(){
        Buffer buffer = new Buffer(new StringReader( "print(\"hello world\")" ));
        Lexer lexer = new Lexer(buffer);
        Parser parser = new Parser(lexer);
        Node programTree = parser.parseProgram();

        IdTable idTable = new IdTable(programTree);

        Semantic semantic = new Semantic(programTree, idTable.getIdTable());

        CodeGen codeGen = new CodeGen(programTree, idTable.getIdTable());

        List<String> asmOK = List.of(".LC0:","\t.string \"hello world\"",".global main","\t.text","\t.type main, @function","main:","\tpushq\t%rbp","\tmovq\t%rsp, %rbp","\tsubq\t$2048, %rsp","\tmovl\t$.LC0,\t%edi","\tcall\tprintf", "\tleave", "\tret");
        Assert.assertEquals(asmOK, codeGen.getAssembler());
    }
}
