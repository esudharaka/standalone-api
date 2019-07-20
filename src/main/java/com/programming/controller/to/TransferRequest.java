package com.programming.controller.to;


import java.math.BigDecimal;
public class TransferRequest {

    private int fromAccount;
    private int toAccount;
    private BigDecimal transferAmount;

    public TransferRequest () {

    }

    public TransferRequest(int fromAccount, int toAccount, BigDecimal transferAmount) {
        this.fromAccount = fromAccount;
        this.toAccount = toAccount;
        this.transferAmount = transferAmount;
    }

    public int getFromAccount() {
        return fromAccount;
    }

    public void setFromAccount(int fromAccount) {
        this.fromAccount = fromAccount;
    }

    public int getToAccount() {
        return toAccount;
    }

    public void setToAccount(int toAccount) {
        this.toAccount = toAccount;
    }

    public BigDecimal getTransferAmount() {
        return transferAmount;
    }

    public void setTransferAmount(BigDecimal transferAmount) {
        this.transferAmount = transferAmount;
    }


    @Override
    public String toString() {
        return "TransferRequest{" +
                "fromAccount='" + fromAccount + '\'' +
                ", toAccount='" + toAccount + '\'' +
                ", transferAmount=" + transferAmount +
                '}';
    }


}
