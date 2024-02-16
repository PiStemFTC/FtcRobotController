package org.firstinspires.ftc.teamcode.autonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

@Autonomous(name = "AutoRedRight")
public class AutoRedRight extends LinearOpMode {

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

        grandma = new Grandma(ArenaColor.Red);
        grandma.initializeHardware(hardwareMap);

        grandma.isDuckVisible();
        grandma.closeLeftClaw();
        grandma.closeRightClaw();
        sleep(500);
        grandma.setSlidePosition1();
        waitForStart();
        timeRef = System.currentTimeMillis();
        grandma.forward(8);
        grandma.setSlidePosition0();//move claw out of the way
        while(opModeIsActive()){
            long now = System.currentTimeMillis();
            long elapsedTime = now-timeRef;
            if(state == S_Look){
                if(grandma.isDuckVisible()){
                    grandma.setSlidePosition1();
                    sleep(200);
                    state = S_GoDuck;
                }else {
                    if(elapsedTime < 2000){
                        sleep(100);
                    }else{
                        if(Target == Center) {
                            Target = Left;
                            timeRef=now;
                            grandma.setSlidePosition1();
                            sleep(200);
                            grandma.turn(-25);
                            grandma.setSlidePosition0();
                            sleep(200);
                        } else if(Target == Left){
                            Target = Right;
                            timeRef=now;
                            grandma.setSlidePosition1();
                            sleep(200);
                            grandma.turn(50);
                            grandma.setSlidePosition0();
                            sleep(200);
                        } else{
                            state = S_GiveUp;
                        }
                    }
                }
            }else if(state == S_GoDuck){
                if(Target == Center) {
                    grandma.forward(15);
                    grandma.setSlidePosition1();
                    sleep(200);
                    grandma.openLeftClaw();
                    sleep(200);
                    grandma.forward(-6);
                    grandma.turn(-90);
                    grandma.forward(29);
                    grandma.openRightClaw();
                    grandma.setSlidePosition0();
                    state = S_Done;
                } else if(Target == Left){
                    grandma.turn(25);
                    grandma.forward(14);
                    grandma.turn(-37);
                    // grandma.forward(-2);
                    grandma.setSlidePosition1();
                    sleep(200);
                    grandma.openLeftClaw();
                    sleep(200);
                    grandma.forward(-2);
                    grandma.strafe(-12);
                    grandma.turn(-40);
                    grandma.forward(24);
                    grandma.openRightClaw();
                    state = S_Done;
                } else if(Target == Right){
                    grandma.turn(-25);
                    grandma.forward(14);
                    grandma.turn(60);
                    grandma.setSlidePosition1();
                    sleep(200);
                    // grandma.forward(-2);
                    grandma.openLeftClaw();
                    sleep(200);
                    grandma.forward(-6);
                    grandma.turn(-140);
                    grandma.forward(27);
                    grandma.openRightClaw();
                    state = S_Done;
                }
            } else if(state == S_GiveUp){

            }else if(state == S_Done){
                grandma.setSlidePosition0();

            }
            telemetry.addData("State", "%d", state);
            telemetry.addData("Elap Time", "%d", elapsedTime);
            telemetry.addData("seesDuck", "%b", grandma.isDuckVisible());
            telemetry.update();

        }

    }


}
