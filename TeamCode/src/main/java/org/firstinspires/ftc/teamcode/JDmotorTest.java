package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

@TeleOp
@Disabled
public class JDmotorTest extends LinearOpMode {
    DcMotor motor0;
    DcMotor motor1;
    DcMotor motor2;
    DcMotor motor3;

    private DcMotor[] motors;


    @Override
    public void runOpMode() {

        motor0 = hardwareMap.get(DcMotor.class, "motor0");
        motor1 = hardwareMap.get(DcMotor.class, "motor1");
        motor2 = hardwareMap.get(DcMotor.class, "motor2");
        motor3 = hardwareMap.get(DcMotor.class, "motor3");

        motors = new DcMotor[] {motor0, motor1, motor2, motor3};
        for(DcMotor motor : motors)
        {
            motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        }

        final float [] FrBwCorrections = new float[] {1, -1, 1, -1};
        for(DcMotor motor : motors)
        {
            motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        }

        final float [] strafe = new float[] {-1, 1, 1, -1};

        float [] strafeCorrected = new float[] {-1, 1, 1, -1};
        for(int i = 0; i < 4; i++)
        {
            strafeCorrected[i] = strafe[i] * FrBwCorrections[i];
        }

       // final float [] rot = new float[] {1, 1, 1, 1};

        //float [] rotCorrected = new float[] { 1, 1, 1, 1};
        //for(int i = 0; i < 4; i++)
        //{
        //    rotCorrected[i] = rot[i];
        //}

        final float [] rotCorrections = new float[] {1, 1, 1, 1};
        for(DcMotor motor : motors)
        {
            motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        }

        telemetry.addData("Status", "Initialized");
        telemetry.update();
        waitForStart();

        while (opModeIsActive()) {

           final float y1 = gamepad1.left_stick_y;
           final float x1 = gamepad1.left_stick_x;
           final float x2 = gamepad1.right_stick_x;

           for(int i = 0; i < 4; i++)
           {
               motors[i].setPower(y1*FrBwCorrections[i]
               + x1 * strafeCorrected[i]);
           }

            for(int i = 0; i < 4; i++)
            {
                motors[i].setPower(x2 * rotCorrections[i]);
            }


            //telemetry.addData("Path", "Complete");
            //sleep(1000);  // pause to display final telemetry message.
            telemetry.addData("x", gamepad1.left_stick_x);
            telemetry.addData("y", gamepad1.left_stick_y);
            telemetry.update();

        }
    }

}
