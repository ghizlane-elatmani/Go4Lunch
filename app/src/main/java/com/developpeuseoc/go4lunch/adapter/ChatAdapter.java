package com.developpeuseoc.go4lunch.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.developpeuseoc.go4lunch.R;
import com.developpeuseoc.go4lunch.model.Message;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import static com.developpeuseoc.go4lunch.utils.DatesAndHours.convertDateToHour;

public class ChatAdapter extends FirestoreRecyclerAdapter<Message, ChatAdapter.ChatViewHolder> {

    public interface Listener {
        void onDataChanged();
    }

    //FOR DATA
    private final RequestManager glide;
    private final String idCurrentUser;

    //FOR COMMUNICATION
    private Listener callback;

    // Constructor
    public ChatAdapter(@NonNull FirestoreRecyclerOptions<Message> options, RequestManager glide, Listener callback, String idCurrentUser) {
        super(options);
        this.glide = glide;
        this.callback = callback;
        this.idCurrentUser = idCurrentUser;
    }

    // Create viewHolder
    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ChatViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.message_item, parent, false));
    }

    // Update viewHolder
    @Override
    protected void onBindViewHolder(@NonNull ChatViewHolder holder, int position, @NonNull Message model) {
        holder.updateWithMessage(model, this.idCurrentUser, this.glide);
    }

    @Override
    public void onDataChanged() {
        super.onDataChanged();
        this.callback.onDataChanged();
    }

    public static class ChatViewHolder extends RecyclerView.ViewHolder {

        //ROOT VIEW
        private RelativeLayout rootView;

        //PROFILE CONTAINER
        private LinearLayout profileContainer;
        private ImageView imageViewProfile;
        private ImageView imageViewUser;

        //MESSAGE CONTAINER
        private RelativeLayout messageContainer;

        //IMAGE SENDED CONTAINER
        private CardView cardViewImageSent;
        private ImageView imageViewSent;

        //TEXT MESSAGE CONTAINER
        private CardView textMessageContainer;
        private TextView textViewMessage;

        //DATE TEXT
        private TextView textViewDate;


        //FOR DATA
        private final int colorCurrentUser;
        private final int colorRemoteUser;


        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);

            //findViewById
            rootView = itemView.findViewById(R.id.chat_item_root_view);
            profileContainer = itemView.findViewById(R.id.chat_item_profile_container);
            imageViewProfile = itemView.findViewById(R.id.chat_item_profile_container_profile_image);
            imageViewUser = itemView.findViewById(R.id.chat_item_profile);
            messageContainer = itemView.findViewById(R.id.chat_item_message_container);
            cardViewImageSent = itemView.findViewById(R.id.chat_item_message_container_image_sent_cardview);
            imageViewSent = itemView.findViewById(R.id.chat_item_message_container_image_sent_cardview_image);
            textMessageContainer = itemView.findViewById(R.id.chat_item_message_container_text_message_container);
            textViewMessage = itemView.findViewById(R.id.chat_item_message_container_text_message_container_text_view);
            textViewDate = itemView.findViewById(R.id.chat_item_message_container_text_view_date);

            //For bubble color
            colorCurrentUser = ContextCompat.getColor(itemView.getContext(), R.color.quantum_googyellowA100);
            colorRemoteUser = ContextCompat.getColor(itemView.getContext(), R.color.quantum_deeporange100);
        }

        //Update with message
        public void updateWithMessage(Message message, String currentUserId, RequestManager glide) {

            // Check if current user is the sender
            Boolean isCurrentUser = message.getUserSender().getUid().equals(currentUserId);

            // Update message TextView
            this.textViewMessage.setText(message.getMessage());
            this.textViewMessage.setTextAlignment(isCurrentUser ? View.TEXT_ALIGNMENT_TEXT_END : View.TEXT_ALIGNMENT_TEXT_START);

            // Update date TextView
            if (message.getDateCreated() != null)
                this.textViewDate.setText(convertDateToHour(message.getDateCreated()));

            // Update ImageView
            this.imageViewUser.setVisibility(message.getUserSender().getUserChat() ? View.VISIBLE : View.INVISIBLE);


            // Update profile picture ImageView
            if (message.getUserSender().getUrlPicture() != null)
                glide.load(message.getUserSender().getUrlPicture())
                        .apply(RequestOptions.circleCropTransform())
                        .into(imageViewProfile);

            // Update image sent ImageView
            if (message.getUrlImage() != null) {
                glide.load(message.getUrlImage())
                        .into(imageViewSent);
                this.imageViewSent.setVisibility(View.VISIBLE);
            } else {
                this.imageViewSent.setVisibility(View.GONE);
            }

            //Update Message Bubble Color Background
            textMessageContainer.setBackgroundColor(isCurrentUser ? colorCurrentUser : colorRemoteUser);

            // Update all views alignment depending is current user or not
            this.updateDesignDependingUser(isCurrentUser);
        }

        //For design
        private void updateDesignDependingUser(Boolean isSender) {

            // PROFILE CONTAINER
            RelativeLayout.LayoutParams paramsLayoutHeader = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            paramsLayoutHeader.addRule(isSender ? RelativeLayout.ALIGN_PARENT_RIGHT : RelativeLayout.ALIGN_PARENT_LEFT);
            this.profileContainer.setLayoutParams(paramsLayoutHeader);

            // MESSAGE CONTAINER
            RelativeLayout.LayoutParams paramsLayoutContent = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            paramsLayoutContent.addRule(isSender ? RelativeLayout.LEFT_OF : RelativeLayout.RIGHT_OF, R.id.chat_item_profile_container);
            this.messageContainer.setLayoutParams(paramsLayoutContent);

            // CARDVIEW IMAGE SEND
            RelativeLayout.LayoutParams paramsImageView = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            paramsImageView.addRule(isSender ? RelativeLayout.ALIGN_LEFT : RelativeLayout.ALIGN_RIGHT, R.id.chat_item_message_container_text_message_container);
            this.cardViewImageSent.setLayoutParams(paramsImageView);

            this.rootView.requestLayout();
        }

    }

}