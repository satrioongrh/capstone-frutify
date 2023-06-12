package com.example.frutify.ui.dashboard.edit

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.frutify.MainActivity
import com.example.frutify.R
import com.example.frutify.data.viewmodel.ProductViewModel
import com.example.frutify.databinding.ActivityEditBinding
import com.example.frutify.ui.dashboard.auth.login.LoginActivity
import com.example.frutify.ui.dashboard.camera.CameraActivity
import com.example.frutify.utils.SharePref
import com.example.frutify.utils.Utility
import com.example.frutify.utils.Utility.rotateBitmap
import com.example.frutify.utils.rotateFile
import com.example.frutify.utils.uriToFile
import java.io.File

class EditActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditBinding
    private lateinit var productViewModel: ProductViewModel
    private lateinit var sharePref: SharePref

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        productViewModel = ViewModelProvider(this)[ProductViewModel::class.java]
        sharePref = SharePref(this)

        val productName = intent.getStringExtra("productName")
        val price = intent.getIntExtra("productPrice", 0)
//        binding.etFruit.setText(productName)
        binding.etPrice.setText(price.toString())

        //ask permissions
        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }


        binding.previewImage.setOnClickListener { startCameraX() }

        // Dropdown
        val spinnerFruit: Spinner = binding.etFruit
        // Item Dropdown
        val fruitOptions: ArrayList<String> = ArrayList()
        fruitOptions.add("Apel")
        fruitOptions.add("Pisang")
        fruitOptions.add("Jeruk")
        val fruitIdArray = arrayOf(1, 2, 3)

        val fruitAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, fruitOptions)
        fruitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerFruit.adapter = fruitAdapter

        val idFruit = spinnerFruit.selectedItemPosition


        //receive file from intent galery
        val myFile = intent.getSerializableExtra("pictureUri") as? File
        val ImageBitmap = BitmapFactory.decodeFile(myFile?.path)

        binding.previewImage.setImageBitmap(ImageBitmap)


        binding.btnSave.setOnClickListener {
            val fruit = fruitIdArray[idFruit]
            val userId = sharePref.getUserId
            val name = binding.etName.text.toString().trim()
            val desc = binding.etDesc.text.toString().trim()
            val price = binding.etPrice.text.toString().toInt()
            val unit = "kg"
            val filename = "apel"
            val quality = binding.tvQuality.text.toString().trim()
            addProduct(fruit, userId, name, desc, price, unit, filename, quality)
        }
    }

    private fun startCameraX() {
        val intent = Intent(this, CameraActivity::class.java)
        launcherIntentCameraX.launch(intent)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                Toast.makeText(
                    this,
                    "Tidak mendapatkan permission.",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }
    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private val launcherIntentCameraX = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == CAMERA_X_RESULT) {
            val myFile = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                it.data?.getSerializableExtra("picture", File::class.java)
            } else {
                @Suppress("DEPRECATION")
                it.data?.getSerializableExtra("picture")
            } as? File
            val isBackCamera = it.data?.getBooleanExtra("isBackCamera", true) as Boolean
            myFile?.let { file ->
                rotateFile(file, isBackCamera)
//                getFile = file
                binding.previewImage.setImageBitmap(BitmapFactory.decodeFile(file.path))
            }
        }
    }

    private fun addProduct(
        fruit_id: Int,
        user_id: Int,
        name: String,
        description: String,
        price: Int,
        unit: String,
        filename: String,
        quality: String
    ){
        productViewModel.addProduct(fruit_id, user_id, name, description, price, unit, filename, quality)
        productViewModel.isLoading.observe(this) {
            showLoading(it)
        }
        productViewModel.error.observe(this) { error ->
            // Show error message
            Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
        }
        productViewModel.addProductResult.observe(this) {
            Toast.makeText(this, it?.MESSAGE, Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    companion object {
        const val CAMERA_X_RESULT = 200
        private val REQUIRED_PERMISSIONS = arrayOf(android.Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }
}