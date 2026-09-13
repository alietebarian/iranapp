package com.ideabonyan.iranapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.ideabonyan.iranapp.Activity.ConfirmationActivity;
import com.ideabonyan.iranapp.Activity.LoginActivity;
import com.ideabonyan.iranapp.Components.MyTextView;
import com.ideabonyan.iranapp.Models.AdsToBeListed;
import com.ideabonyan.iranapp.R;
import com.ideabonyan.iranapp.UserData.User;
import com.ideabonyan.iranapp.UserData.UserHelper;
import com.ideabonyan.iranapp.UserData.UserSessionManager;
import com.ideabonyan.iranapp.Utils.StaticData;
import com.ideabonyan.iranapp.Utils.VolleySingleton;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.List;


public class AdListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String LIKED_COLOR = "#4CAF50";
    private static final String NOT_LIKED_COLOR = "#999999";

    List<AdsToBeListed> datas;
    Context context;

    public AdListAdapter(List<AdsToBeListed> datas, Context context) {
        this.datas = datas;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(context).inflate(R.layout.rv_ads_in_list, parent, false);
        return new AdListAdapter.CellFeedViewHolder(view);

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        final AdListAdapter.CellFeedViewHolder holder = (AdListAdapter.CellFeedViewHolder) viewHolder;

        holder.rvAdListTitle.setText(datas.get(position).getTitle());

        if (datas.get(position).getAddress() != null && !datas.get(position).getAddress().equals("null") && !datas.get(position).getAddress().equals("") && !datas.get(position).getAddress().equals(null)) {
            holder.rvAdListAddress.setText(datas.get(position).getAddress());
        } else holder.rvAdListAddress.setText("بدون آدرس");

        if (datas.get(position).getStatus().equals("pending")) {
            holder.pendingIdentifier.setVisibility(View.VISIBLE);
        } else {
            holder.pendingIdentifier.setVisibility(View.GONE);
        }

        /// check mobile numbers
//        if (!datas.get(position).getTel1().equals(null) && !datas.get(position).getTel1().equals("")) {
//            if (!datas.get(position).getTel2().equals(null) && !datas.get(position).getTel2().equals("")) {
//                holder.rvAdListNumber.setText(datas.get(position).getMobile() +
//                        " - " + datas.get(position).getTel1() +
//                        " - " + datas.get(position).getTel2());
//            } else {
//                holder.rvAdListNumber.setText(datas.get(position).getMobile() +
//                        " - " + datas.get(position).getTel1());
//            }
//        } else{

        if (!(datas.get(position).getMobile() == null || datas.get(position).getMobile().equals("null") ||
                datas.get(position).getMobile().equals("") || datas.get(position).getMobile().equals(null) ||
                datas.get(position).getMobile().equals("0"))) {
            holder.rvAdListNumber.setText(datas.get(position).getMobile());
            holder.lin_phone_number.setVisibility(View.VISIBLE);


        } else if (!(datas.get(position).getTel1() == null || datas.get(position).getTel1().equals("null") ||
                datas.get(position).getTel1().equals("") || datas.get(position).getTel1().equals(null) ||
                datas.get(position).getTel1().equals("0"))) {
            holder.rvAdListNumber.setText(datas.get(position).getTel1());
            holder.lin_phone_number.setVisibility(View.VISIBLE);


        } else if (!(datas.get(position).getTel2() == null || datas.get(position).getTel2().equals("null") ||
                datas.get(position).getTel2().equals("") || datas.get(position).getTel2().equals(null) ||
                datas.get(position).getTel2().equals("0"))) {
            holder.rvAdListNumber.setText(datas.get(position).getTel2());
            holder.lin_phone_number.setVisibility(View.VISIBLE);


        }else{
            holder.lin_phone_number.setVisibility(View.INVISIBLE);
        }
//        }

        ///show or hide discount
//        Log.v("descont", datas.get(position).getDiscount() + " ali");
        if (datas.get(position).getDiscount() != null && !datas.get(position).getDiscount().equals(null)
                && !datas.get(position).getDiscount().equals("") && !datas.get(position).getDiscount().equals("null")
                && !datas.get(position).getDiscount().equals(null)) {
            holder.rvAdListDiscountText.setText("%" + datas.get(position).getDiscount());
            holder.rvAdListDiscountLayout.setVisibility(View.VISIBLE);
        } else {
            holder.rvAdListDiscountLayout.setVisibility(View.GONE);
        }

        holder.rvAdListVideoBadge.setVisibility(datas.get(position).hasVideo() ? View.VISIBLE : View.GONE);

        /// like button
        final AdsToBeListed ad = datas.get(position);
        bindLikeState(holder, ad);
        holder.rvAdListLikeBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLikeClicked(holder, ad);
            }
        });

        ///put image
        if (datas.get(position).getPhotos() != null && datas.get(position).getPhotos().size() > 0) {
            if (datas.get(position).getPhotos().get(0) != null) {
                Picasso.get()
                        .load(datas.get(position).getPhotos().get(0).getName())
                        .fit()
                        .centerCrop()
                        .placeholder(R.drawable.place_holder)
                        .into(holder.rvAdListImage);
            } else {
                Picasso.get()
                        .load(R.drawable.place_holder)
                        .fit()
                        //  .placeholder(R.drawable.placeholder)
                        .into(holder.rvAdListImage);

            }
        } else {
            Picasso.get()
                    .load(R.drawable.place_holder)
                    .fit()
                    //  .placeholder(R.drawable.placeholder)
                    .into(holder.rvAdListImage);

        }
    }

    private void bindLikeState(CellFeedViewHolder holder, AdsToBeListed ad) {
        int color = Color.parseColor(ad.isLikedByUser() ? LIKED_COLOR : NOT_LIKED_COLOR);
        holder.rvAdListLikeIMG.setColorFilter(color);
        holder.rvAdListLikeCount.setTextColor(color);
        holder.rvAdListLikeCount.setText(String.valueOf(ad.getLikes()));
    }

    /**
     * Flips the vote locally first so the row reacts immediately, then tells the server.
     * The server counts win once they arrive; a failed call rolls the row back.
     */
    private void onLikeClicked(final CellFeedViewHolder holder, final AdsToBeListed ad) {
        User user = UserHelper.LoadUserInfo(context);

        if (!user.isLoggedIn()) {
            context.startActivity(new Intent(context, LoginActivity.class));
            return;
        }
        if (!"1".equals(user.getIsVerrified())) {
            context.startActivity(new Intent(context, ConfirmationActivity.class));
            return;
        }

        final boolean wasLiked = ad.isLikedByUser();
        final String previousType = ad.getUser_like_type();
        final int previousLikes = ad.getLikes();
        final int previousDislikes = ad.getDislikes();
        final String token = new UserSessionManager(context).getLoginToken();

        String url;
        int method;
        if (wasLiked) {
            ad.setUser_like_type(null);
            ad.setLikes(Math.max(0, previousLikes - 1));
            url = StaticData.DOMAIN_WITH_API + "/ads/" + ad.getId() + "/like/off?like_type=like&token=" + token;
            method = Request.Method.GET;
        } else {
            ad.setUser_like_type("like");
            ad.setLikes(previousLikes + 1);
            // Liking an ad the user had disliked moves the vote across.
            if ("dislike".equals(previousType)) {
                ad.setDislikes(Math.max(0, previousDislikes - 1));
            }
            url = StaticData.DOMAIN_WITH_API + "/ads/" + ad.getId() + "/likes?like_type=like&token=" + token;
            method = Request.Method.POST;
        }
        bindLikeState(holder, ad);

        StringRequest request = new StringRequest(method, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject json = new JSONObject(response);
                    if (json.optInt("status") == 200) {
                        ad.setUser_like_type(json.isNull("like_type") ? null : json.optString("like_type", null));
                        ad.setLikes(json.optInt("likes_count", ad.getLikes()));
                        ad.setDislikes(json.optInt("dislikes_count", ad.getDislikes()));
                    } else {
                        restoreLikeState(ad, previousType, previousLikes, previousDislikes);
                    }
                } catch (Exception e) {
                    Log.v("adListLike", "bad like response: " + e.getMessage());
                    restoreLikeState(ad, previousType, previousLikes, previousDislikes);
                }
                refreshLikeRow(holder, ad);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                restoreLikeState(ad, previousType, previousLikes, previousDislikes);
                refreshLikeRow(holder, ad);
            }
        });

        VolleySingleton.GetInstance(context).AddToRequestQueue(request);
    }

    private void restoreLikeState(AdsToBeListed ad, String type, int likes, int dislikes) {
        ad.setUser_like_type(type);
        ad.setLikes(likes);
        ad.setDislikes(dislikes);
    }

    /** The holder may have been recycled onto another ad while the call was in flight. */
    private void refreshLikeRow(CellFeedViewHolder holder, AdsToBeListed ad) {
        int position = holder.getAdapterPosition();
        if (position != RecyclerView.NO_POSITION && datas.get(position) == ad) {
            bindLikeState(holder, ad);
        }
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    private class CellFeedViewHolder extends RecyclerView.ViewHolder {
        LinearLayout lin_phone_number, rvAdListLikeBTN;
        ImageView rvAdListImage, pendingIdentifier, rvAdListVideoBadge, rvAdListLikeIMG;
        RelativeLayout rvAdListDiscountLayout;
        MyTextView rvAdListDiscountText, rvAdListTitle, rvAdListAddress, rvAdListNumber, rvAdListLikeCount;

        public CellFeedViewHolder(View view) {
            super(view);

            lin_phone_number =  view.findViewById(R.id.lin_phone_number);
            rvAdListImage = (ImageView) view.findViewById(R.id.rvAdListImage);
            rvAdListDiscountLayout = (RelativeLayout) view.findViewById(R.id.rvAdListDiscountLayout);
            rvAdListDiscountText = (MyTextView) view.findViewById(R.id.rvAdListDiscountText);
            rvAdListTitle = (MyTextView) view.findViewById(R.id.rvAdListTitle);
            rvAdListAddress = (MyTextView) view.findViewById(R.id.rvAdListAddress);
            rvAdListNumber = (MyTextView) view.findViewById(R.id.rvAdListNumber);
            pendingIdentifier = (ImageView) view.findViewById(R.id.rvAdListPendingIdentifier);
            rvAdListVideoBadge = view.findViewById(R.id.rvAdListVideoBadge);
            rvAdListLikeBTN = view.findViewById(R.id.rvAdListLikeBTN);
            rvAdListLikeIMG = view.findViewById(R.id.rvAdListLikeIMG);
            rvAdListLikeCount = view.findViewById(R.id.rvAdListLikeCount);
        }
    }
}
