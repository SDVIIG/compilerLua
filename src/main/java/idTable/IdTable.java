package idTable;

import lexer.Token;
import lexer.TokenType;
import parser.Node;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IdTable {

    private Map<String, Variable> idTable;
    private Map<Integer, Character> subLevel;
    private Integer level;
    private Node tree;

    public IdTable(Node tree) {
        this.idTable = new HashMap<>();
        this.subLevel = new HashMap<>();
        this.level = 0;
        this.tree = tree;
        addSubLevel(level);
        formATablel();
    }

    public Map<String, Variable> getIdTable() {
        return idTable;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public void addSubLevel(Integer level) {
        Character subLvl = this.subLevel.get(level);

        if (subLvl == null) {
            subLevel.put(level, 'a');
        } else if (level == 0) {
            subLevel.put(level, 'a');
        } else {
            subLvl = (char) (subLvl.charValue() + 1);
            subLevel.put(level, subLvl);
        }
    }

    public void remSubLevel(Integer level) {
        Character subLvl = this.subLevel.get(level);
        subLvl = (char) (subLvl - 1);
        this.subLevel.put(level, subLvl);
    }

    private void addName(Node name) {
        String lvl = getLevel().toString() + subLevel.get(getLevel()).toString();
        Variable testList = idTable.get(name.getTokenValue().toString());

        if (testList == null) {
            setTypeName(name);
            testList = new Variable(lvl, name.getTokenType());
        } else {
            if (checkTypeName(testList, name)) {
                System.out.printf((char) 27 + "[31m SEMA: переменная может быть только одного типа LOC<%d:%d>",
                        name.getValue().getRow(), name.getValue().getCol());
                System.exit(0);
            }
        }
        idTable.put(name.getTokenValue().toString(), testList);
    }

    private boolean checkTypeName(Variable variable, Node name) {
        switch (name.getFirstChildren().getFirstChildren().getTokenType()) {
            case ARRAY_BODY:
                if (variable.getTokenType().equals(TokenType.ARRAY))
                    return false;
                break;
            case LITERAL:
            case STRSTR:
                if (variable.getTokenType().equals(TokenType.STRING)){
                    name.setValue(new Token<>(TokenType.STRING, name.getTokenValue()));
                    return false;
                }
                break;
            default:
                if (variable.getTokenType().equals(TokenType.NAME))
                    return false;
                break;
        }
        return true;
    }

    private void setTypeName(Node name) {
        switch (name.getFirstChildren().getFirstChildren().getTokenType()) {
            case ARRAY_BODY:
                name.setValue(new Token<>(TokenType.ARRAY, name.getTokenValue()));
                break;
            case STRSTR:
            case LITERAL:
                name.setValue(new Token<>(TokenType.STRING, name.getTokenValue()));
                break;
            default:
                break;
        }
    }

    private void addNameBody(Node name) {
        setLevel(getLevel() + 1);
        addSubLevel(level);
        List<Node> childBody = name.getListChild();
        for (Node node : childBody) {
            body(node);
        }
        setLevel(getLevel() - 1);
    }

    private void bodyRec(Node name) {
        List<Node> body = name.getListChild();
        for (Node node : body) {
            switch (node.getTokenType()) {
                case BODY_WHILE:
                case BODY_THEN:
                case BODY_ELSE:
                    addNameBody(node);
                    break;
            }
        }
    }

    public void body(Node nodeBody) {
        switch (nodeBody.getTokenType()) {
            case NAME:
                addName(nodeBody);
                break;
            case IF:
            case WHILE:
                bodyRec(nodeBody);
                break;
        }
    }

    public void formATablel() {
        if (tree != null) {
            for (Node node : tree.getListChild()) {
                body(node);
            }
        }
    }
}
