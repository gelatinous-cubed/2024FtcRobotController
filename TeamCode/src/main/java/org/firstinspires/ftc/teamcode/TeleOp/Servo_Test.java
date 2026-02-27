package org.firstinspires.ftc.teamcode.TeleOp;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;

/*
 * This OpMode scans a single servo back and forward until Stop is pressed.
 * The code is structured as a LinearOpMode
 * INCREMENT sets how much to increase/decrease the servo position each cycle
 * CYCLE_MS sets the update period.
 *
 * This code assumes a Servo configured with the name "left_hand" as is found on a Robot.
 *
 * NOTE: When any servo position is set, ALL attached servos are activated, so ensure that any other
 * connected servos are able to move freely before running this test.
 *
 * Use Android Studio to Copy this Class, and Paste it into your team's code folder with a new name.
 * Remove or comment out the @Disabled line to add this OpMode to the Driver Station OpMode list
 */
@TeleOp(name = "Concept: Servo Test")
public class Servo_Test extends LinearOpMode {

    static final double MIN_POS     =  0.5;     // Minimum rotational position

    // Define class members
    Servo   servo;
    double  position = MIN_POS; // Start at halfway position


    @Override
    public void runOpMode() {

        // Connect to servo (Assume Robot Left Hand)
        // Change the text in quotes to match any servo name on your robot.
        servo = hardwareMap.get(Servo.class, "servo_claw");

        // Wait for the start button
        telemetry.addData(">", "Press Start to scan Servo." );
        telemetry.update();
        servo.setPosition(MIN_POS);
        waitForStart();
        telemetry.addData("IsActive", opModeIsActive());



        // Scan servo till stop pressed.
        while(opModeIsActive()){
            servo.setPosition(1);
            telemetry.addData("IsActive", opModeIsActive());

            // Display the current value
            telemetry.addData("Servo Position", "%5.2f", position);
            telemetry.addData(">", "Press Stop to end test." );
            telemetry.update();

            sleep(1000);

            servo.setPosition(MIN_POS);

            sleep(1000);
            // Set the servo to the new position and pause;
            // sidle();
        }
        telemetry.addData("IsActive", opModeIsActive());

        // Signal done;
        telemetry.addData(">", "Done");
        telemetry.update();
    }
}

