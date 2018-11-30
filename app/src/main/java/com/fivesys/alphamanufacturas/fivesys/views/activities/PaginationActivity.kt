package com.fivesys.alphamanufacturas.fivesys.views.activities

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fivesys.alphamanufacturas.fivesys.R
import com.fivesys.alphamanufacturas.fivesys.views.adapters.PaginationAdapter
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Function
import io.reactivex.processors.PublishProcessor
import org.reactivestreams.Publisher
import java.util.ArrayList
import java.util.concurrent.TimeUnit

class PaginationActivity : AppCompatActivity() {

    private val compositeDisposable = CompositeDisposable()
    private val paginator = PublishProcessor.create<Int>()
    private var paginationAdapter: PaginationAdapter? = null
    private var recyclerView: RecyclerView? = null
    private var progressBar: ProgressBar? = null
    private var loading = false
    private var pageNumber = 1
    private val VISIBLE_THRESHOLD = 1
    private var lastVisibleItem: Int = 0
    private var totalItemCount: Int = 0
    private var layoutManager: LinearLayoutManager? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pagination)
        recyclerView = findViewById(R.id.recyclerView)
        progressBar = findViewById(R.id.progressBar)
        layoutManager = LinearLayoutManager(this)
        layoutManager!!.setOrientation(RecyclerView.VERTICAL)
        recyclerView!!.setLayoutManager(layoutManager)
        paginationAdapter = PaginationAdapter()
        recyclerView!!.setAdapter(paginationAdapter)
        setUpLoadMoreListener()
        subscribeForData()
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }

    /**
     * setting listener to get callback for load more
     */
    private fun setUpLoadMoreListener() {
        recyclerView!!.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView,
                                    dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                totalItemCount = layoutManager!!.getItemCount()
                lastVisibleItem = layoutManager!!.findLastVisibleItemPosition()
                if (!loading && totalItemCount <= lastVisibleItem + VISIBLE_THRESHOLD) {
                    pageNumber++
                    paginator.onNext(pageNumber)
                    loading = true
                }
            }
        })
    }

    /**
     * subscribing for data
     */
    private fun subscribeForData() {

        val disposable = paginator
                .onBackpressureDrop()
                .concatMap(object : Function<Int, Publisher<List<String>>> {
                    override fun apply(page: Int): Publisher<List<String>> {
                        loading = true
                        progressBar!!.visibility = View.VISIBLE
                        return dataFromNetwork(page)
                    }

                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { items ->
                    paginationAdapter!!.addItems(items)
                    paginationAdapter!!.notifyDataSetChanged()
                    loading = false
                    progressBar!!.setVisibility(View.INVISIBLE)
                }

        compositeDisposable.add(disposable)

        paginator.onNext(pageNumber)

    }

    /**
     * Simulation of network data
     */
    private fun dataFromNetwork(page: Int): Flowable<List<String>> {
        return Flowable.just(true)
                .delay(2, TimeUnit.SECONDS)
                .map(object : Function<Boolean, List<String>> {
                    override fun apply(value: Boolean): List<String> {
                        val items = ArrayList<String>()
                        for (i in 1..10) {
                            items.add("Item " + (page * 10 + i))
                        }
                        return items
                    }
                })
    }
}
