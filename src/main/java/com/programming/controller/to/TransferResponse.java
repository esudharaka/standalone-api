package com.programming.controller.to;

import java.util.Objects;

public class TransferResponse {
    private String status;

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    private String details;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    private String code;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "TransferResponse{" +
                "status='" + status + '\'' +
                "code='" + code + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TransferResponse that = (TransferResponse) o;
        return Objects.equals(status, that.status);
    }

    @Override
    public int hashCode() {

        return Objects.hash(status);
    }
}
