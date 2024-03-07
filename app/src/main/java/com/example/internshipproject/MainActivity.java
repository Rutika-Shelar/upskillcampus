package com.example.internshipproject;

import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.internshipproject.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private TextView textViewLightIntensity;
    private Switch modeControl;
    private DatabaseReference lightIntensityRef;
    private int lightIntensity ;
    private ImageSwitcher imageViewLightBulb;
    private Switch lightController;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textViewLightIntensity = findViewById(R.id.textViewLightIntensity);
        modeControl = findViewById(R.id.modeControl);
        imageViewLightBulb = findViewById(R.id.imageViewLightBulb); // Initialize ImageSwitcher
        lightController=findViewById(R.id.LightControl);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        lightIntensityRef = database.getReference("sensors").child("sensor1");


        lightIntensityRef.child("light_intensity").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                lightIntensity = dataSnapshot.getValue(Integer.class); // Update lightIntensity
                Log.d("FirebaseData", "Light Intensity: " + lightIntensity);
                textViewLightIntensity.setText("Light Intensity: " + lightIntensity);
                updateUI(lightIntensity, modeControl.isChecked());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                showToast("Failed to retrieve light intensity data: " + databaseError.getMessage());
            }
        });

        modeControl.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    showToast("Auto settings enabled");
                    lightController.setVisibility(View.INVISIBLE);


                } else {
                    showToast("Manual settings enabled");
                    lightController.setVisibility(View.VISIBLE);

                    lightController.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if (isChecked) {
                                imageViewLightBulb.setImageResource(R.drawable.bulb_on);

                            } else {
                                imageViewLightBulb.setImageResource(R.drawable.bulb_off);
                            }

                        }
                    });

                }
                // Update UI based on the new switch state and current light intensity
                updateUI(lightIntensity,modeControl.isChecked());
            }
        });

        imageViewLightBulb.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                ImageView imageView = new ImageView(MainActivity.this);
                imageView.setLayoutParams(new ImageSwitcher.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT));
                return imageView;
            }
        });
        imageViewLightBulb.setInAnimation(this, android.R.anim.fade_in);
        imageViewLightBulb.setOutAnimation(this, android.R.anim.fade_out);


    }








    private void updateUI(int lightIntensity, boolean isAutoModeEnabled) {
        // Display the light intensity on the screen
        textViewLightIntensity.setText("Light Intensity: " + lightIntensity);

        if (isAutoModeEnabled) {
            // Auto mode is enabled
            if (lightIntensity < 50) {
                // Light intensity is below 50, turn on the switch and show bulb-on image
                imageViewLightBulb.setImageResource(R.drawable.bulb_on);
                showToast("lights should be turned on");
            } else {
                // Light intensity is 50 or more, turn off the switch and show bulb-off image
                imageViewLightBulb.setImageResource(R.drawable.bulb_off);
                showToast("lights should be turned off");
            }
        } else {
            // Manual mode is enabled
            if (lightController.isChecked()) {
                // Switch is checked, show bulb-on image
                imageViewLightBulb.setImageResource(R.drawable.bulb_on);
            } else if(!lightController.isChecked()) {
                // Switch is unchecked, show bulb-off image
                imageViewLightBulb.setImageResource(R.drawable.bulb_off);
            }
        }
    }



    private void showToast (String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
