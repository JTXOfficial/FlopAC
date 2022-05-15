package me.jtx.flopac.base.user.objects;

import me.jtx.flopac.util.LogUtil;

public class LogObject
{
    public String uuid;
    public LogUtil logUtil;
    public String name;
    
    public LogObject(String uuid) {
        this.uuid = uuid;
        this.logUtil = new LogUtil();
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getUuid() {
        return this.uuid;
    }
    
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
