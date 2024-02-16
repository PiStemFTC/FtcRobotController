package org.firstinspires.ftc.teamcode.teleop;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp(name="Launcher Test", group="Linear OpMode")
public class LauncherTest extends LinearOpMode{
    private Servo launcher = null;
    @Override
    public void runOpMode() {
        waitForStart();
        launcher = hardwareMap.get(Servo.class,"launcher");
    while (opModeIsActive()) {
        if (gamepad1.x) {
            launcher.setPosition(1.0);
        } else {
            launcher.setPosition(0);
        }
    }
    }

}
