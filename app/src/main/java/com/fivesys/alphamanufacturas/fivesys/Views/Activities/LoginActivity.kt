package com.fivesys.alphamanufacturas.fivesys.Views.Activities

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.fivesys.alphamanufacturas.fivesys.R
import android.support.design.widget.TextInputLayout
import android.support.design.widget.TextInputEditText
import android.view.View
import android.widget.Button

class LoginActivity : AppCompatActivity(), View.OnClickListener {

    override fun onClick(v: View) {
        when (v.id) {
            R.id.buttonEnviar -> startActivity(Intent(this@LoginActivity, MainActivity::class.java))
        }
    }

    private lateinit var editTextUser: TextInputEditText
    private lateinit var editTextPass: TextInputEditText
    private lateinit var editTextPassError: TextInputLayout
    private lateinit var editTextUserError: TextInputLayout
    private lateinit var buttonEnviar: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        bindUI()
    }

    private fun bindUI() {
        editTextUser = findViewById(R.id.editTextUser)
        editTextPass = findViewById(R.id.editTextPass)
        editTextUserError = findViewById(R.id.editTextUserError)
        editTextPassError = findViewById(R.id.editTextPassError)
        buttonEnviar = findViewById(R.id.buttonEnviar)
        buttonEnviar.setOnClickListener(this)
    }

}
