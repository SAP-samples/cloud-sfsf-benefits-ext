package com.sap.hana.cloud.samples.benefits.connectivity;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import com.sap.hana.cloud.samples.benefits.api.bean.UserBean;
import com.sap.hana.cloud.samples.benefits.connectivity.base.ODataConnector;
import com.sap.hana.cloud.samples.benefits.connectivity.helper.CoreODataParser;
import com.sap.hana.cloud.samples.benefits.connectivity.helper.SFUser;

@SuppressWarnings("nls")
public class CoreODataConnector extends ODataConnector {

    private static final String UTF_8 = "UTF-8";
    private static CoreODataConnector INSTANCE = null;
    private static final String MANAGED_EMPLOYEES_QUERY = "User?$select=userId,firstName,lastName,email&$filter=hr/userId%20eq%20'#'";
    private static final String PROFILE_QUERY = "User('#')?$select=userId,firstName,lastName,email,hr/userId,hr/firstName,hr/lastName,hr/email&$expand=hr";
    private static final String INFO_QUERY = "User('#')?$select=userId,firstName,lastName,email,location,businessPhone,cellPhone,division,title,department,hr/userId,hr/firstName,hr/lastName,hr/email,hr/businessPhone&$expand=hr";
    public static synchronized CoreODataConnector getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new CoreODataConnector();
        }
        return INSTANCE;
    }

    private CoreODataConnector() {
        super("java:comp/env/sap_hcmcloud_core_odata");
    }

    private String getMangedEmployeesQuery(String hrSFUserName) throws UnsupportedEncodingException {
        return MANAGED_EMPLOYEES_QUERY.replace("#", urlEncode(hrSFUserName));
    }

    private String getProfileQuery(String userName) throws UnsupportedEncodingException {
        return PROFILE_QUERY.replace("#", urlEncode(userName));
    }
    
    private String getInfoQuery(String userName) throws UnsupportedEncodingException {
        return INFO_QUERY.replace("#", urlEncode(userName));
    }

    private String urlEncode(String text) throws UnsupportedEncodingException {
        return URLEncoder.encode(text, UTF_8);
    }

    public List<SFUser> getManagedEmployees(String hrSFUserName) throws IOException {
        String userListJson = getODataResponse(getMangedEmployeesQuery(hrSFUserName));
        CoreODataParser parser = CoreODataParser.getInstance();
        return parser.loadSFUserProfileListFromJsom(userListJson);
    }

    public SFUser getUserProfile(String userName) throws IOException {
        String userJson = getODataResponse(getProfileQuery(userName));
        CoreODataParser parser = CoreODataParser.getInstance();
        return parser.loadSFUserProfileFromJsom(userJson);
    }
    
    public UserBean getInfoProfile(String userName) throws IOException {
        String userJson = getODataResponse(getInfoQuery(userName));
        CoreODataParser parser = CoreODataParser.getInstance();
        return parser.loadUserBeanProfileFromJsom(userJson);
    }

 
}
