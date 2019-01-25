package com.fivesys.alphamanufacturas.fivesys.helper;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.PersistableBundle;

import com.fivesys.alphamanufacturas.fivesys.context.dao.interfaces.AuditoriaImplementation;
import com.fivesys.alphamanufacturas.fivesys.context.dao.overMethod.AuditoriaOver;
import com.fivesys.alphamanufacturas.fivesys.context.retrofit.interfaces.AuditoriaInterfaces;
import com.fivesys.alphamanufacturas.fivesys.entities.Auditoria;
import com.google.gson.Gson;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.concurrent.TimeUnit;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import io.realm.Realm;
import io.realm.RealmResults;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

public class rxJava extends AppCompatActivity {

    AuditoriaInterfaces auditoriaInterfaces;


    @SuppressLint("CheckResult")
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);

        Realm realm = Realm.getDefaultInstance();
        AuditoriaImplementation a = new AuditoriaOver(realm);

        Flowable<RealmResults<Auditoria>> paginator = realm.where(Auditoria.class).findAll().asFlowable();
        paginator.flatMap(new Function<RealmResults<Auditoria>, Publisher<ResponseBody>>() {
            @Override
            public Publisher<ResponseBody> apply(RealmResults<Auditoria> auditorias) {
                return Flowable.fromIterable(auditorias).flatMap(new Function<Auditoria, Flowable<ResponseBody>>() {
                    @Override
                    public Flowable<ResponseBody> apply(Auditoria auditoria) {

                        String json = new Gson().toJson(auditoria);
                        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);


                        return Flowable.zip(Flowable.just(auditoria), auditoriaInterfaces.sendRegisterOffLineFl(requestBody), new BiFunction<Auditoria, ResponseBody, ResponseBody>() {
                            @Override
                            public ResponseBody apply(Auditoria auditoria, ResponseBody responseBody) {
                                return responseBody;
                            }
                        });
                    }
                });
            }
        }).subscribeOn(Schedulers.io())
                .delay(1000, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                 .subscribe(new Subscriber<ResponseBody>() {
                     @Override
                     public void onSubscribe(Subscription s) {

                     }

                     @Override
                     public void onNext(ResponseBody responseBody) {

                     }

                     @Override
                     public void onError(Throwable t) {

                     }

                     @Override
                     public void onComplete() {

                     }
                 });




        // TODO  EJEMPLO KOTLIN


//        val paginator = realm.where(Auditoria::class.java).findAllAsync().asFlowable()
//        paginator.flatMap { observable ->
//                cantidad = observable.size
//            if (cantidad == 0) {
//                textViewTitle.text = "No hay Auditorias a enviar"
//            } else {
//                textViewTitle.text = "Enviando " + suma.toString() + "/" + cantidad
//            }
//            Flowable.fromIterable(observable).flatMap { a ->
//                    val realm = Realm.getDefaultInstance()
//                val auditoriaImp: AuditoriaImplementation = AuditoriaOver(realm)
//                val b = MultipartBody.Builder()
//                val filePaths: ArrayList<String> = ArrayList()
//                for (f: PuntosFijosHeader in a.PuntosFijos!!) {
//                    if (!f.Url.isNullOrEmpty()) {
//                        val file = File(Environment.getExternalStorageDirectory().toString() + "/" + Util.FolderImg + "/" + f.Url)
//                        if (file.exists()) {
//                            filePaths.add(file.toString())
//                        }
//                    }
//                }
//                for (d: Detalle in a.Detalles!!) {
//                    if (!d.Url.isNullOrEmpty()) {
//                        val file = File(Environment.getExternalStorageDirectory().toString() + "/" + Util.FolderImg + "/" + d.Url)
//                        if (file.exists()) {
//                            filePaths.add(file.toString())
//                        }
//                    }
//                }
//                for (i in 0 until filePaths.size) {
//                    val file = File(filePaths[i])
//                    b.addFormDataPart("fotos", file.name, RequestBody.create(MediaType.parse("multipart/form-data"), file))
//                }
//                val auditoria = auditoriaImp.updateFechaAuditoria(a)
//                val json = Gson().toJson(realm.copyFromRealm(auditoria))
//                Log.i("TAG", json)
//                b.setType(MultipartBody.FORM)
//                b.addFormDataPart("model", json)
//                val requestBody = b.build()
//
//                Flowable.zip(Flowable.just(auditoria), auditoriaInterfaces.sendRegisterOffLineFl(requestBody), BiFunction<Auditoria, ResponseBody, ResponseBody> { auditoria, responseBody -> responseBody })
//            }
//        }.subscribeOn(Schedulers.io())
//                .delay(1000, TimeUnit.MILLISECONDS)
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(object : Subscriber<ResponseBody> {
//            override fun onSubscribe(s: Subscription) {
//
//            }
//
//            override fun onNext(t: ResponseBody) {
//                suma += 1
//                textViewTitle.text = "Enviando " + suma.toString() + "/" + cantidad
//                Log.i("TAG", t.source().toString())
//            }
//
//            override fun onError(t: Throwable) {
//                switchOffLine.isChecked = true
//                Util.snackBarMensaje(window.decorView, t.toString())
//                dialog.dismiss()
//            }
//
//            override fun onComplete() {
//                deleteOffLine(mensaje)
//                dialog.dismiss()
//            }
//        })




    }
}
