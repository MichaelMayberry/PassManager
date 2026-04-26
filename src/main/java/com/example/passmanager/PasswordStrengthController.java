package com.example.passmanager;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;

/**
 * This is for the password strength checker in box 4. We have a score calculated based on the amount of upper and
 *  lowercase letters as well as numbers and symbols. This is strictly used for the user to precheck how strong
 *  their passwords are.
 *
 */
public class PasswordStrengthController
{
    @FXML
    private PasswordField passwordField;

    @FXML
    private Label resultLabel;

    /**
     * Password strengths are checked by password length, uppercase letters, lowercase letters, symbols and numbers.
     * The score is calculated by seeing if every field is met and if it is it relates to a high score if not it
     * depends on what was calculated
     */
    @FXML
    private void onCheckClick()
    {
        String password = passwordField.getText();

        if (password.isEmpty())
        {
            resultLabel.setText("Please enter a password.");
            resultLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16px;");
            return;
        }

        int score = 0;

        if (password.length() >= 8)
        {
            score++;
        }
        if (password.length() >= 12)
        {
            score++;
        }
        if (password.length() >= 16)
        {
            score++;
        }

        boolean hasUpper  = false;
        boolean hasLower  = false;
        boolean hasDigit  = false;
        boolean hasSymbol = false;

        for (char c : password.toCharArray())
        {
            if (Character.isUpperCase(c))
            {
                hasUpper  = true;
            }
            else if (Character.isLowerCase(c))
            {
                hasLower  = true;
            }
            else if (Character.isDigit(c))
            {
                hasDigit = true;
            }
            else if (!Character.isLetterOrDigit(c))
            {
                hasSymbol = true;
            }
        }

        if (hasUpper)
        {
            score++;
        }
        if (hasLower)
        {
            score++;
        }
        if (hasDigit)
        {
            score++;
        }
        if (hasSymbol)
        {
            score++;
        }

        String rating;
        String color;

        if (score <= 2)
        {
            rating = "Low";
            color  = "#ef4444";
        }
        else if (score <= 4)
        {
            rating = "Okay";
            color  = "#f97316";
        }
        else if (score == 5)
        {
            rating = "Medium";
            color  = "#eab308";
        }
        else
        {
            rating = "Strong";
            color  = "#22c55e";
        }

        resultLabel.setText("Strength: " + rating);
        resultLabel.setStyle("-fx-text-fill: " + color + "; -fx-font-size: 16px;");
    }

    /**
     * Closes password-strength-popup-fxml
     */
    @FXML
    private void onCancelClick()
    {
        Stage stage = (Stage) passwordField.getScene().getWindow();
        stage.close();
    }
}
