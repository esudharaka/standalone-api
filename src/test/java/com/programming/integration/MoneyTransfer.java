package com.programming.integration;

import com.programming.JettyServer;
import com.programming.controller.to.TransferResponse;
import com.programming.utills.MappingUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.*;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

public class MoneyTransfer {
    JettyServer server;
    @Before
    public void before()  {
        server = new JettyServer();
        server.startServer();
    }

    @After
    public void after() throws Exception {
        this.server.stop();
    }

    @Test
    public void apiShouldReturnSuccessResponseAfterAValidTransger() throws IOException {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpPut httpPost = new HttpPut("http://localhost:8090/account");
        String JSON_STRING="{ \"fromAccount\" : 1, \"toAccount\": 2, \"transferAmount\": 10}";
        HttpEntity stringEntity = new StringEntity(JSON_STRING,ContentType.APPLICATION_JSON);
        httpPost.setEntity(stringEntity);

        HttpResponse httpResponse = callApi(httpclient, httpPost);
        Assert.assertEquals(httpResponse.getStatusLine().getStatusCode(), HttpStatus.SC_OK);

        String responseJSON = EntityUtils.toString(httpResponse.getEntity());
        TransferResponse transferResponse = MappingUtils.toObject(responseJSON, TransferResponse.class);
        Assert.assertEquals( transferResponse.getStatus(), "Success");
    }

    private HttpResponse callApi(CloseableHttpClient httpclient, HttpPut httpPost) throws IOException {
        return httpclient.execute(httpPost);

    }

    @Test
    public void apiShouldReturnFailResultWhenThereAreNoFunds() throws IOException {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpPut httpPost = new HttpPut("http://localhost:8090/account");
        String JSON_STRING="{ \"fromAccount\" : 1, \"toAccount\": 2, \"transferAmount\": 1000}";
        HttpEntity stringEntity = new StringEntity(JSON_STRING,ContentType.APPLICATION_JSON);
        httpPost.setEntity(stringEntity);

        HttpResponse httpResponse = callApi(httpclient, httpPost);
        Assert.assertEquals(httpResponse.getStatusLine().getStatusCode(), HttpStatus.SC_INTERNAL_SERVER_ERROR);

    }



}
