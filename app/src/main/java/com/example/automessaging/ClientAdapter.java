package com.example.automessaging;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class ClientAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<ClientEntity> entities = new ArrayList<>();
    private OnItemClickListener mListener;
    private OnSendImageClick onSendImageClick;
    private DateHelper dateHelper = new DateHelper();

    public interface OnItemClickListener {
        void onItemClick(int position, ClientEntity clientEntity);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    void setOnSendImageClick(OnSendImageClick onSendImageClick) {
        this.onSendImageClick = onSendImageClick;
    }

    public ClientEntity getTransaction(int position) {
        if (position < 0 || position >= entities.size()) {
            return null;
        }
        return entities.get(position);
    }

    public void setEntities(List<ClientEntity> entities) {
        this.entities = entities;
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CustomItemBinding binding = CustomItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        final ClientViewHolder holder = new ClientViewHolder(binding);
        View.OnClickListener listener = v -> mListener.onItemClick(holder.getAdapterPosition(), entities.get(holder.getAdapterPosition()));
        binding.peopleSendBtn.setOnClickListener((v) -> onSendImageClick.onClick(entities.get(holder.getAdapterPosition())));
        binding.root.setOnClickListener(listener);
        return holder;
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ClientEntity entity = entities.get(position);
        ((ClientViewHolder) (holder)).bind(entity);
    }

    @Override
    public int getItemCount() {
        return entities.size();
    }

    interface OnSendImageClick {
        void onClick(ClientEntity position);
    }

    class ClientViewHolder extends RecyclerView.ViewHolder {
        private CustomItemBinding binding;

        public ClientViewHolder(CustomItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        @SuppressLint("SetTextI18n")
        void bind(ClientEntity clientEntity) {

            binding.setClient(clientEntity);
            binding.valabilitate.setText(dateHelper.toDate(clientEntity.from) + " - " + dateHelper.toDate(clientEntity.until));

            binding.countdownExpirare.setText(String.valueOf(dateHelper.daysDifferenceFrom(clientEntity.until)));

            Long valability = dateHelper.daysDifferenceFrom(clientEntity.until);
            if (valability < 0) {
                binding.root.setBackgroundResource(R.drawable.expire_background_expired);
            } else if (valability <= 4 && !clientEntity.sent) {
                binding.root.setBackgroundResource(R.drawable.expire_background_high);
            } else if (valability <= 7 && !clientEntity.sent) {
                binding.root.setBackgroundResource(R.drawable.expire_background_medium);
            } else if (valability < 10 && !clientEntity.sent) {
                binding.root.setBackgroundResource(R.drawable.expire_background_low);
            } else {
                binding.root.setBackgroundResource(R.drawable.custom_listview_background_color);
            }

        }

        private Long calculateDate(Long expireDate) {
            return dateHelper.daysDifferenceFrom(expireDate);
        }
    }
}
