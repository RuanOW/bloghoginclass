package com.example.bloghoginclass

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.example.bloghoginclass.databinding.FragmentWriteBlogBinding
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import java.util.*


class WriteBlogFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: FragmentWriteBlogBinding
    private lateinit var db: FirebaseFirestore
    private lateinit var storage: FirebaseStorage
    private lateinit var uri: Uri

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        auth = Firebase.auth
        db = Firebase.firestore
        storage = Firebase.storage
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_write_blog, container, false)

        binding.cancelBlogPost.setOnClickListener {
            findNavController().navigate(R.id.action_writeBlogFragment_to_homeFragment)
        }

        binding.uploadBlogImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }

        binding.PublishBlog.setOnClickListener {
            val blog = hashMapOf(
                "userId" to auth.currentUser?.uid,
                "timestamp" to Timestamp.now(),
                "title" to binding.blogMainHeading.text.toString(),
                "subheading" to binding.blogSubHeading.text.toString(),
                "body" to binding.blogBodyCopy.text.toString()
            )

            db.collection("blogs")
                .add(blog)
                .addOnFailureListener {
                    Log.d("blog", "Failed to post blog")
                    Toast.makeText(context, "failed to post blog", Toast.LENGTH_SHORT).show()
                }
                .addOnSuccessListener {
                    Log.d("blog", "Blog posted succesfully")
                    UploadImageToStorage(it.id)
                    Toast.makeText(context, "The blog was published succesfully", Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.action_writeBlogFragment_to_homeFragment)
                }
        }

        return binding.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            uri = data.data!!
            Log.d("WriteBlogFragment", "Photo URI: $uri")
            binding.uploadBlogImage.setImageURI(uri)
        }
    }

    private fun UploadImageToStorage(blogId: String) {
        val fileName = UUID.randomUUID().toString()
        Log.d("WriteBlogFragment", "UUID: $fileName")
        val ref = storage.getReference("/images/$fileName")
        ref.putFile(uri)
            .addOnSuccessListener {
                ref.downloadUrl.addOnSuccessListener {
                    Log.d("WriteBlogFragment", "Image URL: $it")
                    // upload image to blog object in firestore
                    saveImageToBlog(it.toString(), blogId)
                }
            }
            .addOnFailureListener {
                Log.d("WriteBlogFragment", "Failed Upload: $it")
            }
    }

    private fun saveImageToBlog(imageUrl: String, blogId: String){
        db.collection("blogs").document(blogId).update("headerImageUrl", imageUrl)
            .addOnSuccessListener {
                Log.d("WriteBlogFragment", "Document: $it")
            }
            .addOnFailureListener {
                Log.d("WriteBlogFragment", "failed to save image to blog: $it")
            }
    }

}