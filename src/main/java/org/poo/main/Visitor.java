package org.poo.main;

public interface Visitor {
    public void visit(Account account);
    public void visit(Savings savings);
    public void visit(Classic classic);
}
