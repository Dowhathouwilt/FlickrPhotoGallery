package com.example.photogallery


import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.example.photogallery.databinding.FragmentPhotoGalleryBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class PhotoGalleryFragment:Fragment() {
    private val photoGalleryViewModel: PhotoGalleryViewModel by viewModels()
    private var _binding: FragmentPhotoGalleryBinding? = null
    private val adapter by lazy {
        PhotoListAdapter{ photoPageUri ->
            val intent = Intent(Intent.ACTION_VIEW, photoPageUri)
            startActivity(intent)
        }
    }
    private val footerAdapter = PagingLoadStateAdapter()
    private var searchView:SearchView? = null
    private val binding
    get() = checkNotNull( _binding){
        "cant access PhotoGallery binding"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.UNMETERED)
            .build()

        val workRequest = OneTimeWorkRequest
            .Builder(Worker::class.java)
            .setConstraints(constraints)
            .build()
        WorkManager.getInstance(requireContext())
            .enqueue(workRequest)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentPhotoGalleryBinding.inflate(inflater, container,false)
        binding.photoGrid.layoutManager = GridLayoutManager(context,3)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.photoGrid.adapter = adapter.withLoadStateFooter(footerAdapter)
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object: MenuProvider{
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.fragment_photo_gallery, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId){
                    R.id.menu_item_search -> {
                        searchView = menuItem.actionView as? SearchView
                        searchView?.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
                            override fun onQueryTextSubmit(query: String?): Boolean {
                                photoGalleryViewModel.setQuery(query ?: "")
                                searchView?.clearFocus()
                                return true
                            }
                            override fun onQueryTextChange(newText: String?): Boolean {
                                return false
                            }
                        })
                        true
                    }
                    R.id.menu_item_clear -> {
                        photoGalleryViewModel.setQuery("")
                        true
                    } else -> false
                }
            }
        },viewLifecycleOwner, Lifecycle.State.RESUMED)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                photoGalleryViewModel.uiState.collectLatest{
                    adapter.submitData(it.images)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }




}