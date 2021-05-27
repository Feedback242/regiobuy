package de.uni_marburg.sp21;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;

import de.uni_marburg.sp21.data_structure.Company;

public class CompanyActivity extends AppCompatActivity {

    private Company company;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company);

        Intent intent = getIntent();
        company = intent.getParcelableExtra(MainActivity.INTENT_TAG);
    }
}