package com.phonegame.to;

import java.io.Serializable;
import java.util.Date;


public class UserGames implements Serializable{

    private static final long serialVersionUID=1L;

    private Integer id;
    
    private Integer userId;
    
    private Integer gameId;
    
    private Integer roleId;
    
    private String roleName;
    
    private Integer gradeId;
    
    private Integer serverId;
    
    private String serverName;
    
    private Date lastLoginDate;

    private Integer loginCNT;
    
    private Integer payCNT;
    
    private Integer amount;
    
    private Integer payTotalAmount;
    
    private String lastIP;
    
    private String lastUA;
    
    private String imei;
    
    private String macAddress;
    
    public Integer getId() {
        return id;
    }

    
    public void setId(Integer id) {
        this.id=id;
    }

    
    public Integer getUserId() {
        return userId;
    }

    
    public void setUserId(Integer userId) {
        this.userId=userId;
    }

    
    public Integer getGameId() {
        return gameId;
    }

    
    public void setGameId(Integer gameId) {
        this.gameId=gameId;
    }

    
    public Integer getRoleId() {
        return roleId;
    }

    
    public void setRoleId(Integer roleId) {
        this.roleId=roleId;
    }

    
    public String getRoleName() {
        return roleName;
    }

    
    public void setRoleName(String roleName) {
        this.roleName=roleName;
    }

    
    public Integer getServerId() {
        return serverId;
    }

    
    public void setServerId(Integer serverId) {
        this.serverId=serverId;
    }

    
    public String getServerName() {
        return serverName;
    }

    
    public void setServerName(String serverName) {
        this.serverName=serverName;
    }

    
    public Date getLastLoginDate() {
        return lastLoginDate;
    }

    
    public void setLastLoginDate(Date lastLoginDate) {
        this.lastLoginDate=lastLoginDate;
    }

    public Integer getLoginCNT() {
        return loginCNT;
    }

    public void setLoginCNT(Integer loginCNT) {
        this.loginCNT=loginCNT;
    }


    
    public Integer getPayCNT() {
        return payCNT;
    }


    
    public void setPayCNT(Integer payCNT) {
        this.payCNT=payCNT;
    }


    
    public Integer getPayTotalAmount() {
        return payTotalAmount;
    }


    
    public void setPayTotalAmount(Integer payTotalAmount) {
        this.payTotalAmount=payTotalAmount;
    }


    
    public String getLastIP() {
        return lastIP;
    }


    
    public void setLastIP(String lastIP) {
        this.lastIP=lastIP;
    }


    
    public String getLastUA() {
        return lastUA;
    }


    
    public void setLastUA(String lastUA) {
        this.lastUA=lastUA;
    }


    
    public String getImei() {
        return imei;
    }


    
    public void setImei(String imei) {
        this.imei=imei;
    }


    
    public Integer getGradeId() {
        return gradeId;
    }


    
    public void setGradeId(Integer gradeId) {
        this.gradeId=gradeId;
    }


    
    public Integer getAmount() {
        return amount;
    }


    
    public void setAmount(Integer amount) {
        this.amount=amount;
    }


    
    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress=macAddress;
    }
    
    
}
