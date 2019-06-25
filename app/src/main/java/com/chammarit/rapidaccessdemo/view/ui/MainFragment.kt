package com.chammarit.rapidaccessdemo.view.ui

import android.support.v4.app.Fragment
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.chammarit.rapidaccessdemo.R
import com.chammarit.rapidaccessdemo.databinding.ContentMainBinding
import com.chammarit.rapidaccessdemo.viewModel.MainViewModel

val TAG_F_MAIN = "MainFragment"

class MainFragment : Fragment() {
    private var binding: ContentMainBinding? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.content_main   , container, false)
        return requireNotNull(binding).root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        requireNotNull(binding).lifecycleOwner = this
        val viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        requireNotNull(binding).viewModel = viewModel
    }

    override fun onStop() {
        super.onStop()
    }
}