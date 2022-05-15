package me.jtx.flopac.checks.movement.step;

import me.jtx.flopac.base.check.api.Check;
import me.jtx.flopac.base.check.api.CheckInformation;

@CheckInformation(checkName = "Step", checkType = "B", description = "Checks if player goes up blocks non-legitimate", canPunish = false)
public class StepB extends Check {
}