package com.macys.hubble.jobs;

import com.macys.hubble.dao.DatabaseUtils;
import com.macys.rest.client.HubbleRestClient;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.sql.Connection;
import java.util.Date;


/**
 * This class is executed when the Project Job is triggered from
 * the ServiceSchedule.
 * Created by vivek on 1/22/15.
 */
public class ProjectJob implements Job {

    /**
     * It queries the active projects from the stars database and calls the web service to
     * push the queried data to the middleware database. This method is called when the project job
     * is triggered from the scheduler.
     */
    public void execute(JobExecutionContext context) throws JobExecutionException {
        // add the code that queries the db and calls the service to push the data
        try {
            System.out.println("==========Projects Job started @ " + new Date()+"=================");
            String serviceUrl = "http://182.74.5.6/HubbleWebService/rest/hublservice/projects";
            String tableInfoUrl = "http://182.74.5.6/HubbleWebService/rest/UpdateInfoService/tableinfo/projects";

            //String tableInfoResponseStr = "2011-03-24 19:12:45";
            HubbleRestClient hubbleRestClient = new HubbleRestClient();
            // Web service to get the last updated timestamp for the project table and query the database based on that.
            String tableInfoResponseStr = hubbleRestClient.tableInfoService(tableInfoUrl);
            System.out.println("Updated timestamp for Projects table: " + tableInfoResponseStr);


            Connection conn = DatabaseUtils.getDBConnection();
            StringBuilder query = new StringBuilder("SELECT PROJECT_ID as \"projectId\", PROJECT_NAME as \"projectName\", PROJECT_DESC as \"projectDesc\", " +
                    "TO_CHAR(start_date, 'YYYY-MM-DD') as \"startDate\",TO_CHAR(end_date, 'YYYY-MM-DD') as \"endDate\", " +
                    "TO_CHAR(updated_ts, 'YYYY-MM-DD HH24:MI:SS') as \"updatedTs\" " +
                    "FROM stars_project where project_id IN " +
                    "(SELECT project_id FROM stars_product where product_id " +
                    "in (SELECT product_id FROM stars_product_approval " +
                    "where approval_state='Pending'))");
            if(tableInfoResponseStr!=null && tableInfoResponseStr!=""){
                query.append(" and UPDATED_TS > TO_DATE('");
                query.append(tableInfoResponseStr+"','YYYY-MM-DD HH24:MI:SS') ");
                query.append(" order by project_id");
            }
            else{
                query.append(" order by project_id");
            }
            System.out.println("Active Projects Query: "+query);
            String projectJsonStr = DatabaseUtils.resultSetToJson(conn, query.toString());
            System.out.println("JSON Resultset from Macy's database : " + projectJsonStr);

            String responseStr = hubbleRestClient.callHubbleService(projectJsonStr,serviceUrl);
            System.out.println("Response from Hubl service : " + responseStr);
            System.out.println("==========Projects Job ended @ " + new Date()+"=================");

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
