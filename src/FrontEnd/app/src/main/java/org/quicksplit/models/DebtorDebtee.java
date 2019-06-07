package org.quicksplit.models;

public class DebtorDebtee {

    private String amount;
    private User debtor;
    private User debtee;

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public User getDebtor() {
        return debtor;
    }

    public void setDebtor(User debtor) {
        this.debtor = debtor;
    }

    public User getDebtee() {
        return debtee;
    }

    public void setDebtee(User debtee) {
        this.debtee = debtee;
    }
}
