package com.example.bloghoginclass

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.bloghoginclass.data.BlogPost
import com.example.bloghoginclass.databinding.FragmentBlogItemDetailBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase


class BlogItemDetailFragment : Fragment() {

    private lateinit var binding: FragmentBlogItemDetailBinding
    private lateinit var db: FirebaseFirestore
    private val args: BlogItemDetailFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_blog_item_detail, container, false)
        binding.blogDetailTitle.text = args.blogId
        db = Firebase.firestore

        db.collection("blogs").document(args.blogId).get()
            .addOnSuccessListener {
                val item = it.toObject<BlogPost>()
                binding.blogDetailTitle.text = item?.title
                binding.blogDetailSubeHeading.text = item?.subheading
                binding.blogDetailTimeStamp.text = item?.timestamp?.toDate().toString()
                binding.blogDetailBody.text = item?.body
            }
            .addOnFailureListener {
                Toast.makeText(context, "Sorry could not get blog", Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.action_blogItemDetailFragment_to_homeFragment)
            }

        return binding.root
    }

}