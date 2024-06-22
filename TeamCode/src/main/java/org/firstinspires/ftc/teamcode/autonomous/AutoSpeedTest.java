package org.firstinspires.ftc.teamcode.autonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;


@Autonomous(name = "AutoSpeedTest")
public class AutoSpeedTest extends LinearOpMode {

    private Grandma grandma;

    double DRIVE_COUNT_PER_IN;

    //@Override
    public void runOpMode(){
        double WHEEL_CIRCUMFERENCE_MM;
        int HD_COUNTS_PER_REV;
        double DRIVE_GEAR_REDUCTION;
        double DRIVE_COUNTS_PER_MM;
        final int Center = 1;
        final int Left = 2;
        final int Right = 3;
        int Target = Center;
        final int S_Look = 0;
        final int S_GoDuck = 1;
        final int S_GiveUp = 2;
        final int S_Done = 3;
        int state = S_Look;
        long timeRef = 0;

        grandma = new Grandma(ArenaColor.Blue);
        grandma.initializeHardware(hardwareMap);

        WHEEL_CIRCUMFERENCE_MM = 90 * Math.PI;
        HD_COUNTS_PER_REV = 28;
        DRIVE_GEAR_REDUCTION = 20.15293;
        DRIVE_COUNTS_PER_MM = (HD_COUNTS_PER_REV * DRIVE_GEAR_REDUCTION);
        DRIVE_COUNT_PER_IN = DRIVE_COUNTS_PER_MM * 25.4;

        grandma.isDuckVisible();
        grandma.closeLeftClaw();
        grandma.closeRightClaw();
        sleep(500);
        grandma.setSlidePosition1();
        waitForStart();
        timeRef = System.currentTimeMillis();
        grandma.helpImTrappedByTheRules();
        //grandma.forward(8);
        grandma.setSlidePosition0();//move claw out of the way
        //while(opModeIsActive()){
            //grandma.forward(24);
            //telemetry.addData("seesDuck", "%b", grandma.isDuckVisible());
        grandma.lookForAprilTag(hardwareMap,telemetry);
            telemetry.update();

       // }

    }


}

