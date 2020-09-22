package semantic;

import idTable.Variable;
import lexer.Token;
import lexer.TokenType;
import parser.Node;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Semantic {

    private Map<Integer, Character> subLevel;
    private Node tree;
    private Integer level;
    private Map<String, Variable> idTable;

    public Semantic(Node tree, Map<String, Variable> idTable) {
        this.idTable = idTable;
        this.subLevel = new HashMap<>();
        this.level = 0;
        this.tree = tree;
        analyze();
    }

    public Integer getLevel() {
        return level;
    }

    public void addSubLevel(Integer level) {
        Character subLvl = this.subLevel.get(level);

        if (subLvl == null) {
            subLevel.put(level, 'a');
        } else if (level == 0) {
            subLevel.put(level, 'a');
        } else {
            subLvl = (char) (subLvl + 1);
            subLevel.put(level, subLvl);
        }
    }

    public void analyze() {
        if (tree != null) {
            for (Node node : tree.getListChild()) {
                body(node);
            }
        }
    }

    public void analyzeRec(Node rec) {
        if (rec != null) {
            for (Node node : rec.getListChild()) {
                body(node);
            }
        }
    }

    public void body(Node node) {
        addSubLevel(level);
        switch (node.getTokenType()) {
            case STRING:
                checkSrtArgs(node);
                break;
            case IF:
            case WHILE:
                condition(node);
                break;
            case READ_BODY:
            case PRINT_BODY:
                inOut(node);
                break;
        }
    }

    private void checkSrtArgs(Node str){
        Node strstr = str.getFirstChildren().getFirstChildren();
        if(strstr.getTokenType().equals(TokenType.STRSTR)){
            for (Node nodeArgStr : strstr.getListChild()) {
                Variable variable = idTable.get(nodeArgStr.getTokenValue());
                if (variable == null) {
                    errorVar(nodeArgStr);
                }
                String lvl = getLevel().toString() + subLevel.get(getLevel()).toString();
                checkErrorCond(nodeArgStr, lvl, variable);
            }
        }
    }

    private void inOut(Node inOut){
        for (Node node : inOut.getListChild()) {
            switch (node.getTokenType()){
                case NAME:
                    Variable variable = idTable.get(node.getTokenValue());
                    if (variable == null) {
                        errorVar(node);
                    }
                    String lvl = getLevel().toString() + subLevel.get(getLevel()).toString();
                    checkErrorCond(node, lvl, variable);
                    break;
            }
        }
    }

    private void condition(Node node) {
        Node condition = node.getFirstChildren();
        List<Node> conditionChild = condition.getListChild();
        for (Node nodeConditionChild : conditionChild) {
            switch (nodeConditionChild.getTokenType()) {
                case NAME:
                    Variable variable = idTable.get(nodeConditionChild.getTokenValue());
                    if (variable == null) {
                        errorVar(nodeConditionChild);
                    }
                    String lvl = getLevel().toString() + subLevel.get(getLevel()).toString();

                    checkErrorCond(nodeConditionChild, lvl, variable);

                    if(!nodeConditionChild.getListChild().isEmpty()){
                        Node arrayIndex = nodeConditionChild.getFirstChildren();
                        Token indexToken = arrayIndex.getValue();
                        if(!indexToken.getTokenValue().toString().matches("[-+]?\\d+")){

                            if(indexToken.getTokenValue().equals(nodeConditionChild.getTokenValue())){
                                System.out.printf((char) 27 + "[31m SEMA: переменная индекса массива не может быть массивом LOC<%d:%d>",
                                        nodeConditionChild.getValue().getRow(), nodeConditionChild.getValue().getCol());
                                System.exit(0);
                            }
                            Variable variableIndex = idTable.get(indexToken.getTokenValue());
                            if (variableIndex == null) {
                                errorVar(nodeConditionChild);
                            }

                            if(variableIndex.getTokenType().equals(TokenType.STRING)){
                                System.out.printf((char) 27 + "[31m SEMA: переменная индекса массива не может быть строкой LOC<%d:%d>",
                                        nodeConditionChild.getValue().getRow(), nodeConditionChild.getValue().getCol());
                                System.exit(0);
                            }

                            if(checkLvl(lvl, variableIndex.getValue())){
                                errorVar(nodeConditionChild);
                            }
                        }
                    }
                    break;
            }
        }

        for (Node nodeRec : node.getListChild()) {
            switch (nodeRec.getTokenType()){
                case BODY_WHILE:
                case BODY_THEN:
                case BODY_ELSE:
                    level = level + 1;
                    analyzeRec(nodeRec);
                    break;
                case EMPTY:
                    level = level - 1;
                    break;
            }
        }
    }

    private void checkErrorCond(Node cond, String lvl, Variable variable){
        if(checkLvl(lvl, variable.getValue())){
            errorVar(cond);
        }
        if(cond.getListChild().isEmpty() && variable.getTokenType().equals(TokenType.ARRAY)){
            errorVar(cond);
        }
        if(!cond.getListChild().isEmpty() && !variable.getTokenType().equals(TokenType.ARRAY)){
            errorVar(cond);
        }
    }

    private boolean checkLvl(String lvl, String varLvl){
        Character lvlIndex = lvl.charAt(0);
        Character varLvlIndex = varLvl.charAt(0);

        return lvlIndex < varLvlIndex;
    }

    private void errorVar(Node error) {
        System.out.printf((char) 27 + "[31m SEMA: переменная не была объявлена LOC<%d:%d>",
                error.getValue().getRow(), error.getValue().getCol());
        System.exit(0);
    }
}
