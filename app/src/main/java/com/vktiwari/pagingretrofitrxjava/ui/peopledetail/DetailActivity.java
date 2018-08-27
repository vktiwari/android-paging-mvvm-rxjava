package com.vktiwari.pagingretrofitrxjava.ui.peopledetail;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.vktiwari.pagingretrofitrxjava.R;
import com.vktiwari.pagingretrofitrxjava.model.People;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_activity);
        init();

    }

    private void init() {
        People people = (People) getIntent().getParcelableExtra("data");
        TextView textName = findViewById(R.id.text_name);
        TextView textHeight = findViewById(R.id.text_height);
        TextView textMass = findViewById(R.id.text_mass);
        TextView textCreatedDate = findViewById(R.id.text_created_date);

        assert people != null;
        textName.setText(people.name);
        textHeight.setText(people.height);
        textMass.setText(people.mass);
        textCreatedDate.setText(people.created);
    }
}
