package com.macys.rest.client;

import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;

import javax.ws.rs.core.MediaType;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;


/**
 * Created by vivek on 1/22/15.
 */
public class HubbleRestClient {


    public String callHubbleService(String resultSetJsonStr, String requestURL) {

        String output = "";
        try {
            ClientRequest request = new ClientRequest(requestURL);
            request.accept("application/json");
            request.body("application/json", resultSetJsonStr);

            ClientResponse<String> response = request.post(String.class);

            if (response.getStatus() != 201) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + response.getResponseStatus());
            }
            output = response.getEntity();
            System.out.println("Response String: "+output);
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    new ByteArrayInputStream(response.getEntity().getBytes())));

            System.out.println("Output from Server .... \n");
            while ((output += br.readLine()) != null) {
                //System.out.println(output);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Error message: "+ex.getMessage());
        }
        return output;
    }

    public String tableInfoService(String requestURL) {

        String output = "";
        try {
            ClientRequest request = new ClientRequest(requestURL);
            ClientResponse<String> response = request.get(String.class);
            if (response.getStatus() != 200) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + response.getResponseStatus());
            }
            output = response.getEntity();
            System.out.println("Table Info: "+output);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return output;
    }

}
