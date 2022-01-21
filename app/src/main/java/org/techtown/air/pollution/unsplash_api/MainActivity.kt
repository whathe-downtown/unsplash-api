package org.techtown.air.pollution.unsplash_api

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import org.techtown.air.pollution.unsplash_api.data.PhotoAdapter
import org.techtown.air.pollution.unsplash_api.data.Repository
import org.techtown.air.pollution.unsplash_api.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private  val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private  val scope = MainScope()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initViews()
        bindViews()
        fetchRandomPhotos()
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }
    private fun initViews() {
        binding.recyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        binding.recyclerView.adapter = PhotoAdapter()
    }
    private fun bindViews() {
        binding.searchEditText
            .setOnEditorActionListener { editText, actionId, event ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    currentFocus?.let { view ->
                        val inputMethodManager =
                            getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                        inputMethodManager?.hideSoftInputFromWindow(
                            view.windowToken,
                            0
                        ) // keyobrad gone

                        view.clearFocus()
                    }

                    fetchRandomPhotos(editText.text.toString())
                }

                true
            }
        binding.refreshLayout.setOnRefreshListener {
            fetchRandomPhotos(binding.searchEditText.toString())
        }
    }

    private fun fetchRandomPhotos(query: String? = null ) = scope.launch{
        try {
        Repository.getRandomPhotos(query)?.let { photos->

                binding.errorDescriptionTextView.visibility = View.GONE
                (binding.recyclerView.adapter as? PhotoAdapter)?.apply {
                    this.photos = photos
                    notifyDataSetChanged()
                    }
                }
                binding.recyclerView.visibility = View.VISIBLE
            }catch (exception : Exception){
                binding.recyclerView.visibility = View.INVISIBLE
                binding.errorDescriptionTextView.visibility = View.VISIBLE
            }finally {
                binding.shimmerLayout.visibility = View.GONE
                binding.refreshLayout.isRefreshing   = false
            }



        }

    }
