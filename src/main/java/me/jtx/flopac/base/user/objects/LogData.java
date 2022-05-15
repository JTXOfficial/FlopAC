package me.jtx.flopac.base.user.objects;

import lombok.Getter;
import lombok.Setter;
import me.jtx.flopac.FlopAC;

@Getter
@Setter
public class LogData {
    public LogObject getUser(String uuid) {
        for (LogObject user : FlopAC.getInstance().getLogObjectList()) {
            if (user.getUuid().equalsIgnoreCase(uuid)) {
                return user;
            }
        }
        return null;
    }

    public void addUser(LogObject user) {
        if (!FlopAC.getInstance().getLogObjectList().contains(user)) {
            FlopAC.getInstance().getLogObjectList().add(user);
        }
    }

    public void removeUser(LogObject user) {
        if (FlopAC.getInstance().getLogObjectList().contains(user)) {
            FlopAC.getInstance().getLogObjectList().remove(user);
        }
    }
}