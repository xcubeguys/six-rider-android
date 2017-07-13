package com.tommy.rider;

import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Password;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.tommy.rider.adapter.FontChangeCrawler;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.List;

@EActivity (R.layout.activity_signup_password)
public class SignupPassword extends MyBaseActivity implements Validator.ValidationListener {

    public String firstName,lastName,email,passWord,conform_passWord,nickName;
    Validator validator;

    @NotEmpty (message = "")
    @Password(min = 8, message = "Enter a Minimimum of 8 characters")
    @ViewById (R.id.view)
    MaterialEditText inputPassword;

    @NotEmpty (message = "")
    @Password(min = 8, message = "Enter a Minimimum of 8 characters")
    @ViewById (R.id.view2)
    MaterialEditText inputConfirmPassword;

    @AfterViews
    void signUpPassword() {
        FontChangeCrawler fontChanger = new FontChangeCrawler(getAssets(), getString(R.string.app_font));
        fontChanger.replaceFonts((ViewGroup) this.findViewById(android.R.id.content));
        validator = new Validator(this);
        validator.setValidationListener(this);

        Intent i = getIntent();
        firstName = i.getStringExtra("firstname");
        lastName = i.getStringExtra("lastname");
        nickName = i.getStringExtra("nickname");
        email = i.getStringExtra("email");

    }

    @Click({R.id.imageButton3,R.id.imageButton2})
    void toSignUpMobile() {
        validator.validate();
    }

    @Click (R.id.backButton)
    public void goBack() {
        finish();
    }

    @Override
    public void onValidationSucceeded() {
        passWord = inputPassword.getText().toString();
        conform_passWord = inputConfirmPassword.getText().toString();
        passWord=passWord.replaceAll(" ","%20");
        conform_passWord=conform_passWord.replaceAll(" ","%20");

        if(passWord.equals(conform_passWord)){
            /*try {
                byte[] encoded = Base64.encode(passWord.getBytes("UTF-8"), Base64.DEFAULT);
                passWord = new String(encoded, "UTF-8");
                System.out.println("Encoding UTF"+passWord);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }*/
            Intent i = new Intent(SignupPassword.this,ReferralActivity_.class);
            i.putExtra("firstname",firstName);
            i.putExtra("lastname",lastName);
            i.putExtra("email",email);
            i.putExtra("password",passWord);
            i.putExtra("nickname",nickName);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
        } else{
            inputConfirmPassword.setError("Confirm Password does not match");
        }
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        for (ValidationError error : errors) {
            View view = error.getView();
            String message = error.getCollatedErrorMessage(this);

            // Display error messages ;)
            if (view instanceof EditText) {
                ((EditText) view).setError(message);
            } else {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            }
        }
    }
}
