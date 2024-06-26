/* Copyright (c) 2021 FIRST. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted (subject to the limitations in the disclaimer below) provided that
 * the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list
 * of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this
 * list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * Neither the name of FIRST nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written permission.
 *
 * NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
 * LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.firstinspires.ftc.teamcode.teleop;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.autonomous.ArenaColor;
import org.firstinspires.ftc.teamcode.autonomous.Grandma;

/*
 * This file contains an example of a Linear "OpMode".
 * An OpMode is a 'program' that runs in either the autonomous or the teleop period of an FTC match.
 * The names of OpModes appear on the menu of the FTC Driver Station.
 * When a selection is made from the menu, the corresponding OpMode is executed.
 *
 * This particular OpMode illustrates driving a 4-motor Omni-Directional (or Holonomic) robot.
 * This code will work with either a Mecanum-Drive or an X-Drive train.
 * Both of these drives are illustrated at https://gm0.org/en/latest/docs/robot-design/drivetrains/holonomic.html
 * Note that a Mecanum drive must display an X roller-pattern when viewed from above.
 *
 * Also note that it is critical to set the correct rotation direction for each motor.  See details below.
 *
 * Holonomic drives provide the ability for the robot to move in three axes (directions) simultaneously.
 * Each motion axis is controlled by one Joystick axis.
 *
 * 1) Axial:    Driving forward and backward               Left-joystick Forward/Backward
 * 2) Lateral:  Strafing right and left                     Left-joystick Right and Left
 * 3) Yaw:      Rotating Clockwise and counter clockwise    Right-joystick Right and Left
 *
 * This code is written assuming that the right-side motors need to be reversed for the robot to drive forward.
 * When you first test your robot, if it moves backward when you push the left stick forward, then you must flip
 * the direction of all 4 motors (see code below).
 *
 * Use Android Studio to Copy this Class, and Paste it into your team's code folder with a new name.
 * Remove or comment out the @Disabled line to add this OpMode to the Driver Station OpMode list
 */

@TeleOp(name="Omni Linear OpMode KP", group="Linear OpMode")
public class OmniOpMode_LinearKP extends LinearOpMode {

    // Declare OpMode members for each of the 4 motors.
    private ElapsedTime runtime = new ElapsedTime();
    private DcMotor leftFrontDrive = null;
    private DcMotor leftBackDrive = null;
    private DcMotor rightFrontDrive = null;
    private DcMotor rightBackDrive = null;
    private DcMotor linearSlide = null;
    private Servo launcher = null;
    private DcMotor jointA = null;
    private Servo clawLeft = null;
    private Servo clawRight = null;

	private Grandma grandma;
	boolean leftClawOpen = true;
	boolean rightClawOpen = true;
	long grabTimeRight = 0;
	long grabTimeLeft = 0;
	private void toggleLeftClaw(){
		long now = System.currentTimeMillis();
		if(now-grabTimeLeft < 500){
			return;
		}
		if(leftClawOpen){
			grandma.closeLeftClaw();
			leftClawOpen = false;
		} else{
			grandma.openLeftClaw();
			leftClawOpen = true;
		}
		grabTimeLeft = now;
	}
	private void toggleRightClaw(){
		long now = System.currentTimeMillis();
		if(now-grabTimeRight < 500){
			return;
		}
		if(rightClawOpen){
			grandma.closeRightClaw();
			rightClawOpen = false;
		} else{
			grandma.openRightClaw();
			rightClawOpen = true;
		}
		grabTimeRight = now;
	}
    @Override
    public void runOpMode() {
		final int SwivelIdle = 0;
		final int SwivelUp = 1;
		final int SwivelDown = 2;

		int swivelState = SwivelIdle;
		grandma = new Grandma(ArenaColor.Blue);
		grandma.mapWithoutInitialize(hardwareMap);
		// grandma.initializeHardware(hardwareMap);
		
		// Initialize the hardware variables. Note that the strings used here must correspond
		// to the names assigned during the robot configuration step on the DS or RC devices.
		leftFrontDrive = hardwareMap.get(DcMotor.class, "leftFrontDrive");
		leftBackDrive = hardwareMap.get(DcMotor.class, "leftBackDrive");
		rightFrontDrive = hardwareMap.get(DcMotor.class, "rightFrontDrive");
		rightBackDrive = hardwareMap.get(DcMotor.class, "rightBackDrive");
		linearSlide = hardwareMap.get(DcMotor.class, "linearSlide");
		launcher = hardwareMap.get(Servo.class, "launcher");
		jointA = hardwareMap.get(DcMotor.class, "jointA");
		clawLeft = hardwareMap.get(Servo.class, "clawLeft");
		clawRight = hardwareMap.get(Servo.class, "clawRight");




		//boolean currentPixelButtonState = false;

		// ########################################################################################
		// !!!            IMPORTANT Drive Information. Test your motor directions.            !!!!!
		// ########################################################################################
		// Most robots need the motors on one side to be reversed to drive forward.
		// The motor reversals shown here are for a "direct drive" robot (the wheels turn the same direction as the motor shaft)
		// If your robot has additional gear reductions or uses a right-angled drive, it's important to ensure
		// that your motors are turning in the correct direction.  So, start out with the reversals here, BUT
		// when you first test your robot, push the left joystick forward and observe the direction the wheels turn.
		// Reverse the direction (flip FORWARD <-> REVERSE ) of any wheel that runs backward
		// Keep testing until ALL the wheels move the robot forward when you push the left joystick forward.
		leftFrontDrive.setDirection(DcMotor.Direction.REVERSE);
		leftBackDrive.setDirection(DcMotor.Direction.REVERSE);
		rightFrontDrive.setDirection(DcMotor.Direction.FORWARD);
		rightBackDrive.setDirection(DcMotor.Direction.FORWARD);
		linearSlide.setDirection(DcMotor.Direction.REVERSE);

		jointA.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

		jointA.setTargetPosition(0);
		jointA.setMode(DcMotor.RunMode.RUN_TO_POSITION);

		jointA.setPower(0.5);

		jointA.setTargetPosition(0);

		// Wait for the game to start (driver presses PLAY)
		telemetry.addData("Status", "Initialized");
		telemetry.update();

		linearSlide.setTargetPosition(0);
		linearSlide.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
		linearSlide.setMode(DcMotor.RunMode.RUN_TO_POSITION);
		final int maxSlideThrow = 3000; //5000
		final int maxSwivel = 1000;

		grandma.closeClaw();
		launcher.setPosition(0.5);
		waitForStart();
		runtime.reset();

		int homePosition = linearSlide.getCurrentPosition();
		int swivelTarget = 0;
		int swivelMax = 90;

		int xstate = 0;
		long xtime = 0;

		linearSlide.setPower(.5);

		// run until the end of the match (driver presses STOP)
		while (opModeIsActive()) {
			double max;


			// POV Mode uses left joystick to go forward & strafe, and right joystick to rotate.
			double axial = -gamepad1.left_stick_y;  // Note: pushing stick forward gives negative value
			double lateral = gamepad1.left_stick_x;
			double yaw = gamepad1.right_stick_x;
			boolean launch = gamepad1.x;
			double swivel = -gamepad2.left_stick_y;
			double extend = -gamepad2.right_stick_y;
			boolean pickupPosition = gamepad2.a;
			boolean placementPosition = gamepad2.x;
			//boolean openL = gamepad1.left_bumper;
			//boolean openR = gamepad1.right_bumper;
			//boolean closeR = gamepad2.right_trigger > 0.1;
			//boolean closeL = gamepad2.left_trigger > 0.1;


			if(pickupPosition){ //if a is being held down
				linearSlide.setTargetPosition(homePosition+235); //pickup position
				jointA.setPower(0);
			} else if(placementPosition) { //if x button is being held down
				xstate = 1;
				linearSlide.setTargetPosition(homePosition+2000);//placement position
				jointA.setPower(.5);
				jointA.setTargetPosition(80);
			} else {
				linearSlide.setTargetPosition(homePosition); //drive position (default)
				if (xstate == 1) {
					xtime = System.currentTimeMillis();
					xstate = 2;
					jointA.setTargetPosition(15);
					jointA.setPower(0.0);
				} else if (xstate == 2) {
					long now = System.currentTimeMillis();
					if (now - xtime >= 1000) {
						jointA.setPower(0.0);
						xstate = 0;
					} else if (now - xtime >= 100) {
						jointA.setPower(0.15);
					} else {
						jointA.setPower(0.25);

					}
				}
				//while(linearSlide.isBusy());
				//jointA.setPower(-0.75);
				//long startP = jointA.getCurrentPosition();
				//long cP;
				//do{
				//	cP = jointA.getCurrentPosition();
				//}while(cP >= startP - 90);
				//jointA.setPower(0.25);

				//try {Thread.sleep(1000); }catch(InterruptedException e){}
				//jointA.setPower(0.0);
			}



			// Combine the joystick requests for each axis-motion to determine each wheel's power.
			// Set up a variable for each drive wheel to save the power level for telemetry.
			double leftFrontPower = axial + lateral + yaw;
			double rightFrontPower = axial - lateral - yaw;
			double leftBackPower = axial - lateral + yaw;
			double rightBackPower = axial + lateral - yaw;

			if (launch) {
				launcher.setPosition(0.0);
			} else {
				launcher.setPosition(0.5);
			}

			// check to see if we need to move the servo.
			if (gamepad2.left_bumper || gamepad2.left_trigger > 0.25) {
				toggleLeftClaw();
			}
            // check to see if we need to move the servo.
            if (gamepad2.right_bumper || gamepad2.right_trigger > 0.25) {
				toggleRightClaw();
            }

            // Normalize the values so no wheel power exceeds 100%
            // This ensures that the robot maintains the desired motion.
            max = Math.max(Math.abs(leftFrontPower), Math.abs(rightFrontPower));
            max = Math.max(max, Math.abs(leftBackPower));
            max = Math.max(max, Math.abs(rightBackPower));

            if (max > 1.0) {
				leftFrontPower /= max;
				rightFrontPower /= max;
				leftBackPower /= max;
				rightBackPower /= max;
            }

            // This is test code:
            //
            // Uncomment the following code to test your motor directions.
            // Each button should make the corresponding motor run FORWARD.
            //   1) First get all the motors to take to correct positions on the robot
            //      by adjusting your Robot Configuration if necessary.
            //   2) Then make sure they run in the correct direction by modifying the
            //      the setDirection() calls above.
            // Once the correct motors move in the correct direction re-comment this code.

			/*
			  leftFrontPower  = gamepad1.x ? 1.0 : 0.0;  // X gamepad
			  leftBackPower   = gamepad1.a ? 1.0 : 0.0;  // A gamepad
			  rightFrontPower = gamepad1.y ? 1.0 : 0.0;  // Y gamepad
			  rightBackPower  = gamepad1.b ? 1.0 : 0.0;  // B gamepad
			*/

            // Send calculated power to wheels
            leftFrontDrive.setPower(leftFrontPower);
            rightFrontDrive.setPower(rightFrontPower);
            leftBackDrive.setPower(leftBackPower);
            rightBackDrive.setPower(rightBackPower);

//            if (linearSlide.getCurrentPosition() > maxSlideThrow) {
//				/* Only allow retraction (negative) when over max throw */
//				if (extend < 0.0)
//					linearSlide.setPower(extend);
//				else
//					linearSlide.setPower(0.0); // stop the motor
//            } else if (linearSlide.getCurrentPosition() <= 80) {
//				/* Only allow extension (positive) when we're near the initial position */
//				if (extend > 0.0)
//					linearSlide.setPower(extend);
//				else
//					linearSlide.setPower(0); // stop the motor
//            } else if (linearSlide.getCurrentPosition() <= 500) {
//				if (extend < 0.0)
//					linearSlide.setPower(extend * .25);
//				else
//					linearSlide.setPower(extend);
//            } else {
//				linearSlide.setPower(extend);
//            }

			if (swivelTarget < swivelMax && swivel > 0.0) {
				/* Allow to increase */
				swivelTarget += swivel * 2.5;
				swivelState = SwivelUp;
			}
			else if ((swivelTarget > -10 || gamepad2.y) && swivel < 0.0) {
				/* Allow to decrease */
				swivelTarget += swivel * 2.5;
				swivelState = SwivelDown;
			}
			else if (swivel == 0.0) {
				if (SwivelIdle != swivelState) {
					swivelTarget = jointA.getCurrentPosition();
					swivelState = SwivelIdle;
				}
			}

//			if (-10 <= swivelTarget && swivelTarget <= swivelMax) {
//				jointA.setTargetPosition(swivelTarget);
//			}
//			else if (swivelTarget < -10) {
//				swivelTarget = -10;
//			}
//			else if (swivelTarget > swivelMax) {
//				swivelTarget = swivelMax;
//			}



			telemetry.addData("swivel target", "%d", swivelTarget);

            // Show the elapsed game time and wheel power.
            telemetry.addData("Status", "Run Time: " + runtime.toString());
            telemetry.addData("Front left/Right", "%4.2f, %4.2f", leftFrontPower, rightFrontPower);
            telemetry.addData("Back  left/Right", "%4.2f, %4.2f", leftBackPower, rightBackPower);
            telemetry.addData("Slide position", "%d, %f", linearSlide.getCurrentPosition(), extend);
            telemetry.addData("Joint position", "%d", jointA.getCurrentPosition());
            telemetry.addData("Target position", "%d", jointA.getTargetPosition());
			telemetry.addData("xstate", "%d", xstate);
            telemetry.update();
		}
	}
}

