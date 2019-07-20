package com.programming.controller;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import com.programming.controller.to.TransferRequest;
import com.programming.controller.to.TransferResponse;
import com.programming.domain.Account;
import com.programming.exceptions.InSufficientFunds;
import com.programming.exceptions.NoAccountFound;
import com.programming.service.TransferService;
import com.programming.service.TransferServiceImpl;
import com.programming.utills.MappingUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.programming.utills.MappingUtils.toJsonString;

public class AccountController extends HttpServlet {
    private static final Logger LOGGER = LoggerFactory.getLogger(AccountController.class);
    private final TransferService transferService = TransferServiceImpl.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        String pathInfo = req.getPathInfo();
        String[] pathParts = pathInfo.split("/");
        String accountId = pathParts[1];
        try {
            Account account = transferService.getAccount(Integer.valueOf(accountId));
            if (account != null) {
                String accountJson = toJsonString(account);
                sendSuccessResponse(resp, accountJson);
            } else {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }

        } catch (Exception e) {
            onError(resp, "Error Occurred while fetching the account");
        }

    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            final TransferRequest transferRequest = MappingUtils.toObject(readRequestParams(req), TransferRequest.class);
            final TransferResponse transferResponse = transferService.doTransfer(transferRequest);
            final String employeeJsonString = toJsonString(transferResponse);
            sendSuccessResponse(resp, employeeJsonString);
        } catch (InSufficientFunds inSufficientFunds) {
            LOGGER.warn("Insufficient balance", inSufficientFunds);
            onError(resp, "Insufficient balance");
        } catch (NoAccountFound noAccountFound) {
            LOGGER.error("Could not find the account", noAccountFound);
            onError(resp, "Account/s not found");
        }

    }

    private void onError(HttpServletResponse resp, String details) {
        LOGGER.error("Error while reading the request");
        final TransferResponse transferResponse = new TransferResponse();
        transferResponse.setStatus("FAIL");
        transferResponse.setDetails(details);
        try (
                PrintWriter out = resp.getWriter();
        ) {
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(toJsonString(transferResponse));
            out.flush();
        } catch (IOException e) {
            LOGGER.error("Error occured");
        }

    }


    private void sendSuccessResponse(HttpServletResponse resp, String employeeJsonString) {
        try (
                PrintWriter out = resp.getWriter();
        ) {
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            resp.setStatus(HttpServletResponse.SC_OK);
            out.print(employeeJsonString);
            out.flush();
        } catch (IOException e) {
            LOGGER.error("Error occured");
        }
    }

    private String readRequestParams(HttpServletRequest req) throws IOException {
        StringBuilder buffer = new StringBuilder();
        BufferedReader reader = req.getReader();
        String line;
        while ((line = reader.readLine()) != null) {
            buffer.append(line);
        }

        return buffer.toString();
    }
}