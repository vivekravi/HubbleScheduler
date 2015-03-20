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
public class FilterValueJob implements Job {

    public void execute(JobExecutionContext context) throws JobExecutionException {
        // add the code that queries the db and calls the service to push the data
        try {
            System.out.println("==========All Filter value Job started @ " + new Date()+"=================");
            String serviceUrl = "http://182.74.5.6/HubbleWebService/rest/hublservice/filtervalues";
            String tableInfoUrl = "http://182.74.5.6/HubbleWebService/rest/UpdateInfoService/tableinfo/filter_value";

            HubbleRestClient hubbleRestClient = new HubbleRestClient();
            String tableInfoResponseStr = hubbleRestClient.tableInfoService(tableInfoUrl);
            System.out.println("Updated timestamp for filter_value table: " + tableInfoResponseStr);


            Connection conn = DatabaseUtils.getDBConnection();
            String query = "SELECT DISTINCT gmm.gmm_id as \"gmmId\", prod.mdse_dept_nbr as \"deptId\", " +
                    "prod.mdse_dept_vendor_nbr as \"vendorId\", prod.project_id as \"projectId\" " +
                    "FROM stars_product prod " +
                    "INNER JOIN stars_department dept ON " +
                    "prod.mdse_dept_nbr = Dept.department_id " +
                    "INNER JOIN stars_corp_group grp ON " +
                    "dept.group_id = grp.group_id " +
                    "INNER JOIN stars_division div ON " +
                    "grp.division_id = div.division_id " +
                    "INNER JOIN stars_corp_gmm gmm ON " +
                    "div.gmm_id = gmm.gmm_id AND prod.product_id IN(SELECT product_id FROM " +
                    "STARS_PRODUCT_APPROVAL where APPROVAL_STATE='Pending') " +
                    "AND prod.project_id IN " +
                    "(SELECT distinct project_id FROM stars_project) ORDER BY prod.project_id";
            System.out.println("All GMM Query: "+query);
            String filterValueJsonStr = DatabaseUtils.resultSetToJson(conn, query);
            System.out.println("JSON Resultset from Macy's database : " + filterValueJsonStr);

            String responseStr = hubbleRestClient.callHubbleService(filterValueJsonStr,serviceUrl);
            System.out.println("Response from Hubl service : " + responseStr);
            System.out.println("==========All Filter value Job Job ended @ " + new Date()+"=================");

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
