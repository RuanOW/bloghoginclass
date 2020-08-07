package com.example.bloghoginclass

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.example.bloghoginclass.data.BlogPost
import com.example.bloghoginclass.databinding.FragmentHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item

import kotlinx.android.synthetic.main.fragment_all_blog_item.*

class HomeFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: FragmentHomeBinding
    private lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        auth = Firebase.auth
        db = Firebase.firestore
        var adapter = GroupAdapter<GroupieViewHolder>()
        binding.allBlogRecyclerView.adapter = adapter

        adapter.setOnItemClickListener { item, view ->
            val blogItem = item as BlogItem
            Log.d("ItemClicked", "Item ID: ${blogItem.blogItem.id}")
            val action = HomeFragmentDirections.actionHomeFragmentToBlogItemDetailFragment(blogItem.blogItem.id)
            findNavController().navigate(action)
        }

        db.collection("blogs").get()
            .addOnSuccessListener {
                for (blog in it){
                    val resultBlogItem = blog.toObject<BlogPost>()
                    resultBlogItem.id = blog.id
                    Log.d("BlogItem", "ID ${resultBlogItem.id}")
                    adapter.add(BlogItem(resultBlogItem))
                }
            }


        auth.addAuthStateListener { firebaseAuth ->
            val firebaseUser = firebaseAuth.currentUser
            if (firebaseUser == null) {
                Log.d("Authentication", "This user is not logged in")
                findNavController().navigate(R.id.loginFragment)
            }
        }

        binding.createBlogPost.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_writeBlogFragment)
        }


        return binding.root

    }

}

class BlogItem(val blogItem: BlogPost) : Item(){
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.mainHeading.text = blogItem.title
        viewHolder.subHeading.text = blogItem.subheading
        viewHolder.timeStamp.text = blogItem.timestamp.toDate().toString()
    }

    override fun getLayout(): Int = R.layout.fragment_all_blog_item
}