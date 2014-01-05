package com.fasterxml.jackson.jsonpath.internal.js;

public class TernaryJSExpr extends JSExpr {

    private JSExpr condition;

    private JSExpr onTrue;
    
    private JSExpr onFalse;

    public TernaryJSExpr(JSExpr condition, JSExpr onTrue, JSExpr onFalse) {
        this.condition = condition;
        this.onTrue = onTrue;
        this.onFalse = onFalse;
    }
}
