package com.omgodse.notally.activities

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Base64
import android.widget.Toast
import com.omgodse.notally.R
import com.omgodse.notally.miscellaneous.Operations
import com.omgodse.notally.miscellaneous.setOnNextAction
import com.omgodse.notally.room.Type
import java.io.ByteArrayOutputStream


const val AUTO_SAVE_INTERVAL = 5000L
class DrawNote : NotallyActivity(Type.DRAW), Runnable{
    lateinit var mHandler: Handler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.EnterTitle.setOnNextAction {
            binding.EnterBody.requestFocus()
        }

        setupAutoSave()

        if (model.isNewNote) {
            binding.EnterBody.requestFocus()
        }
    }

    override fun receiveSharedNote() {
        val title = intent.getStringExtra(Intent.EXTRA_SUBJECT)

        val string = intent.getStringExtra(Intent.EXTRA_TEXT)
        val charSequence = intent.getCharSequenceExtra(Operations.extraCharSequence)
        val drawing = charSequence ?: string

        if (drawing != null) {
            model.drawing = drawing as String
        }

        if (title != null) {
            model.title = title
        }

        Toast.makeText(this, R.string.saved_to_notally, Toast.LENGTH_SHORT).show()
    }

    override fun setStateFromModel() {
        super.setStateFromModel()
        val encodedBitmap = Base64.decode(model.drawing, Base64.DEFAULT)
        val bitmap = BitmapFactory.decodeByteArray(encodedBitmap, 0, encodedBitmap.size);
        bitmap?.let { binding.NoteView.setOverlayBitmap(bitmap) }
    }


    private fun setupAutoSave() {
        mHandler = Handler(Looper.getMainLooper())
        //mHandler.postDelayed(this, AUTO_SAVE_INTERVAL)
    }

    override fun run() {
        val bitmap = binding.NoteView.getOverlayBitmap()
        if (bitmap != null) {
            val stream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            val b: ByteArray = stream.toByteArray()
            val encodedBitmap = Base64.encodeToString(b, Base64.DEFAULT)
            model.drawing = encodedBitmap
        }
        mHandler.postDelayed(this, AUTO_SAVE_INTERVAL)
    }
}