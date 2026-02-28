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

package org.firstinspires.ftc.teamcode.TeleOp;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

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

@TeleOp(name="HillCrest Qualifier", group="Linear OpMode")
public class BasicOmniOpMode_Linear extends LinearOpMode {

    private double precise_movement(double joystick_numb) {
        double sign = 1;
        if (joystick_numb < 0) {
            sign = -1;
        }
        joystick_numb = joystick_numb * joystick_numb;
        joystick_numb = joystick_numb * sign;
        return joystick_numb;
    }

    // Declare OpMode members for each of the 4 motors.
    private ElapsedTime runtime = new ElapsedTime();
    private ElapsedTime servoTime = new ElapsedTime();

    private ElapsedTime autoBallShootingTime = new ElapsedTime();
    private DcMotor leftFrontDrive = null;
    private DcMotor leftBackDrive = null;
    private DcMotor rightFrontDrive = null;
    private DcMotor rightBackDrive = null;
    private double currentLeftFrontPower = 0;
    private double currentRightFrontPower = 0;
    private double currentLeftBackPower = 0;
    private double currentRightBackPower = 0;
    private boolean isServoOpen = false;
    private boolean isShootingBall = false;
    private DcMotor motor = null;
    private Servo servo = null;
    private final double CLOSE_POS = 0.5;
    private final double OPEN_POS = 0.75;
    private final double POWER = 0.87;
    static final double AUTO_SPEED = 0.6;
    static final double DEADZONE = 0.01;

    private double timeLapse = 2.0;

    @Override
    public void runOpMode() {

        // Initialize the hardware variables. Note that the strings used here must correspond
        // to the names assigned during the robot configuration step on the DS or RC devices.
        leftFrontDrive = hardwareMap.get(DcMotor.class, "left_front_drive");
        leftBackDrive = hardwareMap.get(DcMotor.class, "left_back_drive");
        rightFrontDrive = hardwareMap.get(DcMotor.class, "right_front_drive");
        rightBackDrive = hardwareMap.get(DcMotor.class, "right_back_drive");

        // stopping skidding behavior that happens with breaking
        leftFrontDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        leftBackDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightFrontDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightBackDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        motor = hardwareMap.get(DcMotor.class, "wheel");
        servo = hardwareMap.get(Servo.class, "servo");


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
        leftFrontDrive.setDirection(DcMotor.Direction.FORWARD);
        leftBackDrive.setDirection(DcMotor.Direction.FORWARD);
        rightFrontDrive.setDirection(DcMotor.Direction.REVERSE);
        rightBackDrive.setDirection(DcMotor.Direction.REVERSE);

        servo.setPosition(CLOSE_POS);

        // Wait for the game to start (driver presses START)
        telemetry.addData("Status", "Initialized");
        telemetry.update();

        waitForStart();
        runtime.reset();

        // run until the end of the match (driver presses STOP)
        while (opModeIsActive()) {

            // Right Bumper on gamepad 2 to control servo
            if (gamepad2.right_bumper && !this.isServoOpen) { // only works when servo is closed
                servo.setPosition(OPEN_POS);
                servoTime.reset();
                isServoOpen = true;
//                telemetry.addData("Servo Set Position", "%5.2f", OPEN_POS);
//                telemetry.update();


                // Close servo after .8 seconds automatically
                if (isServoOpen && servoTime.seconds() > 0.8) { // servo will shut after 8 seconds
                    servo.setPosition(CLOSE_POS);
                    isServoOpen = false; // mark that servo is closed
                }

                // Press gamepad 2 motor and run the motor
                if (gamepad2.right_trigger > 0) {
                    motor.setPower(Math.min(gamepad2.right_trigger, POWER)); // speed never exceeds 0.87
                } else {
                    motor.setPower(0);
                }

                // Press A on gamepad2 and enter shooting ball mode and wait 2 seconds to open servo
                if (gamepad2.a && !this.isShootingBall) {
                    this.autoBallShootingTime.reset();
                    this.isShootingBall = true;
                    motor.setPower(POWER);
                    this.timeLapse = 2;
                    this.isServoOpen = false;
                }

                // Auto open servo every .8 seconds and close every .9 seconds
                if (this.isShootingBall && autoBallShootingTime.seconds() > timeLapse) {
                    if (this.isServoOpen) {
                        servo.setPosition(CLOSE_POS);
                        timeLapse = 0.8;
                    } else {
                        servo.setPosition(OPEN_POS);
                        timeLapse = 0.9;
                    }
                    isServoOpen = !isServoOpen;
                    autoBallShootingTime.reset();
                }

                // Press B to stop auto ball shooting
                if (gamepad2.b && this.isShootingBall) {
                    // telemetry.addData(">", "B Pressed.");
                    //  telemetry.addData(">", "Ending...");
                    servo.setPosition(CLOSE_POS);
                    motor.setPower(0);
                    this.isShootingBall = false;
                    this.isServoOpen = false;
                    autoBallShootingTime.reset();
                    //  telemetry.addData(">", "Done");
                }

                // Automatically drive forward for 2.1 seconds
                if (gamepad2.x) {
                    leftFrontDrive.setPower(-AUTO_SPEED);
                    leftBackDrive.setPower(AUTO_SPEED);
                    rightFrontDrive.setPower(-AUTO_SPEED);
                    rightBackDrive.setPower(AUTO_SPEED);
                    runtime.reset();

                    while (opModeIsActive() && (runtime.seconds() < 2.1)) {
                        telemetry.addData("Path", "Leg 3: %4.1f S Elapsed", runtime.seconds());
                        telemetry.update();
                    }

                    // Stop
                    leftFrontDrive.setPower(0);
                    leftBackDrive.setPower(0);
                    rightFrontDrive.setPower(0);
                    rightBackDrive.setPower(0);
//                telemetry.addData("Path", "Complete");
//                telemetry.update();
                }

                double max;

                // POV Mode uses left joystick to go forward & strafe, and right joystick to rotate.
                double axial = -gamepad1.left_stick_y; // Note: pushing stick forward gives negative value
                double lateral = gamepad1.left_stick_x;
                double yaw = gamepad1.right_stick_x;
                yaw = precise_movement(yaw);

                //            telemetry.addData("axial", axial);
                //            telemetry.addData("lateral", lateral);
                //            telemetry.addData("yaw", yaw);

                // Combine the joystick requests for each axis-motion to determine each wheel's power.
                // Set up a variable for each drive wheel to save the power level for telemetry.
                double leftFrontPower = axial + lateral + yaw;
                double rightFrontPower = axial - lateral - yaw;
                double leftBackPower = axial - lateral + yaw;
                double rightBackPower = axial + lateral - yaw;

                // removing skidding with the robot
                if (Math.abs(leftFrontPower) < DEADZONE) {
                    leftFrontPower = 0;
                }
                if (Math.abs(rightFrontPower) < DEADZONE) {
                    rightFrontPower = 0;
                }
                if (Math.abs(leftBackPower) < DEADZONE) {
                    leftBackPower = 0;
                }
                if (Math.abs(rightBackPower) < DEADZONE) {
                    rightBackPower = 0;
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

                // smoothing out how fast it speeds up
                currentLeftFrontPower += (leftFrontPower - currentLeftFrontPower) * 0.1;
                currentRightFrontPower += (rightFrontPower - currentRightFrontPower) * 0.1;
                currentLeftBackPower += (leftBackPower - currentLeftBackPower) * 0.1;
                currentRightBackPower += (rightBackPower - currentRightBackPower) * 0.1;

                // Send calculated power to wheels
                leftFrontDrive.setPower(currentLeftFrontPower);
                rightFrontDrive.setPower(currentRightFrontPower);
                leftBackDrive.setPower(currentLeftBackPower);
                rightBackDrive.setPower(currentRightBackPower);

                // Show the elapsed game time and wheel power.
                telemetry.addData("Status", "Run Time: " + runtime.toString());
                telemetry.addData("Front left/Right", "%4.2f, %4.2f", currentLeftFrontPower, currentRightFrontPower);
                telemetry.addData("Back  left/Right", "%4.2f, %4.2f", currentLeftBackPower, currentRightBackPower);
                telemetry.update();
            }
        }
    }
}

