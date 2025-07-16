package com.usedocetangerinaestoque.presentation.ui

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.bumptech.glide.Glide
import com.usedocetangerinaestoque.R
import com.usedocetangerinaestoque.data.entities.Category
import com.usedocetangerinaestoque.data.entities.Size
import com.usedocetangerinaestoque.presentation.viewmodel.AddItemViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import kotlin.properties.Delegates

@AndroidEntryPoint
class AddItemActivity : BaseDrawerActivity() {

    private lateinit var editName: EditText
    private lateinit var editDescription: EditText
    private lateinit var editValue: EditText
    private lateinit var editQuantity: EditText
    private lateinit var spinnerSize: Spinner
    private lateinit var spinnerCategory: Spinner
    private lateinit var btnSave: Button
    private lateinit var btnAddSize: ImageButton
    private lateinit var btnAddCategory: ImageButton
    private lateinit var btnSelectImages: Button
    private lateinit var imagePreviewContainer: LinearLayout

    private lateinit var pickImagesLauncher: ActivityResultLauncher<Array<String>>

    private val viewModel: AddItemViewModel by viewModels()

    private var selectedImagePaths = mutableListOf<String>()

    private var editItemId by Delegates.notNull<Long>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_item)

        editItemId = intent.getLongExtra("editItemId", -1L)
        if (editItemId != -1L) {
            viewModel.loadItemForEdit(editItemId)
        }

        pickImagesLauncher = registerForActivityResult(
            ActivityResultContracts.OpenMultipleDocuments()
        ) { uris ->
            uris.let {
                val paths = it.mapNotNull { uri -> saveImageToInternalStorage(uri) }
                //selectedImagePaths.clear()
                selectedImagePaths.addAll(paths)
                showImagePreviews()
            }
        }

        initViews()
        setupSpinners()
        observeViewModel()

        btnSelectImages.setOnClickListener {
            pickImagesLauncher.launch(arrayOf("image/*"))
        }
    }

    private fun initViews() {
        editName       = findViewById(R.id.editName)
        editDescription= findViewById(R.id.editDescription)
        editValue      = findViewById(R.id.editValue)
        editQuantity   = findViewById(R.id.editQuantity)
        spinnerSize    = findViewById(R.id.spinnerSize)
        spinnerCategory= findViewById(R.id.spinnerCategory)
        btnSave        = findViewById(R.id.btnSave)
        btnAddSize     = findViewById(R.id.btnAddSize)
        btnAddCategory = findViewById(R.id.btnAddCategory)
        btnSelectImages = findViewById(R.id.btnSelectImages)
        imagePreviewContainer = findViewById(R.id.imagePreviewContainer)
    }

    private fun setupSpinners() {
        viewModel.sizes.observe(this) { sizes ->
            if (sizes.isNullOrEmpty()) {
                showToast("Nenhum tamanho disponível.")
                return@observe
            }

            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, sizes)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerSize.adapter = adapter
        }

        viewModel.categories.observe(this) { categories ->
            if (categories.isNullOrEmpty()) {
                showToast("Nenhuma categoria disponivel.")
                return@observe
            }

            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerCategory.adapter = adapter
        }

        btnAddSize.setOnClickListener {
            showInputDialog("Novo tamanho") { name ->
                viewModel.addSize(name)
            }
        }

        btnAddCategory.setOnClickListener {
            showInputDialog("Nova categoria") { name ->
                viewModel.addCategory(name)
            }
        }

        btnSelectImages.setOnClickListener {
            pickImagesLauncher.launch(arrayOf("image/*"))
        }

        btnSave.setOnClickListener {
            val name     = editName.text.toString()
            val desc     = editDescription.text.toString()
            val value    = editValue.text.toString().toDoubleOrNull() ?: -1.0
            val quantity = editQuantity.text.toString().toIntOrNull() ?: -1
            val size     = spinnerSize.selectedItem as? Size
            val category = spinnerCategory.selectedItem as? Category

            if (name.isBlank() || desc.isBlank()) {
                showToast("Preencha nome e descrição.")
                return@setOnClickListener
            }
            if (value < 0 || quantity < 0) {
                showToast("Valor ou quantidade inválidos.")
                return@setOnClickListener
            }
            if (size == null || category == null) {
                showToast("Selecione tamanho e categoria.")
                return@setOnClickListener
            }

            if (editItemId != -1L) {
                viewModel.updateItem(editItemId, name, desc, value, quantity, size.id, category.id, selectedImagePaths)
            } else {
                viewModel.saveItem(name, desc, value, quantity, size.id, category.id, selectedImagePaths)
            }
        }
    }

    private fun observeViewModel() {
        viewModel.saveStatus.observe(this) { success ->
            if (success) {
                showToast("Item salvo com sucesso.")
                finish()
            }
        }

        viewModel.error.observe(this) { message ->
            message?.let {
                showToast(it)
                viewModel.clearError()
            }
        }

        viewModel.loading.observe(this) { isLoading ->
            btnSave.isEnabled = !isLoading
        }

        viewModel.editItem.observe(this) { item ->
            item?.let {
                editName.setText(it.item.name)
                editDescription.setText(it.item.description)
                editValue.setText(it.item.value.toString())
                editQuantity.setText(it.item.quantity.toString())

                val sizeIndex = viewModel.sizes.value?.indexOfFirst { s -> s.id == it.size?.id } ?: -1
                if (sizeIndex >= 0) spinnerSize.setSelection(sizeIndex)

                val categoryIndex = viewModel.categories.value?.indexOfFirst { c -> c.id == it.category?.id } ?: -1
                if (categoryIndex >= 0) spinnerCategory.setSelection(categoryIndex)

                selectedImagePaths.clear()
                selectedImagePaths.addAll(it.images.map { img -> img.path })
                showImagePreviews()
            }
        }
    }

    private fun showToast(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    }

    private fun showInputDialog(title: String, onConfirm: (String) -> Unit) {
        val input = EditText(this).apply {
            hint = title
            setPadding(32, 16, 32, 16)
        }

        AlertDialog.Builder(this)
            .setTitle(title)
            .setView(input)
            .setPositiveButton("OK") { _, _ ->
                val name = input.text.toString().trim()
                if (name.isNotEmpty()) onConfirm(name)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun saveImageToInternalStorage(uri: Uri): String? {
        return try {
            val input = contentResolver.openInputStream(uri) ?: return null
            val dir = File(filesDir, "images").apply { mkdirs() }
            val file = File(dir, "${System.currentTimeMillis()}.jpg")
            input.use { inp -> file.outputStream().use { out -> inp.copyTo(out) } }
            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun showImagePreviews() {
        imagePreviewContainer.removeAllViews()

        selectedImagePaths.forEach { path ->
            val preview = layoutInflater.inflate(
                R.layout.item_image_preview,
                imagePreviewContainer,
                false
            )

            val img = preview.findViewById<ImageView>(R.id.imgPreview)
            val removeBtn = preview.findViewById<ImageButton>(R.id.btnRemove)

            Glide.with(this)
                .load(path)
                .into(img)

            removeBtn.setOnClickListener {
                selectedImagePaths.remove(path)
                showImagePreviews()
            }

            imagePreviewContainer.addView(preview)
        }
    }

}