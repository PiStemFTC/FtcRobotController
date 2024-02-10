package org.firstinspires.ftc.teamcode.autonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;


@Autonomous(name = "AutoBlueLeft")
public class AutoBlueLeft extends LinearOpMode {

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
        grandma.setSlidePosition1();
        waitForStart();
        timeRef = System.currentTimeMillis();
        grandma.forward(8);
        while(opModeIsActive()){
            long now = System.currentTimeMillis();
            long elapsedTime = now-timeRef;
            if(state == S_Look){
                if(grandma.isDuckVisible()){
                    state = S_GoDuck;
                }else {
                   if(elapsedTime < 2000){
                       sleep(100);
                   }else{
                       if(Target == Center) {
                           Target = Left;
                           timeRef=now;
                           grandma.turn(-25);
                       } else if(Target == Left){
                           Target = Right;
                           timeRef=now;
                           grandma.turn(50);
                       } else{
                           state = S_GiveUp;
                       }
                   }
                }
            }else if(state == S_GoDuck){
                if(Target == Center) {
                    grandma.forward(15);
                    grandma.openLeftClaw();
                    sleep(200);
                    grandma.forward(-6);
                    state = S_Done;
                } else if(Target == Left){
                    grandma.turn(25);
                    grandma.forward(14);
                    grandma.turn(-37);
                   // grandma.forward(-2);
                    grandma.openLeftClaw();
                    sleep(200);
                    grandma.forward(-2);
                    state = S_Done;
                } else if(Target == Right){
                    grandma.turn(-25);
                    grandma.forward(14);
                    grandma.turn(60);
                    // grandma.forward(-2);
                    grandma.openLeftClaw();
                    sleep(200);
                    grandma.forward(-2);
                    state = S_Done;
                }
            } else if(state == S_GiveUp){

            }else if(state == S_Done){

            }
            telemetry.addData("State", "%d", state);
            telemetry.addData("Elap Time", "%d", elapsedTime);
            telemetry.addData("seesDuck", "%b", grandma.isDuckVisible());
            telemetry.update();

        }

    }


}
