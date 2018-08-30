package com.vktiwari.pagingretrofitrxjava.ui.peopledetail

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.widget.TextView
import com.vktiwari.pagingretrofitrxjava.R
import com.vktiwari.pagingretrofitrxjava.model.People
import com.vktiwari.pagingretrofitrxjava.utils.Utils

class DetailActivity : AppCompatActivity() {

    protected override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.detail_activity)
        init()
    }

    private fun init() {
        val people = intent.getParcelableExtra("data") as People
        val textName = findViewById<TextView>(R.id.text_name)
        val textHeight = findViewById<TextView>(R.id.text_height)
        val textMass = findViewById<TextView>(R.id.text_mass)
        val textCreatedDate = findViewById<TextView>(R.id.text_created_date)

        textName.text = people.name
        textHeight.text = if (TextUtils.isEmpty(people.height)) "" else Utils.convertCentimetreToMeter(java.lang.Float.parseFloat(people.height)).toString().plus(" metres")
        textMass.text = if (TextUtils.isEmpty(people.mass)) "" else people.mass + " kg"
        textCreatedDate.text = people.created
    }
}