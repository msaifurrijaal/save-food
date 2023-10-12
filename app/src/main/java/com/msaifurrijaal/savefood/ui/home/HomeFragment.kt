package com.msaifurrijaal.savefood.ui.home

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.msaifurrijaal.savefood.R
import com.msaifurrijaal.savefood.adapter.ArticleAdapter
import com.msaifurrijaal.savefood.adapter.FoodHomeAdapter
import com.msaifurrijaal.savefood.data.Resource
import com.msaifurrijaal.savefood.data.dummy.ArticlesData
import com.msaifurrijaal.savefood.data.model.Article
import com.msaifurrijaal.savefood.data.model.Food
import com.msaifurrijaal.savefood.data.model.User
import com.msaifurrijaal.savefood.databinding.FragmentHomeBinding
import com.msaifurrijaal.savefood.ui.additem.AddItemActivity
import com.msaifurrijaal.savefood.ui.article.ArticleActivity
import com.msaifurrijaal.savefood.ui.article.ArticleActivity.Companion.ARTICLE_ITEM
import com.msaifurrijaal.savefood.ui.chat.ListChatActivity
import com.msaifurrijaal.savefood.ui.detailproduct.DetailProductActivity
import com.msaifurrijaal.savefood.ui.voucher.VoucherActivity
import com.msaifurrijaal.savefood.ui.voucher.VoucherActivity.Companion.USER_ITEM
import com.msaifurrijaal.savefood.utils.showDialogLoading

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var dialogLoading: AlertDialog
    private lateinit var foodAdapter: FoodHomeAdapter
    private lateinit var articleAdapter: ArticleAdapter
    private var list: ArrayList<Article> = arrayListOf()
    private var user: User? = null
    private lateinit var homeViewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        dialogLoading = showDialogLoading(requireContext())
        articleAdapter = ArticleAdapter()
        foodAdapter = FoodHomeAdapter()
        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        list.addAll(ArticlesData.listData)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeUserData()
        observeListFood()
        onItemClick()
        onAction()
        setArticleRV()
    }

    private fun onItemClick() {
        foodAdapter.onItemClick = { food ->
            startActivity(Intent(activity, DetailProductActivity::class.java)
                .putExtra(DetailProductActivity.FOOD_ITEM, food))
        }

        articleAdapter.onItemClick = { article ->
            startActivity(Intent(activity, ArticleActivity::class.java)
                .putExtra(ARTICLE_ITEM, article))
        }
    }

    private fun observeListFood() {
        homeViewModel.getAllFood().observe(viewLifecycleOwner, Observer { response ->
            when(response) {
                is Resource.Error -> {
                    binding.rvFoods.visibility = View.GONE
                    Toast.makeText(requireContext(), "error : ${response.message}", Toast.LENGTH_SHORT).show()
                }
                is Resource.Loading -> {
                    binding.pgFoodHome.visibility = View.VISIBLE
                    binding.rvFoods.visibility = View.INVISIBLE
                }
                is Resource.Success -> {
                    binding.pgFoodHome.visibility = View.GONE
                    setFoodRv(response.data)
                    binding.rvFoods.visibility = View.VISIBLE
                }
            }
        })
    }

    private fun setFoodRv(data: List<Food>?) {
        data?.let {
            foodAdapter.setFoodList(data)
        }
        binding.rvFoods.apply {
            layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
            adapter = foodAdapter
        }
    }

    private fun observeUserData() {
        homeViewModel.getCurrentUser().observe(viewLifecycleOwner, Observer { response ->
            when(response) {
                is Resource.Error -> {}
                is Resource.Loading -> {}
                is Resource.Success -> {
                    user = response.data
                    binding.tvUserFullName.text = "Hai, ${response.data?.nameUser}"
                    binding.tvPointUser.text = response.data?.userPoint?.toInt().toString()
                }
            }
        })
    }

    private fun setArticleRV() {
        articleAdapter.setArticleList(list.take(3))

        binding.rvArticle.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = articleAdapter
        }
    }

    private fun onAction() {
        binding.apply {
            fabAddItem.setOnClickListener {
                startActivity(Intent(requireContext(), AddItemActivity::class.java))
            }

            btnVoucher.setOnClickListener {
                Toast.makeText(requireContext(), getString(R.string.the_voucher_feature_is_still_not_available), Toast.LENGTH_SHORT).show()
            }

            ivChat.setOnClickListener {
                startActivity(Intent(requireContext(), ListChatActivity::class.java))
            }

            containerPointUser.setOnClickListener {
                if (user != null) {
                    startActivity(Intent(requireContext(), VoucherActivity::class.java)
                        .putExtra(USER_ITEM, user))
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}