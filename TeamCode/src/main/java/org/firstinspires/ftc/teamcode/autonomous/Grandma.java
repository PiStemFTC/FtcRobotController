package org.firstinspires.ftc.teamcode.autonomous;

import android.util.Size;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.hardware.camera.BuiltinCameraDirection;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.tfod.TfodProcessor;

import java.util.List;

public class Grandma {
    private static final boolean USE_WEBCAM = true;
    ArenaColor color;
    public Grandma(ArenaColor color){
        this.color = color;
    }

    public void initializeHardware(HardwareMap hardwareMap){
        leftFront = hardwareMap.get(DcMotor.class, "leftFrontDrive");
        rightFront = hardwareMap.get(DcMotor.class, "rightFrontDrive");
        leftBack = hardwareMap.get(DcMotor.class, "leftBackDrive");
        rightBack = hardwareMap.get(DcMotor.class, "rightBackDrive");
        clawLeft = hardwareMap.get(Servo.class, "clawLeft");
        clawRight = hardwareMap.get(Servo.class, "clawRight");

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

        initTfod(hardwareMap);
    }
    
    private DcMotor leftFront;
    private DcMotor leftBack;
    private DcMotor rightFront;
    private DcMotor rightBack;
    private DcMotor jointA = null;
    private Servo clawLeft = null;
    private Servo clawRight = null;
    private TfodProcessor tfod;
    private VisionPortal visionPortal;

    private void initTfod(HardwareMap hardwareMap) {
        String fileName = "TeamElementBlue.tflite";
        if(color.equals(ArenaColor.Red)){
            fileName = "TeamElement_Red.tflite";
        }
        // Create the TensorFlow processor by using a builder.
        tfod = new TfodProcessor.Builder()

                .setModelFileName(fileName)
                .setIsModelTensorFlow2(true)
                .setIsModelQuantized(true)
                .setModelInputSize(300)
                .setModelAspectRatio(16.0 / 9.0)

                .build();

        // Create the vision portal by using a builder.
        VisionPortal.Builder builder = new VisionPortal.Builder();

        if (USE_WEBCAM) {
            builder.setCamera(hardwareMap.get(WebcamName.class, "Webcam 1"));
        } else {
            builder.setCamera(BuiltinCameraDirection.BACK);
        }

        // Choose a camera resolution. Not all cameras support all resolutions.
        builder.setCameraResolution(new Size(1280, 720));

        // Enable the RC preview (LiveView).  Set "false" to omit camera monitoring.
        builder.enableLiveView(true);

        // Set the stream format; MJPEG uses less bandwidth than default YUY2.
        builder.setStreamFormat(VisionPortal.StreamFormat.MJPEG);

        // Choose whether or not LiveView stops if no processors are enabled.
        // If set "true", monitor shows solid orange screen if no processors enabled.
        // If set "false", monitor shows camera view without annotations.
        builder.setAutoStopLiveView(true);

        // Set and enable the processor.
        builder.addProcessor(tfod);

        // Build the Vision Portal, using the above settings.
        visionPortal = builder.build();

        // Set confidence threshold for TFOD recognitions, at any time.
        tfod.setMinResultConfidence(0.65f);

        // Disable or re-enable the TFOD processor at any time.
        visionPortal.setProcessorEnabled(tfod, true);

    }   // end method initTfod()

    public boolean isDuckVisible(){
        List<Recognition> currentRecognitions = tfod.getRecognitions();
        return 0 < currentRecognitions.size();
    }
    public boolean chaseDuck(Telemetry telemetry){
        final double width = 1280;
        final double height = 720;

        List<Recognition> currentRecognitions = tfod.getRecognitions();
        telemetry.addData("# Objects Detected", currentRecognitions.size());

        // Step through the list of recognitions and display info for each one.
        for (Recognition recognition : currentRecognitions) {
            double x = (recognition.getLeft() + recognition.getRight()) / 2;
            double y = (recognition.getTop() + recognition.getBottom()) / 2;
            double z = (x-(width/2));
            int turndeg = 0;
            float inches = 0;

            if(Math.abs(z)/width > 0.05){
                if(z > 0){
                    turndeg = 1;
                } else{
                    turndeg = -1;
                }
            }
            float e = (float) (height-recognition.getBottom());
            inches = (float) (e/23.0);

            if (turndeg != 0 || inches != 0)
            {
                move(inches, turndeg);
            } else{
                return true;
            }

            telemetry.addData("", " ");
            telemetry.addData("Image", "%s (%.0f %% Conf.)", recognition.getLabel(), recognition.getConfidence() * 100);
            telemetry.addData("- Position", "%.0f / %.0f", x, y);
            telemetry.addData("- Size", "%.0f x %.0f", recognition.getWidth(), recognition.getHeight());
            telemetry.addData("z","%.02f",z);

            break;
        }
        return false;
    }

    public void forward(float inches) {
        float amountPerInch = 152.4f;
        int lf, lb, rf, rb;
        lf = leftFront.getCurrentPosition();
        lb = leftBack.getCurrentPosition();
        rf = rightFront.getCurrentPosition();
        rb = rightBack.getCurrentPosition();

        int amount = (int) (amountPerInch * inches);

        leftFront.setTargetPosition(lf + amount);
        rightFront.setTargetPosition(rf + amount);
        rightBack.setTargetPosition(rb + amount);
        leftBack.setTargetPosition(lb + amount);

        while (leftFront.isBusy() || leftBack.isBusy() || rightFront.isBusy() || rightBack.isBusy()) ;
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
    public void move(float inches, int degree){
        float amountPerInch = 152.4f;
        int changePerDegree = 40;
        int lf, lb, rf, rb;
        lf = leftFront.getCurrentPosition();
        lb = leftBack.getCurrentPosition();
        rf = rightFront.getCurrentPosition();
        rb = rightBack.getCurrentPosition();

        int amount = (int)(amountPerInch * inches);

        leftFront.setTargetPosition(lf + amount + (degree * changePerDegree));
        leftBack.setTargetPosition(lb + amount + (degree * changePerDegree));
        rightFront.setTargetPosition(rf + amount - (degree * changePerDegree));
        rightBack.setTargetPosition(rb + amount - (degree * changePerDegree));

        while(leftFront.isBusy() || leftBack.isBusy() || rightFront.isBusy() || rightBack.isBusy());
    }

    public void openClaw(){
        openLeftClaw();
        openRightClaw();
    }

    public void closeClaw(){
        closeLeftClaw();
        closeRightClaw();
    }
     public void closeLeftClaw(){
         clawLeft.setPosition(0.65);
     }
    public void closeRightClaw(){
        clawRight.setPosition(0.35);
    }
    public void openLeftClaw(){
        clawLeft.setPosition(0.3);
    }
    public void openRightClaw(){
        clawRight.setPosition(0.7);
    }

    public void sleep(int i) {
    }
}
