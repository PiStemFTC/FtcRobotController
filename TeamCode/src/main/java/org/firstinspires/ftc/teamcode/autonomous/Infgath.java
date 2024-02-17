package org.firstinspires.ftc.teamcode.autonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

@Autonomous(name = "Infgath")
public class Infgath extends LinearOpMode {
    private Grandma grandma;
    public void runOpMode() {
        grandma = new Grandma(ArenaColor.Red);
        grandma.initializeHardware(hardwareMap);
        grandma.closeRightClaw();
        grandma.closeLeftClaw();

        waitForStart();
        //grandma.swivel.setPower(0.0);
        grandma.helpImTrappedByTheRules();
        while(opModeIsActive()){
            telemetry.addData("help", "%d", grandma.swivel.getCurrentPosition());
            telemetry.update();
        }
    }
}
