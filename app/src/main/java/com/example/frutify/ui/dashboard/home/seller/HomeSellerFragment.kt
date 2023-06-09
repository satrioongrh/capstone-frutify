package com.example.frutify.ui.dashboard.home.seller

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.frutify.data.model.ProductItem
import com.example.frutify.data.viewmodel.ProductViewModel
import com.example.frutify.databinding.FragmentHomeSellerBinding
import com.example.frutify.ui.dashboard.edit.EditActivity
import com.example.frutify.utils.SharePref

class HomeSellerFragment : Fragment() {

    private var _binding: FragmentHomeSellerBinding? = null
    private val binding get() = _binding!!
    private lateinit var productViewModel: ProductViewModel
    private lateinit var sharePref: SharePref
    private lateinit var homeSellerAdapter: HomeSellerAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeSellerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        productViewModel = ViewModelProvider(this).get(ProductViewModel::class.java)
        sharePref = SharePref(requireContext())

        homeSellerAdapter = HomeSellerAdapter()
        binding.recyclerViewSeller.adapter = homeSellerAdapter
        binding.recyclerViewSeller.layoutManager = LinearLayoutManager(requireContext())

        showProduct()
        productViewModel.isLoading.observe(viewLifecycleOwner) {
            showLoading(it)
        }
        productViewModel.productResult.observe(viewLifecycleOwner) { products ->
            if (products != null) {
                homeSellerAdapter.submitList(products)
            }
        }

        homeSellerAdapter.setOnProductClickListener(object : HomeSellerAdapter.OnProductClickListener {
            override fun onProductClick(product: ProductItem) {
                val intent = Intent(requireContext(), EditActivity::class.java)
                intent.putExtra("product", product)
                intent.putExtra("from_home_seller", true)
                startActivityForResult(intent, EDIT_ACTIVITY_REQUEST_CODE)

            }
        })

        binding.ivSearch.setOnClickListener {
            val query = binding.etSearch.text.toString()
            getProduct(query, sharePref.getUserId)
        }
    }

    private fun showProduct(){
        productViewModel.getListProduct(binding.etSearch.text.toString(), sharePref.getUserId)

    }

    private fun getProduct(query: String? = null, userId: Int) {
        if (query.isNullOrEmpty()) {
            productViewModel.getListProduct(null, userId)
        } else {
            productViewModel.getListProduct(query, userId)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    companion object{
        private const val EDIT_ACTIVITY_REQUEST_CODE = 100
    }
}