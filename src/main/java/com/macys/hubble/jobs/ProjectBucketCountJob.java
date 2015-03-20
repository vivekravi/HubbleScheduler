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
public class ProjectBucketCountJob implements Job {

        public void execute(JobExecutionContext context) throws JobExecutionException {
            // add the code that queries the db and calls the service to push the data
            try {
                System.out.println("==========All Project Bucket Count Job started @ " + new Date()+"=================");
                String serviceUrl = "http://182.74.5.6/HubbleWebService/rest/hublservice/projectcount";
                String tableInfoUrl = "http://182.74.5.6/HubbleWebService/rest/UpdateInfoService/tableinfo/project_bucket_count";

                HubbleRestClient hubbleRestClient = new HubbleRestClient();
                String tableInfoResponseStr = hubbleRestClient.tableInfoService(tableInfoUrl);
                System.out.println("Updated timestamp for project_bucket_count table: " + tableInfoResponseStr);


                Connection conn = DatabaseUtils.getDBConnection();
                String query = "select p.project_id as \"projectId\", a.APPROVAL_TYPE_CODE as \"approvalTypeCode\", count(a.PRODUCT_ID) as \"productCount\" " +
                        "from stars_product_approval a INNER JOIN stars_product p ON p.product_id = a.product_id AND a.APPROVAL_STATE='Pending' " +
                        "and a.product_id in (select product_id from stars_product prod, stars_project proj where prod.project_id = proj.PROJECT_ID) " +
                        "group by p.project_id, a.APPROVAL_TYPE_CODE ORDER By p.project_id";
                System.out.println("All GMM Query: "+query);
                String projectBucketCountJsonStr = DatabaseUtils.resultSetToJson(conn, query);
                System.out.println("JSON Resultset from Macy's database : " + projectBucketCountJsonStr);

            String responseStr = hubbleRestClient.callHubbleService(projectBucketCountJsonStr,serviceUrl);
            System.out.println("Response from Hubl service : " + responseStr);
                System.out.println("==========All Project Bucket Count Job Job ended @ " + new Date()+"=================");

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
}
