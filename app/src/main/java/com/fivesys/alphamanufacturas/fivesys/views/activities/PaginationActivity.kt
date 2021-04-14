package com.fivesys.alphamanufacturas.fivesys.views.activities

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fivesys.alphamanufacturas.fivesys.R
import com.fivesys.alphamanufacturas.fivesys.context.retrofit.ConexionRetrofit
import com.fivesys.alphamanufacturas.fivesys.context.retrofit.interfaces.AuditoriaInterfaces
import com.fivesys.alphamanufacturas.fivesys.entities.Auditoria
import com.fivesys.alphamanufacturas.fivesys.entities.Filtro
import com.fivesys.alphamanufacturas.fivesys.helper.Util
import com.fivesys.alphamanufacturas.fivesys.views.adapters.AuditoriaAdapter
import com.google.gson.Gson
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.processors.PublishProcessor
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_pagination.*
import okhttp3.MediaType
import okhttp3.RequestBody
import java.util.concurrent.TimeUnit

class PaginationActivity : AppCompatActivity() {

    private val compositeDisposable = CompositeDisposable()
    private val paginator = PublishProcessor.create<Int>()
    private var paginationAdapter: AuditoriaAdapter? = null
    private var loading = false
    private var pageNumber = 1
    private val visibleThreshold = 1
    private var lastVisibleItem: Int = 0
    private var totalItemCount: Int = 0
    private var layoutManager: LinearLayoutManager? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pagination)
        layoutManager = LinearLayoutManager(this)
        layoutManager!!.orientation = RecyclerView.VERTICAL
        recyclerView!!.layoutManager = layoutManager
        paginationAdapter = AuditoriaAdapter(R.layout.cardview_list_auditoria, object : AuditoriaAdapter.OnItemClickListener {
            override fun onItemClick(a: Auditoria, v: View, position: Int) {
                Util.snackBarMensaje(v, a.Nombre!!)
            }
        })
        recyclerView!!.adapter = paginationAdapter
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
                totalItemCount = layoutManager!!.itemCount
                lastVisibleItem = layoutManager!!.findLastVisibleItemPosition()
                if (!loading && totalItemCount <= lastVisibleItem + visibleThreshold) {
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
                .concatMap { page ->
                    loading = true
                    progressBar!!.visibility = View.VISIBLE
                    dataFromNetwork(page)
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { items ->
                    paginationAdapter!!.addItems(items)
                    paginationAdapter!!.notifyDataSetChanged()
                    loading = false
                    progressBar!!.visibility = View.INVISIBLE
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
                .map { t -> t }
    }
}
