package com.example.fruitrecognizer

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeler
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.automl.AutoMLImageLabelerLocalModel
import com.google.mlkit.vision.label.automl.AutoMLImageLabelerOptions
import com.google.mlkit.vision.label.automl.AutoMLImageLabelerRemoteModel
import kotlinx.android.synthetic.main.activity_main.*
import java.io.IOException

class MainActivity : AppCompatActivity() {
private val code=123
    private lateinit var labeler:ImageLabeler
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        button.setOnClickListener{
            textView.text = " "
           intent=Intent()
            intent.type = "image/"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(
                Intent.createChooser(intent,"Choose image"),
                this.code)
        }
        val localModel = AutoMLImageLabelerLocalModel.Builder()
            .setAssetFilePath("modelFile/manifest.json")
            // or .setAbsoluteFilePath(absolute file path to manifest file)
            .build()
        val autoMLImageLabelerOptions = AutoMLImageLabelerOptions.Builder(localModel)
            .setConfidenceThreshold(0F)  // Evaluate your model in the Firebase console
            // to determine an appropriate value.
            .build()
         labeler = ImageLabeling.getClient(autoMLImageLabelerOptions)





}
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==code)
        {
            if (data != null) {
                imageView.setImageURI(data.data)
                val image: InputImage
                try {
                    image = InputImage.fromFilePath(
                        applicationContext, data.data!!
                    )
                    labeler.process(image)
                        .addOnSuccessListener {
                            for (label in it) {
                                val text = label.text
                                val confidence = label.confidence
                                val index = label.index
                                textView.append("$text $confidence\n")
                            }
                        }
                        .addOnFailureListener { e ->
                            // Task failed with an exception
                            // ...
                        }
                } catch (e: IOException) {
                    e.printStackTrace()
                }


            }
        }
    }
}