package com.example.tictactoe.util

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

data class GameMessage(
    val type: String, // "MOVE", "JOIN", "RESTART", "QUIT"
    val moveIndex: Int = -1,
    val player: String = "" // "X" or "O"
)

class FirebaseManager {
    private val TAG = "FirebaseManager"
    
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance("https://tictactoe-app-61d1c-default-rtdb.asia-southeast1.firebasedatabase.app/")
    
    private var currentRoomCode: String = ""
    private var valueEventListener: ValueEventListener? = null
    private var roomRef: DatabaseReference? = null
    
    private val gson = Gson()

    private val _messageFlow = MutableSharedFlow<GameMessage>(extraBufferCapacity = 10)
    val messageFlow = _messageFlow.asSharedFlow()

    fun connect(roomCode: String, onConnected: (Boolean, String?) -> Unit) {
        this.currentRoomCode = roomCode
        
        if (auth.currentUser == null) {
            auth.signInAnonymously().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    setupRoomListener(roomCode, onConnected)
                } else {
                    val error = task.exception?.message ?: "Auth failed"
                    Log.e(TAG, "Auth failed: $error")
                    onConnected(false, error)
                }
            }
        } else {
            setupRoomListener(roomCode, onConnected)
        }
    }

    private fun setupRoomListener(roomCode: String, onConnected: (Boolean, String?) -> Unit) {
        roomRef = database.getReference("rooms").child(roomCode)
        
        valueEventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    try {
                        val payload = snapshot.getValue(String::class.java)
                        if (payload != null) {
                            val gameMsg = gson.fromJson(payload, GameMessage::class.java)
                            _messageFlow.tryEmit(gameMsg)
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Failed to parse message", e)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "DatabaseError: ${error.message}")
            }
        }
        
        roomRef?.addValueEventListener(valueEventListener!!)
        onConnected(true, null)
    }

    fun sendMessage(message: GameMessage, onComplete: ((Boolean) -> Unit)? = null) {
        if (currentRoomCode.isNotEmpty()) {
            val payload = gson.toJson(message)
            roomRef?.setValue(payload)
                ?.addOnSuccessListener {
                    onComplete?.invoke(true)
                }
                ?.addOnFailureListener { e ->
                    Log.e(TAG, "Failed to send message", e)
                    onComplete?.invoke(false)
                }
        } else {
            onComplete?.invoke(false)
        }
    }

    fun disconnect() {
        valueEventListener?.let {
            roomRef?.removeEventListener(it)
        }
        valueEventListener = null
        roomRef = null
        currentRoomCode = ""
    }
}
