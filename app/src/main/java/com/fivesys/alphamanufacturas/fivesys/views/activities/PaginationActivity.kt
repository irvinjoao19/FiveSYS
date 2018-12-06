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
import com.fivesys.alphamanufacturas.fivesys.entities.Auditoria
import com.fivesys.alphamanufacturas.fivesys.entities.Filtro
import com.fivesys.alphamanufacturas.fivesys.helper.ItemClickListener
import com.fivesys.alphamanufacturas.fivesys.helper.Util
import com.fivesys.alphamanufacturas.fivesys.views.adapters.AuditoriaAdapter
import com.fivesys.alphamanufacturas.fivesys.views.adapters.PaginationAdapter2
import com.google.gson.Gson
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Function
import io.reactivex.processors.PublishProcessor
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType
import okhttp3.RequestBody
import org.reactivestreams.Publisher
import java.util.concurrent.TimeUnit

class PaginationActivity : AppCompatActivity() {

    private val compositeDisposable = CompositeDisposable()
    private val paginator = PublishProcessor.create<Int>()
    private var paginationAdapter: AuditoriaAdapter? = null
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
        paginationAdapter = AuditoriaAdapter(R.layout.cardview_list_auditoria,object : ItemClickListener {
            override fun onItemClick(a: Auditoria, position: Int) {
                Util.snackBarMensaje(window.decorView, a.Nombre!!)
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
                .concatMap(object : Function<Int, Publisher<List<Auditoria>>> {
                    override fun apply(page: Int): Publisher<List<Auditoria>> {
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
    private fun dataFromNetwork(page: Int): Flowable<List<Auditoria>> {
        val auditoriaInterfaces = ConexionRetrofit.api.create(AuditoriaInterfaces::class.java)

        val envio = Filtro(page, 10)
        val sendPage = Gson().toJson(envio)
        val requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), sendPage)
        return auditoriaInterfaces.pagination(requestBody)
                .delay(600, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .map(object : Function<List<Auditoria>, List<Auditoria>> {
                    override fun apply(t: List<Auditoria>): List<Auditoria> {
                        return t
                    }
                })

    }
}
