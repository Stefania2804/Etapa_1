package org.poo.main;

import java.util.ArrayList;

public class Classic extends Account {

    public Classic(String IBAN, double balance, String currency,
                   String type) {
        super(IBAN, balance, currency, type);
    }
    public void accept(Visitor v) {
        v.visit(this);
    }
}
