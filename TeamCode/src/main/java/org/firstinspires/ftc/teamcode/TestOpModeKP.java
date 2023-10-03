package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp

public class TestOpModeKP extends LinearOpMode {
    //imu = hardwareMap.get(Gyroscope.class, "imu");
//motorTest = hardwareMap.get(DcMotor.class, "motorTest");
//digitalTouch = hardwareMap.get(DigitalChannel.class, "digitalTouch");
//sensorColorRange = hardwareMap.get(DistanceSensor.class, "sensorColorRange");
    //private Gyroscope imu;
    //private DcMotor motorTest;
    //private DigitalChannel digitalTouch;
    //private DistanceSensor sensorColorRange;
    private Servo servo = hardwareMap.get(Servo.class, "servo");
    private long CYCLE_MS;
    private double position;


    @Override
    public void runOpMode() {

        telemetry.addData("Status", "Initialized");
        telemetry.update();
        // Wait for the game to start (driver presses PLAY)
        waitForStart();
    CYCLE_MS=100;
    position=0.0;
        // run until the end of the match (driver presses STOP)
        while (opModeIsActive()) {
            telemetry.addData("Status", "Not running");
            telemetry.update();
            servo.setPosition(position);
            position+=0.1;
            if(position>1.0){
                position=0.0;
            }
            sleep(CYCLE_MS);
            idle();
        }
    }
}