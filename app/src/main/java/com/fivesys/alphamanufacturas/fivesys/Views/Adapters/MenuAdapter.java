package com.fivesys.alphamanufacturas.fivesys.Views.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.fivesys.alphamanufacturas.fivesys.R;


public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.ViewHolder> {
    private String[] titulos;
    private int[] imagenes;
    private OnItemClickListener listener;

    public MenuAdapter(String[] titulos, int[] imagenes, OnItemClickListener listener) {
        this.titulos = titulos;
        this.imagenes = imagenes;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_menu, parent, false);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.bind(titulos, imagenes, position, listener);
    }

    @Override
    public int getItemCount() {
        return titulos.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageViewPhoto;
        private TextView textViewTitulo;

        public ViewHolder(View itemView) {
            super(itemView);
            imageViewPhoto = itemView.findViewById(R.id.imageViewPhoto);
            textViewTitulo = itemView.findViewById(R.id.textViewTitulo);
        }

        private void bind(final String[] string, int[] imagenes, final int position, final OnItemClickListener listener) {
            imageViewPhoto.setImageResource(imagenes[position]);
            textViewTitulo.setText(string[position]);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onItemClick(string[position], getAdapterPosition());
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(String strings, int position);
    }
}
