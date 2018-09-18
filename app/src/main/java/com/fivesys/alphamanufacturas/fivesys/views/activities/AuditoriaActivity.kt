package com.fivesys.alphamanufacturas.fivesys.views.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.fivesys.alphamanufacturas.fivesys.R
import android.support.design.widget.TabLayout
import com.fivesys.alphamanufacturas.fivesys.views.adapters.TabLayoutAdapter
import android.support.v4.view.ViewPager
import android.support.v7.app.ActionBar
import android.support.v7.widget.Toolbar
import java.util.*


class AuditoriaActivity : AppCompatActivity() {

    lateinit var toolbar: Toolbar
    lateinit var tabLayout: TabLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auditoria)
        bindToolbar()
        bindTabLayout()
    }

    private fun bindToolbar() {
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        Objects.requireNonNull<ActionBar>(supportActionBar).title = "Nueva Auditoria"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun bindTabLayout() {
        val tabLayout = findViewById<TabLayout>(R.id.tabLayout)
        tabLayout.addTab(tabLayout.newTab().setText(R.string.tab1))
        tabLayout.addTab(tabLayout.newTab().setText(R.string.tab2))
        tabLayout.addTab(tabLayout.newTab().setText(R.string.tab3))
        val viewPager = findViewById<ViewPager>(R.id.viewPager)
        val tabLayoutAdapter = TabLayoutAdapter(supportFragmentManager, tabLayout.tabCount)
        viewPager.adapter = tabLayoutAdapter
        viewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout))
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                val position = tab.position
                viewPager.currentItem = position

            }

            override fun onTabUnselected(tab: TabLayout.Tab) {

            }

            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
    }
}
