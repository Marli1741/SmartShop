package com.example.smartshop

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch
import androidx.core.view.isGone

class MainActivity : AppCompatActivity() {

    private val viewModel: ShoppingViewModel by viewModels()
    private lateinit var adapter: ShoppingAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val loginOverlay = findViewById<ConstraintLayout>(R.id.loginOverlay)
        val etUsername = findViewById<TextInputEditText>(R.id.etUsername)
        val etPassword = findViewById<TextInputEditText>(R.id.etPassword)
        val btnLogin = findViewById<MaterialButton>(R.id.btnLogin)
        val btnSignUp = findViewById<MaterialButton>(R.id.btnSignUp)

        val topAppBar = findViewById<MaterialToolbar>(R.id.topAppBar)
        val emptyStateLayout = findViewById<LinearLayout>(R.id.emptyStateLayout)
        val etItemName = findViewById<EditText>(R.id.etItemName)
        val btnAdd = findViewById<FloatingActionButton>(R.id.btnAdd)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)


        topAppBar.menu.add("Log Out").setShowAsAction(android.view.MenuItem.SHOW_AS_ACTION_ALWAYS)
        topAppBar.setOnMenuItemClickListener {
            viewModel.logout()
            findViewById<ConstraintLayout>(R.id.loginOverlay).visibility = View.VISIBLE
            true
        }

        // Check Initial Auth State
        if (viewModel.auth.currentUser != null) {
            loginOverlay.visibility = View.GONE
        }

// AUTHENTICATION CLICKS
        btnLogin.setOnClickListener {
            val username = etUsername.text.toString().trim()
            val pass = etPassword.text.toString()
            if (username.isNotEmpty() && pass.isNotEmpty()) {
                viewModel.login(username, pass) { success, msg ->
                    if (success) {
                        loginOverlay.visibility = View.GONE
                        etUsername.text?.clear()
                        etPassword.text?.clear()

                        // Check the list size immediately after hiding the login screen
                        if (viewModel.items.value.isEmpty()) {
                            emptyStateLayout.visibility = View.VISIBLE
                            recyclerView.visibility = View.GONE
                        } else {
                            emptyStateLayout.visibility = View.GONE
                            recyclerView.visibility = View.VISIBLE
                        }
                    } else Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Please enter a username and password", Toast.LENGTH_SHORT).show()
            }
        }

        btnSignUp.setOnClickListener {
            val username = etUsername.text.toString().trim()
            val pass = etPassword.text.toString()
            if (username.isNotEmpty() && pass.isNotEmpty()) {
                if (pass.length < 6) {
                    Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                viewModel.signUp(username, pass) { success, msg ->
                    if (success) {
                        loginOverlay.visibility = View.GONE
                        etUsername.text?.clear()
                        etPassword.text?.clear()

                        // Check the list size immediately after hiding the login screen
                        if (viewModel.items.value.isEmpty()) {
                            emptyStateLayout.visibility = View.VISIBLE
                            recyclerView.visibility = View.GONE
                        } else {
                            emptyStateLayout.visibility = View.GONE
                            recyclerView.visibility = View.VISIBLE
                        }
                    } else Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Please enter a username and password", Toast.LENGTH_SHORT).show()
            }
        }
        // APP LOGIC CLICKS
        adapter = ShoppingAdapter(
            items = emptyList(),
            onToggle = { item -> viewModel.togglePurchased(item) },
            onDelete = { itemId -> viewModel.deleteItem(itemId) }
        )
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        btnAdd.setOnClickListener {
            val itemName = etItemName.text.toString()
            if (itemName.isNotEmpty()) {
                viewModel.addItem(itemName)
                etItemName.text.clear()
            }
        }

        // Observe Data and Toggle Empty State
        lifecycleScope.launch {
            viewModel.items.collect { itemsList ->
                adapter.updateData(itemsList)
                if (itemsList.isEmpty() && loginOverlay.isGone) {
                    emptyStateLayout.visibility = View.VISIBLE
                    recyclerView.visibility = View.GONE
                } else {
                    emptyStateLayout.visibility = View.GONE
                    recyclerView.visibility = View.VISIBLE
                }
            }
        }
    }
}