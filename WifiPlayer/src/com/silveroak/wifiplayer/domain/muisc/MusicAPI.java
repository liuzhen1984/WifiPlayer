package com.silveroak.wifiplayer.domain.muisc;

/**
 * Created by zliu on 14/12/9.
 */
public class MusicAPI {
    /**
     *
     private static final String ALIAS_NAME = "alias_name";
     private static final String ORGANIZE_NAME = "organize_name";
     private static final String API_URL = "api_url";
     private static final String ICO_URL = "ico_url";
     private static final String ORGANIZE_URL = "organize_url";
     private static final String ORGANIZE_DESC = "organize_desc";
     */
    private long _id;
    private  String aliasName ;
    private  String organizeName ;
    private  String apiUrl ;
    private  String icoUrl;
    private  String organizeUrl;
    private  String organizeDesc;

    public long get_id() {
        return _id;
    }

    public void set_id(long _id) {
        this._id = _id;
    }

    public String getAliasName() {
        return aliasName;
    }

    public void setAliasName(String aliasName) {
        this.aliasName = aliasName;
    }

    public String getOrganizeName() {
        return organizeName;
    }

    public void setOrganizeName(String organizeName) {
        this.organizeName = organizeName;
    }

    public String getApiUrl() {
        return apiUrl;
    }

    public void setApiUrl(String apiUrl) {
        this.apiUrl = apiUrl;
    }

    public String getIcoUrl() {
        return icoUrl;
    }

    public void setIcoUrl(String icoUrl) {
        this.icoUrl = icoUrl;
    }

    public String getOrganizeUrl() {
        return organizeUrl;
    }

    public void setOrganizeUrl(String organizeUrl) {
        this.organizeUrl = organizeUrl;
    }

    public String getOrganizeDesc() {
        return organizeDesc;
    }

    public void setOrganizeDesc(String organizeDesc) {
        this.organizeDesc = organizeDesc;
    }
}
