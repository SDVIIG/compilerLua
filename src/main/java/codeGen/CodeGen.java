package codeGen;

import idTable.Variable;
import lexer.TokenType;
import parser.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CodeGen {
    private Integer numberLC;
    private Map<String, Integer> addressVar;
    private Map<String, List<String>> arrays;
    private List<String> assembler;
    private List<String> literal;
    private Integer varBytes;
    private Integer bodyCounter;
    private Map<String, Variable> idTable;

    public CodeGen(Node tree, Map<String, Variable> idTable) {
        this.addressVar = new HashMap<>();
        this.arrays = new HashMap<>();
        this.assembler = new ArrayList<>();
        this.literal = new ArrayList<>();
        this.varBytes = 0;
        this.bodyCounter = 0;
        this.idTable = idTable;
        start(tree);
    }

    public void setVar(String name, TokenType type) {
        switch (type) {
            case NAME:
                varBytes = varBytes + 4;
                break;
            case STRING:
                varBytes = varBytes + 12;
                break;
        }
        addressVar.put(name, varBytes);
    }

    public String getNameLC() {
        return "LC" + numberLC.toString();
    }

    public void setNameLC() {
        if (numberLC == null) {
            numberLC = 0;
        } else {
            numberLC++;
        }
    }

    public String getNameIf(Integer bodyCounter, Integer numberIF) {
        String val = bodyCounter.toString() + numberIF;
        return ".if" + val;
    }

    public String getNameIfNext(Integer bodyCounter, Integer numberIF) {
        String nextVal = bodyCounter.toString() + (numberIF + 1);
        return ".if" + nextVal;
    }

    public String getNameWhile(Integer bodyCounter, Integer numberWhile) {
        String val = bodyCounter.toString() + numberWhile;
        return ".while" + val;
    }

    public String getNameWhileNext(Integer bodyCounter, Integer numberWhile) {
        String nextVal = bodyCounter.toString() + (numberWhile + 1);
        return ".while" + nextVal;
    }

    public List<String> getAssembler() {
        return assembler;
    }

    public void start(Node tree) {
        assemblerStart();
        assembler.addAll(generator(tree));
        assembler.add(6, "\tsubq\t$" + stackBytes(varBytes) + ", %rsp");
        assembler.addAll(0, literal);
        assembler.add("\tleave");
        assembler.add("\tret");
    }

    public List<String> generator(Node tree) {
        Integer numberWhile = 0;
        Integer randForCommand = bodyCounter;
        List<String> asmBody = new ArrayList<>();
        if (tree != null) {
            for (Node child : tree.getListChild()) {
                switch (child.getTokenType()) {
                    case STRING:
                        asmBody.addAll(codegenStr(child));
                        break;
                    case ARRAY:
                        asmBody.addAll(codegenArray(child));
                        break;
                    case NAME:
                        asmBody.addAll(codegenName(child));
                        break;
                    case PRINT_BODY:
                        asmBody.addAll(codegenPrint(child, literal));
                        break;
                    case READ_BODY:
                        asmBody.addAll(codegenRead(child, literal));
                        break;
                    case IF:
                        asmBody.addAll(codegenIf(child, randForCommand));
                        break;
                    case WHILE:
                        asmBody.addAll(codegenWhile(child, randForCommand, numberWhile));
                        numberWhile = numberWhile + 2;
                        break;
                }
            }
        }
        return asmBody;
    }

    private void assemblerStart() {
        assembler.add(".global main");
        assembler.add("\t.text");
        assembler.add("\t.type main, @function");
        assembler.add("main:");
        assembler.add("\tpushq\t%rbp");
        assembler.add("\tmovq\t%rsp, %rbp");
       // assembler.add("\tsubq\t$2048, %rsp");
    }

    public List<String> codegenIf(Node ifNode, int randForCommand) {
        List<String> assemblerIf = new ArrayList<>();
        List<String> assemblerThen = new ArrayList<>();
        List<String> assemblerElse = new ArrayList<>();
        Integer numberIf = 0;

        for (Node nodeIfChild : ifNode.getListChild()) {
            switch (nodeIfChild.getTokenType()) {
                case CONDITION:
                    condition(nodeIfChild, assemblerIf, TokenType.IF, randForCommand, numberIf);
                    break;
                case BODY_THEN:
                    numberIf++;
                    bodyCounter++;
                    assemblerThen.addAll(generator(nodeIfChild));
                    break;
                case BODY_ELSE:
                    bodyCounter++;
                    assemblerElse.addAll(generator(nodeIfChild));
                    break;
                case EMPTY:
                    if (assemblerElse.isEmpty()) {
                        assemblerIf.addAll(assemblerThen);
                        assemblerIf.add(getNameIf(randForCommand, numberIf) + ":");
                    } else {
                        assemblerIf.addAll(assemblerThen);
                        assemblerIf.add("\tjmp\t" + getNameIf(randForCommand, (numberIf + 1)));
                        assemblerIf.add(getNameIf(randForCommand, numberIf) + ":");
                        assemblerIf.addAll(assemblerElse);
                        assemblerIf.add(getNameIf(randForCommand, (numberIf + 1)) + ":");
                    }
                    break;
            }
        }
        return assemblerIf;
    }

    public List<String> codegenWhile(Node whileNode, int randForCommand, int numberWhile) {
        List<String> assemblerWhile = new ArrayList<>();
        for (Node nodeWhileChild : whileNode.getListChild()) {
            switch (nodeWhileChild.getTokenType()) {
                case CONDITION:
                    assemblerWhile.add("\tjmp\t" + getNameWhile(randForCommand, numberWhile));
                    condition(nodeWhileChild, assemblerWhile, TokenType.WHILE, randForCommand, numberWhile);
                    break;
                case BODY_WHILE:
                    numberWhile++;
                    bodyCounter++;
                    codegenBodyWhile(nodeWhileChild, assemblerWhile, randForCommand, numberWhile);
                    break;
            }
        }
        return assemblerWhile;
    }

    public void codegenBodyWhile(Node bodyWhile, List<String> assembler, int randForCommand, int numberWhile) {
        assembler.add(1, getNameWhile(randForCommand, numberWhile) + ":");
        List<String> asmBodyWhile = generator(bodyWhile);
        assembler.addAll(2, asmBodyWhile);
        numberWhile++;
    }

    public void condition(Node condition, List<String> assemblerCondition, TokenType type, int randForCommand, int numberIfWhile) {
        int count = 0;
        String signType = null;
        String val1 = null;
        String val2 = null;
        String nameArray1 = null;
        String nameArray2 = null;

        boolean literal = false;
        String lit1 = null;
        String lit2 = null;

        for (Node conditionChild : condition.getListChild()) {
            switch (conditionChild.getTokenType()) {
                case LITERAL:
                    literal = true;
                    if (count == 0) {
                        lit1 = conditionChild.getTokenValue().toString();
                        count++;
                    } else {
                        lit2 = conditionChild.getTokenValue().toString();
                    }
                    break;
                case NAME:
                    if (conditionChild.getListChild().isEmpty()) {
                        if (count == 0) {
                            val1 = conditionChild.getTokenValue().toString();
                            count++;
                        } else {
                            val2 = conditionChild.getTokenValue().toString();
                        }
                    } else {
                        if (count == 0) {
                            nameArray1 = conditionChild.getTokenValue().toString();
                            val1 = conditionChild.getFirstChildren().getTokenValue().toString();
                            count++;
                        } else {
                            nameArray2 = conditionChild.getTokenValue().toString();
                            val2 = conditionChild.getFirstChildren().getTokenValue().toString();
                        }
                    }
                    break;
                case NUMBER:
                    if (count == 0) {
                        val1 = conditionChild.getTokenValue().toString();
                        count++;
                    } else {
                        val2 = conditionChild.getTokenValue().toString();
                    }
                    break;
                case SIGN:
                    signType = conditionChild.getTokenValue().toString();
                    break;
                case EMPTY:
                    break;
            }
        }

        if (type.equals(TokenType.WHILE)) {
            assemblerCondition.add(getNameWhile(randForCommand, numberIfWhile) + ":");
        }

        if (literal) {
            if (lit1 != null) {
                if (lit1.equals("null")) {
                    lit1 = "$0";
                    assemblerCondition.add("\tcmp\t" + lit1 + ",\t-" + addressVar.get(val2) + "(%rbp)");
                } else {
                    StringBuilder str = new StringBuilder();
                    for (int i = lit1.length(); i > 0; i--) {
                        str.append(Integer.toHexString(lit1.charAt(i - 1)));
                    }
                    assemblerCondition.add("\tcmp\t0x" + str + ",\t-" + addressVar.get(val2) + "(%rbp)");
                }
            }
            if (lit2 != null) {
                if (lit2.equals("null")) {
                    lit2 = "$0";
                }
                assemblerCondition.add("\tcmp\t" + lit2 + ",\t-" + addressVar.get(val1) + "(%rbp)");
            }
        } else {
            if (nameArray1 == null) {
                if (isNumeric(val1)) {
                    assemblerCondition.add("\tmovl\t$" + val1 + ",\t%eax");
                } else {
                    assemblerCondition.add("\tmovl\t-" + addressVar.get(val1) + "(%rbp),\t%eax");
                }
            } else {
                if (isNumeric(val1)) {
                    String arValue = nameArray1 + val1;
                    assemblerCondition.add("\tmovl\t-" + addressVar.get(arValue) + "(%rbp),\t%eax");
                } else {
                    arrayToAsm(val1, nameArray1, assemblerCondition);
                }
            }
            if (nameArray2 == null) {
                if (isNumeric(val2)) {
                    assemblerCondition.add("\tcmpl\t$" + val2 + ",\t%eax");
                } else {
                    assemblerCondition.add("\tcmpl\t-" + addressVar.get(val2) + "(%rbp),\t%eax");
                }
            } else {
                if (isNumeric(val2)) {
                    String arValue = nameArray2 + val2;
                    assemblerCondition.add("\tcmpl\t-" + addressVar.get(arValue) + "(%rbp),\t%eax");
                } else {
                    arrayToAsm(val2, nameArray2, assemblerCondition);
                    assemblerCondition.add("\tcmpl\t%eax,\t-" + addressVar.get(val2) + "(%rbp)"); // val1
                }
            }
        }
        assert signType != null;
        singType(signType, assemblerCondition, randForCommand, numberIfWhile, type);
    }

    public void singType(String signType, List<String> assembler, Integer randForCommand, Integer numberIfWhile, TokenType type) {

        switch (signType) {
            case ">":
                switch (type) {
                    case IF:
                        assembler.add("\tjle\t" + getNameIfNext(randForCommand, numberIfWhile));
                        break;
                    case WHILE:
                        assembler.add("\tjg\t" + getNameWhileNext(randForCommand, numberIfWhile));
                        break;
                }
                break;
            case "<":
                switch (type) {
                    case IF:
                        assembler.add("\tjge\t" + getNameIfNext(randForCommand, numberIfWhile));
                        break;
                    case WHILE:
                        assembler.add("\tjl\t" + getNameWhileNext(randForCommand, numberIfWhile));
                        break;
                }
                break;
            case "<=":
                switch (type) {
                    case IF:
                        assembler.add("\tjg\t" + getNameIfNext(randForCommand, numberIfWhile));
                        break;
                    case WHILE:
                        assembler.add("\tjle\t" + getNameWhileNext(randForCommand, numberIfWhile));
                        break;
                }
                break;
            case "==":
                switch (type) {
                    case IF:
                        assembler.add("\tjne\t" + getNameIfNext(randForCommand, numberIfWhile));
                        break;
                    case WHILE:
                        assembler.add("\tje\t" + getNameWhileNext(randForCommand, numberIfWhile));
                        break;
                }
                break;
            case "!=":
                switch (type) {
                    case IF:
                        assembler.add("\tje\t" + getNameIfNext(randForCommand, numberIfWhile));
                        break;
                    case WHILE:
                        assembler.add("\tjne\t" + getNameWhileNext(randForCommand, numberIfWhile));
                        break;
                }
                break;
            case ">=":
                switch (type) {
                    case IF:
                        assembler.add("\tjl\t" + getNameIfNext(randForCommand, numberIfWhile));
                        break;
                    case WHILE:
                        assembler.add("\tjge\t" + getNameWhileNext(randForCommand, numberIfWhile));
                        break;
                }
                break;
        }
    }

    public void arrayToAsm(String val, String nameArray, List<String> assembler) {
        assembler.add("\tmovl    -" + addressVar.get(val) + "(%rbp), %eax");
        assembler.add("\tcltd");

        List<String> addresArray = arrays.get(nameArray);
        nameArray = nameArray + (addresArray.size() - 1);

        assembler.add("\tmovl    -" + addressVar.get(nameArray) + "(%rbp,%rax,4), %eax");
    }

    public List<String> codegenRead(Node read, List<String> literal) {
        setNameLC();
        List<String> assemblerRead = new ArrayList<>();
        List<String> names = new ArrayList<>();
        assemblerRead.add("\tcall\tscanf");

        for (Node nodeRead : read.getListChild()) {
            switch (nodeRead.getTokenType()) {
                case LITERAL:
                    literal.add("." + getNameLC() + ":");
                    literal.add("\t.string \"" + nodeRead.getTokenValue() + "\"");
                    break;
                case NAME:
                    names.add(nodeRead.getTokenValue().toString());
                    break;
            }
        }

        if (names.size() == 1) {
            assemblerRead.add(0, "\txorl\t%eax,\t%eax");
            assemblerRead.add(1, "\tmovq\t$." + getNameLC() + ",\t%rdi");
            assemblerRead.add(2, "\tleaq\t-" + addressVar.get(names.get(0)) + "(%rbp),\t%rsi");
        } else if (names.size() > 1) {
            System.out.println("принимать можно не более одного значения");
            System.exit(0);
        }
        return assemblerRead;
    }

    public List<String> codegenStr(Node string) {
        List<String> assemblerString = new ArrayList<>();

        String nameVar = string.getTokenValue().toString();
        Node type = string.getFirstChildren().getFirstChildren();

        switch (type.getTokenType()) {
            case STRSTR:
                strstr(type, assemblerString, nameVar);
                break;
            case LITERAL:
                if (addressVar.get(nameVar) == null) {
                    setVar(nameVar, TokenType.STRING);
                }
                StringBuilder str = new StringBuilder();
                String literal = type.getTokenValue().toString();

                for (int i = literal.length(); i > 0; i--) {
                    str.append(Integer.toHexString(literal.charAt(i - 1)));
                }
                assemblerString.add("\tmovabsq\t$0x" + str + ", %rax");
                assemblerString.add("\tmovq\t%rax, -" + addressVar.get(nameVar) + "(%rbp)");
                break;
        }
        return assemblerString;
    }

    public void strstr(Node str, List<String> assemblerString, String nameVar) {
        String name1;
        String name2;

        if (addressVar.get(nameVar) == null) {
            setVar(nameVar, TokenType.STRING);
        }

        name1 = str.getListChild().get(0).getTokenValue().toString();
        name2 = str.getListChild().get(1).getTokenValue().toString();

        assemblerString.add("\tleaq\t-" + addressVar.get(name2) + "(%rbp),\t%rdx");
        assemblerString.add("\tleaq\t-" + addressVar.get(name1) + "(%rbp),\t%rax");
        assemblerString.add("\tmovq\t%rdx, %rsi");
        assemblerString.add("\tmovq\t%rax, %rdi");
        assemblerString.add("\tcall\tstrstr");
        assemblerString.add("\tmovq\t%rax, -" + addressVar.get(nameVar) + "(%rbp)");
    }

    public List<String> codegenArray(Node array) {
        List<String> assemblerArray = new ArrayList<>();
        List<String> value = new ArrayList<>();
        int arraySize = array.getFirstChildren().getFirstChildren().getListChild().size();

        for (Node nodeNumber : array.getFirstChildren().getFirstChildren().getListChild()) {
            value.add(nodeNumber.getTokenValue().toString());
        }

        arrays.put(array.getTokenValue().toString(), value);

        for (int i = 0; i < value.size(); i++) {

            String nameVar = array.getTokenValue().toString() + i;
            setVar(nameVar, TokenType.NAME);
            assemblerArray.add("\tmovl    $" + value.get(i) + " , -" + addressVar.get(nameVar) + "(%rbp)");
        }

        return assemblerArray;
    }

    public List<String> codegenPrint(Node print, List<String> literal) {
        setNameLC();
        List<String> assemblerPrint = new ArrayList<>();
        List<String> names = new ArrayList<>();
        assemblerPrint.add("\tcall\tprintf");
        for (Node nodePrint : print.getListChild()) {
            switch (nodePrint.getTokenType()) {
                case NAME:
                    if (!nodePrint.getListChild().isEmpty()) {
                        //array
                        String valueArray = nodePrint.getFirstChildren().getTokenValue().toString();
                        String name = nodePrint.getTokenValue().toString() + valueArray;
                        names.add(name);
                    } else {
                        names.add(nodePrint.getTokenValue().toString());
                    }
                    break;
                case LITERAL:
                    literal.add("." + getNameLC() + ":");
                    literal.add("\t.string \"" + nodePrint.getTokenValue() + "\"");
                    break;
            }
        }

        switch (names.size()) {
            case 0:
                assemblerPrint.add(0, "\tmovl\t$." + getNameLC() + ",\t%edi");
                break;
            case 1:
                Variable var = idTable.get(names.get(0));
                if (var.getTokenType().equals(TokenType.STRING)) {
                    assemblerPrint.add(0, "\tleaq\t-" + addressVar.get(names.get(0)) + "(%rbp),\t%rax");
                    assemblerPrint.add(1, "\tmovq\t%rax,\t%rsi");
                    assemblerPrint.add(2, "\tmovl\t$." + getNameLC() + ",\t%edi");
                } else {
                    assemblerPrint.add(0, "\tmovl\t-" + addressVar.get(names.get(0)) + "(%rbp),\t%eax");
                    assemblerPrint.add(1, "\tmovl\t%eax,\t%esi");
                    assemblerPrint.add(2, "\tmovl\t$." + getNameLC() + ",\t%edi");
                }
                break;
            default:
                throw new RuntimeException("CG: Данное количество аргументов в выводе не поддерживается");
        }
        return assemblerPrint;
    }

    public List<String> codegenName(Node name) {
        List<String> assemblerName = new ArrayList<>();
        String nameVar = name.getTokenValue().toString();

        if (addressVar.get(nameVar) == null) {
            setVar(name.getTokenValue().toString(), TokenType.NAME);
        }
        TokenType type = name.getFirstChildren().getFirstChildren().getTokenType();
        switch (type) {
            case NUMBER:
                assemblerName.add("\tmovl\t$" + name.getFirstChildren().getFirstChildren().getTokenValue() + ",\t-" + addressVar.get(name.getTokenValue().toString()) + "(%rbp)");
                break;
            case PLUS:
            case MINUS:
            case MULTIPLICATION:
            case DIVISION:
                assemblerMath(name, assemblerName, type, 0, nameVar);
                break;
            case NAME:
                if (!name.getFirstChildren().getFirstChildren().getListChild().isEmpty()) {
                    codegenArrayIndex(name, assemblerName, nameVar);
                } else {
                    String valueAsName = name.getFirstChildren().getFirstChildren().getTokenValue().toString();
                    assemblerName.add("\tmovl\t-" + addressVar.get(valueAsName) + "(%rbp), %eax");
                    assemblerName.add("\tmovl\t%eax,\t-" + addressVar.get(nameVar) + "(%rbp)");
                }
                break;
        }
        return assemblerName;
    }

    public void codegenArrayIndex(Node arrayIndex, List<String> assemblerArrayIndex, String nameVariable) {
        String index = arrayIndex.getFirstChildren().getFirstChildren().getFirstChildren().getTokenValue().toString();
        String arrayName = arrayIndex.getFirstChildren().getFirstChildren().getTokenValue().toString();
        if (isNumeric(index)) {
            //a[3]
            String value = arrayName + index;
            assemblerArrayIndex.add("\tmovl\t-" + addressVar.get(value) + "(%rbp), %eax");
            assemblerArrayIndex.add("\tmovl\t%eax, -" + addressVar.get(nameVariable) + "(%rbp)");
        } else {
            //a[b]
            assemblerArrayIndex.add("\tmovl\t-" + addressVar.get(index) + "(%rbp), %eax");
            assemblerArrayIndex.add("\tcltd");

            List<String> addresArray = arrays.get(arrayName);
            arrayName = arrayName + (addresArray.size() - 1);

            assemblerArrayIndex.add("\tmovl\t-" + addressVar.get(arrayName) + "(%rbp,%rax,4), %eax");
            assemblerArrayIndex.add("\tmovl\t%eax, -" + addressVar.get(nameVariable) + "(%rbp)");
        }
    }

    public static boolean isNumeric(String strNum) {
        try {
            double d = Double.parseDouble(strNum);
        } catch (NumberFormatException | NullPointerException nfe) {
            return false;
        }
        return true;
    }

    public void assemblerMath(Node number, List<String> commandAssembler, TokenType type, int node, String nameVariable) {
        if (node != 1) {

            String num1 = number.getFirstChildren().getFirstChildren().getListChild().get(0).getTokenValue().toString();
            String num2 = number.getFirstChildren().getFirstChildren().getListChild().get(1).getTokenValue().toString();

            if (type.equals(TokenType.DIVISION)) {

                if (isNumeric(num1) && isNumeric(num2)) {
                    int one = Integer.parseInt(num1);
                    int two = Integer.parseInt(num2);

                    String result = String.valueOf(one / two);
                    assembler.add("\tmovl\t$" + result + ", -" + addressVar.get(nameVariable) + "(%rbp)");
                } else {
                    if (isNumeric(num1)) {
                        setVar("numDiv1", TokenType.NAME);
                        assembler.add("\tmovl\t$" + num1 + ", -" + addressVar.get("numDiv1") + "(%rbp)");
                        assembler.add("\tmovl\t-" + addressVar.get("numDiv1") + "(%rbp), %eax");

                    } else {
                        assembler.add("\tmovl\t-" + addressVar.get(num1) + "(%rbp), %eax");
                        assembler.add("\tcltd");
                    }

                    if (isNumeric(num2)) {
                        setVar("numDiv2", TokenType.NAME);
                        assembler.add("\tmovl\t$" + num2 + ", -" + addressVar.get("numDiv2") + "(%rbp)");
                        assembler.add("\tidivl\t-" + addressVar.get("numDiv2") + "(%rbp)");
                    } else {
                        assembler.add("\tcltd");
                        assembler.add("\tidivl\t-" + addressVar.get(num2) + "(%rbp)");
                    }
                    assembler.add("\tmovl\t%eax, -" + addressVar.get(nameVariable) + "(%rbp)");
                }

            } else if (isNumeric(num1)) {
                commandAssembler.add("\tmovl\t$" + num1 + ", %edx");
            } else {
                commandAssembler.add("\tmovl\t-" + addressVar.get(num1) + "(%rbp), %edx");
            }

            switch (type) {
                case MINUS:
                    if (isNumeric(num2)) {
                        commandAssembler.add("\tsubl\t$" + num2 + ", %edx");
                    } else {
                        commandAssembler.add("\tsubl\t-" + addressVar.get(num2) + "(%rbp), %edx");
                    }
                    commandAssembler.add("\tmovl\t%edx, -" + addressVar.get(nameVariable) + "(%rbp)");
                    break;
                case PLUS:
                    if (isNumeric(num2)) {
                        commandAssembler.add("\taddl\t$" + num2 + ", %edx");
                    } else {
                        commandAssembler.add("\taddl\t-" + addressVar.get(num2) + "(%rbp), %edx");
                    }
                    commandAssembler.add("\tmovl\t%edx, -" + addressVar.get(nameVariable) + "(%rbp)");
                    break;
                case MULTIPLICATION:
                    if (isNumeric(num2)) {
                        commandAssembler.add("\tmovl\t$" + num2 + ", %eax");
                    } else {
                        commandAssembler.add("\tmovl\t-" + addressVar.get(num2) + "(%rbp), %eax");
                    }
                    commandAssembler.add("\tmull\t%edx");
                    commandAssembler.add("\tmovl\t%eax, -" + addressVar.get(nameVariable) + "(%rbp)");
                    break;
            }
            node++;
        }
    }

    private Integer stackBytes(Integer varBytes) {
        if (varBytes % 16 == 0) {
            return varBytes;
        } else {
            Integer tmp = varBytes / 16;
            tmp++;
            return tmp * 16;
        }
    }

}
