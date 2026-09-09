package com.ideabonyan.iranapp.Adapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.ideabonyan.iranapp.Activity.Edit_Home_Add;
import com.ideabonyan.iranapp.Activity.LoginActivity;
import com.ideabonyan.iranapp.Components.MyTextView;
import com.ideabonyan.iranapp.Fragment.Account.My_Home;
import com.ideabonyan.iranapp.Interface.Get_Insert_Edit_Data;
import com.ideabonyan.iranapp.Interface.Get_Insert_Edit_Data3;
import com.ideabonyan.iranapp.Models.Estates;
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


public class My_Home_List_Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Get_Insert_Edit_Data{
    ProgressDialog progressDialog;

    List<Estates> estatesList;
    Context context;
    Get_Insert_Edit_Data3 get_insert_edit_data3;
    public My_Home_List_Adapter(List<Estates> estatesList, Context context,Get_Insert_Edit_Data3 get_insert_edit_data3) {
        this.estatesList = estatesList;
        this.context = context;
        this.get_insert_edit_data3 = get_insert_edit_data3;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(context).inflate(R.layout.rv_my_home_in_list, parent, false);

        return new My_Home_List_Adapter.CellFeedViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {
        final My_Home_List_Adapter.CellFeedViewHolder holder = (My_Home_List_Adapter.CellFeedViewHolder) viewHolder;

        holder.rvAdListTitle.setText(estatesList.get(position).getAds_title());

        holder.rvAdListAddress.setText(estatesList.get(position).getCity_name() + "،" + estatesList.get(position).getRegion_name()
                + " / " + estatesList.get(position).getElapsed_time());

//        if (estatesList.get(position).getStatus().equals("pending"))
//            holder.pendingIdentifier.setVisibility(View.VISIBLE);
//        else holder.pendingIdentifier.setVisibility(View.GONE);


        if (estatesList.get(position).getThumbnail_photo() != null && !estatesList.get(position).getThumbnail_photo().equals(null)
                && !estatesList.get(position).getThumbnail_photo().equals("null") && !estatesList.get(position).getThumbnail_photo().equals("")) {
            Picasso.get()
                    .load(estatesList.get(position).getThumbnail_photo())
//                    .resize(200, 200)
                    .fit()
//                    .resizeDimen(16, 9)
//                    .centerCrop()
                    .placeholder(R.drawable.place_holder_home)
                    .into(holder.rvAdListImage);
        } else {
            Picasso.get()
                    .load(R.drawable.place_holder_home)
//                    .resize(200, 200)
                    .fit()
//                    .resizeDimen(16, 9)
//                    .centerCrop()
                    //  .placeholder(R.drawable.placeholder)
                    .into(holder.rvAdListImage);

        }

//        Log.v("cost1",estatesList.get(position).getMonthly_price_ejare());

        if (estatesList.get(position).getCategory_id().equals("1") || estatesList.get(position).getCategory_id().equals("3")
                ||estatesList.get(position).getCategory_parent_id().equals("1")|| estatesList.get(position).getCategory_parent_id().equals("3")) {
            holder.txt_ejare.setVisibility(View.INVISIBLE);
            holder.txt_vadeae.setVisibility(View.INVISIBLE);
            String cost = "";

            if (!estatesList.get(position).getPrice_kharid().equals("0")&&!estatesList.get(position).getPrice_kharid().equals("-1")) {
                try {
                    Long num = Long.parseLong(estatesList.get(position).getPrice_kharid());
                    DecimalFormat numFormat;
                    String number;
                    numFormat = new DecimalFormat("#,###,###");
                    number = numFormat.format(num);
                    cost = (String.valueOf(number) + " تومان");

                } catch (NumberFormatException e) {
                    cost = (estatesList.get(position).getPrice_kharid() + " تومان");
                }
                holder.txt_cost.setText("قیمت : " + cost);

            } else if (estatesList.get(position).getPrice_kharid().equals("0")){
                cost = ("توافقی");
                holder.txt_cost.setText( cost);

            } else {
                cost = ("جهت معاوضه");
                holder.txt_cost.setText( cost);

            }

            holder.txt_cost.setVisibility(View.VISIBLE);
        } else if (estatesList.get(position).getCategory_id().equals("2") || estatesList.get(position).getCategory_id().equals("5")
                ||estatesList.get(position).getCategory_parent_id().equals("2")||estatesList.get(position).getCategory_parent_id().equals("4")) {
            holder.txt_cost.setVisibility(View.INVISIBLE);
            holder.txt_ejare.setVisibility(View.VISIBLE);
            holder.txt_vadeae.setVisibility(View.VISIBLE);
            String cost="";
            String cost1="";
            if (!estatesList.get(position).getPre_pay_ejare().equals("0")&&!estatesList.get(position).getPre_pay_ejare().equals("-1")) {
                try {
                    Long num = Long.parseLong(estatesList.get(position).getPre_pay_ejare());
                    DecimalFormat numFormat;
                    String number;
                    numFormat = new DecimalFormat("#,###,###");
                    number = numFormat.format(num);
                    cost = (String.valueOf(number) + " تومان");

                } catch (NumberFormatException e) {
                    cost = (estatesList.get(position).getPre_pay_ejare() + " تومان");
                }
            } else if (estatesList.get(position).getPre_pay_ejare().equals("0")){
                cost = ("توافقی");
            }else {
                cost = ("مجانی");

            }
            holder.txt_vadeae.setText("ودیعه : " + cost);
            if (!estatesList.get(position).getMonthly_price_ejare().equals("0")&&!estatesList.get(position).getMonthly_price_ejare().equals("-1")) {
                try {
                    Long num = Long.parseLong(estatesList.get(position).getMonthly_price_ejare());
                    DecimalFormat numFormat;
                    String number;
                    numFormat = new DecimalFormat("#,###,###");
                    number = numFormat.format(num);
                    cost1 = (String.valueOf(number) + " تومان");

                } catch (NumberFormatException e) {
                    cost1 = (estatesList.get(position).getMonthly_price_ejare() + " تومان");
                }
            } else if (estatesList.get(position).getMonthly_price_ejare().equals("0")){
                cost1 = ("توافقی");
            }else {
                cost1 = ("مجانی");

            }
            holder.txt_ejare.setText("اجاره ماهیانه : " + cost1);

        } else {
            holder.txt_cost.setVisibility(View.INVISIBLE);
            holder.txt_ejare.setVisibility(View.INVISIBLE);
            holder.txt_vadeae.setVisibility(View.INVISIBLE);
        }

        if (estatesList.get(position).getStatus().equals("pending")) {
//            holder.pendingIdentifier.setVisibility(View.VISIBLE);
            holder.txt_statous.setBackgroundColor(Color.parseColor("#F57C00"));
            holder.txt_statous.setText("در انتظار تایید");

        } else if (estatesList.get(position).getStatus().equals("rejected")) {
            holder.txt_statous.setBackgroundColor(Color.parseColor("#d32f2f"));
            holder.txt_statous.setText("رد شده");
        } else if (estatesList.get(position).getStatus().equals("approved")) {
            holder.txt_statous.setBackgroundColor(Color.parseColor("#388E3C"));
            holder.txt_statous.setText("تایید شد");
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
//                Add_Product.where = 1;
//                Add_Product.product = products.get(position);
//                context.startActivity(new Intent(context, Add_Product.class));
//                Edite_Car_Add.vehicles=vehiclesList.get(position);
//                context.startActivity(new Intent(context,Edite_Car_Add.class));
                Intent intent =new Intent(context,Edit_Home_Add.class);
                intent.putExtra("estates",estatesList.get(position));
//                Edit_Home_Add.estates=estatesList.get(position);
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
                                delete_this_add(estatesList.get(position).getId());
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

    @Override
    public int getItemCount() {
        return estatesList.size();
    }

    @Override
    public void on_volley_response(String response, int id) {
        if (id==135){
            try {
                progressDialog.dismiss();
                JSONObject jsonObject=new JSONObject(response);
                if (jsonObject.getString("status").equals("200")) {
                    My_Home.rv.setVisibility(View.GONE);
                    My_Home.progressBar.setVisibility(View.VISIBLE);
                    My_Home.offset = 0;
                    My_Home.estatesList = null;
                    My_Home.getData(context,get_insert_edit_data3);
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
        progressDialog.dismiss();
        ShowToast.failure("در ارتباط با سرور دچار مشکل شدیم لطفا مجددا تلاش نفرمایید",(Activity) context);
    }

    private class CellFeedViewHolder extends RecyclerView.ViewHolder {

        ImageView rvAdListImage, pendingIdentifier;
        MyTextView rvAdListTitle, rvAdListAddress;
        TextView txt_cost, txt_vadeae, txt_ejare,txt_statous;
        LinearLayout lin_edit,lin_main,lin_go_back,lin_edit_product,lin_delete_product;
        CardView card_main;
        public CellFeedViewHolder(View view) {
            super(view);

            rvAdListImage = (ImageView) view.findViewById(R.id.rvAdListImage);
            rvAdListTitle = (MyTextView) view.findViewById(R.id.rvAdListTitle);
            rvAdListAddress = (MyTextView) view.findViewById(R.id.rvAdListAddress);
            txt_ejare = (MyTextView) view.findViewById(R.id.txt_ejare);
            txt_vadeae = (MyTextView) view.findViewById(R.id.txt_vadeae);
            txt_statous = (MyTextView) view.findViewById(R.id.txt_statous);
            pendingIdentifier = (ImageView) view.findViewById(R.id.rvAdListPendingIdentifier);
            txt_cost = (TextView) view.findViewById(R.id.txt_cost);
            lin_edit= (LinearLayout) view.findViewById(R.id.lin_edit);
            lin_main= (LinearLayout) view.findViewById(R.id.lin_main);
            lin_go_back= (LinearLayout) view.findViewById(R.id.lin_go_back);
            lin_edit_product= (LinearLayout) view.findViewById(R.id.lin_edit_product);
            lin_delete_product= (LinearLayout) view.findViewById(R.id.lin_delete_product);
            card_main= (CardView) view.findViewById(R.id.card_main);
        }
    }

    private void delete_this_add(String id){
        progressDialog=new ProgressDialog(context);
        progressDialog.setMessage("در حال اعمال تغییرات ...");
        progressDialog.show();
        Get_Volley_Call_Back.binddata(My_Home_List_Adapter.this);
        Map<String, String> params = new HashMap<String, String>();
        String url= StaticData.estates+"/ads/"+id+ "?token=" + new UserSessionManager(context).getLoginToken();
        Get_Volley_Call_Back.Call_Volley(context,params,url, Request.Method.DELETE,135);

    }

}
