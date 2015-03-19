package com.macys.hubble.jobs;

import com.macys.hubble.dao.DatabaseUtils;
import com.macys.rest.client.HubbleRestClient;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.sql.Connection;
import java.util.Date;

/**
 * Created by vivek on 3/18/15.
 */
public class AllActiveProductsJob implements Job {

    public void execute(JobExecutionContext context) throws JobExecutionException {
        // add the code that queries the db and calls the service to push the data
        try {
            System.out.println("==========All Active Products Job started @ " + new Date()+"=================");
            String serviceUrl = "http://182.74.5.6/HubbleWebService/rest/hublservice/products";
            String tableInfoUrl = "http://182.74.5.6/HubbleWebService/rest/UpdateInfoService/tableinfo/all_active_products";

            HubbleRestClient hubbleRestClient = new HubbleRestClient();
            String tableInfoResponseStr = hubbleRestClient.tableInfoService(tableInfoUrl);
            System.out.println("Updated timestamp for Projects table: " + tableInfoResponseStr);


            Connection conn = DatabaseUtils.getDBConnection();
            String query = "SELECT PRODUCT_ID as \"productId\", APPROVAL_TYPE_CODE as \"approvalTypeCode\", " +
                    "TO_CHAR(APPROVAL_DATE, 'YYYY-MM-DD') as \"approvalDate\", " +
                    "APPROVAL_STATE as \"approvalState\", APPROVER_NAME as \"approver\", " +
                    "TO_CHAR(created_ts, 'YYYY-MM-DD HH24:MI:SS') as \"createdTs\", " +
                    "TO_CHAR(updated_ts, 'YYYY-MM-DD HH24:MI:SS') as \"updatedTs\" " +
                    "from STARS_PRODUCT_APPROVAL where APPROVAL_STATE='Pending'";
            System.out.println("All GMM Query: "+query);
            String activeProductsJsonStr = DatabaseUtils.resultSetToJson(conn, query);
            System.out.println("JSON Resultset from Macy's database : " + activeProductsJsonStr);

            /*String responseStr = hubbleRestClient.callHubbleService(activeProductsJsonStr,serviceUrl);
            System.out.println("Response from Hubl service : " + responseStr);*/
            System.out.println("==========All Active Products Job Job ended @ " + new Date()+"=================");

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}