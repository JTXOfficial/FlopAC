package me.jtx.flopac.base.check.impl;

import lombok.Getter;
import me.jtx.flopac.FlopAC;
import me.jtx.flopac.base.check.api.Check;
import me.jtx.flopac.base.user.User;

import java.util.LinkedList;
import java.util.List;

@Getter
public class CheckManager {
    private final List<Check> checkList = new LinkedList<>();

    public void setupChecks(User user) {
        this.checkList.addAll(FlopAC.getInstance().getCheckManager().cloneChecks());
        this.checkList.forEach(check -> check.setupTimers(user));
    }
}