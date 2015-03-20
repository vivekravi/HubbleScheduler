package com.macys.hubble.jobs;

import com.macys.hubble.dao.DatabaseUtils;
import com.macys.rest.client.HubbleRestClient;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.sql.Connection;
import java.util.Date;

/**
 * Created by vivek on 2/12/15.
 */
public class GmmJob implements Job {

    public void execute(JobExecutionContext context) throws JobExecutionException {
        // add the code that queries the db and calls the service to push the data
        try {
            System.out.println("==========All GMM Job started @ " + new Date()+"=================");
            String serviceUrl = "http://182.74.5.6/HubbleWebService/rest/hublservice/gmm";
            String tableInfoUrl = "http://182.74.5.6/HubbleWebService/rest/UpdateInfoService/tableinfo/all_gmm";


            HubbleRestClient hubbleRestClient = new HubbleRestClient();
            String tableInfoResponseStr = hubbleRestClient.tableInfoService(tableInfoUrl);
            System.out.println("Updated timestamp for Projects table: " + tableInfoResponseStr);


            Connection conn = DatabaseUtils.getDBConnection();
            String query = "SELECT gmm_id as \"gmmId\", gmm_name as \"gmmName\" FROM STARS_CORP_GMM";
            System.out.println("All GMM Query: "+query);
            String gmmJsonStr = DatabaseUtils.resultSetToJson(conn, query);
            System.out.println("JSON Resultset from Macy's database : " + gmmJsonStr);

            String responseStr = hubbleRestClient.callHubbleService(gmmJsonStr,serviceUrl);
            System.out.println("Response from Hubl service : " + responseStr);
            System.out.println("==========All GMM Job ended @ " + new Date()+"=================");

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}

