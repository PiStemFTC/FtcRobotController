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
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;
import org.firstinspires.ftc.vision.tfod.TfodProcessor;

import java.util.List;

public class Grandma {
    private static final boolean USE_WEBCAM = true;
    private static final int DESIRED_TAG_ID = -1;
    ArenaColor color;


    private boolean mirror = false;
    public Grandma(ArenaColor color){
        this.color = color;
        if(this.color == ArenaColor.Red){
            mirror = true;
        }
    }

    public void mapWithoutInitialize(HardwareMap hardwareMap){
        leftFront = hardwareMap.get(DcMotor.class, "leftFrontDrive");
        rightFront = hardwareMap.get(DcMotor.class, "rightFrontDrive");
        leftBack = hardwareMap.get(DcMotor.class, "leftBackDrive");
        rightBack = hardwareMap.get(DcMotor.class, "rightBackDrive");
        clawLeft = hardwareMap.get(Servo.class, "clawLeft");
        clawRight = hardwareMap.get(Servo.class, "clawRight");
        linearSlide = hardwareMap.get(DcMotor.class, "linearSlide");
    }

    public void initializeHardware(HardwareMap hardwareMap){
        leftFront = hardwareMap.get(DcMotor.class, "leftFrontDrive");
        rightFront = hardwareMap.get(DcMotor.class, "rightFrontDrive");
        leftBack = hardwareMap.get(DcMotor.class, "leftBackDrive");
        rightBack = hardwareMap.get(DcMotor.class, "rightBackDrive");
        clawLeft = hardwareMap.get(Servo.class, "clawLeft");
        clawRight = hardwareMap.get(Servo.class, "clawRight");
        linearSlide = hardwareMap.get(DcMotor.class, "linearSlide");
        swivel = hardwareMap.get(DcMotor.class, "jointA");
        light = hardwareMap.get(Servo.class, "light");


        leftBack.setDirection(DcMotor.Direction.REVERSE);
        leftFront.setDirection(DcMotor.Direction.REVERSE);
        linearSlide.setDirection(DcMotor.Direction.REVERSE);
        //swivel.setDirection(DcMotor.Direction.REVERSE);


        leftBack.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        leftFront.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightBack.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightFront.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        linearSlide.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        swivel.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);


        leftBack.setTargetPosition(0);
        leftFront.setTargetPosition(0);
        rightBack.setTargetPosition(0);
        rightFront.setTargetPosition(0);
        linearSlide.setTargetPosition(0);
        swivel.setTargetPosition(0);


        leftBack.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        leftFront.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        rightBack.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        rightFront.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        linearSlide.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        swivel.setMode(DcMotor.RunMode.RUN_TO_POSITION);


        leftBack.setPower(0.65);
        leftFront.setPower(0.65);
        rightBack.setPower(0.65);
        rightFront.setPower(0.65);
        linearSlide.setPower(1.0);
        swivel.setPower(0.1);


        initTfod(hardwareMap);
    }
    
    private DcMotor leftFront;
    private DcMotor leftBack;
    private DcMotor rightFront;
    private DcMotor rightBack;
    public DcMotor swivel = null;
    private Servo clawLeft = null;
    private Servo clawRight = null;
    private Servo light = null;
    private TfodProcessor tfod;
    private VisionPortal visionPortal;

    private AprilTagProcessor aprilTag;              // Used for managing the AprilTag detection process.
    private AprilTagDetection desiredTag = null;
    private DcMotor linearSlide;

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

    private void initAprilTag(HardwareMap hardwareMap) {
        // Create the AprilTag processor by using a builder.
        aprilTag = new AprilTagProcessor.Builder().build();

        // Adjust Image Decimation to trade-off detection-range for detection-rate.
        // eg: Some typical detection data using a Logitech C920 WebCam
        // Decimation = 1 ..  Detect 2" Tag from 10 feet away at 10 Frames per second
        // Decimation = 2 ..  Detect 2" Tag from 6  feet away at 22 Frames per second
        // Decimation = 3 ..  Detect 2" Tag from 4  feet away at 30 Frames Per Second
        // Decimation = 3 ..  Detect 5" Tag from 10 feet away at 30 Frames Per Second
        // Note: Decimation can be changed on-the-fly to adapt during a match.
        aprilTag.setDecimation(2);

        // Create the vision portal by using a builder.
        if (USE_WEBCAM) {
            visionPortal = new VisionPortal.Builder()
                    .setCamera(hardwareMap.get(WebcamName.class, "Webcam 1"))
                    .addProcessor(aprilTag)
                    .build();
        } else {
            visionPortal = new VisionPortal.Builder()
                    .setCamera(BuiltinCameraDirection.BACK)
                    .addProcessor(aprilTag)
                    .build();
        }
    }
    public void lookForAprilTag(HardwareMap hardwareMap, Telemetry telemetry) {
        visionPortal.setProcessorEnabled(tfod, false);
        initAprilTag(hardwareMap);
        boolean targetFound = false;
        desiredTag  = null;

        // Step through the list of detected tags and look for a matching tag
        List<AprilTagDetection> currentDetections = aprilTag.getDetections();
        for (AprilTagDetection detection : currentDetections) {
            // Look to see if we have size info on this tag.
            if (detection.metadata != null) {
                //  Check to see if we want to track towards this tag.
                if ((DESIRED_TAG_ID < 0) || (detection.id == DESIRED_TAG_ID)) {
                    // Yes, we want to use this tag.
                    targetFound = true;
                    desiredTag = detection;
                    break;  // don't look any further.
                } else {
                    // This tag is in the library, but we do not want to track it right now.
                    telemetry.addData("Skipping", "Tag ID %d is not desired", detection.id);
                }
            } else {
                // This tag is NOT in the library, so we don't have enough information to track to it.
                telemetry.addData("Unknown", "Tag ID %d is not in TagLibrary", detection.id);
            }
        }
        if (targetFound) {
            telemetry.addData("\n>","HOLD Left-Bumper to Drive to Target\n");
            telemetry.addData("Found", "ID %d (%s)", desiredTag.id, desiredTag.metadata.name);
            telemetry.addData("Range",  "%5.1f inches", desiredTag.ftcPose.range);
            telemetry.addData("Bearing","%3.0f degrees", desiredTag.ftcPose.bearing);
            telemetry.addData("Yaw","%3.0f degrees", desiredTag.ftcPose.yaw);
        } else {
            telemetry.addData("\n>","Drive using joysticks to find valid target\n");
        }
    }

    public void helpImTrappedByTheRules_(){
        light.setPosition(-0.59);
        swivel.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        linearSlide.setTargetPosition(linearSlide.getCurrentPosition()+900);
        while(linearSlide.isBusy());
        swivel.setPower(-0.75);
        //swivel.setTargetPosition(-73);
        try {Thread.sleep(850); }catch(InterruptedException e){}
        swivel.setPower(0.3);
        //sleep(5000);
        try {Thread.sleep(1000); }catch(InterruptedException e){}
        swivel.setPower(0.0);
        linearSlide.setTargetPosition(250);
        while(linearSlide.isBusy());
        swivel.setPower(0.0);
        light.setPosition(0.87);
    }

    public void helpImTrappedByTheRules(){
        light.setPosition(-0.59);
        swivel.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        linearSlide.setTargetPosition(linearSlide.getCurrentPosition()+900);
        while(linearSlide.isBusy());
        swivel.setPower(-0.75);
        //swivel.setTargetPosition(-73);
        long startP = swivel.getCurrentPosition();
        long cP;
        do{
            cP = swivel.getCurrentPosition();
        }while(cP >= startP - 90);
        //try {Thread.sleep(850); }catch(InterruptedException e){}
        swivel.setPower(0.25);
        //sleep(5000);
        try {Thread.sleep(1000); }catch(InterruptedException e){}
        swivel.setPower(0.0);
        linearSlide.setTargetPosition(250);
        while(linearSlide.isBusy());
        swivel.setPower(0.0);
        light.setPosition(0.87);
    }

    public boolean isDuckVisible(){
        final double width = 1280;
        final double height = 720;

        List<Recognition> currentRecognitions = tfod.getRecognitions();

        // Step through the list of recognitions and display info for each one.
        for (Recognition recognition : currentRecognitions) {
            double x = (recognition.getLeft() + recognition.getRight()) / 2;
            double z = (x-(width/2));

            if (Math.abs(z) / width < 0.45) {
                return true;
            }
        }
        return false;
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
        float amountPerInch = 76.2f;
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
        if (mirror)degree = -degree;
        float changePerDegree = 20.5f;
        float a = degree;

        int lf, lb, rf, rb;
        lf = leftFront.getCurrentPosition();
        lb = leftBack.getCurrentPosition();
        rf = rightFront.getCurrentPosition();
        rb = rightBack.getCurrentPosition();

        leftFront.setTargetPosition(lf + (int)(a * changePerDegree));
        leftBack.setTargetPosition(lb + (int)(a * changePerDegree));
        rightFront.setTargetPosition(rf - (int)(a * changePerDegree));
        rightBack.setTargetPosition(rb - (int)(a * changePerDegree));

        while(leftFront.isBusy() || leftBack.isBusy() || rightFront.isBusy() || rightBack.isBusy());
    }
    public void move(float inches, int degree){
        float amountPerInch = 76.2f;
        int changePerDegree = 20;
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

    public void strafe(int inches) {
        if (mirror)inches = -inches;
        float amountPerInch = 76.2f;
        int lf, lb, rf, rb;
        lf = leftFront.getCurrentPosition();
        lb = leftBack.getCurrentPosition();
        rf = rightFront.getCurrentPosition();
        rb = rightBack.getCurrentPosition();

        int amount = (int)(amountPerInch * inches);

        leftFront.setTargetPosition(lf + amount);
        rightFront.setTargetPosition(rf - amount);
        rightBack.setTargetPosition(rb + amount);
        leftBack.setTargetPosition(lb - amount);

        while(leftFront.isBusy() || leftBack.isBusy() || rightFront.isBusy() || rightBack.isBusy());
    }

    public void moveRobot(double x, double y, double yaw) {
        // Calculate wheel powers.
        double leftFrontPower    =  x -y -yaw;
        double rightFrontPower   =  x +y +yaw;
        double leftBackPower     =  x +y -yaw;
        double rightBackPower    =  x -y +yaw;

        // Normalize wheel powers to be less than 1.0
        double max = Math.max(Math.abs(leftFrontPower), Math.abs(rightFrontPower));
        max = Math.max(max, Math.abs(leftBackPower));
        max = Math.max(max, Math.abs(rightBackPower));

        if (max > 1.0) {
            leftFrontPower /= max;
            rightFrontPower /= max;
            leftBackPower /= max;
            rightBackPower /= max;
        }

        // Send powers to the wheels.
        leftFront.setPower(leftFrontPower);
        rightFront.setPower(rightFrontPower);
        leftBack.setPower(leftBackPower);
        rightBack.setPower(rightBackPower);
    }
    public void openClaw(){
        openLeftClaw();
        openRightClaw();
    }

    public void closeClaw(){
        closeLeftClaw();
        closeRightClaw();
    }
     private void closeLeftClaw_(){
         clawLeft.setPosition(0.68);
     }
    private void closeRightClaw_(){
        clawRight.setPosition(0.30);
    }
    private void openLeftClaw_(){
        clawLeft.setPosition(0.3);
    }
    private void openRightClaw_(){
        clawRight.setPosition(0.7);
    }
    public void closeLeftClaw(){
        if(mirror)closeRightClaw_();
        else closeLeftClaw_();
    }
    public void closeRightClaw(){
        if(mirror)closeLeftClaw_();
        else closeRightClaw_();
    }
    public void openLeftClaw(){
        if(mirror)openRightClaw_();
        else openLeftClaw_();
    }
    public void openRightClaw(){
        if(mirror)openLeftClaw_();
        else openRightClaw_();
    }

    public void sleep(int i) {
    }

    public void setSlidePosition1(){
       //linearSlide.setTargetPosition(500);
        //swivel.setTargetPosition(20);

    }
    public void setSlidePosition2(){

        //linearSlide.setTargetPosition(600);
    }

    public void setSlidePosition3(){

        //linearSlide.setTargetPosition(600);
    }
    public void setSlidePosition0(){
        //linearSlide.setTargetPosition(0);
        //swivel.setTargetPosition(0);
    }
     public void setPlacementPosition(){
         linearSlide.setTargetPosition(2000);//placement position
         swivel.setPower(.5);
         swivel.setTargetPosition(80);
     }
}
