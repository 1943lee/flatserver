package common;

import com.kedacom.uc.sdk.api.impl.OpenAPIImpl;

/**
 * Created by keda on 2017/1/18.
 */
public class OpenAPI_Single {
    private static OpenAPI_Single _single = new OpenAPI_Single();
    private OpenAPI_Single(){}
    public static OpenAPI_Single getSingle(){
        return _single;
    }

    public OpenAPIImpl getM_OpenAPI() {
        return m_OpenAPI;
    }

    public void setM_OpenAPI(OpenAPIImpl m_OpenAPI) {
        if(this.m_OpenAPI==null) {
            this.m_OpenAPI = m_OpenAPI;
        }
    }

    private OpenAPIImpl m_OpenAPI = null;

}
