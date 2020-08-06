package com.example.bloghoginclass

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


class WriteBlogFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: FragmentWriteBlogBinding
    private lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        auth = Firebase.auth
        db = Firebase.firestore
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_write_blog, container, false)

        binding.cancelBlogPost.setOnClickListener {
            findNavController().navigate(R.id.action_writeBlogFragment_to_homeFragment)
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
                    Toast.makeText(context, "The blog was published succesfully", Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.action_writeBlogFragment_to_homeFragment)
                }
        }

        return binding.root
    }

}