package org.firstinspires.ftc.teamcode.autonomous;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class Grandma {
    Grandma(){
    }

    public void initializeHardware(HardwareMap hardwareMap){
        leftFront = hardwareMap.get(DcMotor.class, "leftFrontDrive");
        rightFront = hardwareMap.get(DcMotor.class, "rightFrontDrive");
        leftBack = hardwareMap.get(DcMotor.class, "leftBackDrive");
        rightBack = hardwareMap.get(DcMotor.class, "rightBackDrive");
        servo = hardwareMap.get(Servo.class, "servo");

        leftBack.setDirection(DcMotor.Direction.REVERSE);
        leftFront.setDirection(DcMotor.Direction.REVERSE);

        leftBack.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        leftFront.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightBack.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightFront.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        leftBack.setTargetPosition(0);
        leftFront.setTargetPosition(0);
        rightBack.setTargetPosition(0);
        rightFront.setTargetPosition(0);

        leftBack.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        leftFront.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        rightBack.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        rightFront.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        leftBack.setPower(1.0);
        leftFront.setPower(1.0);
        rightBack.setPower(1.0);
        rightFront.setPower(1.0);
    }
    
    private DcMotor leftFront;
    private DcMotor leftBack;
    private DcMotor rightFront;
    private DcMotor rightBack;
    private Servo servo;

    public void forward(float inches){
        float amountPerInch = 152.4f;
        int lf, lb, rf, rb;
        lf = leftFront.getCurrentPosition();
        lb = leftBack.getCurrentPosition();
        rf = rightFront.getCurrentPosition();
        rb = rightBack.getCurrentPosition();

        int amount = (int)(amountPerInch * inches);

        leftFront.setTargetPosition(lf + amount);
        rightFront.setTargetPosition(rf + amount);
        rightBack.setTargetPosition(rb + amount);
        leftBack.setTargetPosition(lb + amount);

        while(leftFront.isBusy() || leftBack.isBusy() || rightFront.isBusy() || rightBack.isBusy());
    }

    public void turn(int degree){
        int changePerDegree = 40;
        int lf, lb, rf, rb;
        lf = leftFront.getCurrentPosition();
        lb = leftBack.getCurrentPosition();
        rf = rightFront.getCurrentPosition();
        rb = rightBack.getCurrentPosition();

        leftFront.setTargetPosition(lf + (degree * changePerDegree));
        leftBack.setTargetPosition(lb + (degree * changePerDegree));
        rightFront.setTargetPosition(rf - (degree * changePerDegree));
        rightBack.setTargetPosition(rb - (degree * changePerDegree));

        while(leftFront.isBusy() || leftBack.isBusy() || rightFront.isBusy() || rightBack.isBusy());
    }

    public void servoMin(){
        servo.setPosition(0.0);
    }

    public void servoMax(){
        servo.setPosition(1.0);
    }
}
