package com.fivesys.alphamanufacturas.fivesys.views.activities

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fivesys.alphamanufacturas.fivesys.R
import com.fivesys.alphamanufacturas.fivesys.context.retrofit.ConexionRetrofit
import com.fivesys.alphamanufacturas.fivesys.context.retrofit.interfaces.AuditoriaInterfaces
import com.fivesys.alphamanufacturas.fivesys.entities.DataList
import com.fivesys.alphamanufacturas.fivesys.entities.Lista
import com.fivesys.alphamanufacturas.fivesys.helper.ItemClickListener
import com.fivesys.alphamanufacturas.fivesys.helper.Util
import com.fivesys.alphamanufacturas.fivesys.views.adapters.PaginationAdapter2
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Function
import io.reactivex.processors.PublishProcessor
import io.reactivex.schedulers.Schedulers
import org.reactivestreams.Publisher
import java.util.concurrent.TimeUnit

class PaginationActivity : AppCompatActivity() {

    private val compositeDisposable = CompositeDisposable()
    private val paginator = PublishProcessor.create<Int>()
    private var paginationAdapter: PaginationAdapter2? = null
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
        paginationAdapter = PaginationAdapter2(object : ItemClickListener {
            override fun onClick(data: DataList, position: Int) {
                Util.snackBarMensaje(window.decorView, data.Nombre!!)
            }

        })
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
                .concatMap(object : Function<Int, Publisher<List<DataList>>> {
                    override fun apply(page: Int): Publisher<List<DataList>> {
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
    private fun dataFromNetwork(page: Int): Flowable<List<DataList>> {
        val auditoriaInterfaces = ConexionRetrofit.api.create(AuditoriaInterfaces::class.java)
        return auditoriaInterfaces.pagination(page, 10)
                .delay(600, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .map(object : Function<Lista, List<DataList>> {
                    override fun apply(t: Lista): List<DataList>? {
                        return t.lista
                    }
                })
    }
}
