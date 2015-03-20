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
public class ActiveProjectProductsJob implements Job {

    public void execute(JobExecutionContext context) throws JobExecutionException {
        // add the code that queries the db and calls the service to push the data
        try {
            System.out.println("==========All Active project products Job started @ " + new Date()+"=================");
            String serviceUrl = "http://182.74.5.6/HubbleWebService/rest/hublservice/active_proj_prod";
            String tableInfoUrl = "http://182.74.5.6/HubbleWebService/rest/UpdateInfoService/tableinfo/active_project_product";

            /*HubbleRestClient hubbleRestClient = new HubbleRestClient();
            String tableInfoResponseStr = hubbleRestClient.tableInfoService(tableInfoUrl);
            System.out.println("Updated timestamp for Projects table: " + tableInfoResponseStr);*/


            Connection conn = DatabaseUtils.getDBConnection();
            String query = "select a.product_id as \"productId\", p.project_id as \"projectId\", a.approval_type_code as \"approvalTypeCode\", " +
                    "TO_CHAR(a.approval_date, 'YYYY-MM-DD') as \"approvalDate\", " +
                    "a.approval_state as \"approvalState\", a.approver_name as \"approver\", " +
                    "TO_CHAR(a.created_ts, 'YYYY-MM-DD HH24:MI:SS') as \"createdTs\", " +
                    "TO_CHAR(a.updated_ts, 'YYYY-MM-DD HH24:MI:SS') as \"updatedTs\"  " +
                    "from STARS_PRODUCT_APPROVAL a INNER JOIN stars_product p ON " +
                    "p.product_id = a.product_id AND a.APPROVAL_STATE='Pending' " +
                    "and a.product_id in (select product_id from stars_product prod INNER JOIN stars_project proj ON prod.project_id = proj.PROJECT_ID) order by p.project_id";
            System.out.println("All Active project products Query: "+query);
            String countQuery = "select count(*) from ("+query+") x";
            int count = DatabaseUtils.executeCount(conn, countQuery);
            System.out.println("Query result count : " + count);
            String sql = "select * from ("+query+") where rownum between %d and %d";
            int firstRow = 1, rowCount = 1000;
            while (count>1000){
                String sqlQuery = String.format(sql, firstRow, rowCount);
                Connection connection = DatabaseUtils.getDBConnection();
                String projJson = DatabaseUtils.resultSetToJson(connection, sqlQuery);
                System.out.println("ResultSetJson: "+projJson);
                firstRow+=rowCount;
                rowCount+=1000;
                count=count-1000;

            }


            /*int rowCountMin = 0;
            int rowCountMax = 1000;
            while(count>1000){
                count = count-1000;
                conn = DatabaseUtils.getDBConnection();
                String activeProjProdJsonStr = DatabaseUtils.resultSetToJson(conn, query+" and rownum > "+rowCountMin+" and rownum <="+rowCountMax);
                System.out.println(activeProjProdJsonStr);
                rowCountMin=rowCountMax+1;
                if(count>1000)
                    rowCountMax+=1000;
                else
                    rowCountMax+=count;

            }
            String gmmJsonStr = DatabaseUtils.resultSetToJson(conn, query);
            System.out.println("JSON Resultset from Macy's database : " + gmmJsonStr);*/

            /*String responseStr = hubbleRestClient.callHubbleService(gmmJsonStr,serviceUrl);
            System.out.println("Response from Hubl service : " + responseStr);*/
            System.out.println("==========All Active project products Job ended @ " + new Date()+"=================");

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
