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

        grandma = new Grandma(ArenaColor.Red);
        grandma.initializeHardware(hardwareMap);

        WHEEL_CIRCUMFERENCE_MM = 90 * Math.PI;
        HD_COUNTS_PER_REV = 28;
        DRIVE_GEAR_REDUCTION = 20.15293;
        DRIVE_COUNTS_PER_MM = (HD_COUNTS_PER_REV * DRIVE_GEAR_REDUCTION);
        DRIVE_COUNT_PER_IN = DRIVE_COUNTS_PER_MM * 25.4;

        waitForStart();
        if (opModeIsActive()){
            //2000 = 33.5 cm
            //60 per cm
            //152 per in
            grandma.turn(90);
            grandma.forward(46);
            sleep(2000);





        }

    }


}
