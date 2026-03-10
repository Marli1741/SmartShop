package com.example.smartshop

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ShoppingViewModel : ViewModel() {
    val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private var snapshotListener: ListenerRegistration? = null

    private val _items = MutableStateFlow<List<ShoppingItem>>(emptyList())
    val items: StateFlow<List<ShoppingItem>> = _items

    init {
        if (auth.currentUser != null) {
            loadUserList()
        }
    }

    // AUTHENTICATION LOGIC
    fun login(username: String, pass: String, onResult: (Boolean, String) -> Unit) {
        val fakeEmail = "$username@smartshop.app"

        auth.signInWithEmailAndPassword(fakeEmail, pass).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                loadUserList()
                onResult(true, "Success")
            } else {
                onResult(false, task.exception?.message ?: "Login Failed")
            }
        }
    }

    fun signUp(username: String, pass: String, onResult: (Boolean, String) -> Unit) {
        val fakeEmail = "$username@smartshop.app"

        auth.createUserWithEmailAndPassword(fakeEmail, pass).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                loadUserList()
                onResult(true, "Success")
            } else {
                onResult(false, task.exception?.message ?: "Signup Failed")
            }
        }
    }

    fun logout() {
        auth.signOut()
        _items.value = emptyList()
        snapshotListener?.remove()
    }

    // CRUD LOGIC
    private fun getCollection() =
        db.collection("users").document(auth.currentUser!!.uid).collection("shopping_list")

    private fun loadUserList() {
        snapshotListener?.remove()
        snapshotListener = getCollection().addSnapshotListener { snapshot, _ ->
            if (snapshot != null) {
                val itemList =
                    snapshot.documents.mapNotNull { it.toObject(ShoppingItem::class.java) }
                _items.value = itemList.sortedBy { it.isPurchased }
            }
        }
    }

    fun addItem(name: String) {
        if (name.isBlank() || auth.currentUser == null) return
        val id = getCollection().document().id
        val newItem = ShoppingItem(id = id, name = name)
        getCollection().document(id).set(newItem)
    }

    fun togglePurchased(item: ShoppingItem) {
        if (auth.currentUser == null) return
        val updatedItem = item.copy(isPurchased = !item.isPurchased)
        getCollection().document(item.id).set(updatedItem)
    }

    fun deleteItem(itemId: String) {
        if (auth.currentUser == null) return
        getCollection().document(itemId).delete()
    }
}
