package com.ideabonyan.iranapp.Adapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.ideabonyan.iranapp.Activity.Edite_Car_Add;
import com.ideabonyan.iranapp.Activity.LoginActivity;
import com.ideabonyan.iranapp.Components.MyTextView;
import com.ideabonyan.iranapp.Fragment.Account.My_Car;
import com.ideabonyan.iranapp.Interface.Get_Insert_Edit_Data;
import com.ideabonyan.iranapp.Interface.Get_Insert_Edit_Data2;
import com.ideabonyan.iranapp.Models.Vehicles;
import com.ideabonyan.iranapp.R;
import com.ideabonyan.iranapp.UserData.UserHelper;
import com.ideabonyan.iranapp.UserData.UserSessionManager;
import com.ideabonyan.iranapp.Utils.Get_Volley_Call_Back;
import com.ideabonyan.iranapp.Utils.ShowToast;
import com.ideabonyan.iranapp.Utils.StaticData;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import top.wefor.circularanim.CircularAnim;


public class My_Car_List_Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Get_Insert_Edit_Data {
    ProgressDialog progressDialog;

    List<Vehicles> vehiclesList;
    Context context;
    Get_Insert_Edit_Data2 get_insert_edit_data2;
    public My_Car_List_Adapter(List<Vehicles> vehiclesList, Context context,Get_Insert_Edit_Data2 get_insert_edit_data2) {
        this.vehiclesList = vehiclesList;
        this.context = context;
        this.get_insert_edit_data2=get_insert_edit_data2;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(context).inflate(R.layout.rv_my_car_in_list, parent, false);

        return new My_Car_List_Adapter.CellFeedViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {
        final My_Car_List_Adapter.CellFeedViewHolder holder = (My_Car_List_Adapter.CellFeedViewHolder) viewHolder;

        holder.rvAdListTitle.setText(vehiclesList.get(position).getAds_title());

        holder.rvAdListAddress.setText(vehiclesList.get(position).getCity_name() + "،" + vehiclesList.get(position).getRegion_name()
                + " / " + vehiclesList.get(position).getPassed_time());

        if (vehiclesList.get(position).getStatus().equals("pending")) {
//            holder.pendingIdentifier.setVisibility(View.VISIBLE);
            holder.txt_statous.setBackgroundColor(Color.parseColor("#F57C00"));
            holder.txt_statous.setText("در انتظار تایید");

        } else if (vehiclesList.get(position).getStatus().equals("rejected")) {
            holder.txt_statous.setBackgroundColor(Color.parseColor("#d32f2f"));
            holder.txt_statous.setText("رد شده");
        } else if (vehiclesList.get(position).getStatus().equals("approved")) {
            holder.txt_statous.setBackgroundColor(Color.parseColor("#388E3C"));
            holder.txt_statous.setText("تایید شد");
        }

        if (!vehiclesList.get(position).getPrice().equals("0")) {
            try {


                Long num = Long.parseLong(vehiclesList.get(position).getPrice());
                DecimalFormat numFormat;
                String number;
                numFormat = new DecimalFormat("#,###,###");
                number = numFormat.format(num);
                holder.txt_cost.setText(String.valueOf(number) + " تومان");

            } catch (NumberFormatException e) {
                holder.txt_cost.setText(vehiclesList.get(position).getPrice() + " تومان");
            }
        } else {
            holder.txt_cost.setText("قیمت توافقی");
        }
        if (vehiclesList.get(position).getThumbnail_photo() != null && !vehiclesList.get(position).getThumbnail_photo().equals(null)
                && !vehiclesList.get(position).getThumbnail_photo().equals("null") && !vehiclesList.get(position).getThumbnail_photo().equals("")) {
            Picasso.with(context)
                    .load(vehiclesList.get(position).getThumbnail_photo())
                    .resize(200, 200)
                    //.fit()
//                    .resizeDimen(16, 9)
                    .centerCrop()
                    .placeholder(R.drawable.place_holder_car)
                    .into(holder.rvAdListImage);
        }
//        else if (!(vehiclesList.get(position).getPhotosDatas() == null || vehiclesList.get(position).getPhotosDatas().size() == 0)) {

//            Picasso.with(context)
//                    .load(vehiclesList.get(position).getPhotosDatas().get(0).getName())
//                    .resize(200, 200)
//                    //.fit()
////                    .resizeDimen(16, 9)
//                    .centerCrop()
//                    //  .placeholder(R.drawable.placeholder)
//                    .into(holder.rvAdListImage);
//        }
        else {
            Picasso.with(context)
                    .load(R.drawable.place_holder_car)
                    .resize(200, 200)
                    //.fit()
//                    .resizeDimen(16, 9)
                    .centerCrop()
                    //  .placeholder(R.drawable.placeholder)
                    .into(holder.rvAdListImage);

        }
        holder.lin_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CircularAnim.show(holder.lin_edit)
                        .startRadius(0)
                        .endRadius(holder.card_main.getWidth())
                        .go(new CircularAnim.OnAnimationEndListener() {
                            @Override
                            public void onAnimationEnd() {
                                holder.lin_main.setEnabled(false);
                                holder.lin_edit.setEnabled(true);
                            }
                        });

            }
        });
        holder.lin_go_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CircularAnim.hide(holder.lin_edit)
                        .startRadius(holder.card_main.getWidth())
                        .endRadius(0)
                        .go(new CircularAnim.OnAnimationEndListener() {
                            @Override
                            public void onAnimationEnd() {
                                holder.lin_main.setEnabled(true);
                                holder.lin_edit.setEnabled(false);
                            }
                        });

            }
        });

        holder.lin_edit_product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context,Edite_Car_Add.class);
                intent.putExtra("vehicles",vehiclesList.get(position));
                context.startActivity(intent);
            }
        });
        holder.lin_delete_product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(context)
                        .setMessage("آیا از حذف این محصول اطمینان دارید؟")
                        .setPositiveButton("بله", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
//                                product_delete(products.get(position).getId());
                            delete_this_add(vehiclesList.get(position).getId());
                            }
                        })
                        .setNegativeButton("خیر", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .show();
            }
        });
    }

    private void delete_this_add(String id){
        progressDialog=new ProgressDialog(context);
        progressDialog.setMessage("در حال اعمال تغییرات ...");
        progressDialog.show();
        Get_Volley_Call_Back.binddata(My_Car_List_Adapter.this);
        Map<String, String> params = new HashMap<String, String>();
        String url= StaticData.add_cae+"/"+id+ "?token=" + new UserSessionManager(context).getLoginToken();
        Get_Volley_Call_Back.Call_Volley(context,params,url, Request.Method.DELETE,133);

    }

    @Override
    public int getItemCount() {
        return vehiclesList.size();
    }

    @Override
    public void on_volley_response(String response, int id) {

        if (id==133){
            try {
                progressDialog.dismiss();
                JSONObject jsonObject=new JSONObject(response);
                if (jsonObject.getString("status").equals("200")) {
                    My_Car.rv.setVisibility(View.GONE);
                    My_Car.progressBar.setVisibility(View.VISIBLE);
                    My_Car.offset = 0;
                    My_Car.vehiclesList = null;
                    My_Car.getData(context,get_insert_edit_data2);
                } else if (jsonObject.getString("status").equals("401")) {
                    if (jsonObject.getString("error").equals("token_invalid")) {
                        UserHelper.RemoveUserInfo(context);
                        Intent intent = new Intent(context, LoginActivity.class);
                        ShowToast.failure("لطفا دوباره وارد حساب خود شوید",(Activity) context);
                        context.startActivity(intent);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void on_volley_error(VolleyError error, int id) {

    }

    private class CellFeedViewHolder extends RecyclerView.ViewHolder {

        ImageView rvAdListImage;
        MyTextView rvAdListTitle, rvAdListAddress;
        TextView txt_cost, txt_statous;
        LinearLayout lin_edit,lin_main,lin_go_back,lin_edit_product,lin_delete_product;
        CardView card_main;
        public CellFeedViewHolder(View view) {
            super(view);
            txt_statous = (TextView) view.findViewById(R.id.txt_statous);
            rvAdListImage = (ImageView) view.findViewById(R.id.rvAdListImage);
            rvAdListTitle = (MyTextView) view.findViewById(R.id.rvAdListTitle);
            rvAdListAddress = (MyTextView) view.findViewById(R.id.rvAdListAddress);
//            pendingIdentifier = (ImageView) view.findViewById(R.id.rvAdListPendingIdentifier);
            txt_cost = (TextView) view.findViewById(R.id.txt_cost);
            lin_edit= (LinearLayout) view.findViewById(R.id.lin_edit);
            lin_main= (LinearLayout) view.findViewById(R.id.lin_main);
            lin_go_back= (LinearLayout) view.findViewById(R.id.lin_go_back);
            lin_edit_product= (LinearLayout) view.findViewById(R.id.lin_edit_product);
            lin_delete_product= (LinearLayout) view.findViewById(R.id.lin_delete_product);
            card_main= (CardView) view.findViewById(R.id.card_main);
        }
    }
}
